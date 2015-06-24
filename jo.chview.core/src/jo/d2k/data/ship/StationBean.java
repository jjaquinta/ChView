package jo.d2k.data.ship;

import jo.util.beans.Bean;


public class StationBean extends Bean
{
    private String  mName;
    private int     mTier;
    private long    mSystemOID;
    private String  mLocation;
    
    @Override
    public String toString()
    {
        return mName;
    }
    
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public int getTier()
    {
        return mTier;
    }
    public void setTier(int tier)
    {
        mTier = tier;
    }

    public String getLocation()
    {
        return mLocation;
    }

    public void setLocation(String location)
    {
        mLocation = location;
    }

    public long getSystemOID()
    {
        return mSystemOID;
    }

    public void setSystemOID(long systemOID)
    {
        mSystemOID = systemOID;
    }
}
