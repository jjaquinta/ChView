package jo.d2k.data.ship;

import jo.util.beans.Bean;


public class ShipBean extends Bean
{
    private long    mUserOID;
    private String  mName;
    private int mColdModuleCapacity;
    private int mWarmModuleCapacity;
    private String  mLocation;
    private String  mHeading;
    private long    mNextMove;

    public int getColdModuleCapacity()
    {
        return mColdModuleCapacity;
    }
    public void setColdModuleCapacity(int coldModuleCapacity)
    {
        mColdModuleCapacity = coldModuleCapacity;
    }
    public int getWarmModuleCapacity()
    {
        return mWarmModuleCapacity;
    }
    public void setWarmModuleCapacity(int warmModuleCapacity)
    {
        mWarmModuleCapacity = warmModuleCapacity;
    }
    public long getUserOID()
    {
        return mUserOID;
    }
    public void setUserOID(long userOID)
    {
        mUserOID = userOID;
    }
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public String getLocation()
    {
        return mLocation;
    }
    public void setLocation(String location)
    {
        mLocation = location;
    }
    public String getHeading()
    {
        return mHeading;
    }
    public void setHeading(String heading)
    {
        mHeading = heading;
    }
    public long getNextMove()
    {
        return mNextMove;
    }
    public void setNextMove(long nextMove)
    {
        mNextMove = nextMove;
    }
}
