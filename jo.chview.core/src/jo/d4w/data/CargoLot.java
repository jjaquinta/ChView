package jo.d4w.data;

import jo.d2k.data.data.URIBean;
import jo.util.beans.Bean;

public class CargoLot extends Bean implements URIBean
{
    private String  mURI;
    private String  mPort;
    private long    mDateAvailable;
    private long    mDateUnAvailable;
    private long    mClassification;    // TradeGood OID
    private int     mSize; // in myrialiters (MYs)
    private double  mValueMod;
    
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
    public long getDateAvailable()
    {
        return mDateAvailable;
    }
    public void setDateAvailable(long dateAvailable)
    {
        mDateAvailable = dateAvailable;
    }
    public long getDateUnAvailable()
    {
        return mDateUnAvailable;
    }
    public void setDateUnAvailable(long dateUnAvailable)
    {
        mDateUnAvailable = dateUnAvailable;
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
    public double getValueMod()
    {
        return mValueMod;
    }
    public void setValueMod(double valueMod)
    {
        mValueMod = valueMod;
    }
    
}
