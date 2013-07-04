package com.dsmviewer.ui.dsmtable;

import java.awt.Dimension;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.config.DefaultColumnHeaderLayerConfiguration;

import com.dsmviewer.Activator;
import com.dsmviewer.logging.Logger;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmColumnHeaderLayer extends AbstractLayerTransform {

    @SuppressWarnings("unused")
    private Logger logger = Activator.getLogger(DsmColumnHeaderLayer.class);

    private DataLayer columnDataLayer;

    private Dimension cellSize = new Dimension(20, 10);

    private ColumnHeaderLayer colHeaderLayer;

    public DsmColumnHeaderLayer(IDataProvider columnHeaderDataProvider, DsmBodyLayer bodyLayer) {
        columnDataLayer = new DataLayer(columnHeaderDataProvider, cellSize.width, cellSize.height);
        colHeaderLayer = new ColumnHeaderLayer(columnDataLayer, bodyLayer,
                bodyLayer.getSelectionLayer(), false);
        colHeaderLayer.addConfiguration(new DefaultColumnHeaderLayerConfiguration() {
            @Override
            protected void addColumnHeaderStyleConfig() {
                addConfiguration(new DsmColumnHeaderConfiguration());
            }
        });
        setUnderlyingLayer(colHeaderLayer);
    }

    @Override
    public boolean isColumnPositionResizable(int columnPosition) {
        return false;
    }

    @Override
    public int getColumnWidthByPosition(int columnPosition) {
        return cellSize.width;
    }

    @Override
    public int getRowHeightByPosition(int rowPosition) {
        return cellSize.height;
    }

    public void setCellSize(Dimension cellDimension) {
        this.cellSize = cellDimension;
        columnDataLayer.setDefaultColumnWidth(cellDimension.width);
        columnDataLayer.setDefaultRowHeight(cellDimension.height);
    }

    public DataLayer getColumnDataLayer() {
        return columnDataLayer;
    }

    public ColumnHeaderLayer getColHeaderLayer() {
        return colHeaderLayer;
    }


}
