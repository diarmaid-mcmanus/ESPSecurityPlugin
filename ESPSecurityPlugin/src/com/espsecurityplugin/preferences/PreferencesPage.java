package com.espsecurityplugin.preferences;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.espsecurityplugin.activator.Activator;


public class PreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	Map<String, Boolean> sourceRules;
	Map<String, Boolean> sinkRules;
	Map<String, Boolean> validationRules;
	
	IntegerFieldEditor reconciliationDelay;
	CustomFileFieldEditor sourceRulesFile;
	FileFieldEditor sinkRulesFile;
	FileFieldEditor validationRulesFile;
	BooleanFieldEditor enabled;
	
	public PreferencesPage() {
		super(GRID);
		sourceRules = new HashMap<String, Boolean>();
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
		
		/*
		 * Temporary: create a checkbox and a string label for each rule file.
		 * Long term: create a fancy table, with two columns,
		 */
		
		// Preferences are stored (path,boolean);(path,boolean) for (rule file,enabled)
		
//		for(AbstractMap.SimpleEntry<String, Boolean> rulesMap : convertToMap(sinkRules)) {
			// TODO
//		}
		
//		for(AbstractMap.SimpleEntry<String, Boolean> rulesMap : convertToMap(validationRules)) {
			// TODO
//		}
		
		sourceRulesFile = new CustomFileFieldEditor(
				"sourcerules.location", "&Source Rule location:", false, getFieldEditorParent());
		sourceRulesFile.setPreferencesPage(this);
		sourceRulesFile.setEmptyStringAllowed(true);
		
		for(Entry<String, Boolean> entry : sourceRules.entrySet()) {
			if(entry.getValue() == true) {
				Activator.getLogger().log(Level.INFO, entry.getKey());
			}
			
		}
		
		sinkRulesFile = new FileFieldEditor(
				"sinkrules.location", "&Sink Rule location:", false, getFieldEditorParent());
		sinkRulesFile.setEmptyStringAllowed(true);
		
		validationRulesFile = new FileFieldEditor(
				"validationrules.location", "&Validation Rule location:", false, getFieldEditorParent());
		validationRulesFile.setEmptyStringAllowed(true);
		
		addField(enabled);
		addField(reconciliationDelay);
		addField(sourceRulesFile);
		addField(sinkRulesFile);
		addField(validationRulesFile);
		
	}
	
	private Iterable<AbstractMap.SimpleEntry<String, Boolean>> convertToMap(String sourceRules2) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// will this need two arguments; filePath and which list it should be added to? 
	public void addFileToList(String filePath, int list) {
		Activator.getLogger().log(Level.INFO, filePath);
		switch(list) {
		case 0:	// source
			sourceRules.put(filePath, true);
			break;
		case 1:
			sinkRules.put(filePath, true);
			break;
		case 2:
			validationRules.put(filePath, true);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean performOk() {
		enabled.store();
		reconciliationDelay.store();
		sourceRulesFile.store();
		sinkRulesFile.store();
		validationRulesFile.store();
		return super.performOk();
	}
	
	@Override
	protected void performDefaults() {
		reconciliationDelay.loadDefault();
	}

}
