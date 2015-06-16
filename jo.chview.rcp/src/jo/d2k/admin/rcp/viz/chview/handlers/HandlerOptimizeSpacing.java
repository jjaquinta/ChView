package jo.d2k.admin.rcp.viz.chview.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarBean;
import jo.util.geom3d.Point3D;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class HandlerOptimizeSpacing extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        List<StarBean> workingSet = HandlerOptimizeFlattest.getWorkingSet();
        int[] best = null;
        double bestV = 0;
        for (int dx = 0; dx < 360; dx += 10)
            for (int dy = 0; dy < 360; dy += 10)
            {
                ChViewVisualizationLogic.rotateTo(dx, dy);
                Map<StarBean, Point3D> locations = new HashMap<StarBean, Point3D>();
                for (StarBean star : workingSet)
                {
                    Point3D pre = new Point3D(star.getX(), star.getY(), star.getZ());
                    Point3D post = ChViewVisualizationLogic.calcLocation(pre);
                    locations.put(star,  post);
                }
                double err = 0;
                for (StarBean star : workingSet)
                {
                    Point3D starLoc = locations.get(star);
                    StarBean nearest = null;
                    double dist = 0;
                    for (StarBean companion : workingSet)
                        if (companion != star)
                        {
                            Point3D companionLoc = locations.get(companion);
                            double d = starLoc.dist(companionLoc);
                            if ((nearest == null) || (d < dist))
                            {
                                nearest = companion;
                                dist = d;
                            }
                        }
                    err += dist;
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
