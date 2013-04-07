package com.espsecurityplugin.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.espsecurityplugin.activator.Activator;
import com.espsecurityplugin.rules.JavaKeyLoader;

/**
 * Visits the AST and returns a list of 
 * @author e
 *
 */
public class SourceAnalyser extends ASTVisitor {

	// find based on QualifiedName. We've resolved bindings.
	
	List<String> sourceKeys;
	List<String> taintedVariables;
	
	public SourceAnalyser() throws IOException, ParserConfigurationException, SAXException {
		// get rules file from properties
		String sourceRuleLocation = Activator.getDefault().getPreferenceStore().getString("sourcerules.location");
		InputStream sourceInputStream;
		if(sourceRuleLocation == null || sourceRuleLocation.isEmpty()) {
			Bundle bundle = Platform.getBundle("ESPSecurityPlugin");
			URL url = bundle.getResource("resources/sourceRules.xml");
			URLConnection urlConnection = url.openConnection();
			sourceInputStream = urlConnection.getInputStream();
		} else {
			File file = new File(sourceRuleLocation);
			sourceInputStream = new FileInputStream(file);
		}
		
		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxFactory.newSAXParser();
		DefaultHandler sourceHandler = new JavaKeyLoader();
		saxParser.parse(sourceInputStream, sourceHandler);
		sourceInputStream.close();
		
		sourceKeys = new ArrayList<String>();
		sourceKeys.addAll(((JavaKeyLoader) sourceHandler).getList());
		
		taintedVariables = new ArrayList<String>();
	}
	
	/*
	 * identifier = expression;
	 * 
	 * check if expression is a source expression. if it is, add identifier to a list.
	 */
	@Override
	public boolean visit(VariableDeclarationFragment node) {
		if(isExpressionSource(node.getInitializer())) {
			taintedVariables.addAll(simpleNameToString(node.getName()));
		}
		return true;
	}
	
	@Override
	public boolean visit(Assignment node) {
		if(isExpressionSource(node.getRightHandSide())) {
			taintedVariables.addAll(simpleNameToString((SimpleName) node.getLeftHandSide()));
		}
		return true;
	}
	
	public Collection<String> getTaintedVariables() {
		return this.taintedVariables;
	}
	
	private boolean isExpressionSource(Expression expression) {
		if(expression instanceof MethodInvocation) {
			return isExpressionSource((MethodInvocation) expression);
		}
		return false;
	}
	
	private boolean isExpressionSource(MethodInvocation node) {
		IMethodBinding methodBinding = node.resolveMethodBinding();
		String nodeKey = methodBinding.getKey();
		for(String sourceKey : sourceKeys) {
			if(sourceKey.equals(nodeKey)) {
				return true;
			}
		}
		return false;
	}
	
	private Collection<String> simpleNameToString(SimpleName simpleName) {
		Collection<String> result = new ArrayList<String>();
		result.add(simpleName.getIdentifier());
		return result;
	}

	public void init() {
		taintedVariables = new ArrayList<String>();
	}
}

