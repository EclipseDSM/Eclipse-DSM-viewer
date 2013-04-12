package com.dsmviewer.dsmtable;

import org.dtangler.core.dsm.DsmRow;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import com.dsmviewer.dsm.DsMatrix;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmBodyDataProvider implements IDataProvider {

    private static final String EMPTY_STRING = "";

    private DsMatrix dsMatrix;

    public DsmBodyDataProvider(DsMatrix dsMatrix) {
        this.dsMatrix = dsMatrix;
    }

    @Override
    public int getColumnCount() {
        return dsMatrix == null ? 0 : dsMatrix.getSize();
    }

    @Override
    public Object getDataValue(int i, int j) {
        return dsMatrix == null ? EMPTY_STRING : getValue(i, j);
    }

    private String getValue(int i, int j) {
        DsmRow dsmRow = dsMatrix.getRows().get(i);
        int dependencyWeight = dsmRow.getCells().get(j).getDependencyWeight();
        String depWeight = (dependencyWeight == 0) ? EMPTY_STRING : Integer.toString(dependencyWeight);
        return depWeight;
    }

    @Override
    public int getRowCount() {
        return dsMatrix == null ? 0 : dsMatrix.getSize();
    }

    @Override
    public void setDataValue(int i, int j, Object value) {
        // do nothing
    }

    public void setDsMatrix(DsMatrix dsMatrix) {
        this.dsMatrix = dsMatrix;
    }

    public DsMatrix getDsMatrix() {
        return dsMatrix;
    }

}
