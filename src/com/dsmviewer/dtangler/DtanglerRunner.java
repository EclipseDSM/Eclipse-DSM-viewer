package com.dsmviewer.dtangler;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavtsev</a>
 * 
 */
public class DtanglerRunner {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * Run.
     *
     * @param arguments the arguments
     */
    public void run(Arguments arguments) {
        try {
        logger.info("Dtangler analisys started.");        
                             
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
            throw e;
        } catch (DtException e) {
            throw e;
        }
    }
    
    /**
     * Gets the analysis result.
     *
     * @param arguments the arguments
     * @param dependencies the dependencies
     * @return the analysis result
     */
    private AnalysisResult getAnalysisResult(Arguments arguments, Dependencies dependencies) {
        return new ConfigurableDependencyAnalyzer(arguments).analyze(dependencies);
    }

    /**
     * Prints the dsm.
     *
     * @param dependencies the dependencies
     * @param analysisResult the analysis result
     */
    private void printDsm(DependencyGraph dependencies, AnalysisResult analysisResult) {
        Writer writer = new SysoutWriter();        
        DSMWriter textUI = new DSMWriter(writer);
        textUI.printDsm(new DsmEngine(dependencies).createDsm(),analysisResult);
        ViolationWriter violationWriter = new ViolationWriter(writer);
        violationWriter.printViolations(analysisResult.getViolations(dependencies.getAllItems()));
    }
    
}
