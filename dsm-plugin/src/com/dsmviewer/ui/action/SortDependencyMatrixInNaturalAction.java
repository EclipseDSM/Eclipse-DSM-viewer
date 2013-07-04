package com.dsmviewer.ui.action;

import org.eclipse.jface.action.Action;

import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.dsm.DependencyMatrixOrdering;
import com.dsmviewer.ui.dsmtable.DsmTableController;

public class SortDependencyMatrixInNaturalAction extends Action {

    private DsmTableController dsmTableController;

    public SortDependencyMatrixInNaturalAction(DsmTableController dsmTableController) {
        this.dsmTableController = dsmTableController;
    }

    @Override
    public void run() {
        DependencyMatrix dependencyMatrix = dsmTableController.getDependencyMatrix();
        if (dependencyMatrix != null) {
            dependencyMatrix.sort(DependencyMatrixOrdering.NATURAL_ORDERING);
            dsmTableController.setDependencyMatrix(dependencyMatrix, true);
        }
    }

    @Override
    public String getToolTipText() {
        return "Sort in natural ordering";
    }
}
