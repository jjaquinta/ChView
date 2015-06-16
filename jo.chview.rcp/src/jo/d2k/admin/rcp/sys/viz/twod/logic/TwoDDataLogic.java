package jo.d2k.admin.rcp.sys.viz.twod.logic;

import java.util.List;
import java.util.Map;

import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDDisplay;
import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDLinkBean;
import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDObject;
import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDRouteBean;
import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDStarBean;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.d2k.admin.rcp.viz.chview.prefs.ChViewPreferencesBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarLink;
import jo.d2k.data.data.StarRouteBean;
import jo.util.geom2d.Point2D;
import jo.util.geom2d.Point2DLogic;
import jo.util.geom3d.Point3i;
import jo.util.utils.FormatUtils;

import org.eclipse.swt.graphics.Rectangle;

public class TwoDDataLogic
{
    public static TwoDDisplay makeDisplay(ChViewPreferencesBean prefs,
            List<StarBean> stars, List<StarLink> links, List<StarRouteBean> routes)
    {
        TwoDDisplay display = new TwoDDisplay();
        display.setPrefs(prefs);
        if (stars != null)
            for (StarBean star : stars)
                addStar(display, star);
        if (links != null)
            for (StarLink link : links)
                addLink(display, link);
        if (routes != null)
            for (StarRouteBean route : routes)
                addRoute(display, route);
        display.getObjects().addAll(display.getStars());
        display.getObjects().addAll(display.getLinks());
        display.getObjects().addAll(display.getRoutes());
        updateBounds(display);
        return display;
    }

    private static void addStar(TwoDDisplay display, StarBean star)
    {
        TwoDStarBean tdStar = new TwoDStarBean();
        tdStar.setStar(star);
        Point3i p = ChViewVisualizationLogic.getLocation(star);
        tdStar.setLocation(new Point2D(p.x, p.y));
        tdStar.setLabel(ChViewRenderLogic.getStarName(star));
        display.getStars().add(tdStar);
    }

    private static void addLink(TwoDDisplay display, StarLink link)
    {
        TwoDLinkBean tdLink = new TwoDLinkBean();
        tdLink.setLink(link);
        tdLink.setStar1(findStar(display, link.getStar1()));
        tdLink.setStar2(findStar(display, link.getStar2()));
        if (ChViewVisualizationLogic.mPreferences.isShowLinkNumbers())
            tdLink.setLabel(FormatUtils.formatDouble(link.getDistance(), 1));
        display.getLinks().add(tdLink);
    }

    private static void addRoute(TwoDDisplay display, StarRouteBean route)
    {
        TwoDRouteBean tdRoute = new TwoDRouteBean();
        tdRoute.setRoute(route);
        tdRoute.setStar1(findStar(display, route.getStar1Ref()));
        tdRoute.setStar2(findStar(display, route.getStar2Ref()));
        display.getRoutes().add(tdRoute);
    }

    private static TwoDStarBean findStar(TwoDDisplay display, StarBean star)
    {
        for (TwoDStarBean s : display.getStars())
            if (s.getStar() == star)
                return s;
        return null;
    }

    private static void updateBounds(TwoDDisplay display)
    {
        Point2D min = null;
        Point2D max = null;
        for (TwoDStarBean star : display.getStars())
        {
            if (min == null)
                min = new Point2D(star.getLocation());
            else
            {
                min.x = Math.min(min.x, star.getLocation().x);
                min.y = Math.min(min.y, star.getLocation().y);
            }
            if (max == null)
                max = new Point2D(star.getLocation());
            else
            {
                max.x = Math.max(max.x, star.getLocation().x);
                max.y = Math.max(max.y, star.getLocation().y);
            }
        }
        display.setLowerBounds(min);
        display.setUpperBounds(max);
        for (TwoDLinkBean link : display.getLinks())
            link.setLocation(Point2DLogic.average(link.getStar1().getLocation(), link.getStar2().getLocation()));
        for (TwoDRouteBean route : display.getRoutes())
            route.setLocation(Point2DLogic.average(route.getStar1().getLocation(), route.getStar2().getLocation()));
    }

    public static TwoDObject getObjectAt(TwoDDisplay disp, Point2D p)
    {
        for (TwoDObject o : disp.getObjects())
            if (o.getLocation().dist(p) < 6)
                return o;
        return null;
    }

    public static void addToSelection(TwoDDisplay disp, TwoDObject sel)
    {
        if (disp.getSelected().contains(sel))
            return;
        disp.getSelected().add(sel);
        disp.fireMonotonicPropertyChange("data");
    }

    public static void removeFromSelection(TwoDDisplay disp, TwoDObject sel)
    {
        if (!disp.getSelected().contains(sel))
            return;
        disp.getSelected().remove(sel);
        disp.fireMonotonicPropertyChange("data");
    }

    public static void toggleSelection(TwoDDisplay disp, TwoDObject sel)
    {
        if (disp.getSelected().contains(sel))
            removeFromSelection(disp, sel);
        else
            addToSelection(disp, sel);
    }

    public static void setFocused(TwoDDisplay disp, TwoDObject sel)
    {
        if (sel == disp.getFocus())
            return;
        disp.setFocus(sel);
        disp.fireMonotonicPropertyChange("data");    
    }

    public static void setSelectionBand(TwoDDisplay disp, Rectangle sel)
    {
        disp.setSelectionBand(sel);
        disp.fireMonotonicPropertyChange("data");
    }

    public static void move(TwoDDisplay disp, Point2D d)
    {
        if (disp.getFocus() != null)
            disp.getFocus().getLocation().incr(d);
        for (TwoDObject o : disp.getSelected())
            if (o instanceof TwoDStarBean)
                o.getLocation().incr(d);
        updateBounds(disp);
        disp.fireMonotonicPropertyChange("data");
    }

    public static void move(TwoDDisplay disp, Map<TwoDStarBean, Point2D> locations)
    {
        for (TwoDStarBean star : locations.keySet())
            star.setLocation(locations.get(star));
        updateBounds(disp);
        disp.fireMonotonicPropertyChange("data");
    }

    public static void selectNone(TwoDDisplay disp)
    {
        disp.getSelected().clear();
        disp.fireMonotonicPropertyChange("data");
    }
    
    public static void selectWithin(TwoDDisplay disp, Rectangle rectangle)
    {
        if (rectangle.width < 0)
        {
            rectangle.x += rectangle.width;
            rectangle.width *= -1;
        }
        if (rectangle.height < 0)
        {
            rectangle.y += rectangle.height;
            rectangle.height *= -1;
        }
        for (TwoDObject obj : disp.getObjects())
        {
            if (disp.getSelected().contains(obj))
                continue;
            if (rectangle.contains((int)obj.getLocation().x, (int)obj.getLocation().y))
                disp.getSelected().add(obj);
        }
        disp.fireMonotonicPropertyChange("data");
    }
}
