package com.dsmviewer.dsm;

import java.util.ArrayList;
import java.util.List;

import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dependencies.Scope;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsm.DsmCell;
import org.dtangler.core.dsm.DsmRow;
import org.dtangler.core.dsmengine.DsmEngine;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsMatrix {

    private AnalysisResult analysisResult;
    private List<DsmRow> rows;

    public DsMatrix(DependencyGraph dependencyGraph, AnalysisResult analysisResult) {
        Dsm dsm = new DsmEngine(dependencyGraph).createDsm();
//        rows = DtanglerUtils.sortDsmRowsInNaturalOrder(dsm.getRows());
        rows = dsm.getRows();
        this.analysisResult = analysisResult;
    }

    public List<DsmRow> getRows() {
        return rows;
    }

    public int getSize() {
        return rows.size();
    }

    public List<String> getDisplayNames() {
        List<String> result = new ArrayList<String>(getSize());
        List<DsmRow> rows = getRows();
        for (int i = 0; i < rows.size(); i++) {
            result.add(rows.get(i).getDependee().getDisplayName());
        }
        return result;
    }

    public AnalysisResult getAnalysisResult() {
        return analysisResult;
    }

    public DsmCell getCell(int i, int j) {
        return getRows().get(i).getCells().get(j);
    }

    public void setCell(int i, int j, DsmCell value) {
        getRows().get(i).getCells().set(j, value);
    }

    public void replaceCells(int i1, int j1, int i2, int j2) {
        DsmCell temp = getCell(i1, j1);
        setCell(i1, j1, getCell(i2, j2));
        setCell(i2, j2, temp);
    }

    public boolean hasViolations(int i, int j) {
        return hasViolations(getCell(i, j));
    }

    public boolean hasViolations(DsmCell cell) {
        return hasViolations(cell.getDependency());
    }

    public boolean hasViolations(Dependency dependency) {
        return analysisResult.hasViolations(dependency);
    }

    public DependencyScope getScope(int i, int j, DependencyLocation dependencyLocation) {
        return getScope(dependencyLocation, getCell(i, j));
    }

    private DependencyScope getScope(DependencyLocation dependencyLocation, DsmCell cell) {
        return getScope(cell.getDependency(), dependencyLocation);
    }

    public DependencyScope getScope(Dependency dependency, DependencyLocation dependencyLocation) {
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

}