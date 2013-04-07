package com.espsecurityplugin.interfaces;


public interface FeedbackInstance {
	
	public void setStartPosition(int position);
	public int getStartPosition();

	public void setOffset(int position);
	public int getEndPosition();
	
	public void setMessage(String message);
	public String getMessage();
}
