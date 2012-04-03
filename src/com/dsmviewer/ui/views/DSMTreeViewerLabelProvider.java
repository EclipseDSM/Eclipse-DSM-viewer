package com.dsmviewer.ui.views;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.dsmviewer.Activator;
import com.dsmviewer.ui.views.DSMModel.Label;

public class DSMTreeViewerLabelProvider extends ColumnLabelProvider {

    DSMTreeViewer treeViewer;
    
    private String scope;

    private static final Image CLASS_ICON = Activator.getImageDescriptor(
            "icons/class.gif").createImage();

    private static final Image PACKAGE_ICON = Activator.getImageDescriptor(
            "icons/package.gif").createImage();
    
    public DSMTreeViewerLabelProvider(DSMTreeViewer treeViewer, String scope) {
        this.scope = scope;
        this.treeViewer = treeViewer;
    }

    @Override
    public Color getBackground(Object element) {
        Label label = (Label)element;        
        return treeViewer.getItemColor(label.getNumber());        
    }

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
