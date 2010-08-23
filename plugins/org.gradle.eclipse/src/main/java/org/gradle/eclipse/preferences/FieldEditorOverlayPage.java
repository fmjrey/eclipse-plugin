package org.gradle.eclipse.preferences;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * The class FieldEditorOverlayPage can act as a common superclass for all field
 * editor preference pages that also want to behave as property pages.
 * 
 * @author Rene
 * */
public abstract class FieldEditorOverlayPage extends FieldEditorPreferencePage
		implements IWorkbenchPropertyPage {

	private ImageDescriptor image;
	private IAdaptable element;
	private IPreferenceStore overlayStore;
	private String pageId;

	private List<FieldEditor> editors = new ArrayList<FieldEditor>();

	public static final String USEPROJECTSETTINGS = "useProjectSettings";
	private Button useWorkspaceSettingsButton, useProjectSettingsButton,
			configureButton;
	
	public FieldEditorOverlayPage(int style) {
		super(style);
	}

	public FieldEditorOverlayPage(String title, int style) {
		super(title, style);
	}

	public FieldEditorOverlayPage(String title, ImageDescriptor image, int style) {
		super(title, image, style);
		this.image = image;
	}

	public void setElement(IAdaptable element) {
		this.element = element;
	}

	public IAdaptable getElement() {
		return element;
	}

	/**
	 * this method is used to differentiate the usage as PropertyPage or as
	 * PreferencePage
	 * 
	 * @return isPropertyPage If this is used as property page.
	 */
	public boolean isPropertyPage() {
		return element != null;
	}

	protected Control createContents(Composite parent) {
		if (isPropertyPage())
			createSelectionGroup(parent);
		return super.createContents(parent);
	}

	private void createSelectionGroup(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
//		comp.
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Composite radioGroup = new Composite(comp, SWT.NONE);
		radioGroup.setLayout(new GridLayout());
		radioGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		useWorkspaceSettingsButton = createRadioButton(radioGroup,
				"Use workspace settings");
		useProjectSettingsButton = createRadioButton(radioGroup,
				"Use project settings");
		configureButton = new Button(comp, SWT.PUSH);
		configureButton.setText("Configure Workspace Settings ...");
		configureButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				configureWorkspaceSettings();
			}
		});
		try {
			String use = ((IResource) element)
					.getPersistentProperty(new QualifiedName(pageId,
							USEPROJECTSETTINGS));
			if ("true".equals(use)) {
				useProjectSettingsButton.setSelection(true);
				configureButton.setEnabled(false);
			} else
				useWorkspaceSettingsButton.setSelection(true);
		} catch (CoreException e) {
			useWorkspaceSettingsButton.setSelection(true);
		}
	}

	protected void configureWorkspaceSettings() {
		try {
			IPreferencePage page = (IPreferencePage) this.getClass()
					.newInstance();
			page.setTitle(getTitle());
			page.setImageDescriptor(image);
			showPreferencePage(pageId, page);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	protected void showPreferencePage(String id, IPreferencePage page) {
		final IPreferenceNode targetNode = new PreferenceNode(id, page);
		PreferenceManager manager = new PreferenceManager();
		manager.addToRoot(targetNode);
		final PreferenceDialog dialog = new PreferenceDialog(getControl()
				.getShell(), manager);
		BusyIndicator.showWhile(getControl().getDisplay(), new Runnable() {
			public void run() {
				dialog.create();
				dialog.setMessage(targetNode.getLabelText());
				dialog.open();
			}
		});
	}

	private Button createRadioButton(Composite parent, String label) {
		final Button button = new Button(parent, SWT.RADIO);
		button.setText(label);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				configureButton
						.setEnabled(button == useWorkspaceSettingsButton);
				updateFieldEditors();
			}
		});
		return button;
	}

	protected void addField(FieldEditor editor) {
		editors.add(editor);
		super.addField(editor);
	}

	private void updateFieldEditors() {
		boolean enabled = useProjectSettingsButton.getSelection();
		updateFieldEditors(enabled);
	}

	protected void updateFieldEditors(boolean enabled) {
		Composite parent = getFieldEditorParent();
		Iterator<FieldEditor> it = editors.iterator();
		while (it.hasNext()) {
			FieldEditor editor = it.next();
			editor.setEnabled(enabled, parent);
		}
	}

	public void createControl(Composite parent) {
		if (isPropertyPage()) {
			pageId = getPageId();
			overlayStore = createPreferenceStore();
		}
		super.createControl(parent);
		if (isPropertyPage())
			updateFieldEditors();
	}

	private IPreferenceStore createPreferenceStore() {
		IProject proj = (IProject) getElement().getAdapter(IProject.class);
		return preferenceStore(proj);
	}

	protected IPersistentPreferenceStore preferenceStore(IProject proj) {
		return new ScopedPreferenceStore(new ProjectScope(proj),
				"org.gradle.eclipse.preferences");
	}

	public IPreferenceStore getPreferenceStore() {
		if (isPropertyPage())
			return overlayStore;
		return super.getPreferenceStore();
	}

	public boolean performOk() {
		boolean result = super.performOk();
		if (result && isPropertyPage()) {
			IResource resource = (IResource) element;
			try {
				String value = (useProjectSettingsButton.getSelection()) ? "true"
						: "false";
				resource.setPersistentProperty(new QualifiedName(pageId,
						USEPROJECTSETTINGS), value);
			} catch (CoreException e) {
			}
		}
		return result;
	}

	protected void performDefaults() {
		if (isPropertyPage()) {
			useWorkspaceSettingsButton.setSelection(true);
			useProjectSettingsButton.setSelection(false);
			configureButton.setEnabled(true);
			updateFieldEditors();
		}
		super.performDefaults();
	}

	protected abstract String getPageId();
}