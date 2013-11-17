package com.espsecurityplugin.feedback;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.IDocument;

/**
 * Provides context for the Feedback Plugin
 * Contains IDocument, IResource
 * 
 * @author diarmaidmcmanus
 *
 */
public interface ContextModel {
	
	/**
	 * Sets the IDocument for adding the DocumentListener
	 * @param document
	 */
	public void setDocument(IDocument document);
	
	/**
	 * Sets the {@link ICompilationUnit} for the parser to generate an AST
	 * @param iCompilationUnit
	 */
	public void setCompilationUnit(ICompilationUnit iCompilationUnit);
	
	public IResource getResource();
	public IDocument getDocument();
	public ICompilationUnit getCompilationUnit();

}
