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
import java.util.List;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.StarRouteBean;
import jo.d2k.data.logic.ChViewFormatLogic;
import jo.d2k.data.logic.StarRouteLogic;


/**
 * @author jgrant
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class RoutesReport
{
    public static String generateReport(ChViewContextBean params)
    {
        StringBuffer text = new StringBuffer();

        text.append("<html>");
        text.append("<head>");
        text.append("<title>ROUTES REPORT</title>");
        text.append("</head>");
        text.append("<body>");
        text.append("<h1>ROUTES REPORT</h1>");
        List<List<Double>> allRoutes = new ArrayList<List<Double>>();
        double[] totalDist = new double[9];
        for (int i = 0; i < 8; i++)
            allRoutes.add(new ArrayList<Double>());
        allRoutes.add(new ArrayList<Double>());
        for (StarRouteBean route : StarRouteLogic.getAll())
        {
            StarRouteLogic.getReferences(route);
            List<Double> dists = allRoutes.get(route.getType());
            dists.add(route.getDistance());
            allRoutes.get(8).add(route.getDistance());
            totalDist[route.getType()] += route.getDistance();
            totalDist[8] += route.getDistance();
        }
        
        double max = 0;
        double[][] vals = new double[9][];
        for (int i = 0; i < 9; i++)
        {
            List<Double> dists = allRoutes.get(i);
            if (dists.size() == 0)
                continue;
            vals[i] = LinksReport.toArray(dists);
            max = Math.max(max, vals[i][vals[i].length - 1]);
        }
        for (int i = 0; i < 9; i++)
            if ((vals[i] != null) && (vals[i].length >= 3))
            {
                String title;
                title = (i < 8) ? ChViewFormatLogic.getRouteName(params, i) : "Overall";
                text.append(LinksReport.drawQuartiles(title, vals[i][0], vals[i][vals[i].length - 1], vals[i], 0, max, 800, totalDist[i]));
            }
        
        text.append("</body>");
        text.append("</html>");
        return text.toString();
    }
}
