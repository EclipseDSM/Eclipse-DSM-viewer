package com.dsmviewer.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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

    public static String getAbsolutePath(Dependable dependable) {
        return getAbsolutePath(dependable.getFullyQualifiedName());
    }

    /**
     * Translates Dtangler FullyQualifiedName to absolutePath. <br/>
     * <p>
     * Example for packages: '/home/Workspace/Project: test.package' --> '/home/Workspace/Project/test/package' <br/>
     * <p>
     * Example for Classes: '/home/Workspace/Project: ClassName' --> '/home/Workspace/Project/Classname' ?? (not tested
     * yet)
     * 
     * @param fullyQualifiedName Dtangler resources naming format (see examples below)
     * @return
     */
    public static String getAbsolutePath(String fullyQualifiedName) {
        String[] splitted = fullyQualifiedName.split(": ");
        String fullPath = splitted[0];
        String resourceName = splitted[1];
        StringBuilder sb = new StringBuilder();
        sb.append(fullPath);
        sb.append(File.separator);
        sb.append(resourceName.replaceAll("\\.", File.separator));
        File file = new File(sb.toString());
        if (file.exists() && file.isDirectory()) {
            // do nothing
        } else {
            sb.append(".class");
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public static List<DsmRow> sortInNaturalOrderByDisplayNames(final DependencyMatrix dsMatrix) {

        final List<DsmRow> rows = dsMatrix.getRows();

        Collections.sort(rows, new NaturalOrderComparator() {
            @Override
            public int compare(Object row1, Object row2) {
                String displayName1 = ((DsmRow) row1).getDependee().getDisplayName();
                String displayName2 = ((DsmRow) row2).getDependee().getDisplayName();

                int compareResult = super.compare(displayName1, displayName2);
                if (compareResult > 0) {
                    int rowIndex1 = dsMatrix.getRowIndex(displayName1);
                    int rowIndex2 = dsMatrix.getRowIndex(displayName2);

                    int rowI1 = Math.min(rowIndex1, rowIndex2);
                    int rowI2 = Math.max(rowIndex1, rowIndex2);

                    // fix changes for cells which should not be moved
                    dsMatrix.replaceCells(rowI1, rowI2, rowI1, rowI1);
                    dsMatrix.replaceCells(rowI2, rowI1, rowI2, rowI2);

                }
                return compareResult;
            }

        });
        return rows;
    }

    public static void quickSort(DependencyMatrix dsMatrix) {
        int startIndex = 0;
        int endIndex = dsMatrix.getSize() - 1;

        doSort(dsMatrix, startIndex, endIndex);
    }

    private static void doSort(DependencyMatrix dsMatrix, int start, int end) {
        if (start >= end) {
            return;
        }
        int i = start, j = end;
        int cur = i - (i - j) / 2;
        while (i < j) {
            // dsMatrix.get(i).get(0) <= dsMatrix.get(cur).get(0)
            while (i < cur && (
                    NATURAL_ORDER_COMPARATOR.compare(dsMatrix.getDisplayName(i), dsMatrix.getDisplayName(cur)) <= 0
                    )) {
                i++;
            }
            // dsMatrix.get(cur).get(0) <= dsMatrix.get(j).get(0)
            while (j > cur && (
                    NATURAL_ORDER_COMPARATOR.compare(dsMatrix.getDisplayName(cur), dsMatrix.getDisplayName(j)) <= 0
                    )) {
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
        doSort(dsMatrix, start, cur);
        doSort(dsMatrix, cur + 1, end);
    }

    public static Dsm transposeValues(Dsm dsm) {
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
