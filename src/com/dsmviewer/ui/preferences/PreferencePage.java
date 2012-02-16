package com.dsmviewer.ui.preferences;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.dsmviewer.Activator;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavtsev</a>
 *
 */
public class PreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    private IPreferenceStore store;

    @Override
    public void init(final IWorkbench workbench) {
        store = Activator.getInstance().getPreferenceStore();
        setPreferenceStore(store);
        setDescription("DSM-viewer settings here.");
    }

    @Override
    protected void createFieldEditors() {
        GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 15;
        Composite composite = getFieldEditorParent();
        composite.setLayout(gridLayout);
    }


    @Override
    protected void performApply() {
        super.performApply();
    }

    @Override
    public boolean performOk() {
        boolean isSaved = super.performOk();
        return isSaved;
    }
}
