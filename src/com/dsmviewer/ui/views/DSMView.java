package com.dsmviewer.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavtsev</a>
 * 
 */
public class DSMView extends ViewPart {

    private static final String DSM_VIEW_ID = "DSM View";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private TableViewer tableViewer;

    private Table table;

    private Action action1;

    private final DSMViewController dsmViewController = new DSMViewController(table);

    public DSMViewController getDsmViewController() {
        return dsmViewController;
    }

    public Table getTable() {
        return table;
    }

    public void createPartControl(final Composite composite) {
        try {
            addLifeCycleListener();

            createTable(composite);
            createTableViewer();

            // Create the help context id for the viewer's control
            PlatformUI.getWorkbench().getHelpSystem().setHelp(tableViewer.getControl(), "DSM-viewer.viewer");

            makeActions();
            hookContextMenu();
            contributeToActionBars();
        } catch (RuntimeException e) {
            logger.error("Exception occured: " + e.getMessage());
            showErrorMessage("Exception occured: " + e.getMessage());            
        }
    }

    private void createTable(final Composite parent) {
        int style = SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
        table = new Table(parent, style);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        table.setLayoutData(gridData);

        table.setLinesVisible(true);
        table.setHeaderVisible(true);
    }

    private void createTableViewer() {
        tableViewer = new TableViewer(table);
        tableViewer.setUseHashlookup(true);
        tableViewer.setContentProvider(new DSMViewContentProvider());
        tableViewer.setLabelProvider(new DSMViewLabelProvider());
        tableViewer.setInput(getViewSite());        
    }

    /**
     * Adds necessary listeners to DSM View.
     */
    private void addLifeCycleListener() {
        getViewSite().getPage().addPartListener(new ViewLyfeCycleListener());
        logger.debug("View lifecycle listener was added to DSM View");
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                DSMView.this.fillContextMenu(manager);
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
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(action1);
    }

    private void makeActions() {
        action1 = new Action() {
            public void run() {
                // action code here..
            }
        };
        action1.setText("Action 1");
        action1.setToolTipText("Action 1 tooltip");
        action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT));

    }

    /**
     * Shows the info message.
     * 
     * @param message
     *            the message
     */
    public static void showInfoMessage(String message) {
        MessageDialog.openInformation(
                Display.getDefault().getActiveShell(),
                DSM_VIEW_ID,
                message);
    }

    /**
     * Shows the error message. 
     * @param message
     *            the message
     */
    public static void showErrorMessage(String message) {
        MessageDialog.openError(
                Display.getDefault().getActiveShell(),
                DSM_VIEW_ID,
                message);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        tableViewer.getControl().setFocus();
        logger.info("View lifecycle: DSM view is in focus.");
    }

}