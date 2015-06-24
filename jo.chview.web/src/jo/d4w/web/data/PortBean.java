package jo.d4w.web.data;

import jo.d2k.data.data.URIBean;
import jo.d4w.data.PopulatedObjectBean;
import jo.util.beans.Bean;

public class PortBean extends Bean implements URIBean
{
    private double  mX;
    private double  mY;
    private double  mZ;
    private String  mName;
    private String  mRGB;
    private String  mURI;
    private PopulatedObjectBean mPopStats;
    private int  mAgricultural;
    private int  mMaterial;
    private int  mEnergy;
    
    public String toString()
    {
        return mName;
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
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public PopulatedObjectBean getPopStats()
    {
        return mPopStats;
    }
    public void setPopStats(PopulatedObjectBean popStats)
    {
        mPopStats = popStats;
    }
    public String getURI()
    {
        return mURI;
    }
    public void setURI(String uRI)
    {
        mURI = uRI;
    }

    public String getRGB()
    {
        return mRGB;
    }

    public void setRGB(String rGB)
    {
        mRGB = rGB;
    }

    public int getAgricultural()
    {
        return mAgricultural;
    }

    public void setAgricultural(int agricultural)
    {
        mAgricultural = agricultural;
    }

    public int getMaterial()
    {
        return mMaterial;
    }

    public void setMaterial(int material)
    {
        mMaterial = material;
    }

    public int getEnergy()
    {
        return mEnergy;
    }

    public void setEnergy(int energy)
    {
        mEnergy = energy;
    }
}
