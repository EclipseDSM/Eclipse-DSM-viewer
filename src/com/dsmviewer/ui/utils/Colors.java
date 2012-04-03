package com.dsmviewer.ui.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public final class Colors {

    public static final Color DEFAULT = SWTResourceManager.getColor(SWT.COLOR_WHITE);
    public static final Color SELECTION = SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION);
    public static final Color DARK_BLUE = SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE);
    public static final Color WHITE = SWTResourceManager.getColor(SWT.COLOR_WHITE);
    public static final Color LIGHT_GRAY = new Color(Display.getDefault(),200,200,200);
    public static final Color BLACK = SWTResourceManager.getColor(SWT.COLOR_BLACK);
    
    
    public static Color getLighterColor(Color inputColor, int intesity) {
        return new Color(Display.getDefault(), Colors.SELECTION.getRed() + intesity,
                Colors.SELECTION.getGreen() + intesity,
                Colors.SELECTION.getBlue() + intesity);
    }

    public static Color getDarkerColor(Color inputColor, int intesity) {
        return new Color(Display.getDefault(), Colors.SELECTION.getRed() - intesity,
                Colors.SELECTION.getGreen() - intesity,
                Colors.SELECTION.getBlue() - intesity);
    }
    
}
