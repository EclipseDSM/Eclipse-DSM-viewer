package com.dsmviewer.ui;

import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import com.dsmviewer.ui.dsmtable.DsmBodyLayerConfiguration;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public final class UiHelper extends GUIHelper {

    // colors
    public static final Color COLOR_LIGHT_GRAY = getColor(250, 250, 250);

    public static final Color COLOR_DSM_COLUMN_HEADER_BG = COLOR_LIGHT_GRAY;
    public static final Color COLOR_DSM_COLUMN_HEADER_FG = COLOR_WIDGET_FOREGROUND;

    public static final Color COLOR_DSM_ROW_HEADER_BG = COLOR_LIGHT_GRAY;
    public static final Color COLOR_DSM_ROW_HEADER_FG = COLOR_WIDGET_FOREGROUND;

    public static final Color COLOR_DSM_CELL_BG = getColor(246, 253, 255);
    public static final Color COLOR_DSM_CELL_FG = COLOR_BLACK;

    public static final Color COLOR_DSM_DIAGONAL_CELL_BG = getColor(216, 218, 235);

    public static final Color COLOR_DSM_INVALID_CELL_BG = getColor(255, 156, 156);

    public static final Color COLOR_DSM_SELECTION_BG_LIGHT = getColor(155, 188, 209);
    public static final Color COLOR_DSM_SELECTION_BG_DARK = getColor(186, 214, 232);
    public static final Color COLOR_DSM_SELECTION_FG = getColor(56, 75, 87);

    public static final Color COLOR_DSM_ADDITIONALLY_SELECTED_ROW_BG = getColor(245, 230, 162);

    // Log colors for debug mode logging
    public static final Color LOG_COLOR_DEFAULT = getSystemColor(SWT.COLOR_BLACK);
    public static final Color LOG_COLOR_DEBUG = getSystemColor(SWT.COLOR_BLACK);
    public static final Color LOG_COLOR_INFO = getSystemColor(SWT.COLOR_DARK_GREEN);
    public static final Color LOG_COLOR_WARN = getColor(232, 174, 91); // orange
    public static final Color LOG_COLOR_ERROR = getSystemColor(SWT.COLOR_RED);

    public static final int DEFAULT_FONT_SIZE = 9;
    public static final Font FONT_BOLD_ARIAL = new Font(Display.getCurrent(), "Arial", 8, SWT.BOLD);
    public static final int DEFAULT_FONT_HEIGHT = DsmBodyLayerConfiguration.FONT_SIZE;
    // TODO: use GC.fontMetrics method instead of 'height * const' hacks
    public static final int DEFAULT_FONT_WIDTH = (int) (DEFAULT_FONT_HEIGHT * 1.5);

    private UiHelper() {
    }

    public static Color getSystemColor(int swtColorConstant) {
        return Display.getCurrent().getSystemColor(swtColorConstant);
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
        return getFont(new FontData(name, size, style));
    }

    public static Font changeFontSize(Font font, int size) {
        FontData[] fontData = font.getFontData();
        fontData[0].setHeight(size);
        return new Font(Display.getDefault(), fontData[0]);
    }

}