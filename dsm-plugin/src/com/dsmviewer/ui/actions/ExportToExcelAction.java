package com.dsmviewer.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.export.ILayerExporter;
import org.eclipse.nebula.widgets.nattable.export.NatExporter;
import org.eclipse.nebula.widgets.nattable.export.excel.ExcelExporter;

import com.dsmviewer.Activator;
import com.dsmviewer.ui.dsmtable.DsmTableController;

public class ExportToExcelAction extends Action {

    private DsmTableController dsmTableController;

    public ExportToExcelAction(DsmTableController dsmTableController) {
        this.dsmTableController = dsmTableController;
    }

    @Override
    public void run() {
        NatTable table = dsmTableController.getTable();

        NatExporter exporter = new NatExporter(table.getShell());

        IConfigRegistry configRegistry = table.getConfigRegistry();
        configRegistry.registerConfigAttribute(ILayerExporter.CONFIG_ATTRIBUTE, new ExcelExporter());

        exporter.exportSingleLayer(table.getLayer(), configRegistry);
    }

    @Override
    public String getToolTipText() {
        return "Export to Excel spreadsheet (*.xls)";
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return Activator.getImageDescriptorFromRegistry("export_to_excel.png");
    }

}
