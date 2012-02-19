package com.dsmviewer.ui.views;

import org.dtangler.core.MissingArgumentsException;
import org.dtangler.core.analysis.configurableanalyzer.ConfigurableDependencyAnalyzer;
import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dependencyengine.DependencyEngine;
import org.dtangler.core.dependencyengine.DependencyEngineFactory;
import org.dtangler.core.dependencyengine.DependencyEnginePool;
import org.dtangler.core.dsmengine.DsmEngine;
import org.dtangler.core.exception.DtException;
import org.dtangler.core.input.ArgumentBuilder;
import org.dtangler.core.textui.DSMWriter;
import org.dtangler.core.textui.SysoutWriter;
import org.dtangler.core.textui.ViolationWriter;
import org.dtangler.core.textui.Writer;
import org.dtangler.javaengine.dependencyengine.JavaDependencyEngine;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//import com.dsmviewer.DependencyEngine;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavtsev</a>
 * 
 */
public class DSMView extends ViewPart {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * path to folder containing the items being analyzed | path and
     * name of the dependencies file.
     */
    String dtanglerInput = "/home/selden/log4j viewer Eclipse linux workspace/log4j-viewer/com.plugin.log4j.viewer";
    
    private TableViewer viewer;
    private Action action1;
    private Action action2;

    private Action doubleClickAction;

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    public void createPartControl(Composite parent) {
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
        manager.add(action1);
        manager.add(new Separator());
        manager.add(action2);
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(action1);
        manager.add(action2);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(action1);
        manager.add(action2);
    }

    private void makeActions() {
        action1 = new Action() {
            public void run() {
                showMessage("Action 1 executed");
            }
        };
        action1.setText("Action 1");
        action1.setToolTipText("Action 1 tooltip");
        action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED_DISABLED));

        action2 = new Action() {
            public void run() {
                runDtangler();
            }
        };
        action2.setText("Action 2");
        action2.setToolTipText("Action 2 tooltip");
        action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
        doubleClickAction = new Action() {
            public void run() {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection) selection).getFirstElement();
                showMessage("Double-click detected on " + obj.toString());
            }
        };
    }

    private void runDtangler() {

        String cmdLineArguments = "-input=" + dtanglerInput;

        try {

        Arguments arguments = new ArgumentBuilder().build(new String[] {cmdLineArguments});

        DependencyEngine engine = new JavaDependencyEngine();        

        Dependencies dependencies = engine.getDependencies(arguments);
        DependencyGraph dependencyGraph = dependencies.getDependencyGraph();

        AnalysisResult analysisResult = getAnalysisResult(arguments,dependencies);

        printDsm(dependencyGraph, analysisResult);

        if (analysisResult.isValid()) {
            logger.info("Dtangler analisys stopped. Analysis result is valid.");
        } else {
            logger.info("Dtangler analisys stopped. Analysis result is not valid.");
        }

        } catch (MissingArgumentsException e) {
            e.printStackTrace(); // wrong arguments
        } catch (DtException e) {
            e.printStackTrace(); // wrong DTangler operation
        } catch (Exception e) {
            System.err.println("Internal error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private AnalysisResult getAnalysisResult(Arguments arguments, Dependencies dependencies) {
        return new ConfigurableDependencyAnalyzer(arguments).analyze(dependencies);
    }

    private void printDsm(DependencyGraph dependencies, AnalysisResult analysisResult) {
        Writer writer = new SysoutWriter();        
        DSMWriter textUI = new DSMWriter(writer);
        textUI.printDsm(new DsmEngine(dependencies).createDsm(),analysisResult);
        ViolationWriter violationWriter = new ViolationWriter(writer);
        violationWriter.printViolations(analysisResult.getViolations(dependencies.getAllItems()));
    }


    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }

    private void showMessage(String message) {
        MessageDialog.openInformation(
                viewer.getControl().getShell(),
                "DSM View",
                message);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        viewer.getControl().setFocus();
        logger.info("DSM View is in focus.");
    }

    class NameSorter extends ViewerSorter {
    }

    class ViewContentProvider implements IStructuredContentProvider {
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }

        public void dispose() {
        }

        public Object[] getElements(Object parent) {
            return new String[] { "One", "Two", "Three" };
        }
    }
    
    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object obj, int index) {
            return getText(obj);
        }

        public Image getColumnImage(Object obj, int index) {
            return getImage(obj);
        }

        public Image getImage(Object obj) {
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEF_VIEW);
        }
    }

}