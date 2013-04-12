package com.dsmviewer.dsmtable;

import org.eclipse.nebula.widgets.nattable.layer.config.DefaultColumnHeaderStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.LineBorderDecorator;
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
public class DsmColumnHeaderConfiguration extends DefaultColumnHeaderStyleConfiguration {

    private static final int FONT_SIZE = UiHelper.DEFAULT_FONT_SIZE;
    private static final int CELL_SPACING = 0;

    {
        this.font = UiHelper.getSystemFont(FONT_SIZE);
        this.bgColor = UiHelper.COLOR_DSM_COLUMN_HEADER_BG;
        this.fgColor = UiHelper.COLOR_DSM_COLUMN_HEADER_FG;
        this.hAlign = HorizontalAlignmentEnum.CENTER;
        this.vAlign = VerticalAlignmentEnum.MIDDLE;
        this.borderStyle = new BorderStyle(1, GUIHelper.COLOR_WIDGET_NORMAL_SHADOW, LineStyleEnum.SOLID);
        this.cellPainter = new LineBorderDecorator(new TextPainter(false, true, CELL_SPACING, true));
    }

}
