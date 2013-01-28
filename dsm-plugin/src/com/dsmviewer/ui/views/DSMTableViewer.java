package com.dsmviewer.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dsm.DsmRow;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.dsmviewer.dtangler.DSMatrix;

public class DSMTableViewer extends TableViewer {

    /**
     * The logger.
     */
	private final Logger logger = Logger.getLogger(getClass().getName());

    // fixed sizes !
    private static final int DS_MATRIX_COLUMN_SIZE = 30;
    private static final int NAME_COLUMN_SIZE = 210;

    private List<TableViewerColumn> columns = new ArrayList<TableViewerColumn>();

    public DSMTableViewer(Composite parent, int style) {
        super(parent, style);
    }

    public void showDSMatrix(DSMatrix dsMatrix) {
        removeAllColumns();
        
        composeColumns(dsMatrix);
        
        setInput(dsMatrix.getRows());

        Control tableControl = this.getControl();
        tableControl.pack();        

        addControlListener(tableControl);
    }

    private void addControlListener(final Control tableControl) {
        this.getControl().addControlListener(new ControlListener() {

            @Override
            public void controlResized(ControlEvent arg0) {
                tableControl.pack();
				logger.log(Level.INFO, "DS-Matrix resized.");
            }

            @Override
            public void controlMoved(ControlEvent arg0) {
				logger.log(Level.INFO, "DS-Matrix moved.");
            }
        });
    }

    private void removeAllColumns() {
        for (TableViewerColumn column : columns) {
            column.getColumn().dispose();
        }
        columns.clear();
    }

    private void composeColumns(DSMatrix dsMatrix) {

        TableViewerColumn nameColumn = createTableViewerColumn("Names: ", NAME_COLUMN_SIZE, true);
        setNameColumnLabelProvider(nameColumn);
        // nameColumn.getColumn().pack();
        
        columns.add(nameColumn); // add column to list

        for (int i = 1; i <= dsMatrix.getSize(); i++) {
            // save the column number to set column headers later.
            dsMatrix.getRows().get(i - 1).getDependee().setContentCount(i);
            TableViewerColumn matrixColumn = createTableViewerColumn("" + i, DS_MATRIX_COLUMN_SIZE, false);

            matrixColumn.getColumn().setAlignment(SWT.CENTER);

            setMatrixColumnLabelProvider(matrixColumn, i);  
            
            //matrixColumn.getColumn().pack();
            columns.add(matrixColumn); // add column to list
        }
    }

	private static TableViewerColumn setNameColumnLabelProvider(TableViewerColumn column) {
        column.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                final DsmRow dsmRow = (DsmRow) element;
                final Dependable dep = dsmRow.getDependee();
                return dep.getContentCount() + ": " + dep.getDisplayName();
            }

            @Override
            public Color getBackground(Object element) {
                return new Color(Display.getCurrent(), 240, 220, 240);
            }
            
            @Override
			public Image getImage(Object obj) {
                return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DEF_PERSPECTIVE);
            }

        });
        return column;
    }

	private static TableViewerColumn setMatrixColumnLabelProvider(TableViewerColumn column, final int columnNumber) {
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                DsmRow dsmRow = (DsmRow) element;
                int dependencyWeight = dsmRow.getCells().get(columnNumber - 1).getDependencyWeight();
                String result = (dependencyWeight == 0) ? "" : Integer.toString(dependencyWeight);
                return result;
            }

            @Override
            public Color getBackground(Object element) {
                final DsmRow dsmRow = (DsmRow) element;
                final Dependable dep = dsmRow.getDependee();
                if (dep.getContentCount() == columnNumber) { // main 
                    return new Color(Display.getCurrent(), 200, 200, 200);
                }
                else {
                    return null;
                }
            }
        });
        return column;
    }

    private TableViewerColumn createTableViewerColumn(String title, int bound, boolean isResizable) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(this, SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(isResizable);
        column.setMoveable(false);
        return viewerColumn;
    }

    public List<TableViewerColumn> getColumns() {
        return columns;
    }

}
