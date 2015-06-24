package jo.d2k.data.data;

import jo.util.beans.Bean;

/**
*
* @author jo_grant
*/
public class LifeScenarioBean extends Bean
{
   private String  mID;
   private String  mLockedBy;
   private boolean mActive;
   private long     mLastChange;
   private int     mGenerations;
   private int     mEdgeSize;
   private String  mSurfaceTexture;
   private double  mCloudRotateX;
   private double  mCloudRotateY;
   private double  mCloudRotateZ;
   private String  mCloudTexture;
   private String   mTitle;
   private String   mDescription;
   private String   mGameInfo;
   private String   mLifeMod;   // size EdgeSize*EdgeSide*12, each char ABC = +1, +2, +3, abc = -1, -2, -3 
   private String   mDriftMod;   // size EdgeSize*EdgeSide*12, each char 0 = none, 1 = N, 2 = NE, 3 = E, ... 8 = NW 
   private String   mInitialPattern;   // size EdgeSize*EdgeSide*12, <space> = none, + = item 
   private String   mStarQuad;
   private String   mStarID;
   private String   mPlanetID;
   
   public LifeScenarioBean()
   {
       mGenerations = 100;
       mEdgeSize = 8;
       mSurfaceTexture = "";
       mCloudTexture = "";
       mTitle = "";
       mID = "";
       mLockedBy = "";
       mDescription = "";
       mGameInfo = "";
       mLifeMod = "";
       mDriftMod = "";
       mInitialPattern = "";
       mActive = false;
       mLastChange = System.currentTimeMillis();
       mStarQuad = "";
       mStarID = "";
       mPlanetID = "";
   }

public String getID()
{
    return mID;
}

public void setID(String iD)
{
    mID = iD;
}

public int getGenerations()
{
    return mGenerations;
}

public void setGenerations(int generations)
{
    mGenerations = generations;
}

public int getEdgeSize()
{
    return mEdgeSize;
}

public void setEdgeSize(int edgeSize)
{
    mEdgeSize = edgeSize;
}

public String getSurfaceTexture()
{
    return mSurfaceTexture;
}

public void setSurfaceTexture(String surfaceTexture)
{
    mSurfaceTexture = surfaceTexture;
}

public double getCloudRotateX()
{
    return mCloudRotateX;
}

public void setCloudRotateX(double cloudRotateX)
{
    mCloudRotateX = cloudRotateX;
}

public double getCloudRotateY()
{
    return mCloudRotateY;
}

public void setCloudRotateY(double cloudRotateY)
{
    mCloudRotateY = cloudRotateY;
}

public double getCloudRotateZ()
{
    return mCloudRotateZ;
}

public void setCloudRotateZ(double cloudRotateZ)
{
    mCloudRotateZ = cloudRotateZ;
}

public String getCloudTexture()
{
    return mCloudTexture;
}

public void setCloudTexture(String cloudTexture)
{
    mCloudTexture = cloudTexture;
}

public String getTitle()
{
    return mTitle;
}

public void setTitle(String title)
{
    mTitle = title;
}

public String getDescription()
{
    return mDescription;
}

public void setDescription(String description)
{
    mDescription = description;
}

public String getGameInfo()
{
    return mGameInfo;
}

public void setGameInfo(String gameInfo)
{
    mGameInfo = gameInfo;
}

public String getLifeMod()
{
    return mLifeMod;
}

public void setLifeMod(String lifeMod)
{
    mLifeMod = lifeMod;
}

public String getDriftMod()
{
    return mDriftMod;
}

public void setDriftMod(String driftMod)
{
    mDriftMod = driftMod;
}

public boolean isActive()
{
    return mActive;
}

public void setActive(boolean active)
{
    mActive = active;
}

public long getLastChange()
{
    return mLastChange;
}

public void setLastChange(long lastChange)
{
    mLastChange = lastChange;
}

public String getStarQuad()
{
    return mStarQuad;
}

public void setStarQuad(String starQuad)
{
    mStarQuad = starQuad;
}

public String getStarID()
{
    return mStarID;
}

public void setStarID(String starID)
{
    mStarID = starID;
}

public String getPlanetID()
{
    return mPlanetID;
}

public void setPlanetID(String planetID)
{
    mPlanetID = planetID;
}

public String getLockedBy()
{
    return mLockedBy;
}

public void setLockedBy(String lockedBy)
{
    mLockedBy = lockedBy;
}

public String getInitialPattern()
{
    return mInitialPattern;
}

public void setInitialPattern(String initialPattern)
{
    mInitialPattern = initialPattern;
}
}
