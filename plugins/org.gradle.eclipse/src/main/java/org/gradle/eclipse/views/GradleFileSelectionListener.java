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
package org.gradle.eclipse.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author rene@breskeby.com
 * */
public class GradleFileSelectionListener implements ISelectionListener {

	private TaskView taskView = null;
	
	public GradleFileSelectionListener(TaskView taskView){
		this.taskView = taskView;
	}
	public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
		if(selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection  = (IStructuredSelection)selection ;
			if(structuredSelection.size() == 1 && 
			   structuredSelection.getFirstElement() instanceof IFile &&
			   ((IFile)structuredSelection.getFirstElement()).getFileExtension().equals("gradle")){
				taskView.setModel((IFile)structuredSelection.getFirstElement());
			}
		}
	}
	
}
