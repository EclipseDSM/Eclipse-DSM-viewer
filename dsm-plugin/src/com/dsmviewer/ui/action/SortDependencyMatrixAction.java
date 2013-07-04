package com.dsmviewer.ui.action;

import org.eclipse.jface.action.Action;

import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.dsm.DependencyMatrixOrdering;
import com.dsmviewer.ui.dsmtable.DsmTableController;

public class SortDependencyMatrixAction extends Action {

//    private final Logger logger = Activator.getLogger(getClass());
    private DsmTableController dsmTableController;
    private DependencyMatrixOrdering dsmOrdering;

    public SortDependencyMatrixAction(DsmTableController dsmTableController, DependencyMatrixOrdering dsmOrdering) {
        this.dsmTableController = dsmTableController;
        this.dsmOrdering = dsmOrdering;
    }

    @Override
    public void run() {
        DependencyMatrix dependencyMatrix = dsmTableController.getDependencyMatrix();
        if (dependencyMatrix != null) {
            dependencyMatrix.sort(dsmOrdering);
            dsmTableController.setDependencyMatrix(dependencyMatrix, true);
        }
    }

}
