package com.dsmviewer.ui.action;

import org.eclipse.jface.action.Action;

import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.dsm.DependencyMatrixOrdering;
import com.dsmviewer.ui.dsmtable.DsmTableController;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public abstract class AbstractSortDependencyMatrixAction extends Action {

    private DsmTableController dsmTableController;

    public abstract DependencyMatrixOrdering getDependencyMatrixOrdering();

    public AbstractSortDependencyMatrixAction(DsmTableController dsmTableController) {
        this.dsmTableController = dsmTableController;
    }

    @Override
    public void run() {
        DependencyMatrix dependencyMatrix = dsmTableController.getDependencyMatrix();
        if (dependencyMatrix != null) {
            dependencyMatrix.sort(getDependencyMatrixOrdering());
            dsmTableController.setDependencyMatrix(dependencyMatrix, true);
        }
    }

    public DsmTableController getDsmTableController() {
        return dsmTableController;
    }

}
