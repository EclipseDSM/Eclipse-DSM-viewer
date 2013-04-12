package com.dsmviewer.dsmtable;

import org.eclipse.nebula.widgets.nattable.selection.config.DefaultSelectionStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle.LineStyleEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import com.dsmviewer.ui.UiHelper;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmSelectionLayerConfiguration extends DefaultSelectionStyleConfiguration {

    private static final int SELECTION_FONT_SIZE = UiHelper.DEFAULT_FONT_SIZE;
    private static final Color SELECTION_COLOR_BG = UiHelper.COLOR_DSM_SELECTION_BG_LIGHT;
    private static final Color SELECTION_COLOR_FG = UiHelper.COLOR_DSM_SELECTION_FG;

    {
        this.selectionFont = UiHelper.getSystemFont(SELECTION_FONT_SIZE, SWT.BOLD);
        this.selectionBgColor = UiHelper.COLOR_DSM_SELECTION_BG_DARK;
        this.selectionFgColor = SELECTION_COLOR_FG;

        // Anchor style
        this.anchorBorderColor = GUIHelper.COLOR_DARK_GRAY;
        this.anchorBorderStyle = new BorderStyle(1, anchorBorderColor, LineStyleEnum.SOLID);
        this.anchorBgColor = SELECTION_COLOR_BG;
        this.anchorFgColor = GUIHelper.COLOR_WHITE;

        // Selected headers style
        this.selectedHeaderBgColor = SELECTION_COLOR_BG;
        this.selectedHeaderFgColor = SELECTION_COLOR_FG;
        this.selectedHeaderFont = UiHelper.getSystemFont(SELECTION_FONT_SIZE, SWT.BOLD);
        this.selectedHeaderBorderStyle = new BorderStyle(1, selectedHeaderFgColor, LineStyleEnum.SOLID);
    }

}
