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

//	@Override
//	protected IStatus runGradleJob(IProgressMonitor monitor) {
//
//		final GradleProcessExecListener executionlistener = 
//		
//		pluginLord.startExecutionQueue();
//
//		final GradleProcessResult processResult = new GradleProcessResult();
//		// /
//		GradlePluginLord.RequestObserver observer = new GradlePluginLord.RequestObserver() {
//			public void executionRequestAdded(ExecutionRequest request) {
//				request.setExecutionInteraction(executionlistener);
//			}
//
//			public void refreshRequestAdded(RefreshTaskListRequest request) {
//
//			}
//
//			public void aboutToExecuteRequest(Request request) {
//
//			}
//
//			/**
//			 * -1 indicates failing process creation
//			 * 1 indicates process started correctly but build failed
//			 * */
//			public void requestExecutionComplete(Request request, int result,
//					String output) {
//				processResult.setComplete(true);
//				processResult.setResult(result);
//				processResult.setOutput(output);
//			}
//		};
//
//		// add the observer before we add the request due to timing issues.
//		// It's possible for it to completely execute before we return from
//		// addExecutionRequestToQueue.
//
//		pluginLord.addRequestObserver(observer, false);
//		pluginLord.addExecutionRequestToQueue(commandLine, configuration.getName());
//
//		// gradlePluginLord.addExecutionRequestToQueue(commandLine,
//		// executionlistener);
//		// keep job open til listener reports gradle has finished
//		while (!processResult.isComplete()) {
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				return new Status(IStatus.WARNING, GradlePlugin.PLUGIN_ID,
//						"Error while running Gradle Tasks", e);
//			}
//		}
//
//		if (executionlistener.getThrowable() != null) {
//			return new Status(IStatus.WARNING, GradlePlugin.PLUGIN_ID,
//					"Error while running Gradle Tasks", executionlistener
//							.getThrowable());
//		}
//		
//		if(!executionlistener.isSuccessful() && processResult.getResult()==-1) {
//			return new Status(IStatus.ERROR, GradlePlugin.PLUGIN_ID,
//					"Error while starting Gradle Process. Please check that GRADLE_HOME is defined correctly in your preferences!", executionlistener
//							.getThrowable());
//		}
//		return Status.OK_STATUS;
//	}

	@Override
	protected GradleProcessExecListener createExecutionListener(
			IProgressMonitor monitor) {
		return new GradleBuildExecutionInteraction(monitor, process);
	}
}
