package com.espsecurityplugin.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.espsecurityplugin.activator.Activator;
import com.espsecurityplugin.feedback.EclipseMarkerFeedback;
import com.espsecurityplugin.interfaces.FeedbackInstance;
import com.espsecurityplugin.rules.JavaKeyLoader;

public class SinkAnalyser extends ASTVisitor {
	
	Collection<String> sinkKeys;
	Collection<String> taintedVariables;
	Collection<FeedbackInstance> feedbackList;
	
	public SinkAnalyser() throws IOException, ParserConfigurationException, SAXException {
		
		String sinkRuleLocation = Activator.getDefault().getPreferenceStore().getString("sinkrules.location");
		InputStream sinkInputStream;
		if(sinkRuleLocation == null || sinkRuleLocation.isEmpty()) {
			Bundle bundle = Platform.getBundle("ESPSecurityPlugin");
			URL url = bundle.getResource("resources/sinkRules.xml");
			URLConnection urlConnection = url.openConnection();
			sinkInputStream = urlConnection.getInputStream();
		} else {
			File file = new File(sinkRuleLocation);
			sinkInputStream = new FileInputStream(file);
		}
		
		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxFactory.newSAXParser();
		DefaultHandler sinkHandler = new JavaKeyLoader();
		saxParser.parse(sinkInputStream, sinkHandler);
		sinkInputStream.close();
		
		sinkKeys = new ArrayList<String>();
		sinkKeys.addAll(((JavaKeyLoader) sinkHandler).getList());
		
		feedbackList = new ArrayList<FeedbackInstance>();
	}

	@Override
	public boolean visit(MethodInvocation node) {
		IMethodBinding methodBinding = node.resolveMethodBinding();
		if(methodBinding == null) return true;
		String nodeKey = methodBinding.getKey();

		for(String sinkKey : sinkKeys) {
			if(sinkKey.equals(nodeKey)) {
				// is an argument a taintedVar?
				for(Object expression : node.arguments()) {
					for(String taintedVar : taintedVariables) {
						if(matches((Expression)expression, taintedVar)) {
							createFeedback(node);
						}
					}
				}
			}
		}
		return true;
	}
	
	public void setTaintedVariables(Collection<String> taintedVariables) {
		this.taintedVariables = taintedVariables;
	}
	
	public Collection<FeedbackInstance> getFeedback() {
		return feedbackList;
	}
	
	private boolean matches(Expression expression, String taintedVariable) {
		if(expression instanceof SimpleName) {
			return matches((SimpleName)expression, taintedVariable);
		}
		return false;
	}
	
	private boolean matches(SimpleName expression, String taintedVariable) {
		return taintedVariable.equals(expression.getIdentifier());
	}

	private void createFeedback(ASTNode node) {
		FeedbackInstance feedback = new EclipseMarkerFeedback();
		feedback.setMessage("Validate data before using it!"); // TODO configurable message
		feedback.setStartPosition(node.getStartPosition());
		feedback.setOffset(node.getLength());
		feedbackList.add(feedback);
	}

	public void init() {
		feedbackList.clear();
	}

}
