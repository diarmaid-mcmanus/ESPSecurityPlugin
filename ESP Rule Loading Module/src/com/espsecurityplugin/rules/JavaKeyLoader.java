package com.espsecurityplugin.rules;

import java.util.ArrayList;
import java.util.Collection;

import org.xml.sax.helpers.DefaultHandler;


public class JavaKeyLoader extends DefaultHandler {
	
	private Collection<String> javaKeys = new ArrayList<String>();
	
	@Override
	public void characters(char[] ch, int start, int length) {
		String key = new String(ch, start, length);
		if(key == null || key.trim().isEmpty()) {
			// String is empty.
			return;
		}
		javaKeys.add(key.trim());
	}
	
	public Collection<String> getList() {
		return javaKeys;
	}
}
