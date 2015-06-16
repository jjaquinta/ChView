package jo.d2k.admin.rcp.viz.chview.prefs;

import jo.util.ui.utils.ColorUtils;
import jo.util.ui.utils.FontUtils;
import jo.util.ui.utils.LineAttributesLogic;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.DoubleUtils;
import jo.util.utils.obj.IntegerUtils;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;

import jo.chview.rcp.Activator;

public class ChViewPreferencesEclipse extends ChViewPreferencesBean
{
    public static final String SCOPE_GAP = "chview.scope.gap";
    public static final String SCOPE_HEIGHT1 = "chivew.scope.height1";
    public static final String SCOPE_HEIGHT2 = "chivew.scope.height2";
    public static final String SCOPE_HEIGHT3 = "chivew.scope.height3";
    public static final String SCOPE_COLOR = "chivew.scope.color";
    public static final String SCOPE_STYLE = "chivew.scope.style";
    public static final String GRID_GAP = "chivew.grid.gap";
    public static final String GRID_COLOR = "chivew.grid.color";
    public static final String GRID_STYLE = "chivew.grid.style";
    public static final String GRID_STEM_STYLE = "chivew.grid.style.stem";
    public static final String LINK_COLOR1 = "chivew.link.color.1";
    public static final String LINK_COLOR2 = "chivew.link.color.2";
    public static final String LINK_COLOR3 = "chivew.link.color.3";
    public static final String LINK_STYLE1 = "chivew.link.style.1";
    public static final String LINK_STYLE2 = "chivew.link.style.2";
    public static final String LINK_STYLE3 = "chivew.link.style.3";
    public static final String LINK_DIST1 = "chivew.link.dist.1";
    public static final String LINK_DIST2 = "chivew.link.dist.2";
    public static final String LINK_DIST3 = "chivew.link.dist.3";
    public static final String LINK_DIST4 = "chivew.link.dist.4";
    public static final String LINK_FONT = "chivew.link.font";
    public static final String STAR_FONT = "chview.star.font";
    public static final String STAR_FONT_COLOR = "chview.star.font.color";
    public static final String STAR_O_COLOR = "chview.star.o.color";
    public static final String STAR_B_COLOR = "chview.star.b.color";
    public static final String STAR_A_COLOR = "chview.star.a.color";
    public static final String STAR_F_COLOR = "chview.star.f.color";
    public static final String STAR_G_COLOR = "chview.star.g.color";
    public static final String STAR_K_COLOR = "chview.star.k.color";
    public static final String STAR_M_COLOR = "chview.star.m.color";
    public static final String STAR_L_COLOR = "chview.star.l.color";
    public static final String STAR_T_COLOR = "chview.star.t.color";
    public static final String STAR_Y_COLOR = "chview.star.y.color";
    public static final String STAR_NAME_COLUMN = "chview.star.name.column";
    public static final String STAR_0_RADIUS = "chview.star.0.radius";
    public static final String STAR_1_RADIUS = "chview.star.1.radius";
    public static final String STAR_2_RADIUS = "chview.star.2.radius";
    public static final String STAR_3_RADIUS = "chview.star.3.radius";
    public static final String STAR_4_RADIUS = "chview.star.4.radius";
    public static final String STAR_5_RADIUS = "chview.star.5.radius";
    public static final String SELECT_COLOR = "chview.select.color";
    public static final String SELECT_SHAPE = "chview.select.shape";
    public static final String FOCUS_COLOR = "chview.focus.color";
    public static final String FOCUS_SHAPE = "chview.focus.shape";
    public static final String ROUTE_1_COLOR = "chview.route.1.color";
    public static final String ROUTE_2_COLOR = "chview.route.2.color";
    public static final String ROUTE_3_COLOR = "chview.route.3.color";
    public static final String ROUTE_4_COLOR = "chview.route.4.color";
    public static final String ROUTE_5_COLOR = "chview.route.5.color";
    public static final String ROUTE_6_COLOR = "chview.route.6.color";
    public static final String ROUTE_7_COLOR = "chview.route.7.color";
    public static final String ROUTE_8_COLOR = "chview.route.8.color";
    public static final String ROUTE_1_STYLE = "chview.route.1.style";
    public static final String ROUTE_2_STYLE = "chview.route.2.style";
    public static final String ROUTE_3_STYLE = "chview.route.3.style";
    public static final String ROUTE_4_STYLE = "chview.route.4.style";
    public static final String ROUTE_5_STYLE = "chview.route.5.style";
    public static final String ROUTE_6_STYLE = "chview.route.6.style";
    public static final String ROUTE_7_STYLE = "chview.route.7.style";
    public static final String ROUTE_8_STYLE = "chview.route.8.style";
    public static final String ROUTE_1_NAME = "chview.route.1.name";
    public static final String ROUTE_2_NAME = "chview.route.2.name";
    public static final String ROUTE_3_NAME = "chview.route.3.name";
    public static final String ROUTE_4_NAME = "chview.route.4.name";
    public static final String ROUTE_5_NAME = "chview.route.5.name";
    public static final String ROUTE_6_NAME = "chview.route.6.name";
    public static final String ROUTE_7_NAME = "chview.route.7.name";
    public static final String ROUTE_8_NAME = "chview.route.8.name";

    public ChViewPreferencesEclipse()
    {
        Activator.getDefault().getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {                    
                    @Override
                    public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent ev)
                    {
                        ChViewPreferencesEclipse.this.propertyChange(ev);
                    }
                });
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        setScopeGap(store.getInt(SCOPE_GAP));
        setScopeHeight1(store.getInt(SCOPE_HEIGHT1));
        setScopeHeight2(store.getInt(SCOPE_HEIGHT2));
        setScopeHeight3(store.getInt(SCOPE_HEIGHT3));
        setScopeColor(ColorUtils.getColor(store.getString(SCOPE_COLOR)));
        setScopeStyle(LineAttributesLogic.fromString(store.getString(SCOPE_STYLE)));
        setGridGap(store.getDouble(GRID_GAP));
        setGridColor(ColorUtils.getColor(store.getString(GRID_COLOR)));
        setGridStyle(LineAttributesLogic.fromString(store.getString(GRID_STYLE)));
        setGridStemStyle(LineAttributesLogic.fromString(store.getString(GRID_STEM_STYLE)));
        setLinkColor1(ColorUtils.getColor(store.getString(LINK_COLOR1)));
        setLinkColor2(ColorUtils.getColor(store.getString(LINK_COLOR2)));
        setLinkColor3(ColorUtils.getColor(store.getString(LINK_COLOR3)));
        setLinkStyle1(LineAttributesLogic.fromString(store.getString(LINK_STYLE1)));
        setLinkStyle2(LineAttributesLogic.fromString(store.getString(LINK_STYLE2)));
        setLinkStyle3(LineAttributesLogic.fromString(store.getString(LINK_STYLE3)));
        setLinkDist1(store.getDouble(LINK_DIST1));
        setLinkDist2(store.getDouble(LINK_DIST2));
        setLinkDist3(store.getDouble(LINK_DIST3));
        setLinkDist4(store.getDouble(LINK_DIST4));
        setLinkFont(store.getString(LINK_FONT));
        setStarFont(store.getString(STAR_FONT));
        setStarFontColor(ColorUtils.getColor(store.getString(STAR_FONT_COLOR)));
        setStarOColor(ColorUtils.getColor(store.getString(STAR_O_COLOR)));
        setStarBColor(ColorUtils.getColor(store.getString(STAR_B_COLOR)));
        setStarAColor(ColorUtils.getColor(store.getString(STAR_A_COLOR)));
        setStarFColor(ColorUtils.getColor(store.getString(STAR_F_COLOR)));
        setStarGColor(ColorUtils.getColor(store.getString(STAR_G_COLOR)));
        setStarKColor(ColorUtils.getColor(store.getString(STAR_K_COLOR)));
        setStarMColor(ColorUtils.getColor(store.getString(STAR_M_COLOR)));
        setStarLColor(ColorUtils.getColor(store.getString(STAR_L_COLOR)));
        setStarTColor(ColorUtils.getColor(store.getString(STAR_T_COLOR)));
        setStarYColor(ColorUtils.getColor(store.getString(STAR_Y_COLOR)));
        setStarNameColumn(store.getString(STAR_NAME_COLUMN));
        setStar0Radius(store.getInt(STAR_0_RADIUS));
        setStar1Radius(store.getInt(STAR_1_RADIUS));
        setStar2Radius(store.getInt(STAR_2_RADIUS));
        setStar3Radius(store.getInt(STAR_3_RADIUS));
        setStar4Radius(store.getInt(STAR_4_RADIUS));
        setStar5Radius(store.getInt(STAR_5_RADIUS));
        setFocusColor(ColorUtils.getColor(store.getString(FOCUS_COLOR)));
        setFocusShape(store.getInt(FOCUS_SHAPE));
        setSelectColor(ColorUtils.getColor(store.getString(SELECT_COLOR)));
        setSelectShape(store.getInt(SELECT_SHAPE));
        setRoute1Color(ColorUtils.getColor(store.getString(ROUTE_1_COLOR)));
        setRoute2Color(ColorUtils.getColor(store.getString(ROUTE_2_COLOR)));
        setRoute3Color(ColorUtils.getColor(store.getString(ROUTE_3_COLOR)));
        setRoute4Color(ColorUtils.getColor(store.getString(ROUTE_4_COLOR)));
        setRoute5Color(ColorUtils.getColor(store.getString(ROUTE_5_COLOR)));
        setRoute6Color(ColorUtils.getColor(store.getString(ROUTE_6_COLOR)));
        setRoute7Color(ColorUtils.getColor(store.getString(ROUTE_7_COLOR)));
        setRoute8Color(ColorUtils.getColor(store.getString(ROUTE_8_COLOR)));
        setRoute1Style(LineAttributesLogic.fromString(store.getString(ROUTE_1_STYLE)));
        setRoute2Style(LineAttributesLogic.fromString(store.getString(ROUTE_2_STYLE)));
        setRoute3Style(LineAttributesLogic.fromString(store.getString(ROUTE_3_STYLE)));
        setRoute4Style(LineAttributesLogic.fromString(store.getString(ROUTE_4_STYLE)));
        setRoute5Style(LineAttributesLogic.fromString(store.getString(ROUTE_5_STYLE)));
        setRoute6Style(LineAttributesLogic.fromString(store.getString(ROUTE_6_STYLE)));
        setRoute7Style(LineAttributesLogic.fromString(store.getString(ROUTE_7_STYLE)));
        setRoute8Style(LineAttributesLogic.fromString(store.getString(ROUTE_8_STYLE)));
        setRoute1Name(store.getString(ROUTE_1_NAME));
        setRoute2Name(store.getString(ROUTE_2_NAME));
        setRoute3Name(store.getString(ROUTE_3_NAME));
        setRoute4Name(store.getString(ROUTE_4_NAME));
        setRoute5Name(store.getString(ROUTE_5_NAME));
        setRoute6Name(store.getString(ROUTE_6_NAME));
        setRoute7Name(store.getString(ROUTE_7_NAME));
        setRoute8Name(store.getString(ROUTE_8_NAME));
    }
    
    void propertyChange(org.eclipse.jface.util.PropertyChangeEvent ev)
    {
        DebugUtils.trace(ev.getProperty()+" -> "+ev.getNewValue());
        switch (ev.getProperty())
        {
            case SCOPE_GAP:
                setScopeGap(IntegerUtils.parseInt(ev.getNewValue()));
                break;
            case SCOPE_HEIGHT1:
                setScopeHeight1(IntegerUtils.parseInt(ev.getNewValue()));
                break;
            case SCOPE_HEIGHT2:
                setScopeHeight2(IntegerUtils.parseInt(ev.getNewValue()));
                break;
            case SCOPE_HEIGHT3:
                setScopeHeight3(IntegerUtils.parseInt(ev.getNewValue()));
                break;
            case SCOPE_COLOR:
                setScopeColor(ColorUtils.getColor(ev.getNewValue()));
                break;
            case SCOPE_STYLE:
                setScopeStyle(LineAttributesLogic.fromString(ev.getNewValue()));
                break;
            case GRID_GAP:
                setGridGap(DoubleUtils.parseDouble(ev.getNewValue()));
                break;
            case GRID_COLOR:
                setGridColor(ColorUtils.getColor(ev.getNewValue()));
                break;
            case GRID_STYLE:
                setGridStyle(LineAttributesLogic.fromString(ev.getNewValue()));
                break;
            case GRID_STEM_STYLE:
                setGridStemStyle(LineAttributesLogic.fromString(ev.getNewValue()));
                break;
            case LINK_COLOR1:
                setLinkColor1(ColorUtils.getColor(ev.getNewValue()));
                break;
            case LINK_COLOR2:
                setLinkColor2(ColorUtils.getColor(ev.getNewValue()));
                break;
            case LINK_COLOR3:
                setLinkColor3(ColorUtils.getColor(ev.getNewValue()));
                break;
            case LINK_STYLE1:
                setLinkStyle1(LineAttributesLogic.fromString(ev.getNewValue()));
                break;
            case LINK_STYLE2:
                setLinkStyle2(LineAttributesLogic.fromString(ev.getNewValue()));
                break;
            case LINK_STYLE3:
                setLinkStyle3(LineAttributesLogic.fromString(ev.getNewValue()));
                break;
            case LINK_DIST1:
                setLinkDist1(DoubleUtils.parseDouble(ev.getNewValue()));
                break;
            case LINK_DIST2:
                setLinkDist2(DoubleUtils.parseDouble(ev.getNewValue()));
                break;
            case LINK_DIST3:
                setLinkDist3(DoubleUtils.parseDouble(ev.getNewValue()));
                break;
            case LINK_DIST4:
                setLinkDist4(DoubleUtils.parseDouble(ev.getNewValue()));
                break;
            case LINK_FONT:
                setLinkFont(FontUtils.toString(ev.getNewValue()));
                break;
            case STAR_FONT:
                setStarFont(FontUtils.toString(ev.getNewValue()));
                break;
            case STAR_FONT_COLOR:
                setStarFontColor(ColorUtils.getColor(ev.getNewValue()));
                break;
            case STAR_O_COLOR:
                setStarOColor(ColorUtils.getColor(ev.getNewValue()));
                break;
            case STAR_B_COLOR:
                setStarBColor(ColorUtils.getColor(ev.getNewValue()));
                break;
            case STAR_A_COLOR:
                setStarAColor(ColorUtils.getColor(ev.getNewValue()));
                break;
            case STAR_F_COLOR:
                setStarFColor(ColorUtils.getColor(ev.getNewValue()));
                break;
            case STAR_G_COLOR:
                setStarGColor(ColorUtils.getColor(ev.getNewValue()));
                break;
            case STAR_K_COLOR:
                setStarKColor(ColorUtils.getColor(ev.getNewValue()));
                break;
            case STAR_M_COLOR:
                setStarMColor(ColorUtils.getColor(ev.getNewValue()));
                break;
            case STAR_L_COLOR:
                setStarLColor(ColorUtils.getColor(ev.getNewValue()));
                break;
            case STAR_T_COLOR:
                setStarTColor(ColorUtils.getColor(ev.getNewValue()));
                break;
            case STAR_Y_COLOR:
                setStarYColor(ColorUtils.getColor(ev.getNewValue()));
                break;
            case STAR_NAME_COLUMN:
                setStarNameColumn((String)(ev.getNewValue()));
                break;
            case STAR_0_RADIUS:
                setStar0Radius(IntegerUtils.parseInt(ev.getNewValue()));
                break;
            case STAR_1_RADIUS:
                setStar1Radius(IntegerUtils.parseInt(ev.getNewValue()));
                break;
            case STAR_2_RADIUS:
                setStar2Radius(IntegerUtils.parseInt(ev.getNewValue()));
                break;
            case STAR_3_RADIUS:
                setStar3Radius(IntegerUtils.parseInt(ev.getNewValue()));
                break;
            case STAR_4_RADIUS:
                setStar4Radius(IntegerUtils.parseInt(ev.getNewValue()));
                break;
            case STAR_5_RADIUS:
                setStar5Radius(IntegerUtils.parseInt(ev.getNewValue()));
                break;
            case FOCUS_COLOR:
                setFocusColor(ColorUtils.getColor(ev.getNewValue()));
                break;
            case FOCUS_SHAPE:
                setFocusShape(IntegerUtils.parseInt(ev.getNewValue()));
                break;
            case SELECT_COLOR:
                setSelectColor(ColorUtils.getColor(ev.getNewValue()));
                break;
            case SELECT_SHAPE:
                setSelectShape(IntegerUtils.parseInt(ev.getNewValue()));
                break;
            case ROUTE_1_COLOR:
                setRoute1Color(ColorUtils.getColor(ev.getNewValue()));
                break;
            case ROUTE_2_COLOR:
                setRoute1Color(ColorUtils.getColor(ev.getNewValue()));
                break;
            case ROUTE_3_COLOR:
                setRoute1Color(ColorUtils.getColor(ev.getNewValue()));
                break;
            case ROUTE_4_COLOR:
                setRoute1Color(ColorUtils.getColor(ev.getNewValue()));
                break;
            case ROUTE_5_COLOR:
                setRoute1Color(ColorUtils.getColor(ev.getNewValue()));
                break;
            case ROUTE_6_COLOR:
                setRoute1Color(ColorUtils.getColor(ev.getNewValue()));
                break;
            case ROUTE_7_COLOR:
                setRoute1Color(ColorUtils.getColor(ev.getNewValue()));
                break;
            case ROUTE_8_COLOR:
                setRoute1Color(ColorUtils.getColor(ev.getNewValue()));
                break;
            case ROUTE_1_NAME:
                setRoute1Name((String)(ev.getNewValue()));
                break;
            case ROUTE_2_NAME:
                setRoute1Name((String)(ev.getNewValue()));
                break;
            case ROUTE_3_NAME:
                setRoute1Name((String)(ev.getNewValue()));
                break;
            case ROUTE_4_NAME:
                setRoute1Name((String)(ev.getNewValue()));
                break;
            case ROUTE_5_NAME:
                setRoute1Name((String)(ev.getNewValue()));
                break;
            case ROUTE_6_NAME:
                setRoute1Name((String)(ev.getNewValue()));
                break;
            case ROUTE_7_NAME:
                setRoute1Name((String)(ev.getNewValue()));
                break;
            case ROUTE_8_NAME:
                setRoute1Name((String)(ev.getNewValue()));
                break;
            case ROUTE_1_STYLE:
                setRoute1Style(LineAttributesLogic.fromString(ev.getNewValue()));
                break;
            case ROUTE_2_STYLE:
                setRoute2Style(LineAttributesLogic.fromString(ev.getNewValue()));
                break;
            case ROUTE_3_STYLE:
                setRoute3Style(LineAttributesLogic.fromString(ev.getNewValue()));
                break;
            case ROUTE_4_STYLE:
                setRoute4Style(LineAttributesLogic.fromString(ev.getNewValue()));
                break;
            case ROUTE_5_STYLE:
                setRoute5Style(LineAttributesLogic.fromString(ev.getNewValue()));
                break;
            case ROUTE_6_STYLE:
                setRoute6Style(LineAttributesLogic.fromString(ev.getNewValue()));
                break;
            case ROUTE_7_STYLE:
                setRoute7Style(LineAttributesLogic.fromString(ev.getNewValue()));
                break;
            case ROUTE_8_STYLE:
                setRoute8Style(LineAttributesLogic.fromString(ev.getNewValue()));
                break;
        }
    }
}
