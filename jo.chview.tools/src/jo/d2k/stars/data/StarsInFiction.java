package jo.d2k.stars.data;

import java.util.ArrayList;
import java.util.List;

public class StarsInFiction
{
    private String  mPopularName;
    private String  mWikiURL;
    private List<String>    mSimbadURLs;
    
    public StarsInFiction()
    {
        mSimbadURLs = new ArrayList<String>();
    }
    
    public String getPopularName()
    {
        return mPopularName;
    }
    public void setPopularName(String popularName)
    {
        mPopularName = popularName;
    }
    public String getWikiURL()
    {
        return mWikiURL;
    }
    public void setWikiURL(String wikiURL)
    {
        mWikiURL = wikiURL;
    }

    public List<String> getSimbadURLs()
    {
        return mSimbadURLs;
    }

    public void setSimbadURLs(List<String> simbadURLs)
    {
        mSimbadURLs = simbadURLs;
    }
}
