package com.espsecurityplugin.feedback;

import com.espsecurityplugin.feedback.internal.ConcreteContextModel;

public class ContextFactory {

	private static ContextModel contextModel = new ConcreteContextModel();
	
	public static ContextModel getInstance() {
		return contextModel;
	}
}
