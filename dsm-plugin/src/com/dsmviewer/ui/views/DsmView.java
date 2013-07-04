package com.dsmviewer.ui.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.dsmviewer.Activator;
import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.dsm.DependencyMatrixOrdering;
import com.dsmviewer.logging.Logger;
import com.dsmviewer.ui.action.SortDependencyMatrixAction;
import com.dsmviewer.ui.dsmtable.DsmTableController;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavtsev</a>
 * 
 */
public class DsmView extends ViewPart {

    private final Logger logger = Activator.getLogger(DsmView.class);

    public static final String ID = "com.dsmviewer.ui.views.DsmView";

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
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        Action action1 = new Action() {
            // TODO: refactor and move to separate file!
            @Override
            public void run() {

                Shell shell = getSite().getShell();

                FileDialog dialog = new FileDialog(shell, SWT.SAVE);
                dialog.setFilterNames(new String[] { "Png Files", "All Files (*.*)" });
                dialog.setFilterExtensions(new String[] { "*.png", "*.*" });
                dialog.setFileName("screenshot.png");
                String fileNameAndPath = dialog.open();

                NatTable table = dsmTableController.getTable();
                Point tableSize = dsmTableController.getDsmTableBounds();

                setState(IWorkbenchPage.STATE_MAXIMIZED);

                GC gc = new GC(table);
                Display display = shell.getDisplay();
                final Image image = new Image(display, tableSize.x, tableSize.y);
                gc.copyArea(image, 0, 0);
                gc.dispose();

                setState(IWorkbenchPage.STATE_RESTORED);

                ImageLoader loader = new ImageLoader();
                loader.data = new ImageData[] { image.getImageData() };
                try {
                    loader.save(new FileOutputStream(new File(fileNameAndPath)), SWT.IMAGE_PNG);
                } catch (FileNotFoundException e) {
                    logger.error("Error while saving screenshot to file " + fileNameAndPath, e);
                }
            }
        };
        action1.setText("Take screenshot of visible table part");

        manager.add(action1);

    }

    private static void fillLocalToolBar(IToolBarManager manager) {
        final Action sortByInstabilityAction = new SortDependencyMatrixAction(dsmTableController,
                DependencyMatrixOrdering.BY_INSTABILITY);
        sortByInstabilityAction.setToolTipText("Sort matrix by instability");
        sortByInstabilityAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

        final Action sortInNaturalOrderingAction = new SortDependencyMatrixAction(dsmTableController,
                DependencyMatrixOrdering.NATURAL_ORDERING);
        sortInNaturalOrderingAction.setToolTipText("Sort matrix in natural ordering");
        sortInNaturalOrderingAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

        // first state
        sortByInstabilityAction.setChecked(false);
        sortInNaturalOrderingAction.setChecked(true);

        manager.add(sortByInstabilityAction);
        manager.add(sortInNaturalOrderingAction);
//        manager.add(new Separator());

        sortByInstabilityAction.addPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if ("checked".equals(event.getProperty())) {
                    sortInNaturalOrderingAction.setChecked((Boolean) event.getOldValue());
                }
            }
        });

        sortInNaturalOrderingAction.addPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if ("checked".equals(event.getProperty())) {
                    sortByInstabilityAction.setChecked((Boolean) event.getOldValue());
                }
            }
        });
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

    /**
     * @param state one of the IWorkbenchPage STATE_* values: STATE_MAXIMIZED, STATE_MINIMIZED, STATE_RESTORED
     */
    public void setState(int state) {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        int currentState = page.getPartState(page.getReference(this));
        if (currentState != state) {
            page.activate(this);
            page.setPartState(page.getReference(this), state);
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
        dsmTableController.setDependencyMatrix(dsMatrix, true);
    }

}