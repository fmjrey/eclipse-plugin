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

public class UpdateEclipseCpInteraction extends GradleBackgroundRequestExecutionInteraction{

	public UpdateEclipseCpInteraction(IProgressMonitor monitor) {
		super(monitor);
	}
	
	@Override
	public void reportLiveOutput(String output) {
		if(output.startsWith("\nDownload")){ //use own build logger to grap output //just a prototype here!
			monitor.subTask(output);
		}
	}

	public void reportTaskStarted(String task, float arg1) {
		monitor.setTaskName("Starting Gradle Task: " + task);
	}
}
