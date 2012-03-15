package com.dsmviewer.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DSMTableViewer extends TableViewer {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // fixed sizes !
    private final static int DS_MATRIX_COLUMN_SIZE = 30;
    
    private List<TableViewerColumn> columns = new ArrayList<TableViewerColumn>();

    public DSMTableViewer(final Composite parent, final int style) {
        super(parent, style);
    }

    public void showModel(final DSMModel dsmModel) {
        removeAllColumns();

        composeColumns(dsmModel.getLabels());
        setInput(dsmModel.getRows());

        Control tableControl = this.getControl();
        tableControl.pack();

        addControlListener(tableControl);
    }

    private void addControlListener(final Control tableControl) {
        this.getControl().addControlListener(new ControlListener() {

            @Override
            public void controlResized(final ControlEvent arg0) {
                tableControl.pack();
                logger.debug("DS-Matrix resized.");
            }

            @Override
            public void controlMoved(final ControlEvent arg0) {
                logger.debug("DS-Matrix moved.");
            }
        });
    }

    private void removeAllColumns() {
        for (TableViewerColumn column : columns) {
            column.getColumn().dispose();
        }
        columns.clear();
    }

    private void composeColumns(final ArrayList<DSMModel.Label> label) {
        for (int n = 0; n < label.size(); n++) {
            TableViewerColumn matrixColumn = createTableViewerColumn(Integer.toString(label.get(n).getNumber()),
                    DS_MATRIX_COLUMN_SIZE, false);
            matrixColumn.getColumn().setAlignment(SWT.CENTER);
            setColumnLabelProvider(matrixColumn, n);
            this.columns.add(matrixColumn);
        }
    }

    private TableViewerColumn setColumnLabelProvider(final TableViewerColumn column, final int columnNumber) {
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(final Object element) {
                final DSMModel.Row row = (DSMModel.Row) element;
                int weight = row.row.get(columnNumber);
                String result = row.number == columnNumber ? "--" : Integer.toString(weight);
                return result;
            }

            @Override
            public Color getBackground(final Object element) {
                final DSMModel.Row row = (DSMModel.Row) element;
                if (row.number == columnNumber) {
                    return new Color(Display.getCurrent(), 200, 200, 200); // diagonal
                }
                else {
                    return null;
                }
            }
        });
        return column;
    }

    private TableViewerColumn createTableViewerColumn(final String title, final int bound, final boolean isResizable) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(this, SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(isResizable);
        column.setMoveable(false);
        return viewerColumn;
    }

    public List<TableViewerColumn> getColumns() {
        return this.columns;
    }

}
