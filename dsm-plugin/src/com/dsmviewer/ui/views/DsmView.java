package com.dsmviewer.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.dsmviewer.Activator;
import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.dsmtable.DsmTableController;
import com.dsmviewer.logging.Logger;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavtsev</a>
 * 
 */
public class DsmView extends ViewPart {

    private final Logger logger = Activator.getLogger(DsmView.class);

    public static final String ID = "com.dsmviewer.ui.views.DsmView";

    private Action sampleAction;

    private ViewLyfeCycleListener lifeCycleListener;

    private static DsmTableController dsmTableController;

    @Override
    public void createPartControl(final Composite parent) {

        dsmTableController = new DsmTableController(parent);

        try {

            dsmTableController.init(null);

            lifeCycleListener = new ViewLyfeCycleListener();
            getViewSite().getPage().addPartListener(lifeCycleListener);
            logger.info("View lifecycle listener was added to DSM View");

            makeActions();
            hookContextMenu();
            contributeToActionBars();

        } catch (Exception e) {
            logger.error("Cannot create control part of " + Activator.PLUGIN_ID, e);
        }
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                DsmView.this.fillContextMenu(manager);
            }
        });
//        Menu menu = menuMgr.createContextMenu(tableViewer.getControl());
//        getSite().registerContextMenu(menuMgr, tableViewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(sampleAction);
        // manager.add(new Separator());
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(sampleAction);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(sampleAction);
    }

    private void makeActions() {
        sampleAction = new Action() {
            @Override
            public void run() {
            }
        };
        sampleAction.setText("Action 1");
        sampleAction.setToolTipText("Action 1 tooltip");
        sampleAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT));
    }

    /**
     * Passing the focus request to the view's parent control.
     */
    @Override
    public void setFocus() {
        if (dsmTableController != null) {
            dsmTableController.setTableFocused();
        }
        if (lifeCycleListener != null) {
            lifeCycleListener.logStatus("DSM view got focus.");
        }
    }

    @Override
    public void dispose() {
        dsmTableController = null;
        // Remove lifecycle listener from view
        logger.info("Removing lifecycle listener from DSM view");
        getViewSite().getPage().removePartListener(lifeCycleListener);
    }

    public static void showDsMatrix(DependencyMatrix dsMatrix) {
        dsmTableController.setDsMatrix(dsMatrix, true);
    }

}