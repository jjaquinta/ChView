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
import java.util.Comparator;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.ChViewFormatLogic;
import jo.util.geom3d.Point3D;
import jo.util.utils.FormatUtils;


/**
 * @author jgrant
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DistanceMatrixReport
{
    public static String generateReport(final ChViewContextBean params)
    {
        StringBuffer text = new StringBuffer();

        text.append("<html>");
        text.append("<head>");
        text.append("<title>DISTANCE MATRIX</title>");
        text.append("</head>");
        text.append("<body>");
        text.append("<h1>DISTANCE MATRIX</h1>");
        UtilLogic.reportFilter(text, params);
        Collection<StarBean> starList = params.getSelected();
        if (starList.size() < 2)
        {
            starList = params.getStars();
            UtilLogic.winnowStars(starList, params);
        }
		int size = starList.size();
		if (size < 2)
			text.append("No objects selected to report on!");
		else
		{
			StarBean[] stars = starList.toArray(new StarBean[0]);
			Arrays.sort(stars, new Comparator<StarBean>() {
			    @Override
			    public int compare(StarBean o1, StarBean o2)
			    {
			        String n1 = ChViewFormatLogic.getStarName(params, o1).toLowerCase();
                    String n2 = ChViewFormatLogic.getStarName(params, o2).toLowerCase();
			        return n1.compareTo(n2);
			    }
            });
	        double[][] distances = new double[size-1][];
	        for (int i = 0; i < stars.length - 1; i++)
	        {
				Point3D loc1 = new Point3D(stars[i].getX(), stars[i].getY(), stars[i].getZ());
	        	distances[i] = new double[stars.length - i - 1];
	        	for (int j = 0; j < stars.length - 1 - i; j++)
	        	{
	        	    Point3D loc2 = new Point3D(stars[stars.length - 1 - j].getX(), stars[stars.length - 1 - j].getY(), stars[stars.length - 1 - j].getZ());
					distances[i][j] = loc1.dist(loc2);
	        	}
	        }
	        text.append("<table>");
	        // top row
			text.append("<tr>");
			text.append("<td></td>");
			for (int j = 0; j < stars.length - 1; j++)
				text.append("<th>"+ChViewFormatLogic.getStarName(params, stars[stars.length - 1 - j])+"</th>");
			text.append("</tr>\n");
			// values
			for (int i = 0; i < stars.length - 1; i++)
			{
				text.append("<tr>");
				text.append("<th>"+ChViewFormatLogic.getStarName(params, stars[i])+"</th>");
				for (int j = 0; j < stars.length - 1 - i; j++)
				{
				    if ((i%2) == 0)
				        if ((j%2) == 0)
				            text.append("<td style=\"background-color:#C0C0C0; text-align: right;\">");
				        else
                            text.append("<td style=\"background-color:#E0E0E0; text-align: right;\">");
				    else
                        if ((j%2) == 0)
                            text.append("<td style=\"background-color:#E0E0E0; text-align: right;\">");
                        else
                            text.append("<td style=\"background-color:#FFFFFF; text-align: right;\">");
                    text.append(FormatUtils.formatDouble(distances[i][j], 2));
                    text.append("</td>");
				}
				text.append("</tr>\n");
			}
			text.append("</table>");
		}
        text.append("</body>");
        text.append("</html>");
        return text.toString();
    }
}
