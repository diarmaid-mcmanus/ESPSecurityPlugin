package com.espsecurityplugin.rules;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.espsecurityplugin.rules.model.Rule;

public class RuleLoader {
	
	public static List<Rule> loadRules(String location, boolean inBundle) throws IOException, ParserConfigurationException, SAXException {
		List<Rule> rules = new ArrayList<Rule>();
		
		InputStream sourceInputStream;
		if(inBundle) {
			Bundle bundle = Platform.getBundle("ESPSecurityPlugin");
			URL url = bundle.getResource(location);
			URLConnection urlConnection = url.openConnection();
			sourceInputStream = urlConnection.getInputStream();
		} else {
			File file = new File(location);
			sourceInputStream = new FileInputStream(file);
		}

		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxFactory.newSAXParser();
		DefaultHandler sourceHandler = new JavaKeyLoader();
		saxParser.parse(sourceInputStream, sourceHandler);
		sourceInputStream.close();

		rules.addAll(((JavaKeyLoader) sourceHandler).getList());
		return rules;
	}

}
