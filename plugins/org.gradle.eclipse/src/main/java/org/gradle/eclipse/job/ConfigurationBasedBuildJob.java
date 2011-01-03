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
import org.eclipse.core.runtime.IProgressMonitor;
import org.gradle.eclipse.interaction.GradleBuildExecutionInteraction;
import org.gradle.eclipse.interaction.GradleProcessExecListener;
import org.gradle.eclipse.launchConfigurations.GradleProcess;
import org.gradle.gradleplugin.foundation.GradlePluginLord;


public class ConfigurationBasedBuildJob extends AbstractGradleJob {

	private final GradleProcess process;
	
	public ConfigurationBasedBuildJob(IProject project, GradlePluginLord gradlePluginLord,
			String absoluteBuildFilePath, GradleProcess process) {

		super(project, gradlePluginLord, "Running Gradle Build...", absoluteBuildFilePath);
		this.process = process;
	}

	@Override
	protected GradleProcessExecListener createExecutionListener(
			IProgressMonitor monitor) {
		return new GradleBuildExecutionInteraction(monitor, process);
	}
}
