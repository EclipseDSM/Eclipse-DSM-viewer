package com.dsmviewer.ui;

import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import com.dsmviewer.dsmtable.DsmBodyLayerConfiguration;

public final class UiHelper {

    public static final Color COLOR_LIGHT_GRAY = GUIHelper.getColor(250, 250, 250);

    public static final Color COLOR_DSM_COLUMN_HEADER_BG = COLOR_LIGHT_GRAY;
    public static final Color COLOR_DSM_COLUMN_HEADER_FG = GUIHelper.COLOR_WIDGET_FOREGROUND;

    public static final Color COLOR_DSM_ROW_HEADER_BG = COLOR_LIGHT_GRAY;
    public static final Color COLOR_DSM_ROW_HEADER_FG = GUIHelper.COLOR_WIDGET_FOREGROUND;

    public static final Color COLOR_DSM_CELL_BG = GUIHelper.getColor(246, 253, 255);
    public static final Color COLOR_DSM_CELL_FG = GUIHelper.COLOR_BLACK;

    public static final Color COLOR_DSM_DIAGONAL_CELL_BG = GUIHelper.getColor(216, 218, 235);
    public static final Color COLOR_DSM_NON_VALID_CELL_BG = GUIHelper.getColor(255, 156, 156);

    public static final Color COLOR_DSM_SELECTION_BG_LIGHT = GUIHelper.getColor(155, 188, 209);
    public static final Color COLOR_DSM_SELECTION_BG_DARK = GUIHelper.getColor(186, 214, 232);
    public static final Color COLOR_DSM_SELECTION_FG = GUIHelper.getColor(56, 75, 87);

    public static final Color COLOR_DSM_ADDITIONALLY_SELECTED_ROW_BG = GUIHelper.getColor(245, 230, 162);

//    public static final Color COLOR_DSM_COLUMN_HEADER = COLOR_LIGHT_GRAY;
//    public static final Color COLOR_DSM_COLUMN_HEADER = COLOR_LIGHT_GRAY;
//    public static final Color COLOR_DSM_COLUMN_HEADER = COLOR_LIGHT_GRAY;

    public static final Font FONT_BOLD_ARIAL = new Font(Display.getCurrent(), "Arial", 8, SWT.BOLD);

    public static final int DEFAULT_FONT_HEIGHT = DsmBodyLayerConfiguration.FONT_SIZE;

    public static final int DEFAULT_FONT_SIZE = 9;

    // TODO: use GC.fontMetrics instead
    public static final int DEFAULT_FONT_WIDTH = (int) (DEFAULT_FONT_HEIGHT * 1.5);


    private UiHelper() {
    }

    public static Font getSystemFont() {
        return getSystemFont(DEFAULT_FONT_SIZE);
    }

    public static Font getSystemFont(int fontSize) {
        Font systemFont = Display.getDefault().getSystemFont();
        return changeFontSize(systemFont, fontSize);
    }

    public static Font getSystemFont(int size, int style) {
        String name = getSystemFont(size).getFontData()[0].getName();
        return GUIHelper.getFont(new FontData(name, size, style));
    }

    public static Font changeFontSize(Font font, int size) {
        FontData[] fontData = font.getFontData();
        fontData[0].setHeight(size);
        return new Font(Display.getDefault(), fontData[0]);
    }

}