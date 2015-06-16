package jo.d2k.admin.rcp.viz.chview.prefs;

import java.util.List;
import java.util.StringTokenizer;

import jo.chview.rcp.Activator;
import jo.d2k.data.data.StarColumn;
import jo.d2k.data.logic.StarColumnLogic;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.DebugUtils;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class StarPreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage
{
    private FontColorFieldEditor    mStarFont;
    private Combo                   mStarName;
    
    private List<StarColumn>        mColumns;
    
    private Composite left;
    private Composite right;
    
    public StarPreferencePage()
    {
        super(FieldEditorPreferencePage.GRID);
    }

    @Override
    public void init(IWorkbench wb)
    {
        // Set the preference store for the preference page.
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        setPreferenceStore(store);
        setDescription("Settings for the Star styles");
    }

    @Override
    protected void createFieldEditors()
    {
        mStarFont = new FontColorFieldEditor(ChViewPreferencesEclipse.STAR_FONT,
                "Font:",
                getFieldEditorParent());
        addField(mStarFont);
        StringTokenizer st = new StringTokenizer(getPreferenceStore().getDefaultString(ChViewPreferencesEclipse.STAR_FONT_COLOR), ",");
        RGB starFontColor = new RGB(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())); 
        mStarFont.setChosenColor(starFontColor);

        Composite insert = new Composite(getFieldEditorParent(), SWT.NULL);
        GridUtils.setLayoutData(insert, "3x1 fill=hv");
        insert.setLayout(new GridLayout(2, true));       
        left = new Composite(insert, SWT.NULL);
        GridUtils.setLayoutData(left, "fill=hv");
        left.setLayout(new GridLayout(3, false));
        right = new Composite(insert, SWT.NULL);
        GridUtils.setLayoutData(right, "fill=hv");
        right.setLayout(new GridLayout(3, false));
        DebugUtils.trace("Left: "+((GridLayout)left.getLayout()).numColumns);
        DebugUtils.trace("Right: "+((GridLayout)right.getLayout()).numColumns);
        
        ColorFieldEditor starOColor = new ColorFieldEditor(ChViewPreferencesEclipse.STAR_O_COLOR,
                "O Color:",
                left);
        addField(starOColor);
        ColorFieldEditor starBColor = new ColorFieldEditor(ChViewPreferencesEclipse.STAR_B_COLOR,
                "B Color:",
                left);
        addField(starBColor);
        ColorFieldEditor starAColor = new ColorFieldEditor(ChViewPreferencesEclipse.STAR_A_COLOR,
                "A Color:",
                left);
        addField(starAColor);
        ColorFieldEditor starFColor = new ColorFieldEditor(ChViewPreferencesEclipse.STAR_F_COLOR,
                "F Color:",
                left);
        addField(starFColor);
        ColorFieldEditor starGColor = new ColorFieldEditor(ChViewPreferencesEclipse.STAR_G_COLOR,
                "G Color:",
                left);
        addField(starGColor);
        ColorFieldEditor starKColor = new ColorFieldEditor(ChViewPreferencesEclipse.STAR_K_COLOR,
                "K Color:",
                left);
        addField(starKColor);
        ColorFieldEditor starMColor = new ColorFieldEditor(ChViewPreferencesEclipse.STAR_M_COLOR,
                "M Color:",
                left);
        addField(starMColor);
        ColorFieldEditor starLColor = new ColorFieldEditor(ChViewPreferencesEclipse.STAR_L_COLOR,
                "L Color:",
                left);
        addField(starLColor);
        ColorFieldEditor starTColor = new ColorFieldEditor(ChViewPreferencesEclipse.STAR_T_COLOR,
                "T Color:",
                left);
        addField(starTColor);
        ColorFieldEditor starYColor = new ColorFieldEditor(ChViewPreferencesEclipse.STAR_Y_COLOR,
                "Y Color:",
                left);
        addField(starYColor);

        IntegerFieldEditor star0Radius = new IntegerFieldEditor(ChViewPreferencesEclipse.STAR_0_RADIUS,
                "Class D Radius:",
                right);
        star0Radius.setValidRange(1, 50);
        addField(star0Radius); 
        IntegerFieldEditor star1Radius = new IntegerFieldEditor(ChViewPreferencesEclipse.STAR_5_RADIUS,
                "Class V Radius:",
                right);
        star1Radius.setValidRange(1, 50);
        addField(star1Radius); 
        IntegerFieldEditor star2Radius = new IntegerFieldEditor(ChViewPreferencesEclipse.STAR_4_RADIUS,
                "Class IV Radius:",
                right);
        star2Radius.setValidRange(1, 50);
        addField(star2Radius); 
        IntegerFieldEditor star3Radius = new IntegerFieldEditor(ChViewPreferencesEclipse.STAR_3_RADIUS,
                "Class III Radius:",
                right);
        star3Radius.setValidRange(1, 50);
        addField(star3Radius); 
        IntegerFieldEditor star4Radius = new IntegerFieldEditor(ChViewPreferencesEclipse.STAR_2_RADIUS,
                "Class II Radius:",
                right);
        star4Radius.setValidRange(1, 50);
        addField(star4Radius); 
        IntegerFieldEditor star5Radius = new IntegerFieldEditor(ChViewPreferencesEclipse.STAR_1_RADIUS,
                "Class I Radius:",
                right);
        star5Radius.setValidRange(1, 50);
        addField(star5Radius); 

        mColumns = StarColumnLogic.getPotentialColumns();
        GridUtils.makeLabel(getFieldEditorParent(), "Name field:", "");
        mStarName = GridUtils.makeCombo(getFieldEditorParent(), mColumns, "2x1 fill=h");
        StarColumn sel = StarColumnLogic.getColumn(getPreferenceStore().getString(ChViewPreferencesEclipse.STAR_NAME_COLUMN));
        if (sel != null)
            mStarName.select(mColumns.indexOf(sel));
        else
            mStarName.select(0);
    }

    @Override
    public boolean performOk()
    {
        RGB starFontColor = mStarFont.getChosenColor();
        getPreferenceStore().setValue(ChViewPreferencesEclipse.STAR_FONT_COLOR, starFontColor.red+","+starFontColor.green+","+starFontColor.blue);
        StarColumn sel = mColumns.get(mStarName.getSelectionIndex());
        getPreferenceStore().setValue(ChViewPreferencesEclipse.STAR_NAME_COLUMN, sel.getID());
        return super.performOk();
    }
    
    @Override
    protected void performApply()
    {
        RGB starFontColor = mStarFont.getChosenColor();
        getPreferenceStore().setValue(ChViewPreferencesEclipse.STAR_FONT_COLOR, starFontColor.red+","+starFontColor.green+","+starFontColor.blue);
        StarColumn sel = mColumns.get(mStarName.getSelectionIndex());
        getPreferenceStore().setValue(ChViewPreferencesEclipse.STAR_NAME_COLUMN, sel.getID());
        super.performApply();
    }
    
    @Override
    protected void performDefaults()
    {
        StringTokenizer st = new StringTokenizer(getPreferenceStore().getDefaultString(ChViewPreferencesEclipse.STAR_FONT_COLOR), ",");
        RGB starFontColor = new RGB(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())); 
        mStarFont.setChosenColor(starFontColor);
        mStarName.select(0);
        super.performDefaults();
        left.getParent().layout();
        DebugUtils.trace("Left: "+((GridLayout)left.getLayout()).numColumns);
        for (Control c : left.getChildren())
            DebugUtils.trace("  "+c.getClass().getSimpleName()+" - "+GridUtils.toString((GridData)c.getLayoutData()));
        DebugUtils.trace("Right: "+((GridLayout)right.getLayout()).numColumns);
        for (Control c : right.getChildren())
            DebugUtils.trace("  "+c.getClass().getSimpleName()+" - "+GridUtils.toString((GridData)c.getLayoutData()));
    }
}
