package com.dsmviewer.utils;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dsm.DsmRow;
import org.dtangler.core.dsmengine.DsmEngine;
import org.dtangler.core.textui.DSMWriter;
import org.dtangler.core.textui.ViolationWriter;
import org.dtangler.core.textui.Writer;

import com.dsmviewer.dsm.DsMatrix;

public final class DtanglerUtils {

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
        return fullPath + File.separator + resourceName.replaceAll("\\.", File.separator);
    }

    @SuppressWarnings("unchecked")
    public static DsMatrix sortDsmRowsInNaturalOrder(DsMatrix matrix) {
        List<DsmRow> input = matrix.getRows();

        Collections.sort(input, new NaturalOrderComparator() {
            @Override
            public int compare(Object row1, Object row2) {
                DsmRow dsmRow1 = (DsmRow) row1;
                DsmRow dsmRow2 = (DsmRow) row2;
                String displayName1 = dsmRow1.getDependee().getDisplayName();
                String displayName2 = dsmRow2.getDependee().getDisplayName();
                int compareResult = super.compare(displayName1, displayName2);
                if (compareResult < 0) { // row 1 < row2. --> row 2 goes to row1

                } else { // row2 < row1 --> row 1 goes to row2

                }
                return compareResult;
            }
        });
        return matrix;
    }

}
