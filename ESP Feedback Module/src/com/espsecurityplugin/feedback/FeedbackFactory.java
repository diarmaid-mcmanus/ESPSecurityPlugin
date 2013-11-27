package com.espsecurityplugin.feedback;

import com.espsecurityplugin.feedback.internal.EclipseMarkerFeedback;

public class FeedbackFactory {

	public static FeedbackInstance getFeedbackInstance() {
		return new EclipseMarkerFeedback();
	}
}
