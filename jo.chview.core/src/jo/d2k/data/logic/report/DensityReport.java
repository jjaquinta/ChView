/**
 * Created on Aug 28, 2002
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package jo.d2k.data.logic.report;

import java.util.Collection;

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
public class DensityReport
{
    public static String generateReport(ChViewContextBean params)
    {
        StringBuffer text = new StringBuffer();
        DensityNums nums = new DensityNums();
        Collection<StarBean> c = UtilLogic.getReportableStars(params);
        for (StarBean star : c)
            countStar(nums, star);
        nums.volume = 4.0/3.0*3.14159*Math.pow(params.getRadius()*3, 3);
        calcDensity(nums);

        String bigStr = String.valueOf(nums.biggest);
        int leftPlaces = bigStr.indexOf(".");
        int rightPlaces = 2;
        while ((nums.smallest < .1) && (rightPlaces < 4))
        {
            rightPlaces++;
            nums.smallest *= 10;
        }

        text.append("<html>");
        text.append("<head>");
        text.append("<title>SPHERICAL DENSITY REPORT</title>");
        text.append("</head>");
        text.append("<body>");
        text.append("<h1>SPHERICAL DENSITY REPORT</h1>");
        text.append("<h2>Sphere centered on "+UtilLogic.format(params.getCenter())+"</h2>");
        text.append("<h2>Radius of "+UtilLogic.format(params.getRadius()*3)+"</h2>");
        UtilLogic.reportFilter(text, params);
        text.append("<table>");
        text.append("<tr><th>Spectra</th><th>Number</th><th>Avg/1000ly^3</th><th>%</th></tr>\n");
        addLine(text, "O", nums.oCount, nums.oDensity, leftPlaces, rightPlaces, nums.totCount);
        addLine(text, "B", nums.bCount, nums.bDensity, leftPlaces, rightPlaces, nums.totCount);
        addLine(text, "A", nums.aCount, nums.aDensity, leftPlaces, rightPlaces, nums.totCount);
        addLine(text, "F", nums.fCount, nums.fDensity, leftPlaces, rightPlaces, nums.totCount);
        addLine(text, "G", nums.gCount, nums.gDensity, leftPlaces, rightPlaces, nums.totCount);
        addLine(text, "K", nums.kCount, nums.kDensity, leftPlaces, rightPlaces, nums.totCount);
        addLine(text, "M", nums.mCount, nums.mDensity, leftPlaces, rightPlaces, nums.totCount);
        addLine(text, "L", nums.tCount, nums.lDensity, leftPlaces, rightPlaces, nums.totCount);
        addLine(text, "T", nums.lCount, nums.tDensity, leftPlaces, rightPlaces, nums.totCount);
        addLine(text, "Y", nums.yCount, nums.yDensity, leftPlaces, rightPlaces, nums.totCount);
        addLine(text, "X", nums.xCount, nums.xDensity, leftPlaces, rightPlaces, nums.totCount);
        text.append("</table>");


        text.append("</body>");
        text.append("</html>");
        return text.toString();
    }

    private static void addLine(StringBuffer text, String title, int count, double density, int leftPlaces, int rightPlaces, int totCount)
    {
        text.append("<tr><td>"+title+"</td>"
                + "<td align=\"right\">"+count+"</td>"
                + "<td align=\"right\">"+UtilLogic.format(density, leftPlaces, rightPlaces)+"</td>"
                + "<td align=\"right\">"+UtilLogic.format(count*100.0/totCount)+"</td>"
                + "</tr>\n");
    }
    
    private static void countStar(DensityNums nums, StarBean star)
    {
        switch (StarExtraLogic.getSpectra(star))
        {
            case 0:
				nums.oCount++;
                break;
            case 1:
				nums.bCount++;
                break;
            case 2:
				nums.aCount++;
                break;
            case 3:
				nums.fCount++;
                break;
            case 4:
				nums.gCount++;
                break;
            case 5:
				nums.kCount++;
                break;
            case 6:
				nums.mCount++;
                break;
            case 7:
                nums.lCount++;
                break;
            case 8:
                nums.tCount++;
                break;
            case 9:
                nums.yCount++;
                break;
            default:
				nums.xCount++;
                break;
        }
		nums.totCount++;
    }

    private static void calcDensity(DensityNums nums)
    {
		nums.biggest = 0;
		nums.smallest = nums.totCount*1000.0;
		nums.oDensity = calcDensity(nums, nums.oCount);
		nums.bDensity = calcDensity(nums, nums.bCount);
		nums.aDensity = calcDensity(nums, nums.aCount);
		nums.fDensity = calcDensity(nums, nums.fCount);
		nums.gDensity = calcDensity(nums, nums.gCount);
		nums.kDensity = calcDensity(nums, nums.kCount);
		nums.mDensity = calcDensity(nums, nums.mCount);
        nums.lDensity = calcDensity(nums, nums.lCount);
        nums.tDensity = calcDensity(nums, nums.tCount);
        nums.yDensity = calcDensity(nums, nums.yCount);
		nums.xDensity = calcDensity(nums, nums.xCount);
		nums.totDensity = (double)nums.totCount/nums.volume*1000.0;
    }

    private static double calcDensity(DensityNums nums, int count)
    {
        double ret = (double)count/nums.volume*1000.0;
        if (ret > nums.biggest)
			nums.biggest = ret;
        if ((ret > 0) && (ret < nums.smallest))
			nums.smallest = ret;
        return ret;
    }
}
