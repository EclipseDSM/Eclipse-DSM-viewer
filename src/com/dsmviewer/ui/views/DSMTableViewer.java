package com.dsmviewer.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.dtangler.core.dsm.DsmRow;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import com.dsmviewer.dtangler.DSMatrix;

public class DSMTableViewer extends TableViewer {
    
    private List<TableViewerColumn> columns = new ArrayList<TableViewerColumn>();
    
    public DSMTableViewer(Composite parent, int style) {
        super(parent, style); 
    }

    public void showDSMatrix(DSMatrix dsMatrix) {        
        removeAllColumns();
        composeColumns(dsMatrix);

        setInput(dsMatrix.getRows());
    }
    
    private void removeAllColumns(){
        for(TableViewerColumn column: columns){
            column.getColumn().dispose();            
        }
        columns.clear();
    }

    private void composeColumns(DSMatrix dsMatrix) {
        TableViewerColumn nameColumn = createTableViewerColumn("Names: ", 200, true);       
        this.columns.add(nameColumn);
        nameColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                DsmRow dsmRow = (DsmRow) element;
                return dsmRow.getDependee().getDisplayName();
            }
        });

        for (int i = 1; i <= dsMatrix.getSize(); i++) {           
            // save the column number to set column headers later.
            dsMatrix.getRows().get(i-1).getDependee().setContentCount(i);
            TableViewerColumn matrixColumn = createTableViewerColumn("" + i, 35, true);
            this.columns.add(matrixColumn);
            matrixColumn.setLabelProvider(new ColumnLabelProvider() {
                @Override
                public String getText(Object element) {
                    DsmRow dsmRow = (DsmRow) element;
                    return Integer.toString(dsmRow.getCells().get(0).getDependencyWeight());
                }
            });
        }
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
    
    public List<TableViewerColumn> getColumns(){
        return this.columns;
    }
    
}
