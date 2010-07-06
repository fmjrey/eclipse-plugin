package org.gradle.eclipse.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.gradle.eclipse.GradlePlugin;
import org.gradle.eclipse.interaction.GradleProcessExecListener;
import org.gradle.eclipse.interaction.GradleProcessResult;
import org.gradle.eclipse.interaction.GradleRefreshRequestExecutionInteraction;
import org.gradle.gradleplugin.foundation.GradlePluginLord;
import org.gradle.gradleplugin.foundation.request.ExecutionRequest;
import org.gradle.gradleplugin.foundation.request.RefreshTaskListRequest;
import org.gradle.gradleplugin.foundation.request.Request;

public class UpdateClasspathJob extends BackgroundBuildJob {

	private static final String ECLIPSE_CP_TASK = "eclipseCp";
	private static final String ECLIPSE_INIT_SCRIPT = "eclipse.gradle";
	
	private final GradlePluginLord pluginLord;

	public UpdateClasspathJob(GradlePluginLord gradlePluginLord, String absoluteBuildFilePath) {
		super("Update Classpath", absoluteBuildFilePath);
		//setInitScriptPath(GradlePlugin.getDefault().getInitScript(ECLIPSE_INIT_SCRIPT));
		getTasks().add(ECLIPSE_CP_TASK);
		this.pluginLord = gradlePluginLord;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		return refreshClasspath(monitor);
	}

	private IStatus refreshClasspath(IProgressMonitor monitor) {
		final GradleProcessExecListener executionlistener = new GradleRefreshRequestExecutionInteraction(monitor);
		
		pluginLord.startExecutionQueue();
		final GradleProcessResult processResult = new GradleProcessResult();

		StringBuffer commandLineArgs = new StringBuffer();
		configureBuildFilePath(commandLineArgs);
		configureInitScript(commandLineArgs);
		configureTasks(commandLineArgs);
		
		GradlePluginLord.RequestObserver observer = new GradlePluginLord.RequestObserver() {
	           
			public void executionRequestAdded( ExecutionRequest request )
	           {
	              request.setExecutionInteraction( executionlistener );
	           }
	           public void refreshRequestAdded( RefreshTaskListRequest request ) { 
	           }
	           public void aboutToExecuteRequest( Request request ) { 
	           }

	           public void requestExecutionComplete( Request request, int result, String output ) {
	        	   processResult.setComplete(true);
	        	   processResult.setResult(result);
	        	   processResult.setOutput(output);
	           }
	        };

	    pluginLord.addRequestObserver(observer, false);
	    pluginLord.addExecutionRequestToQueue(commandLineArgs.toString(), getBuildFileName() + UpdateClasspathJob.ECLIPSE_CP_TASK);
		//keep job open til listener reports gradle has finished
		while(!processResult.isComplete()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				return new Status(IStatus.WARNING, GradlePlugin.PLUGIN_ID, "Error while recalculating Gradle Tasks", e);
			}
		}
				
		if( processResult.getResult() == -1) {
			return new Status(IStatus.ERROR, GradlePlugin.PLUGIN_ID,
					"Error while starting Gradle Process. Please check that GRADLE_HOME is defined correctly in your preferences!", executionlistener
							.getThrowable());
		}else if( processResult.getResult() == 1) {
			return new Status(IStatus.ERROR, GradlePlugin.PLUGIN_ID, processResult.getOutput(), executionlistener.getThrowable());
		}
		return Status.OK_STATUS;	
	}

}
