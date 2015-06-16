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
import jo.d2k.data.logic.StarExtraLogic;
import jo.util.geom3d.Point3D;

/**
 * @author jgrant
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class BinaryReport
{
    public static String generateReport(ChViewContextBean params)
    {
        StringBuffer text = new StringBuffer();

        text.append("<html>");
        text.append("<head>");
        text.append("<title>BINARY SYSTEM REPORT</title>");
        text.append("</head>");
        text.append("<body>");
        text.append("<h1>BINARY SYSTEM REPORT</h1>");
        UtilLogic.reportFilter(text, params);

        List<List<StarBean>> clusters = findClusters(params);

        for (List<StarBean> cluster : clusters)
            reportOnCluster(params, cluster, text);
        text.append("</body>");
        text.append("</html>");
        return text.toString();
    }

    private static void reportOnCluster(ChViewContextBean params, List<StarBean> cluster, StringBuffer text)
    {
    	if (cluster.size() < 3)
    		return;
        Collections.sort(cluster, new BinaryComp());
        double totalMass = 0;
        Point3D center = new Point3D();
        for (StarBean star : cluster)
        {
            totalMass += StarExtraLogic.calcMassFromTypeAndClass(star.getSpectra());
            center.x += star.getX();
            center.y += star.getY();
            center.z += star.getZ();
        }
        center.setX(center.getX()/cluster.size());
        center.setY(center.getY()/cluster.size());
        center.setZ(center.getZ()/cluster.size());

        text.append("<h2>");
        text.append(ChViewFormatLogic.getStarName(params, cluster.get(0)));
        text.append(" System</h2>");
        text.append("Size: ");
        text.append(cluster.size());
        text.append("<br>");
        text.append("Center: ");
        text.append(UtilLogic.format(center.getX()));
        text.append(",");
        text.append(UtilLogic.format(center.getY()));
        text.append(",");
        text.append(UtilLogic.format(center.getZ()));
        text.append("<br>");
        text.append("Mass: ");
        text.append(UtilLogic.format(totalMass));
        text.append("<br>");
        text.append("<table>");
        for (int i = 0; i < cluster.size(); i++)
        {
            if (i%20 == 0)
                addHeader(text);
            StarBean star = cluster.get(i);
            Point3D loc = new Point3D(star.getX(), star.getY(), star.getZ());
            text.append("<tr><td>");
            text.append(ChViewFormatLogic.getStarName(params, star));
            text.append("</td><td align=\"right\"><pre>");
            text.append(UtilLogic.format(center.dist(loc)));
            text.append("</pre></td><td align=\"right\"><pre>");
            text.append(UtilLogic.format(StarExtraLogic.calcMassFromTypeAndClass(star.getSpectra())));
            text.append("</pre></td><td>");
            text.append(star.getSpectra());
            text.append("</pre></td><td>");
            if (star.getParent() == 0)
                text.append("primary");
            text.append("</td></tr>\n");
        }
        text.append("</table>");
    }

    private static void addHeader(StringBuffer text)
    {
        text.append("<tr><th>Star</th><th>Distance</th><th>Mass</th><th>Spectrum</th><th></th></tr>\n");
    }

    private static List<List<StarBean>> findClusters(ChViewContextBean params)
    {
        List<StarBean> stars = UtilLogic.getReportableStars(params);
        Map<Long,List<StarBean>> index = new HashMap<Long,List<StarBean>>();
        List<List<StarBean>> clusters = new ArrayList<List<StarBean>>();
        for (StarBean star : stars)
        {
            List<StarBean> system = star.getAllChildren();
            index.put(star.getOID(), system);
        }
        clusters.addAll(index.values());
        return clusters;
    }
}

class BinaryComp implements Comparator<StarBean>
{
	public BinaryComp()
	{
	}
	
	public int compare(StarBean oo1, StarBean oo2)
	{
		double m1 = StarExtraLogic.calcMassFromTypeAndClass(oo1.getSpectra());
		double m2 = StarExtraLogic.calcMassFromTypeAndClass(oo2.getSpectra());
		double delta = m2 - m1;
		if (delta < 0)
			return -1;
		if (delta > 0)
			return 1;
		return 0;
	}
}
