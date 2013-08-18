package com.dsmviewer.cluster;

import java.util.List;

import org.dtangler.core.dsm.DsmCell;
import org.dtangler.core.dsm.DsmRow;

import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.utils.Utils;

public class DependencyMatrixShuffler {

	private int numberOfPassages;

	public DependencyMatrixShuffler(int numberOfPassages) {
		this.numberOfPassages = numberOfPassages;
	}

	public DependencyMatrix shuffle(DependencyMatrix dependencyMatrix) {

		DependencyMatrix result = dependencyMatrix;

		double maxTotalDistance = countTotalDistance(dependencyMatrix);

		for (int i = 0; i < numberOfPassages; i++) {
			performShuffle(dependencyMatrix);
			double totalDistance = countTotalDistance(dependencyMatrix);
			if (maxTotalDistance < totalDistance) {
				maxTotalDistance = totalDistance;
				result = dependencyMatrix;
			}
		}

		return result;
	}

	private static void performShuffle(DependencyMatrix dependencyMatrix) {

		int matrixSize = dependencyMatrix.getSize();
		for (int i = 0; i < matrixSize * 2; i++) {
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
					DsmCell cellFrom = row.getCells().get(j);
					int cellFromDependencyWeight = cellFrom.getDependencyWeight();
					if (cellFromDependencyWeight > 0) {
						result += countDistanceToAnotherCells(rows, i, j, cellFromDependencyWeight);
					}
				}
			}
		}

		return result;
	}

	private static double countDistanceToAnotherCells(List<DsmRow> rows,
			int rowIndex, int columnIndex, int cellFromDependencyWeight) {

		double result = 0;

		for (int i = 0; i < rows.size(); i++) {

			if (i != rowIndex) {
				DsmRow row = rows.get(i);
				List<DsmCell> cells = row.getCells();
				for (int j = 0; j < cells.size(); j++) {
					if (j != columnIndex) {
						DsmCell cellTo = cells.get(j);

						int cellToDependencyWeight = cellTo.getDependencyWeight();
						if (cellToDependencyWeight > 0) {
							double rowDistance = Math.abs(i - rowIndex) * 2 + 0.1;
							double columnDistance = Math.abs(j - columnIndex) * 2 + 0.1;
							result += Math.max(rowDistance, columnDistance) * cellFromDependencyWeight
									* cellToDependencyWeight;
						}
					}
				}
			}
		}

		return result;
	}

	public int getNumberOfPassages() {
		return numberOfPassages;
	}

	public void setNumberOfPassages(int numberOfPassages) {
		this.numberOfPassages = numberOfPassages;
	}
}
