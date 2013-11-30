package com.espsecurityplugin.rules.model;

public class Rule {

	private String key;
	private boolean argumentsRequired = true; // default

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isArgumentsRequired() {
		return argumentsRequired;
	}

	public void setArgumentsRequired(boolean argumentsRequired) {
		this.argumentsRequired = argumentsRequired;
	}

}
