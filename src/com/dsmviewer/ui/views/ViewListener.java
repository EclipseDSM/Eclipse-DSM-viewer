package com.dsmviewer.ui.views;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dsmviewer.Activator;

/**
 * The listener for DSM View events such as: close/open view, activate/deactivate view, etc. *
 * 
 * @see IPartListener
 */
class ViewListener implements IPartListener {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
    public void partActivated(final IWorkbenchPart part) {
        //logger.debug("View lifecycle: DSM view was activated.");
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
    public void partBroughtToTop(final IWorkbenchPart part) {
        logger.debug("View lifecycle: DSM view was brought to top.");
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
    public void partClosed(final IWorkbenchPart part) {

        String dsmViewID = Activator.getInstance().getPluginId();
        String closedViewID = part.getSite().getPluginId();

        if (dsmViewID.equals(closedViewID)) {

            if (part instanceof DSMView) {
                logger.debug("View lifecycle: DSM view was closed.");
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
    public void partDeactivated(final IWorkbenchPart part) {
        //logger.debug("View lifecycle: DSM view was deactivated.");
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
    public void partOpened(final IWorkbenchPart part) {

        String dsmViewID = Activator.getInstance().getPluginId();
        String openedViewID = part.getSite().getPluginId();

        if (dsmViewID.equals(openedViewID)) {
            if (part instanceof DSMView) {
                logger.debug("View lifecycle: DSM view was opened.");
            }
        }
    }
}
