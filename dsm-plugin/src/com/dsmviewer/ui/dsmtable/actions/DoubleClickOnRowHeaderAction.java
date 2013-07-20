package com.dsmviewer.ui.dsmtable.actions;

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
import com.dsmviewer.ui.DsmView;
import com.dsmviewer.ui.dsmtable.DsmBodyLayer;
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

        Dependable dependee = rowUnderCursor.getDependee();

        // Open sub-DSM if doubleclicked row has a 'package' scope
        if (DependencyScope.PACKAGES.equals(dependee.getScope())) {

            List<String> sourcesPaths = new LinkedList<String>();
            sourcesPaths.add(dependee.getFullyQualifiedName());

            DependencyMatrix dsMatrix = DtanglerRunner.computeDsMatrixFromSources(sourcesPaths,
                    DependencyScope.PACKAGES, DependencyScope.CLASSES,
                    DependencyMatrixOrdering.NATURAL_ORDERING);

            DsmView.getCurrent().showDsMatrix(dsMatrix, false, true);

        } else if (DependencyScope.CLASSES.equals(dependee.getScope())) {
            // if doubleclicked cell has a 'class' scope - open appropriate source file in Project Explorer
            EclipseUtils.openInJavaEditor(dependee);
        }
    }

}
