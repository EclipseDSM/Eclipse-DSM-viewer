package com.dsmviewer.ui.action;

import com.dsmviewer.dsm.DependencyMatrixOrdering;
import com.dsmviewer.ui.dsmtable.DsmTableController;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class SortDependencyMatrixInNaturalOrderingAction extends AbstractSortDependencyMatrixAction {

    public SortDependencyMatrixInNaturalOrderingAction(DsmTableController dsmTableController) {
        super(dsmTableController);
    }

    @Override
    public DependencyMatrixOrdering getDependencyMatrixOrdering() {
        return DependencyMatrixOrdering.NATURAL_ORDERING;
    }

    @Override
    public String getToolTipText() {
        return "Sort in natural ordering";
    }

}
