package jo.d2k.admin.rcp.viz.chview.logic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import jo.d2k.admin.rcp.viz.chview.ChViewThemeBean;
import jo.d2k.admin.rcp.viz.chview.prefs.ChViewPreferencesBean;
import jo.d2k.data.data.MetadataBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.MetadataLogic;
import jo.d2k.data.logic.RuntimeLogic;
import jo.d2k.data.logic.StarLogic;
import jo.util.beans.PropChangeSupport;
import jo.util.geom3d.Point3DLogic;
import jo.util.ui.utils.ClipboardLogic;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.DoubleUtils;
import jo.util.utils.obj.LongUtils;

public class ChViewThemeLogic
{
    private static List<ChViewThemeBean>   mThemes = null;
    private static boolean mInitialized = false;
    private static PropChangeSupport mPCS = new PropChangeSupport(StarLogic.class);
    
    public static List<ChViewThemeBean> getThemes()
    {
        if (mThemes == null)
        {
            if (!mInitialized)
            {
                RuntimeLogic.getInstance().addPropertyChangeListener("dataSource", new PropertyChangeListener() {                    
                    @Override
                    public void propertyChange(PropertyChangeEvent evt)
                    {
                        mThemes = null;
                        mPCS.fireMonotonicPropertyChange("themes", mThemes);
                    }
                });
            }
            mThemes = new ArrayList<ChViewThemeBean >();
            List<MetadataBean> mds = MetadataLogic.findByDomainIndex("themes", -1);
            for (MetadataBean md : mds)
            {
                ChViewThemeBean  theme = new ChViewThemeBean();
                theme.setOID(md.getOID());
                theme.setName(md.getKey());
                byte[] val = md.getValue();
                ByteArrayInputStream rdr = new ByteArrayInputStream(val);
                try
                {
                    theme.getProps().load(rdr);
                }
                catch (IOException e)
                {
                }
                mThemes.add(theme);
            }
            sortThemes();
        }
        return mThemes;
    }

    private static void sortThemes()
    {
        Collections.sort(mThemes, new Comparator<ChViewThemeBean >() {
            @Override
            public int compare(ChViewThemeBean  o1, ChViewThemeBean  o2)
            {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
    
    public static void makeTheme(String name)
    {
        ChViewThemeBean theme = getTheme(name);
        if (theme == null)
            theme = new ChViewThemeBean();
        theme.setName(name);
        toProps(theme.getProps(), ChViewVisualizationLogic.mPreferences);
        ByteArrayOutputStream wtr = new ByteArrayOutputStream();
        try
        {
            theme.getProps().store(wtr, name);
        }
        catch (IOException e)
        {
        }
        byte[] val = wtr.toByteArray();
        MetadataBean md = new MetadataBean();
        md.setOID(theme.getOID());
        md.setDomain("themes");
        md.setIndex(-1);
        md.setKey(theme.getName());
        md.setValue(val);
        if (theme.getOID() <= 0)
        {
            md = MetadataLogic.create(md);
            theme.setOID(md.getOID());
            mThemes.add(theme);
            sortThemes();
        }
        else
            MetadataLogic.update(md);
        mPCS.fireMonotonicPropertyChange("themes", mThemes);
    }
    
    public static ChViewThemeBean getTheme(String name)
    {
        for (ChViewThemeBean t : getThemes())
            if (t.getName().equalsIgnoreCase(name))
                return t;
        return null;
    }
    
    public static void deleteTheme(String name)
    {
        ChViewThemeBean theme = getTheme(name);
        if (theme == null)
            return;
        if (theme.getOID() > 0)
            MetadataLogic.deleteByOID(theme.getOID());
        mThemes.remove(theme);
        mPCS.fireMonotonicPropertyChange("themes", mThemes);
    }
    
    public static void applyTheme(ChViewThemeBean theme)
    {
        fromProps(ChViewVisualizationLogic.mPreferences, theme.getProps());
        ChViewVisualizationLogic.updateData(true);
    }
    
    private static void toProps(Properties props, ChViewPreferencesBean params)
    {
        props.clear();
        props.put("center",  params.getCenter().toString());
        props.put("rotation",  params.getRotation().toString());
        props.put("radius",  String.valueOf(params.getRadius()));
        props.put("scale",  String.valueOf(params.getScale()));
        props.put("showNames",  String.valueOf(params.isShowNames()));
        props.put("showLinks",  String.valueOf(params.isShowLinks()));
        props.put("showLinkNumbers",  String.valueOf(params.isShowLinkNumbers()));
        props.put("showRoutes",  String.valueOf(params.isShowRoutes()));
        props.put("showScope",  String.valueOf(params.isShowScope()));
        props.put("showGrid",  String.valueOf(params.isShowGrid()));
        // TODO: persist filter
        if (params.getFocus() != null)
            props.put("focus",  params.getFocus().getQuadrant()+":"+params.getFocus().getOID());
        toProps(props, "selected.", params.getSelected());
        toProps(props, "hidden.", params.getHidden());
    }
    
    private static void toProps(Properties props, String prefix, Set<StarBean> stars)
    {
        int idx = 0;
        for (StarBean sel : stars)
            props.put(prefix+(idx++),  sel.getQuadrant()+":"+sel.getOID());        
    }
    
    private static void fromProps(ChViewPreferencesBean params, Properties props)
    {
        params.setCenter(Point3DLogic.fromString(props.getProperty("center")));
        params.setRotation(Point3DLogic.fromString(props.getProperty("rotation")));
        params.setRadius(DoubleUtils.parseDouble(props.getProperty("radius")));
        params.setScale(DoubleUtils.parseDouble(props.getProperty("scale")));
        params.setShowNames(BooleanUtils.parseBoolean(props.getProperty("showNames")));
        params.setShowLinks(BooleanUtils.parseBoolean(props.getProperty("showLinks")));
        params.setShowLinkNumbers(BooleanUtils.parseBoolean(props.getProperty("showLinkNumbers")));
        params.setShowRoutes(BooleanUtils.parseBoolean(props.getProperty("showRoutes")));
        params.setShowScope(BooleanUtils.parseBoolean(props.getProperty("showScope")));
        params.setShowGrid(BooleanUtils.parseBoolean(props.getProperty("showGrid")));
        // TODO: restore filter
        params.getFilter().getConditions().clear();
        fromProps("selected.", params.getSelected(), props);
        fromProps("hidden.", params.getHidden(), props);
    }
    
    private static void fromProps(String prefix, Set<StarBean> stars, Properties props)
    {
        stars.clear();
        for (int idx = 0; idx < 999; idx++)
        {
            if (!props.containsKey(prefix+idx))
                break;
            String oidQuad = props.getProperty(prefix+idx);
            int o = oidQuad.indexOf(':');
            long oid = LongUtils.parseLong(oidQuad.substring(0, o));
            String quad = oidQuad.substring(o + 1);
            StarBean star = StarLogic.getByQuadrantID(quad, oid);
            if (star != null)
                stars.add(star);
        }
        
    }
    
    public static void copyTheme()
    {
        Properties props = new Properties();
        toProps(props, ChViewVisualizationLogic.mPreferences);
        StringWriter wtr = new StringWriter();
        try
        {
            props.store(wtr, "ChView Theme");
        }
        catch (IOException e)
        {
            return;
        }
        String txt = wtr.toString();
        ClipboardLogic.setAsText(txt);
    }
    
    public static void pasteTheme()
    {
        String txt = ClipboardLogic.getAsText();
        Properties props = new Properties();
        StringReader rdr = new StringReader(txt);
        try
        {
            props.load(rdr);
        }
        catch (IOException e)
        {
            return;
        }
        fromProps(ChViewVisualizationLogic.mPreferences, props);
        ChViewVisualizationLogic.updateData(true);
    }
    
    // listeners
    public static void addPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
        mPCS.addPropertyChangeListener(prop, pcl);
    }
    public static void addPropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.addPropertyChangeListener(pcl);
    }
    public static void addUIPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
        mPCS.addUIPropertyChangeListener(prop, pcl);
    }
    public static void addUIPropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.addUIPropertyChangeListener(pcl);
    }
    public static void removePropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.removePropertyChangeListener(pcl);
    }
}
