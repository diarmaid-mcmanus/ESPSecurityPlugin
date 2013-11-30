package com.espsecurityplugin.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.espsecurityplugin.rules.model.Rule;


public class JavaKeyLoader extends DefaultHandler {
	
	private Collection<Rule> rules = new ArrayList<Rule>();
	private Stack<Rule> ruleStack = new Stack<Rule>();
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if("rule".equals(qName)) {
			Rule rule = new Rule();
			
			if(attributes != null && attributes.getLength() > 0) {
				rule.setArgumentsRequired(Boolean.parseBoolean(attributes.getValue(0)));
			}
			
			ruleStack.push(rule);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if("rule".equals(qName)) {
			try {
				Rule rule = ruleStack.pop();
				rules.add(rule);
			} catch (EmptyStackException exception) {
				// Most likely, the java key was empty. ignore. This means bad xml, maybe raise an alert
			}
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) {
		String key = new String(ch, start, length);
		
		if(key == null || key.trim().isEmpty()) {
			// this is weird. it has to return here, I can't negate the checks 
			// using ! and get rid of the return, to move the below block in 
			// here. That causes a weird exception. 
			return;
		}
		Rule rule = ruleStack.pop();
		rule.setKey(key.trim());
		ruleStack.push(rule);
	}
	
	public Collection<Rule> getList() {
		return rules;
	}
}
