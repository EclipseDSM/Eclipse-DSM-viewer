package com.dsmviewer.dsm;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dependencies.Scope;
import org.dtangler.core.dsm.DsmCell;
import org.dtangler.core.dsm.DsmRow;

import com.dsmviewer.utils.DtanglerUtils;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DependencyMatrix {

    private AnalysisResult analysisResult;
    private List<DsmRow> rows;
    private DependencyGraph dependencyGraph;
    private DependencyMatrixOrdering currentOrdering;

    public DependencyMatrix(DependencyGraph dependencyGraph, AnalysisResult analysisResult,
            DependencyMatrixOrdering ordering) {
        this.dependencyGraph = dependencyGraph;
        this.rows = DtanglerUtils.buildDsmRowsUsingDtangler(dependencyGraph);
        this.analysisResult = analysisResult;

        // skip sorting if Dtangler`s default ordering is provided
        if (!DependencyMatrixOrdering.isDtanglerDefaultOrdering(ordering)) {
            sort(ordering);
        }
    }

    public List<DsmRow> getRows() {
        return this.rows;
    }

    public DsmRow getRow(int index) {
        return this.rows.get(index);
    }

    public void setRow(int index, DsmRow row) {
        this.rows.set(index, row);
    }

    public void setRows(List<DsmRow> rows) {
        this.rows = rows;
    }

    public int getRowIndex(String displayName) {
        return getDisplayNames().indexOf(displayName);
    }

    public final int getSize() {
        return this.rows.size();
    }

    public List<String> getDisplayNames() {
        List<String> result = new ArrayList<String>(getSize());
        for (int i = 0; i < getSize(); i++) {
            result.add(this.rows.get(i).getDependee().getDisplayName());
        }
        return result;
    }

    public String getDisplayName(int rowIndex) {
        return this.rows.get(rowIndex).getDependee().getDisplayName();
    }

    public String getFullyQualifiedName(int rowIndex) {
        return this.rows.get(rowIndex).getDependee().getFullyQualifiedName();
    }

    public AnalysisResult getAnalysisResult() {
        return this.analysisResult;
    }

    public DsmCell getCell(int rowIndex, int columnIndex) {
        return getRow(rowIndex).getCells().get(columnIndex);
    }

    public void setCell(int rowIndex, int columnIndex, DsmCell value) {
        getRow(rowIndex).getCells().set(columnIndex, value);
    }

    public final void replaceCells(int rowIndex1, int columnIndex1, int rowIndex2, int columnIndex2) {
        DsmCell temp = getCell(rowIndex1, columnIndex1);
        setCell(rowIndex1, columnIndex1, getCell(rowIndex2, columnIndex2));
        setCell(rowIndex2, columnIndex2, temp);
    }

    public void replaceRows(int rowNum1, int rowNum2) {

        int row1Index = Math.min(rowNum1, rowNum2);
        int row2Index = Math.max(rowNum1, rowNum2);

        // replace rows
        for (int j = 0; j < getSize(); j++) {
            if (j == row1Index || j == row2Index) {
                // skip
            } else {
                replaceCells(row1Index, j, row2Index, j);
            }
        }

        // replace columns
        for (int i = 0; i < getSize(); i++) {
            if (i == row1Index || i == row2Index) {
                // skip
            } else {
                replaceCells(i, row1Index, i, row2Index);
            }
        }

        // replace additional cells on intersection of row1 and row2
        replaceCells(row2Index, row1Index, row1Index, row2Index);

        DsmRow row1 = getRow(rowNum1);
        DsmRow row2 = getRow(rowNum2);
        rows.set(rowNum1, new DsmRow(row2.getDependee(), row1.getCells()));
        rows.set(rowNum2, new DsmRow(row1.getDependee(), row2.getCells()));
    }

    public boolean hasViolations(int i, int j) {
        return hasViolations(getCell(i, j));
    }

    public boolean hasViolations(DsmCell cell) {
        return hasViolations(cell.getDependency());
    }

    public boolean hasViolations(Dependency dependency) {
        return this.analysisResult.hasViolations(dependency);
    }

    public DependencyScope getScope(int i, int j, DependencyType dependencyLocation) {
        return getScope(dependencyLocation, getCell(i, j));
    }

    private DependencyScope getScope(DependencyType dependencyLocation, DsmCell cell) {
        return getScope(cell.getDependency(), dependencyLocation);
    }

    public DependencyScope getScope(Dependency dependency, DependencyType dependencyLocation) {
        Scope scope = null;
        switch (dependencyLocation) {
        case DEPENDANT:
            scope = dependency.getDependant().getScope();
            break;
        case DEPENDEE:
            scope = dependency.getDependee().getScope();
            break;
        default:
            return null;
        }

        switch (scope.index()) {
        case 1:
            return DependencyScope.PACKAGES;
        case 2:
            return DependencyScope.CLASSES;
        default:
            return null;
        }
    }

    public DependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }

    public void sort(DependencyMatrixOrdering ordering) {
        if (currentOrdering != ordering) {
            this.currentOrdering = ordering;

            switch (ordering) {
            case BY_INSTABILITY:
                setRows(DtanglerUtils.buildDsmRowsUsingDtangler(getDependencyGraph()));
                break;
            case NATURAL_ORDERING:
                DtanglerUtils.sortDisplayNamesInNaturalOrder(this);
                break;
            default:
                throw new IllegalArgumentException("Ordering '" + ordering + "' is not supported");
            }
        }
    }

    public DependencyMatrixOrdering getOrdering() {
        return currentOrdering;
    }

    @Override
    public String toString() {
        if (getSize() > 20) {
            String hasViolationsSuffix = analysisResult.isValid() ? ""
                    : MessageFormat.format("has {0} violations", analysisResult.getAllViolations().size());
            return "DependencyMatrix: size = " + getSize() + ", " + hasViolationsSuffix;
        } else {
            // TODO: This code is written for test purposes and should be removed before release
            StringBuilder sb = new StringBuilder(getClass().getSimpleName());
            sb.append("\n");
            List<String> displayNames = getDisplayNames();
            for (int i = 0; i < getSize(); i++) {
                sb.append(displayNames.get(i));
                sb.append(" ");
                for (int j = 0; j < getSize(); j++) {
                    sb.append("| ");
                    sb.append(getCell(i, j).getDependencyWeight());
                    sb.append(" |");
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((analysisResult == null) ? 0 : analysisResult.hashCode());
        result = prime * result + ((currentOrdering == null) ? 0 : currentOrdering.hashCode());
        result = prime * result + ((dependencyGraph == null) ? 0 : dependencyGraph.hashCode());
        result = prime * result + ((rows == null) ? 0 : rows.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DependencyMatrix other = (DependencyMatrix) obj;
        if (analysisResult == null) {
            if (other.analysisResult != null) {
                return false;
            }
        } else if (!analysisResult.getAllViolations().equals(other.analysisResult.getAllViolations())) {
            return false;
        }
        if (currentOrdering != other.currentOrdering) {
            return false;
        }
        if (dependencyGraph == null) {
            if (other.dependencyGraph != null) {
                return false;
            }
        } else if (!dependencyGraph.getAllItems().equals(other.dependencyGraph.getAllItems())) {
            return false;
        }
        return true;
    }

}