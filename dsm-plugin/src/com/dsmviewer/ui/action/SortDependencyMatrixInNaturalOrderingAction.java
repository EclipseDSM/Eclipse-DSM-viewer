package com.dsmviewer.ui.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;

import com.dsmviewer.Activator;
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

    @Override
    public ImageDescriptor getImageDescriptor() {
        return Activator.getImageDescriptorFromRegistry("sort_in_natural_ordering.png");
    }

    @Override
    public int getStyle() {
        return IAction.AS_RADIO_BUTTON;
    }
}
