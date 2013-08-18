package com.dsmviewer.ui.actions;

import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.textui.SysoutWriter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.dsmviewer.Activator;
import com.dsmviewer.dsm.DependencyMatrix;
import com.dsmviewer.ui.dsmtable.DsmTableController;
import com.dsmviewer.utils.DtanglerUtils;

public class PrintViolationsAction extends Action {

	private DsmTableController dsmTableController;

	public PrintViolationsAction(DsmTableController dsmTableController) {
		this.dsmTableController = dsmTableController;
	}

	@Override
	public void run() {
		DependencyMatrix dependencyMatrix = dsmTableController.getDependencyMatrix();		
		DtanglerUtils.printViolations(dependencyMatrix);
	}

	@Override
	public String getToolTipText() {
		return "Print violations";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return Activator.getImageDescriptorFromRegistry("clear_all.gif");
	}

}
