package com.dsmviewer.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsm.DsmRow;
import org.dtangler.core.dsm.DsmCell;
import org.dtangler.core.dsmengine.DsmEngine;

/**
 * 
 * @author <a href="mailto:klaus.tannnenberg@gmail.com">Grigorov Aleksey</a>
 * 
 */
public class DSMModel {
    private ArrayList<Row> matrix;
    private ArrayList<Label> label;
        
    public ArrayList<Row> getMatrix() {
        return matrix;
    }
    public ArrayList<Label> getLabel() {
        return label;
    }
    
    public void createModel(DependencyGraph dependencyGraph)
    {
        Dsm dsm = new DsmEngine(dependencyGraph).createDsm();
        matrix = new ArrayList<Row>();
        label = new ArrayList<Label>();
        
        // получение имен всех классов
        SortedSet<String> temp = new TreeSet<String>(); 
        for(DsmRow row : dsm.getRows())
        {
            String curPath = row.getDependee().getFullyQualifiedName();
            curPath = curPath.substring(curPath.lastIndexOf(':') + 1).trim();
            while(curPath.length() != 1)
            {
                temp.add(curPath);
                int index = curPath.lastIndexOf('.');
                if(index >= 0) curPath = curPath.substring(0, index);
                else break;
            }
        }

        // создание подписей к матрице для всех внутренностей пакета
        int pos = 0;
        for(String cur : temp) {
            String shortname = (cur.lastIndexOf('.') >= 0) ? cur.substring(cur.lastIndexOf('.')) : cur;
            label.add(new Label(pos, 0, cur, shortname));
            pos++;
        }
//        String[] classes = temp.toArray(new String[0]);
//        for(int n = 0; n < classes.length; n++) {
//            String shortname = (classes[n].lastIndexOf('.') >= 0) ? classes[n].substring(classes[n].lastIndexOf('.')) : classes[n];
//            array.add(new Label(n, 0, classes[n], shortname));
//        }
        label.trimToSize();
        
        // расчет дерева
        buildTree(label, 0, label.size() - 1, -1);
        
        // расчет DSM
        // пока что Dummy для теста UI
        matrix.ensureCapacity(label.size());
        for(int n = 0; n < label.size(); n++) {
            ArrayList<Integer> row = new ArrayList<Integer>(label.size()); 
            for(int m = 0; m < label.size(); m++) {
                // вот тут будет магическая ебля с исходным dsm
                row.add(m, Math.max(n, m));
            }
            matrix.add(n, new Row(n, row));
        }
    }
    
    private void buildTree(ArrayList<Label> array, int index, int last, int parent)
    {
        if(index >= array.size()) {
            if(parent < array.size()) buildTree(array, parent + 2, last, parent + 1);
            return;
        }
        else if(parent == -1) {
            for(Label item : array){
                item.fold = 0;
            }
            buildTree(array, index + 1, last, index);
            return;
        }
        else {
            if(array.get(index).fullname.startsWith(array.get(parent).fullname)) {
                array.get(parent).fold++;
                buildTree(array, index + 1, last, parent);
            }
            else {
                buildTree(array, parent + 2, last, parent + 1);
            }
        }
    }
    
    protected class Label{
        int number;
        int fold;
        String fullname;
        String shortname;
        
        public Label(int number, int fold, String fullname, String shortname)
        {
            this.number = number;
            this.fold = fold;
            this.fullname = fullname;
            this.shortname = shortname;
        }
    }
    
    protected class Row{
        int number;
        ArrayList<Integer> row;
        
        public Row(int number, ArrayList<Integer> row)
        {
            this.number = number;
            this.row = row;
        }
    }
}
