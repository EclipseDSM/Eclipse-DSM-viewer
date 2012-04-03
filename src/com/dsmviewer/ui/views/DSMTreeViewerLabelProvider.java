package com.dsmviewer.ui.views;


import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.dsmviewer.Activator;
import com.dsmviewer.ui.views.DSMModel.Label;

public class DSMTreeViewerLabelProvider extends ColumnLabelProvider {

    private String scope;

    private static final Image CLASS_ICON = Activator.getImageDescriptor(
            "icons/class.gif").createImage();

    private static final Image PACKAGE_ICON = Activator.getImageDescriptor(
            "icons/package.gif").createImage();

    public DSMTreeViewerLabelProvider(String scope) {
        this.scope = scope;
    }

//    @Override
//    public Color getBackground(Object element) {
//        return null;        
//    }
    
//  @Override
//  public Color getForeground(Object element) {
//      return null;        
//  }

    @Override
    public String getText(Object element) {
        Label label = (Label)element;        
        return (label.getNumber() + 1) + ": " + label.getFullname();
    }

    @Override
    public Image getImage(Object element) {
        Image image = null;
        
        if ("packages".equals(this.scope)) {
            image = PACKAGE_ICON;
        }
        else if ("classes".equals(this.scope)) {
            image = CLASS_ICON;
        }
        return image;
    }
    
//    @Override
//    public void update(ViewerCell cell) {
//
//    }
    
  }
