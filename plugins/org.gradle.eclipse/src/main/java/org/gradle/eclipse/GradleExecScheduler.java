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
package org.gradle.eclipse;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.gradle.eclipse.GradlePlugin;
import org.gradle.eclipse.job.ConfigurationBasedBuildJob;
import org.gradle.eclipse.job.RefreshTaskJob;
import org.gradle.eclipse.job.UpdateClasspathJob;
import org.gradle.eclipse.launchConfigurations.GradleProcess;
import org.gradle.eclipse.util.GradleUtil;
import org.gradle.foundation.ProjectView;
import org.gradle.gradleplugin.foundation.GradlePluginLord;


/**
 * @author Rene Groeschke
 * */
public class GradleExecScheduler {

	private static GradleExecScheduler instance = null;

	/**
	 * The Gradle Scheduler manages the lifecycle of the buildinformation cache
	 * */
	private BuildInformationCache cache;


	public static GradleExecScheduler getInstance() {
		if(instance==null){
			instance = new GradleExecScheduler();
		}
		return instance;
	}

	private GradleExecScheduler(){
		this.cache = new BuildInformationCache();
		GradlePlugin.getPlugin().getGradleHome(); // side-effect: make gradle file in bin dir executable
		GradlePlugin.getPlugin().getDefaultGradleHome(); // side-effect: make gradle file in bin dir executable
	}

	public List<ProjectView> getProjectViews(String absolutePath) {
		if(cache.get(absolutePath)==null){
			refreshTaskView(absolutePath, true);
		}
		return cache.get(absolutePath);
	}

	public void refreshTaskView(final String absolutePath, boolean synched) {
		if(absolutePath!=null && !absolutePath.isEmpty()){
			final File absoluteDirectory = new File(absolutePath).getParentFile();

			if(absoluteDirectory.exists()){
				//run gradle only if directory exists
				final GradlePluginLord gradlePluginLord = new GradlePluginLord();

				IContainer containerForLocation = getProjectForPath(absolutePath);
				RefreshTaskJob job = new RefreshTaskJob((IProject)containerForLocation, absolutePath, gradlePluginLord, cache);

				if(!synched){
					job.setUser(false);
					job.setSystem(true);
					job.setPriority(Job.LONG);

					job.schedule(); // start as soon as possible
				}

				else{
					job.setUser(true);
					job.runSynchronized();
					// something wrong while calculating tasks
					final IStatus status = job.getResult();
					if(status!=null && !status.isOK()){
						final Display display = Display.getCurrent();
						display.asyncExec(new Runnable() {
					    		public void run() {
					    			MessageDialog.openError(display.getActiveShell(), "Error while calculating gradle tasks", status.getMessage());					    		}
					    });
					}
				}
			}
		}
	}

	/**
	 * @param absolutePath
	 * @return
	 */
	private IContainer getProjectForPath(final String absolutePath) {
		//resolve the IProject object of the buildfile
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IFile buildFileIFile = GradleUtil.getFileForLocation(absolutePath);
		IContainer containerForLocation = root.getContainerForLocation(buildFileIFile.getLocation());
		while(! (containerForLocation instanceof IProject)){
			containerForLocation = containerForLocation.getParent();
		}
		return containerForLocation;
	}

	/**
	 *
	 * @param projectToUpdate the project which classpath will be updated
	 * */
	public void updateProjectClasspath(IPreferenceStore store, String absoluteBuildPath, IProject projectToUpdate) throws CoreException{
		final GradlePluginLord gradlePluginLord = new GradlePluginLord();
		gradlePluginLord.setGradleHomeDirectory(new File(GradlePlugin.getPlugin().getGradleHome(store)));

		File buildFile = new File(VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(absoluteBuildPath));
		if(buildFile==null || !buildFile.exists()){
			throw(new CoreException(new Status(IStatus.ERROR, IGradleConstants.PLUGIN_ID,
											   "buildPath: [ " + absoluteBuildPath + "] cannot be resolved")
				 ));
		}

		// create gradle build job
		Job job = new UpdateClasspathJob(projectToUpdate, gradlePluginLord, absoluteBuildPath);
		job.setUser(true);
		job.setPriority(Job.LONG);
		job.schedule(); // start as soon as possible
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public void startGradleBuildRun(final ILaunchConfiguration configuration, final List<String> tasks, final StringBuffer commandLine, final GradleProcess gradleProcess) throws CoreException{
		final GradlePluginLord gradlePluginLord = new GradlePluginLord();
		gradlePluginLord.setGradleHomeDirectory(new File(GradlePlugin.getPlugin().getGradleHome()));
		String buildfilePath = configuration.getAttribute(IGradleConstants.ATTR_LOCATION, "");
		File buildFile = new File(VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(buildfilePath));
		if(buildFile==null || !buildFile.exists()){
			throw(new CoreException(new Status(IStatus.ERROR,
											   IGradleConstants.PLUGIN_ID,
											   "buildPath: [ " + buildfilePath + "] cannot be resolved")
				));
		}
		// create and schedule gradle build job
		IContainer projectForPath = getProjectForPath(buildFile.getAbsolutePath());
		ConfigurationBasedBuildJob job = new ConfigurationBasedBuildJob((IProject)projectForPath, gradlePluginLord, buildFile.getAbsolutePath(), gradleProcess);
		job.setTasks(tasks);
		job.setUiProvidedCommandLineParams(commandLine.toString());
		job.setUser(true);
		job.setPriority(Job.LONG);
		job.schedule(); // start as soon as possible
	}
}
