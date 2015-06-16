package jo.d2k.admin.rcp.sys.viz.twod.data;


public class TwoDConnectionBean extends TwoDObject
{
    private TwoDStarBean    mStar1;
    private TwoDStarBean    mStar2;
    private String          mLabel;
    private int             mLabelLocation;

    public TwoDStarBean getStar1()
    {
        return mStar1;
    }
    public void setStar1(TwoDStarBean star1)
    {
        mStar1 = star1;
    }
    public TwoDStarBean getStar2()
    {
        return mStar2;
    }
    public void setStar2(TwoDStarBean star2)
    {
        mStar2 = star2;
    }
    public String getLabel()
    {
        return mLabel;
    }
    public void setLabel(String label)
    {
        mLabel = label;
    }
    public int getLabelLocation()
    {
        return mLabelLocation;
    }
    public void setLabelLocation(int labelLocation)
    {
        mLabelLocation = labelLocation;
    }
}
