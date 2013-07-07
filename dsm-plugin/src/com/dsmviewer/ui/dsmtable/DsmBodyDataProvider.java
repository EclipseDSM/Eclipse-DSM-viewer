package com.dsmviewer.ui.dsmtable;

import org.dtangler.core.dsm.DsmRow;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import com.dsmviewer.dsm.DependencyMatrix;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmBodyDataProvider implements IDataProvider {

    private static final String EMPTY_STRING = "";

    private DependencyMatrix dependencyMatrix;

    public DsmBodyDataProvider(DependencyMatrix dependencyMatrix) {
        this.dependencyMatrix = dependencyMatrix;
    }

    @Override
    public int getColumnCount() {
        return dependencyMatrix.getSize();
    }

    @Override
    public Object getDataValue(int column, int row) {
        return internalGetDataValue(row, column);
    }

    private String internalGetDataValue(int row, int column) {
        DsmRow dsmRow = dependencyMatrix.getRow(row);
        int dependencyWeight = dsmRow.getCells().get(column).getDependencyWeight();
        return (dependencyWeight == 0) ? EMPTY_STRING : String.valueOf(dependencyWeight);
    }

    @Override
    public int getRowCount() {
        return dependencyMatrix.getSize();
    }

    @Override
    public void setDataValue(int i, int j, Object value) {
        // do nothing
    }

    public void setDependencyMatrix(DependencyMatrix dsMatrix) {
        this.dependencyMatrix = dsMatrix;
    }

    public DependencyMatrix getDependencyMatrix() {
        return dependencyMatrix;
    }

}
