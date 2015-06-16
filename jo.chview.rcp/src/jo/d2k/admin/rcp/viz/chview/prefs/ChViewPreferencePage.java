package jo.d2k.admin.rcp.viz.chview.prefs;

import jo.chview.rcp.Activator;
import jo.util.ui.utils.ColorUtils;
import jo.util.ui.utils.GridUtils;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import chuck.terran.admin.ui.jface.LineFieldEditor;

public class ChViewPreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage
{
    private ColorFieldEditor mGridColor;
    private ColorFieldEditor mScopeColor;
    private LineFieldEditor mGridStyle;
    private LineFieldEditor mGridStemStyle;
    private LineFieldEditor mScopeStyle;
    
    public ChViewPreferencePage()
    {
        super(FieldEditorPreferencePage.GRID);
    }

    @Override
    public void init(IWorkbench wb)
    {
        // Set the preference store for the preference page.
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        setPreferenceStore(store);
        setDescription("Settings for the visualization style");
    }

    @Override
    protected Control createContents(Composite parent)
    {
        Control ret = super.createContents(parent);
        updateColors();
        return ret;
    }

    @Override
    protected void createFieldEditors()
    {
        GridUtils.makeLabel(getFieldEditorParent(), "Grid:", "3x1 fill=h");
        StringFieldEditor gridGap = new StringFieldEditor(ChViewPreferencesEclipse.GRID_GAP,
                "Gap between grid lines:",
                getFieldEditorParent());
        gridGap.setEmptyStringAllowed(false);
        addField(gridGap); 
        mGridColor = new ColorFieldEditor(ChViewPreferencesEclipse.GRID_COLOR,
                "Color:",
                getFieldEditorParent());
        addField(mGridColor);
        mGridStyle = new LineFieldEditor(ChViewPreferencesEclipse.GRID_STYLE,
                "Style:",
                getFieldEditorParent());
        addField(mGridStyle);
        mGridStemStyle = new LineFieldEditor(ChViewPreferencesEclipse.GRID_STEM_STYLE,
                "Stem:",
                getFieldEditorParent());
        addField(mGridStemStyle);

        GridUtils.makeLabel(getFieldEditorParent(), "Scope:", "3x1 fill=h");
        IntegerFieldEditor scopeGap = new IntegerFieldEditor(ChViewPreferencesEclipse.SCOPE_GAP,
                "Gap between Scope ticks:",
                getFieldEditorParent());
        scopeGap.setValidRange(5, 50);
        addField(scopeGap); 
        IntegerFieldEditor scopeHeight1 = new IntegerFieldEditor(ChViewPreferencesEclipse.SCOPE_HEIGHT1,
                "Height of small ticks:",
                getFieldEditorParent());
        scopeHeight1.setValidRange(5, 50);
        addField(scopeHeight1); 
        IntegerFieldEditor scopeHeight2 = new IntegerFieldEditor(ChViewPreferencesEclipse.SCOPE_HEIGHT2,
                "Height of large ticks:",
                getFieldEditorParent());
        scopeHeight2.setValidRange(5, 50);
        addField(scopeHeight2); 
        IntegerFieldEditor scopeHeight3 = new IntegerFieldEditor(ChViewPreferencesEclipse.SCOPE_HEIGHT3,
                "Height of crosshair:",
                getFieldEditorParent());
        scopeHeight3.setValidRange(5, 50);
        addField(scopeHeight3); 
        mScopeColor = new ColorFieldEditor(ChViewPreferencesEclipse.SCOPE_COLOR,
                "Color:",
                getFieldEditorParent());
        addField(mScopeColor);
        mScopeStyle = new LineFieldEditor(ChViewPreferencesEclipse.SCOPE_STYLE,
                "Style:",
                getFieldEditorParent());
        addField(mScopeStyle);
        
        mGridStyle.getLineSelector().setBGColor(ColorUtils.getColor("black"));
        mGridStemStyle.getLineSelector().setBGColor(ColorUtils.getColor("black"));
        mScopeStyle.getLineSelector().setBGColor(ColorUtils.getColor("black"));
        IPropertyChangeListener pcl = new IPropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent event)
            {
                updateColors();
            }
        };
        mGridColor.getColorSelector().addListener(pcl);
        mScopeColor.getColorSelector().addListener(pcl);
    }

    private void updateColors()
    {
        mGridStyle.getLineSelector().setFGColor(ColorUtils.getColor(mGridColor.getColorSelector().getColorValue()));
        mGridStemStyle.getLineSelector().setFGColor(ColorUtils.getColor(mGridColor.getColorSelector().getColorValue()));
        mScopeStyle.getLineSelector().setFGColor(ColorUtils.getColor(mScopeColor.getColorSelector().getColorValue()));
    }
    
    @Override
    protected void performDefaults()
    {        
        super.performDefaults();
        updateColors();
    }

}
