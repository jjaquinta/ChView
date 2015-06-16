package jo.d2k.data.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jo.util.beans.Bean;
import jo.util.intro.PseudoProp;

public class StarBean extends Bean implements URIBean
{
    private String  mName;
    private String  mCommonName;
    private String  mHIPName;
    private String  mGJName;
    private String  mHDName;
    private String  mHRName;
    private String  mSAOName;
    private String  mTwoMassName;
    private String  mQuadrant;
    private double  mX;
    private double  mY;
    private double  mZ;
    private String  mSpectra;
    private double  mAbsMag;
    private long    mParent;
    private String  mSimbadURL;
    private String  mWikipediaURL;
    private StarBean    mParentRef;
    private boolean mGenerated;
    private Map<String,String>  mMetadata;
    private List<StarBean> mChildren;
    
    public StarBean()
    {
        mChildren = new ArrayList<StarBean>();
    }
    
    @Override
    public String toString()
    {
        return mQuadrant + " - " + mName;
    }
    
    @Override
    public int hashCode()
    {
        return mName.hashCode() + mQuadrant.hashCode();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof StarBean))
            return super.equals(obj);
        StarBean s2 = (StarBean)obj;
        if (getOID() != s2.getOID())
            return false;
        return mQuadrant.equals(s2.getQuadrant());
    }
    
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public String getQuadrant()
    {
        return mQuadrant;
    }
    public void setQuadrant(String quadrant)
    {
        mQuadrant = quadrant;
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
    public String getSpectra()
    {
        return mSpectra;
    }
    public void setSpectra(String spectra)
    {
        mSpectra = spectra;
    }
    public double getAbsMag()
    {
        return mAbsMag;
    }
    public void setAbsMag(double absMag)
    {
        mAbsMag = absMag;
    }
    public long getParent()
    {
        return mParent;
    }
    public void setParent(long parent)
    {
        mParent = parent;
    }

    public String getSimbadURL()
    {
        return mSimbadURL;
    }

    public void setSimbadURL(String simbadURL)
    {
        mSimbadURL = simbadURL;
    }

    public String getWikipediaURL()
    {
        return mWikipediaURL;
    }

    public void setWikipediaURL(String wikipediaURL)
    {
        mWikipediaURL = wikipediaURL;
    }

    @PseudoProp
    public StarBean getParentRef()
    {
        return mParentRef;
    }

    @PseudoProp
    public void setParentRef(StarBean parentRef)
    {
        mParentRef = parentRef;
    }

    @Override
    public String getURI()
    {
        StringBuffer uri = new StringBuffer("star://");
        uri.append(Long.toHexString(getOID()));
        uri.append("@");
        uri.append(getQuadrant());
        return uri.toString();
    }

    @PseudoProp
    public boolean isGenerated()
    {
        return mGenerated;
    }

    @PseudoProp
    public void setGenerated(boolean generated)
    {
        mGenerated = generated;
    }

    public String getCommonName()
    {
        return mCommonName;
    }

    public void setCommonName(String commonName)
    {
        mCommonName = commonName;
    }

    public String getHIPName()
    {
        return mHIPName;
    }

    public void setHIPName(String hIPName)
    {
        mHIPName = hIPName;
    }

    public String getGJName()
    {
        return mGJName;
    }

    public void setGJName(String gJName)
    {
        mGJName = gJName;
    }

    public String getHDName()
    {
        return mHDName;
    }

    public void setHDName(String hDName)
    {
        mHDName = hDName;
    }

    public String getHRName()
    {
        return mHRName;
    }

    public void setHRName(String hRName)
    {
        mHRName = hRName;
    }

    public String getSAOName()
    {
        return mSAOName;
    }

    public void setSAOName(String sAOName)
    {
        mSAOName = sAOName;
    }

    public String getTwoMassName()
    {
        return mTwoMassName;
    }

    public void setTwoMassName(String twoMassName)
    {
        mTwoMassName = twoMassName;
    }

    @PseudoProp
    public Map<String, String> getMetadata()
    {
        return mMetadata;
    }

    @PseudoProp
    public void setMetadata(Map<String, String> metadata)
    {
        mMetadata = metadata;
    }

    @PseudoProp
    public List<StarBean> getChildren()
    {
        return mChildren;
    }

    @PseudoProp
    public List<StarBean> getAllChildren()
    {
        List<StarBean> tree = new ArrayList<StarBean>();
        tree.add(this);
        for (StarBean c : mChildren)
            tree.addAll(c.getAllChildren());
        return tree;
    }

    @PseudoProp
    public StarBean getPrimary()
    {
        if (mParent == 0)
            return this;
        else
            return mParentRef.getPrimary();
    }
    
    @PseudoProp
    public void setChildren(List<StarBean> children)
    {
        mChildren = children;
    }

}
