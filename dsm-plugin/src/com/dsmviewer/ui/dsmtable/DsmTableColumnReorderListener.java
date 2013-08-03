package com.dsmviewer.ui.dsmtable;

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.reorder.event.ColumnReorderEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.ColumnSelectionEvent;

import com.dsmviewer.Activator;
import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.logging.Logger;

/**
 * Marks the ColumnHeader as selected in response to a
 * {@link ColumnSelectionEvent}
 */
public class DsmTableColumnReorderListener implements ILayerListener {

	private Logger logger = Activator.getLogger(getClass());
	private DsmTableController dsmTableController;

	public DsmTableColumnReorderListener(DsmTableController dsmTableController) {
		this.dsmTableController = dsmTableController;
	}

	public void handleLayerEvent(ILayerEvent event) {

		logger.info(event.getClass().getName());

		if (event instanceof ColumnReorderEvent) {
			ColumnReorderEvent columnReorderEvent = (ColumnReorderEvent) event;

			Collection<Range> beforeFromColumnPositionRanges = columnReorderEvent.getBeforeFromColumnPositionRanges();

			if (!beforeFromColumnPositionRanges.isEmpty()) {
				Range fromColumnPositionRange = beforeFromColumnPositionRanges.iterator().next();
				int columnIndexFrom = fromColumnPositionRange.start;
				int columnIndexTo = columnReorderEvent.getBeforeToColumnPosition() - 1;

				if (columnIndexFrom != columnIndexTo) {
					DependencyMatrix dependencyMatrix = dsmTableController.getDependencyMatrix();
					dependencyMatrix.replaceElements(columnIndexFrom, columnIndexTo);
					dsmTableController.setDependencyMatrix(dependencyMatrix, true);
				}
			}
		}
	}
}
