package com.dsmviewer.ui.views;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dsm.DsmRow;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class DSMViewLabelProvider extends LabelProvider implements ITableLabelProvider {

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    public String getColumnText(Object obj, int index) {
        DsmRow dsmRow = (DsmRow)obj;
        if (index == 0) {
            Dependable dep = dsmRow.getDependee();
            String count = dep.getContentCount() + "";
            return count +": " +dep.getDisplayName();
        }
        else {
            return Integer.toString(dsmRow.getCells().get(index-1).getDependencyWeight());
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    public Image getColumnImage(Object obj, int index) {
        if(index == 0) {
            return getImage(obj);
        } else {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    public Image getImage(Object obj) {
        return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DEF_PERSPECTIVE);
    }

}
