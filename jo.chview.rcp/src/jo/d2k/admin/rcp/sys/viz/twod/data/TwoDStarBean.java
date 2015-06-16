package jo.d2k.admin.rcp.sys.viz.twod.data;

import jo.d2k.data.data.StarBean;

public class TwoDStarBean extends TwoDObject
{
    private StarBean    mStar;
    private String      mLabel;
    private int         mLabelLocation;
    
    public StarBean getStar()
    {
        return mStar;
    }
    public void setStar(StarBean star)
    {
        mStar = star;
    }
    public int getLabelLocation()
    {
        return mLabelLocation;
    }
    public void setLabelLocation(int labelLocation)
    {
        mLabelLocation = labelLocation;
    }
    public String getLabel()
    {
        return mLabel;
    }
    public void setLabel(String label)
    {
        mLabel = label;
    }
}
