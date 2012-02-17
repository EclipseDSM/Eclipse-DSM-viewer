package com.dsmviewer.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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
