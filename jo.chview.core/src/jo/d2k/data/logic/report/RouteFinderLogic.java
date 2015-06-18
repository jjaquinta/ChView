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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarFilter;
import jo.d2k.data.logic.FilterLogic;
import jo.d2k.data.logic.StarLogic;
import jo.util.astar.AStarNode;
import jo.util.astar.AStarSearch;
import jo.util.geom3d.Point3D;
import jo.util.geom3d.Point3DLogic;

/**
 * @author jgrant
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class RouteFinderLogic
{
    public static List<StarBean> computeRoute(StarBean o1, StarBean o2, double d, Set<StarBean> exclude, StarFilter filter, ChViewContextBean params)
    {
        if (d <= 0)
            d = params.getLinkDist4();
        if (!FilterLogic.isAnyFilter(filter))
            filter = null;
        Point3D p1 = new Point3D(o1.getX(), o1.getY(), o1.getZ());
        Point3D p2 = new Point3D(o2.getX(), o2.getY(), o2.getZ());
        Point3D center = Point3DLogic.add(p1, p2);
        center.scale(.5);
        double radius = p1.dist(p2)/2 + d;
        List<StarBean> stars = StarLogic.getAllWithin(center.x, center.y, center.z, radius);
        for (Iterator<StarBean> i = stars.iterator(); i.hasNext(); )
        {
            StarBean star = i.next();
            if (star.getParent() != 0)
                i.remove();
            else if ((exclude != null) && exclude.contains(star))
                i.remove();
            else if ((filter != null) && FilterLogic.isFiltered(params, star, filter))
                i.remove();
        }
        return computeRoute(stars, d, o1, o2);
    }
    
	public static List<StarBean> computeRoute(List<StarBean> stars, double jumpDist, StarBean o1, StarBean o2)
	{
		// first find a route to link the two objects
	    // normalize stars
	    for (StarBean star : stars)
	    {
	        if (star.equals(o1))
	            o1 = star;
	        else if (star.equals(o2))
	            o2 = star;
	    }
	    StarNode.init(stars, jumpDist);
	    AStarSearch src = new AStarSearch();
	    StarNode startNode = StarNode.getInstance(o1);
        StarNode goalNode = StarNode.getInstance(o2);
	    List<AStarNode> apath = src.findPath(startNode, goalNode);
	    StarNode.term();
        List<StarBean> path = new ArrayList<StarBean>();        
        if (apath != null)
        {
            path.add(o1);
            for (AStarNode astar : apath)
                path.add(((StarNode)astar).getStar());
        }
        return path;
	}
}
