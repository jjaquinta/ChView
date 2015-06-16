package jo.d2k.admin.rcp.viz.chview.handlers;

import java.util.ArrayList;
import java.util.List;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.data.data.StarBean;
import jo.util.geom3d.Point3D;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class HandlerOptimizeFlattest extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent ev) throws ExecutionException
    {
        List<StarBean> workingSet = getWorkingSet();
        int[] best = null;
        double bestV = 0;
        for (int dx = 0; dx < 360; dx += 10)
            for (int dy = 0; dy < 360; dy += 10)
            {
                ChViewVisualizationLogic.rotateTo(dx, dy);
                double err = 0;
                for (StarBean star : workingSet)
                {
                    Point3D pre = new Point3D(star.getX(), star.getY(), star.getZ());
                    Point3D post = ChViewVisualizationLogic.calcLocation(pre);
                    err += Math.abs(post.z);
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

    public static List<StarBean> getWorkingSet()
    {
        int selectionSize = ChViewVisualizationLogic.mPreferences.getSelected().size();
        if (selectionSize >= 2)
        {
            List<StarBean> working = new ArrayList<StarBean>(selectionSize);
            working.addAll(ChViewVisualizationLogic.mPreferences.getSelected());
            return working;
        }
        List<StarBean> working = new ArrayList<StarBean>(ChViewVisualizationLogic.mPreferences.getStars().size());
        working.addAll(ChViewVisualizationLogic.mPreferences.getStars());
        if (ChViewVisualizationLogic.mPreferences.getHidden().size() > 0)
            working.removeAll(ChViewVisualizationLogic.mPreferences.getHidden());
        return working;
    }
}
