package com.espsecurityplugin.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.espsecurityplugin.activator.Activator;


public class PreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	IntegerFieldEditor reconciliationDelay;
	FileFieldEditor sourceRulesFile;
	FileFieldEditor sinkRulesFile;
	BooleanFieldEditor enabled;
	
	public PreferencesPage() {
		super(GRID);
	}
	
	@Override
	public void init(IWorkbench arg0) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Configuration options for ESP: Security Plugin.");
	}

	@Override
	protected void createFieldEditors() {
		
		enabled = new BooleanFieldEditor("esp.disabled", "&Disable ESP", getFieldEditorParent());
		
		reconciliationDelay = new IntegerFieldEditor(
				"reconciliation.delay",	"&Reconcile after:", 
				getFieldEditorParent(), 4);
		
		reconciliationDelay.setValidRange(100, 5000);
		
		sourceRulesFile = new FileFieldEditor(
				"sourcerules.location", "&Source Rule location:", false, getFieldEditorParent());
		sourceRulesFile.setEmptyStringAllowed(true);
		
		sinkRulesFile = new FileFieldEditor(
				"sinkrules.location", "&Sink Rule location:", false, getFieldEditorParent());
		sinkRulesFile.setEmptyStringAllowed(true);
		
		addField(enabled);
		addField(reconciliationDelay);
		addField(sourceRulesFile);
		addField(sinkRulesFile);
		
	}
	
	@Override
	public boolean performOk() {
		enabled.store();
		reconciliationDelay.store();
		sourceRulesFile.store();
		sinkRulesFile.store();
		return super.performOk();
	}
	
	@Override
	protected void performDefaults() {
		reconciliationDelay.loadDefault();
	}

}
