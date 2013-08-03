package com.dsmviewer.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Rectangle;

import com.dsmviewer.Activator;
import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.ui.DsmView;
import com.dsmviewer.ui.dsmtable.DsmTableController;

public class ClearAllAction extends Action {

	private DsmTableController dsmTableController;

	public ClearAllAction(DsmTableController dsmTableController) {
		this.dsmTableController = dsmTableController;
	}

	@Override
	public void run() {
		dsmTableController.setDependencyMatrix(DependencyMatrix.EMPTY_MATRIX, false);
		dsmTableController.setDsmTableBounds(new Rectangle(0, 0, 0, 0));
		dsmTableController.clearHistory();
		DsmView.getCurrent().setActionsEnabled(false);
	}

	@Override
	public String getToolTipText() {
		return "Clear";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return Activator.getImageDescriptorFromRegistry("clear_all.gif");
	}

}
