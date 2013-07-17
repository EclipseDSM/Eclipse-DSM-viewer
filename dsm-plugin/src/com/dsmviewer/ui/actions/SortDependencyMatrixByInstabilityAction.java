package com.dsmviewer.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;

import com.dsmviewer.dsm.DependencyMatrixOrdering;
import com.dsmviewer.ui.UiHelper;
import com.dsmviewer.ui.dsmtable.DsmTableController;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class SortDependencyMatrixByInstabilityAction extends AbstractSortDependencyMatrixAction {

    public SortDependencyMatrixByInstabilityAction(DsmTableController dsmTableController) {
        super(dsmTableController);
    }

    @Override
    public DependencyMatrixOrdering getDependencyMatrixOrdering() {
        return DependencyMatrixOrdering.BY_INSTABILITY;
    }

    @Override
    public String getToolTipText() {
        return "Sort dsm by dependees instability";
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return UiHelper.getSharedImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK);
    }

    @Override
    public int getStyle() {
        return IAction.AS_RADIO_BUTTON;
    }
}
