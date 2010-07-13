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
