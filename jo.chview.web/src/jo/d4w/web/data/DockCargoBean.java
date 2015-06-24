package jo.d4w.web.data;

import jo.d2k.data.data.URIBean;
import jo.util.beans.Bean;

public class DockCargoBean extends Bean implements URIBean
{
    private String  mURI;
    private String  mPort;
    private String  mName;
    private String  mDesc;
    private long    mValue;
    private long    mPurchasePrice;
    private long    mSalePrice;
    private long    mClassification;
    private int     mSize; // in myrialiters (MYs)
    private int  mAgricultural;
    private int  mMaterial;
    private int  mEnergy;

    public String getURI()
    {
        return mURI;
    }
    public void setURI(String uRI)
    {
        mURI = uRI;
    }
    public String getPort()
    {
        return mPort;
    }
    public void setPort(String port)
    {
        mPort = port;
    }
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public long getValue()
    {
        return mValue;
    }
    public void setValue(long value)
    {
        mValue = value;
    }
    public long getPurchasePrice()
    {
        return mPurchasePrice;
    }
    public void setPurchasePrice(long purchasePrice)
    {
        mPurchasePrice = purchasePrice;
    }
    public long getClassification()
    {
        return mClassification;
    }
    public void setClassification(long classification)
    {
        mClassification = classification;
    }
    public int getSize()
    {
        return mSize;
    }
    public void setSize(int size)
    {
        mSize = size;
    }
    public long getSalePrice()
    {
        return mSalePrice;
    }
    public void setSalePrice(long salePrice)
    {
        mSalePrice = salePrice;
    }
    public String getDesc()
    {
        return mDesc;
    }
    public void setDesc(String desc)
    {
        mDesc = desc;
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
