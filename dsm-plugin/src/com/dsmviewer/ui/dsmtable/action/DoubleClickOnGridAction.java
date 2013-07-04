package com.dsmviewer.ui.dsmtable.action;

import java.util.LinkedList;
import java.util.List;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dsm.DsmCell;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.swt.events.MouseEvent;

import com.dsmviewer.Activator;
import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.dsm.DependencyMatrixOrdering;
import com.dsmviewer.dsm.DependencyScope;
import com.dsmviewer.dtangler.DtanglerRunner;
import com.dsmviewer.logging.Logger;
import com.dsmviewer.ui.dsmtable.DsmBodyLayer;
import com.dsmviewer.ui.views.DsmView;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DoubleClickOnGridAction implements IMouseAction {

    @SuppressWarnings("unused")
    private final Logger logger = Activator.getLogger(getClass());

    @Override
    public void run(NatTable natTable, MouseEvent event) {

        int columnIndex = natTable.getColumnPositionByX(event.x) - 1;
        int rowIndex = natTable.getRowPositionByY(event.y) - 1;
//      Object valueUnderCursor = natTable.getDataValueByPosition(columnIndex, rowIndex);

        DsmBodyLayer bodyLayer = (DsmBodyLayer) ((GridLayer) natTable.getLayer()).getBodyLayer();
        DependencyMatrix dsm = bodyLayer.getDependencyMatrix();
        DsmCell cellUnderCursor = dsm.getCell(rowIndex, columnIndex);

        logger.info("DoubleClick. Source: " + event.getSource()
                + ". Coordinates: " + event.x + " | " + event.y + ". Mouse button : " + event.button + "\n"
                + "On Layer: " + bodyLayer
                + "\n" + "Column position: " + columnIndex
                + "\n" + "Row position: " + rowIndex
                + "\n" + "And with dependency: " + cellUnderCursor.getDependency()
                + "\n" + "Retriewed dep weight:" + cellUnderCursor.getDependencyWeight()
                );

        Dependable dependant = cellUnderCursor.getDependency().getDependant();
        Dependable dependee = cellUnderCursor.getDependency().getDependee();

        // if we clicked at 'package scope' matrix cell (dependency package --> package):
        if (dependant.getScope().getDisplayName().equals(DependencyScope.PACKAGES.getDisplayName())
                && dependant.getScope() == dependee.getScope()) {

            List<String> pathList = new LinkedList<String>();
            pathList.add(dependant.getFullyQualifiedName());
            pathList.add(dependee.getFullyQualifiedName());
            DependencyMatrix dsMatrix = DtanglerRunner.computeDsMatrixFromSources(pathList,
                    DependencyScope.PACKAGES, DependencyScope.CLASSES,
                    DependencyMatrixOrdering.NATURAL_ORDERING);
            DsmView.getInstance().showDsMatrix(dsMatrix);
        }
    }

}
