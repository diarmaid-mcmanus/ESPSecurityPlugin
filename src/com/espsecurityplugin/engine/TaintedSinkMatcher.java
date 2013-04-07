package com.espsecurityplugin.engine;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.xml.sax.SAXException;

import com.espsecurityplugin.activator.Activator;
import com.espsecurityplugin.feedback.ConcreteContextModel;
import com.espsecurityplugin.feedback.EclipseMarkerFeedbackReporter;
import com.espsecurityplugin.interfaces.FeedbackInstance;
import com.espsecurityplugin.interfaces.FeedbackReporter;

public class TaintedSinkMatcher implements Runnable {
	
	private final Logger LOG = Activator.getLogger();
	
	private ASTVisitor sourceAnalyser;
	private ASTVisitor taintAnalyser;
	private ASTVisitor sinkAnalyser;
	private FeedbackReporter feedbackReporter;
	
	public TaintedSinkMatcher() {
		try {
			sourceAnalyser = new SourceAnalyser();
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Error setting up Source Analyser: " + e.getMessage());
		} catch (ParserConfigurationException e) {
			LOG.log(Level.WARNING, "Error setting up Source Analyser: " + e.getMessage());
		} catch (SAXException e) {
			LOG.log(Level.WARNING, "Error setting up Source Analyser: " + e.getMessage());
		}
		
		try {
			sinkAnalyser = new SinkAnalyser();
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Error setting up Sink Analyser: " + e.getMessage());
		} catch (ParserConfigurationException e) {
			LOG.log(Level.WARNING, "Error setting up Sink Analyser: " + e.getMessage());
		} catch (SAXException e) {
			LOG.log(Level.WARNING, "Error setting up Sink Analyser: " + e.getMessage());
		}
		
		taintAnalyser = new TaintAnalyser();
		feedbackReporter = new EclipseMarkerFeedbackReporter();
	}
	
	Logger LOGGER = Activator.getLogger();

	@Override
	public void run() {
		// Clear the markers. Otherwise, when we disable the plugin, they'll hang around forever.
		try {
			ConcreteContextModel.getContextModel().getResource().deleteMarkers("ESPSecurityPlugin.secproblem", true, IResource.DEPTH_ZERO);
			((SourceAnalyser) sourceAnalyser).init();
			((SinkAnalyser) sinkAnalyser).init();
		} catch (CoreException e) {
			LOGGER.log(Level.WARNING, e.getMessage());
		}
		
		// next ensure we're enabled
		Boolean disabled = Activator.getDefault().getPreferenceStore().getBoolean("esp.disabled");
		if(disabled) {
			// Don't create the AST, dn't analyse.
			return;
		} 
		
		
		ASTParser astParser = ASTParser.newParser(AST.JLS4);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		astParser.setSource(ConcreteContextModel.getContextModel().getCompilationUnit());
		astParser.setResolveBindings(true);
		CompilationUnit target = (CompilationUnit) astParser.createAST(null); // TODO IProgressMonitor
		
		// now run the SourceAnalyser, TaintAnalyser and SinkAnalyser in order
		target.accept(sourceAnalyser);
		Collection<String> sourceVars = ((SourceAnalyser) sourceAnalyser).getTaintedVariables();
		((TaintAnalyser) taintAnalyser).setTaintedVariables(sourceVars); // TODO ;_; fix this line
		
		target.accept(taintAnalyser);
		Collection<String> taintedVars = ((TaintAnalyser) taintAnalyser).getTaintedVariables();
		((SinkAnalyser) sinkAnalyser).setTaintedVariables(taintedVars);
		
		target.accept(sinkAnalyser);
		Collection<FeedbackInstance> feedbackList = ((SinkAnalyser) sinkAnalyser).getFeedback();
		for(FeedbackInstance feedback : feedbackList) {
			try {
				feedbackReporter.sendFeedback(feedback);
			} catch (Exception e) {
				Activator.getLogger().log(Level.WARNING, e.getMessage());
			}
		}
		
	}

}
