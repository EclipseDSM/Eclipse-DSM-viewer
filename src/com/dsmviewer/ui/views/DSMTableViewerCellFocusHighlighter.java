package com.dsmviewer.ui.views;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import com.dsmviewer.ui.utils.Colors;

public class DSMTableViewerCellFocusHighlighter extends FocusCellOwnerDrawHighlighter {

    DSMTableViewer tableViewer;
    
    public DSMTableViewerCellFocusHighlighter(DSMTableViewer tableViewer) {
        super(tableViewer);
        this.tableViewer = tableViewer;
    }

    protected boolean onlyTextHighlighting(ViewerCell cell) {
        return false;
    }

    protected Color getSelectedCellBackgroundColor(ViewerCell cell) {
        //return null;        
        return cell.getControl().getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE);
    }

    protected Color getSelectedCellForegroundColor(ViewerCell cell) {
        return Colors.BLACK;
    }

    protected Color getSelectedCellForegroundColorNoFocus(ViewerCell cell) {
        return Colors.DARK_BLUE;
    }

    protected Color getSelectedCellBackgroundColorNoFocus(ViewerCell cell) {
        //return null;
        return cell.getControl().getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE);
    }

    protected void focusCellChanged(ViewerCell newCell, ViewerCell oldCell) {
        super.focusCellChanged(newCell, oldCell);        
        tableViewer.setSelection(newCell, oldCell, Colors.WHITE, Colors.SELECTION);
    }

}