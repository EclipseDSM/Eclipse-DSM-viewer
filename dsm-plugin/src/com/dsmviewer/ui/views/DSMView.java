package com.dsmviewer.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.dsmviewer.logging.Logger;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavtsev</a>
 * 
 */
public class DSMView extends ViewPart {

    private static final String DSM_VIEW_ID = "DSM View";

	private final Logger logger = Logger.getLogger(DSMView.class);

    private static DSMTableViewer tableViewer;

    private Table table;

    private Action action1;

    private final DSMViewController dsmViewController = new DSMViewController(table);

    private ViewLyfeCycleListener lifeCycleListener;

    public DSMViewController getDsmViewController() {
        return dsmViewController;
    }

    public Table getTable() {
        return table;
    }
    
    public static DSMTableViewer getTableViewer() {
        return tableViewer;
    }

	@Override
	public void createPartControl(final Composite parent) {
        try {            
            
            Composite childComposite = new Composite(parent, SWT.DOUBLE_BUFFERED);
            
            addLifeCycleListener();

            createTableViewer(childComposite);

            createTable();

            // Create the help context id for the viewer's control
            PlatformUI.getWorkbench().getHelpSystem().setHelp(tableViewer.getControl(), "DSM-viewer.viewer");

            makeActions();
            hookContextMenu();
            contributeToActionBars();
        } catch (RuntimeException e) {
			logger.error("Cannot create control part: ", e);
            showErrorMessage("Cannot create control part: " + e.getMessage());
        }
    }

    private void createTableViewer(Composite parent) {

		tableViewer = new DSMTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
				| SWT.BORDER);

        tableViewer.setUseHashlookup(true);
        tableViewer.setContentProvider(new DSMViewContentProvider());
        
        // Selection provider for the view.
        getSite().setSelectionProvider(tableViewer);
    }

    private void createTable() {
        table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setToolTipText("DS-Matrix");
    }


    public void clearDSMTable() {
        table.removeAll();
		logger.info("DSM table was cleared.");
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
			@Override
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
     * 
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
	@Override
	public void setFocus() {
        tableViewer.getControl().setFocus();
        logger.info("View lifecycle: DSM view is in focus.");
    }

	@Override
	public void dispose() {
        // Remove lifecycle listener from view
        getViewSite().getPage().removePartListener(lifeCycleListener);
		logger.info("Lifecycle listener was removed from DSM view");
    }

}