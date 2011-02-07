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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.gradle.eclipse.GradleImages;
import org.gradle.eclipse.IGradleConstants;
import org.gradle.foundation.TaskView;

/**
 * @author François Rey
 * @author Rene Groeschke
 *
 */
public class GradleTaskModelLabelProvider extends ColumnLabelProvider implements ITableLabelProvider, IColorProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			return getImage(element);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object node, int columnIndex) {
		if (columnIndex == 0){
			return getText(node);
		}
		StringBuilder text=new StringBuilder();
		if (node instanceof GradleTaskGroup) {
			text.append("*:");
			text.append(((GradleTaskGroup)node).getName());
		}
		if (node instanceof TaskView) {
			String desc= ((TaskView)node).getDescription();
			if (desc != null) text.append(desc);
		}
		return text.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object node) {
		if (!(node instanceof TaskView)) return null;
		return ((TaskView)node).isDefault() ?
				GradleImages.getImage(IGradleConstants.IMG_GRADLE_DEFAULT_TASK) :
				GradleImages.getImage(IGradleConstants.IMG_GRADLE_TASK);
	}
    
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(Object)
	 */
	public String getText(Object node) {
		if (node instanceof GradleTaskGroup)
			return ((GradleTaskGroup)node).getName();
		if (node instanceof TaskView)
			return ((TaskView)node).getFullTaskName();
		return node.toString();
	}

	public Color getForeground(Object node) {
		if(node instanceof TaskView && ((TaskView)node).isDefault()){
			//set default tasks blue
			return Display.getDefault().getSystemColor(SWT.COLOR_BLUE);			
		}//set normal tasks black
			return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	}

	public Color getBackground(Object element) {
		return null;
	}
}
