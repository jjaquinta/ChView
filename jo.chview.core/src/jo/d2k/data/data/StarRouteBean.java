package jo.d2k.data.data;

import jo.d2k.data.logic.StarExtraLogic;
import jo.util.beans.Bean;
import jo.util.intro.PseudoProp;

public class StarRouteBean extends Bean
{
    private String      mStar1Quad;
    private long        mStar1;
    private String      mStar2Quad;
    private long        mStar2;
    private StarBean    mStar1Ref;
    private StarBean    mStar2Ref;
    private int         mType;
    
    public long getStar1()
    {
        return mStar1;
    }
    public void setStar1(long star1)
    {
        mStar1 = star1;
    }
    public long getStar2()
    {
        return mStar2;
    }
    public void setStar2(long star2)
    {
        mStar2 = star2;
    }
    @PseudoProp
    public StarBean getStar1Ref()
    {
        return mStar1Ref;
    }
    @PseudoProp
    public void setStar1Ref(StarBean star1Ref)
    {
        mStar1Ref = star1Ref;
        if (mStar1Ref != null)
        {
            mStar1 = mStar1Ref.getOID();
            mStar1Quad = mStar1Ref.getQuadrant();
        }
    }
    @PseudoProp
    public StarBean getStar2Ref()
    {
        return mStar2Ref;
    }
    @PseudoProp
    public void setStar2Ref(StarBean star2Ref)
    {
        mStar2Ref = star2Ref;
        if (mStar2Ref != null)
        {
            mStar2 = mStar2Ref.getOID();
            mStar2Quad = mStar2Ref.getQuadrant();
        }
    }
    public int getType()
    {
        return mType;
    }
    public void setType(int type)
    {
        mType = type;
    }
    @PseudoProp
    public double getDistance()
    {
        return StarExtraLogic.distance(mStar1Ref, mStar2Ref);        
    }
    public String getStar1Quad()
    {
        return mStar1Quad;
    }
    public void setStar1Quad(String star1Quad)
    {
        mStar1Quad = star1Quad;
    }
    public String getStar2Quad()
    {
        return mStar2Quad;
    }
    public void setStar2Quad(String star2Quad)
    {
        mStar2Quad = star2Quad;
    }
}
