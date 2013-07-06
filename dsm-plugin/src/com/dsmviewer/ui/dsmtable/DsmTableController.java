package com.dsmviewer.ui.dsmtable;

import java.awt.Dimension;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderSelectionListener;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.CellSelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.dsmviewer.Activator;
import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.dsm.DependencyScope;
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

    public NatTable init(DependencyMatrix dsMatrix) {

        logger.debug("Initialising with DS-matrix: " + dsMatrix);

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

        logger.debug("Initialisation complete");

        return table;
    }

    private void configureListeners() {

        // Additionally select the row which is a dependee for selected cell
        bodyLayer.getSelectionLayer().addLayerListener(
                new ColumnHeaderSelectionListener(columnHeaderLayer.getColHeaderLayer()) {
                    @Override
                    public void handleLayerEvent(ILayerEvent event) {
                        if (event instanceof CellSelectionEvent) {
                            CellSelectionEvent cellSelectionEvent = (CellSelectionEvent) event;
                            PositionCoordinate[] selectedCellPositions
                            = cellSelectionEvent.getSelectionLayer().getSelectedCellPositions();
                            if (selectedCellPositions.length == 1) {
                                int columnPosition = cellSelectionEvent.getColumnPosition();
                                PositionCoordinate selectedCellPosition = selectedCellPositions[0];
                                int selectedCellColumnIndex = selectedCellPosition.columnPosition;
                                int selectedCellRowIndex = selectedCellPositions[0].rowPosition;
                                if (selectedCellColumnIndex == selectedCellRowIndex) {
                                    rowHeaderLayer.setAdditionallySelectedRowIndex(-1);
                                } else {
                                    if (rowHeaderLayer.getAdditionallySelectedRowIndex() != columnPosition) {
                                        rowHeaderLayer.setAdditionallySelectedRowIndex(columnPosition);
                                    }
                                }
                            } else {
                                rowHeaderLayer.setAdditionallySelectedRowIndex(-1);
                            }
                        } else { // if selection is not a cell (row selection, etc.)
                            rowHeaderLayer.setAdditionallySelectedRowIndex(-1);
                        }
                    }
                });

    }

    public void setDependencyMatrix(DependencyMatrix dsMatrix, boolean refresh) {
        mainDataProvider.setDependencyMatrix(dsMatrix);
        rowHeaderDataProvider.setDependencyMatrix(dsMatrix);
        colHeaderDataProvider.setDependencyMatrix(dsMatrix);

        int cellSize = 21; // computeMaxCellSize(dsMatrix);

        // TODO: write the better solution instead of such ugly hardcoding!
//        if (cellSize > 23 || cellSize < 18) {
//            cellSize = 23;
//        }

        Dimension cellDimension = new Dimension(cellSize, cellSize);

        bodyLayer.setCellSize(cellDimension);
        columnHeaderLayer.setCellSize(cellDimension);

        int rowHeight = (int) (cellSize * 1.25);
        rowHeaderLayer.setRowHeight(rowHeight);

        int maximumHeaderWidth = computeMaximumRowHeaderWidth(dsMatrix);
        rowHeaderLayer.setHeaderWidth(maximumHeaderWidth);

        if (refresh) {
            table.refresh();
//          table.redraw();
        }

//        logger.info("DS-Matrix with size = " + dsMatrix.getSize() + " is shown successfully");
    }

    public Point getDsmTableBounds() {
        int height = bodyLayer.getWidth() + rowHeaderLayer.getWidth();
        int width = bodyLayer.getHeight();
        return new Point(height, width);
    }

    public DependencyMatrix getDependencyMatrix() {
        return mainDataProvider.getDependencyMatrix();
    }

    private static int computeMaximumRowHeaderWidth(DependencyMatrix dependencyMatrix) {
        int maxLength = 0;
        List<String> displayNames = dependencyMatrix.getDisplayNames();
        for (int i = 0; i < displayNames.size(); i++) {
            int length = displayNames.get(i).length();
            if (maxLength < length) {
                maxLength = length;
            }
        }
        return (int) (maxLength * UiHelper.DEFAULT_FONT_SIZE / 1.2) + 2 * ICON_SIZE;
    }

//    private static int computeMaxCellSize(DependencyMatrix dsMatrix) {
//        int maxLength = 0;
//        List<DsmRow> rows = dsMatrix.getRows();
//        // indexed loops were used to avoid creation of many unnecessary Iterator objects
//        for (int i = 0; i < rows.size(); i++) {
//            List<DsmCell> cells = rows.get(i).getCells();
//            for (int j = 0; j < cells.size(); j++) {
//                DsmCell cell = cells.get(j);
//                int length = String.valueOf(cell.getDependencyWeight()).length();
//                if (length > maxLength) {
//                    maxLength = length;
//                }
//            }
//        }
//        return maxLength * UiHelper.DEFAULT_FONT_WIDTH + 6;
//    }

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
