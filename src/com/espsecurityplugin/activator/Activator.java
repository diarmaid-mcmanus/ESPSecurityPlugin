package com.espsecurityplugin.activator;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;

import com.espsecurityplugin.reconciler.StaticAnalysisPartListener;


/**
 * The activator class controls the plug-in life cycle
 * 
 * TODO currently exporting the Activator package. Refactor so I don't have to do this
 */
public class Activator extends AbstractUIPlugin implements IStartup {

	public static final String PLUGIN_ID = "ESPSecurityPlugin"; //$NON-NLS-1$
	
	private static final Logger LOGGER = Logger.getLogger(PLUGIN_ID);

	private static Activator plugin;
	
	public void start(BundleContext context) {
		try {
			super.start(context);
		} catch (Exception e) {
			Activator.getLogger().log(Level.WARNING, e.getMessage());
		}
		plugin = this;
		
		// set up local classes and other stuff I need
		initalizeLocalClasses();
		
		/*
		 *  Create a UIJob for initializing the plugin
		 *  At a high level:
		 *  	* Adds something to track what file's open (StaticAnalysisPartListener)
		 *  	* Keeps track of when to execute static analysis (ReconcilerTextListener)
		 *  	* Generates AST & passes to visitor, collects results & sends to Feedback Reporter (TaintedSinkMatcher) (I need better names...)
		 */
		final UIJob job = new UIJob("ESP: Security Plugin Init Job") {
			
			@Override
			public IStatus runInUIThread(IProgressMonitor arg0) {
				try {
					IWorkbench workbench = PlatformUI.getWorkbench();
					IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
					
					IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
					IEditorPart editorPart = workbenchPage.getActiveEditor();
					
					IPartService partService = workbenchWindow.getPartService();
					StaticAnalysisPartListener staticAnalysisPartListener = new StaticAnalysisPartListener(editorPart);
					partService.addPartListener(staticAnalysisPartListener);
				} catch (NullPointerException e) {
					Activator.getLogger().log(Level.INFO, "Could not obtain a partListener, rescheduling.");
					this.schedule(2000);
					return Status.CANCEL_STATUS;
				} catch (Exception e) {
					Activator.getLogger().log(Level.WARNING, "Unknown error: " + e.toString());
				}
				
				return Status.OK_STATUS;
			}
		}; 
		
		// Run the job
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}
	
	public static Logger getLogger() {
		return LOGGER;
	}

	@Override
	public void earlyStartup() {
		// required because we use the startup extension point. Nothing to do
		// here; we handle everything in start()
	}
	
	/*
	 * Plugin defaults here
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault("reconciliation.delay", 500);
	}
	
	private void initalizeLocalClasses() {
		
	}

}
