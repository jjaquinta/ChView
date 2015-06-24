package jo.d2k.data.ship;

import jo.d2k.data.logic.ship.LocationLogic;
import jo.d2k.data.logic.stargen.data.BodyBean;

public class Location
{
    public static final String INTERPLANETARY = "I";
    public static final String LOW_ORBIT = "OL";
    public static final String MEDIUM_ORBIT = "OM";
    public static final String HIGH_ORBIT = "OH";
    public static final String LAGRANGE_POINT = "L";
    public static final String L1 = LAGRANGE_POINT+"1";
    public static final String L2 = LAGRANGE_POINT+"2";
    public static final String L3 = LAGRANGE_POINT+"3";
    public static final String L4 = LAGRANGE_POINT+"4";
    public static final String L5 = LAGRANGE_POINT+"5";
    
    private BodyBean    mBody;
    private double      mX;
    private double      mY;
    private double      mZ;
    private String      mType;
    
    @Override
    public String toString()
    {
        return LocationLogic.toURI(this);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Location))
            return super.equals(o);
        Location l2 = (Location)o;
        if (!mBody.equals(l2.getBody()))
            return false;
        if (!mType.equals(l2.getType()))
            return false;
        if (mX != l2.getX())
            return false;
        if (mY != l2.getY())
            return false;
        if (mZ != l2.getZ())
            return false;
        return true;
    }
    
    public BodyBean getBody()
    {
        return mBody;
    }
    public void setBody(BodyBean body)
    {
        mBody = body;
    }
    public double getX()
    {
        return mX;
    }
    public void setX(double x)
    {
        mX = x;
    }
    public double getY()
    {
        return mY;
    }
    public void setY(double y)
    {
        mY = y;
    }
    public double getZ()
    {
        return mZ;
    }
    public void setZ(double z)
    {
        mZ = z;
    }
    public String getType()
    {
        return mType;
    }
    public void setType(String type)
    {
        mType = type;
    }
}
