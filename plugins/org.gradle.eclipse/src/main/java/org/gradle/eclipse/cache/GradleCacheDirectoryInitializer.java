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
package org.gradle.eclipse.cache;

import java.io.File;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.gradle.eclipse.GradlePlugin;
import org.gradle.eclipse.preferences.IGradlePreferenceConstants;

/**
 * @author Rene
 * 
 * This class is used to initialize the GRADLE_CACHE classpath variable;
 * It points to the directory, defined in the GRADLE_CACHE preference.
 * */
public class GradleCacheDirectoryInitializer extends org.eclipse.jdt.core.ClasspathVariableInitializer{

	public void initialize(String variable) {
		IPreferenceStore preferenceStore = GradlePlugin.getPlugin().getPreferenceStore();
		String gradleCache = preferenceStore.getString(IGradlePreferenceConstants.GRADLE_CACHE);
		
		//create directory if it not already exists
		File gradleCacheDir = new File(gradleCache);
		gradleCacheDir.mkdirs();
		try {
			JavaCore.setClasspathVariable(variable, new Path(gradleCacheDir.getAbsolutePath()), null);
		} catch (JavaModelException e) {
			
			// TODO FIX ERROR HANDLING !!!
			e.printStackTrace();
		}
	}	

}
