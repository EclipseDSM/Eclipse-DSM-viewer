package com.dsmviewer.ui.dsmtable.action;

import java.util.LinkedList;
import java.util.List;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dsm.DsmRow;
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
import com.dsmviewer.utils.EclipseUtils;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DoubleClickOnRowHeaderAction implements IMouseAction {

    @SuppressWarnings("unused")
    private final Logger logger = Activator.getLogger(getClass());

    @Override
    public void run(NatTable natTable, MouseEvent event) {

        int rowIndex = natTable.getRowPositionByY(event.y) - 1;

        DsmBodyLayer bodyLayer = (DsmBodyLayer) ((GridLayer) natTable.getLayer()).getBodyLayer();
        DependencyMatrix dsm = bodyLayer.getDependencyMatrix();
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

        Dependable dependee = rowUnderCursor.getDependee();

        // if we clicked at 'package scope' matrix cell (dependency package --> package):
        if (dependee.getScope().getDisplayName().equals(DependencyScope.PACKAGES.getDisplayName())) {
            logger.debug("opening the sub-Dsm");

            List<String> sourcesPaths = new LinkedList<String>();

            sourcesPaths.add(dependee.getFullyQualifiedName());
            DependencyMatrix dsMatrix = DtanglerRunner.computeDsMatrixFromSources(sourcesPaths,
                    DependencyScope.PACKAGES, DependencyScope.CLASSES,
                    DependencyMatrixOrdering.NATURAL_ORDERING);
            DsmView.getInstance().showDsMatrix(dsMatrix);
        } else if (dependee.getScope().getDisplayName().equals(DependencyScope.CLASSES.getDisplayName())) {
            // if we`ve clicked at 'class scope' matrix cell - open appropriate class in Project Explorer
            EclipseUtils.openInEditor(dependee);
        }
    }

}
