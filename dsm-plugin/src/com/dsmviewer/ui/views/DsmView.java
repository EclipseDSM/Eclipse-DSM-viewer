package com.dsmviewer.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.dsmviewer.Activator;
import com.dsmviewer.logging.Logger;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavtsev</a>
 * 
 */
public class DsmView extends ViewPart {

    private final Logger logger = Activator.getLogger(DsmView.class);

    private static DsmTableViewer tableViewer;

    private Table table;
    private DsmViewController dsmViewController = new DsmViewController(table);

    private Action action1;

    private ViewLyfeCycleListener lifeCycleListener;

    public DsmViewController getDsmViewController() {
        return dsmViewController;
    }

    public Table getTable() {
        return table;
    }

    public static DsmTableViewer getTableViewer() {
        return tableViewer;
    }

    @Override
    public void createPartControl(final Composite parent) {
        try {

            Composite childComposite = new Composite(parent, SWT.DOUBLE_BUFFERED);

            tableViewer = new DsmTableViewer(childComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
                    | SWT.FULL_SELECTION | SWT.BORDER);

            tableViewer.setUseHashlookup(true);
//          tableViewer.setContentProvider(new DsmViewContentProvider());

            // Selection provider for the view.
            getSite().setSelectionProvider(tableViewer);

            table = tableViewer.getTable();
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            table.setToolTipText("DS-Matrix");

            // Create the help context id for the viewer's control
            PlatformUI.getWorkbench().getHelpSystem().setHelp(tableViewer.getControl(), "DSM-viewer.viewer");

            makeActions();
            hookContextMenu();
            contributeToActionBars();

            addLifeCycleListener();
        } catch (Exception e) {
            String errorMessage = "Cannot create control part";
            logger.error(errorMessage, e);
            Activator.showErrorMessage(errorMessage + ": " + e.getMessage());
        }
    }

    public void clearDsmTable() {
        table.removeAll();
        logger.debug("DSM table was cleared.");
    }

    /**
     * Adds necessary listeners to DSM View.
     */
    private void addLifeCycleListener() {
        lifeCycleListener = new ViewLyfeCycleListener();
        getViewSite().getPage().addPartListener(lifeCycleListener);
        logger.info("View lifecycle listener was added to DSM View");
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
        Menu menu = menuMgr.createContextMenu(tableViewer.getControl());
        tableViewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, tableViewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(action1);
        // manager.add(new Separator());
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(action1);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(action1);
    }

    private void makeActions() {
        action1 = new Action() {
            @Override
            public void run() {
            }
        };
        action1.setText("Action 1");
        action1.setToolTipText("Action 1 tooltip");
        action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT));

    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        tableViewer.getControl().setFocus();
        logger.debug("View lifecycle: DSM view got focus.");
    }

    @Override
    public void dispose() {
        // Remove lifecycle listener from view
        getViewSite().getPage().removePartListener(lifeCycleListener);
        logger.info("Lifecycle listener was removed from DSM view");
    }

}