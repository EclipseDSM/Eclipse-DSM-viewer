package com.dsmviewer.ui.dsmtable;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import com.dsmviewer.dsm.DependencyMatrix;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmColumnHeaderDataProvider implements IDataProvider {

    private DependencyMatrix dependencyMatrix;

    public DsmColumnHeaderDataProvider(DependencyMatrix dependencyMatrix) {
        this.dependencyMatrix = dependencyMatrix;
    }

    @Override
    public int getColumnCount() {
        return dependencyMatrix.getSize();
    }

    @Override
    public Object getDataValue(int column, int row) {
        return Integer.toString(column + 1);
    }

    @Override
    public int getRowCount() {
        return 1;
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
