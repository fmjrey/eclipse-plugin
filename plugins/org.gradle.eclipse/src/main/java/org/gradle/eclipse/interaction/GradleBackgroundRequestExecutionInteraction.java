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
package org.gradle.eclipse.interaction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.gradle.eclipse.GradlePlugin;

/**
 * @author Rene Groeschke
 *
 */
public class GradleBackgroundRequestExecutionInteraction extends GradleProcessExecListener {

	public GradleBackgroundRequestExecutionInteraction(IProgressMonitor monitor) {
		super(monitor);
		successful = true;
	}

	public void reportExecutionStarted() {
		beginTask("Initialize Gradle", 10);
	}

	public void reportLiveOutput(String arg0) {
		System.out.println("reportLiveOutput -- " + arg0);
	}

	public void reportTaskComplete(String arg0, float arg1) {
		System.out.println("reportTaskComplete -- " + arg0);
	}

	public void reportTaskStarted(String arg0, float arg1) {
		System.out.println("reportTaskStarted -- " + arg0);
	}

	public void reportNumberOfTasksToExecute(int taskCount) {
		GradlePlugin.log("number of tasks to execute:  " + taskCount, null);
	}
}
