package com.dsmviewer.ui.dsmtable;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.cycleanalysis.DependencyCycle;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dsm.DsmCell;
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

import com.dsmviewer.dsm.DependencyMatrix;
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

	private static final TextPainter DIRECT_CYCLIC_DEPENDENCY_CELL_PAINTER = new TextPainter() {
		@Override
		protected Color getBackgroundColour(ILayerCell cell, IConfigRegistry configRegistry) {
			return UiHelper.COLOR_DSM_DIRECT_CYCLIC_DEPENDENCY_CELL_BG;
		};
	};

	private static final TextPainter TRANSITIVE_CYCLIC_DEPENDENCY_FROM_3_ELEMENTS_CELL_PAINTER = new TextPainter() {
		@Override
		protected Color getBackgroundColour(ILayerCell cell, IConfigRegistry configRegistry) {
			return UiHelper.COLOR_DSM_TRANSITIVE_CYCLIC_DEPENDENCY_CELL_BG;
		};
	};

	private static final TextPainter TRANSITIVE_CYCLIC_DEPENDENCY_FROM_4_ELEMENTS_CELL_PAINTER = new TextPainter() {
		@Override
		protected Color getBackgroundColour(ILayerCell cell, IConfigRegistry configRegistry) {
			return UiHelper.COLOR_DSM_TRANSITIVE_CYCLIC_DEPENDENCY_CELL_BG_LIGHT;
		};
	};

	private SelectionLayer selectionLayer;
	private Dimension cellSize = new Dimension(UiHelper.DSM_CELL_SIZE_DEFAULT, UiHelper.DSM_CELL_SIZE_DEFAULT);
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

		ICellPainter result;

		int columnIndex = cell.getColumnIndex();
		int rowIndex = cell.getRowIndex();

		if (columnIndex == rowIndex) {
			result = MAIN_DIAGONAL_CELL_PAINTER;
		} else {
			DependencyMatrix dependencyMatrix = getDependencyMatrix();
			DsmCell dsmCell = dependencyMatrix.getCell(rowIndex, columnIndex);
			if (isCycleMember(dsmCell)) {
				result = getCyclicDependencyCellPainter(colPosition, rowPosition, cell, confRegistry, dsmCell);
			} else {
				result = super.getCellPainter(colPosition, rowPosition, cell, confRegistry);
			}
		}

		return result;
	}

	private ICellPainter getCyclicDependencyCellPainter(int colPosition, int rowPosition, ILayerCell cell,
			IConfigRegistry confRegistry, DsmCell dsmCell) {

		ICellPainter result;

		int minCycleWeight = getMinCycleWeight(dsmCell);
		switch (minCycleWeight) {
		case 2: // direct cycle
			result = DIRECT_CYCLIC_DEPENDENCY_CELL_PAINTER;
			break;
		case 3: // transitive cycle from 3 elements
			result = TRANSITIVE_CYCLIC_DEPENDENCY_FROM_3_ELEMENTS_CELL_PAINTER;
			break;
		case 4: // transitive cycle from 4 elements
			result = TRANSITIVE_CYCLIC_DEPENDENCY_FROM_4_ELEMENTS_CELL_PAINTER;
			break;
		default: // ignore bigger transitive cycles
			result = super.getCellPainter(colPosition, rowPosition, cell, confRegistry);
			break;
		}

		return result;
	}

	private int getMinCycleWeight(DsmCell dsmCell) {
		int result = Integer.MAX_VALUE;

		for (DependencyCycle cycle : getAllDependencyCycles(dsmCell)) {
			int size = cycle.getElements().size() - 1;
			if (result > size) {
				result = size;
			}
		}

		return result;
	}

	public List<DependencyCycle> getAllDependencyCycles(DsmCell dsmCell) {

		List<DependencyCycle> result = new ArrayList<DependencyCycle>();

		Dependency dependency = dsmCell.getDependency();
		Set<Violation> violations = getDependencyMatrix().getViolations(dependency);
		for (Violation violation : violations) {
			if (violation instanceof DependencyCycle) {
				result.add((DependencyCycle) violation);
			}
		}
		return result;
	}

	public boolean isCycleMember(DsmCell dsmCell) {
		Dependency dependency = dsmCell.getDependency();
		Set<Violation> violations = getDependencyMatrix().getViolations(dependency);
		for (Violation violation : violations) {
			if (violation instanceof DependencyCycle) {
				return true;
			}
		}
		return false;
	}

	public DependencyMatrix getDependencyMatrix() {
		return dataProvider.getDependencyMatrix();
	}

	@Override
	public boolean isRowPositionResizable(int rowPosition) {
		return false;
	}

	@Override
	public boolean isColumnPositionResizable(int columnPosition) {
		return false;
	}

}
