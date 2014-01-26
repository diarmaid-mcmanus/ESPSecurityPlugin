package com.espsecurityplugin.preferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.espsecurityplugin.activator.Activator;


public class PreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	Map<String, Boolean> sourceRules;
	Map<String, Boolean> sinkRules;
	Map<String, Boolean> validationRules;
	
	IntegerFieldEditor reconciliationDelay;
	CustomFileFieldEditor sourceRulesFile;
	CustomFileFieldEditor sinkRulesFile;
	CustomFileFieldEditor validationRulesFile;
	BooleanFieldEditor enabled;
	
	public PreferencesPage() {
		super(GRID);
	}
	
	@Override
	public void init(IWorkbench arg0) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Configuration options for ESP: Security Plugin.");
		loadSerialisedPreferences();
	}

	/*
	 *  Load the serialised preferences from preferences store; convert it back into a map.
	 */
	private void loadSerialisedPreferences() {
		String sourceRulesString = Platform.getPreferencesService().getString("ESPSecurityPlugin", "sourcerules.serialised", null, null);
		sourceRules = convertStringToMap(sourceRulesString);
		String sinkRulesString = Platform.getPreferencesService().getString("ESPSecurityPlugin", "sinkrules.serialised", null, null);
		sinkRules = convertStringToMap(sinkRulesString);
		String validationRulesString = Platform.getPreferencesService().getString("ESPSecurityPlugin", "validationrules.serialised", null, null);
		validationRules = convertStringToMap(validationRulesString);
		
		for(String key : sourceRules.keySet()) {
			Activator.getLogger().log(Level.INFO, key);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Boolean> convertStringToMap(String rulesInput) {
		// Input is Base64 encoded
		if(rulesInput == null) {
			Activator.getLogger().log(Level.INFO, "Input string null - returning empty hashmap");
			return new HashMap<String,Boolean>();
		}
		byte[] rulesBytes = DatatypeConverter.parseBase64Binary(rulesInput);
		ByteArrayInputStream input = new ByteArrayInputStream(rulesBytes);
		ObjectInput inputObject = null;
		Object object = null;
		try {
			inputObject = new ObjectInputStream(input);
			try {
				object = inputObject.readObject();
			} catch (ClassNotFoundException e) {
				Activator.getLogger().log(Level.INFO, "CNF - empty hashmap");
				// TODO raise a log message here.
				return new HashMap<String,Boolean>();
			}
		} catch (IOException exception) {
			// TODO raise a log message here
			Activator.getLogger().log(Level.INFO, "IOE - eh");
			return new HashMap<String,Boolean>();
		}
		if(!(object instanceof Map)) {
			// TODO raise a log message here
			Activator.getLogger().log(Level.INFO, "object not instanceof");
			return new HashMap<String,Boolean>();
		}
		return (Map<String, Boolean>)object;
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
		
		sourceRulesFile = new CustomFileFieldEditor(
				"sourcerules.location", "&Source Rule location:", false, getFieldEditorParent());
		sourceRulesFile.setPreferencesPage(this);
		sourceRulesFile.setEmptyStringAllowed(true);
		
		sinkRulesFile = new CustomFileFieldEditor(
				"sinkrules.location", "&Sink Rule location:", false, getFieldEditorParent());
		sinkRulesFile.setPreferencesPage(this);
		sinkRulesFile.setEmptyStringAllowed(true);
		
		validationRulesFile = new CustomFileFieldEditor(
				"validationrules.location", "&Validation Rule location:", false, getFieldEditorParent());
		validationRulesFile.setPreferencesPage(this);
		validationRulesFile.setEmptyStringAllowed(true);
		
		addField(enabled);
		addField(reconciliationDelay);
		addField(sourceRulesFile);
		addField(sinkRulesFile);
		addField(validationRulesFile);
		
	}
	
	// will this need two arguments; filePath and which list it should be added to? 
	public void addFileToList(String filePath, int list) {
		Activator.getLogger().log(Level.INFO, filePath);
		Preferences preferences = ConfigurationScope.INSTANCE.getNode("ESPSecurityPlugin");
		switch(list) {
		case 0:	// source
			// add it to the map
			sourceRules.put(filePath, true);
			String serialisedSourceRules = serialiseMap(sourceRules);
			preferences.put("sourcerules.serialised", serialisedSourceRules);
			Activator.getLogger().log(Level.INFO, Platform.getPreferencesService().getString("ESPSecurityPlugin", "sourcerules.serialised", null, null));
			break;
		case 1:
			sinkRules.put(filePath, true);
			String serialisedSinkRules = serialiseMap(sinkRules);
			preferences.put("sinkrules.serialised", serialisedSinkRules);
			Activator.getLogger().log(Level.INFO, Platform.getPreferencesService().getString("ESPSecurityPlugin", "sinkrules.serialised", null, null));
			break;
		case 2:
			validationRules.put(filePath, true);
			String serialisedValidationRules = serialiseMap(validationRules);
			preferences.put("validationrules.serialised", serialisedValidationRules);
			Activator.getLogger().log(Level.INFO, Platform.getPreferencesService().getString("ESPSecurityPlugin", "validationrules.serialised", null, null));
			break;
		default:
			break;
		}
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			// TODO raise a log here
		}
	}

	private String serialiseMap(Map<String, Boolean> sourceRules2) {
		// serialise the map
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ObjectOutput objectOutput = null;
		try {
			objectOutput = new ObjectOutputStream(output);
			objectOutput.writeObject(sourceRules);
			objectOutput.flush();
		} catch (IOException e) {
			// TODO raise an error here
			e.printStackTrace();
		} finally {
			if (objectOutput != null) {
				try {
					objectOutput.close();
				} catch (IOException e) {
					// ignore this.
				}
			}
		}
		String serialisedSourceRules = DatatypeConverter.printBase64Binary(output.toByteArray());
		return serialisedSourceRules;
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
