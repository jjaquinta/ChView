package jo.d2k.data.data;

import java.util.ArrayList;
import java.util.List;

import jo.util.beans.PCSBean;
import jo.util.geom3d.Point3D;

public class SearchParams extends PCSBean
{
    private List<StarBean>  mCache;
    private String          mPattern;
    private boolean         mFindFirst;
    private StarFilter      mFilter;
    private List<StarBean>  mResults;
    private Point3D         mCenter;
    private double          mSearchRadius;
    private boolean         mDone;
    private int             mTotalSteps;
    private int             mTakenSteps;
    
    private boolean         mCancel;
    
    public SearchParams()
    {
        mDone = true;
        mCache = new ArrayList<StarBean>();
        mPattern = "";
        mFindFirst = true;
        mFilter = new StarFilter();
        mCancel = false;
        mCenter = new Point3D();
        mSearchRadius = 25;
        mResults = new ArrayList<StarBean>();
        mTakenSteps = 0;
        mTotalSteps = 100;
    }

    public List<StarBean> getCache()
    {
        return mCache;
    }

    public void setCache(List<StarBean> cache)
    {
        queuePropertyChange("cache", mCache, cache);
        mCache = cache;
        firePropertyChange();
    }

    public String getPattern()
    {
        return mPattern;
    }

    public void setPattern(String pattern)
    {
        queuePropertyChange("pattern", mPattern, pattern);
        mPattern = pattern;
        firePropertyChange();
    }

    public boolean isFindFirst()
    {
        return mFindFirst;
    }

    public void setFindFirst(boolean findFirst)
    {
        mFindFirst = findFirst;
    }

    public List<StarBean> getResults()
    {
        return mResults;
    }

    public void setResults(List<StarBean> results)
    {
        mResults = results;
    }

    public boolean isCancel()
    {
        return mCancel;
    }

    public void setCancel(boolean cancel)
    {
        mCancel = cancel;
    }

    public Point3D getCenter()
    {
        return mCenter;
    }

    public void setCenter(Point3D center)
    {
        mCenter = center;
    }

    public double getSearchRadius()
    {
        return mSearchRadius;
    }

    public void setSearchRadius(double searchRadius)
    {
        mSearchRadius = searchRadius;
    }

    public StarFilter getFilter()
    {
        return mFilter;
    }

    public void setFilter(StarFilter filter)
    {
        mFilter = filter;
    }

    public boolean isDone()
    {
        return mDone;
    }

    public void setDone(boolean done)
    {
        mDone = done;
    }

    public int getTotalSteps()
    {
        return mTotalSteps;
    }

    public void setTotalSteps(int totalSteps)
    {
        mTotalSteps = totalSteps;
    }

    public int getTakenSteps()
    {
        return mTakenSteps;
    }

    public void setTakenSteps(int takenSteps)
    {
        mTakenSteps = takenSteps;
    }
}
