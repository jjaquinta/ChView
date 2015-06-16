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
import jo.d2k.data.logic.StarExtraLogic;
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
public class DistanceReport
{
    public static String generateReport(final ChViewContextBean params)
    {
        StringBuffer text = new StringBuffer();

        text.append("<html>");
        text.append("<head>");
        text.append("<title>DISTANCE REPORT</title>");
        text.append("<script type=\"text/javascript\">");
        text.append("var tsorter=function(){\"use strict\";var a,b,c,d=!!document.addEventListener;return Object.create||(Object.create=function(a){var b=function(){return void 0};return b.prototype=a,new b}),b=function(a,b,c){d?a.addEventListener(b,c,!1):a.attachEvent(\"on\"+b,c)},c=function(a,b,c){d?a.removeEventListener(b,c,!1):a.detachEvent(\"on\"+b,c)},a={getCell:function(a){var b=this;return b.trs[a].cells[b.column]},sort:function(a){var b=this,c=a.target;b.column=c.cellIndex,b.get=b.getAccessor(c.getAttribute(\"data-tsorter\")),b.prevCol===b.column?(c.className=\"ascend\"!==c.className?\"ascend\":\"descend\",b.reverseTable()):(c.className=\"ascend\",-1!==b.prevCol&&\"exc_cell\"!==b.ths[b.prevCol].className&&(b.ths[b.prevCol].className=\"\"),b.quicksort(0,b.trs.length)),b.prevCol=b.column},getAccessor:function(a){var b=this,c=b.accessors;if(c&&c[a])return c[a];switch(a){case\"link\":return function(a){return b.getCell(a).firstChild.firstChild.nodeValue};case\"input\":return function(a){return b.getCell(a).firstChild.value};case\"numeric\":return function(a){return parseFloat(b.getCell(a).firstChild.nodeValue,10)};default:return function(a){return b.getCell(a).firstChild.nodeValue}}},exchange:function(a,b){var c,d=this,e=d.tbody,f=d.trs;a===b+1?e.insertBefore(f[a],f[b]):b===a+1?e.insertBefore(f[b],f[a]):(c=e.replaceChild(f[a],f[b]),f[a]?e.insertBefore(c,f[a]):e.appendChild(c))},reverseTable:function(){var a,b=this;for(a=1;a<b.trs.length;a++)b.tbody.insertBefore(b.trs[a],b.trs[0])},quicksort:function(a,b){var c,d,e,f=this;if(!(a+1>=b)){if(b-a===2)return void(f.get(b-1)>f.get(a)&&f.exchange(b-1,a));for(c=a+1,d=b-1,f.get(a)>f.get(c)&&f.exchange(c,a),f.get(d)>f.get(a)&&f.exchange(a,d),f.get(a)>f.get(c)&&f.exchange(c,a),e=f.get(a);;){for(d--;e>f.get(d);)d--;for(c++;f.get(c)>e;)c++;if(c>=d)break;f.exchange(c,d)}f.exchange(a,d),b-d>d-a?(f.quicksort(a,d),f.quicksort(d+1,b)):(f.quicksort(d+1,b),f.quicksort(a,d))}},init:function(a,c,d){var e,f=this;for(\"string\"==typeof a&&(a=document.getElementById(a)),f.table=a,f.ths=a.getElementsByTagName(\"th\"),f.tbody=a.tBodies[0],f.trs=f.tbody.getElementsByTagName(\"tr\"),f.prevCol=c&&c>0?c:-1,f.accessors=d,f.boundSort=f.sort.bind(f),e=0;e<f.ths.length;e++)b(f.ths[e],\"click\",f.boundSort)},destroy:function(){var a,b=this;if(b.ths)for(a=0;a<b.ths.length;a++)c(b.ths[a],\"click\",b.boundSort)}},{create:function(b,c,d){var e=Object.create(a);return e.init(b,c,d),e}}}();");
        text.append("function init() {");
        text.append("var sorter = tsorter.create('dist');");
        text.append("}");
        text.append("window.onload = init;");
        text.append("</script>");
        text.append("<style type=\"text/css\">");
        text.append("th.descend:after{");
        text.append("content: \"\\25B2\";");
        text.append("}");
        text.append("th.ascend:after{");
        text.append("content: \"\\25BC\";");
        text.append("}");
        text.append("</style>        ");
        text.append("</head>");
        text.append("<body>");
        text.append("<h1>DISTANCE REPORT</h1>");
        UtilLogic.reportFilter(text, params);
        Collection<StarBean> starList = params.getSelected();
        StarBean focus = params.getFocus();
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
		    if (focus == null)
		    {
		        double bestDist = -1;
		        Point3D center = params.getCenter();
		        for (StarBean star : starList)
		        {
		            double d = StarExtraLogic.distance(star, center.getX(), center.getY(), center.getZ());
		            if ((focus == null) || (d < bestDist))
		            {
		                bestDist = d;
		                focus = star;
		            }
		        }
		    }
		    text.append("<h2>Distances from "+ChViewFormatLogic.getStarName(params, focus)+"</h2>");
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
	        text.append("<table id=\"dist\">");
            text.append("<thead>");
            text.append("<tr style=\"background-color:#E0E0E0;color:#3333FF\"><th>Name</th><th data-tsorter=\"numeric\">Distance</th></tr>");
            text.append("</thead>");
            text.append("<tbody>");
			// values
			for (int i = 0; i < stars.length; i++)
			{
			    if (stars[i] == focus)
			        continue;
                text.append("<tr style=\"text-align: right;\">");
                text.append("<td>"+ChViewFormatLogic.getStarName(params, stars[i])+"</td>");
                text.append("<td>");
                text.append(FormatUtils.formatDouble(StarExtraLogic.distance(focus, stars[i]), 2));
                text.append("</td>");
				text.append("</tr>\n");
			}
            text.append("</tbody>");
			text.append("</table>");
		}
        text.append("</body>");
        text.append("</html>");
        return text.toString();
    }
}
