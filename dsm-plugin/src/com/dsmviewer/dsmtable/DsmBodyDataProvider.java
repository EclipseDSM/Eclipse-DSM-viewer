package com.dsmviewer.dsmtable;

import org.dtangler.core.dsm.DsmRow;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import com.dsmviewer.dsm.DependencyMatrix;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmBodyDataProvider implements IDataProvider {

    private static final String EMPTY_STRING = "";

    private DependencyMatrix dsMatrix;

    public DsmBodyDataProvider(DependencyMatrix dsMatrix) {
        this.dsMatrix = dsMatrix;
    }

    @Override
    public int getColumnCount() {
        return dsMatrix == null ? 0 : dsMatrix.getSize();
    }

    @Override
    public Object getDataValue(int column, int row) {
        return dsMatrix == null ? EMPTY_STRING : internalGetDataValue(row, column);
    }

    private String internalGetDataValue(int row, int column) {
        DsmRow dsmRow = dsMatrix.getRow(row);
        int dependencyWeight = dsmRow.getCells().get(column).getDependencyWeight();
        return (dependencyWeight == 0) ? EMPTY_STRING : String.valueOf(dependencyWeight);
    }

    @Override
    public int getRowCount() {
        return dsMatrix == null ? 0 : dsMatrix.getSize();
    }

    @Override
    public void setDataValue(int i, int j, Object value) {
        // do nothing
    }

    public void setDsMatrix(DependencyMatrix dsMatrix) {
        this.dsMatrix = dsMatrix;
    }

    public DependencyMatrix getDsMatrix() {
        return dsMatrix;
    }

}
