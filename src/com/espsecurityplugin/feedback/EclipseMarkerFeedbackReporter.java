package com.espsecurityplugin.feedback;


import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.MarkerUtilities;

import com.espsecurityplugin.activator.Activator;
import com.espsecurityplugin.interfaces.FeedbackInstance;
import com.espsecurityplugin.interfaces.FeedbackReporter;


public class EclipseMarkerFeedbackReporter implements FeedbackReporter {
	
	private final Logger LOG = Activator.getLogger();
	
	public EclipseMarkerFeedbackReporter() {
	}
	
	@Override
	public void sendFeedback(FeedbackInstance feedbackInstance) {
		// Get all markers on the resource in question
		// place the marker
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
		map.put(IMarker.MESSAGE, feedbackInstance.getMessage());
		map.put(IMarker.CHAR_START, feedbackInstance.getStartPosition());
		map.put(IMarker.CHAR_END, feedbackInstance.getEndPosition());
		
		try {
			MarkerUtilities.createMarker(ConcreteContextModel.getContextModel().getResource(), map, "ESPSecurityPlugin.secproblem");
		} catch (CoreException e) {
			LOG.log(Level.INFO, "Could not create marker: " + e.getMessage());
		}
	}

}
