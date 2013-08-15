package com.dsmviewer.ui.dsmtable.actions;

import org.dtangler.core.dsm.DsmCell;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.swt.events.MouseEvent;

import com.dsmviewer.Activator;
import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.logging.Logger;
import com.dsmviewer.ui.dsmtable.DsmBodyLayer;

public class MouseHoverOnGridAction implements IMouseAction {

	private final Logger logger = Activator.getLogger(getClass());

	@Override
	public void run(NatTable natTable, MouseEvent event) {

		int columnIndex = natTable.getColumnPositionByX(event.x) - 1;
		int rowIndex = natTable.getRowPositionByY(event.y) - 1;
//      Object valueUnderCursor = natTable.getDataValueByPosition(columnIndex, rowIndex);

		DsmBodyLayer bodyLayer = (DsmBodyLayer) ((GridLayer) natTable.getLayer()).getBodyLayer();
		DependencyMatrix dsm = bodyLayer.getDependencyMatrix();
		DsmCell cellUnderCursor = dsm.getCell(rowIndex, columnIndex);

		logger.debug("Source: " + event.getSource()
				+ ". Coordinates: " + event.x + " | " + event.y + ". Mouse button : " + event.button + "\n"
				+ "On Layer: " + bodyLayer
				+ "\n" + "Column position: " + columnIndex
				+ "\n" + "Row position: " + rowIndex
				+ "\n" + "And with dependency: " + cellUnderCursor.getDependency()
				+ "\n" + "Retriewed dep weight:" + cellUnderCursor.getDependencyWeight()
				);

	}

}
