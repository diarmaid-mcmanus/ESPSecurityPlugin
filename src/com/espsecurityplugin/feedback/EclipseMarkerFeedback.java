package com.espsecurityplugin.feedback;


import com.espsecurityplugin.interfaces.FeedbackInstance;

public class EclipseMarkerFeedback implements FeedbackInstance {
	
	private String message;
	private int startPosition;
	private int offset;
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public int getStartPosition() {
		return startPosition;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	@Override
	public String toString() {
		return this.message + " [" + this.startPosition + "->" + (this.startPosition+this.offset) +"]";
	}

	@Override
	public int getEndPosition() {
		return this.getStartPosition() + this.getOffset();
	}
	
}
