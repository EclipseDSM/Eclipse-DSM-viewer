package com.dsmviewer.ui.views;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

import com.dsmviewer.Activator;
import com.dsmviewer.logging.Logger;

/**
 * The listener for DSM View events such as: close/open view, activate/deactivate view, etc.
 * 
 * @author Daniil Yaroslavtsev
 */
public class ViewLyfeCycleListener implements IPartListener {

    private final Logger logger = Activator.getLogger(ViewLyfeCycleListener.class);

	@Override
	public void partActivated(final IWorkbenchPart part) {
//	    logStatus("View lifecycle: DSM view was activated.");
	}

	@Override
	public void partBroughtToTop(final IWorkbenchPart part) {
//		logStatus("View lifecycle: DSM view was brought to top.");
	}

	@Override
	public void partClosed(final IWorkbenchPart part) {
		String dsmViewId = Activator.getInstance().getPluginId();
		String closedViewId = part.getSite().getPluginId();

		if (dsmViewId.equals(closedViewId)) {
			if (part instanceof DsmView) {
                logStatus("DSM view was closed.");
			}
		}
	}

	@Override
	public void partDeactivated(final IWorkbenchPart part) {
//		logStatus("DSM view was deactivated.");
	}

	@Override
	public void partOpened(final IWorkbenchPart part) {
		String dsmViewId = Activator.getInstance().getPluginId();
		String openedViewId = part.getSite().getPluginId();

		if (dsmViewId.equals(openedViewId)) {
			if (part instanceof DsmView) {
                logStatus("DSM View was opened.");
			}
		}
	}

    public void logStatus(String status) {
        logger.debug(status);
    }
}
