package com.dsmviewer.ui.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.dsmviewer.ui.views.DSMModel.Label;

public class DSMTreeViewerContentProvider implements ITreeContentProvider {

    private static final Object[] EMPTY_ARRAY = new Object[0];

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof Label[])
            return (Label[]) inputElement;
        else
            return EMPTY_ARRAY;
    }

    @Override
    public boolean hasChildren(Object element) {
//       if (element instanceof Company || element instanceof Department) {
//         return true;
//       }
        return true;
    }

    //Queried to load the children of a given node
    @Override
    public Object[] getChildren(Object parentElement) {
//       if (parentElement instanceof Company) {
//         Company company = (Company) parentElement;
//         return company.getListDepartment().toArray();
//       } else
//       if (parentElement instanceof Department) {
//         Department department = (Department) parentElement;
//         return department.getListEmployee().toArray();
//       }
        return EMPTY_ARRAY;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public void dispose() {

    }

}