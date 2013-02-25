package com.dsmviewer.dsmtable;

import java.awt.Dimension;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import com.dsmviewer.dsm.DsMatrix;
import com.dsmviewer.ui.UiHelper;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmBodyLayer extends AbstractLayerTransform {

    private static final String MAIN_DIAGONAL_SIGN = "-";

    private static final TextPainter MAIN_DIAGONAL_CELL_PAINTER = new TextPainter() {
        @Override
        protected String getTextToDisplay(ILayerCell cell, GC gc, int availableLength, String text) {
            return MAIN_DIAGONAL_SIGN;
        }

        @Override
        protected Color getBackgroundColour(ILayerCell cell, IConfigRegistry configRegistry) {
            return UiHelper.COLOR_DSM_DIAGONAL_CELL_BG;
        };
    };

    private static final TextPainter NON_VALID_CELL_PAINTER = new TextPainter() {
        @Override
        protected Color getBackgroundColour(ILayerCell cell, IConfigRegistry configRegistry) {
            return UiHelper.COLOR_DSM_NON_VALID_CELL_BG;
        };
    };

    private SelectionLayer selectionLayer;
    private Dimension cellSize = new Dimension(20, 10);
    private DataLayer dataLayer;

    private DsmBodyDataProvider dataProvider;

    public DsmBodyLayer(DsmBodyDataProvider dataProvider) {
        this.dataProvider = dataProvider;
        this.dataLayer = new DataLayer(dataProvider);
        ColumnReorderLayer columnReorderLayer = new ColumnReorderLayer(dataLayer);
        ColumnHideShowLayer columnHideShowLayer = new ColumnHideShowLayer(columnReorderLayer);
        selectionLayer = new SelectionLayer(columnHideShowLayer);
        selectionLayer.addConfiguration(new DsmSelectionLayerConfiguration());

        ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);
        setUnderlyingLayer(viewportLayer);
        addConfiguration(new DsmBodyLayerConfiguration());
    }

    public SelectionLayer getSelectionLayer() {
        return selectionLayer;
    }

    @Override
    public int getColumnWidthByPosition(int columnPosition) {
        return cellSize.width;
    }

    @Override
    public int getRowHeightByPosition(int rowPosition) {
        return cellSize.height;
    }

    public void setCellSize(Dimension cellSize) {
        dataLayer.setDefaultColumnWidth(cellSize.width);
        dataLayer.setDefaultRowHeight(cellSize.height);
        this.cellSize = cellSize;
    }

    @Override
    public ICellPainter getCellPainter(int colPosition, int rowPosition, ILayerCell cell, IConfigRegistry confRegistry) {
        int columnIndex = cell.getColumnIndex();
        int rowIndex = cell.getRowIndex();
        if (columnIndex == rowIndex) {
            return MAIN_DIAGONAL_CELL_PAINTER;
        } else if (getDsMatrix().hasViolations(rowIndex, columnIndex)) {
            return NON_VALID_CELL_PAINTER;
        } else {
            return super.getCellPainter(colPosition, rowPosition, cell, confRegistry);
        }
    }

    public DsMatrix getDsMatrix() {
        return dataProvider.getDsMatrix();
    }

}
