package jo.d2k.admin.rcp.viz.chview.prefs;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import jo.chview.rcp.Activator;

public class SelectPreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage
{
    
    public SelectPreferencePage()
    {
        super(FieldEditorPreferencePage.GRID);
    }

    @Override
    public void init(IWorkbench wb)
    {
        // Set the preference store for the preference page.
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        setPreferenceStore(store);
        setDescription("Settings for general UI");
    }

    @Override
    protected void createFieldEditors()
    {
        ColorFieldEditor focusColor = new ColorFieldEditor(ChViewPreferencesEclipse.FOCUS_COLOR,
                "Focused Star Color:",
                getFieldEditorParent());
        addField(focusColor);
        ComboFieldEditor focusShape = new ComboFieldEditor(ChViewPreferencesEclipse.FOCUS_SHAPE, 
                "Focused Star Shape:", 
                ChViewPreferencesBean.FOCUS_SHAPE_LABEL_MAP, 
                getFieldEditorParent());
        addField(focusShape);
        ColorFieldEditor selectColor = new ColorFieldEditor(ChViewPreferencesEclipse.SELECT_COLOR,
                "Selected Star Color:",
                getFieldEditorParent());
        addField(selectColor);
        ComboFieldEditor selectShape = new ComboFieldEditor(ChViewPreferencesEclipse.SELECT_SHAPE, 
                "Selected Star Shape:", 
                ChViewPreferencesBean.FOCUS_SHAPE_LABEL_MAP, 
                getFieldEditorParent());
        addField(selectShape);
    }

}
