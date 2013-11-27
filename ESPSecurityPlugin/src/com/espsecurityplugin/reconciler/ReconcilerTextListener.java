package com.espsecurityplugin.reconciler;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.xml.sax.SAXException;

import com.espsecurityplugin.activator.Activator;
import com.espsecurityplugin.engine.TaintedSinkMatcher;
import com.espsecurityplugin.feedback.ContextFactory;
import com.espsecurityplugin.feedback.ContextModel;


public class ReconcilerTextListener implements IDocumentListener {

	private Runnable violationDetector;
	private ScheduledThreadPoolExecutor pool;
	private ScheduledFuture<?> task;
	private long timeBeforeReconcile;
	private ContextModel contextModel = ContextFactory.getInstance();
	
	/*
	 * Change the pattern matcher below
	 */
	public ReconcilerTextListener() throws ParserConfigurationException, SAXException, IOException {
		violationDetector = new TaintedSinkMatcher();
		pool = new ScheduledThreadPoolExecutor(1);
	}
	
	public void hook(ITextEditor editor) {
		unhook();
		ICompilationUnit iCU = JavaUI.getWorkingCopyManager().getWorkingCopy(editor.getEditorInput());
		IDocumentProvider provider = editor.getDocumentProvider();
		contextModel.setDocument(provider.getDocument(editor.getEditorInput()));
		contextModel.setCompilationUnit(iCU);
		// Now we have the document, add self as a listener.
		contextModel.getDocument().addDocumentListener(this);
		scheduleTask();
	}
	
	/**
	 * remove any references to the previous document, or tasks which would be
	 * running on that document.
	 */
	public void unhook() {
		if(contextModel.getDocument() != null) {
			contextModel.getDocument().removeDocumentListener(this);
		}
		if(task != null) {
			task.cancel(false);
			pool.purge();
		}
	}
	
	@Override
	public void documentAboutToBeChanged(DocumentEvent arg0) {
		scheduleTask();
	}

	@Override
	public void documentChanged(DocumentEvent arg0) {
	}

	/**
	 * This either schedules the first task, or if there's a task already
	 * running, cancel and reschedule it.
	 */
	private void scheduleTask() {
		timeBeforeReconcile = Activator.getDefault().getPreferenceStore().
				getInt("reconciliation.delay");
		if(task != null) {
			task.cancel(false);
			pool.purge();
		}
		task = pool.schedule((Runnable)violationDetector, timeBeforeReconcile,
				TimeUnit.MILLISECONDS);
	}
}
