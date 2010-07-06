package org.gradle.eclipse.job;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class BackgroundBuildJob extends AbstractGradleJob{

	private String initScriptPath = null;
	private String buildFilePath;
	private List<String> tasks = new ArrayList<String>();
	
	public BackgroundBuildJob(String name, String absoluteBuildFilePath){
		super(name);
		this.buildFilePath = absoluteBuildFilePath;
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
}
