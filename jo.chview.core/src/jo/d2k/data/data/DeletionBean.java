package jo.d2k.data.data;

import jo.util.beans.Bean;

public class DeletionBean extends Bean
{
    private String  mStarQuad;
    private long    mStarOID;
    private String  mName;
    private double  mX;
    private double  mY;
    private double  mZ;
    
    public String getStarQuad()
    {
        return mStarQuad;
    }
    public void setStarQuad(String starQuad)
    {
        mStarQuad = starQuad;
    }
    public long getStarOID()
    {
        return mStarOID;
    }
    public void setStarOID(long starOID)
    {
        mStarOID = starOID;
    }
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
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
}
