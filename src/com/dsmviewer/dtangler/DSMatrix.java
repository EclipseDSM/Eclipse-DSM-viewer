package com.dsmviewer.dtangler;

import java.util.List;

import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsm.DsmRow;
import org.dtangler.core.dsmengine.DsmEngine;

public class DSMatrix {

    private Dsm dsm;

    public DSMatrix(DependencyGraph dependencyGraph) {
       dsm = new DsmEngine(dependencyGraph).createDsm();
    }

    public Dsm getDsm() {
        return dsm;
    }
    
    public List<DsmRow> getRows(){
        return dsm.getRows();
    }

    public int getSize(){
        return dsm.getRows().size();
    }
    
}