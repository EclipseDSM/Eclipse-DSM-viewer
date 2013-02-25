package com.dsmviewer.dsmtable;

import java.awt.Dimension;
import java.util.List;

import org.dtangler.core.dsm.DsmCell;
import org.dtangler.core.dsm.DsmRow;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderSelectionListener;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.CellSelectionEvent;
import org.eclipse.swt.widgets.Composite;

import com.dsmviewer.Activator;
import com.dsmviewer.dsm.DependencyScope;
import com.dsmviewer.dsm.DsMatrix;
import com.dsmviewer.logging.Logger;
import com.dsmviewer.ui.UiHelper;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmTableController {

    private static final int ICON_SIZE = DependencyScope.PACKAGES.getDisplayIcon().getBounds().width;

    private Logger logger = Activator.getLogger(getClass());

    private DsmBodyDataProvider mainDataProvider;
    private DsmRowHeaderDataProvider rowHeaderDataProvider;
    private DsmColumnHeaderDataProvider colHeaderDataProvider;

    private DsmBodyLayer bodyLayer;
    private DsmColumnHeaderLayer columnHeaderLayer;
    private DsmRowHeaderLayer rowHeaderLayer;

    private Composite parent;
    private NatTable table;

    public DsmTableController(Composite parent) {
        this.parent = parent;
    }

    public NatTable init(DsMatrix dsMatrix) {

        mainDataProvider = new DsmBodyDataProvider(dsMatrix);
        colHeaderDataProvider = new DsmColumnHeaderDataProvider(dsMatrix);
        rowHeaderDataProvider = new DsmRowHeaderDataProvider(dsMatrix);

        bodyLayer = new DsmBodyLayer(mainDataProvider);
        columnHeaderLayer = new DsmColumnHeaderLayer(colHeaderDataProvider, bodyLayer);
        rowHeaderLayer = new DsmRowHeaderLayer(rowHeaderDataProvider, bodyLayer);

        // just for now I want to do nothing with left-upper corner. It will be a simply rectangle gray area
        DefaultCornerDataProvider cornerDataProvider =
                new DefaultCornerDataProvider(colHeaderDataProvider, rowHeaderDataProvider);

        CornerLayer cornerLayer = new CornerLayer(new DataLayer(cornerDataProvider), rowHeaderLayer, columnHeaderLayer);

        GridLayer mainLayer = new GridLayer(bodyLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

        table = new NatTable(parent, mainLayer, false);

        configureListeners();

        { // configuring layers
            mainLayer.clearConfiguration(); // do not configure parent layer, only child layers
            cornerLayer.addConfiguration(new DsmCornerLayerConfiguration());
            table.configure(); // apply all configuration tweaks to all table layers
        }

        return table;
    }

    private void configureListeners() {

        // Select the row which is a dependee by selected cell
        bodyLayer.getSelectionLayer().addLayerListener(
                new ColumnHeaderSelectionListener(columnHeaderLayer.getColHeaderLayer()) {
                    @Override
                    public void handleLayerEvent(ILayerEvent event) {
                        if (event instanceof CellSelectionEvent) {
                            CellSelectionEvent cellSelectionEvent = (CellSelectionEvent) event;
                            if (cellSelectionEvent.getSelectionLayer().getSelectedCellPositions().length == 1) {
                                int columnPosition = cellSelectionEvent.getColumnPosition();
                                if (rowHeaderLayer.getAdditionallySelectedRowIndex() != columnPosition) {
                                    rowHeaderLayer.setAdditionallySelectedRowIndex(columnPosition);
//                                logger.warn(event.toString() + "  " + columnPosition);
                                }
                            } else {
                                rowHeaderLayer.setAdditionallySelectedRowIndex(-1);
                            }
                        } else { // if not celll selected (row selection, etc.)
                            rowHeaderLayer.setAdditionallySelectedRowIndex(-1);
                        }
                    }
                }
                );

    }

    public void setDsMatrix(DsMatrix dsMatrix, boolean refresh) {
        mainDataProvider.setDsMatrix(dsMatrix);
        rowHeaderDataProvider.setDsMatrix(dsMatrix);
        colHeaderDataProvider.setDsMatrix(dsMatrix);

        int cellSize = computeMaxCellSize(dsMatrix);

        // TODO: write the better solution
        if (cellSize > 23) {
            cellSize = 23;
        }

        Dimension cellDimension = new Dimension(cellSize, cellSize);

        bodyLayer.setCellSize(cellDimension);
        columnHeaderLayer.setCellSize(cellDimension);
        rowHeaderLayer.setRowHeight(cellSize);

        int maximumHeaderWidth = computeMaximumRowHeaderWidth(dsMatrix);
        rowHeaderLayer.setHeaderWidth(maximumHeaderWidth);

        if (refresh) {
            table.refresh();
//          table.redraw();
        }

//        logger.info("DS-Matrix with size = " + dsMatrix.getSize() + " is shown successfully");
    }

    private static int computeMaximumRowHeaderWidth(DsMatrix dsMatrix) {
        int maxLength = 0;
        List<String> displayNames = dsMatrix.getDisplayNames();
        for (int i = 0; i < displayNames.size(); i++) {
            int length = displayNames.get(i).length();
            if (maxLength < length) {
                maxLength = length;
            }
        }
        return (int) (maxLength * UiHelper.DEFAULT_FONT_SIZE / 1.2) + ICON_SIZE;
    }

    private static int computeMaxCellSize(DsMatrix dsMatrix) {
        int maxLength = 0;
        List<DsmRow> rows = dsMatrix.getRows();
        // indexed loops were used to avoid creation of many unnecessary Iterator objects
        for (int i = 0; i < rows.size(); i++) {
            List<DsmCell> cells = rows.get(i).getCells();
            for (int j = 0; j < cells.size(); j++) {
                DsmCell cell = cells.get(j);
                int length = String.valueOf(cell.getDependencyWeight()).length();
                if (length > maxLength) {
                    maxLength = length;
                }
            }
        }
        return maxLength * UiHelper.DEFAULT_FONT_WIDTH + 6;
    }

    public NatTable getTable() {
        return table;
    }

    public boolean setTableFocused() {
        if (table == null) {
            return false;
        } else {
            return table.forceFocus();
        }
    }

}
