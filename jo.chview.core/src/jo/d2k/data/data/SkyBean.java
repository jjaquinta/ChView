package jo.d2k.data.data;

import jo.util.beans.Bean;

public class SkyBean extends Bean
{
    private StarBean    mStar;
    private double      mRA;
    private double      mDec;
    private double      mApparentMagnitude;
    private double      mBrightness;
    private double      mDistance;
    
    public StarBean getStar()
    {
        return mStar;
    }
    public void setStar(StarBean star)
    {
        mStar = star;
    }
    public double getRA()
    {
        return mRA;
    }
    public void setRA(double rA)
    {
        mRA = rA;
    }
    public double getDec()
    {
        return mDec;
    }
    public void setDec(double dec)
    {
        mDec = dec;
    }
    public double getApparentMagnitude()
    {
        return mApparentMagnitude;
    }
    public void setApparentMagnitude(double apparentMagnitude)
    {
        mApparentMagnitude = apparentMagnitude;
    }
    public double getBrightness()
    {
        return mBrightness;
    }
    public void setBrightness(double brightness)
    {
        mBrightness = brightness;
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
