package com.dsmviewer.ui.dsmtable;

import java.awt.Dimension;
import java.util.List;
import java.util.Stack;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;

import com.dsmviewer.dsm.DependencyMatrix;
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

		// TODO: add human-changeable 'default cell size' plugin property
		int cellSize = UiHelper.DSM_CELL_SIZE_DEFAULT;

		Dimension cellDimension = new Dimension(cellSize, cellSize);
		bodyLayer.setCellSize(cellDimension);
		columnHeaderLayer.setCellSize(cellDimension);
		rowHeaderLayer.setRowHeight((int) (cellSize * 1.2));

		List<String> displayNames = dependencyMatrix.getDisplayNames();
		int maxTextExtent = UiHelper.computeMaxTextExtent(displayNames, dsmTable.getShell());
		rowHeaderLayer.setWidth(maxTextExtent + 2 * UiHelper.ICON_SIZE + 20);

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

		// Select row which represents the dependee for cell which is currently being selected
		bodyLayer.getSelectionLayer().addLayerListener(
				new DependeeRowSelectionChangedListener(columnHeaderLayer.getColHeaderLayer(), rowHeaderLayer));

		// Replace columns / rows in UI should cause replacing of related values in model
		bodyLayer.addLayerListener(new DsmTableColumnReorderListener(this));

		dsmTable.addListener(SWT.MouseHover, new Listener() {

			@Override
			public void handleEvent(Event event) {
//				System.out.println("Hower!");
//				// TODO: use https://sites.google.com/site/javatipstocode/how-to/how-to-display-hover-text-in-swt
//				// to display help dialog on hower
			}
		});

	}

	public void refreshTable(boolean changed) {
		dsmTable.refresh();
		parent.layout(changed);
	}

//    public int getDsmTableWidth() {        
//        return bodyLayer.getWidth() + rowHeaderLayer.getWidth();
//    }    
//    public int getDsmTableHeight() {        
//    	return bodyLayer.getHeight() + columnHeaderLayer.getHeight();
//    }
//    public int getDsmTablePreferredWidth() {        
//    	return bodyLayer.getPreferredWidth() + rowHeaderLayer.getPreferredWidth();
//    }    
//    public int getDsmTablePreferredHeight() {        
//    	return bodyLayer.getPreferredHeight() + columnHeaderLayer.getPreferredHeight();
//    }

	public void setDsmTableBounds(Rectangle bounds) {
		dsmTable.setBounds(bounds);
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
