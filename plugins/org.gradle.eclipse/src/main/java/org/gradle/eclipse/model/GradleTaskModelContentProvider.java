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
package org.gradle.eclipse.model;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.gradle.foundation.TaskView;

/**
 * @author François Rey
 * @author Rene Groeschke
 *
 */
public class GradleTaskModelContentProvider implements ITreeContentProvider {

	private GradleTaskModel model;

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public GradleTaskModelContentProvider(GradleTaskModel model) {
		super();
		this.model = model;
	}

	public void dispose() {
	}
    
	/**
	 * do nothing
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof AbstractGradleTaskModelElement) {
			AbstractGradleTaskModelElement element = (AbstractGradleTaskModelElement) inputElement;
			return element.getElements();
		}
		return AbstractGradleTaskModelElement.EMPTY_ARRAY;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(Object)
	 */
	public Object[] getChildren(Object parentNode) {
		return getElements(parentNode);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(Object)
	 */
	public Object getParent(Object aNode) {
		if (aNode instanceof GradleTaskGroup) {
			return model;
		}
		if(aNode instanceof TaskView){
			if (model.containsTask(aNode)) return model;
			for (GradleTaskGroup group: model.getGroups())
				if (group.containsTask(aNode))
					return group;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(Object)
	 */
	public boolean hasChildren(Object aNode) {
		if (aNode instanceof AbstractGradleTaskModelElement) {
			AbstractGradleTaskModelElement element = (AbstractGradleTaskModelElement) aNode;
			return element.hasChildren();
		}
		return false;
	}
}