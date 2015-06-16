package jo.d2k.data.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SkyConstellationBean
{
    private List<SkyLinkBean>   mLinks;
    private Set<SkyBean>        mStars;
    
    public SkyConstellationBean()
    {
        mLinks = new ArrayList<SkyLinkBean>();
        mStars = new HashSet<SkyBean>();
    }
    
    public List<SkyLinkBean> getLinks()
    {
        return mLinks;
    }
    public void setLinks(List<SkyLinkBean> links)
    {
        mLinks = links;
    }
    public Set<SkyBean> getStars()
    {
        return mStars;
    }
    public void setStars(Set<SkyBean> stars)
    {
        mStars = stars;
    }
}
