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

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.gradle.eclipse.BuildInformationCache;
import org.gradle.eclipse.interaction.GradleBackgroundRequestExecutionInteraction;
import org.gradle.eclipse.interaction.GradleProcessExecListener;
import org.gradle.foundation.ProjectView;
import org.gradle.gradleplugin.foundation.GradlePluginLord;


/**
 * @author Rene Groeschke
 * This class extends AbstractGradleJob to get a list of available
 * tasks in a build file.
 */
public class RefreshTaskJob extends AbstractGradleJob{

	private BuildInformationCache cache;

	public RefreshTaskJob(IProject project, String absoluteBuildFilePath, GradlePluginLord gradlePluginLord, BuildInformationCache cache) {
		super(project, gradlePluginLord, "Calculating Gradle Tasks of " + project.getName(), absoluteBuildFilePath, true);
		this.cache = cache;
	}
	
	protected IStatus afterGradleExecutionHook(){
		List<ProjectView> projects = pluginLord.getProjects();
		cache.put(buildFilePath, projects);
		return Status.OK_STATUS;
	}

	@Override
	protected GradleProcessExecListener createExecutionListener(IProgressMonitor monitor) {
		 return new GradleBackgroundRequestExecutionInteraction(monitor);
	}
}
