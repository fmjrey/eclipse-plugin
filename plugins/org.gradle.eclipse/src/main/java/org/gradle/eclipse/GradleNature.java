/**
 * Copyright 2010 the original author or authors.
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

package org.gradle.eclipse;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class GradleNature implements IProjectNature{
	public static final String GRADLE_NATURE = "org.gradle.eclipse.gradleNature"; //$NON-NLS-1$

	public void configure() throws CoreException {
		// TODO Auto-generated method stub
		
	}

	public void deconfigure() throws CoreException {
		// TODO Auto-generated method stub
		
	}

	public IProject getProject() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setProject(IProject arg0) {
		// TODO Auto-generated method stub
		
	}
}
