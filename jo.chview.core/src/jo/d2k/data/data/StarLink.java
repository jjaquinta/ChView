package jo.d2k.data.data;

public class StarLink
{
    private StarBean    mStar1;
    private StarBean    mStar2;
    private double      mDistance;
    public StarBean getStar1()
    {
        return mStar1;
    }
    public void setStar1(StarBean star1)
    {
        mStar1 = star1;
    }
    public StarBean getStar2()
    {
        return mStar2;
    }
    public void setStar2(StarBean star2)
    {
        mStar2 = star2;
    }
    public double getDistance()
    {
        return mDistance;
    }
    public void setDistance(double distance)
    {
        mDistance = distance;
    }
}
