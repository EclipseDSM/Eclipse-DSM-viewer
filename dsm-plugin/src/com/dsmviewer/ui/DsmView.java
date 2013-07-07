package com.dsmviewer.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.dsmviewer.Activator;
import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.dsm.DependencyMatrixOrdering;
import com.dsmviewer.logging.Logger;
import com.dsmviewer.ui.action.ExportToImageAction;
import com.dsmviewer.ui.action.SortDependencyMatrixByInstabilityAction;
import com.dsmviewer.ui.action.SortDependencyMatrixInNaturalOrderingAction;
import com.dsmviewer.ui.action.StepBackwardAction;
import com.dsmviewer.ui.action.StepForwardAction;
import com.dsmviewer.ui.dsmtable.DsmTableController;
import com.dsmviewer.utils.EclipseUtils;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavtsev</a>
 * 
 */
public class DsmView extends ViewPart {

    private final Logger logger = Activator.getLogger(DsmView.class);

    public static final String ID = "com.dsmviewer.ui.DsmView";

    private DsmViewLyfeCycleListener lifeCycleListener;

    private DsmTableController dsmTableController;

    private Action sortInNaturalOrderingAction;
    private Action sortByInstabilityAction;

    private static DsmView currentInstance;

    private ExportToImageAction takeScreenshotAction;

    private StepBackwardAction stepBackWardAction;
    private StepForwardAction stepForwardAction;

    private IPropertyChangeListener sortByInstailityActionPropertyChangeListener = new IPropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if ("checked".equals(event.getProperty()) && event.getNewValue() == Boolean.TRUE) {
                sortInNaturalOrderingAction.setChecked(false);
            }
        }
    };

    private IPropertyChangeListener sortInNaturalOrderingPropertyChangeListener = new IPropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if ("checked".equals(event.getProperty()) && event.getNewValue() == Boolean.TRUE) {
                sortByInstabilityAction.setChecked(false);
            }
        }
    };

    public static DsmView getCurrent() {
        return currentInstance;
    }

    @Override
    public void createPartControl(final Composite parent) {

        currentInstance = this;

        dsmTableController = new DsmTableController(parent);

        try {

            lifeCycleListener = new DsmViewLyfeCycleListener();
            getViewSite().getPage().addPartListener(lifeCycleListener);
            logger.info("View lifecycle listener was added to DSM View");

//            hookContextMenu();
            contributeToActionBars();

        } catch (Exception e) {
            logger.error("Cannot create control part of " + Activator.PLUGIN_ID, e);
        }
    }

//    private void hookContextMenu() {
//        MenuManager menuMgr = new MenuManager("#PopupMenu");
//        menuMgr.setRemoveAllWhenShown(true);
//        menuMgr.addMenuListener(new IMenuListener() {
//            @Override
//            public void menuAboutToShow(IMenuManager manager) {
//                fillContextMenu(manager);
//            }
//        });
//        Menu menu = menuMgr.createContextMenu(dsmTableController.getTable().getChildren()[0]);
//        getSite().registerContextMenu(menuMgr, );
//    }
//
//    private static void fillContextMenu(IMenuManager manager) {
//        Action action3 = new Action() {
//        };
//        action3.setText("Test action3");
//        manager.add(action3);
//        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//        Action action4 = new Action() {
//        };
//        action4.setText("Test action4");
//        manager.add(action4);
//    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
//        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

//    private void fillLocalPullDown(IMenuManager manager) {
//
//    }

    private void fillLocalToolBar(IToolBarManager manager) {

        stepBackWardAction = new StepBackwardAction();
        stepForwardAction = new StepForwardAction();

        sortInNaturalOrderingAction = new SortDependencyMatrixInNaturalOrderingAction(dsmTableController);
        sortByInstabilityAction = new SortDependencyMatrixByInstabilityAction(dsmTableController);

        takeScreenshotAction = new ExportToImageAction(dsmTableController);

        addPropertyChangeListeners();

        manager.add(stepBackWardAction);
        manager.add(stepForwardAction);
        manager.add(new Separator());
        manager.add(sortInNaturalOrderingAction);
        manager.add(sortByInstabilityAction);
        manager.add(new Separator());
        manager.add(takeScreenshotAction);

        setActionsEnabled(false);
    }

    public void showDsMatrix(DependencyMatrix dsMatrix, boolean revealDsmView, boolean refreshAll, boolean addToHistory) {

        if (revealDsmView) {
            EclipseUtils.showDsmView();
        }

        dsmTableController.setDependencyMatrix(dsMatrix, refreshAll, addToHistory);
        setActionsEnabled(true);
        updateSortActionsState(dsMatrix.getOrdering());
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

        currentInstance = null;
    }

    public void updateSortActionsState(DependencyMatrixOrdering currentDsmOrdering) {
        switch (currentDsmOrdering) {
        case BY_INSTABILITY:
            sortByInstabilityAction.setChecked(true);
            sortInNaturalOrderingAction.setChecked(false);
            break;
        case NATURAL_ORDERING:
            sortByInstabilityAction.setChecked(false);
            sortInNaturalOrderingAction.setChecked(true);
            break;
        default:
            throw new IllegalArgumentException("Ordering " + currentDsmOrdering + " is not supported");
        }
    }

    public void setActionsEnabled(boolean enabled) {
        stepBackWardAction.setEnabled(enabled);
        stepForwardAction.setEnabled(false);
        sortInNaturalOrderingAction.setEnabled(enabled);
        sortByInstabilityAction.setEnabled(enabled);
        takeScreenshotAction.setEnabled(enabled);
    }

    private void addPropertyChangeListeners() {
        sortByInstabilityAction.addPropertyChangeListener(sortByInstailityActionPropertyChangeListener);
        sortInNaturalOrderingAction.addPropertyChangeListener(sortInNaturalOrderingPropertyChangeListener);
    }

    public void minimize() {
        setState(IWorkbenchPage.STATE_MINIMIZED);
    }

    public void maximize() {
        setState(IWorkbenchPage.STATE_MAXIMIZED);
    }

    public void restore() {
        setState(IWorkbenchPage.STATE_RESTORED);
    }

    private void setState(int state) {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        int currentState = page.getPartState(page.getReference(this));
        if (currentState != state) {
            page.activate(this);
            page.setPartState(page.getReference(this), state);
        }
    }

    public DsmTableController getDsmTableController() {
        return dsmTableController;
    }

}