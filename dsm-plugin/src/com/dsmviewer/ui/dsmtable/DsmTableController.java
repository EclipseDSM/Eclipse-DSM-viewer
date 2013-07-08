package com.dsmviewer.ui.dsmtable;

import java.awt.Dimension;
import java.util.List;
import java.util.Stack;

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

import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.dsm.DependencyScope;
import com.dsmviewer.ui.DsmView;
import com.dsmviewer.ui.UiHelper;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmTableController {

    private static final int ICON_SIZE = DependencyScope.PACKAGES.getDisplayIcon().getBounds().width; // 16px

    private DsmBodyDataProvider mainDataProvider;
    private DsmRowHeaderDataProvider rowHeaderDataProvider;
    private DsmColumnHeaderDataProvider colHeaderDataProvider;

    private DsmBodyLayer bodyLayer;
    private DsmColumnHeaderLayer columnHeaderLayer;
    private DsmRowHeaderLayer rowHeaderLayer;

    private Composite parent;
    private NatTable table;

    private boolean alreadyInitialized = false;

    private Stack<DependencyMatrix> stack = new Stack<DependencyMatrix>();

    public DsmTableController(Composite parent) {
        this.parent = parent;
    }

    public void setDependencyMatrix(DependencyMatrix dependencyMatrix, boolean fullRefresh, boolean addToHistory) {

        if (!alreadyInitialized) {
            init(dependencyMatrix);
        }

        mainDataProvider.setDependencyMatrix(dependencyMatrix);
        rowHeaderDataProvider.setDependencyMatrix(dependencyMatrix);
        colHeaderDataProvider.setDependencyMatrix(dependencyMatrix);

        // TODO: add to plugin human-changeable properties as 'default cell size'
        int cellSize = UiHelper.DSM_CELL_SIZE_DEFAULT;

        Dimension cellDimension = new Dimension(cellSize, cellSize);

        bodyLayer.setCellSize(cellDimension);
        columnHeaderLayer.setCellSize(cellDimension);

        int rowHeight = (int) (cellSize * 1.25);
        rowHeaderLayer.setRowHeight(rowHeight);

        int maximumHeaderWidth = computeMaximumRowHeaderWidth(dependencyMatrix.getDisplayNames());
        rowHeaderLayer.setHeaderWidth(maximumHeaderWidth);

        refreshTable(fullRefresh);

        if (addToHistory) {
            stack.add(dependencyMatrix);
        }
    }

    private void init(DependencyMatrix dependencyMatrix) {

        mainDataProvider = new DsmBodyDataProvider(dependencyMatrix);
        colHeaderDataProvider = new DsmColumnHeaderDataProvider(dependencyMatrix);
        rowHeaderDataProvider = new DsmRowHeaderDataProvider(dependencyMatrix);

        bodyLayer = new DsmBodyLayer(mainDataProvider);
        columnHeaderLayer = new DsmColumnHeaderLayer(colHeaderDataProvider, bodyLayer);
        rowHeaderLayer = new DsmRowHeaderLayer(rowHeaderDataProvider, bodyLayer);

        // just for now I want to do nothing with left-upper corner. It will be a simply rectangle gray area
        DefaultCornerDataProvider cornerDataProvider =
                new DefaultCornerDataProvider(colHeaderDataProvider, rowHeaderDataProvider);

        CornerLayer cornerLayer = new CornerLayer(new DataLayer(cornerDataProvider), rowHeaderLayer, columnHeaderLayer);

        GridLayer mainLayer = new GridLayer(bodyLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

        table = new NatTable(parent, mainLayer, false);

        { // configuring layers
            mainLayer.clearConfiguration(); // do not configure parent layer, only child layers
            cornerLayer.addConfiguration(new DsmCornerLayerConfiguration());
            table.configure(); // apply all configuration tweaks to all table layers
        }

        configureListeners();

        alreadyInitialized = true;
    }

    private void configureListeners() {

        // Select the row which represents the dependee for selected cell
        bodyLayer.getSelectionLayer().addLayerListener(
                new ColumnHeaderSelectionListener(columnHeaderLayer.getColHeaderLayer()) {
                    @Override
                    public void handleLayerEvent(ILayerEvent event) {
                        handleColumnHeaderSelectionEvent(event);
                    }
                });

    }

    private void handleColumnHeaderSelectionEvent(ILayerEvent event) {
        if (event instanceof CellSelectionEvent) {
            CellSelectionEvent cellSelectionEvent = (CellSelectionEvent) event;
            PositionCoordinate[] selectedCellPositions = cellSelectionEvent.getSelectionLayer()
                    .getSelectedCellPositions();
            if (selectedCellPositions.length == 1) {
                int columnPosition = cellSelectionEvent.getColumnPosition();
                PositionCoordinate selectedCellPosition = selectedCellPositions[0];
                int selectedCellColumnIndex = selectedCellPosition.columnPosition;
                int selectedCellRowIndex = selectedCellPositions[0].rowPosition;
                if (selectedCellColumnIndex == selectedCellRowIndex) {
                    rowHeaderLayer.deselectDependeeRow();
                } else {
                    if (rowHeaderLayer.getSelectedDependeeRowIndex() != columnPosition) {
                        rowHeaderLayer.setSelectedDenendeeRowIndex(columnPosition);
                    }
                }
            } else {
                rowHeaderLayer.deselectDependeeRow();
            }
        } else { // if selection is not a single cell (range selection, etc.)
            rowHeaderLayer.deselectDependeeRow();
        }
    }

    public void refreshTable(boolean layoutAll) {
        table.refresh();
        parent.layout(layoutAll);
    }

    public Point getDsmTableBounds() {
        int height = bodyLayer.getWidth() + rowHeaderLayer.getWidth();
        int width = bodyLayer.getHeight();
        return new Point(height, width);
    }

    public DependencyMatrix getDependencyMatrix() {
        return mainDataProvider.getDependencyMatrix();
    }

    private static int computeMaximumRowHeaderWidth(List<String> displayNames) {
        int maxLength = 0;
        for (int i = 0; i < displayNames.size(); i++) {
            int length = displayNames.get(i).length();
            if (maxLength < length) {
                maxLength = length;
            }
        }
        return (int) (maxLength * UiHelper.DEFAULT_FONT_SIZE / 1.2) + 2 * ICON_SIZE;
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

    // TODO:
//    public void doStepForward() {
//        
//    }

    public void doStepBackWard() {
        if (stack.size() > 1) {
            DependencyMatrix dependencyMatrix = stack.get(stack.size() - 2);
            setDependencyMatrix(dependencyMatrix, false, false);
            DsmView.getCurrent().updateSortActionsState(dependencyMatrix.getOrdering());
            stack.pop();
        } else {
            // TODO: disable 'backward' action button
        }
    }

    public void crearStack() {
        stack.clear();
    }

}
