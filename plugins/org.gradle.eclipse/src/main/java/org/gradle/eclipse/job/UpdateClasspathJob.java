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
package org.gradle.eclipse.job;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.gradle.eclipse.GradlePlugin;
import org.gradle.eclipse.interaction.GradleProcessExecListener;
import org.gradle.eclipse.interaction.UpdateEclipseCpInteraction;
import org.gradle.gradleplugin.foundation.GradlePluginLord;

public class UpdateClasspathJob extends AbstractGradleJob {

	private static final String ECLIPSE_CP_TASK = "eclipseClasspath";
	private static final String ECLIPSE_INIT_SCRIPT = "eclipse.gradle";
		
	public UpdateClasspathJob(IProject project, GradlePluginLord gradlePluginLord, String absoluteBuildFilePath) {
		super(project, gradlePluginLord, "Update Classpath", absoluteBuildFilePath);
		setInitScriptPath(GradlePlugin.getDefault().getInitScript(ECLIPSE_INIT_SCRIPT));
		getTasks().add(ECLIPSE_CP_TASK);
	}

	protected IStatus afterGradleExecutionHook(IProgressMonitor monitor){
		try {			
			project.refreshLocal(IProject.DEPTH_INFINITE, monitor);

		} catch (CoreException coreException) {
			return new Status(IStatus.ERROR, GradlePlugin.PLUGIN_ID, "Exception while refreshing project " + project.getName(), coreException);
		}
		return Status.OK_STATUS;
	}
	
	@Override
	protected GradleProcessExecListener createExecutionListener(
			IProgressMonitor monitor) {
		return new UpdateEclipseCpInteraction(monitor);
	}
}
