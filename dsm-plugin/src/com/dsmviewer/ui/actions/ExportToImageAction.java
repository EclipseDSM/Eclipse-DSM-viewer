package com.dsmviewer.ui.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.print.command.PrintEntireGridCommand;
import org.eclipse.nebula.widgets.nattable.print.command.TurnViewportOnCommand;
import org.eclipse.nebula.widgets.nattable.util.IClientAreaProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.FileDialog;

import com.dsmviewer.Activator;
import com.dsmviewer.logging.Logger;
import com.dsmviewer.ui.dsmtable.DsmTableController;

public class ExportToImageAction extends Action {

    private DsmTableController dsmTableController;

    public ExportToImageAction(DsmTableController dsmTableController) {
        this.dsmTableController = dsmTableController;
    }

	@Override
	public void run() {

		NatTable table = dsmTableController.getTable();

		FileDialog dialog = new FileDialog(table.getShell(), SWT.SAVE);
		dialog.setText("Export DSM to image");
		dialog.setFilterNames(new String[] { "PNG Files (*.png)",
				"JPEG files (*.jpg)", "BMP files (*.bmp)", "All Files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.png", "*.jpg", "*.bmp", "*.*" });
		dialog.setOverwrite(true);
		dialog.setFileName("screenshot.png");
		String filePath = dialog.open();

		if (filePath != null) {

			final Image image = toImage(table);

			int choosedFilterIndex = dialog.getFilterIndex();
			switch (choosedFilterIndex) {
			case 0:
				saveImageToFile(image, filePath, SWT.IMAGE_PNG);
				break;
			case 1:
				saveImageToFile(image, filePath, SWT.IMAGE_JPEG);
				break;
			case 2:
				saveImageToFile(image, filePath, SWT.IMAGE_BMP);
				break;
			default:
				throw new IllegalArgumentException("File format with index "
			              + choosedFilterIndex + " is not supported");
			}

			image.dispose();
		}
	}

	private static void saveImageToFile(Image image, String filePath, int imageType) {

		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { image.getImageData() };

		try {
			FileOutputStream fos = new FileOutputStream(new File(filePath));
			loader.save(fos, imageType);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error while saving image as '" + filePath+ "'", e);			
		}
	}

	private Image toImage(NatTable table) {

		GridLayer layer = (GridLayer)table.getLayer();

		int width = layer.getPreferredWidth();
		int height = layer.getPreferredHeight();

		final Image image = new Image(table.getDisplay(), width, height);

		GC gc = new GC(image);

		setViewportSize(layer, width, height);

		Rectangle layerBounds = new Rectangle(0, 0, width, height);
		IConfigRegistry configRegistry = table.getConfigRegistry();
		layer.getLayerPainter().paintLayer(layer, gc, 0, 0, layerBounds, configRegistry);

        restoreViewPortSize(layer);

		gc.dispose();

		return image;
	}

	/**
	 * Expand the client area of the layer such that
	 * all the contents fit in the viewport. This ensures that when the grid prints
	 * we print the <i>entire</i> table.
	 */
	private static void setViewportSize(final ILayer layer, int width, int height) {		

		final Rectangle layerBounds = new Rectangle(0, 0, width, height);

		layer.setClientAreaProvider(new IClientAreaProvider() {
			@Override
			public Rectangle getClientArea() {
				return layerBounds;
			}
		});

		layer.doCommand(new PrintEntireGridCommand());
	}

	/**
	 * Restores layer`s viewport to its normal operation.
	 */
	private static void restoreViewPortSize(ILayer layer) {
		layer.doCommand(new TurnViewportOnCommand());
	}

    @Override
    public String getToolTipText() {
        return "Export to image";
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return Activator.getImageDescriptorFromRegistry("export_to_image.png");
    }

}
