package jo.d2k.data.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jo.d2k.data.data.SkyBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarFilter;
import jo.d2k.data.data.StarLink;
import jo.d2k.data.data.StarRouteBean;
import jo.util.beans.PCSBean;
import jo.util.geom3d.Point3D;
import jo.util.geom3d.Point3i;

/*
 * This contains the volatile settings that govern the context
 * currently being viewed.
 */
public class ChViewContextBean extends PCSBean
{
    public static final String NAME = "Name";
    public static final String COMMON_NAME = "Common Name";
    public static final String HIP_NAME = "Hipparcos";
    public static final String GJ_NAME = "Gliese-Jahreiﬂ";
    public static final String HD_NAME = "Henry Draper";
    public static final String HR_NAME = "Harvard Revised";
    public static final String SAO_NAME = "SAO";
    public static final String TWOMASS_NAME = "2Mass";

    private Point3D mCenter;
    private Point3D mRotation;
    private double  mRadius;
    private double  mScale;
    private List<StarBean>  mStars;
    private List<SkyBean>   mSky;
    private List<StarLink>  mLinks;
    private List<StarRouteBean>  mRoutes;
    private Map<Point3D, Point3i> mLocations;
    private StarBean mFocus;
    private Set<StarBean> mSelected;
    private Set<StarBean> mHidden;
    
    private boolean mShowNames;
    private boolean mShowLinks;
    private boolean mShowLinkNumbers;
    private boolean mShowRoutes;
    private boolean mShowScope;
    private boolean mShowGrid;
    private boolean mShowSky;
    private StarFilter mFilter;

    private double  mLinkDist1;
    private double  mLinkDist2;
    private double  mLinkDist3;
    private double  mLinkDist4;
    private int mStar0Radius;
    private int mStar1Radius;
    private int mStar2Radius;
    private int mStar3Radius;
    private int mStar4Radius;
    private int mStar5Radius;
    private String mStarNameColumn;
    private String mRoute1Name;
    private String mRoute2Name;
    private String mRoute3Name;
    private String mRoute4Name;
    private String mRoute5Name;
    private String mRoute6Name;
    private String mRoute7Name;
    private String mRoute8Name;

    public ChViewContextBean()
    {
        mLocations = new HashMap<Point3D, Point3i>();
        mCenter = new Point3D(0,0,0);
        mRotation = new Point3D(0,0,0);
        mRadius = 15;
        mSelected = new HashSet<StarBean>();
        mHidden = new HashSet<StarBean>();
        mScale = 25;
        mShowNames = true;
        mShowLinks = true;
        mShowLinkNumbers = false;
        mShowRoutes = true;
        mFilter = new StarFilter();
        mShowSky = false;
        mSky = new ArrayList<SkyBean>();
        mLinkDist1 = 0.25;
        mLinkDist2 = 3;
        mLinkDist3 = 5;
        mLinkDist4 = 7;
        mStar0Radius = 2;
        mStar5Radius = 4;
        mStar4Radius = 6;
        mStar3Radius = 8;
        mStar2Radius = 10;
        mStar1Radius = 12;
        mRoute1Name = "Route 1";
        mRoute2Name = "Route 2";
        mRoute3Name = "Route 3";
        mRoute4Name = "Route 4";
        mRoute5Name = "Route 5";
        mRoute6Name = "Route 6";
        mRoute7Name = "Route 7";
        mRoute8Name = "Route 8";
    }
    
    public Point3D getCenter()
    {
        return mCenter;
    }
    public void setCenter(Point3D center)
    {
        queuePropertyChange("center", mCenter, center);
        mCenter = center;
        firePropertyChange();
    }
    public Point3D getRotation()
    {
        return mRotation;
    }
    public void setRotation(Point3D rotation)
    {
        mRotation = rotation;
    }
    public double getRadius()
    {
        return mRadius;
    }
    public void setRadius(double radius)
    {
        queuePropertyChange("radius", mRadius, radius);
        mRadius = radius;
        firePropertyChange();
    }
    public List<StarBean> getStars()
    {
        return mStars;
    }
    public void setStars(List<StarBean> stars)
    {
        mStars = stars;
    }
    public List<StarLink> getLinks()
    {
        return mLinks;
    }
    public void setLinks(List<StarLink> links)
    {
        mLinks = links;
    }
    public Map<Point3D, Point3i> getLocations()
    {
        return mLocations;
    }
    public void setLocations(Map<Point3D, Point3i> locations)
    {
        mLocations = locations;
    }
    public StarBean getFocus()
    {
        return mFocus;
    }
    public void setFocus(StarBean focus)
    {
        if (mFocus == focus)
            return;
        queuePropertyChange("focus", mFocus, focus);
        mFocus = focus;
        firePropertyChange();
    }

    public double getScale()
    {
        return mScale;
    }

    public void setScale(double scale)
    {
        mScale = scale;
    }

    public boolean isShowLinks()
    {
        return mShowLinks;
    }

    public void setShowLinks(boolean showLinks)
    {
        mShowLinks = showLinks;
    }

    public boolean isShowScope()
    {
        return mShowScope;
    }

    public void setShowScope(boolean showScope)
    {
        mShowScope = showScope;
    }

    public boolean isShowGrid()
    {
        return mShowGrid;
    }

    public void setShowGrid(boolean showGrid)
    {
        mShowGrid = showGrid;
    }

    public boolean isShowLinkNumbers()
    {
        return mShowLinkNumbers;
    }

    public void setShowLinkNumbers(boolean showLinkNumbers)
    {
        mShowLinkNumbers = showLinkNumbers;
    }

    public Set<StarBean> getSelected()
    {
        return mSelected;
    }

    public void setSelected(Set<StarBean> selected)
    {
        mSelected = selected;
    }

    public Set<StarBean> getHidden()
    {
        return mHidden;
    }

    public void setHidden(Set<StarBean> hidden)
    {
        mHidden = hidden;
    }

    public boolean isShowNames()
    {
        return mShowNames;
    }

    public void setShowNames(boolean showNames)
    {
        mShowNames = showNames;
    }

    public StarFilter getFilter()
    {
        return mFilter;
    }

    public void setFilter(StarFilter filter)
    {
        mFilter = filter;
    }

    public boolean isShowRoutes()
    {
        return mShowRoutes;
    }

    public void setShowRoutes(boolean showRoutes)
    {
        mShowRoutes = showRoutes;
    }

    public List<StarRouteBean> getRoutes()
    {
        return mRoutes;
    }

    public void setRoutes(List<StarRouteBean> routes)
    {
        mRoutes = routes;
    }

    public List<SkyBean> getSky()
    {
        return mSky;
    }

    public void setSky(List<SkyBean> sky)
    {
        mSky = sky;
    }

    public boolean isShowSky()
    {
        return mShowSky;
    }

    public void setShowSky(boolean showSky)
    {
        mShowSky = showSky;
    }

    public String getRoute1Name()
    {
        return mRoute1Name;
    }

    public void setRoute1Name(String route1Name)
    {
        queuePropertyChange("route1Name", mRoute1Name, route1Name);
        mRoute1Name = route1Name;
        firePropertyChange();
    }

    public String getRoute2Name()
    {
        return mRoute2Name;
    }

    public void setRoute2Name(String route2Name)
    {
        queuePropertyChange("route2Name", mRoute2Name, route2Name);
        mRoute2Name = route2Name;
        firePropertyChange();
    }

    public String getRoute3Name()
    {
        return mRoute3Name;
    }

    public void setRoute3Name(String route3Name)
    {
        queuePropertyChange("route3Name", mRoute3Name, route3Name);
        mRoute3Name = route3Name;
        firePropertyChange();
    }

    public String getRoute4Name()
    {
        return mRoute4Name;
    }

    public void setRoute4Name(String route4Name)
    {
        queuePropertyChange("route4Name", mRoute4Name, route4Name);
        mRoute4Name = route4Name;
        firePropertyChange();
    }

    public String getRoute5Name()
    {
        return mRoute5Name;
    }

    public void setRoute5Name(String route5Name)
    {
        queuePropertyChange("route5Name", mRoute5Name, route5Name);
        mRoute5Name = route5Name;
        firePropertyChange();
    }

    public String getRoute6Name()
    {
        return mRoute6Name;
    }

    public void setRoute6Name(String route6Name)
    {
        queuePropertyChange("route6Name", mRoute6Name, route6Name);
        mRoute6Name = route6Name;
        firePropertyChange();
    }

    public String getRoute7Name()
    {
        return mRoute7Name;
    }

    public void setRoute7Name(String route7Name)
    {
        queuePropertyChange("route7Name", mRoute7Name, route7Name);
        mRoute7Name = route7Name;
        firePropertyChange();
    }

    public String getRoute8Name()
    {
        return mRoute8Name;
    }

    public void setRoute8Name(String route8Name)
    {
        queuePropertyChange("route8Name", mRoute8Name, route8Name);
        mRoute8Name = route8Name;
        firePropertyChange();
    }

    public int getStar0Radius()
    {
        return mStar0Radius;
    }

    public void setStar0Radius(int star0Radius)
    {
        queuePropertyChange("star0Radius", mStar0Radius, star0Radius);
        mStar0Radius = star0Radius;
        firePropertyChange();
    }

    public int getStar1Radius()
    {
        return mStar1Radius;
    }

    public void setStar1Radius(int star1Radius)
    {
        queuePropertyChange("star1Radius", mStar1Radius, star1Radius);
        mStar1Radius = star1Radius;
        firePropertyChange();
    }

    public int getStar2Radius()
    {
        return mStar2Radius;
    }

    public void setStar2Radius(int star2Radius)
    {
        queuePropertyChange("star2Radius", mStar2Radius, star2Radius);
        mStar2Radius = star2Radius;
        firePropertyChange();
    }

    public int getStar3Radius()
    {
        return mStar3Radius;
    }

    public void setStar3Radius(int star3Radius)
    {
        queuePropertyChange("star3Radius", mStar3Radius, star3Radius);
        mStar3Radius = star3Radius;
        firePropertyChange();
    }

    public int getStar4Radius()
    {
        return mStar4Radius;
    }

    public void setStar4Radius(int star4Radius)
    {
        queuePropertyChange("star4Radius", mStar4Radius, star4Radius);
        mStar4Radius = star4Radius;
        firePropertyChange();
    }

    public int getStar5Radius()
    {
        return mStar5Radius;
    }

    public void setStar5Radius(int star5Radius)
    {
        queuePropertyChange("star5Radius", mStar5Radius, star5Radius);
        mStar5Radius = star5Radius;
        firePropertyChange();
    }

    public String getStarNameColumn()
    {
        return mStarNameColumn;
    }

    public void setStarNameColumn(String starNameColumn)
    {
        queuePropertyChange("starNameColumn", mStarNameColumn, starNameColumn);
        mStarNameColumn = starNameColumn;
        firePropertyChange();
    }

    public double getLinkDist1()
    {
        return mLinkDist1;
    }

    public void setLinkDist1(double linkDist1)
    {
        queuePropertyChange("linkDist1", mLinkDist1, linkDist1);
        mLinkDist1 = linkDist1;
        firePropertyChange();
    }

    public double getLinkDist2()
    {
        return mLinkDist2;
    }

    public void setLinkDist2(double linkDist2)
    {
        queuePropertyChange("linkDist2", mLinkDist2, linkDist2);
        mLinkDist2 = linkDist2;
        firePropertyChange();
   }

    public double getLinkDist3()
    {
        return mLinkDist3;
    }

    public void setLinkDist3(double linkDist3)
    {
        queuePropertyChange("linkDist3", mLinkDist3, linkDist3);
        mLinkDist3 = linkDist3;
        firePropertyChange();
    }

    public double getLinkDist4()
    {
        return mLinkDist4;
    }

    public void setLinkDist4(double linkDist4)
    {
        queuePropertyChange("linkDist4", mLinkDist4, linkDist4);
        mLinkDist4 = linkDist4;
        firePropertyChange();
    }
}
