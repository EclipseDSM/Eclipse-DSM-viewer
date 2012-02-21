package com.dsmviewer.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dsmviewer.Activator;
import com.dsmviewer.ui.views.DSMView;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavtsev</a>
 * 
 */
public class PreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {
    
    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

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
        
        try {
            
            // ... PreferencePage controls composing
            
        } catch (RuntimeException e) {
            e.printStackTrace();
            logger.error("Exception occured: " + e.getMessage());
            DSMView.showErrorMessage("Exception occured: " + e.getMessage());  
        }
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
