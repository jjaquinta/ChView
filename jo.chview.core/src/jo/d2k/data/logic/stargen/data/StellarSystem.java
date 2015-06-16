package jo.d2k.data.logic.stargen.data;

import java.util.List;

public class StellarSystem
{
    private long    mRndSeed;
    private SunBean     mSun;
    private boolean mUseSeedSystem;
    private List<SolidBodyBean>  mSeedSystem;
    private int     mSysNo;
    private String  mSystemName;
    private double  mOuterPlanetLimit;
    private boolean mDoGases;
    private boolean mDoMoons;
    
    public long getRndSeed()
    {
        return mRndSeed;
    }
    public void setRndSeed(long rnd_seed)
    {
        this.mRndSeed = rnd_seed;
    }
    public SunBean getSun()
    {
        return mSun;
    }
    public void setSun(SunBean sun)
    {
        mSun = sun;
    }
    public boolean isDoMoons()
    {
        return mDoMoons;
    }
    public void setDoMoons(boolean doMoons)
    {
        mDoMoons = doMoons;
    }
    public boolean isDoGases()
    {
        return mDoGases;
    }
    public void setDoGases(boolean doGases)
    {
        mDoGases = doGases;
    }
    public double getOuterPlanetLimit()
    {
        return mOuterPlanetLimit;
    }
    public void setOuterPlanetLimit(double outerPlanetLimit)
    {
        mOuterPlanetLimit = outerPlanetLimit;
    }
    public String getSystemName()
    {
        return mSystemName;
    }
    public void setSystemName(String systemName)
    {
        mSystemName = systemName;
    }
    public int getSysNo()
    {
        return mSysNo;
    }
    public void setSysNo(int sysNo)
    {
        mSysNo = sysNo;
    }
    public List<SolidBodyBean> getSeedSystem()
    {
        return mSeedSystem;
    }
    public void setSeedSystem(List<SolidBodyBean> seedSystem)
    {
        mSeedSystem = seedSystem;
    }
    public boolean isUseSeedSystem()
    {
        return mUseSeedSystem;
    }
    public void setUseSeedSystem(boolean useSeedSystem)
    {
        mUseSeedSystem = useSeedSystem;
    }
}
