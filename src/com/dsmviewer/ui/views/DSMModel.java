package com.dsmviewer.ui.views;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsm.DsmRow;

/**
 * 
 * @author <a href="mailto:klaus.tannnenberg@gmail.com">Grigorov Aleksey</a>
 * 
 */
public class DSMModel {
    private ArrayList<Row> rows;
    private ArrayList<Label> labels;

    public ArrayList<Row> getRows() {
        return rows;
    }

    public ArrayList<Label> getLabels() {
        return labels;
    }

    public void createModel(Dsm dsm)
    {
        rows = new ArrayList<Row>();
        labels = new ArrayList<Label>();

        // получение имен всех классов
        SortedSet<String> temp = new TreeSet<String>();
        for (DsmRow row : dsm.getRows())
        {
            String curPath = row.getDependee().getFullyQualifiedName();
            curPath = curPath.substring(curPath.lastIndexOf(':') + 1).trim();
            while (curPath.length() > 1) {
                temp.add(curPath);
                int index = curPath.lastIndexOf('.');
                if (index >= 0)
                    curPath = curPath.substring(0, index);
                else
                    break;
            }
        }

        // создание подписей к матрице для всех внутренностей пакета
        int pos = 0;
        for (String cur : temp) {
            String shortname = (cur.lastIndexOf('.') >= 0) ? cur.substring(cur.lastIndexOf('.') + 1) : cur;
            labels.add(new Label(pos, 0, cur, shortname, true));
            pos++;
        }
        labels.trimToSize();

        // расчет дерева
        buildTree(labels, 0, labels.size() - 1, -1);

        // selecting active indexes in labels (these ones, data for which was calculated by drangler)
        int[] active = new int[dsm.getRows().size()];
        rows.ensureCapacity(labels.size());
        
        // TODO: Check is dtangler output DS-matrix is sorted by rows in natural order!
        for (int n = 0, m = 0; n < labels.size(); n++) {
            // сразу создаем место под создание новой DSM
            ArrayList<Integer> row = new ArrayList<Integer>(labels.size());
            for (int h = 0; h < labels.size(); h++) {
                row.add(0);
            }
            rows.add(new Row(n, row));
            if (m < active.length && labels.get(n).fold == 0) {
                active[m++] = n;
            }
        }
        // scalling old dsm to new size
        for (int n = 0; n < active.length; n++) {
            for (int m = 0; m < active.length; m++) {
                /* находим в исходной dsm ячейку с индексами [n, m]
                 * копируем данные в новую dsm в место с индексом [active[n], active[m]]
                 * то есть в те места новой dsm, где находятся объекты, которые существовали в исходной dsm
                 */
                int element = dsm.getRows().get(n).getCells().get(m).getDependencyWeight();
                rows.get(active[n]).row.set(active[m], element);
            }
        }

        buildDSM(rows, labels, 0, labels.size() - 1);

        @SuppressWarnings("unused")
        int n = 0; // debug anchor
    }

    private void buildTree(ArrayList<Label> array, int index, int last, int parent) {
        if (index >= array.size()) {
            if (parent < array.size())
                buildTree(array, parent + 2, last, parent + 1);
            return;
        }
        else if (parent == -1) {
            for (Label item : array) {
                item.fold = 0;
            }
            buildTree(array, index + 1, last, index);
            return;
        }
        else {
            if (array.get(index).fullname.startsWith(array.get(parent).fullname + ".")) {
                array.get(parent).fold++;
                buildTree(array, index + 1, last, parent);
            }
            else {
                buildTree(array, parent + 2, last, parent + 1);
            }
        }
    }

    private void buildDSM(ArrayList<Row> rows, ArrayList<Label> labels, int stIdx, int edIdx) {
        // TODO: заполнение пустых элементов, сейчас проверю null они в ArrayList или нет
    }

    protected class Label {
        private int number;
        private int fold;
        private boolean folded;
        private String fullname;
        private String shortname;

        public Label(int number, int fold, String fullname, String shortname, boolean folded)
        {
            this.number = number;
            this.folded = folded;
            this.fold = fold;
            this.fullname = fullname;
            this.shortname = shortname;
        }

        public int getNumber() {
            return number;
        }

        public int getFold() {
            return fold;
        }

        public String getFullname() {
            return shortname;
        }

        public String getShortname() {
            return shortname;
        }

        public boolean isFolded() {
            return folded;
        }

        public void setFolded(boolean folded) {
            this.folded = folded;
        }
    }

    protected class Row {
        int number;
        ArrayList<Integer> row;

        public Row(int number, ArrayList<Integer> row)
        {
            this.number = number;
            this.row = row;
        }
    }
}
