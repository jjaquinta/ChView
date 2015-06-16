package jo.d2k.admin.rcp.viz.chview.handlers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarLink;
import jo.util.geom2d.Line2D;
import jo.util.geom2d.Line2DLogic;
import jo.util.geom2d.Point2D;
import jo.util.geom3d.Point3D;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class HandlerOptimizeCrossings extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        List<StarBean> workingList = HandlerOptimizeFlattest.getWorkingSet();
        Set<StarBean> workingSet = new HashSet<StarBean>();
        workingSet.addAll(workingList);
        int[] best = null;
        double bestV = 0;
        for (int dx = 0; dx < 360; dx += 10)
            for (int dy = 0; dy < 360; dy += 10)
            {
                ChViewVisualizationLogic.rotateTo(dx, dy);
                Map<StarBean, Point3D> locations = new HashMap<StarBean, Point3D>();
                for (StarBean star : workingList)
                {
                    Point3D pre = new Point3D(star.getX(), star.getY(), star.getZ());
                    Point3D post = ChViewVisualizationLogic.calcLocation(pre);
                    locations.put(star,  post);
                }
                double err = 0;
                for (StarLink link1 : ChViewVisualizationLogic.mPreferences.getLinks())
                {
                    if (!workingSet.contains(link1.getStar1()) || !workingSet.contains(link1.getStar2()))
                        continue;
                    Point3D link1Loc1 = locations.get(link1.getStar1());
                    Point3D link1Loc2 = locations.get(link1.getStar2());
                    Line2D link1line = new Line2D(link1Loc1.x, link1Loc1.y, link1Loc2.x, link1Loc2.y);
                    for (StarLink link2 : ChViewVisualizationLogic.mPreferences.getLinks())
                    {
                        if (link1 == link2)
                            continue;
                        if (!workingSet.contains(link2.getStar1()) || !workingSet.contains(link2.getStar2()))
                            continue;
                        if ((link1.getStar1() == link2.getStar1()) || (link1.getStar1() == link2.getStar2()))
                            continue;
                        if ((link1.getStar2() == link2.getStar1()) || (link1.getStar2() == link2.getStar2()))
                            continue;                        
                        Point3D link2Loc1 = locations.get(link2.getStar1());
                        Point3D link2Loc2 = locations.get(link2.getStar2());
                        Line2D link2line = new Line2D(link2Loc1.x, link2Loc1.y, link2Loc2.x, link2Loc2.y);
                        Point2D intersect = Line2DLogic.intersectSegment(link1line,  link2line);
                        if (intersect != null)
                            err++;
                    }
                }
                if ((best == null) || (err < bestV))
                {
                    best = new int [] { dx, dy };
                    bestV = err;
                }
            }
        ChViewVisualizationLogic.rotateTo(best[0], best[1]);
        return null;
    }

}
