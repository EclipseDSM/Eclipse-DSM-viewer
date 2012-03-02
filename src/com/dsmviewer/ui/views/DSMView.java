package com.dsmviewer.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.dtangler.core.configuration.Arguments;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dsmviewer.dtangler.DSMatrix;
import com.dsmviewer.dtangler.DtanglerArguments;
import com.dsmviewer.dtangler.DtanglerRunner;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavtsev</a>
 * 
 */
public class DSMView extends ViewPart {

    private static final String DSM_VIEW_ID = "DSM View";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static TableViewer tableViewer;

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
    
    public static TableViewer getTableViewer() {
        return tableViewer;
    }

    public void createPartControl(final Composite parent) {
        try {
            addLifeCycleListener();

            createTableViewer(parent);

            // Create the help context id for the viewer's control
            PlatformUI.getWorkbench().getHelpSystem().setHelp(tableViewer.getControl(), "DSM-viewer.viewer");

            makeActions();
            hookContextMenu();
            contributeToActionBars();
        } catch (RuntimeException e) {
            logger.error("Cannot create control part: " + e.getMessage());
            showErrorMessage("Cannot create control part: " + e.getMessage());
        }
    }

    private void createTableViewer(Composite parent) {               
        
        tableViewer = new DSMTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        
        table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setToolTipText("DS-Matrix");
        
        DSMatrix dsMatrix = runDtanglerManually();

        tableViewer.setUseHashlookup(true);

        composeColumns(dsMatrix);

        tableViewer.setContentProvider(new DSMViewContentProvider());
        tableViewer.setLabelProvider(new DSMViewLabelProvider());

        tableViewer.setInput(dsMatrix.getRows());

        // Selection provider for the view.
        getSite().setSelectionProvider(tableViewer);

    }

    private DSMatrix runDtanglerManually() {

        DtanglerRunner runner = new DtanglerRunner();
        List<String> pathList = new ArrayList<String>();

        String path = "//home//selden//Рабочий стол//dtangler-core_v_2.0.0.jar";
        pathList.add(path);

        String scope = "classes";

        Arguments arguments = DtanglerArguments.build(pathList, scope, false);

        return runner.run(arguments);
    }

    private void composeColumns(DSMatrix dsMatrix) {

        TableViewerColumn nameColumn = createTableViewerColumn("Names: ", 200, true);

//        nameColumn.setLabelProvider(new ColumnLabelProvider() {
//            @Override
//            public String getText(Object element) {
//                DsmRow dsmRow = (DsmRow) element;
//                return dsmRow.getDependee().getDisplayName();
//            }
//        });

        for (int i = 1; i <= dsMatrix.getSize(); i++) {

            
            
            // save the column number to set column headers later.
            dsMatrix.getRows().get(i-1).getDependee().setContentCount(i);

            TableViewerColumn column = createTableViewerColumn("" + i, 35, true);
//            column.setLabelProvider(new ColumnLabelProvider() {
//                @Override
//                public String getText(Object element) {
//                    DsmRow dsmRow = (DsmRow) element;
//                    return Integer.toString(dsmRow.getCells().get(0).getDependencyWeight());
//                }
//            });
        }

    }

    private TableViewerColumn createTableViewerColumn(String title, int bound, boolean isResizable) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(isResizable);
        column.setMoveable(false);
        return viewerColumn;
    }

    public void clearDSMTable() {
        table.removeAll();
        logger.debug("DSM table was cleared.");
    }

    /**
     * Adds necessary listeners to DSM View.
     */
    private void addLifeCycleListener() {
        lifeCycleListener = new ViewLyfeCycleListener();
        getViewSite().getPage().addPartListener(lifeCycleListener);
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
    public void setFocus() {
        tableViewer.getControl().setFocus();
        logger.info("View lifecycle: DSM view is in focus.");
    }

    public void dispose() {
        // Remove lifecycle listener from view
        getViewSite().getPage().removePartListener(lifeCycleListener);
        logger.debug("Lifecycle listener was removed from DSM view");
    }

}