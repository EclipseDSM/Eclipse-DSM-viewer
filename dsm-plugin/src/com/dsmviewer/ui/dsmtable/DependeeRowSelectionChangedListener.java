package com.dsmviewer.ui.dsmtable;

import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderSelectionListener;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.CellSelectionEvent;

public class DependeeRowSelectionChangedListener extends ColumnHeaderSelectionListener {

	private DsmRowHeaderLayer rowHeaderLayer;

	public DependeeRowSelectionChangedListener(ColumnHeaderLayer columnHeaderLayer, DsmRowHeaderLayer rowHeaderLayer) {
		super(columnHeaderLayer);
		this.rowHeaderLayer = rowHeaderLayer;
	}

	@Override
	public void handleLayerEvent(ILayerEvent event) {
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

}
