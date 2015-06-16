package jo.d2k.admin.rcp.viz.chview.prefs;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import chuck.terran.admin.ui.jface.LineFieldEditor;
import jo.chview.rcp.Activator;
import jo.util.ui.utils.ColorUtils;
import jo.util.ui.utils.GridUtils;

public class RoutePreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage
{
    private ColorFieldEditor[] mRouteColors;
    private LineFieldEditor[] mRouteStyles;

    private Composite left;
    private Composite right;

    public RoutePreferencePage()
    {
        super(FieldEditorPreferencePage.GRID);
    }

    @Override
    protected Control createContents(Composite parent)
    {
        Control ret = super.createContents(parent);
        updateColors();
        return ret;
    }

    @Override
    public void init(IWorkbench wb)
    {
        // Set the preference store for the preference page.
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        setPreferenceStore(store);
        setDescription("Settings for Star routes");
    }

    private static final String[][] ROUTE_IDS = {
        { ChViewPreferencesEclipse.ROUTE_1_NAME, ChViewPreferencesEclipse.ROUTE_1_COLOR, ChViewPreferencesEclipse.ROUTE_1_STYLE },
        { ChViewPreferencesEclipse.ROUTE_2_NAME, ChViewPreferencesEclipse.ROUTE_2_COLOR, ChViewPreferencesEclipse.ROUTE_2_STYLE },
        { ChViewPreferencesEclipse.ROUTE_3_NAME, ChViewPreferencesEclipse.ROUTE_3_COLOR, ChViewPreferencesEclipse.ROUTE_3_STYLE },
        { ChViewPreferencesEclipse.ROUTE_4_NAME, ChViewPreferencesEclipse.ROUTE_4_COLOR, ChViewPreferencesEclipse.ROUTE_4_STYLE },
        { ChViewPreferencesEclipse.ROUTE_5_NAME, ChViewPreferencesEclipse.ROUTE_5_COLOR, ChViewPreferencesEclipse.ROUTE_5_STYLE },
        { ChViewPreferencesEclipse.ROUTE_6_NAME, ChViewPreferencesEclipse.ROUTE_6_COLOR, ChViewPreferencesEclipse.ROUTE_6_STYLE },
        { ChViewPreferencesEclipse.ROUTE_7_NAME, ChViewPreferencesEclipse.ROUTE_7_COLOR, ChViewPreferencesEclipse.ROUTE_7_STYLE },
        { ChViewPreferencesEclipse.ROUTE_8_NAME, ChViewPreferencesEclipse.ROUTE_8_COLOR, ChViewPreferencesEclipse.ROUTE_8_STYLE },
    };
    
    @Override
    protected void createFieldEditors()
    {
        mRouteColors = new ColorFieldEditor[ROUTE_IDS.length];
        mRouteStyles = new LineFieldEditor[ROUTE_IDS.length];
        IPropertyChangeListener pcl = new IPropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent event)
            {
                updateColors();
            }
        };
        Composite insert = new Composite(getFieldEditorParent(), SWT.NULL);
        GridUtils.setLayoutData(insert, "3x1 fill=hv");
        insert.setLayout(new GridLayout(2, true));       
        left = new Composite(insert, SWT.NULL);
        GridUtils.setLayoutData(left, "fill=hv");
        left.setLayout(new GridLayout(3, false));
        right = new Composite(insert, SWT.NULL);
        GridUtils.setLayoutData(right, "fill=hv");
        right.setLayout(new GridLayout(3, false));

        for (int i = 0; i < ROUTE_IDS.length; i++)
        {
            Composite parent = ((i%2) == 0) ? left : right;
            StringFieldEditor route1Name = new StringFieldEditor(ROUTE_IDS[i][0],
                    "Route "+(i+1)+":",
                    parent);
            addField(route1Name); 
            mRouteColors[i] = new ColorFieldEditor(ROUTE_IDS[i][1],
                    "",
                    parent);
            addField(mRouteColors[i]);
            mRouteStyles[i] = new LineFieldEditor(ROUTE_IDS[i][2],
                    "",
                    parent);
            addField(mRouteStyles[i]);
            mRouteStyles[i].getLineSelector().setBGColor(ColorUtils.getColor("black"));            
            mRouteColors[i].getColorSelector().addListener(pcl);
        }
    }

    private void updateColors()
    {
        for (int i = 0; i < mRouteStyles.length; i++)
            mRouteStyles[i].getLineSelector().setFGColor(ColorUtils.getColor(mRouteColors[i].getColorSelector().getColorValue()));
    }
    
    @Override
    protected void performDefaults()
    {        
        super.performDefaults();
        updateColors();
    }

}
