package jo.d2k.admin.rcp.viz.chview.logic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jo.d2k.admin.rcp.viz.chview.prefs.ChViewPreferencesBean;
import jo.d2k.admin.rcp.viz.chview.prefs.ChViewPreferencesEclipse;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarFilter;
import jo.d2k.data.data.StarRouteBean;
import jo.d2k.data.logic.FilterLogic;
import jo.d2k.data.logic.MetadataLogic;
import jo.d2k.data.logic.SkyLogic;
import jo.d2k.data.logic.StarExtraLogic;
import jo.d2k.data.logic.StarLogic;
import jo.d2k.data.logic.StarRouteLogic;
import jo.util.geom3d.Point3D;
import jo.util.geom3d.Point3DLogic;
import jo.util.geom3d.Point3i;
import jo.util.logic.ThreadLogic;

import org.eclipse.swt.graphics.Rectangle;

public class ChViewVisualizationLogic
{
    public static ChViewPreferencesBean   mPreferences = new ChViewPreferencesEclipse();
    static
    {
        mPreferences.addPropertyChangeListener(new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                if (!evt.getPropertyName().equals("data"))
                    fireNewPreferences();
            }
        });
    }
    static
    {
        StarLogic.addPropertyChangeListener("data", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                updateData(true);
            }
        });
    }
    
    private static void fireNewPreferences()
    {
        mPreferences.fireMonotonicPropertyChange("data");
    }
    
    static StarBean getStarAt(int x, int y)
    {
        StarBean closest = null;
        double dist = -1;
        for (StarBean s : mPreferences.getFilteredStars())
        {
            Point3i l = getLocation(s);
            double d = Math.abs(l.x - x) + Math.abs(l.y - y);
            if ((closest == null) || (d < dist))
            {
                closest = s;
                dist = d;
            }
        }
        if (closest == null)
            return null;
        if (dist < 12)
            return closest;
        return null;
    }
    
    public static synchronized void updateData(boolean background)
    {
        if (background)
        {
            ThreadLogic.runOnBackgroundThread(new Thread("updateDataBackground") { public void run() { ChViewVisualizationLogic.updateData(false); } });
            return;
        }
        List<StarBean> stars = StarLogic.getAllWithin(mPreferences.getCenter().x, mPreferences.getCenter().y, mPreferences.getCenter().z, mPreferences.getRadius());
        List<StarBean> filteredStars = new ArrayList<StarBean>();
        StarFilter filter = mPreferences.getFilter();
        if (!FilterLogic.isAnyFilter(filter))
            filter = null;
        for (Iterator<StarBean> i = stars.iterator(); i.hasNext(); )
        {
            StarBean next = i.next();
            if (next.getParent() != 0)
                i.remove();
            else if (filter == null)
                filteredStars.add(next);
            else
                if (!FilterLogic.isFiltered(mPreferences, next, filter))
                    filteredStars.add(next);
        }
        mPreferences.setStars(stars);
        mPreferences.setFilteredStars(filteredStars);
        mPreferences.setLinks(StarExtraLogic.findLinks(mPreferences.getFilteredStars(), mPreferences.getLinkDist4()));
        mPreferences.setRoutes(StarRouteLogic.getAllLinking(mPreferences.getFilteredStars()));
        //StarExtraLogic.pruneLinks(mPreferences.getLinks(), 3, mPreferences.getLinkDist1());
        // prune selection
        for (Iterator<StarBean> i = mPreferences.getSelected().iterator(); i.hasNext(); )
        {
            StarBean sel = i.next();
            if (!mPreferences.getFilteredStars().contains(sel))
                i.remove();
        }
        // prune hidden
        for (Iterator<StarBean> i = mPreferences.getHidden().iterator(); i.hasNext(); )
        {
            StarBean sel = i.next();
            if (!mPreferences.getFilteredStars().contains(sel))
                i.remove();
        }
        if (mPreferences.getFocus() != null)
            if (!mPreferences.getFilteredStars().contains(mPreferences.getFocus()))
                mPreferences.setFocus(null);
        mPreferences.getSky().clear();
        if (mPreferences.isShowSky())
        {
            mPreferences.fireMonotonicPropertyChange("data");
            mPreferences.fireMonotonicPropertyChange("context");
            SkyLogic.getSky(mPreferences.getCenter(), mPreferences.getSky());
        }
        mPreferences.getLocations().clear();
        mPreferences.fireMonotonicPropertyChange("data");
        mPreferences.fireMonotonicPropertyChange("context");
    }
        
    public static Point3i getLocation(StarBean star)
    {
        Point3D spaceL = new Point3D(star.getX(),  star.getY(), star.getZ());
        return getLocation(spaceL);
    }

    public static Point3i getLocation(Point3D spaceL)
    {
        Point3i l = mPreferences.getLocations().get(spaceL);
        if (l == null)
        {
            spaceL = calcLocation(spaceL);
            int x = (int)(spaceL.x*mPreferences.getScale());
            int y = (int)(spaceL.y*mPreferences.getScale());
            int z = (int)(spaceL.z*mPreferences.getScale());
            l = new Point3i(x, y, z);
            mPreferences.getLocations().put(spaceL, l);
        }
        return l;
    }

    public static Point3D calcLocation(Point3D spaceL)
    {
        spaceL = Point3DLogic.sub(spaceL, mPreferences.getCenter());
        spaceL = Point3DLogic.rotate(spaceL, mPreferences.getRotation());
        return spaceL;
    }

    public static void expand()
    {
        mPreferences.setScale(mPreferences.getScale()*1.1);
        mPreferences.getLocations().clear();
        mPreferences.fireMonotonicPropertyChange("data");
    }

    public static void shrink()
    {
        mPreferences.setScale(mPreferences.getScale()/1.1);
        mPreferences.getLocations().clear();
        mPreferences.fireMonotonicPropertyChange("data");
    }

    public static void setRadius(double radius)
    {
        mPreferences.setRadius(radius);
        updateData(true);
    }

    public static void enlarge()
    {
        setRadius(mPreferences.getRadius()*1.1);
    }

    public static void reduce()
    {
        setRadius(mPreferences.getRadius()/1.1);
    }

    public static void toggleNames()
    {
        mPreferences.setShowNames(!mPreferences.isShowNames());
        mPreferences.fireMonotonicPropertyChange("data");
    }

    public static void toggleLinks()
    {
        mPreferences.setShowLinks(!mPreferences.isShowLinks());
        mPreferences.fireMonotonicPropertyChange("data");
    }

    public static void toggleLinkNumbers()
    {
        mPreferences.setShowLinkNumbers(!mPreferences.isShowLinkNumbers());
        mPreferences.fireMonotonicPropertyChange("data");
    }

    public static void toggleRoutes()
    {
        mPreferences.setShowRoutes(!mPreferences.isShowRoutes());
        mPreferences.fireMonotonicPropertyChange("data");
    }

    public static void toggleScope()
    {
        mPreferences.setShowScope(!mPreferences.isShowScope());
        mPreferences.fireMonotonicPropertyChange("data");
    }

    public static void toggleSky()
    {
        mPreferences.setShowSky(!mPreferences.isShowSky());
        updateData(true);
    }

    public static void toggleGrid()
    {
        mPreferences.setShowGrid(!mPreferences.isShowGrid());
        updateData(true);
    }

    public static void setCenter(Point3D center)
    {
        mPreferences.setCenter(center);
        updateData(true);
    }

    public static void addToSelection(StarBean sel)
    {
        if (mPreferences.getSelected().contains(sel))
            return;
        mPreferences.getSelected().add(sel);
        mPreferences.fireMonotonicPropertyChange("data");
    }

    public static void removeFromSelection(StarBean sel)
    {
        if (!mPreferences.getSelected().contains(sel))
            return;
        mPreferences.getSelected().remove(sel);
        mPreferences.fireMonotonicPropertyChange("data");
    }

    public static void addToSelection(Collection<StarBean> sel)
    {
        if (mPreferences.getSelected().containsAll(sel))
            return;
        mPreferences.getSelected().addAll(sel);
        mPreferences.fireMonotonicPropertyChange("data");
    }

    public static void removeFromSelection(Collection<StarBean> sel)
    {
        if (!mPreferences.getSelected().containsAll(sel))
            return;
        mPreferences.getSelected().removeAll(sel);
        mPreferences.fireMonotonicPropertyChange("data");
    }

    public static void toggleSelection(StarBean sel)
    {
        if (mPreferences.getSelected().contains(sel))
            removeFromSelection(sel);
        else
            addToSelection(sel);
    }

    public static void setSelection(Collection<StarBean> sel)
    {
        mPreferences.getSelected().clear();
        mPreferences.getSelected().addAll(sel);
        mPreferences.fireMonotonicPropertyChange("data");
    }

    public static void setFocused(StarBean sel)
    {
        if (mPreferences.getFocus() == sel)
            return;
        mPreferences.setFocus(sel);
        mPreferences.fireMonotonicPropertyChange("data");
    }

    public static void setSelectionBand(Rectangle sel)
    {
        mPreferences.setSelectionBand(sel);
        mPreferences.fireMonotonicPropertyChange("data");
    }

    public static void selectWithin(Rectangle rectangle)
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
        for (StarBean star : mPreferences.getFilteredStars())
        {
            if (mPreferences.getSelected().contains(star))
                continue;
            Point3i l = getLocation(star);
            if (rectangle.contains(l.x, l.y))
                mPreferences.getSelected().add(star);
        }
        mPreferences.fireMonotonicPropertyChange("data");
    }

    public static void rotate(int dx, int dy)
    {
        mPreferences.getRotation().y += dx*Math.PI/180;
        mPreferences.getRotation().x += dy*Math.PI/180;
        mPreferences.getLocations().clear();
        mPreferences.fireMonotonicPropertyChange("data");
    }
    
    public static void rotateTo(int dx, int dy)
    {
        mPreferences.getRotation().y = dx*Math.PI/180;
        mPreferences.getRotation().x = dy*Math.PI/180;
        mPreferences.getLocations().clear();
        mPreferences.fireMonotonicPropertyChange("data");
    }
    
    public static void showAll()
    {
        mPreferences.getHidden().clear();
        mPreferences.fireMonotonicPropertyChange("data");
    }
    
    public static void hide(Collection<StarBean> stars)
    {
        mPreferences.getHidden().addAll(stars);
        mPreferences.fireMonotonicPropertyChange("data");
    }
    
    public static void showOnly(Collection<StarBean> stars)
    {
        mPreferences.getHidden().addAll(mPreferences.getFilteredStars());
        mPreferences.getHidden().removeAll(stars);
        mPreferences.fireMonotonicPropertyChange("data");
    }

    public static void setFilter(StarFilter filter)
    {
        mPreferences.getFilter().set(filter);
        updateData(true);
    }

    public static void makeRoute(int routeNum)
    {
        List<StarBean> endPoints = new ArrayList<StarBean>();
        endPoints.addAll(mPreferences.getSelected());
        if (endPoints.size() == 1)
            endPoints.add(mPreferences.getFocus());
        List<StarRouteBean> existing = StarRouteLogic.getAllLinking(endPoints);
        if (routeNum < 0)
        {
            if (existing != null)
            {
                StarRouteLogic.delete(existing);
                updateData(true);
            }
            return;
        }
        StarRouteBean route;
        if (existing.size() == 0)
        {
            route = new StarRouteBean();
            route.setStar1(endPoints.get(0).getOID());
            route.setStar1Quad(endPoints.get(0).getQuadrant());
            route.setStar2(endPoints.get(1).getOID());
            route.setStar2Quad(endPoints.get(1).getQuadrant());
        }
        else
        {
            route = existing.get(0);
        }
        route.setType(routeNum);
        StarRouteLogic.update(route);
        updateData(true);
    }

    public static void makeRoutes(List<StarRouteBean> routes)
    {
        StarRouteLogic.update(routes);
        updateData(true);
    }

    public static void makeRoute(StarRouteBean route)
    {
        StarRouteLogic.update(route);
        updateData(true);
    }

    public static void deleteRoutes(List<StarRouteBean> routes)
    {
        StarRouteLogic.delete(routes);
        updateData(true);
    }

    public static void deleteStars(List<StarBean> stars)
    {
        StarLogic.delete(stars);
        updateData(true);
    }

    public static void updateStar(StarBean star)
    {
        if (!star.isGenerated() && (star.getOID() < 0))
            star = StarLogic.create(star);
        else
            StarLogic.update(star);
        if (star.getMetadata() != null)
            MetadataLogic.setAsMap("star.md", star.getOID(), star.getMetadata());
        updateData(true);
    }
}
