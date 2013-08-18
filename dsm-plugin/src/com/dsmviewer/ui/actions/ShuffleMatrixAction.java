package com.dsmviewer.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.dsmviewer.Activator;
import com.dsmviewer.cluster.DependencyMatrixShuffler;
import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.dsm.DependencyMatrixOrdering;
import com.dsmviewer.ui.DsmView;
import com.dsmviewer.ui.dsmtable.DsmTableController;

public class ShuffleMatrixAction extends Action {

	private static final int DEFAULT_SHUFFLE_PASSAGES_COUNT = 1500;

	private DsmTableController dsmTableController;

	private DependencyMatrixShuffler dependencyMatrixShuffler = new DependencyMatrixShuffler(
			DEFAULT_SHUFFLE_PASSAGES_COUNT);

	public ShuffleMatrixAction(DsmTableController dsmTableController) {
		this.dsmTableController = dsmTableController;
	}

	@Override
	public void run() {

		DependencyMatrix dependencyMatrix = dsmTableController.getDependencyMatrix();

		if (dependencyMatrix.getSize() <= 30) {
			dependencyMatrixShuffler.setNumberOfPassages(10 * DEFAULT_SHUFFLE_PASSAGES_COUNT);
		}

		DependencyMatrix clusterizedMatrix = dependencyMatrixShuffler.shuffle(dependencyMatrix);

		dsmTableController.setDependencyMatrix(clusterizedMatrix, true);
		DsmView.getCurrent().updateSortActionsState(DependencyMatrixOrdering.UNKNOWN_ODERING);
	}

	@Override
	public String getToolTipText() {
		return "Shuffle to be more close one to another";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return Activator.getImageDescriptorFromRegistry("dsm-view-favicon.png");
	}

}
