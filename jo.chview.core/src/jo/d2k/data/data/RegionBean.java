package jo.d2k.data.data;

import java.util.Iterator;
import java.util.List;

import jo.util.beans.Bean;

public class RegionBean extends Bean implements URIBean, Iterable<StarBean>
{
    private String  mURI;
    private List<StarBean>  mStars;
    
    public String getURI()
    {
        return mURI;
    }
    public void setURI(String uRI)
    {
        mURI = uRI;
    }
    public List<StarBean> getStars()
    {
        return mStars;
    }
    public void setStars(List<StarBean> stars)
    {
        mStars = stars;
    }
    
    @Override
    public Iterator<StarBean> iterator()
    {
        return mStars.iterator();
    }
}
