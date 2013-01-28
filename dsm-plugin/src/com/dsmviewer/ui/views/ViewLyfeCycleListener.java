package com.dsmviewer.ui.views;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

import com.dsmviewer.Activator;

/**
 * The listener for DSM View events such as: close/open view, activate/deactivate view, etc. *
 * 
 * @see IPartListener
 */
class ViewLyfeCycleListener implements IPartListener {

	private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void partActivated(final IWorkbenchPart part) {
        //logger.debug("View lifecycle: DSM view was activated.");
    }

    @Override
    public void partBroughtToTop(final IWorkbenchPart part) {
		logger.log(Level.INFO, "View lifecycle: DSM view was brought to top.");
    }

    @Override
    public void partClosed(final IWorkbenchPart part) {

        String dsmViewID = Activator.getInstance().getPluginId();
        String closedViewID = part.getSite().getPluginId();

        if (dsmViewID.equals(closedViewID)) {
            if (part instanceof DSMView) {
				logger.log(Level.INFO, "View lifecycle: DSM view was closed.");
            }
        }
    }

    @Override
    public void partDeactivated(final IWorkbenchPart part) {
        //logger.debug("View lifecycle: DSM view was deactivated.");
    }

    @Override
    public void partOpened(final IWorkbenchPart part) {

        String dsmViewID = Activator.getInstance().getPluginId();
        String openedViewID = part.getSite().getPluginId();

        if (dsmViewID.equals(openedViewID)) {
            if (part instanceof DSMView) {
				logger.log(Level.INFO, "View lifecycle: DSM view was opened.");
            }
        }
    }
}
