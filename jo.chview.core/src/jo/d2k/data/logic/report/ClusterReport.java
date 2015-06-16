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
public class ClusterReport
{
    public static String generateReport(ChViewContextBean params)
    {
        StringBuffer text = new StringBuffer();

        text.append("<html>");
        text.append("<head>");
        text.append("<title>CLUSTER REPORT</title>");
        text.append("</head>");
        text.append("<body>");
        text.append("<h1>CLUSTER REPORT</h1>");
        text.append("Clusters size: "+params.getLinkDist3()+"LY");
        UtilLogic.reportFilter(text, params);

        List<List<StarBean>> clusters = findClusters(params);
        Collections.sort(clusters, new ClustersComp());

        for (List<StarBean> cluster : clusters)
            reportOnCluster(cluster, text, params);
        text.append("</body>");
        text.append("</html>");
        return text.toString();
    }

    private static void reportOnCluster(List<StarBean> cluster, StringBuffer text, ChViewContextBean params)
    {
    	if (cluster.size() < 3)
    		return;
        Collections.sort(cluster, new ClusterComp());
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
        text.append(" Cluster</h2>");
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
            text.append("</pre></td></tr>\n");
        }
        text.append("</table>");
    }

    private static void addHeader(StringBuffer text)
    {
        text.append("<tr><th>Star</th><th>Distance</th><th>Mass</th></tr>\n");
    }

    private static List<List<StarBean>> findClusters(ChViewContextBean params)
    {
        List<StarBean> stars = UtilLogic.getReportableStars(params);
		Map<StarBean,List<StarBean>> clusterTable = new HashMap<StarBean, List<StarBean>>();
        List<List<StarBean>> clusters = new ArrayList<List<StarBean>>();
        double max = params.getLinkDist3();
        for (int i = 0; i < stars.size() - 1; i++)
        {
            List<StarBean> iCluster = clusterTable.get(stars.get(i));
			Point3D loci = StarExtraLogic.getLocation(stars.get(i));
            for (int j = i + 1; j < stars.size(); j++)
            {
                List<StarBean> jCluster = clusterTable.get(stars.get(j));
                if ((iCluster != null) && (iCluster == jCluster))
                    continue;
				Point3D locj = StarExtraLogic.getLocation(stars.get(j));
                double dist = loci.dist(locj);
                if (dist < max)
                {
                    if ((iCluster == null) && (jCluster == null))
                    {
                        iCluster = new ArrayList<StarBean>();
                        iCluster.add(stars.get(i));
                        iCluster.add(stars.get(j));
                        clusterTable.put(stars.get(i), iCluster);
                        clusterTable.put(stars.get(j), iCluster);
                        clusters.add(iCluster);
                    }
                    else if ((iCluster != null) && (jCluster == null))
                    {
                        iCluster.add(stars.get(j));
                        clusterTable.put(stars.get(j), iCluster);
                    }
                    else if ((iCluster == null) && (jCluster != null))
                    {
                        jCluster.add(stars.get(i));
                        iCluster = jCluster;
                        clusterTable.put(stars.get(i), iCluster);
                    }
                    else //if ((iCluster != null) && (jCluster != null))
                    {
                        for (StarBean star : jCluster)
                        {
                            iCluster.add(star);
                            clusterTable.put(star, iCluster);
                        }
                        clusters.remove(jCluster);
                    }

                }
            }
        }
        return clusters;
    }
}

class ClustersComp implements Comparator<List<StarBean>>
{
	public int compare(List<StarBean> oo1, List<StarBean> oo2)
	{
		return (int)(oo2.size() - oo1.size());
	}
}

class ClusterComp implements Comparator<StarBean>
{
	public ClusterComp()
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
