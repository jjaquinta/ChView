/**
 * Created on Aug 28, 2002
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package jo.d2k.data.logic.report;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.StarExtraLogic;
import jo.util.utils.FormatUtils;
import jo.util.utils.MathUtils;

import org.jfree.graphics2d.svg.SVGGraphics2D;


/**
 * @author jgrant
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LinksReport
{
    public static String generateReport(ChViewContextBean params)
    {
        StringBuffer text = new StringBuffer();

        text.append("<html>");
        text.append("<head>");
        text.append("<title>LINKS REPORT</title>");
        text.append("</head>");
        text.append("<body>");
        text.append("<h1>LINKS REPORT</h1>");
        UtilLogic.reportFilter(text, params);
        Collection<StarBean> starList = UtilLogic.getReportableStars(params);
        double linkDist1 = params.getLinkDist1();
        double linkDist2 = params.getLinkDist2();
        double linkDist3 = params.getLinkDist3();
        double linkDist4 = params.getLinkDist4();
        double[][] links = getLinks(starList.toArray(new StarBean[0]),
                    linkDist1, linkDist2, linkDist3, linkDist4);
        double low = 0;
        double high = linkDist4*1.1;
        text.append(drawQuartiles("Short Links", linkDist1, linkDist2, links[0], low, high, 800, null));
        text.append(drawQuartiles("Medium Links", linkDist2, linkDist3, links[1], low, high, 800, null));
        text.append(drawQuartiles("Long Links", linkDist3, linkDist4, links[2], low, high, 800, null));
        text.append(drawQuartiles("Overall", linkDist1, linkDist4, links[3], low, high, 800, null));
        
        text.append("</body>");
        text.append("</html>");
        return text.toString();
    }
    
    public static String drawQuartiles(String title, double min, double max, double[] vals, double left, double right, int pixels,
            Double total)
    {
        return drawQuartiles(title, "Distance", "Links", "ly", min, max, vals, left, right, pixels, total);
    }
    
    public static String drawQuartiles(String title, String quantity, String type, String units, 
            double min, double max, double[] vals, double left, double right, int pixels,
            Double total)
    {
        StringBuffer text = new StringBuffer();
        text.append("<h2>"+title+"</h2>\n");
        text.append("<table>\n");
        text.append("<tr>");
        text.append("<td align=\"right\">Minimum "+quantity+"</td><td align=\"left\">"+FormatUtils.formatDouble(min, 2)+" "+units+"</td>");
        if (vals.length > 3)
            text.append("<td align=\"right\">Lower Quartile</td><td align=\"left\">"+FormatUtils.formatDouble(vals[vals.length/4], 2)+" "+units+"</td>");
        text.append("</tr>");
        text.append("<tr>");
        text.append("<td align=\"right\">Maximum "+quantity+"</td><td align=\"left\">"+FormatUtils.formatDouble(max, 2)+" "+units+"</td>");
        if (vals.length > 3)
            text.append("<td align=\"right\">Median "+quantity+"</td><td align=\"left\">"+FormatUtils.formatDouble(vals[vals.length/2], 2)+" "+units+"</td>");
        text.append("</tr>");
        text.append("<tr>");
        text.append("<td align=\"right\">Number of "+type+"</td><td align=\"left\">"+vals.length+"</td>");
        if (vals.length > 3)
            text.append("<td align=\"right\">Upper Quartile</td><td align=\"left\">"+FormatUtils.formatDouble(vals[vals.length*3/4], 2)+" "+units+"</td>");
        text.append("</tr>");
        if (total != null)
        {
            text.append("<tr>");
            text.append("<td align=\"right\">Total "+quantity+"</td><td align=\"left\">"+FormatUtils.formatDouble(total, 2)+" "+units+"</td>");
            text.append("</tr>");
        }
        text.append("</table>\n");
        if (vals.length > 3)
        {
            //text.append(getHTMLTable(min, max, vals, left, right, pixels));
            text.append(getSVGTable(min, max, vals, left, right, pixels, units));
        }
        return text.toString();
    }

    public static String getSVGTable(double min, double max, double[] vals,
            double left, double right, int width, String units)
    {
        int height = width/4;
        int minpx = (int)MathUtils.interpolate(min, left, right, 0, width);
        int minqpx = (int)MathUtils.interpolate(vals[vals.length/4], left, right, 0, width);
        int medpx = (int)MathUtils.interpolate(vals[vals.length/2], left, right, 0, width);
        int maxqpx = (int)MathUtils.interpolate(vals[vals.length*3/4], left, right, 0, width);
        int maxpx = (int)MathUtils.interpolate(max, left, right, 0, width);
        
        SVGGraphics2D gc = new SVGGraphics2D(width, height);
        int ascent = gc.getFontMetrics().getAscent();
        //int descent = gc.getFontMetrics().getDescent();
        int lineHeight = gc.getFontMetrics().getHeight();

        // draw number line
        int numberLineHeight = height - lineHeight*3/2;
        // ticks
        for (double d = Math.floor(left); d <= Math.ceil(right); d += .25)
        {
            int x = (int)MathUtils.interpolate(d, left, right, 0, width);
            if (Math.abs(d - Math.floor(d)) < .1)
            {   // major tick
                gc.setColor(Color.black);
                gc.drawLine(x, numberLineHeight - lineHeight/2, x, numberLineHeight + lineHeight/2);
                gc.drawString(((int)d)+" "+units, x, numberLineHeight + lineHeight/2 + ascent);
            }
            else // minor tick
            {
                gc.setColor(Color.gray);
                gc.drawLine(x, numberLineHeight - lineHeight/4, x, numberLineHeight + lineHeight/4);
            }
        }
        gc.setColor(Color.black);
        gc.drawLine(0, numberLineHeight, width, numberLineHeight);
        
        // draw box and whiskers
        int quartileLow = numberLineHeight - lineHeight*2;
        int quartileHigh = quartileLow - lineHeight*2;
        int quartileMid = (quartileLow + quartileHigh)/2;
        // box
        gc.setColor(Color.lightGray);
        gc.fillRect(minqpx, quartileHigh, maxqpx - minqpx, quartileLow - quartileHigh);
        gc.setColor(Color.gray);
        gc.drawRect(minqpx, quartileHigh, maxqpx - minqpx, quartileLow - quartileHigh);
        gc.drawLine(medpx, quartileHigh, medpx, quartileLow);
        // whiskers
        gc.setColor(Color.black);
        gc.drawLine(minpx, quartileMid, maxpx, quartileMid);
        gc.drawLine(minpx, quartileMid - lineHeight/2, minpx, quartileMid + lineHeight/2);
        gc.drawLine(maxpx, quartileMid - lineHeight/2, maxpx, quartileMid + lineHeight/2);
        
        // draw histogram
        List<Integer> buckets = new ArrayList<Integer>();
        double step = .25;
        if (right - left > 8)
            if (right - left > 16)
                step = 1;
            else
                step = .5;        
        int vmax = divideValues(vals, buckets, left, right, step);
        int graphLow = quartileHigh - lineHeight*2;
        int graphHigh = lineHeight*2;
        for (int b = 0; b < buckets.size(); b++)
        {
            Integer bucketSize = buckets.get(b);
            if (bucketSize == 0)
                continue;
            int leftpx = (int)MathUtils.interpolate(b, 0, buckets.size() + 1, 0, width);
            int rightpx = (int)MathUtils.interpolate(b + 1, 0, buckets.size() + 1, 0, width);
            int toppx = (int)MathUtils.interpolate(bucketSize, 0, vmax, graphLow, graphHigh);
            gc.setColor(Color.lightGray);
            gc.fillRect(leftpx, toppx, rightpx - leftpx, graphLow - toppx);
            gc.setColor(Color.black);
            gc.drawRect(leftpx, toppx, rightpx - leftpx, graphLow - toppx);
            gc.drawString(String.valueOf(bucketSize), leftpx, graphLow + ascent);
        }
        
        String svg = gc.getSVGDocument();
        gc.dispose();
        return svg;
    }
    
    private static int divideValues(double[] vals, List<Integer> buckets, double low, double high, double step)
    {
        int[] b = new int[(int)((high - low)/step)+1];
        for (double d : vals)
        {
            int idx = (int)((d - low)/step);
            b[idx]++;
        }
        int max = 0;
        buckets.clear();
        for (int i : b)
        {
            buckets.add(i);
            max = Math.max(max, i);
        }
        return max;
    }
    
    public static String getHTMLTable(double min, double max, double[] vals,
            double left, double right, int pixels)
    {
        StringBuffer text = new StringBuffer();
        int minpx = (int)MathUtils.interpolate(min, left, right, 0, pixels);
        int minqpx = (int)MathUtils.interpolate(vals[vals.length/4], left, right, 0, pixels);
        int medpx = (int)MathUtils.interpolate(vals[vals.length/2], left, right, 0, pixels);
        int maxqpx = (int)MathUtils.interpolate(vals[vals.length*3/4], left, right, 0, pixels);
        int maxpx = (int)MathUtils.interpolate(max, left, right, 0, pixels);
   
        text.append("<table style=\"width: "+pixels+"px; border: 1px solid black; padding: 0px; margin: 0px; border-collapse: collapse; \">\n");
        
        // data row
        text.append("<tr>");
        text.append("<td style=\"width: "+minpx+";\">&nbsp;</td>"); // gap between left and lower end
        text.append("<td style=\"width: "+(minqpx - minpx)+"; background-color: yellow; \">&nbsp;</td>"); // lower quartile whisker
        text.append("<td style=\"width: "+(medpx - minqpx)+"; background-color: red; \">&nbsp;</td>"); // left mid range
        text.append("<td style=\"width: "+(maxqpx - medpx)+"; background-color: red; \">&nbsp;</td>"); // right mid range
        text.append("<td style=\"width: "+(maxpx - maxqpx)+"; background-color: yellow; \">&nbsp;</td>"); // upper quartile whisker
        text.append("<td style=\"width: "+(pixels - maxpx)+";\">&nbsp;</td>"); // gap between upper end and right
        text.append("</tr>");
        
        text.append("<tr>");
        text.append("<td style=\"width: "+minpx+";\">&nbsp;</td>"); // gap between left and lower end
        text.append("<td style=\"width: "+(minqpx - minpx)+"; border-left: 1px solid black; \">&nbsp;</td>"); // lower quartile whisker
        text.append("<td style=\"width: "+(medpx - minqpx)+"; border-left: 1px solid black; \">&nbsp;</td>"); // left mid range
        text.append("<td colspan=\"5\" style=\"width: "+(pixels - medpx)+"; text-align: left; border-left: 1px solid black; \">"+FormatUtils.formatDouble(vals[vals.length/2], 2)+" ly</td>");
        text.append("</tr>");
        
        text.append("<tr>");
        text.append("<td style=\"width: "+minpx+";\">&nbsp;</td>"); // gap between left and lower end
        text.append("<td style=\"width: "+(minqpx - minpx)+"; border-left: 1px solid black; \">&nbsp;</td>"); // lower quartile whisker
        text.append("<td colspan=\"5\" style=\"width: "+(pixels - minqpx)+"; text-align: left; border-left: 1px solid black; \">"+FormatUtils.formatDouble(vals[vals.length/4], 2)+" ly</td>");
        text.append("</tr>");
   
        text.append("<tr>");
        text.append("<td style=\"width: "+minpx+";\">&nbsp;</td>"); // gap between left and lower end
        text.append("<td colspan=\"5\" style=\"width: "+(pixels - minpx)+"; text-align: left; border-left: 1px solid black; \">"+FormatUtils.formatDouble(min, 2)+" ly</td>"); // lower end
        text.append("</tr>");
   
        // overall row
        text.append("<tr>");
        text.append("<td colspan=\"3\" style=\"width: "+medpx+"; text-align: left; \">"+FormatUtils.formatDouble(left, 2)+" ly</td>");
        text.append("<td colspan=\"3\" style=\"width: "+(pixels - medpx)+"; text-align: right\">"+FormatUtils.formatDouble(right, 2)+" ly</td>");
        text.append("</tr>");
        text.append("</table>\n");
        return text.toString();
    }

    private static double[][] getLinks(StarBean[] starList, double linkDist1, double linkDist2,
            double linkDist3, double linkDist4)
    {
        List<Double> low = new ArrayList<Double>();
        List<Double> med = new ArrayList<Double>();
        List<Double> high = new ArrayList<Double>();
        List<Double> all = new ArrayList<Double>();
        
        for (int i = 0; i < starList.length - 1; i++)
            for (int j = i + 1; j < starList.length; j++)
            {
                double d = StarExtraLogic.distance(starList[i], starList[j]);
                if (d <= linkDist4)
                {
                    if (d > linkDist3)
                        high.add(d);
                    else if (d >= linkDist2)
                        med.add(d);
                    else if (d >= linkDist1)
                        low.add(d);
                    if (d >= linkDist1)
                        all.add(d);
                }
            }
        
        double[][] links = new double[4][];
        links[0] = toArray(low);
        links[1] = toArray(med);
        links[2] = toArray(high);
        links[3] = toArray(all);
        return links;
    }

    public static double[] toArray(List<Double> oarr)
    {
        double[] arr = new double[oarr.size()];
        for (int i = 0; i < arr.length; i++)
            arr[i] = oarr.get(i);
        Arrays.sort(arr);
        return arr;
    }
}
