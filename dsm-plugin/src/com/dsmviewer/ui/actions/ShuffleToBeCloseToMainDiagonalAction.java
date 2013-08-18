package com.dsmviewer.ui.actions;

import java.util.List;

import org.dtangler.core.dsm.DsmCell;
import org.dtangler.core.dsm.DsmRow;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.dsmviewer.Activator;
import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.dsm.DependencyMatrixOrdering;
import com.dsmviewer.ui.DsmView;
import com.dsmviewer.ui.dsmtable.DsmTableController;
import com.dsmviewer.utils.Utils;

public class ShuffleToBeCloseToMainDiagonalAction extends Action {

	private DsmTableController dsmTableController;

	public ShuffleToBeCloseToMainDiagonalAction(DsmTableController dsmTableController) {
		this.dsmTableController = dsmTableController;
	}

	@Override
	public void run() {
		DependencyMatrix dependencyMatrix = dsmTableController.getDependencyMatrix();
		DependencyMatrix shuffledMatrix = shuffle(dependencyMatrix, 1500);

		dsmTableController.setDependencyMatrix(shuffledMatrix, true);
		DsmView.getCurrent().updateSortActionsState(DependencyMatrixOrdering.UNKNOWN_ODERING);
	}

	public DependencyMatrix shuffle(DependencyMatrix dependencyMatrix, int numberOfPassages) {

		DependencyMatrix result = dependencyMatrix;

		double minTotalDistance = countTotalDistance(dependencyMatrix);

		for (int i = 0; i < numberOfPassages; i++) {
			performShuffle(dependencyMatrix);
			double totalDistance = countTotalDistance(dependencyMatrix);
			System.out.println();
			if (minTotalDistance > totalDistance) {
				minTotalDistance = totalDistance;
				result = dependencyMatrix;
			}
		}

		return result;
	}

	private static void performShuffle(DependencyMatrix dependencyMatrix) {

		int matrixSize = dependencyMatrix.getSize();
		for (int i = 0; i < matrixSize * 5; i++) {
			int randomIndex1 = Utils.getRandomInt(0, matrixSize - 1);
			int randomIndex2 = Utils.getRandomInt(0, matrixSize - 1);
			if (randomIndex1 != randomIndex2) {
				dependencyMatrix.replaceElements(randomIndex1, randomIndex2);
			}
		}
	}

	private static double countTotalDistance(DependencyMatrix dependencyMatrix) {

		double result = 0;

		List<DsmRow> rows = dependencyMatrix.getRows();
		for (int i = 0; i < rows.size(); i++) {
			DsmRow row = rows.get(i);
			List<DsmCell> cells = row.getCells();
			for (int j = 0; j < cells.size(); j++) {
				if (i != j) {
					DsmCell cell = row.getCells().get(j);
					int cellDependencyWeight = cell.getDependencyWeight();
					if (cellDependencyWeight > 0) {
						result += Math.pow(Math.abs(i - j), 2) + 1;
					}
				}
			}
		}

		return result;
	}

	@Override
	public String getToolTipText() {
		return "Shuffle to be more close one to main diagonal";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return Activator.getImageDescriptorFromRegistry("dsm-view-favicon2.png");
	}

}
