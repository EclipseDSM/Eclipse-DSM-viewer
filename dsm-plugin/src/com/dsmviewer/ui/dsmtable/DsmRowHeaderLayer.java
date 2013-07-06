package com.dsmviewer.ui.dsmtable;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.config.DefaultRowHeaderLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.LineBorderDecorator;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.swt.graphics.Color;

import com.dsmviewer.dsm.DependencyLocation;
import com.dsmviewer.dsm.DependencyScope;
import com.dsmviewer.ui.UiHelper;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmRowHeaderLayer extends AbstractLayerTransform {

    private static final TextPainter ADDITIONAL_SELECTED_ROW_BG_PAINTER = new TextPainter() {
        @Override
        protected Color getBackgroundColour(ILayerCell cell, IConfigRegistry configRegistry) {
            return UiHelper.COLOR_DSM_ADDITIONALLY_SELECTED_ROW_BG;
        }
    };

    private int rowHeight;
    private int headerWidth;

    private DataLayer rowDataLayer;
    private DsmRowHeaderDataProvider rowHeaderDataProvider;

    private int additionallySelectedRowIndex = -1;

    public DsmRowHeaderLayer(DsmRowHeaderDataProvider rowHeaderDataProvider, DsmBodyLayer bodyLayer) {
        this.rowHeaderDataProvider = rowHeaderDataProvider;
        rowDataLayer = new DataLayer(rowHeaderDataProvider);
        RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(rowDataLayer, bodyLayer, bodyLayer.getSelectionLayer(),
                false);
        rowHeaderLayer.addConfiguration(new DefaultRowHeaderLayerConfiguration() {
            @Override
            protected void addRowHeaderStyleConfig() {
                addConfiguration(new DsmRowHeaderConfiguration());
            }
        });
        setUnderlyingLayer(rowHeaderLayer);
    }

    @Override
    public boolean isRowPositionResizable(int rowPosition) {
        return false;
    }

    @Override
    public boolean isColumnPositionResizable(int columnPosition) {
        return true;
    }

    @Override
    public int getColumnWidthByPosition(int columnPosition) {
        return headerWidth;
    }

    public int getRowHeight() {
        return this.rowHeight;
    }

    public void setRowHeight(int rowHeight) {
        rowDataLayer.setDefaultRowHeight(rowHeight);
        this.rowHeight = rowHeight;
    }

    public void setHeaderWidth(int headerWidth) {
        rowDataLayer.setDefaultColumnWidth(headerWidth);
        this.headerWidth = headerWidth;
    }

    public int getAdditionallySelectedRowIndex() {
        return additionallySelectedRowIndex;
    }

    public void setAdditionallySelectedRowIndex(int additionallySelectedRowPos) {
        this.additionallySelectedRowIndex = additionallySelectedRowPos;
    }

    @Override
    public ICellPainter getCellPainter(int columnPosition, int rowPosition, ILayerCell cell,
            IConfigRegistry configRegistry) {

        final int rowIndex = cell.getRowIndex();
        final int columnIndex = cell.getColumnIndex();

        ICellPainter textWithBgPainter;
        if (rowIndex == additionallySelectedRowIndex) {
            textWithBgPainter = ADDITIONAL_SELECTED_ROW_BG_PAINTER;
        } else {
            textWithBgPainter = super.getCellPainter(columnPosition, rowPosition, cell, configRegistry);
        }

        DependencyScope scope = rowHeaderDataProvider.getDependencyMatrix().getScope(rowIndex, columnIndex,
                DependencyLocation.DEPENDEE);

        return new LineBorderDecorator(
                new CellPainterDecorator(textWithBgPainter, CellEdgeEnum.LEFT,
                        new ImagePainter(scope.getDisplayIcon()) {
                            @Override
                            protected Color getBackgroundColour(ILayerCell cell, IConfigRegistry configRegistry) {
                                if (cell.getRowIndex() == additionallySelectedRowIndex) {
                                    return UiHelper.COLOR_DSM_ADDITIONALLY_SELECTED_ROW_BG;
                                } else {
                                    return super.getBackgroundColour(cell, configRegistry);
                                }
                            }
                        }));
    }

}
