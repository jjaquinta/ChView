package jo.d2k.admin.rcp.sys.viz.twod.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jo.d2k.admin.rcp.viz.chview.prefs.ChViewPreferencesBean;
import jo.util.beans.PCSBean;
import jo.util.geom2d.Point2D;

import org.eclipse.swt.graphics.Rectangle;

public class TwoDDisplay extends PCSBean implements Cloneable
{
    private ChViewPreferencesBean mPrefs;
    private List<TwoDStarBean>  mStars = new ArrayList<>();
    private List<TwoDLinkBean>  mLinks = new ArrayList<>();
    private List<TwoDRouteBean> mRoutes = new ArrayList<>();
    private List<TwoDObject>    mObjects = new ArrayList<>();
    private Point2D             mUpperBounds;
    private Point2D             mLowerBounds;
    private Set<TwoDObject>     mSelected = new HashSet<>();
    private TwoDObject          mFocus;
    private Rectangle           mSelectionBand;
    private Point2D             mMouseDown;
    
    public List<TwoDStarBean> getStars()
    {
        return mStars;
    }
    public void setStars(List<TwoDStarBean> stars)
    {
        mStars = stars;
    }
    public List<TwoDLinkBean> getLinks()
    {
        return mLinks;
    }
    public void setLinks(List<TwoDLinkBean> links)
    {
        mLinks = links;
    }
    public List<TwoDRouteBean> getRoutes()
    {
        return mRoutes;
    }
    public void setRoutes(List<TwoDRouteBean> routes)
    {
        mRoutes = routes;
    }
    public Point2D getUpperBounds()
    {
        return mUpperBounds;
    }
    public void setUpperBounds(Point2D upperBounds)
    {
        mUpperBounds = upperBounds;
    }
    public Point2D getLowerBounds()
    {
        return mLowerBounds;
    }
    public void setLowerBounds(Point2D lowerBounds)
    {
        mLowerBounds = lowerBounds;
    }
    public ChViewPreferencesBean getPrefs()
    {
        return mPrefs;
    }
    public void setPrefs(ChViewPreferencesBean prefs)
    {
        mPrefs = prefs;
    }
    public List<TwoDObject> getObjects()
    {
        return mObjects;
    }
    public void setObjects(List<TwoDObject> objects)
    {
        mObjects = objects;
    }
    public Set<TwoDObject> getSelected()
    {
        return mSelected;
    }
    public void setSelected(Set<TwoDObject> selected)
    {
        mSelected = selected;
    }
    public TwoDObject getFocus()
    {
        return mFocus;
    }
    public void setFocus(TwoDObject focus)
    {
        mFocus = focus;
    }
    public Rectangle getSelectionBand()
    {
        return mSelectionBand;
    }
    public void setSelectionBand(Rectangle selectionBand)
    {
        mSelectionBand = selectionBand;
    }
    public Point2D getMouseDown()
    {
        return mMouseDown;
    }
    public void setMouseDown(Point2D mouseDown)
    {
        mMouseDown = mouseDown;
    }
}
