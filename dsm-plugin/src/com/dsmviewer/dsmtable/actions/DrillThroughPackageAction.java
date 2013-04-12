package com.dsmviewer.dsmtable.actions;

import java.util.LinkedList;
import java.util.List;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dsm.DsmRow;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.swt.events.MouseEvent;

import com.dsmviewer.Activator;
import com.dsmviewer.dsm.DependencyScope;
import com.dsmviewer.dsm.DsMatrix;
import com.dsmviewer.dsmtable.DsmBodyLayer;
import com.dsmviewer.dtangler.DtanglerRunner;
import com.dsmviewer.logging.Logger;
import com.dsmviewer.ui.views.DsmView;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DrillThroughPackageAction implements IMouseAction {

    @SuppressWarnings("unused")
    private final Logger logger = Activator.getLogger(getClass());

    @Override
    public void run(NatTable natTable, MouseEvent event) {

        int rowIndex = natTable.getRowPositionByY(event.y) - 1;

        DsmBodyLayer bodyLayer = (DsmBodyLayer) ((GridLayer) natTable.getLayer()).getBodyLayer();
        DsMatrix dsm = bodyLayer.getDsMatrix();
        DsmRow rowUnderCursor = dsm.getRows().get(rowIndex);

//        logger.info("DoubleClick. Source: " + event.getSource()
//                + ". Coordinates: " + event.x + " | " + event.y + ". Mouse button : " + event.button + "\n"
//                + "On Layer: " + bodyLayer
//                + "\n" + "Column position: " + columnIndex
//                + "\n" + "Row position: " + rowIndex
//                + "\n" + "On cell with data: " + valueUnderCursor
//                + "\n" + "And with dependency: " + cellUnderCursor.getDependency()
//                + "\n" + "Retriewed dep weight:" + cellUnderCursor.getDependencyWeight()
//                );

        Dependable dependant = rowUnderCursor.getDependee();

        // if we clicked at 'package scope' matrix cell (dependency package --> package):
        if (dependant.getScope().getDisplayName().equals(DependencyScope.PACKAGES.getDisplayName())) {
//            logger.warn("opening the sub-Dsm:");
            List<String> pathList = new LinkedList<String>();
            pathList.add(dependant.getFullyQualifiedName());
            DsMatrix dsMatrix = DtanglerRunner.computeDsMatrixFromSources(pathList, DependencyScope.CLASSES);
            DsmView.showDsMatrix(dsMatrix);
        }
    }

}
