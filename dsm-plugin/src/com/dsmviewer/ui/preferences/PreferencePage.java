package com.dsmviewer.ui.preferences;


import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.dsmviewer.Activator;
import com.dsmviewer.logging.Logger;

/**
 * Template for preferences page <is not used yet>
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavtsev</a>
 * 
 */
public class PreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    @SuppressWarnings("unused")
    private final Logger logger = Activator.getLogger(PreferencePage.class);

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

        // ... PreferencePage controls composing
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
