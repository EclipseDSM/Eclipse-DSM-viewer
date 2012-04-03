package com.dsmviewer.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
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
    public DSMView() {
    }    

    private static final String DSM_VIEW_ID = "DSM View";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static DSMTableViewer tableViewer;

    private static DSMTreeViewer treeViewer;
    
    private Table table;

    private Action action1;

    private ViewLyfeCycleListener lifeCycleListener;    

    public void createPartControl(final Composite parent) {
        try {
            
            this.addLifeCycleListener();
            parent.setLayout(new FillLayout(SWT.HORIZONTAL));

            // not used. I`m use separate tree/table scrollbars instead.
//            ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.BORDER);
//            scrolledComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

            SashForm sashForm = new SashForm(parent, SWT.NONE);

            Composite treeComposite = new Composite(sashForm, SWT.BORDER);
            Composite tableComposite = new Composite(sashForm, SWT.BORDER);
            
            treeComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
            treeViewer = new DSMTreeViewer(treeComposite);

            tableComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
            tableViewer = new DSMTableViewer(tableComposite);  
            
            getSite().setSelectionProvider(tableViewer);

            sashForm.setWeights(new int[] {1, 3});

            // Create the help context id for the viewer's control
            PlatformUI.getWorkbench().getHelpSystem().setHelp(tableViewer.getControl(), "DSM-viewer.viewer");
 

            makeActions();
            hookContextMenu();
            contributeToActionBars();

        } catch (RuntimeException e) {
            logger.error("Cannot create control part: " + e.getMessage());
            showErrorMessage("Cannot create control part: " + e.getMessage());
        }
        
        finally {
            setupListeners();
        }
    }

    private void setupListeners() {

        // scrollbars mirroring
        final ScrollBar tableVerticalScroll = tableViewer.getTableVerticalBar();
        final ScrollBar treeVerticalScroll = treeViewer.getTreeVerticalBar();

        SelectionListener listener1 = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                tableVerticalScroll.setSelection(treeVerticalScroll.getSelection());
            }
        };
        
        SelectionListener listener2 = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                treeVerticalScroll.setSelection(tableVerticalScroll.getSelection());
            }
        };
        
        treeVerticalScroll.addSelectionListener(listener1);
        tableVerticalScroll.addSelectionListener(listener2);
        //:~


        // selection mirroring
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent arg0) {
                int index = tableViewer.getSelectionIndex();
                int treeViewerSelection = treeViewer.getSelectionIndex();
                if (treeViewerSelection != index) {
                    treeViewer.setSelectionIndex(index);
                }
            }
        });
        
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent arg0) {
                int index = treeViewer.getSelectionIndex();
                int tableViewerSelection = tableViewer.getSelectionIndex();
                if (tableViewerSelection != index) {
                    tableViewer.setSelectionIndex(index);
                }
            }
        });
        //:~

        // cell selection enabling. Table editing avoided.
        TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(tableViewer,new DSMTableViewerCellFocusHighlighter(tableViewer));
        ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(tableViewer) {
            protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
                return false;
            }
        };
        
        TableViewerEditor.create(tableViewer, focusCellManager, actSupport, ColumnViewerEditor.DEFAULT);
        //:~
    }
    
    public static void clearDSMView() {
        tableViewer.getTable().removeAll();
        treeViewer.getTree().removeAll();
    }

    /**
     * Adds a lyfecycle listener to DSM View.
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
            public void menuAboutToShow(final IMenuManager manager) {
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

    private void fillLocalPullDown(final IMenuManager manager) {
        manager.add(action1);
        // manager.add(new Separator());
    }

    private void fillContextMenu(final IMenuManager manager) {
        manager.add(action1);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(final IToolBarManager manager) {
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

    public static void showDSModel(final DSMModel dsmModel, String scope) {
        clearDSMView();
        tableViewer.setDSMatrix(dsmModel);
        treeViewer.setLabels(dsmModel.getLabels(), scope);
    }

    public static void showInfoMessage(final String message) {
        MessageDialog.openInformation(
                Display.getDefault().getActiveShell(),
                DSM_VIEW_ID,
                message);
    }

    public static void showErrorMessage(final String message) {
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

    @Override
    public void dispose() {
        // Remove lifecycle listener from view
        getViewSite().getPage().removePartListener(lifeCycleListener);
        logger.debug("Lifecycle listener was removed from DSM view");
    }

}