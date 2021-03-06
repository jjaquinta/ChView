package jo.d2k.data.logic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarColumn;
import jo.d2k.data.data.StarSchemaBean;
import jo.d2k.data.logic.report.UtilLogic;
import jo.d2k.data.logic.schema.StarSchemaComparatorLogic;
import jo.util.beans.PropChangeSupport;
import jo.util.utils.BeanUtils;
import jo.util.utils.FormatUtils;

public class StarColumnLogic
{
    private static List<StarColumn> mAllColumns = null;
    private static List<StarColumn> mPotentialColumns = null;
    private static Map<String,StarColumn> mColumnIndex = null;
    private static boolean mInit = false;
    private static PropChangeSupport mPCS = new PropChangeSupport(StarColumnLogic.class);

    public static List<StarColumn> getAllColumns()
    {
        if (mAllColumns != null)
            return mAllColumns;
        if (!mInit)
        {
            mInit = true;
            StarSchemaLogic.addPropertyChangeListener("schemas", new PropertyChangeListener() {                
                @Override
                public void propertyChange(PropertyChangeEvent evt)
                {
                    mAllColumns = null; // reset on data source change
                    mPotentialColumns = null;
                    mPCS.fireMonotonicPropertyChange("columns", mAllColumns);
                }
            });
        }
        mAllColumns = new ArrayList<StarColumn>();
        mAllColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, ChViewContextBean.NAME, "Name"));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, ChViewContextBean.COMMON_NAME, "Common Name"));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, ChViewContextBean.HIP_NAME, "HIP Name"));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, ChViewContextBean.GJ_NAME, "GJ Name"));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, ChViewContextBean.HD_NAME, "HD Name"));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, ChViewContextBean.HR_NAME, "HR Name"));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, ChViewContextBean.SAO_NAME, "SAO Name"));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, ChViewContextBean.TWOMASS_NAME, "2MASS Name"));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, "quadrant", "Quadrant"));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_CALCULATED, "x,y,z", "Coords"));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, "x", "X", 50, StarSchemaBean.SORT_BY_NUMBER,
                StarSchemaComparatorLogic.getComparator(StarSchemaBean.DOUBLE)));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, "y", "Y", 50, StarSchemaBean.SORT_BY_NUMBER,
                StarSchemaComparatorLogic.getComparator(StarSchemaBean.DOUBLE)));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, "z", "Z", 50, StarSchemaBean.SORT_BY_NUMBER,
                StarSchemaComparatorLogic.getComparator(StarSchemaBean.DOUBLE)));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, "spectra", "Spectrum"));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_CALCULATED, "apparentMagnitude", "App Mag", 50, StarSchemaBean.SORT_BY_NUMBER,
                StarSchemaComparatorLogic.getComparator(StarSchemaBean.DOUBLE)));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_CALCULATED, "parent", "Parent"));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, "simbadURL", "Simbad", 127, StarSchemaBean.SORT_BY_TEXT,
                StarSchemaComparatorLogic.getComparator(StarSchemaBean.LINK)));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, "wikipediaURL", "Wikipedia", 127, StarSchemaBean.SORT_BY_TEXT,
                StarSchemaComparatorLogic.getComparator(StarSchemaBean.LINK)));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_INTRINSIC, "generated", "Generated"));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_CALCULATED, "ra", "RA", 100, StarSchemaBean.SORT_BY_NUMBER,
                StarSchemaComparatorLogic.getComparator(StarSchemaBean.DOUBLE)));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_CALCULATED, "dec", "Dec", 100, StarSchemaBean.SORT_BY_NUMBER,
                StarSchemaComparatorLogic.getComparator(StarSchemaBean.DOUBLE)));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_CALCULATED, "plx", "Plx", 50, StarSchemaBean.SORT_BY_NUMBER,
                StarSchemaComparatorLogic.getComparator(StarSchemaBean.DOUBLE)));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_CALCULATED, "dist", "Dist", 50, StarSchemaBean.SORT_BY_NUMBER,
                StarSchemaComparatorLogic.getComparator(StarSchemaBean.DOUBLE)));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_CALCULATED, "absoluteMagnitude", "Abs Mag", 50, StarSchemaBean.SORT_BY_NUMBER,
                StarSchemaComparatorLogic.getComparator(StarSchemaBean.DOUBLE)));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_CALCULATED, "mass", "Mass", 50, StarSchemaBean.SORT_BY_NUMBER,
                StarSchemaComparatorLogic.getComparator(StarSchemaBean.DOUBLE)));
        for (StarSchemaBean schema : StarSchemaLogic.getSchemas())
            mAllColumns.add(new StarColumn(StarColumn.TYPE_EXTRA, schema.getMetadataID(), schema.getTitle(),
                    schema.getWidth(), schema.getSortBy(),
                    StarSchemaComparatorLogic.getComparator(schema.getType())));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_PSEUDO, "AND", "AND", 0, 0,
                StarSchemaComparatorLogic.getComparator(StarSchemaBean.AND)));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_PSEUDO, "OR", "OR", 0, 0,
                StarSchemaComparatorLogic.getComparator(StarSchemaBean.OR)));
        mAllColumns.add(new StarColumn(StarColumn.TYPE_PSEUDO, "NOT", "NOT", 0, 0,
                StarSchemaComparatorLogic.getComparator(StarSchemaBean.NOT)));
        mColumnIndex = new HashMap<String, StarColumn>();
        for (StarColumn col : mAllColumns)
            mColumnIndex.put(col.getID(), col);
        mPotentialColumns = null;
        return mAllColumns;
    }

    public static List<StarColumn> getPotentialColumns()
    {
        if (mPotentialColumns != null)
            return mPotentialColumns;
        mPotentialColumns = new ArrayList<StarColumn>();
        for (StarColumn col : getAllColumns())
            if (col.getType() != StarColumn.TYPE_PSEUDO)
                mPotentialColumns.add(col);
        return mPotentialColumns;            
    }
    
    public static StarColumn getColumn(String id)
    {
        getAllColumns();
        return mColumnIndex.get(id);
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
            if (!usedIDs.contains(column.getID()) && (column.getType() != StarColumn.TYPE_PSEUDO))
                columns.add(column);
        return columns;
    }

    public static String getText(ChViewContextBean context, StarBean star, StarColumn col)
    {
        Object val = getValue(context, star, col);
        if (val instanceof String)
            return (String)val;
        else if (val instanceof Double)
        {
            if (col.getType() == StarColumn.TYPE_INTRINSIC)
                return FormatUtils.formatDouble((Double)val, 2);
            else if (col.getType() == StarColumn.TYPE_CALCULATED)
            {
                String id = col.getID();
                if (id.equals("dist"))
                    return FormatUtils.formatDouble((Double)val, 1);
                if (id.equals("ra"))
                    return FormatUtils.formatHMS((Double)val);
                if (id.equals("dec"))
                    return FormatUtils.formatDMS((Double)val);
                if (id.equals("plx"))
                    return FormatUtils.formatDouble((Double)val, 1);
                if (id.equals("apparentMagnitude"))
                    return FormatUtils.formatDouble((Double)val, 2);
                if (id.equals("absoluteMagnitude"))
                    return FormatUtils.formatDouble((Double)val, 2);
                if (id.equals("mass"))
                    return UtilLogic.format((Double)val);
            }
            return FormatUtils.formatDouble((Double)val, 2);
        }
        else if (val instanceof StarBean)
            return ChViewFormatLogic.getStarName(context, (StarBean)val);
        else if (val == null)
            return "";
        else
            return val.toString();
    }

    public static Object getValue(ChViewContextBean context, StarBean star, StarColumn col)
    {
        if (col.getType() == StarColumn.TYPE_INTRINSIC)
            return BeanUtils.get(star, col.getID());
        else if (col.getType() == StarColumn.TYPE_CALCULATED)
        {
            String id = col.getID();
            if (id.equals("x,y,z"))
                return FormatUtils.formatDouble(star.getX(), 1)+","+
                    FormatUtils.formatDouble(star.getY(), 1)+","+
                    FormatUtils.formatDouble(star.getZ(), 1);
            if (id.equals("parent"))
                return star.getParentRef();
            if (id.equals("dist"))
                return StarExtraLogic.distance(star, 0, 0, 0);
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
                return ra;
            }
            if (id.equals("dec"))
            {
                double d = StarExtraLogic.distance(star, 0, 0, 0);
                double f = Math.acos(star.getZ()/d);
                double dec = 90 - (180*f/Math.PI);
                return dec;
            }
            if (id.equals("plx"))
            {
                double d = StarExtraLogic.distance(star, 0, 0, 0);
                double plx;
                if (d > 0)
                    plx = 1/(d/3.26);
                else
                    return "n/a";
                return plx*1000;
            }
            if (id.equals("apparentMagnitude"))
            {
                double d = StarExtraLogic.distance(star, 0, 0, 0);
                double mag = star.getAbsMag() - 5 + 5*Math.log(d);
                return mag;
            }
            if (id.equals("absoluteMagnitude"))
            {
                return star.getAbsMag();
            }
            if (id.equals("mass"))
            {
                return StarExtraLogic.calcMassFromTypeAndClass(star.getSpectra());
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
