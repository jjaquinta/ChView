package jo.d2k.data.data;

import jo.util.beans.Bean;

public class SkyLinkBean extends Bean
{
    private SkyBean mStar1;
    private SkyBean mStar2;
    private double  mSeparation;
    private double  mStrength;
    
    public SkyBean getStar1()
    {
        return mStar1;
    }
    public void setStar1(SkyBean star1)
    {
        mStar1 = star1;
    }
    public SkyBean getStar2()
    {
        return mStar2;
    }
    public void setStar2(SkyBean star2)
    {
        mStar2 = star2;
    }
    public double getSeparation()
    {
        return mSeparation;
    }
    public void setSeparation(double separation)
    {
        mSeparation = separation;
    }
    public double getStrength()
    {
        return mStrength;
    }
    public void setStrength(double strength)
    {
        mStrength = strength;
    }
}
