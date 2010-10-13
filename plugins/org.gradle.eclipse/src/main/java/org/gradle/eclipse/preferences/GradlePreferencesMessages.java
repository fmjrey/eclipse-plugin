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
package org.gradle.eclipse.preferences;

import org.eclipse.osgi.util.NLS;

public class GradlePreferencesMessages extends NLS {
	private static final String BUNDLE_NAME = "org.gradle.eclipse.preferences.GradlePreferencesMessages";//$NON-NLS-1$

	public static String GradleRuntimePreferencePage_USE_MANUEL_GRADLE_CACHE = null;

	public static String GradleRuntimePreferencePage_GRADLE_CACHE_DIR;

	public static String GradleRuntimePreferencePage_GradleHome_Not_Set_ERROR;

	public static String GradleRuntimePreferencePage_USE_MANUEL_GRADLE_HOME;
	
	public static String GradleRuntimePreferencePage_Description;

	public static String GradleRuntimePreferencePage_GradleHome_Label;

	public static String GradleRuntimePreferencePage_BuildFileName;

	public static String GradleRuntimePreferencePage_Enter;

	public static String GradlePreferencePage_Gradle_Color_Options__6;

	public static String GradlePreferencePage_Color__7;

	public static String GradlePreferencePage__Error__2;

	public static String GradlePreferencePage__Warning__3;

	public static String GradlePreferencePage_I_nformation__4;

	public static String GradlePreferencePage_Ve_rbose__5;

	public static String GradlePreferencePage_Deb_ug__6;

	public static String GradleRuntimePreferencePage_ADDITIONAL_COMMANDLINE_PARAMS;
	
	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, GradlePreferencesMessages.class);
	}
}
