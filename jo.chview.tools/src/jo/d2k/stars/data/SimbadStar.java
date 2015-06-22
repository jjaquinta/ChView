package jo.d2k.stars.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jo.util.beans.Bean;

public class SimbadStar extends Bean
{
    private int    mStarID;
    private String mName;
    private String mNotes;
    private double mRA;
    private double mDec;
    private double mParalax;
    private double mDistance;
    private double mMag;
    private double mAbsMag;
    private String mSpectrum;
    private double mX;
    private double mY;
    private double mZ;
    private String mD2KName;
    private String mD2KQuadrant;
    private String mD2KSpectra;
    private double mD2KX;
    private double mD2KY;
    private double mD2KZ;
    private SimbadStar mSecondary;
    private Set<String> mIdents;
    private String mWikipediaURL;
    private String mSimbadURL;
    private Map<String,String> mCatalogNames;
    
    public SimbadStar()
    {
        mIdents = new HashSet<String>();
        mCatalogNames = new HashMap<String, String>();
    }
    
    @Override
    public String toString()
    {
        return mName;
    }
    
    public int getStarID()
    {
        return mStarID;
    }
    public void setStarID(int starID)
    {
        mStarID = starID;
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
    public double getDistance()
    {
        return mDistance;
    }
    public void setDistance(double distance)
    {
        mDistance = distance;
    }
    public double getAppMag()
    {
        return mMag;
    }
    public void setAppMag(double mag)
    {
        mMag = mag;
    }
    public double getAbsMag()
    {
        return mAbsMag;
    }
    public void setAbsMag(double absMag)
    {
        mAbsMag = absMag;
    }
    public String getSpectrum()
    {
        return mSpectrum;
    }
    public void setSpectrum(String spectrum)
    {
        mSpectrum = spectrum;
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
    public String getD2KName()
    {
        return mD2KName;
    }
    public void setD2KName(String d2kName)
    {
        mD2KName = d2kName;
    }
    public String getD2KQuadrant()
    {
        return mD2KQuadrant;
    }
    public void setD2KQuadrant(String d2kQuadrant)
    {
        mD2KQuadrant = d2kQuadrant;
    }
    public String getD2KSpectra()
    {
        return mD2KSpectra;
    }
    public void setD2KSpectra(String d2kSpectra)
    {
        mD2KSpectra = d2kSpectra;
    }
    public double getD2KX()
    {
        return mD2KX;
    }
    public void setD2KX(double d2kx)
    {
        mD2KX = d2kx;
    }
    public double getD2KY()
    {
        return mD2KY;
    }
    public void setD2KY(double d2ky)
    {
        mD2KY = d2ky;
    }
    public double getD2KZ()
    {
        return mD2KZ;
    }
    public void setD2KZ(double d2kz)
    {
        mD2KZ = d2kz;
    }
    public SimbadStar getSecondary()
    {
        return mSecondary;
    }
    public void setSecondary(SimbadStar secondary)
    {
        mSecondary = secondary;
    }
    public double getParalax()
    {
        return mParalax;
    }
    public void setParalax(double paralax)
    {
        mParalax = paralax;
    }
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }

    public Set<String> getIdents()
    {
        return mIdents;
    }

    public void setIdents(Set<String> idents)
    {
        mIdents = idents;
    }

    public String getNotes()
    {
        return mNotes;
    }

    public void setNotes(String notes)
    {
        mNotes = notes;
    }

    public String getWikipediaURL()
    {
        return mWikipediaURL;
    }

    public void setWikipediaURL(String wikipediaURL)
    {
        mWikipediaURL = wikipediaURL;
    }

    public String getSimbadURL()
    {
        return mSimbadURL;
    }

    public void setSimbadURL(String simbadURL)
    {
        mSimbadURL = simbadURL;
    }

    public Map<String, String> getCatalogNames()
    {
        return mCatalogNames;
    }

    public void setCatalogNames(Map<String, String> catalogNames)
    {
        mCatalogNames = catalogNames;
    }

}
