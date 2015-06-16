package jo.d2k.admin.rcp.viz.chview.prefs;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import jo.chview.rcp.Activator;

public class ChViewPreferencesInitializer extends AbstractPreferenceInitializer
{
    @Override
    public void initializeDefaultPreferences()
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setDefault(ChViewPreferencesEclipse.SCOPE_GAP, 10);
        store.setDefault(ChViewPreferencesEclipse.SCOPE_HEIGHT1, 5);
        store.setDefault(ChViewPreferencesEclipse.SCOPE_HEIGHT2, 10);
        store.setDefault(ChViewPreferencesEclipse.SCOPE_HEIGHT3, 20);
        store.setDefault(ChViewPreferencesEclipse.SCOPE_COLOR, "0,0,255");
        store.setDefault(ChViewPreferencesEclipse.GRID_GAP, 3.26);
        store.setDefault(ChViewPreferencesEclipse.GRID_COLOR, "0,0,255");
        store.setDefault(ChViewPreferencesEclipse.LINK_COLOR1, "0,255,0");
        store.setDefault(ChViewPreferencesEclipse.LINK_COLOR2, "0,192,0");
        store.setDefault(ChViewPreferencesEclipse.LINK_COLOR3, "0,128,0");
        store.setDefault(ChViewPreferencesEclipse.LINK_DIST1, ".25");
        store.setDefault(ChViewPreferencesEclipse.LINK_DIST2, "3");
        store.setDefault(ChViewPreferencesEclipse.LINK_DIST3, "5");
        store.setDefault(ChViewPreferencesEclipse.LINK_DIST4, "7");
        store.setDefault(ChViewPreferencesEclipse.LINK_FONT, "1|Segoe UI|8.25|0|WINDOWS|1|-11|0|0|0|400|0|0|0|0|3|2|1|34|Segoe UI;");
        store.setDefault(ChViewPreferencesEclipse.STAR_O_COLOR, "155,176,255");
        store.setDefault(ChViewPreferencesEclipse.STAR_B_COLOR, "187,204,255");
        store.setDefault(ChViewPreferencesEclipse.STAR_A_COLOR, "226,231,255");
        store.setDefault(ChViewPreferencesEclipse.STAR_F_COLOR, "255,248,248");
        store.setDefault(ChViewPreferencesEclipse.STAR_G_COLOR, "255,240,227");
        store.setDefault(ChViewPreferencesEclipse.STAR_K_COLOR, "255,152,51");
        store.setDefault(ChViewPreferencesEclipse.STAR_M_COLOR, "210,0,51");
        store.setDefault(ChViewPreferencesEclipse.STAR_L_COLOR, "204,0,153");
        store.setDefault(ChViewPreferencesEclipse.STAR_T_COLOR, "153,102,51");
        store.setDefault(ChViewPreferencesEclipse.STAR_Y_COLOR, "102,51,0");
        store.setDefault(ChViewPreferencesEclipse.STAR_FONT, "1|Segoe UI|10.25|0|WINDOWS|1|-11|0|0|0|400|0|0|0|0|3|2|1|34|Segoe UI;");
        store.setDefault(ChViewPreferencesEclipse.STAR_FONT_COLOR, "255,255,255");
        store.setDefault(ChViewPreferencesEclipse.STAR_NAME_COLUMN, "Name");
        store.setDefault(ChViewPreferencesEclipse.STAR_0_RADIUS, 2);
        store.setDefault(ChViewPreferencesEclipse.STAR_5_RADIUS, 4);
        store.setDefault(ChViewPreferencesEclipse.STAR_4_RADIUS, 6);
        store.setDefault(ChViewPreferencesEclipse.STAR_3_RADIUS, 8);
        store.setDefault(ChViewPreferencesEclipse.STAR_2_RADIUS, 10);
        store.setDefault(ChViewPreferencesEclipse.STAR_1_RADIUS, 12);
        store.setDefault(ChViewPreferencesEclipse.FOCUS_COLOR, "255,255,224");
        store.setDefault(ChViewPreferencesEclipse.FOCUS_SHAPE, "0");
        store.setDefault(ChViewPreferencesEclipse.SELECT_COLOR, "255,255,0");
        store.setDefault(ChViewPreferencesEclipse.SELECT_SHAPE, "1");
        store.setDefault(ChViewPreferencesEclipse.ROUTE_1_NAME, "Route 1");
        store.setDefault(ChViewPreferencesEclipse.ROUTE_2_NAME, "Route 2");
        store.setDefault(ChViewPreferencesEclipse.ROUTE_3_NAME, "Route 3");
        store.setDefault(ChViewPreferencesEclipse.ROUTE_4_NAME, "Route 4");
        store.setDefault(ChViewPreferencesEclipse.ROUTE_5_NAME, "Route 5");
        store.setDefault(ChViewPreferencesEclipse.ROUTE_6_NAME, "Route 6");
        store.setDefault(ChViewPreferencesEclipse.ROUTE_7_NAME, "Route 7");
        store.setDefault(ChViewPreferencesEclipse.ROUTE_8_NAME, "Route 8");
        store.setDefault(ChViewPreferencesEclipse.ROUTE_1_COLOR, "255,0,0");
        store.setDefault(ChViewPreferencesEclipse.ROUTE_2_COLOR, "0,255,0");
        store.setDefault(ChViewPreferencesEclipse.ROUTE_3_COLOR, "0,0,255");
        store.setDefault(ChViewPreferencesEclipse.ROUTE_4_COLOR, "255,255,0");
        store.setDefault(ChViewPreferencesEclipse.ROUTE_5_COLOR, "255,0,255");
        store.setDefault(ChViewPreferencesEclipse.ROUTE_6_COLOR, "0,255,255");
        store.setDefault(ChViewPreferencesEclipse.ROUTE_7_COLOR, "255,255,255");
        store.setDefault(ChViewPreferencesEclipse.ROUTE_8_COLOR, "192,192,192");

    }
}
