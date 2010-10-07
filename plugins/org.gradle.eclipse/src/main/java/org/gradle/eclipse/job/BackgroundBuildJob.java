package org.gradle.eclipse.job;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.IPreferenceStore;
import org.gradle.eclipse.preferences.IGradlePreferenceConstants;
import org.gradle.eclipse.util.GradleUtil;

public abstract class BackgroundBuildJob extends AbstractGradleJob{

	private String initScriptPath = null;
	private String buildFilePath;
	protected final IProject project;

	private List<String> tasks = new ArrayList<String>();
	
	public BackgroundBuildJob(IProject project, String name, String absoluteBuildFilePath){
		super(name);
		this.project = project;
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
	
	protected void configureGradleCache(StringBuffer commandLineArgs) {
		IPreferenceStore store = GradleUtil.getStoreForProject(project);
		boolean useCustomCache = store.getBoolean(IGradlePreferenceConstants.USE_SPECIFIC_GRADLE_CACHE);
		if(useCustomCache){
			String gradleCache = store.getString(IGradlePreferenceConstants.GRADLE_CACHE);
			commandLineArgs.append(" -g ");
			commandLineArgs.append(gradleCache);
		}
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		StringBuffer setupGradle = setupGradleCommandLine();
		return runGradleJob(monitor, setupGradle);
	}
	
	abstract IStatus runGradleJob(IProgressMonitor monitor, StringBuffer commandLine);
	
	private StringBuffer setupGradleCommandLine() {
		StringBuffer commandLineArgs = new StringBuffer();
		configureBuildFilePath(commandLineArgs);
		configureInitScript(commandLineArgs);
		configureTasks(commandLineArgs);
		return commandLineArgs;
	}



}
