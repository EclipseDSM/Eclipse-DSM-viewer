package com.dsmviewer.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dsmviewer.exception.PluginUIException;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavtsev</a>
 * 
 */
public class DSMView extends ViewPart {

    private static final String DSM_VIEW_ID = "DSM View";

    private static final String PATH_TO_ANALYZE_DEPENDENCIES =
            "/home/selden/log4j viewer Eclipse linux workspace/log4j-viewer/com.plugin.log4j.viewer/bin/com/log4jviewer";

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private TableViewer viewer;
    private Action runDtanglerManuallyAction;
    private Action doubleClickAction;

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     * 
     * @param parent
     *            the parent
     */
    public void createPartControl(Composite parent) {
        try {
            addLifeCycleListener();

            viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
            viewer.setContentProvider(new ViewContentProvider());
            viewer.setLabelProvider(new ViewLabelProvider());
            viewer.setSorter(new NameSorter());
            viewer.setInput(getViewSite());

            // Create the help context id for the viewer's control
            PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "DSM-viewer.viewer");
            makeActions();
            hookContextMenu();
            hookDoubleClickAction();
            contributeToActionBars();
        } catch (PluginUIException e) {
            logger.error("Plugin UI exception occured: " + e.getMessage());
            showErrorMessage("Plugin UI exception occured: " + e.getMessage());            
        }
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
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(runDtanglerManuallyAction);
        // manager.add(new Separator());
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(runDtanglerManuallyAction);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(runDtanglerManuallyAction);
    }

    private void makeActions() {
        runDtanglerManuallyAction = new Action() {            
            public void run() {
//                Display.getDefault().asyncExec(new Runnable() {
//                    @Override
//                    public void run() {
//                        runDtanglerManually();
//                    }
//                });
            }
        };
        runDtanglerManuallyAction.setText("Action 1");
        runDtanglerManuallyAction.setToolTipText("Action 1 tooltip");
        runDtanglerManuallyAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT));

        doubleClickAction = new Action() {
            public void run() {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection) selection).getFirstElement();
                showInfoMessage("Double-click detected on " + obj.toString());
            }
        };
    }

    /*
    private void runDtanglerManually() {
        List<String> pathList = new ArrayList<String>();

//        pathList.add("/home/selden/DSM-viever Eclipse workspace/Eclipse-DSM-viewer/bin/com/dsmviewer");
//        pathList.add("/home/selden/DSM-viever Eclipse workspace/Eclipse-DSM-viewer/bin/com/dsmviewer/dtangler");
//        pathList.add("/home/selden/DSM-viever Eclipse workspace/Eclipse-DSM-viewer/bin/com/dsmviewer/ui/preferences");
//        pathList.add("/home/selden/DSM-viever Eclipse workspace/Eclipse-DSM-viewer/bin/com/dsmviewer/ui/views");

        DtanglerRunner dtanglerRunner = new DtanglerRunner();

        try {
            Arguments arguments = DtanglerArguments.build(pathList, "classes", false);
            dtanglerRunner.run(arguments);
        } catch (MissingArgumentsException e) {
            e.printStackTrace(); // wrong arguments
            showErrorMessage(e.getMessage());
        } catch (DtException e) {
            e.printStackTrace(); // wrong DTangler operation
            showErrorMessage("DTangler cannot process your request.");
        }
    }*/

    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
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
        viewer.getControl().setFocus();
        logger.info("View lifecycle: DSM view is in focus.");
    }

    class NameSorter extends ViewerSorter {
    }

    class ViewContentProvider implements IStructuredContentProvider {

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, 
         * java.lang.Object, java.lang.Object)
         */
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements(Object parent) {
            return new String[] { "One", "Two", "Three" };
        }
    }

    /**
     * The Class ViewLabelProvider.
     */
    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText(Object obj, int index) {
            return getText(obj);
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage(Object obj, int index) {
            return getImage(obj);
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
         */
        public Image getImage(Object obj) {
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEF_VIEW);
        }
    }

}