package jo.d2k.data.logic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarColumn;
import jo.d2k.data.data.StarSchemaBean;
import jo.util.beans.PropChangeSupport;
import jo.util.utils.BeanUtils;
import jo.util.utils.FormatUtils;

public class StarColumnLogic
{
    private static List<StarColumn> mColumns = null;
    private static boolean mInit = false;
    private static PropChangeSupport mPCS = new PropChangeSupport(StarColumnLogic.class);

    public static List<StarColumn> getPotentialColumns()
    {
        if (mColumns != null)
            return mColumns;
        if (!mInit)
        {
            mInit = true;
            StarSchemaLogic.addPropertyChangeListener("schemas", new PropertyChangeListener() {                
                @Override
                public void propertyChange(PropertyChangeEvent evt)
                {
                    mColumns = null; // reset on data source change
                    mPCS.fireMonotonicPropertyChange("columns", mColumns);
                }
            });
        }
        mColumns = new ArrayList<StarColumn>();
        mColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, ChViewContextBean.NAME, "Name"));
        mColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, ChViewContextBean.COMMON_NAME, "Common Name"));
        mColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, ChViewContextBean.HIP_NAME, "HIP Name"));
        mColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, ChViewContextBean.GJ_NAME, "GJ Name"));
        mColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, ChViewContextBean.HD_NAME, "HD Name"));
        mColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, ChViewContextBean.HR_NAME, "HR Name"));
        mColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, ChViewContextBean.SAO_NAME, "SAO Name"));
        mColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, ChViewContextBean.TWOMASS_NAME, "2MASS Name"));
        mColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, "quadrant", "Quadrant"));
        mColumns.add(new StarColumn(StarColumn.TYPE_CALCULATED, "x,y,z", "Coords"));
        mColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, "x", "X", 50, StarSchemaBean.SORT_BY_NUMBER));
        mColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, "y", "Y", 50, StarSchemaBean.SORT_BY_NUMBER));
        mColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, "z", "Z", 50, StarSchemaBean.SORT_BY_NUMBER));
        mColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, "spectra", "Spectrum"));
        mColumns.add(new StarColumn(StarColumn.TYPE_CALCULATED, "apparentMagnitude", "App Mag", 50, StarSchemaBean.SORT_BY_NUMBER));
        mColumns.add(new StarColumn(StarColumn.TYPE_CALCULATED, "parent", "Parent"));
        mColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, "simbadURL", "Simbad"));
        mColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, "wikipediaURL", "Wikipedia"));
        mColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, "generated", "Generated"));
        mColumns.add(new StarColumn(StarColumn.TYPE_CALCULATED, "ra", "RA", 100, StarSchemaBean.SORT_BY_NUMBER));
        mColumns.add(new StarColumn(StarColumn.TYPE_CALCULATED, "dec", "Dec", 100, StarSchemaBean.SORT_BY_NUMBER));
        mColumns.add(new StarColumn(StarColumn.TYPE_CALCULATED, "plx", "Plx", 50, StarSchemaBean.SORT_BY_NUMBER));
        mColumns.add(new StarColumn(StarColumn.TYPE_CALCULATED, "dist", "Dist", 50, StarSchemaBean.SORT_BY_NUMBER));
        mColumns.add(new StarColumn(StarColumn.TYPE_CALCULATED, "absoluteMagnitude", "Abs Mag", 50, StarSchemaBean.SORT_BY_NUMBER));
        for (StarSchemaBean schema : StarSchemaLogic.getSchemas())
            mColumns.add(new StarColumn(StarColumn.TYPE_EXTRA, schema.getMetadataID(), schema.getTitle(),
                    schema.getWidth(), schema.getSortBy()));
        return mColumns;
    }
    
    public static StarColumn getColumn(String id)
    {
        for (StarColumn column : getPotentialColumns())
            if (column.getID().equals(id))
                return column;
        return null;
    }
    
    public static List<StarColumn> getDefaultColumns()
    {
        List<StarColumn> columns = new ArrayList<StarColumn>();
        columns.add(getColumn(ChViewContextBean.NAME));
        columns.add(getColumn("x,y,z"));
        columns.add(getColumn("spectra"));
        return columns;
    }
    
    public static List<StarColumn> getUnusedColumns(List<StarColumn> usedColumns)
    {
        Set<String> usedIDs = new HashSet<String>();
        for (StarColumn used : usedColumns)
            usedIDs.add(used.getID());
        List<StarColumn> columns = new ArrayList<StarColumn>();
        for (StarColumn column : getPotentialColumns())
            if (!usedIDs.contains(column.getID()))
                columns.add(column);
        return columns;
    }

    public static String getText(ChViewContextBean context, StarBean star, StarColumn col)
    {
        if (col.getType() == StarColumn.TYPE_INTRINSIC)
        {
            Object val = BeanUtils.get(star, col.getID());
            if (val == null)
                return "";
            if (val instanceof Double)
                return FormatUtils.formatDouble((Double)val, 2);
            return val.toString();
        }
        else if (col.getType() == StarColumn.TYPE_CALCULATED)
        {
            String id = col.getID();
            if (id.equals("x,y,z"))
                return FormatUtils.formatDouble(star.getX(), 1)+","+
                    FormatUtils.formatDouble(star.getY(), 1)+","+
                    FormatUtils.formatDouble(star.getZ(), 1);
            if (id.equals("parent"))
            {
                StarBean parent = StarLogic.getByQuadrantID(star.getQuadrant(), star.getParent());
                if (parent != null)
                    return ChViewFormatLogic.getStarName(context, parent);
                else
                    return "";
            }
            if (id.equals("dist"))
            {
                double d = StarExtraLogic.distance(star, 0, 0, 0);
                return FormatUtils.formatDouble(d, 1);
            }
            if (id.equals("ra"))
            {
                //double d = StarExtraLogic.distance(star, 0, 0, 0);
                double q = Math.atan2(star.getY(), star.getX());
                //double f = Math.acos(star.getZ()/d);
                //double q = Math.asin(star.getY()/(d*Math.sin(f)));
                //double q = Math.acos(star.getX()/(d*Math.sin(f)));
                double ra = 12*q/Math.PI;
                if (ra < 0)
                    ra += 24;
                return FormatUtils.formatHMS(ra);
            }
            if (id.equals("dec"))
            {
                double d = StarExtraLogic.distance(star, 0, 0, 0);
                double f = Math.acos(star.getZ()/d);
                double dec = 90 - (180*f/Math.PI);
                return FormatUtils.formatDMS(dec);
            }
            if (id.equals("plx"))
            {
                double d = StarExtraLogic.distance(star, 0, 0, 0);
                double plx;
                if (d > 0)
                    plx = 1/(d/3.26);
                else
                    return "n/a";
                return FormatUtils.formatDouble(plx*1000, 1);
            }
            if (id.equals("apparentMagnitude"))
            {
                double d = StarExtraLogic.distance(star, 0, 0, 0);
                double mag = star.getAbsMag() - 5 + 5*Math.log(d);
                return FormatUtils.formatDouble(mag, 2);
            }
            if (id.equals("absoluteMagnitude"))
            {
                return FormatUtils.formatDouble(star.getAbsMag(), 2);
            }
            throw new IllegalArgumentException("Not implemented, calculated column '"+col.getID()+"'");
        }
        else if (col.getType() == StarColumn.TYPE_EXTRA)
        {
            return StarLogic.getMetadata(star).get(col.getID());
        }
        else
            throw new IllegalArgumentException("Not implemented, calculated column type '"+col.getType()+"'");
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
