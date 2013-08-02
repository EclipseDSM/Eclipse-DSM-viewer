package com.dsmviewer.ui;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

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
    public static final Color COLOR_DSM_SELECTION_BG_DARK = getColor(176, 204, 222);
    public static final Color COLOR_DSM_SELECTION_FG = getColor(56, 75, 87);

    public static final Color COLOR_DSM_SELECTED_DEPENDEE_ROW_BG = getColor(245, 230, 162);

    // Log colors for debug mode logging
    public static final Color LOG_COLOR_DEFAULT = getSystemColor(SWT.COLOR_BLACK);
    public static final Color LOG_COLOR_DEBUG = getSystemColor(SWT.COLOR_BLACK);
    public static final Color LOG_COLOR_INFO = getSystemColor(SWT.COLOR_DARK_GREEN);
    public static final Color LOG_COLOR_WARN = getColor(232, 174, 91); // orange
    public static final Color LOG_COLOR_ERROR = getSystemColor(SWT.COLOR_RED);

    //sizes
    public static final int DSM_CELL_SIZE_DEFAULT = 21;

    public static final int ICON_SIZE = 16;

    // fonts
    public static final int DEFAULT_FONT_SIZE = 9;

    private UiHelper() {
    }

    /**
     * Gets image is accessible from shared images of active Eclipse plugins
     * 
     * @param imageId String constant from ISharedImages i-face. Example: ISharedImages.IMG_OBJS_INFO_TSK
     */
    public static Image getSharedImage(String imageId) {
        return PlatformUI.getWorkbench().getSharedImages().getImage(imageId);
    }

    /**
     * Gets descriptor for image is accessible from shared images of active Eclipse plugins
     * 
     * @param imageId String constant from ISharedImages i-face. Example: ISharedImages.IMG_OBJS_INFO_TSK
     */
    public static ImageDescriptor getSharedImageDescriptor(String imageId) {
        return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(imageId);
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

    public static int computeMaxTextExtent(List<String> strings, Shell shell) {
        GC gc = new GC(shell);

        int maxTextExtent = 0;
        for (int i = 0; i < strings.size(); i++) {
            int textExtent = gc.textExtent(strings.get(i)).x;
            if (maxTextExtent < textExtent) {
                maxTextExtent = textExtent;
            }
        }

        return maxTextExtent;
    }

}