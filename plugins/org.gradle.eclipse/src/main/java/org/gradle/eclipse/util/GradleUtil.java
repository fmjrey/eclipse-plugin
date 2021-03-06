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
package org.gradle.eclipse.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.gradle.eclipse.GradleNature;
import org.gradle.eclipse.GradlePlugin;
import org.gradle.eclipse.preferences.IGradlePreferenceConstants;

/**
 * @author Rene Groeschke
 * @FIXME refactor this class
 * */
public class GradleUtil {

	/**
	 * Returns the list of Strings that were delimiter separated.
	 * 
	 * @param delimString
	 *            the String to be tokenized based on the delimiter
	 * @return a list of Strings
	 */
	public static String[] parseString(String delimString, String delim) {
		if (delimString == null) {
			return new String[0];
		}

		// Need to handle case where separator character is
		// actually part of the target name!
		StringTokenizer tokenizer = new StringTokenizer(delimString, delim);
		String[] results = new String[tokenizer.countTokens()];
		for (int i = 0; i < results.length; i++) {
			results[i] = tokenizer.nextToken();
		}

		return results;
	}

	/**
	 * Returns the workspace file associated with the given path in the local
	 * file system, or <code>null</code> if none. If the path happens to be a
	 * relative path, then the path is interpreted as relative to the specified
	 * parent file.
	 * 
	 * Attempts to handle linked files; the first found linked file with the
	 * correct path is returned.
	 * 
	 * @param path
	 * @param buildFileParent
	 * @return file or <code>null</code>
	 * @see org.eclipse.core.resources.IWorkspaceRoot#findFilesForLocation(IPath)
	 */
	public static IFile getFileForLocation(String path) {
		if (path == null) {
			return null;
		}
		IPath filePath = new Path(path);
		IFile file = null;
		IFile[] files = ResourcesPlugin.getWorkspace().getRoot()
				.findFilesForLocationURI(filePath.toFile().toURI());
		if (files.length > 0) {
			file = files[0];
		}
		if (file == null) {
			// relative path
			try {
				// this call is ok if buildFileParent is null
				files = ResourcesPlugin.getWorkspace().getRoot()
						.findFilesForLocationURI(filePath.toFile().toURI());
				if (files.length > 0) {
					file = files[0];
				} else {
					return null;
				}
			} catch (Exception be) {
				return null;
			}
		}

		if (file.exists()) {
			return file;
		}
		File ioFile = file.getLocation().toFile();
		if (ioFile.exists()) {// needs to handle case insensitivity on WINOS
			files = ResourcesPlugin.getWorkspace().getRoot()
					.findFilesForLocationURI(ioFile.toURI());
			if (files.length > 0) {
				return files[0];
			}
		}

		return null;
	}

	public static IFile getFileForLocation(IPath filePath, File buildFileParent) {
		if (filePath == null) {
			return null;
		}
		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getFileForLocation(filePath);
		return file;
	}

	public static void addGradleNature(final IProject project)
			throws CoreException {
		final IProjectDescription description = project.getDescription();
		final String[] ids = description.getNatureIds();
		final String[] newIds = new String[ids == null ? 1 : ids.length + 1];
		newIds[0] = GradleNature.GRADLE_NATURE;
		if (ids != null) {
			for (int i = 1; i < newIds.length; i++) {
				newIds[i] = ids[i - 1];
			}
		}

		description.setNatureIds(newIds);
		project.setDescription(description, null);
	}

	public static void removeGradleNature(final IProject project)
			throws CoreException {
		final IProjectDescription description = project.getDescription();
		List<String> ids = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
		ids.remove(GradleNature.GRADLE_NATURE);
		description.setNatureIds(ids.toArray(new String[ids.size()]));
		project.setDescription(description, null);
	}
	
	/**
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public static IPreferenceStore getStoreForProject(IProject project){
		//check whether to use project specific settings or not
		IPreferenceStore store = null;
		try{
			String use = project.getPersistentProperty(new QualifiedName("org.gradle.eclipse.preferences.GradleRuntimePreferencePage", IGradlePreferenceConstants.USE_PROJECT_SETTINGS));
			if ("true".equals(use)){
				store = new ScopedPreferenceStore(new ProjectScope(project), "org.gradle.eclipse.preferences");  	  					
			}else{
				store = GradlePlugin.getDefault().getPreferenceStore();
			}
		}catch(CoreException ce){
			GradlePlugin.log(ce);
			store = GradlePlugin.getDefault().getPreferenceStore();
		}
		return store;
	}
}
