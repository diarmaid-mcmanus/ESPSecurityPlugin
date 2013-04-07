package com.espsecurityplugin.feedback;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;

import com.espsecurityplugin.activator.Activator;
import com.espsecurityplugin.interfaces.ContextModel;

/**
 * I need the {@link IDocument} to add an {@link IDocumentListener} so that my 
 * Reconciler will work, and the {@link IResource} is needed for adding
 * {@link IMarker}s.
 * 
 * This class is a singleton (for now at least) as the IDocument/IResource is
 * (should?) only set in one location; but read from several
 * 
 * @author Diarmaid McManus
 *
 */
public class ConcreteContextModel implements ContextModel {

	private Logger LOGGER = Activator.getLogger();
	
	private IDocument document;
	private ICompilationUnit iCompilationUnit;
	private static ConcreteContextModel instance;
	
	public static ConcreteContextModel getContextModel() {
		if(instance == null) {
			instance = new ConcreteContextModel();
		}
		return instance;
	}
	
	@Override
	public void setCompilationUnit(ICompilationUnit iCompilationUnit) {
		this.iCompilationUnit = iCompilationUnit;
	}
	
	@Override
	public ICompilationUnit getCompilationUnit() {
		return this.iCompilationUnit;
	}
	
	@Override
	public void setDocument(IDocument document) {
		this.document = document;
	}

	@Override
	public IResource getResource() {
		/*
		 * This used to have to store the IResource, but now that we use the
		 * ICompilationUnit, we can just run .getUnderlyingResource() which
		 * gives us the IResource we need for placing IMarkers.
		 */
		try {
			return this.getCompilationUnit().getUnderlyingResource();
		} catch (JavaModelException e) {
			LOGGER.log(Level.WARNING, "JavaModelException, returning null: " + e.getMessage());
			return null;
		}
	}

	@Override
	public IDocument getDocument() {
		return document;
	}
}
