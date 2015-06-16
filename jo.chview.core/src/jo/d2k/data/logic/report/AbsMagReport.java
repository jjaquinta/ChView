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
import java.util.Collection;
import java.util.Iterator;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.StarBean;


/**
 * @author jgrant
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class AbsMagReport
{
    public static String generateReport(ChViewContextBean params)
    {
        StringBuffer text = new StringBuffer();

        text.append("<html>");
        text.append("<head>");
        text.append("<title>ABSOLUTE MAGNITUDE REPORT</title>");
        text.append("</head>");
        text.append("<body>");
        text.append("<h1>ABSOLUTE MAGNITUDE REPORT</h1>");
        UtilLogic.reportFilter(text, params);
        Collection<StarBean> starList = UtilLogic.getReportableStars(params);
        double[] absMag = new double[starList.size()];
        Iterator<StarBean> i = starList.iterator();
        absMag[0] = i.next().getAbsMag();
        double min = absMag[0];
        double max = absMag[0];
        for (int idx = 1; i.hasNext(); idx++)
        {
            absMag[idx] = i.next().getAbsMag();
            min = Math.min(min, absMag[idx]);
            max = Math.max(max, absMag[idx]);
        }
        Arrays.sort(absMag);
        
        text.append(LinksReport.drawQuartiles("Absolute Magnitude", "Magnitude", "Stars", "", 
                min, max, absMag, Math.floor(min), Math.ceil(max), 800, null));
        
        text.append("</body>");
        text.append("</html>");
        return text.toString();
    }
}
