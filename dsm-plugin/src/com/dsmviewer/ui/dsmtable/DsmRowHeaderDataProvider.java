package com.dsmviewer.ui.dsmtable;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import com.dsmviewer.Activator;
import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.logging.Logger;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmRowHeaderDataProvider implements IDataProvider {

    @SuppressWarnings("unused")
    private Logger logger = Activator.getLogger(getClass());

    private DependencyMatrix dependencyMatrix;
    private List<String> displayNames;

    public DsmRowHeaderDataProvider(DependencyMatrix dsMatrix) {
        this.dependencyMatrix = dsMatrix;
    }

    @Override
    public int getColumnCount() {
        return dependencyMatrix == null ? 0 : 1;
    }

    @Override
    public Object getDataValue(int columnIndex, int rowIndex) {
        return dependencyMatrix == null ? "" : MessageFormat.format("{0} {1}",
                String.valueOf(rowIndex + 1), displayNames.get(rowIndex));
    }

    @Override
    public int getRowCount() {
        return dependencyMatrix == null ? 0 : dependencyMatrix.getSize();
    }

    @Override
    public void setDataValue(int i, int j, Object value) {
        // do nothing
    }

    public void setDependencyMatrix(DependencyMatrix dsMatrix) {
        this.dependencyMatrix = dsMatrix;
        displayNames = dsMatrix.getDisplayNames();
    }

    public DependencyMatrix getDependencyMatrix() {
        return dependencyMatrix;
    }

}