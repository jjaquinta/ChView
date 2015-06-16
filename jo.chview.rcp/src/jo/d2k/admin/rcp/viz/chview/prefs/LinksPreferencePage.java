package jo.d2k.admin.rcp.viz.chview.prefs;

import jo.chview.rcp.Activator;
import jo.util.ui.utils.ColorUtils;
import jo.util.ui.utils.GridUtils;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import chuck.terran.admin.ui.jface.LineFieldEditor;

public class LinksPreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage
{
    private ColorFieldEditor mLinkColor1;
    private ColorFieldEditor mLinkColor2;
    private ColorFieldEditor mLinkColor3;
    private LineFieldEditor mShortLine;
    private LineFieldEditor mMedLine;
    private LineFieldEditor mLongLine;
    
    public LinksPreferencePage()
    {
        super(FieldEditorPreferencePage.GRID);
    }

    @Override
    public void init(IWorkbench wb)
    {
        // Set the preference store for the preference page.
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        setPreferenceStore(store);
        setDescription("Settings for length and color of links");
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
        StringFieldEditor linkDist1 = new StringFieldEditor(ChViewPreferencesEclipse.LINK_DIST1,
                "Minimum:",
                getFieldEditorParent());
        addField(linkDist1);
        FontColorFieldEditor linkFont = new FontColorFieldEditor(ChViewPreferencesEclipse.LINK_FONT,
                "Font:",
                getFieldEditorParent());
        addField(linkFont);

        GridUtils.makeLabel(getFieldEditorParent(), "Short Links:", "3x1 fill=h");
        mLinkColor1 = new ColorFieldEditor(ChViewPreferencesEclipse.LINK_COLOR1,
                "Color:",
                getFieldEditorParent());
        addField(mLinkColor1);
        StringFieldEditor linkDist2 = new StringFieldEditor(ChViewPreferencesEclipse.LINK_DIST2,
                "Distance:",
                getFieldEditorParent());
        addField(linkDist2);
        mShortLine = new LineFieldEditor(ChViewPreferencesEclipse.LINK_STYLE1, "Style:", getFieldEditorParent());
        addField(mShortLine);

        GridUtils.makeLabel(getFieldEditorParent(), "Medium Links:", "3x1 fill=h");
        mLinkColor2 = new ColorFieldEditor(ChViewPreferencesEclipse.LINK_COLOR2,
                "Color:",
                getFieldEditorParent());
        addField(mLinkColor2);
        StringFieldEditor linkDist3 = new StringFieldEditor(ChViewPreferencesEclipse.LINK_DIST3,
                "Distance",
                getFieldEditorParent());
        addField(linkDist3);
        mMedLine = new LineFieldEditor(ChViewPreferencesEclipse.LINK_STYLE2, "Style:", getFieldEditorParent());
        addField(mMedLine);
        
        GridUtils.makeLabel(getFieldEditorParent(), "Long Links:", "3x1 fill=h");
        mLinkColor3 = new ColorFieldEditor(ChViewPreferencesEclipse.LINK_COLOR3,
                "Long:",
                getFieldEditorParent());
        addField(mLinkColor3);
        StringFieldEditor linkDist4 = new StringFieldEditor(ChViewPreferencesEclipse.LINK_DIST4,
                "Long:",
                getFieldEditorParent());
        addField(linkDist4);
        mLongLine = new LineFieldEditor(ChViewPreferencesEclipse.LINK_STYLE3, "Style:", getFieldEditorParent());
        addField(mLongLine);
                
        mShortLine.getLineSelector().setBGColor(ColorUtils.getColor("black"));
        mMedLine.getLineSelector().setBGColor(ColorUtils.getColor("black"));
        mLongLine.getLineSelector().setBGColor(ColorUtils.getColor("black"));
        IPropertyChangeListener pcl = new IPropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent event)
            {
                updateColors();
            }
        };
        mLinkColor1.getColorSelector().addListener(pcl);
        mLinkColor2.getColorSelector().addListener(pcl);
        mLinkColor3.getColorSelector().addListener(pcl);
    }

    private void updateColors()
    {
        mShortLine.getLineSelector().setFGColor(ColorUtils.getColor(mLinkColor1.getColorSelector().getColorValue()));
        mMedLine.getLineSelector().setFGColor(ColorUtils.getColor(mLinkColor2.getColorSelector().getColorValue()));
        mLongLine.getLineSelector().setFGColor(ColorUtils.getColor(mLinkColor3.getColorSelector().getColorValue()));
    }
    
    @Override
    protected void performDefaults()
    {        
        super.performDefaults();
        updateColors();
    }
}
