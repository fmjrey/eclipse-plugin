/**
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.eclipse.launchConfigurations;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.gradle.eclipse.GradleExecScheduler;
import org.gradle.eclipse.GradleImages;
import org.gradle.eclipse.GradlePlugin;
import org.gradle.eclipse.IGradleConstants;
import org.gradle.eclipse.model.GradleTaskModel;
import org.gradle.eclipse.model.GradleTaskModelContentProvider;
import org.gradle.eclipse.model.GradleTaskModelLabelProvider;
import org.gradle.eclipse.util.GradleUtil;
import org.gradle.foundation.ProjectView;
import org.gradle.foundation.TaskView;


/**
 * @author Rene Groeschke
 *
 */
public class GradleTasksTab extends AbstractLaunchConfigurationTab implements IPropertyChangeListener {

	private CheckboxTreeViewer fTreeViewer = null;
	private ILaunchConfiguration launchConfiguration;
	private List<ProjectView> allProjects = null;
	private ProjectView rootProject = null;
	private final GradleTaskModel model = new GradleTaskModel();
	private final GradleTaskModelLabelProvider labelProvider = new GradleTaskModelLabelProvider();
	private final GradleTaskModelContentProvider contentProvider = new GradleTaskModelContentProvider(model);
	
	private List<Object> selectedTasks = new ArrayList<Object>();
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		
		Font font = parent.getFont();
		
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout topLayout = new GridLayout();
		comp.setLayout(topLayout);		
		GridData gd = new GridData(GridData.FILL_BOTH);
		comp.setLayoutData(gd);
		comp.setFont(font);
		
		createTasksTableTree(comp);
		
		Composite buttonComposite= new Composite(comp, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttonComposite.setLayout(layout);
		buttonComposite.setFont(font);
		
		createVerticalSpacer(comp, 1);
		Dialog.applyDialogFont(parent);
	}
	
	/**
	 * Return the number of rows available in the current display using the
	 * current font.
	 * @param parent The Composite whose Font will be queried.
	 * @return int The result of the display size divided by the font size.
	 */
	private int availableRows(Composite parent) {

		int fontHeight = (parent.getFont().getFontData())[0].getHeight();
		int displayHeight = parent.getDisplay().getClientArea().height;

		return displayHeight / fontHeight;
	}
	
	/**
	 * Creates the table tree which displays the available tasks
	 * @param parent the parent composite
	 */
	private void createTasksTableTree(Composite parent) {
		Font font= parent.getFont();
		Label label = new Label(parent, SWT.NONE);
		label.setFont(font);
		label.setText(GradleLaunchConfigurationMessages.GradleTasksTab_Check_task_to_e_xecute__1);
				
		fTreeViewer = new CheckboxTreeViewer(parent, SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION );
		fTreeViewer.setLabelProvider(labelProvider);
		fTreeViewer.setContentProvider(contentProvider);

		final Tree tree= fTreeViewer.getTree();
		
		GridData data= new GridData(GridData.FILL_BOTH);
		int availableRows= availableRows(parent);
		data.heightHint = tree.getItemHeight() * (availableRows / 20);
		data.widthHint = 500;
		data.minimumWidth = 500;
		tree.setLayoutData(data);
		tree.setFont(font);	
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);		

		TableLayout treeLayout= new TableLayout();
		ColumnWeightData weightData = new ColumnWeightData(250, true);
		treeLayout.addColumnData(weightData);
		weightData = new ColumnWeightData(250, true);
		treeLayout.addColumnData(weightData);		
		tree.setLayout(treeLayout);

		final TreeViewerColumn column1= new TreeViewerColumn(fTreeViewer, SWT.NULL);
		column1.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object node) {
				return labelProvider.getColumnText(node, 0);
			}
		});
		column1.getColumn().setText(GradleLaunchConfigurationMessages.GradleTasksTab_Name_5);
		column1.getColumn().setWidth(300);
		
		final TreeViewerColumn column2= new TreeViewerColumn(fTreeViewer, SWT.NULL);
		column2.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object node) {
				return labelProvider.getColumnText(node, 1);
			}
		});
		column2.getColumn().setText(GradleLaunchConfigurationMessages.GradleTasksTab_Description_6);
		column2.getColumn().setWidth(300);

		//TableLayout only sizes columns once. If showing the tasks
		//tab as the initial tab, the dialog isn't open when the layout
		//occurs and the column size isn't computed correctly. Need to
		//recompute the size of the columns once all the parent controls 
		//have been created/sized.
		//HACK Bug 139190 
		getShell().addShellListener(new ShellAdapter() {
			public void shellActivated(ShellEvent e) {
				if(!tree.isDisposed()) {
					int tableWidth = tree.getSize().x;
					if (tableWidth > 0) {
						int c1 = tableWidth / 3;
						column1.getColumn().setWidth(c1);
						column2.getColumn().setWidth(tableWidth - c1);
					}
					getShell().removeShellListener(this);
				}
			}
		});
		

		fTreeViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				updateOrderedTargets(event.getElement(), event.getChecked());
			}
		});
	}

	
	/**
	 * Updates the ordered targets list in response to an element being checked
	 * or unchecked. When the element is checked, it's added to the list. When
	 * unchecked, it's removed.
	 * 
	 * @param element the element in question
	 * @param checked whether the element has been checked or unchecked
	 */
	private void updateOrderedTargets(Object element , boolean checked) {
		if (checked) {
			 selectedTasks.add(element);
		} else {
			selectedTasks.remove(element);
		}	 
		updateLaunchConfigurationDialog();	
	}
	
	/**
	 * Load all tasks in the buildfile.
	 * @return all tasks in the buildfile
	 */
	private void loadGradleTaskModel() {
		if (!model.hasChildren() || isDirty()) {

			setDirty(false);
			setErrorMessage(null);
			setMessage(null);
			
			final CoreException[] exceptions= new CoreException[1];
			try {
				
				IRunnableContext context= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (context == null) {
				    context= getLaunchConfigurationDialog();
				}

				if (!ResourcesPlugin.getWorkspace().isTreeLocked()) {
					//only set a scheduling rule if not in a resource change callback
					String variableString = launchConfiguration.getAttribute(IGradleConstants.ATTR_LOCATION, "");
					if(!variableString.isEmpty()){
						ISchedulingRule rule= null;
						final String absFileLocation = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(variableString);
						rule = GradleUtil.getFileForLocation(absFileLocation);
						
						IRunnableWithProgress operation= new IRunnableWithProgress() {
							public void run(IProgressMonitor monitor) {
								allProjects = GradleExecScheduler.getInstance().getProjectViews(absFileLocation);
							}
						};
						PlatformUI.getWorkbench().getProgressService().runInUI(context, operation, rule);
					}
				}
			} catch (CoreException e) {
			    GradlePlugin.log("Internal error occurred retrieving targets", e); //$NON-NLS-1$
			    setErrorMessage(GradleLaunchConfigurationMessages.GradleTasksTab_1);
			    return;
			} catch (InvocationTargetException e) {
			    GradlePlugin.log("Internal error occurred retrieving targets", e.getTargetException()); //$NON-NLS-1$
			    setErrorMessage(GradleLaunchConfigurationMessages.GradleTasksTab_1);
			    return;
			} catch (InterruptedException e) {
			    GradlePlugin.log("Internal error occurred retrieving targets", e); //$NON-NLS-1$
			    setErrorMessage(GradleLaunchConfigurationMessages.GradleTasksTab_1);
			    return;
			}
			
			if (exceptions[0] != null) {
				IStatus exceptionStatus= exceptions[0].getStatus();
				IStatus[] children= exceptionStatus.getChildren();
				StringBuffer message= new StringBuffer(exceptions[0].getMessage());
				for (int i = 0; i < children.length; i++) {
					message.append(' ');
					IStatus childStatus = children[i];
					message.append(childStatus.getMessage());
				}
				setErrorMessage(message.toString());
				return;
			}
			
			if (allProjects == null) {
				//if an error was not thrown during parsing then having no task is valid
				return;
			}
			if (allProjects.isEmpty() || allProjects.size()>1) {
				// Not sure why we're not getting a single root project
				setErrorMessage("Unexpected number of projects returned:" + allProjects.size());
				return;
			}
			rootProject = (ProjectView)allProjects.get(0);
			model.setRootProject(rootProject);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return GradleLaunchConfigurationMessages.GradleTasksTab_1;
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		return GradleImages.getImage(IGradleConstants.IMG_TAB_GRADLE_TASKS);
	}

	@SuppressWarnings("unchecked")
	public void initializeFrom(ILaunchConfiguration configuration) {
		launchConfiguration = configuration;
		loadGradleTaskModel();
		fTreeViewer.setInput(model);
		List<String> tasks = null;
		try {
			tasks = configuration.getAttribute(IGradleConstants.GRADLE_TASKS_ATTRIBUTES, new ArrayList<String>());
		} catch (CoreException e) {
			setErrorMessage("Could not retrieve list of previously selected tasks:" + e.getMessage());
		}
		
		//Select previously selected tasks or default tasks
		if (tasks!=null && !tasks.isEmpty()) {
			for (String text: tasks) {
				Object element = model.getElement(text);
				if (element!=null)
					fTreeViewer.setChecked(element, true);
			}
		} else if(model.hasDefaultTasks()) {
			for(TaskView defTask : model.getDefaultTasks()){
				fTreeViewer.setChecked(defTask, true);
			}			
		}
		fTreeViewer.refresh();
	}
	
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		//build tasks string
		List<String> taskList = new ArrayList<String>();
		for(Object element : selectedTasks){
			taskList.add(labelProvider.getText(element));
		}
		
		configuration.setAttribute(IGradleConstants.GRADLE_TASKS_ATTRIBUTES, taskList);

	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		//change rows with defaulttasks checked
		for(TaskView defTask : model.getDefaultTasks()){
			fTreeViewer.setChecked(defTask, true);
		}
	}

	
	public void propertyChange(PropertyChangeEvent event) {
		
	}
}
