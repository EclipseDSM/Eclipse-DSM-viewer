package com.dsmviewer.dtangler;

import java.util.ArrayList;
import java.util.List;

import org.dtangler.core.MissingArgumentsException;
import org.dtangler.core.analysis.configurableanalyzer.ConfigurableDependencyAnalyzer;
import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dependencyengine.DependencyEngine;
import org.dtangler.core.dsmengine.DsmEngine;
import org.dtangler.core.exception.DtException;
import org.dtangler.core.textui.DSMWriter;
import org.dtangler.core.textui.SysoutWriter;
import org.dtangler.core.textui.ViolationWriter;
import org.dtangler.core.textui.Writer;
import org.dtangler.javaengine.dependencyengine.JavaDependencyEngine;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.dsmviewer.Activator;
import com.dsmviewer.logging.Logger;
import com.dsmviewer.ui.views.DsmView;

/**
 * 
 * @author Roman Ivanov
 * 
 */
public class DtanglerRunner implements IObjectActionDelegate {

    private final Logger logger = Activator.getLogger(DtanglerRunner.class);

    /** Current Eclipse Project/Package Explorer selection. */
    private IStructuredSelection selection;

    @Override
	public void setActivePart(final IAction arg0, final IWorkbenchPart arg1) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public void selectionChanged(final IAction action, final ISelection selectionData) {
        selection = (IStructuredSelection) selectionData;
		logger.debug("Package Explorer selection was changed to "
				+ ((IResource) selection.getFirstElement()).getClass().toString());
    }

    /**
     * Runs Dtangler library when "Package Explorer" context menu action called.
     * 
     * {@inheritDoc}
     */
    @Override
	public void run(final IAction action) {

        try {

            List<String> pathList = getPathList(selection);

            String scope = action.getDescription(); // "classes" / "packages"

            Arguments arguments = DtanglerArguments.build(pathList, scope, false);

            DsMatrix dsMatrix = computeDsMatrix(arguments);
            DsmView.getTableViewer().showDsMatrix(dsMatrix);

        } catch (MissingArgumentsException e) {
            logger.error(e.getMessage(), e);
            Activator.showErrorMessage(e.getMessage());
        } catch (DtException e) {
            String errorMessage = "DTangler cannot process this request";
            logger.error(errorMessage, e);
            Activator.showErrorMessage(errorMessage + ": " + e.getMessage());
        }
    }

    /**
     * Run Dtangler analysis with given Arguments.
     * 
     * @param arguments - the arguments
     * @throws DtException when DTangler cannot process current request.
     * @throws MissingArgumentsException if the request parameters are incorrect.
     */
    public DsMatrix computeDsMatrix(Arguments arguments) {

        DsMatrix dsMatrix;

        try {
            logger.info("Dtangler analisys started.");

            DependencyEngine engine = new JavaDependencyEngine();
            // engine.setDependencyEngineId("java");

            Dependencies dependencies = engine.getDependencies(arguments);
            DependencyGraph dependencyGraph = dependencies.getDependencyGraph();

            AnalysisResult analysisResult = getAnalysisResult(arguments, dependencies);

            dsMatrix = new DsMatrix(dependencyGraph);

            printDsmAndViolations(dependencyGraph, analysisResult);

			String message;
            if (analysisResult.isValid()) {
				message = "Analysis result is valid.";
            } else {
				message = "Analysis result is not valid.";
            }
			logger.info("Dtangler analisys stopped." + message);

        } catch (MissingArgumentsException e) {
            throw e;
        } catch (DtException e) {
            throw e;
        }

        return dsMatrix;
    }

    /**
     * Gets the full path of the given Eclipse Project Explorer resource (Project/File/Folder etc).
     * 
     * @param resource - the resource.
     * @return the full path of the given resource.
     */
	private static String getFullPath(IResource resource) {
        return resource.getLocationURI().getPath().toString();
    }

    /**
     * Gets the list of paths for resources that are selected in Package Explorer.
     * 
     * @param selection - selected resources.
     * @return the list of paths that will be passed to Dtangler Analyzer.
     */
    private List<String> getPathList(IStructuredSelection selection) {
        List<String> pathList = new ArrayList<String>();
        List<Object> selectedResources = selection.toList();
        for (Object selectedResource : selectedResources) {
            IResource resource = (IResource) selectedResource;
            String resourcePath = getFullPath(resource);
            pathList.add(resourcePath);
			logger.debug("Added to analysis:" + resourcePath);
        }
        return pathList;
    }

    /**
     * Gets the Dtangler analysis result as AnalisysResult object.
     * 
     * @param arguments
     *            the Arguments.
     * @param dependencies
     *            the Dependencies.
     * @return the analysis result
     */
	private static AnalysisResult getAnalysisResult(Arguments arguments, Dependencies dependencies) {
        return new ConfigurableDependencyAnalyzer(arguments).analyze(dependencies);
    }

    /**
     * Prints the DS Matrix for given dependencies and analysis result and all analysis violations.
     * 
     * @param dependencies
     *            the Dependencies.
     * @param analysisResult
     *            the Analysis result.
     */
	private static void printDsmAndViolations(DependencyGraph dependencies, AnalysisResult analysisResult) {
        Writer writer = new SysoutWriter();
        DSMWriter textUI = new DSMWriter(writer);
        textUI.printDsm(new DsmEngine(dependencies).createDsm(), analysisResult);
        ViolationWriter violationWriter = new ViolationWriter(writer);
        violationWriter.printViolations(analysisResult.getViolations(dependencies.getAllItems()));
    }

}