package com.dsmviewer.dsmtable;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import com.dsmviewer.dsm.DsMatrix;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmColumnHeaderDataProvider implements IDataProvider {

    private DsMatrix dsMatrix;

    public DsmColumnHeaderDataProvider(DsMatrix dsMatrix) {
        this.dsMatrix = dsMatrix;
    }

    @Override
    public int getColumnCount() {
        return dsMatrix == null ? 0 : dsMatrix.getSize();
    }

    @Override
    public Object getDataValue(int i, int j) {
        return dsMatrix == null ? "" : Integer.toString(i);
    }

    @Override
    public int getRowCount() {
        return dsMatrix == null ? 0 : 1;
    }

    @Override
    public void setDataValue(int i, int j, Object value) {
        // do nothing
    }

    public void setDsMatrix(DsMatrix dsMatrix) {
        this.dsMatrix = dsMatrix;
    }

}
