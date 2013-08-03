package com.dsmviewer.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;

import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsm.DsmCell;
import org.dtangler.core.dsm.DsmRow;
import org.dtangler.core.dsmengine.DsmEngine;
import org.dtangler.core.textui.DSMWriter;
import org.dtangler.core.textui.ViolationWriter;
import org.dtangler.core.textui.Writer;

import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.dsm.DependencyScope;
import com.dsmviewer.utils.misc.NaturalOrderComparator;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public final class DtanglerUtils {

    private static final NaturalOrderComparator NATURAL_ORDER_COMPARATOR = new NaturalOrderComparator();

    private DtanglerUtils() {
    }

    /**
     * Prints the DS Matrix for given dependencies and analysis result and all analysis violations.
     * 
     * @param Writer any kind of custom writer to use for export (CsvWriter, SysoutWriter, etc)
     * @param dependencies the Dependencies.
     * @param analysisResult the Analysis result.
     */
    public static synchronized void printDsmAndViolations(Writer writer, DependencyGraph dependencies,
            AnalysisResult analysisResult, boolean printViolations) {
        DSMWriter textUI = new DSMWriter(writer);
        textUI.printDsm(new DsmEngine(dependencies).createDsm(), analysisResult);
        if (printViolations) {
            ViolationWriter violationWriter = new ViolationWriter(writer);
            violationWriter.printViolations(analysisResult.getViolations(dependencies.getAllItems()));
        }
    }

    public static String getAbsolutePath(Dependable resource, DependencyScope scope) {
        return getAbsolutePath(resource.getFullyQualifiedName(), scope);
    }

    /**
     * Translates Dtangler FullyQualifiedName to absolutePath. <br/>
     * <p>
     * Example for packages: '/home/Workspace/Project: test.package' --> '/home/Workspace/Project/test/package' <br/>
     * <p>
     * Example for Classes: '/home/Test2/bin: test.package.AbstractClass1' --> ''
     * 
     * @param fullyQualifiedName Dtangler resources naming format (see examples above)
     * @param scope
     * @return
     */
    public static String getAbsolutePath(String fullyQualifiedName, DependencyScope scope) {

        String[] splitted = fullyQualifiedName.split(": ");
        String resourceParentPath = splitted[0];

        switch (scope) {
        case PACKAGES:
        	if("default".equals(splitted[1])) { //
                // do not include package name if there is a "default" package
                // (note that Eclipse package can`t be named as "default" manually)
        		return resourceParentPath;
        	} else {
        		String packageRelativePath = splitted[1].replaceAll("\\.", Matcher.quoteReplacement(File.separator));
        		return resourceParentPath.concat(File.separator).concat(packageRelativePath);
        	}
        case CLASSES:
            if (resourceParentPath.endsWith(".class")) {
                return resourceParentPath;
            } else {
                String classRelativePath = splitted[1].replaceAll("\\.",  Matcher.quoteReplacement(File.separator));
                
                // fix the path for resource if it is placed under "default" package
                String defaultPackageName = "default" + File.separator;
				if(classRelativePath.startsWith(defaultPackageName)) {
                	classRelativePath = classRelativePath.substring(defaultPackageName.length());
                }

                String classFullPath = resourceParentPath.concat(File.separator).concat(classRelativePath);
                if (!classFullPath.endsWith(".class")) {
                    classFullPath = classFullPath.concat(".class");
                }
                return classFullPath;
            }
        default:
            throw new IllegalArgumentException("Scope " + scope + " is not supported");
        }
    }

    public static List<DsmRow> buildDsmRowsUsingDtanglersDefaultOrdering(DependencyGraph dependencyGraph) {
        Dsm dsm = new DsmEngine(dependencyGraph).createDsm();
        // transpose matrix to make it more human-readable
        return DtanglerUtils.transposeDsm(dsm).getRows();
    }

    @SuppressWarnings("unchecked")
    public static void sortDisplayNamesInNaturalOrder(DependencyMatrix dsMatrix) {
        int startIndex = 0;
        int endIndex = dsMatrix.getSize() - 1;
        sortByDependencyNames(dsMatrix, NATURAL_ORDER_COMPARATOR, startIndex, endIndex);
    }

    /**
     * Sort dependency matrix by dependant resources names. Uses quick sort algorithm.
     */
    private static void sortByDependencyNames(DependencyMatrix dsMatrix, Comparator<String> comparator, int start, int end) {
        if (start >= end) {
            return;
        }
        int i = start, j = end;
        int cur = i - (i - j) / 2;
        while (i < j) {
            while (i < cur && (comparator.compare(dsMatrix.getDisplayName(i), dsMatrix.getDisplayName(cur)) <= 0)) {
                i++;
            }
            while (j > cur && (comparator.compare(dsMatrix.getDisplayName(cur), dsMatrix.getDisplayName(j)) <= 0)) {
                j--;
            }
            if (i < j) {
                dsMatrix.replaceRows(i, j);

                if (i == cur) {
                    cur = j;
                }
                else if (j == cur) {
                    cur = i;
                }
            }
        }
        sortByDependencyNames(dsMatrix, comparator, start, cur);
        sortByDependencyNames(dsMatrix, comparator, cur + 1, end);
    }

    public static Dsm transposeDsm(Dsm dsm) {
        List<DsmRow> inputRows = dsm.getRows();
        int dsmSize = inputRows.size();

        List<DsmRow> resultRows = new ArrayList<DsmRow>(dsmSize);

        for (int i = 0; i < dsmSize; i++) {
            ArrayList<DsmCell> cells = new ArrayList<DsmCell>(dsmSize);
            for (int j = 0; j < dsmSize; j++) {
                DsmCell inputCell = inputRows.get(j).getCells().get(i);
                cells.add(inputCell);
            }
            Dependable dependee = inputRows.get(i).getDependee();
            resultRows.add(new DsmRow(dependee, cells));
        }
        
        // free the memory
        inputRows = null;
        dsm = null;
        
        return new Dsm(resultRows);
    }
}
