package com.dsmviewer.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.dsmviewer.Activator;
import com.dsmviewer.ui.DsmView;

public class StepBackwardAction extends Action {

    @Override
    public void run() {
        DsmView.getCurrent().getDsmTableController().doStepBackWard();
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return Activator.getImageDescriptorFromRegistry("step_backward.png");
    }
}
