package com.dsmviewer.ui.dsmtable;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import com.dsmviewer.dsm.DependencyMatrix;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmColumnHeaderDataProvider implements IDataProvider {

    private DependencyMatrix dependencyMatrix;

    public DsmColumnHeaderDataProvider(DependencyMatrix dsMatrix) {
        this.dependencyMatrix = dsMatrix;
    }

    @Override
    public int getColumnCount() {
        return dependencyMatrix == null ? 0 : dependencyMatrix.getSize();
    }

    @Override
    public Object getDataValue(int column, int row) {
        return dependencyMatrix == null ? "" : Integer.toString(column + 1);
    }

    @Override
    public int getRowCount() {
        return dependencyMatrix == null ? 0 : 1;
    }

    @Override
    public void setDataValue(int column, int row, Object value) {
        // do nothing
    }

    public void setDependencyMatrix(DependencyMatrix dsMatrix) {
        this.dependencyMatrix = dsMatrix;
    }

    public DependencyMatrix getDependencyMatrix() {
        return dependencyMatrix;
    }

}
