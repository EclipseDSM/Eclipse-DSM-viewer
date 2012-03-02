package com.dsmviewer.dtangler;

import java.util.ArrayList;
import java.util.List;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Scope;
import org.dtangler.core.dsm.DsmCell;
import org.dtangler.core.dsm.DsmRow;

public enum DSMatrixModel {

    INSTANCE;

    private List<DsmRow> dsmRows;

    private DSMatrixModel() {
        
        dsmRows = new ArrayList<DsmRow>();

        Scope scope = new Scope() {

            @Override
            public int index() {
                return 0;
            }

            @Override
            public String getDisplayName() {
                return "scopeName";
            }
        };

        Dependable dep = new Dependable(scope, "fullyQualifiedName", "displayName1", 1);
        Dependable dep2 = new Dependable(scope, "fullyQualifiedName", "displayName2", 1);
        Dependable dep3 = new Dependable(scope, "fullyQualifiedName", "displayName3", 1);
        Dependable dep4 = new Dependable(scope, "fullyQualifiedName", "displayName4", 1);

        List<DsmCell> dsmCells = new ArrayList<DsmCell>();
        dsmCells.add(new DsmCell(dep, dep2, 1));
        dsmCells.add(new DsmCell(dep, dep2, 2));
        dsmCells.add(new DsmCell(dep, dep2, 3));
        dsmCells.add(new DsmCell(dep, dep2, 4));
        
        List<DsmCell> dsmCells1 = new ArrayList<DsmCell>();
        dsmCells1.add(new DsmCell(dep, dep2, 2));
        dsmCells1.add(new DsmCell(dep, dep2, 6));
        dsmCells1.add(new DsmCell(dep, dep2, 3));
        dsmCells1.add(new DsmCell(dep, dep2, 8));

        dsmRows.add(new DsmRow(dep, dsmCells));
        dsmRows.add(new DsmRow(dep2, dsmCells1));
        dsmRows.add(new DsmRow(dep3, dsmCells));
        dsmRows.add(new DsmRow(dep4, dsmCells1));
    }

    public List<DsmRow> getDsmRows() {
        return dsmRows;
    }

}
