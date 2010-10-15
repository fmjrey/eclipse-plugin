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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.gradle.eclipse.GradlePlugin;
import org.gradle.eclipse.interaction.GradleProcessExecListener;
import org.gradle.eclipse.interaction.GradleProcessResult;
import org.gradle.eclipse.preferences.IGradlePreferenceConstants;
import org.gradle.eclipse.util.GradleUtil;
import org.gradle.gradleplugin.foundation.GradlePluginLord;
import org.gradle.gradleplugin.foundation.request.ExecutionRequest;
import org.gradle.gradleplugin.foundation.request.RefreshTaskListRequest;
import org.gradle.gradleplugin.foundation.request.Request;

/**
 * @author Rene Groeschke
 * This class represents a Job which schedules different gradle builds.
 */
abstract class AbstractGradleJob extends Job{

	protected String initScriptPath = null;
	protected String buildFilePath = null;
	protected final IProject project;
	protected final GradlePluginLord pluginLord;
	
	private List<String> tasks = new ArrayList<String>();
	private final boolean useRefreshQueue;
	
	protected String uiProvidedCommandLineParams = "";
	
	public String getUiProvidedCommandLineParams() {
		return uiProvidedCommandLineParams;
	}

	public void setUiProvidedCommandLineParams(String uiProvidedCommandLineParams) {
		this.uiProvidedCommandLineParams = uiProvidedCommandLineParams;
	}

	public AbstractGradleJob(IProject project, 
							 GradlePluginLord pluginLord, 
							 String name,
							 String absoluteBuildFilePath){
		this(project, pluginLord, name, absoluteBuildFilePath, false);
	}
	
	public AbstractGradleJob(IProject project, 
			 GradlePluginLord pluginLord, 
			 String name, 
			 String absoluteBuildFilePath,
			 boolean useRefreshQueue){
		super(name);
		this.pluginLord = pluginLord;
		this.project = project;
		this.buildFilePath = absoluteBuildFilePath;

		this.useRefreshQueue = useRefreshQueue;
	}
	
	public void setInitScriptPath(String initScriptPath) {
		this.initScriptPath = initScriptPath;
	}

	public String getInitScriptPath() {
		return initScriptPath;
	}	

	protected String getBuildFileName() {
		return new File(buildFilePath).getName();
	}

	public void setTasks(List<String> tasks) {
		this.tasks = tasks;
	}

	public List<String> getTasks() {
		return tasks;
	}
	
	protected void configureBuildFilePath(StringBuffer commandLineArgs) {
		commandLineArgs.append(" -b ");
		commandLineArgs.append(buildFilePath);
	}

	protected void configureInitScript(StringBuffer commandLineArgs) {
		String initScriptPath = getInitScriptPath();
		if(initScriptPath!=null && !initScriptPath.isEmpty() && new File(initScriptPath).exists()){
			commandLineArgs.append(" -I ");
			commandLineArgs.append(initScriptPath);
		}
	}


	protected void configureTasks(StringBuffer commandLineArgs) {
		for(String task : getTasks()){
			commandLineArgs.append(" ");
			commandLineArgs.append(task);
		}
	}
	
	protected void configureGradleCache(StringBuffer commandLineArgs) {
		IPreferenceStore store = GradleUtil.getStoreForProject(project);
		boolean useCustomCache = store.getBoolean(IGradlePreferenceConstants.USE_SPECIFIC_GRADLE_CACHE);
		if(useCustomCache){
			String gradleCache = store.getString(IGradlePreferenceConstants.GRADLE_CACHE);
			commandLineArgs.append(" -g ");
			commandLineArgs.append(gradleCache);
		}
	}
	
	protected void configureAdditionalCmdParams(StringBuffer commandLineArgs) {
		IPreferenceStore store = GradleUtil.getStoreForProject(project);
		String customCmdParams = store.getString(IGradlePreferenceConstants.ADDITIONAL_COMMANDLINE_PARAMS);
		if(customCmdParams!= null && customCmdParams.isEmpty()){
			commandLineArgs.append(" ")
						   .append(customCmdParams);
		}
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		final GradleProcessExecListener executionListener = createExecutionListener(monitor);
		pluginLord.setCurrentDirectory(new File(new File(buildFilePath).getParent()));

		pluginLord.startExecutionQueue();
		final GradleProcessResult processResult = new GradleProcessResult();

		GradlePluginLord.RequestObserver observer = new GradlePluginLord.RequestObserver() {
	           
			public void executionRequestAdded( ExecutionRequest request ){
				request.setExecutionInteraction( executionListener );
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
	    String gradleCommandLine = setupGradleCommandLine();
	    
	    if(useRefreshQueue){
		    pluginLord.addRefreshRequestToQueue(gradleCommandLine);
	    }else{
	    	pluginLord.addExecutionRequestToQueue(gradleCommandLine, getName());
	    }
	    
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
					"Error while starting Gradle Process. Please check that GRADLE_HOME is defined correctly in your preferences!", executionListener
							.getThrowable());
		}else if( processResult.getResult() == 1) {
			return new Status(IStatus.ERROR, GradlePlugin.PLUGIN_ID, processResult.getOutput(), executionListener.getThrowable());
		}
		return afterGradleExecutionHook(monitor);	

	}
	
	private String setupGradleCommandLine() {
		StringBuffer commandLineArgs = new StringBuffer();
		configureBuildFilePath(commandLineArgs);
		configureInitScript(commandLineArgs);
		configureTasks(commandLineArgs);
		configureAdditionalCmdParams(commandLineArgs);
		commandLineArgs.append(" ").append(uiProvidedCommandLineParams);
		return commandLineArgs.toString();
	}
	
	public IStatus runSynchronized(){
		return run(null);
	}
	/**
	 * This factory method creates a implementation specific ExecutionListener
	 * */
	abstract protected GradleProcessExecListener createExecutionListener(IProgressMonitor monitor);

	/**
	 * this is a hook to add specific code directly executed 
	 * after the gradle action has finished.
	 * */
	protected IStatus afterGradleExecutionHook(IProgressMonitor monitor){
		return Status.OK_STATUS;
	}
}
