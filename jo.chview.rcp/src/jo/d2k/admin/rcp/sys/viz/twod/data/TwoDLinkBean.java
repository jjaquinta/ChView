package jo.d2k.admin.rcp.sys.viz.twod.data;

import jo.d2k.data.data.StarLink;

public class TwoDLinkBean extends TwoDConnectionBean
{
    private StarLink    mLink;
    
    public StarLink getLink()
    {
        return mLink;
    }
    public void setLink(StarLink link)
    {
        mLink = link;
    }
}
