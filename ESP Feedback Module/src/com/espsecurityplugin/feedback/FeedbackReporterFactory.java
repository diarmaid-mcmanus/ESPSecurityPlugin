package com.espsecurityplugin.feedback;

import com.espsecurityplugin.feedback.internal.EclipseMarkerFeedbackReporter;

public class FeedbackReporterFactory {

	public static FeedbackReporter getFeedbackReporter() {
		return new EclipseMarkerFeedbackReporter();
	}
}
