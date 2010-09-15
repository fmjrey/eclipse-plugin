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

package org.gradle.eclipse.actions;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.gradle.eclipse.GradleExecScheduler;
import org.gradle.eclipse.GradlePlugin;
import org.gradle.eclipse.preferences.IGradlePreferenceConstants;

/**
 * UpdateClasspathAction is responsible for updating the projects classpath using gradle eclipseCp task.
 */
public class UpdateClasspathAction implements IObjectActionDelegate {
    private List<IProject> currSelected = new LinkedList<IProject>();

    public void run(final IAction action) {
        
        if (currSelected != null && currSelected.size() > 0) {
           for (IProject project : currSelected) {
        	   IFile file = project.getFile("build.gradle");
        	   
  				try {
  					//check whether to use project specific settings or not
  					String use = project.getPersistentProperty(new QualifiedName("org.gradle.eclipse.preferences.GradleRuntimePreferencePage", IGradlePreferenceConstants.USE_PROJECT_SETTINGS));
  					IPreferenceStore store = null;
  					if ("true".equals(use)){
  						store = new ScopedPreferenceStore(new ProjectScope(project), "org.gradle.eclipse.preferences");  	  					
  					}else{
  						store = GradlePlugin.getDefault().getPreferenceStore();
  					}
					
  					GradleExecScheduler.getInstance().updateProjectClasspath(store, file.getLocationURI().getPath(), project);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

           }
        }
    }

    /**
     * @see IObjectActionDelegate#selectionChanged
     */
    public void selectionChanged(final IAction action, final ISelection selection) {
        currSelected.clear();
        List<IProject> newSelected = new LinkedList<IProject>();
        boolean enabled = true;
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection newSelection = (IStructuredSelection) selection;
            for (Iterator<?> iter = newSelection.iterator(); iter.hasNext();) {
                Object object = iter.next();
                if (object instanceof IAdaptable) {
                    IProject project = (IProject) ((IAdaptable)object).getAdapter(IProject.class);  
                    if(project != null) {
                        newSelected.add(project);
                    } else {
                        enabled = false;
                        break;
                    }
                } else {
                    enabled = false;
                    break;
                }
            }
            if (action != null) {
                action.setEnabled(enabled);
            }
        }

        if (enabled) {
            this.currSelected = newSelected;
        }
    }

    /**
     * @see IObjectActionDelegate#setActivePart
     */
    public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
    }
}
