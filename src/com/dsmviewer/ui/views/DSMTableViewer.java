package com.dsmviewer.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dsmviewer.ui.utils.Colors;
import com.dsmviewer.ui.views.DSMModel.Label;
import com.dsmviewer.ui.views.DSMModel.Row;

public class DSMTableViewer extends TableViewer {

    private Color [][] cellsColors; 

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // TODO: fixed sizes !
    private final static int DS_MATRIX_COLUMN_SIZE = 33;

    private List<TableViewerColumn> columns = new ArrayList<TableViewerColumn>();

    private Table table;
    
    private int selectedColumnIndex = -1;

    public DSMTableViewer(final Composite parent) {
        super(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        this.setUseHashlookup(true);
        this.setContentProvider(new DSMViewContentProvider());

        addControlListener(getControl());

        this.table = this.getTable();
        this.table.setHeaderVisible(true);
        this.table.setLinesVisible(true);
        this.table.setToolTipText("DS-Matrix");  
        
        logger.debug("DSMTableViewer was composed.");
    }

    public void setDSMatrix(final DSMModel dsmModel) {
        removeAllColumns();
        Label[] labels = dsmModel.getLabels();
        
        int dsMatrixSize = labels.length;
        cellsColors = new Color [dsMatrixSize][dsMatrixSize];

        for(int i=0;i<dsMatrixSize; i++){
            for(int j=0; j<dsMatrixSize; j++){
                cellsColors[i][j] = Colors.WHITE;
            }
        }
        composeColumns(labels);
        setInput(dsmModel.getRows());

        // table.pack();
        // TODO: notify resize listeners to fire scrollbars!   
    }

    private void addControlListener(final Control tableControl) {
        this.getControl().addControlListener(new ControlListener() {

            public void controlResized(final ControlEvent arg0) {
            // table.pack();
            // TODO: notify resize listeners to fire scrollbars!
                logger.debug("DS-Matrix was resized.");
            }

            public void controlMoved(final ControlEvent arg0) {
                logger.debug("DS-Matrix was moved.");
            }
        });        
    }

    private void removeAllColumns() {
        for (TableViewerColumn column : columns) {
            column.getColumn().dispose();
        }
        columns.clear();
    }

    private void composeColumns(Label[] labels) {
        for (int n = 0; n < labels.length; n++) {
            TableViewerColumn matrixColumn = createTableViewerColumn(Integer.toString(labels[n].getNumber()+1),
                    DS_MATRIX_COLUMN_SIZE, false);
            matrixColumn.getColumn().setAlignment(SWT.CENTER);
            setColumnLabelProvider(matrixColumn, n);
            this.columns.add(matrixColumn);
        }

        // adding the ugly redundant column to avoid last column auto-resizing 
        TableViewerColumn lastColumn = createTableViewerColumn("",1, false);
        lastColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(final Object element) {
                return null;
            }
        });
        this.columns.add(lastColumn);

    }

    private TableViewerColumn setColumnLabelProvider(final TableViewerColumn column, final int columnNumber) {
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(final Object element) {
                final Row row = (Row) element;
                int weight = row.getRow()[columnNumber];
                String result = row.number == columnNumber ? "--" : Integer.toString(weight);
                return result;
            }

            @Override
            public Color getBackground(final Object element) {
                final Row row = (Row) element;
                if (row.number == columnNumber) { // set main diagonal cells color
                    return Colors.LIGHT_GRAY;
                }
                else {
                    return cellsColors[row.number][columnNumber];
                }
            }
        });
        return column;
    }

    private TableViewerColumn createTableViewerColumn(final String title, final int width, final boolean isResizable) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(this, SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(width);
        column.setResizable(isResizable);
        column.setMoveable(false);
        return viewerColumn;
    }

    public List<TableViewerColumn> getColumns() {
        return this.columns;
    }

    public ScrollBar getTableVerticalBar() {
        return this.table.getVerticalBar();
    }
    
    public int getSelectionIndex() {
        int selectionIndex = this.table.getSelectionIndex();        
        return selectionIndex;
    }

    public void setSelectionIndex(int index) {
        this.setSelection(new StructuredSelection(this.getElementAt(index)), true);
    }
    
    public int getRowIndex(ViewerCell cell) {
        int result = 0;
        Row row = (Row) cell.getElement();
        for (int i = 0; i < this.doGetItemCount(); i++) {
            if (this.getElementAt(i).equals(row)) {
                result = i;
                break;
            }
        }
        return result;
    }

    public void setCellColor(int rowindex, int columnIndex, Color color) {
        cellsColors [rowindex][columnIndex] = color;
    }

    public void setRowColor(int rowIndex, Color color) {
        for(int j=0; j<table.getItemCount(); j++) {
            cellsColors[rowIndex][j] = color;
        }
    }

    public void setColumnColor(int columnIndex, Color color) {
        for (int i = 0; i < table.getItemCount(); i++) {
            cellsColors[i][columnIndex] = color;
        }
    }

    public void selectCell(ViewerCell newCell, ViewerCell oldCell,
            Color defaultColor, Color selectionColor) {

        if (oldCell != null && !oldCell.equals(newCell)) {
            
            final int oldColIndex = oldCell.getVisualIndex();
            final int newColIndex = newCell.getVisualIndex();
            final int oldRowIndex = getRowIndex(oldCell);
            final int newRowIndex = getRowIndex(newCell);

            final boolean rowChanged = oldRowIndex != newRowIndex;
            final boolean columnChanged = oldColIndex != newColIndex;

            if (columnChanged) {
                setColumnColor(oldColIndex, defaultColor);
                setColumnColor(newColIndex, selectionColor);
                selectedColumnIndex = newColIndex;
            } else {
                setCellColor(oldRowIndex, oldColIndex, selectionColor); 
            }
            
            if (rowChanged) {
                setRowColor(oldRowIndex, defaultColor);
                setRowColor(newRowIndex, selectionColor);
            } else {
                setCellColor(oldRowIndex, oldColIndex, selectionColor);                
            }   

            setCellColor(oldRowIndex, newColIndex, selectionColor);

            // selected cell colorizing            
            setCellColor(newRowIndex, newColIndex, Colors.SELECTION);
            
            DSMView.highlightTreeItem();
            logger.debug("Selected cell: ["+newRowIndex+"; "+newColIndex+"].");
        }
        this.refresh();
    }

    
    public void selectCell(int row, int column, Color defaultColor, Color selectionColor) {

        for (int i = 0; i < this.getSize(); i++) {
            for (int j = 0; j < this.getSize(); j++) {
                if (i == row || j == column) {
                    cellsColors[i][j] = selectionColor;
                }
                else {
                    if (i == row && j == column) {
                        cellsColors[i][j] = Colors.SELECTION;
                        DSMView.highlightTreeItem();
                        logger.debug("Selected cell: [" + i + "; " + j + "].");
                    } else {
                        cellsColors[i][j] = defaultColor;
                    }
                }
            }
        }
        this.refresh();
    }
    
    /**
     * Selects a cell with given row number in selected column or select [row;row] cell if there are no selected column
     * in table.
     */
    public void selectCell(int row, Color defaultColor, Color selectionColor) {
        int column = (selectedColumnIndex==-1)? row: selectedColumnIndex;
        selectCell(row, column, defaultColor, selectionColor);
    }
    
    public int getSize() {
        return this.doGetItemCount();
    }

    public int getSelectedColumnIndex(){
        return selectedColumnIndex;
    }
    
}
