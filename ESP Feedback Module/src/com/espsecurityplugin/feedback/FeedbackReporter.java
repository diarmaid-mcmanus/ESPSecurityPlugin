package com.espsecurityplugin.feedback;


import org.eclipse.core.runtime.CoreException;

/**
 * Used to collect feedback from an CodeAnalyser and somehow send it to the
 * user, through any implementing class.
 * 
 * @author diarmaidmcmanus
 *
 */
public interface FeedbackReporter {

	/**
	 * report a feedbackinstance to the feedbackreporter.
	 * This will be either a view, a marker, or something else someone else
	 * implemented(XMLFeedbackReporter?)
	 * 
	 * @param feedbackInstance
	 * @throws CoreException 
	 */
	public void sendFeedback(FeedbackInstance feedbackInstance) throws Exception;
}
