/**
 * Created on Aug 28, 2002
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package jo.d2k.data.logic.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.ChViewFormatLogic;
import jo.util.utils.DebugUtils;

/**
 * @author jgrant
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CrossroadsReport
{

    public static String generateReport(ChViewContextBean params)
    {
        StringBuffer text = new StringBuffer();
        CrossroadsNums nums = new CrossroadsNums();

        text.append("<html>");
        text.append("<head>");
        text.append("<title>CROSSROADS REPORT</title>");
        text.append("</head>");
        text.append("<body>");
        text.append("<h1>CROSSROADS REPORT</h1>");
        // find selected
        nums.stars = new ArrayList<StarBean>();
        nums.stars.addAll(params.getFilteredStars());
        if (nums.stars.size() == 0)
        {
            text.append("No objects to report on!");
        }
        else
    	{
            UtilLogic.reportFilter(text, params);
	        computeCrossroads(nums, params);
	        Collections.sort(nums.stars, new CrossComp(nums));
	        reportCrossroads(nums, text, params);
	        reportRoutes(nums, text, params);
		}
        text.append("</body>");
        text.append("</html>");
        return text.toString();
    }

    private static void computeCrossroads(CrossroadsNums nums, ChViewContextBean params)
    {
        int totCalc = nums.stars.size()*nums.stars.size()/2;
        DebugUtils.trace("Total crosses: "+totCalc);
        nums.crossed = new HashMap<StarBean, Integer>();
		nums.totCrossed = 0;
		nums.paths = new HashMap<String, List<StarBean>>();
		nums.routes = new HashMap<String, Object[]>();
		nums.totRoutes = 0;
        for (int i = 0; i < nums.stars.size() - 1; i++)
        {
            StarBean s1 = (StarBean)nums.stars.get(i);
            for (int j = i + 1; j < nums.stars.size(); j++)
            {
                StarBean s2 = (StarBean)nums.stars.get(j);
                computeCrossroads(nums, s1, s2, params);
				totCalc--;
            }
            DebugUtils.trace(" "+totCalc);
        }
    }

    private static void computeCrossroads(CrossroadsNums nums, StarBean s1, StarBean s2, ChViewContextBean params)
    {
        String sig = mkSig(s1, s2);
        if (nums.paths.containsKey(sig))
            return;
        List<StarBean> path = RouteFinderLogic.computeRoute(s1, s2, -1, null, null, params);
        if (path == null)
        	return;
        // register sub-paths
        for (int i = 0; i < path.size() - 1; i++)
        {
			s1 = path.get(i);
            for (int j = i + 1; j < path.size(); j++)
            {
                s2 = path.get(j);
                sig = mkSig(s1, s2);
                if (nums.paths.containsKey(sig))
                    continue;
                List<StarBean> subPath = new ArrayList<StarBean>();
                for (int k = i; k <= j; k++)
                    subPath.add(path.get(k));
                countCrossroads(nums, subPath);
                nums.paths.put(sig, subPath);
            }
        }
    }

    private static void countCrossroads(CrossroadsNums nums, List<StarBean> path)
    {
        StarBean[] stars = new StarBean[path.size()];
        path.toArray(stars);
        for (int i = 0; i < stars.length; i++)
        {
            // count crossings
            if ((i > 0) && (i < stars.length - 1))
            {
                Integer iv = (Integer)nums.crossed.get(stars[i]);
                if (iv == null)
                    iv = new Integer(1);
                else
                    iv = new Integer(iv.intValue()+1);
                nums.crossed.put(stars[i], iv);
                nums.totCrossed++;
            }
            // count routes
            if (i > 0)
            {
                String sig = mkSig((StarBean)stars[i], (StarBean)stars[i-1]);
                Object[] oo = (Object[])nums.routes.get(sig);
                if (oo == null)
                {
                    oo = new Object[3];
                    oo[0] = new Integer(1);
                    oo[1] = stars[i];
                    oo[2] = stars[i-1];
					nums.routes.put(sig, oo);
                }
                else
                {
                    oo[0] = new Integer(((Integer)oo[0]).intValue() + 1);
                }
				nums.totRoutes++;
            }
        }
    }

    private static void reportCrossroads(CrossroadsNums nums, StringBuffer text, ChViewContextBean params)
    {
        text.append("<table cellspacing=1 cellpadding=1>");
        int idx = 0;
        for (StarBean s : params.getFilteredStars())
        {
            Integer iv = (Integer)nums.crossed.get(s);
            if (iv == null)
                continue;
            int i = iv.intValue();
            if (i < 5)
                continue;
            if (idx++%20 == 0)
                addHeader(text);
            double pc = (double)i*100.0/(double)nums.totCrossed;
            text.append("<tr><td>");
            text.append(ChViewFormatLogic.getStarName(params, s));
            text.append("</td><td align=\"right\">");
            text.append(i);
            text.append("</td><td align=\"right\">");
            text.append(UtilLogic.format(pc, 2, 2));
            text.append("</td></tr>\n");
        }
        text.append("</table>");
    }

    private static void addHeader(StringBuffer text)
    {
        text.append("<tr><th>Name</th><th>Crossings</th><th>%</th></tr>\n");
    }

    private static void reportRoutes(CrossroadsNums nums, StringBuffer text, ChViewContextBean params)
    {
        List<String> routeVec = new ArrayList<String>();
        routeVec.addAll(nums.routes.keySet());
        Collections.sort(routeVec, new RouteComp(nums));

        text.append("<table cellspacing=1 cellpadding=1>");
        int idx = 0;
        for (String key : routeVec)
        {
            if (idx++%10 == 20)
                text.append("<tr><th>Point 1</th><th>Point 2</th><th>Crossings</th><th>%</th></tr>");
            Object[] oo = nums.routes.get(key);
            Integer iv = (Integer)oo[0];
            if (iv == null)
                continue;
            int i = iv.intValue();
            if (i < 5)
                continue;
            double pc = (double)i*100.0/(double)nums.totRoutes;
            StarBean s1 = (StarBean)oo[1];
            StarBean s2 = (StarBean)oo[2];
            int sv1 = 0;
            try { sv1 = ((Integer)nums.crossed.get(s1)).intValue(); } catch (Exception e1) { }
            int sv2 = 0;
            try { sv2 = ((Integer)nums.crossed.get(s2)).intValue(); } catch (Exception e1) { }
            if (sv2 > sv1)
            {
                s1 = (StarBean)oo[2];
                s2 = (StarBean)oo[1];
            }
            text.append("<tr><td>");
            text.append(ChViewFormatLogic.getStarName(params, s1));
            text.append("</td><td>");
            text.append(s2.getName());
            text.append("</td><td align=\"right\">");
            text.append(i);
            text.append("</td><td align=\"right\">");
            text.append(UtilLogic.format(pc, 2, 2));
            text.append("</td></tr>\n");
        }
        text.append("</table>");
    }

    private static String mkSig(StarBean s1, StarBean s2)
    {
        String name1 = s1.getOID()+s1.getName();
        String name2 = s2.getOID()+s2.getName();
        if (name1.compareTo(name2) < 0)
            return name2+":"+name1;
        else
            return name1+":"+name2;
    }
}

class CrossroadsNums
{
	List<StarBean>    stars;
	Map<StarBean,Integer> crossed;
	int       totCrossed;
	Map<String,List<StarBean>> paths;
	Map<String,Object[]> routes;
	int       totRoutes;
}	

class CrossComp implements Comparator<StarBean>
{
	CrossroadsNums	mNums;
	
	public CrossComp(CrossroadsNums nums)
	{
		mNums = nums;
	}
	
	public int compare(StarBean s1, StarBean s2)
	{
		int v1;
		int v2;
		Integer iv1 = mNums.crossed.get(s1);
		if (iv1 != null)
			v1 = iv1.intValue();
		else
			v1 = 0;
		Integer iv2 = mNums.crossed.get(s2);
		if (iv2 != null)
			v2 = iv2.intValue();
		else
			v2 = 0;
		return (int)(v2 - v1);
	}
}

class RouteComp implements Comparator<String>
{
	CrossroadsNums	mNums;
	
	public RouteComp(CrossroadsNums nums)
	{
		mNums = nums;
	}
	public int compare(String key1, String key2)
	{
		Object[] oo1 = (Object[])mNums.routes.get(key1);
		Integer iv1 = (Integer)oo1[0];
		int i1 = iv1.intValue();
		Object[] oo2 = (Object[])mNums.routes.get(key2);
		Integer iv2 = (Integer)oo2[0];
		int i2 = iv2.intValue();
		return (int)(i2 - i1);
	}
}
