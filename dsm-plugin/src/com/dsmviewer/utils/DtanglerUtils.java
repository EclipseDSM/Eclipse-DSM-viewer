package com.dsmviewer.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
     * Example for Classes: '/home/Workspace/Project: ClassName' --> '/home/Workspace/Project/Classname'
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
            String resourceRelativePath = splitted[1].replaceAll("\\.", File.separator);

            String resourceFullPath = resourceParentPath.concat(File.separator).concat(resourceRelativePath);
            return resourceFullPath;
        case CLASSES:
            return resourceParentPath;
        default:
            throw new IllegalArgumentException("Scope " + scope + " is not supported");
        }
    }

    public static List<DsmRow> buildDsmRowsUsingDtangler(DependencyGraph dependencyGraph) {
        Dsm dsm = new DsmEngine(dependencyGraph).createDsm();
        // transpose matrix to make it more human-readable
        return DtanglerUtils.transposeDsm(dsm).getRows();
    }

    @SuppressWarnings("unchecked")
    public static void sortDisplayNamesInNaturalOrder(DependencyMatrix dsMatrix) {
        int startIndex = 0;
        int endIndex = dsMatrix.getSize() - 1;
        quickSortByDepName(dsMatrix, NATURAL_ORDER_COMPARATOR, startIndex, endIndex);
    }

    private static void quickSortByDepName(DependencyMatrix dsMatrix, Comparator<String> comparator, int start, int end) {
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
        quickSortByDepName(dsMatrix, comparator, start, cur);
        quickSortByDepName(dsMatrix, comparator, cur + 1, end);
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
        return new Dsm(resultRows);
    }
}
