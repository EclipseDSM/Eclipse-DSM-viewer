package com.dsmviewer.dtangler;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.dtangler.core.MissingArgumentsException;
import org.dtangler.core.analysis.configurableanalyzer.ConfigurableDependencyAnalyzer;
import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dependencyengine.DependencyEngine;
import org.dtangler.core.exception.DtException;
import org.dtangler.javaengine.dependencyengine.JavaDependencyEngine;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

import com.dsmviewer.Activator;
import com.dsmviewer.dsm.DependencyScope;
import com.dsmviewer.dsm.DsMatrix;
import com.dsmviewer.logging.Logger;
import com.dsmviewer.ui.views.DsmView;
import com.dsmviewer.utils.CoreUtils;
import com.dsmviewer.utils.DtanglerUtils;
import com.dsmviewer.utils.EclipseUtils;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DtanglerRunner implements IObjectActionDelegate {

    private static final Logger LOGGER = Activator.getLogger(DtanglerRunner.class);

    /** Current Eclipse Project/Package Explorer selection. */
    private volatile IStructuredSelection selection;

    private IWorkbenchPart activeWorkBechPart;

    private static IResource selectedElement;

    @Override
    public synchronized void setActivePart(final IAction arg0, final IWorkbenchPart activeWorkBechPart) {
        this.activeWorkBechPart = activeWorkBechPart;
    }

    @Override
    public synchronized void selectionChanged(final IAction action, final ISelection selectionData) {
        selection = (IStructuredSelection) selectionData;
        selectedElement = (IResource) selection.getFirstElement();
        if (selectedElement != null) {
            LOGGER.debug("Package Explorer selection was changed to " + selectedElement.getClass().toString());
        }
    }

    /**
     * Runs Dtangler library when "Package Explorer" context menu action called.
     */
    @Override
    public synchronized void run(final IAction action) {

        Job job = new Job("DSM computing") {
            @Override
            protected IStatus run(final IProgressMonitor monitor) {

                while (!monitor.isCanceled()) {
                    monitor.beginTask("Dsm-Viewer", 5);

                    // update UI before analisys
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            LOGGER.info("Dtangler analisys started.");
                        }
                    });

                    monitor.subTask("Getting the Path List");
                    List<String> pathList = null;

                    if (selectedElement instanceof IProject) {
                        IProject project = (IProject) selectedElement;
                        String binaryOutputLocation = EclipseUtils.getBinaryOutputLocation(project, false, false);
                        pathList = new LinkedList<String>();
                        pathList.add(binaryOutputLocation);
                    } else { // get list of all resources under selection
                        pathList = getPathList(selection);
                    }

                    String scope = action.getDescription(); // "classes" / "packages"
                    monitor.worked(1);

                    DsMatrix dsMatrix = null;

                    try {
                        monitor.subTask("Computing DS-Matrix for " + pathList.size() + " resource(s)");
                        dsMatrix = computeDsMatrixFromBinaries(pathList, scope);
                        monitor.worked(2);

                    } catch (MissingArgumentsException e) {
                        LOGGER.error(e.getMessage(), e);
                        Activator.showErrorMessage(e.getMessage());
                        return Status.CANCEL_STATUS;
                    } catch (DtException e) {
                        String errorMessage = "DTangler cannot process this request";
                        LOGGER.error(errorMessage, e);
                        Activator.showErrorMessage(errorMessage, e);
                        return Status.CANCEL_STATUS;
                    }

                    monitor.subTask("Opening the Dsm View");
                    // Showing the DSM-view:
                    Display.getDefault().asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                EclipseUtils.showDsmView(activeWorkBechPart);
                            } catch (PartInitException e) {
                                String message = "Cannot open the DSM View";
                                LOGGER.error(message, e);
                                Activator.showErrorMessage(message, e);
                            }
                        }
                    });
                    monitor.worked(4);

                    monitor.subTask("Showing the DS-Matrix for " + dsMatrix.getSize() + " elements");
                    // update UI after analisys
                    Display.getDefault().asyncExec(new ShowDsMatrixJob(dsMatrix));
                    monitor.worked(5);

                    return Status.OK_STATUS;
                }
                return Status.CANCEL_STATUS;
            }

        };

        job.setPriority(Job.LONG);
        job.schedule();
    }

    public static synchronized DsMatrix computeDsMatrixFromSources(List<String> fullyQualifiedPathList,
            DependencyScope scope, boolean allowCycles) {
        Arguments arguments = DtanglerArguments.build(getBinaryPathList(fullyQualifiedPathList),
                scope.getDisplayName(), allowCycles);
        return computeDsMatrix(arguments);
    }

    public static synchronized DsMatrix computeDsMatrixFromSources(List<String> fullyQualifiedPathList,
            DependencyScope scope) {
        Arguments arguments = DtanglerArguments.build(getBinaryPathList(fullyQualifiedPathList),
                scope.getDisplayName(), false);
        return computeDsMatrix(arguments);
    }

    private static List<String> getBinaryPathList(List<String> resourceFullyQualifiedNames) {

        List<String> result = new LinkedList<String>();

        for (String fullyQualifiedName : resourceFullyQualifiedNames) {
            String binaryResourcePath = DtanglerUtils.getAbsolutePath(fullyQualifiedName);
            File resourceFile = new File(binaryResourcePath);
            if (resourceFile.isFile()) {
                result.add(binaryResourcePath);
            } else if (resourceFile.isDirectory()) {
                if (resourceFile.exists() && resourceFile.isDirectory()) {
                    result.addAll(CoreUtils.listFiles(resourceFile));
                }
            }
        }
        return result;
    }

    public static synchronized DsMatrix computeDsMatrixFromSources(List<String> fullyQualifiedPathList, String scope) {
        Arguments arguments = DtanglerArguments.build(fullyQualifiedPathList, scope, false);
        return computeDsMatrix(arguments);
    }

    public static synchronized DsMatrix computeDsMatrixFromBinaries(List<String> pathList, DependencyScope scope,
            boolean allowCycles) {
        Arguments arguments = DtanglerArguments.build(pathList, scope.getDisplayName(), allowCycles);
        return computeDsMatrix(arguments);
    }

    public static synchronized DsMatrix computeDsMatrixFromBinaries(List<String> pathList, DependencyScope scope) {
        Arguments arguments = DtanglerArguments.build(pathList, scope.getDisplayName(), false);
        return computeDsMatrix(arguments);
    }

    public static synchronized DsMatrix computeDsMatrixFromBinaries(List<String> pathList, String scope,
            boolean allowCycles) {
        Arguments arguments = DtanglerArguments.build(pathList, scope, allowCycles);
        return computeDsMatrix(arguments);
    }

    public static synchronized DsMatrix computeDsMatrixFromBinaries(List<String> pathList, String scope) {
        Arguments arguments = DtanglerArguments.build(pathList, scope, false);
        return computeDsMatrix(arguments);
    }

    /**
     * Run Dtangler analysis with given Arguments.
     * 
     * @param arguments - the arguments
     * @throws DtException when DTangler cannot process current request.
     * @throws MissingArgumentsException if the request parameters are incorrect.
     */
    public static synchronized DsMatrix computeDsMatrix(Arguments arguments) {

        DsMatrix dsMatrix = null;

        try {

            DependencyEngine engine = new JavaDependencyEngine();
            // engine.setDependencyEngineId("java");

            Dependencies dependencies = engine.getDependencies(arguments);
            DependencyGraph dependencyGraph = dependencies.getDependencyGraph();

            ConfigurableDependencyAnalyzer analyzer = new ConfigurableDependencyAnalyzer(arguments);
            AnalysisResult analysisResult = analyzer.analyze(dependencies);

            dsMatrix = new DsMatrix(dependencyGraph, analysisResult);

        } catch (MissingArgumentsException e) {
            String message = "Wrong Dtangler arguments provided";
            LOGGER.error(message, e);
            Activator.showErrorMessage(message, e);
        } catch (DtException e) {
            String message = "Dtangler: Error during computing the DS Matrix";
            LOGGER.error(message, e);
            Activator.showErrorMessage(message, e);
        }

        return dsMatrix;
    }

    /**
     * Gets the list of paths for resources that are selected in Package Explorer.
     * 
     * @param selection - selected resources.
     * @return the list of paths that will be passed to Dtangler Analyzer.
     */
    private synchronized static List<String> getPathList(IStructuredSelection selection) {
        List<String> pathList = new ArrayList<String>();
        if (selection != null) {
            List<Object> selectedResources = selection.toList();
            for (Object selectedResource : selectedResources) {
                if (selectedResource instanceof IProject) {
                    // compile the sources of selected project 
//                    IProject project = (IProject) selectedResource;
//                    try {
//                        Utils.buildProject(project);
//                    } catch (CoreException e) {
//                        e.printStackTrace();
//                    }
                }
                IResource resource = (IResource) selectedResource;
                String resourcePath = EclipseUtils.getFullPath(resource);
                pathList.add(resourcePath);
            }
        }

        return pathList;
    }

    class ShowDsMatrixJob implements Runnable {

        private DsMatrix dsMatrix;

        public ShowDsMatrixJob(DsMatrix dsMatrix) {
            this.dsMatrix = dsMatrix;
        }

        @Override
        public void run() {
            DsmView.showDsMatrix(dsMatrix);
            String message = "\n" + (dsMatrix.getAnalysisResult().isValid() ? "Analysis result is valid."
                    : "Analysis result is not valid.");
            LOGGER.info("Dtangler analisys completed. "
                    + "Analyzed " + dsMatrix.getSize() + " parent-scope resources. " + message);
        }
    }

}
