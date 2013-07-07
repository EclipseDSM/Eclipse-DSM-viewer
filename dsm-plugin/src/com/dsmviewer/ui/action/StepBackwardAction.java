package com.dsmviewer.ui.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.dsmviewer.Activator;

public class StepBackwardAction extends Action {

    @Override
    public void run() {

    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return Activator.getImageDescriptorFromRegistry("step_backward.png");
    }
}
