package com.espsecurityplugin.preferences;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class CustomFileFieldEditor extends FileFieldEditor {


	PreferencesPage preferencesPage;
	
	/*
	 * Super power theme tune
	 * Everybody need to
	 * Get up out seat
	 * feel the beat when we breeze through
	 */
	public CustomFileFieldEditor(String string, String string2, boolean b,
			Composite fieldEditorParent) {
		super(string, string2, b, fieldEditorParent);
	}
	
	public void setPreferencesPage(PreferencesPage preferencesPage) {
		this.preferencesPage = preferencesPage;
	}
	
	/*
	 * When the value in 
	 */
	@Override
	protected void fireValueChanged(String property, Object oldValue, Object newValue) {
		preferencesPage.addFileToList((String)newValue, 0);
		super.fireValueChanged(property, oldValue, newValue);
	}

}
