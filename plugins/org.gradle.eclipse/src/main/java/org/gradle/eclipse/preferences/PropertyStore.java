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

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;

public class PropertyStore extends PreferenceStore {
	private IResource resource;
	private IPreferenceStore workbenchStore;
	private String pageId;

	public PropertyStore(IResource resource, IPreferenceStore workbenchStore,
			String pageId) {
		this.resource = resource;
		this.workbenchStore = workbenchStore;
		this.pageId = pageId;
	}

	public String getDefaultString(String name) {
		return workbenchStore.getDefaultString(name);
	}

	public String getString(String name) {
		insertValue(name);
		return super.getString(name);
	}

	// local semaphore for insertValue()
	private boolean inserting = false;

	private synchronized void insertValue(String name) {
		if (inserting)
			return;
		if (super.contains(name))
			return;
		inserting = true;
		String prop = null;
		try {
			prop = getProperty(name);
		} catch (CoreException e) {
		}
		if (prop == null)
			prop = workbenchStore.getString(name);
		if (prop != null)
			setValue(name, prop);
		inserting = false;
	}

	private String getProperty(String name) throws CoreException {
		return resource.getPersistentProperty(new QualifiedName(pageId, name));
	}

	public void setToDefault(String name) {
		setValue(name, getDefaultString(name));
	}

	public void save() throws IOException {
		writeProperties();
	}

	public void save(OutputStream out, String header) throws IOException {
		writeProperties();
	}

	private void writeProperties() throws IOException {
		String[] preferences = super.preferenceNames();
		for (int i = 0; i < preferences.length; i++) {
			String name = preferences[i];
			try {
				setProperty(name, getString(name));
			} catch (CoreException e) {
				throw new IOException("Cannot write resource property " + name);
			}
		}
	}

	private void setProperty(String name, String value) throws CoreException {
		resource.setPersistentProperty(new QualifiedName(pageId, name), value);
	}
}
