package com.dsmviewer.dsmtable;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import com.dsmviewer.Activator;
import com.dsmviewer.dsm.DsMatrix;
import com.dsmviewer.logging.Logger;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmRowHeaderDataProvider implements IDataProvider {

    @SuppressWarnings("unused")
    private Logger logger = Activator.getLogger(getClass());

    private DsMatrix dsMatrix;
    private List<String> displayNames;

    public DsmRowHeaderDataProvider(DsMatrix dsMatrix) {
        this.dsMatrix = dsMatrix;
    }

    @Override
    public int getColumnCount() {
        return dsMatrix == null ? 0 : 1;
    }

    @Override
    public Object getDataValue(int i, int j) {
        return dsMatrix == null ? "" : displayNames.get(j);
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
        displayNames = dsMatrix.getDisplayNames();
    }

    public DsMatrix getDsMatrix() {
        return dsMatrix;
    }

}