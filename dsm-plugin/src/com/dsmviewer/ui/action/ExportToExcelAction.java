package com.dsmviewer.ui.action;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.export.ILayerExporter;
import org.eclipse.nebula.widgets.nattable.export.NatExporter;
import org.eclipse.nebula.widgets.nattable.export.excel.ExcelExporter;

import com.dsmviewer.Activator;
import com.dsmviewer.logging.Logger;
import com.dsmviewer.ui.dsmtable.DsmTableController;

public class ExportToExcelAction extends Action {

    private Logger logger = Activator.getLogger(getClass());

    private DsmTableController dsmTableController;

    public ExportToExcelAction(DsmTableController dsmTableController) {
        this.dsmTableController = dsmTableController;
    }

    @Override
    public void run() {
        NatTable table = dsmTableController.getTable();

        NatExporter exporter = new NatExporter(table.getShell());
        Map<String, NatTable> natTablesMap = new HashMap<String, NatTable>();

        String dsmOrdering = dsmTableController.getDependencyMatrix().getOrdering().toString();
        natTablesMap.put("DSM with " + dsmOrdering + " ordering", table);

        IConfigRegistry configRegistry = table.getConfigRegistry();
        configRegistry.registerConfigAttribute(ILayerExporter.CONFIG_ATTRIBUTE, new ExcelExporter());
        exporter.exportSingleLayer(table.getLayer(), configRegistry);
    }

    @Override
    public String getToolTipText() {
        return "Export DSM as Excel spreadsheet (*.xls)";
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return Activator.getImageDescriptorFromRegistry("export_to_excel.png");
    }

}
