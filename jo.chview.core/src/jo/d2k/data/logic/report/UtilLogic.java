package jo.d2k.data.logic.report;
/**
 * Created on Aug 28, 2002
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarFilter;
import jo.d2k.data.logic.FilterLogic;
import jo.d2k.data.logic.StarLogic;
import jo.util.geom3d.Point3D;

/**
 * @author jgrant
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class UtilLogic
{
    public static final int toleranceLimit = 3;

    public static boolean withinTolerance(java.awt.Dimension a, java.awt.Dimension b) { return withinTolerance(a.width, a.height, b.width, b.height); }
    public static boolean withinTolerance(int x1, int y1, int x2, int y2)
    {
        if (!withinTolerance(x1 - x2))
            return false;
        if (!withinTolerance(y1 - y2))
            return false;
        return true;
    }
    public static boolean withinTolerance(int delta)
    {
        if ((delta > toleranceLimit) || (delta < -toleranceLimit))
            return false;
        return true;
    }
    public static boolean withinTolerance(Point3D a, Point3D b)
    {
        return withinTolerance((int)a.getX(), (int)a.getY(), (int)b.getX(), (int)b.getY());
    }

    public static String format(double d, int leftPlaces, int rightPlaces)
    {
        StringBuffer ret = new StringBuffer();
        String num = String.valueOf(d);
        int off = num.indexOf(".");
        if (off < 0)
            off = num.length();
        if (off < leftPlaces)
            ret.append("            ".substring(0, leftPlaces - off));
        if (num.length() - off - 1 > rightPlaces )
            ret.append(num.substring(0, off + rightPlaces));
        else
        {
            ret.append(num);
            ret.append("000000000".substring(0, rightPlaces - ((num.length() - off - 1))));
        }
        return ret.toString();
    }
    public static String format(double d)
    {
        if (Double.isNaN(d))
            return "---";
        if (Double.isInfinite(d))
            return "---";
        return format(d, -1, 2);
    }
    public static String format(Point3D p)
    {
        return format(p.x)+","+format(p.y)+","+format(p.z);
    }
    public static double toDegrees(double radians)
    {
        return radians/Math.PI*180;
    }
    public static double toRadians(double degrees)
    {
        return degrees/180*Math.PI;
    }
    public static String formatDegrees(double radians)
    {
       double d = toDegrees(radians);
       StringBuffer ret = new StringBuffer();
       ret.append(Math.floor(d));
       ret.append("\u00b0 ");
       d -= Math.floor(d);
       d *= 60;
       if (Math.abs(d) > 0.01)
       {
           ret.append(Math.floor(d));
           ret.append("' ");
           d -= Math.floor(d);
           d *= 60;
           if (Math.abs(d) >= 1)
           {
               ret.append(Math.floor(d));
               ret.append("''");
           }
       }
       //DebugUtils.trace(radians+"->"+ret);
       return ret.toString();
    }
    
    public static List<StarBean> getReportableStars(ChViewContextBean params)
    {
        Point3D center = params.getCenter();
        double radius = params.getRadius()*3;
        List<StarBean> stars = StarLogic.getAllWithin(
                center.x, center.y, center.z, 
                radius);
        winnowStars(stars, params);        
        return stars;
    }
    public static void winnowStars(Collection<StarBean> stars, ChViewContextBean params)
    {
        StarFilter filter = params.getFilter();
        for (Iterator<StarBean> i = stars.iterator(); i.hasNext(); )
        {
            StarBean star = i.next();
            if (star.getParent() != 0)
                i.remove();
            else if (FilterLogic.isFiltered(star, filter))
                i.remove();
        }
    }
    public static void reportFilter(StringBuffer sb, ChViewContextBean params)
    {
        StarFilter filter = params.getFilter();
        if (!FilterLogic.isAnyFilter(filter))
            return;
        sb.append("<h2>Filtered Out:</h2>");
        sb.append("<ul>");
        if (filter.isSpectraO())
            sb.append("<li>O Class Stars</li>");
        if (filter.isSpectraB())
            sb.append("<li>B Class Stars</li>");
        if (filter.isSpectraA())
            sb.append("<li>A Class Stars</li>");
        if (filter.isSpectraF())
            sb.append("<li>F Class Stars</li>");
        if (filter.isSpectraG())
            sb.append("<li>G Class Stars</li>");
        if (filter.isSpectraK())
            sb.append("<li>K Class Stars</li>");
        if (filter.isSpectraM())
            sb.append("<li>M Class Stars</li>");
        if (filter.isSpectraL())
            sb.append("<li>L Class Stars</li>");
        if (filter.isSpectraT())
            sb.append("<li>T Class Stars</li>");
        if (filter.isSpectraY())
            sb.append("<li>Y Class Stars</li>");
        if (filter.getGenerated() != null)
            if (filter.getGenerated())
                sb.append("<li>Generated Stars</li>");
            else
                sb.append("<li>Non-generated Stars</li>");
        sb.append("</ul>");
    }
}
