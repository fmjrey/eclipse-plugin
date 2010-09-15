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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.gradle.eclipse.GradlePlugin;

/**
 * @author Rene Groeschke
 * 
 */
public class GradleRuntimePreferencePage extends FieldEditorOverlayPage
		implements IWorkbenchPreferencePage, IWorkbenchPropertyPage {

	private static final String PAGE_ID = "org.gradle.eclipse.preferences.GradleRuntimePreferencePage";
	private List consoleColorList;
	private ColorEditor consoleColorEditor;
	private BooleanFieldEditor useCustomGradleHomeFieldEditor;
	private DirectoryFieldEditor gradleHomeDirectoryFieldEditor;
	private DirectoryFieldEditor gradleCacheDirEditor;

	// Array containing the message to display, the preference key, and the
	// default value (initialized in storeInitialValues()) for each color
	// preference
	private final String[][] fAppearanceColorListModel = new String[][] {
			{ GradlePreferencesMessages.GradlePreferencePage__Error__2,
					IGradlePreferenceConstants.CONSOLE_ERROR_COLOR, null },
			{ GradlePreferencesMessages.GradlePreferencePage__Warning__3,
					IGradlePreferenceConstants.CONSOLE_WARNING_COLOR, null },
			{ GradlePreferencesMessages.GradlePreferencePage_I_nformation__4,
					IGradlePreferenceConstants.CONSOLE_INFO_COLOR, null },
			{ GradlePreferencesMessages.GradlePreferencePage_Ve_rbose__5,
					IGradlePreferenceConstants.CONSOLE_VERBOSE_COLOR, null },
			{ GradlePreferencesMessages.GradlePreferencePage_Deb_ug__6,
					IGradlePreferenceConstants.CONSOLE_DEBUG_COLOR, null }, };

	/**
	 * Create the Gradle page.
	 */
	public GradleRuntimePreferencePage() {
		super(GRID);
	}

	public IPreferenceStore doGetPreferenceStore() {
		return GradlePlugin.getDefault().getPreferenceStore();
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#initialize()
	 */
	protected void initialize() {
		super.initialize();
		if(!isPropertyPage()){
			for (int i = 0; i < fAppearanceColorListModel.length; i++) {
				consoleColorList.add(fAppearanceColorListModel[i][0]);
			}
			consoleColorList.getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (consoleColorList != null && !consoleColorList.isDisposed()) {
						consoleColorList.select(0);
						handleAppearanceColorListSelection();
					}
				}
			});			
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	protected void createFieldEditors() {
		storeAppliedValues();
		

		if(!isPropertyPage()){
			Font font = getFieldEditorParent().getFont();
			Label label = new Label(getFieldEditorParent(), SWT.WRAP);
			label.setText(GradlePreferencesMessages.GradleRuntimePreferencePage_Enter);
			GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			gd.horizontalSpan = 3;
			gd.widthHint = convertWidthInCharsToPixels(60);
			label.setLayoutData(gd);
			label.setLayoutData(gd);
			label.setFont(font);

			FieldEditor editor = new StringFieldEditor(
					IGradlePreferenceConstants.GRADLE_FIND_BUILD_FILE_NAMES,
					GradlePreferencesMessages.GradleRuntimePreferencePage_BuildFileName,
					getFieldEditorParent());
			addField(editor);
		}
		
		gradleCacheDirEditor = new DirectoryFieldEditor(
				IGradlePreferenceConstants.GRADLE_CACHE,
				GradlePreferencesMessages.GradleRuntimePreferencePage_GRADLE_CACHE_DIR,
				getFieldEditorParent());
		gradleCacheDirEditor.setEmptyStringAllowed(isPropertyPage());
//		gradleCacheDirEditor.setErrorMessage("GRADLE_CACHE must not be empty!");
//		gradleCacheDirEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
//
		gradleCacheDirEditor.setPropertyChangeListener(this);

		addField(gradleCacheDirEditor);

		useCustomGradleHomeFieldEditor = new BooleanFieldEditor(
				IGradlePreferenceConstants.USE_SPECIFIC_GRADLE_HOME,
				GradlePreferencesMessages.GradleRuntimePreferencePage_USE_MANUEL_GRADLE_HOME,
				getFieldEditorParent());
		useCustomGradleHomeFieldEditor.setPropertyChangeListener(this);
		addField(useCustomGradleHomeFieldEditor);

		gradleHomeDirectoryFieldEditor = new DirectoryFieldEditor(
				IGradlePreferenceConstants.MANUELL_GRADLE_HOME,
				GradlePreferencesMessages.GradleRuntimePreferencePage_GradleHome_Label,
				getFieldEditorParent());
		gradleHomeDirectoryFieldEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		gradleHomeDirectoryFieldEditor.setErrorMessage("GRADLE_HOME must not be empty!");

		gradleHomeDirectoryFieldEditor.setPropertyChangeListener(this);
		addField(gradleHomeDirectoryFieldEditor);

		boolean useCustom = getPreferenceStore().getBoolean(
				IGradlePreferenceConstants.USE_SPECIFIC_GRADLE_HOME);
		if (useCustom) {
			gradleHomeDirectoryFieldEditor.setEmptyStringAllowed(false);
			gradleHomeDirectoryFieldEditor.setEnabled(true,	getFieldEditorParent());
		} else {
			gradleHomeDirectoryFieldEditor.setEmptyStringAllowed(true);
			gradleHomeDirectoryFieldEditor.setEnabled(false, getFieldEditorParent());
		}
		createSpace();
		getPreferenceStore().addPropertyChangeListener(this);

		if(!isPropertyPage()){
			createColorComposite();
		}
		
		checkState();
	}

	/**
	 * Stores the initial values of the color preferences. The preference values
	 * are updated on the fly as the user edits them (instead of only when they
	 * press "Apply"). We need to store the old values so that we can reset them
	 * when the user chooses "Cancel".
	 */
	private void storeAppliedValues() {
		IPreferenceStore store = getPreferenceStore();
		for (int i = 0; i < fAppearanceColorListModel.length; i++) {
			String preference = fAppearanceColorListModel[i][1];
			fAppearanceColorListModel[i][2] = store.getString(preference);
		}
	}

	private void createColorComposite() {
		Font font = getFieldEditorParent().getFont();
		Label label = new Label(getFieldEditorParent(), SWT.LEFT);
		label.setText(GradlePreferencesMessages.GradlePreferencePage_Gradle_Color_Options__6);
		label.setFont(font);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);

		Composite editorComposite = new Composite(getFieldEditorParent(),
				SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		editorComposite.setLayout(layout);
		editorComposite.setFont(font);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.FILL_VERTICAL);
		gd.horizontalSpan = 2;
		editorComposite.setLayoutData(gd);

		consoleColorList = new List(editorComposite, SWT.SINGLE | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING
				| GridData.FILL_HORIZONTAL);
		gd.heightHint = convertHeightInCharsToPixels(8);
		consoleColorList.setLayoutData(gd);
		consoleColorList.setFont(font);

		Composite stylesComposite = new Composite(editorComposite, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		stylesComposite.setLayout(layout);
		stylesComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		stylesComposite.setFont(font);

		label = new Label(stylesComposite, SWT.LEFT);
		label.setText(GradlePreferencesMessages.GradlePreferencePage_Color__7);
		label.setFont(font);
		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		label.setLayoutData(gd);

		consoleColorEditor = new ColorEditor(stylesComposite);
		Button foregroundColorButton = consoleColorEditor.getButton();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = GridData.BEGINNING;
		foregroundColorButton.setLayoutData(gd);
		foregroundColorButton.setFont(font);

		consoleColorList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleAppearanceColorListSelection();
			}
		});

		foregroundColorButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int i = consoleColorList.getSelectionIndex();
				if (i == -1) { // bug 85590
					return;
				}
				String key = fAppearanceColorListModel[i][1];
				PreferenceConverter.setValue(getPreferenceStore(), key,
						consoleColorEditor.getColorValue());
			}
		});
	}

	private void handleAppearanceColorListSelection() {
		int i = consoleColorList.getSelectionIndex();
		if (i == -1) { // bug 85590
			return;
		}
		String key = fAppearanceColorListModel[i][1];
		RGB rgb = PreferenceConverter.getColor(getPreferenceStore(), key);
		consoleColorEditor.setColorValue(rgb);
	}

	private void createSpace() {
		Label label = new Label(getFieldEditorParent(), SWT.NONE);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);
	}

	/**
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
	 */
	public void dispose() {
		getPreferenceStore().removePropertyChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse
	 * .jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getSource().equals(useCustomGradleHomeFieldEditor)) {
			boolean useCustomHome = (Boolean) event.getNewValue();
			revalidateFields(useCustomHome);
		}
		checkState();
	}

	private void revalidateFields(boolean useCustomHome) {
		String stringValue = gradleHomeDirectoryFieldEditor.getStringValue();
		gradleHomeDirectoryFieldEditor.setEnabled(useCustomHome, getFieldEditorParent());
		gradleHomeDirectoryFieldEditor.setEmptyStringAllowed(!useCustomHome);
		gradleHomeDirectoryFieldEditor.setStringValue(stringValue);
		gradleHomeDirectoryFieldEditor.load();
		gradleHomeDirectoryFieldEditor.setStringValue(stringValue);
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gradle.eclipse.preferences.FieldEditorOverlayPage#getPageId()
	 */
	@Override
	protected String getPageId() {
		return PAGE_ID;
	}
	
	
	protected void updateFieldEditors(boolean enabled) {
		super.updateFieldEditors(enabled);
		revalidateFields(useCustomGradleHomeFieldEditor.getBooleanValue() && enabled);
		gradleCacheDirEditor.setEmptyStringAllowed(isPropertyPage() && !enabled);
//		gradleHomeDirectoryFieldEditor.setEnabled(, getFieldEditorParent());
		checkState();
	}
	
	protected void performDefaults() {
		super.performDefaults();
		revalidateFields(useCustomGradleHomeFieldEditor.getBooleanValue());
		checkState();
    }
	 
}
