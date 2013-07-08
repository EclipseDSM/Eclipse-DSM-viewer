package com.dsmviewer.ui.dsmtable;

import org.eclipse.nebula.widgets.nattable.layer.config.DefaultRowHeaderStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle.LineStyleEnum;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;

import com.dsmviewer.ui.UiHelper;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmRowHeaderConfiguration extends DefaultRowHeaderStyleConfiguration {

    private static final int CELL_SPACING = 0;

    private static final TextPainter ROW_HEADER_CELL_PAINTER = new TextPainter(true, true, CELL_SPACING, true);

    {
        this.font = UiHelper.getSystemFont();
        this.bgColor = UiHelper.COLOR_DSM_ROW_HEADER_BG;
        this.fgColor = UiHelper.COLOR_DSM_ROW_HEADER_FG;
        this.hAlign = HorizontalAlignmentEnum.LEFT;
        this.vAlign = VerticalAlignmentEnum.MIDDLE;
        this.borderStyle = new BorderStyle(0, GUIHelper.COLOR_TITLE_INACTIVE_BACKGROUND, LineStyleEnum.SOLID);
        this.cellPainter = ROW_HEADER_CELL_PAINTER;
    }

}
