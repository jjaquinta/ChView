/**
 * Created on Aug 28, 2002
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package jo.d2k.data.logic.report;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.SkyBean;
import jo.d2k.data.logic.SkyLogic;


/**
 * @author jgrant
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class AppMagReport
{
    public static String generateReport(ChViewContextBean params)
    {
        StringBuffer text = new StringBuffer();

        text.append("<html>");
        text.append("<head>");
        text.append("<title>APPARENT MAGNITUDE REPORT</title>");
        text.append("</head>");
        text.append("<body>");
        text.append("<h1>APPARENT MAGNITUDE REPORT</h1>");
        text.append("<h1>Night Sky From "+params.getCenter().toIntString()+"</h1>");
        List<SkyBean> sky = SkyLogic.getSky(params.getCenter());
        double[] absMag = new double[sky.size()];
        Iterator<SkyBean> i = sky.iterator();
        absMag[0] = i.next().getApparentMagnitude();
        double min = absMag[0];
        double max = absMag[0];
        for (int idx = 1; i.hasNext(); idx++)
        {
            SkyBean star = i.next();
            absMag[idx] = star.getApparentMagnitude();
            min = Math.min(min, absMag[idx]);
            max = Math.max(max, absMag[idx]);
        }
        Arrays.sort(absMag);

        text.append(LinksReport.drawQuartiles("Apparent Magnitude", "Magnitude", "Stars", "", 
                min, max, absMag, Math.floor(min), Math.ceil(max), 800, null));
        
        text.append("</body>");
        text.append("</html>");
        return text.toString();
    }
}
