package com.dsmviewer.ui.views;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dsmviewer.ui.views.DSMModel.Label;

public class DSMTreeViewer extends TreeViewer {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Inner tree.
     */
    private Tree tree;

    /**
     * 
     */
    private TreeViewerColumn itemsColumn;

    private TreeColumn treeColumn;

    private Color [] itemsColors;
    
    public DSMTreeViewer(final Composite parent) {        
        super(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

        setContentProvider(new DSMTreeViewerContentProvider());

        tree = this.getTree();
        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);
        
        itemsColumn = new TreeViewerColumn(this, SWT.NONE);
        
        logger.debug("DSMTreeViewer was composed.");
    }

    public void setLabels(final List<Label> labels, String scope) {
        itemsColors = new Color [labels.size()];
        composeColumn(scope);
        this.setInput(labels);
    }

    private void composeColumn(String scope) {        
        itemsColumn.setLabelProvider(new DSMTreeViewerLabelProvider(this, scope));
        treeColumn = itemsColumn.getColumn();

        if ("packages".equals(scope)) {
            treeColumn.setText("Packages:");
            treeColumn.setToolTipText("Packages:");
        }
        else if ("classes".equals(scope)) {
            treeColumn.setText("Classes:");
            treeColumn.setToolTipText("Classes:");
        }
        treeColumn.setWidth(150);
        treeColumn.setResizable(true);
    }

    public int getSelectionIndex() {
        int result = 0;
        IStructuredSelection selection = (IStructuredSelection) this.getSelection();
        Label selectedLabel = (Label) ((selection).getFirstElement());
        if (selectedLabel != null) { // if selection is not empty
            result = selectedLabel.getNumber();
        }
        return result;
    }

    public void setSelectionIndex(int index) {
        Label elementToselect = ((List<Label>) this.getInput()).get(index);
        this.setSelection(new StructuredSelection(elementToselect));
    }

    public void colorizeTreeItem(int index, Color color){
        Arrays.fill(itemsColors, null);
        itemsColors [index] = color;
        this.refresh();
    }
    
    public Color getItemColor(int index){
        return itemsColors [index];
    }
    
    public ScrollBar getTreeVerticalBar(){
        return tree.getVerticalBar();
    }

}
