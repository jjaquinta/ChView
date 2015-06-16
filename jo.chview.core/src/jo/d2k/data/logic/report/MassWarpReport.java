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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.StarExtraLogic;


/**
 * @author jgrant
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class MassWarpReport
{
    private static Map<String, Double> mCache = new HashMap<String, Double>();
    
    public static String generateReport(ChViewContextBean params)
    {
        mCache.clear();
        StringBuffer text = new StringBuffer();

        text.append("<html>");
        text.append("<head>");
        text.append("<title>Mass Clustering</title>");
        text.append("</head>");
        text.append("<body>");
        text.append("<h1>MASS CLUSTERING REPORT</h1>");
        UtilLogic.reportFilter(text, params);
        List<StarBean> starList = UtilLogic.getReportableStars(params);
        double min = -1;
        double max = -1;
        for (int i = 0; i < starList.size() - 1; i++)
        {
            StarBean star1 = starList.get(i);
            for (int j = i + 1; j < starList.size(); j++)
            {
                StarBean star2 = starList.get(j);
                double g = getMassGradient(star1, star2);
                if (min < 0)
                    min = g;
                else
                    min = Math.min(min, g);
                if (max < 0)
                    max = g;
                else
                    max = Math.max(max, g);
            }
        }
        Map<Double,List<List<StarBean>>> threshold = findCriticalThreshold(starList, min, max);
        text.append("<table>");
        text.append("<tr><th>Minimum Gradient</th><th>Maximum Gradient</th></tr>");
        text.append("<tr><td align=\"right\">"+String.format("%f", min)+"</td><td align=\"right\">"+String.format("%f", max)+"</td></tr>");
        text.append("</body>");
        text.append("</table>");
        text.append("<table>");
        text.append("<tr><th>Gradient</th><th>Number of Clusters</th><th>Average Cluster Size</th></tr>");
        Double[] vals = threshold.keySet().toArray(new Double[0]);
        Arrays.sort(vals);
        for (double g : vals)
        {
            List<List<StarBean>> clusters = threshold.get(g);
            double avg = findAverageClusterSize(clusters);
            text.append("<tr>");
            text.append("<td align=\"right\">"+String.format("%f", g)+"</td>");
            text.append("<td align=\"right\">"+clusters.size()+"</td>");
            text.append("<td align=\"right\">"+UtilLogic.format(avg)+"</td>");
            text.append("</tr>");
        }
        text.append("</table>");
        text.append("</html>");
        return text.toString();
    }
    
    private static Map<Double, List<List<StarBean>>> findCriticalThreshold(
            List<StarBean> stars, double min, double max)
    {
        double threshold = (max - min)/1000;
        Map<Double, List<List<StarBean>>> points = new HashMap<Double, List<List<StarBean>>>();
        while (max - min > threshold)
        {
            double trial = (min + max)/2;
            List<List<StarBean>> clusters = findClusters(stars, trial);
            points.put(trial, clusters);
            int trialVal = clusters.size();
            if (trialVal > 2)
                min = trial;
            else
                max = trial;
        }
        return points;
    }

    private static double getMassGradient(StarBean star1, StarBean star2)
    {
        String key = getKey(star1, star2)+".gradient";
        if (mCache.containsKey(key))
            return mCache.get(key);
        double m1 = getMass(star1);
        double m2 = getMass(star2);
        double d = StarExtraLogic.distance(star1, star2);
        double g = (m1 + m2)/(d*d);
        mCache.put(key, g);
        return g;
    }
    
    private static String getKey(StarBean star1, StarBean star2)
    {
        if (star1.hashCode() < star2.hashCode())
            return star1.getName()+"$"+star2.getName();
        else
            return star2.getName()+"$"+star1.getName();
    }
    
    private static double getMass(StarBean star)
    {
        double m;
        if (mCache.containsKey(star.getSpectra()))
            m = mCache.get(star.getSpectra());
        else
        {    
            m = StarExtraLogic.calcMassFromTypeAndClass(star.getSpectra());
            mCache.put(star.getSpectra(), m);
        }
        return m;        
    }
    
    private static double findAverageClusterSize(List<List<StarBean>> clusters)
    {
        int tot = 0;
        for (List<StarBean> cluster : clusters)
            tot += cluster.size();
        double avg = (double)tot/(double)clusters.size();
        return avg;
    }
    
    private static List<List<StarBean>> findClusters(List<StarBean> stars, double max)
    {
        Map<StarBean,List<StarBean>> clusterTable = new HashMap<StarBean, List<StarBean>>();
        List<List<StarBean>> clusters = new ArrayList<List<StarBean>>();
        for (int i = 0; i < stars.size() - 1; i++)
        {
            StarBean star1 = stars.get(i);
            List<StarBean> iCluster = clusterTable.get(star1);
            for (int j = i + 1; j < stars.size(); j++)
            {
                StarBean star2 = stars.get(j);
                List<StarBean> jCluster = clusterTable.get(star2);
                if ((iCluster != null) && (iCluster == jCluster))
                    continue;
                double dist = getMassGradient(star1, star2);
                if (dist > max)
                {
                    if ((iCluster == null) && (jCluster == null))
                    {
                        iCluster = new ArrayList<StarBean>();
                        iCluster.add(star1);
                        iCluster.add(star2);
                        clusterTable.put(star1, iCluster);
                        clusterTable.put(star2, iCluster);
                        clusters.add(iCluster);
                    }
                    else if ((iCluster != null) && (jCluster == null))
                    {
                        iCluster.add(star2);
                        clusterTable.put(star2, iCluster);
                    }
                    else if ((iCluster == null) && (jCluster != null))
                    {
                        jCluster.add(star1);
                        iCluster = jCluster;
                        clusterTable.put(star1, iCluster);
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
