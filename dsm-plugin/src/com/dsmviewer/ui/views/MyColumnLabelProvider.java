package com.dsmviewer.ui.views;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dsm.DsmRow;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class MyColumnLabelProvider extends ColumnLabelProvider {

    private int columnNumber;

    public MyColumnLabelProvider(int columnNumber) {
        this.columnNumber = columnNumber;
    }

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
}
