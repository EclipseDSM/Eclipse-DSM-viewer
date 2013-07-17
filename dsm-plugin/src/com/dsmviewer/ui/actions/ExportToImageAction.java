package com.dsmviewer.ui.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.dsmviewer.Activator;
import com.dsmviewer.logging.Logger;
import com.dsmviewer.ui.DsmView;
import com.dsmviewer.ui.dsmtable.DsmTableController;

public class ExportToImageAction extends Action {

    private Logger logger = Activator.getLogger(getClass());

    private DsmTableController dsmTableController;

    public ExportToImageAction(DsmTableController dsmTableController) {
        this.dsmTableController = dsmTableController;
    }

    @Override
    public void run() {

        Shell shell = DsmView.getCurrent().getViewSite().getShell();

        FileDialog dialog = new FileDialog(shell, SWT.SAVE);
        dialog.setFilterNames(new String[] { "Png Files", "All Files (*.*)" });
        dialog.setFilterExtensions(new String[] { "*.png", "*.*" });
        dialog.setFileName("screenshot.png");
        String fileNameAndPath = dialog.open();

        if (fileNameAndPath != null) {
            NatTable table = dsmTableController.getTable();
            Point tableSize = dsmTableController.getDsmTableBounds();

            GC gc = new GC(table);
            Display display = shell.getDisplay();
            final Image image = new Image(display, tableSize.x, tableSize.y);
            gc.copyArea(image, 0, 0);
            gc.dispose();

            ImageLoader loader = new ImageLoader();
            loader.data = new ImageData[] { image.getImageData() };
            try {
                loader.save(new FileOutputStream(new File(fileNameAndPath)), SWT.IMAGE_PNG);
            } catch (FileNotFoundException e) {
                logger.error("Error while saving screenshot to file " + fileNameAndPath, e);
            }
        }
    }

//    private void setClientAreaToMaximum(ILayer layer) {
//        final Rectangle maxClientArea = new Rectangle(0, 0, layer.getWidth(), layer.getHeight());
//        
//        layer.setClientAreaProvider(new IClientAreaProvider() {
//            public Rectangle getClientArea() {
//                return maxClientArea;
//            }
//        });
//        
//        layer.doCommand(new PrintEntireGridCommand());
//    }

    @Override
    public String getToolTipText() {
        return "Export visible DSM part to PNG image";
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return Activator.getImageDescriptorFromRegistry("take_screenshot.png");
    }

}
