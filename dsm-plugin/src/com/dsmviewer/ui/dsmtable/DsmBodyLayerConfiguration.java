package com.dsmviewer.ui.dsmtable;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;
import org.eclipse.nebula.widgets.nattable.export.excel.DefaultExportFormatter;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.LineBorderDecorator;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle.LineStyleEnum;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

import com.dsmviewer.ui.UiHelper;
import com.dsmviewer.ui.dsmtable.actions.DoubleClickOnGridAction;
import com.dsmviewer.ui.dsmtable.actions.DoubleClickOnRowHeaderAction;
import com.dsmviewer.ui.dsmtable.actions.MouseHoverOnGridAction;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmBodyLayerConfiguration extends AbstractRegistryConfiguration {

	public static final int FONT_SIZE = UiHelper.DEFAULT_FONT_SIZE;

	private static final int CELL_SPACING = 1;

	private final Color bgColor = UiHelper.COLOR_DSM_CELL_BG;
	private final Color fgColor = UiHelper.COLOR_DSM_CELL_FG;
	private final Color gradientBgColor = GUIHelper.COLOR_WHITE;
	private final Color gradientFgColor = GUIHelper.getColor(136, 212, 215);
	private final Font font = UiHelper.getSystemFont(FONT_SIZE);
	private final HorizontalAlignmentEnum hAlign = HorizontalAlignmentEnum.CENTER;
	private final VerticalAlignmentEnum vAlign = VerticalAlignmentEnum.MIDDLE;
	private final BorderStyle borderStyle = new BorderStyle(0, GUIHelper.COLOR_DARK_GRAY, LineStyleEnum.SOLID);

	private ICellPainter cellPainter = new LineBorderDecorator(new TextPainter(false, true, CELL_SPACING, false));

	@Override
	public void configureLayer(ILayer layer) {
	}

	@Override
	public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
		MouseEventMatcher mouseLeftOnRowHeader = new MouseEventMatcher(GridRegion.ROW_HEADER,
				MouseEventMatcher.LEFT_BUTTON);
		uiBindingRegistry.registerDoubleClickBinding(mouseLeftOnRowHeader, new DoubleClickOnRowHeaderAction());

		MouseEventMatcher mouseLeftOnGridBody = new MouseEventMatcher(GridRegion.BODY, MouseEventMatcher.LEFT_BUTTON);
		uiBindingRegistry.registerDoubleClickBinding(mouseLeftOnGridBody, new DoubleClickOnGridAction());

		MouseEventMatcher mouseHoverOnGridBody = new MouseEventMatcher(GridRegion.BODY);
		uiBindingRegistry.registerMouseMoveBinding(mouseHoverOnGridBody, new MouseHoverOnGridAction());
	}

	@Override
	public void configureRegistry(IConfigRegistry configRegistry) {
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, getCellStyle());
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, cellPainter);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultDisplayConverter());
		configRegistry.registerConfigAttribute(CellConfigAttributes.EXPORT_FORMATTER, new DefaultExportFormatter());
	}

	private Style getCellStyle() {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, bgColor);
		cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, fgColor);
		cellStyle.setAttributeValue(CellStyleAttributes.GRADIENT_BACKGROUND_COLOR, gradientBgColor);
		cellStyle.setAttributeValue(CellStyleAttributes.GRADIENT_FOREGROUND_COLOR, gradientFgColor);
		cellStyle.setAttributeValue(CellStyleAttributes.FONT, font);
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, hAlign);
		cellStyle.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT, vAlign);
		cellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, borderStyle);
		return cellStyle;
	}
}
