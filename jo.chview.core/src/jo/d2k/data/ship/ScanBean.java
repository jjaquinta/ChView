package jo.d2k.data.ship;

import jo.util.beans.Bean;

public class ScanBean extends Bean
{
    public static final int RADAR = 1;
    public static final int SPECTRAL = 2;
    public static final int ALTIMETER = 3;
    
    private long    mSystemOID;
    private long    mBodyOID;
    private long    mUser;
    private long    mTime;
    private int     mScanType;
    private int     mScanTier;
    
    public long getSystemOID()
    {
        return mSystemOID;
    }
    public void setSystemOID(long systemOID)
    {
        mSystemOID = systemOID;
    }
    public long getBodyOID()
    {
        return mBodyOID;
    }
    public void setBodyOID(long bodyOID)
    {
        mBodyOID = bodyOID;
    }
    public long getUser()
    {
        return mUser;
    }
    public void setUser(long user)
    {
        mUser = user;
    }
    public long getTime()
    {
        return mTime;
    }
    public void setTime(long time)
    {
        mTime = time;
    }
    public int getScanType()
    {
        return mScanType;
    }
    public void setScanType(int scanType)
    {
        mScanType = scanType;
    }
    public int getScanTier()
    {
        return mScanTier;
    }
    public void setScanTier(int scanTier)
    {
        mScanTier = scanTier;
    }
}
