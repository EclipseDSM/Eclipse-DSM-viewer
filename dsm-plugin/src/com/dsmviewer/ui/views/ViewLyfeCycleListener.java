package com.dsmviewer.ui.views;


import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

import com.dsmviewer.Activator;
import com.dsmviewer.logging.Logger;

/**
 * The listener for DSM View events such as: close/open view, activate/deactivate view, etc. *
 * 
 * @see IPartListener
 */
public class ViewLyfeCycleListener implements IPartListener {

	private final Logger logger = Logger.getLogger(ViewLyfeCycleListener.class);

    @Override
    public void partActivated(final IWorkbenchPart part) {
        //logger.debug("View lifecycle: DSM view was activated.");
    }

    @Override
    public void partBroughtToTop(final IWorkbenchPart part) {
		logger.info("View lifecycle: DSM view was brought to top.");
    }

    @Override
    public void partClosed(final IWorkbenchPart part) {

        String dsmViewID = Activator.getInstance().getPluginId();
        String closedViewID = part.getSite().getPluginId();

        if (dsmViewID.equals(closedViewID)) {
            if (part instanceof DSMView) {
				logger.info("View lifecycle: DSM view was closed.");
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
				logger.info("View lifecycle: DSM View was opened.");
            }
        }
    }
}
