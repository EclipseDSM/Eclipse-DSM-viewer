package com.dsmviewer.ui.dsmtable;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dsm.DsmRow;
import org.eclipse.core.resources.IFile;
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
import org.eclipse.swt.graphics.Image;

import com.dsmviewer.Activator;
import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.dsm.DependencyScope;
import com.dsmviewer.ui.UiHelper;
import com.dsmviewer.utils.EclipseUtils;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmRowHeaderLayer extends AbstractLayerTransform {

	private static final TextPainter SELECTED_DEPENDEE_ROW_BG_PAINTER = new TextPainter() {
		@Override
		protected Color getBackgroundColour(ILayerCell cell, IConfigRegistry configRegistry) {
			return UiHelper.COLOR_DSM_SELECTED_DEPENDEE_ROW_BG;
		}
	};

	private int rowHeight = UiHelper.DSM_CELL_SIZE_DEFAULT;
	private int rowWidth = UiHelper.DSM_CELL_SIZE_DEFAULT;

	private DataLayer rowDataLayer;
	private DsmRowHeaderDataProvider rowHeaderDataProvider;

	private int selectedDependeeRowIndex = -1;

	public DsmRowHeaderLayer(DsmRowHeaderDataProvider rowHeaderDataProvider, DsmBodyLayer bodyLayer) {
		this.rowHeaderDataProvider = rowHeaderDataProvider;
		rowDataLayer = new DataLayer(rowHeaderDataProvider);
		RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(
				rowDataLayer, bodyLayer, bodyLayer.getSelectionLayer(), false);
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
		return false;
	}

	@Override
	public int getColumnWidthByPosition(int columnPosition) {
		return rowWidth;
	}

	public int getRowHeight() {
		return this.rowHeight;
	}

	public void setRowHeight(int rowHeight) {
		rowDataLayer.setDefaultRowHeight(rowHeight);
		this.rowHeight = rowHeight;
	}

	public void setWidth(int width) {
		rowDataLayer.setDefaultColumnWidth(width);
		this.rowWidth = width;
	}

	public int getSelectedDependeeRowIndex() {
		return selectedDependeeRowIndex;
	}

	public void setSelectedDenendeeRowIndex(int selectedDependeeRowIndex) {
		this.selectedDependeeRowIndex = selectedDependeeRowIndex;
	}

	public void deselectDependeeRow() {
		setSelectedDenendeeRowIndex(-1);
	}

	@Override
	public ICellPainter getCellPainter(int columnPosition, int rowPosition, ILayerCell cell,
			IConfigRegistry configRegistry) {

		final int rowIndex = cell.getRowIndex();
//        final int columnIndex = cell.getColumnIndex();

		ICellPainter textWithBgPainter;
		if (rowIndex == selectedDependeeRowIndex) {
			textWithBgPainter = SELECTED_DEPENDEE_ROW_BG_PAINTER;
		} else {
			textWithBgPainter = super.getCellPainter(columnPosition, rowPosition, cell, configRegistry);
		}

		DependencyMatrix dependencyMatrix = rowHeaderDataProvider.getDependencyMatrix();
		DsmRow dsmRow = dependencyMatrix.getRows().get(rowIndex);

		return new LineBorderDecorator(
				new CellPainterDecorator(textWithBgPainter, CellEdgeEnum.LEFT,
						new ImagePainter(getResourceImage(dsmRow)) {
							@Override
							protected Color getBackgroundColour(ILayerCell cell, IConfigRegistry configRegistry) {
								if (cell.getRowIndex() == selectedDependeeRowIndex) {
									return UiHelper.COLOR_DSM_SELECTED_DEPENDEE_ROW_BG;
								} else {
									return super.getBackgroundColour(cell, configRegistry);
								}
							}
						}));
	}

	private static Image getResourceImage(DsmRow dsmRow) {

		Dependable dependee = dsmRow.getDependee();
		DependencyScope scope = DependencyScope.parse(dependee.getScope());

		switch (scope) {
		case CLASSES:
			return getResourceImageForSource(dependee);
		case PACKAGES:
			return Activator.getImageFromRegistry("package.gif");
		default:
			return null;
		}
	}

	// TODO: ugly quick fix till my diploma is not done. This code should use Eclipse AST to get resource type
	private static Image getResourceImageForSource(Dependable dependee) {

		IFile file = EclipseUtils.getSourceFile(dependee);
		if (file != null) {
			String source = EclipseUtils.readIFileAsString(file).replaceAll("\n", " ").replace("\r", "");
			int indexOfOpeningBrace = source.indexOf("{");

			if (indexOfOpeningBrace > 0) {
				source = source.substring(0, indexOfOpeningBrace);
			}

			if (source.matches(".+ enum .+")) {
				return Activator.getImageFromRegistry("enum.gif");
			} else if (source.matches(".+ abstract.{0,} .{0,}class.+")) {
				return Activator.getImageFromRegistry("abstract_class.gif");
			} else if (source.matches(".+ interface .+")) {
				return Activator.getImageFromRegistry("interface.gif");
			} else if (source.matches(".+ @interface .+")) {
				return Activator.getImageFromRegistry("annotation.gif");
			}
			return Activator.getImageFromRegistry("class.gif");
		} else {
			
			if(dependee.getScope().toString().equals("classes")) {				
				return Activator.getImageFromRegistry("class.gif");
			} else {				
				return Activator.getImageFromRegistry("package.gif");
			}			
		}
	}

//    private static Image getResourceImage(Dependable dependable) {
//
//        IFile file = EclipseUtils.getSourceFile(dependable);
//
//        IJavaElement javaElement = JavaCore.create(file);
//        if (javaElement != null && javaElement.exists()
//                && javaElement.getElementType() == IJavaElement.COMPILATION_UNIT) {
//
//            ICompilationUnit compilationUnit = (ICompilationUnit) javaElement;
//            try {
//                IType primaryType = compilationUnit.findPrimaryType();
//                IJavaElement[] children = primaryType.getTypeRoot().getChildren();
//                for (IJavaElement child : children) {
//                    if (child instanceof SourceType) {
//                        // ....
//                        SourceType sourceType = (SourceType) child;
//                        String source = sourceType.getSource();
//                        return getResourceImage(source);
//                    }
//                }
//
//            } catch (JavaModelException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return null;
//    }

}
