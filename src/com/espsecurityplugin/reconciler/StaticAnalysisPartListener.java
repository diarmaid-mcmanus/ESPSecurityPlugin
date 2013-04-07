package com.espsecurityplugin.reconciler;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.texteditor.ITextEditor;
import org.xml.sax.SAXException;

/**
 * Attaches to the workspace and changes where the Reconciler is listening if
 * the partReference is a Java editor (CompilationUnitEditor).
 * 
 * @author Diarmaid McManus
 *
 */
// I shouldn't be accessing org.eclipse.jdt.internal, but I do, so suppress any
// warning messages.
@SuppressWarnings("restriction")
public class StaticAnalysisPartListener implements IPartListener2 {
	
	private ReconcilerTextListener reconcilerTextListener;
	
	/**
	 * Starts up a StaticAnalysisPartListener with no focus(ie, no editors 
	 * running)
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * 
	 */
	public StaticAnalysisPartListener() throws ParserConfigurationException, SAXException, IOException {
		this.reconcilerTextListener = new ReconcilerTextListener();
	}
	
	/**
	 * Starts up a StaticAnalysisPartListener with focus on an active editor
	 * 
	 * @param editorPart
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public StaticAnalysisPartListener(IEditorPart editorPart) throws ParserConfigurationException, SAXException, IOException {
		reconcilerTextListener = new ReconcilerTextListener();
		if(editorPart instanceof org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor) {
			ITextEditor textEditor = (ITextEditor) editorPart;
			openedHelper(textEditor);
		}
	}

	@Override
	public void partVisible(IWorkbenchPartReference arg0) {
		if(arg0.getPart(false) instanceof org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor) {
			ITextEditor textEditor = (ITextEditor) arg0.getPart(false);
			openedHelper(textEditor);
		}
	}
	
	@Override
	public void partOpened(IWorkbenchPartReference arg0) {
	}
	
	
	@Override
	public void partActivated(IWorkbenchPartReference arg0) {
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference arg0) {
	}

	@Override
	public void partClosed(IWorkbenchPartReference arg0) {
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference arg0) {
	}

	@Override
	public void partHidden(IWorkbenchPartReference arg0) {
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference arg0) {
		if(arg0.getPart(false) instanceof org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor) {
			ITextEditor textEditor = (ITextEditor) arg0.getPart(false);
			openedHelper(textEditor);
		}
	}

	/**
	 * Handles unhooking and rehooking of the IDocumentListener.
	 * 
	 * @param textEditor the ITextEditor we'll get our IDocumentProvider from
	 */
	private void openedHelper(ITextEditor textEditor) {
		reconcilerTextListener.hook(textEditor);
	}
	
}
