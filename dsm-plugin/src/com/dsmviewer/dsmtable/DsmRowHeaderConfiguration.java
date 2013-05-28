package com.dsmviewer.dsmtable;

import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.config.DefaultRowHeaderStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle.LineStyleEnum;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.GC;

import com.dsmviewer.ui.UiHelper;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmRowHeaderConfiguration extends DefaultRowHeaderStyleConfiguration {

    private static final int CELL_SPACING = 0;

    private static final TextPainter BASE_CELL_PAINTER = new TextPainter(true, true,
            CELL_SPACING, true);

    private static final ICellPainter ROW_INDEX_PAINTER = new TextPainter(true, true, CELL_SPACING, true) {
        @Override
        protected String getTextToDisplay(ILayerCell cell, GC gc, int availableLength, String text) {
            return String.valueOf(cell.getRowIndex());
        }
    };

    private static final ICellPainter CELL_PAINTER = new CellPainterDecorator(BASE_CELL_PAINTER, CellEdgeEnum.RIGHT,
            ROW_INDEX_PAINTER);

    {
        this.font = UiHelper.getSystemFont();
        this.bgColor = UiHelper.COLOR_DSM_ROW_HEADER_BG;
        this.fgColor = UiHelper.COLOR_DSM_ROW_HEADER_FG;
        this.hAlign = HorizontalAlignmentEnum.LEFT;
        this.vAlign = VerticalAlignmentEnum.MIDDLE;
        this.borderStyle = new BorderStyle(0, GUIHelper.COLOR_TITLE_INACTIVE_BACKGROUND, LineStyleEnum.SOLID);
        this.cellPainter = BASE_CELL_PAINTER;
    }

}
