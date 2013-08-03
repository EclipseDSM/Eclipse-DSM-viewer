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
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.CellSelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.dsmviewer.Activator;
import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.logging.Logger;
import com.dsmviewer.ui.DsmView;
import com.dsmviewer.ui.UiHelper;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmTableController {

    private NatTable dsmTable;

    private Composite parent;

    private DsmBodyDataProvider mainDataProvider;
    private DsmRowHeaderDataProvider rowHeaderDataProvider;
    private DsmColumnHeaderDataProvider colHeaderDataProvider;

    private DsmBodyLayer bodyLayer;
    private DsmColumnHeaderLayer columnHeaderLayer;
    private DsmRowHeaderLayer rowHeaderLayer;

    private boolean alreadyInitialized = false;

    private Stack<DependencyMatrix> stack = new Stack<DependencyMatrix>();

    public DsmTableController(Composite parent) {
        this.parent = parent;
    }

    public void setDependencyMatrix(DependencyMatrix dependencyMatrix, boolean addToNavigationHistory) {
        internalSetDependencyMatrix(dependencyMatrix);
        if (addToNavigationHistory) {
            stack.add(dependencyMatrix);
        }
    }

    private void internalSetDependencyMatrix(DependencyMatrix dependencyMatrix) {

        if (!alreadyInitialized) {
            init(dependencyMatrix);
        }

        mainDataProvider.setDependencyMatrix(dependencyMatrix);
        rowHeaderDataProvider.setDependencyMatrix(dependencyMatrix);
        colHeaderDataProvider.setDependencyMatrix(dependencyMatrix);

        // TODO: add human-changeable 'default cell size' property to plugin
        int cellSize = UiHelper.DSM_CELL_SIZE_DEFAULT;

        Dimension cellDimension = new Dimension(cellSize, cellSize);
        bodyLayer.setCellSize(cellDimension);
        columnHeaderLayer.setCellSize(cellDimension);
        rowHeaderLayer.setRowHeight((int) (cellSize * 1.2));

        List<String> displayNames = dependencyMatrix.getDisplayNames();
        int maxTextExtent = UiHelper.computeMaxTextExtent(displayNames, dsmTable.getShell());
        rowHeaderLayer.setWidth(maxTextExtent + 2 * UiHelper.ICON_SIZE + 10);

        refreshTable(true);
    }

    private void init(DependencyMatrix dependencyMatrix) {

        mainDataProvider = new DsmBodyDataProvider(dependencyMatrix);
        colHeaderDataProvider = new DsmColumnHeaderDataProvider(dependencyMatrix);
        rowHeaderDataProvider = new DsmRowHeaderDataProvider(dependencyMatrix);

        bodyLayer = new DsmBodyLayer(mainDataProvider);
        columnHeaderLayer = new DsmColumnHeaderLayer(colHeaderDataProvider, bodyLayer);
        rowHeaderLayer = new DsmRowHeaderLayer(rowHeaderDataProvider, bodyLayer);

        // just for now I want to do nothing with left-upper corner. It will be the simple gray rectangle
        DefaultCornerDataProvider cornerDataProvider =
                new DefaultCornerDataProvider(colHeaderDataProvider, rowHeaderDataProvider);

        CornerLayer cornerLayer = new CornerLayer(new DataLayer(cornerDataProvider), rowHeaderLayer, columnHeaderLayer);

        GridLayer mainLayer = new GridLayer(bodyLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

        dsmTable = new NatTable(parent, mainLayer, false);

        { // configuring layers
            mainLayer.clearConfiguration(); // do not configure parent layer, only child layers
            cornerLayer.addConfiguration(new DsmCornerLayerConfiguration());
            dsmTable.configure(); // apply all configuration tweaks to all table layers recursively 
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
    
        
        // Replace columns / rows in UI should cause replacing of related values in model
        bodyLayer.addLayerListener(new DsmTableColumnReorderListener(this));

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

    public void refreshTable(boolean changed) {
        dsmTable.refresh();
        parent.layout(changed);
    }

    public Point getDsmTableSize() {
        int height = bodyLayer.getWidth() + rowHeaderLayer.getWidth();
        int width = bodyLayer.getHeight() + columnHeaderLayer.getHeight();
        return new Point(height, width);
    }

    public void setDsmTableBounds(Point bounds) {
        dsmTable.setBounds(bounds.x, bounds.y, bounds.x, bounds.y);
    }

    public DependencyMatrix getDependencyMatrix() {
        return mainDataProvider == null ? null : mainDataProvider.getDependencyMatrix();
    }

    public NatTable getTable() {
        return dsmTable;
    }

    public boolean setTableFocused() {
        if (dsmTable == null) {
            return false;
        } else {
            return dsmTable.forceFocus();
        }
    }

    public void doStepBackWard() {
        if (stack.size() > 1) {
            DependencyMatrix dependencyMatrix = stack.get(stack.size() - 2);
            setDependencyMatrix(dependencyMatrix, false);
            DsmView.getCurrent().updateSortActionsState(dependencyMatrix.getOrdering());
            stack.pop();
        } else {
            // TODO: disable 'backward' action button
        }
    }

    public void clearHistory() {
        stack.clear();
    }

}
