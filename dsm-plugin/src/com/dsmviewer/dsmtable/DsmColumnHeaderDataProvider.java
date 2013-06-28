package com.dsmviewer.dsmtable;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import com.dsmviewer.dsm.DependencyMatrix;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmColumnHeaderDataProvider implements IDataProvider {

    private DependencyMatrix dsMatrix;

    public DsmColumnHeaderDataProvider(DependencyMatrix dsMatrix) {
        this.dsMatrix = dsMatrix;
    }

    @Override
    public int getColumnCount() {
        return dsMatrix == null ? 0 : dsMatrix.getSize();
    }

    @Override
    public Object getDataValue(int column, int row) {
        return dsMatrix == null ? "" : Integer.toString(column + 1);
    }

    @Override
    public int getRowCount() {
        return dsMatrix == null ? 0 : 1;
    }

    @Override
    public void setDataValue(int column, int row, Object value) {
        // do nothing
    }

    public void setDsMatrix(DependencyMatrix dsMatrix) {
        this.dsMatrix = dsMatrix;
    }

}
