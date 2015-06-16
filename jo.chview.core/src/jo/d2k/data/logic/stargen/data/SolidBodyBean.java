package jo.d2k.data.logic.stargen.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SolidBodyBean extends BodyBean
{
    private int        mPlanetNo;
    private boolean    mGasGiant;             // TRUE if the planet is a gas
                                              // giant
    private double     mDustMass;             // mass, ignoring gas
    private double     mGasMass;              // mass, ignoring dust
                                              // ZEROES start here
    private double     mCoreRadius;           // radius of the rocky core (in
                                              // km)
    private int        mOrbitZone;            // the 'zone' of the planet
    private double     mDensity;               // density (in g/cc)
    private double     mOrbPeriod;            // length of the local year (days)
    private double     mDay;                   // length of the local day (hours)
    private boolean    mResonantPeriod;       // TRUE if in resonant rotation
    private double     mEscVelocity;          // units of cm/sec
    private double     mSurfAccel;            // units of cm/sec2
    private double     mSurfGrav;             // units of Earth gravities
    private double     mRMSVelocity;          // units of cm/sec
    private double     mMolecWeight;          // smallest molecular weight
                                              // retained
    private double     mVolatileGasInventory;
    private double     mSurfPressure;         // units of millibars (mb)
    private boolean    mGreenhouseEffect;     // runaway greenhouse effect?
    private double     mBoilPoint;            // the boiling point of water
                                              // (Kelvin)
    private double     mAlbedo;                // albedo of the planet
    private double     mExosphericTemp;       // units of degrees Kelvin
    private double     mEstimatedTemp;        // quick non-iterative estimate
                                              // (K)
    private double     mEstimatedTerrTemp;    // for terrestrial moons and the
                                              // like
    private double     mSurfTemp;             // surface temperature in Kelvin
    private double     mGreenhsRise;          // Temperature rise due to
                                              // greenhouse
    private double     mHighTemp;             // Day-time temperature
    private double     mLowTemp;              // Night-time temperature
    private double     mMaxTemp;              // Summer/Day
    private double     mMinTemp;              // Winter/Night
    private double     mHydrosphere;           // fraction of surface covered
    private double     mCloudCover;           // fraction of surface covered
    private double     mIceCover;             // fraction of surface covered
    private double     mRockCover;             // fraction of surface covered
    private double     mHydroAlbedo;           // actual albedo of water
    private double     mCloudAlbedo;           // actual albedo of clouds
    private double     mIceAlbedo;             // actual albedo of ice
    private double     mRockAlbedo;             // actual albedo of rock
    private List<GasBean>  mAtmosphere;
    private PlanetType mType;                  // Type code
    private int        mMinorMoons;
    
    private boolean mTerraformed;
    private Properties  mProps;

    public SolidBodyBean()
    {
        setAtmosphere(new ArrayList<GasBean>());
        mProps = new Properties();
    }
    
    public SolidBodyBean(String id, int _planet_no, double _a, double _e, double _axial_tilt,
            double _mass, boolean _gas_giant, double _dust_mass,
            double _gas_mass, int _minor_moons)
    {
        this();
        setName(id);
        setPlanetNo(_planet_no);
        setA(_a);
        setE(_e);
        setAxialTilt(_axial_tilt);
        setMass(_mass);
        setGasGiant(_gas_giant);
        setDustMass(_dust_mass);
        setGasMass(_gas_mass);
        setMinorMoons(_minor_moons);
        setType(PlanetType.tUnknown);
    }

    public boolean isTidallyLocked()
    {
        return ((int)getDay() == (int)(getOrbPeriod() * 24.0));        
    }

    public int getPlanetNo()
    {
        return mPlanetNo;
    }

    public void setPlanetNo(int planetNo)
    {
        mPlanetNo = planetNo;
    }

    public boolean isGasGiant()
    {
        return mGasGiant;
    }

    public void setGasGiant(boolean gasGiant)
    {
        mGasGiant = gasGiant;
    }

    public double getDustMass()
    {
        return mDustMass;
    }

    public void setDustMass(double dustMass)
    {
        mDustMass = dustMass;
    }

    public double getGasMass()
    {
        return mGasMass;
    }

    public void setGasMass(double gasMass)
    {
        mGasMass = gasMass;
    }

    public double getCoreRadius()
    {
        return mCoreRadius;
    }

    public void setCoreRadius(double coreRadius)
    {
        mCoreRadius = coreRadius;
    }

    public int getOrbitZone()
    {
        return mOrbitZone;
    }

    public void setOrbitZone(int orbitZone)
    {
        mOrbitZone = orbitZone;
    }

    public double getDensity()
    {
        return mDensity;
    }

    public void setDensity(double density)
    {
        mDensity = density;
    }

    public double getOrbPeriod()
    {
        return mOrbPeriod;
    }

    public void setOrbPeriod(double orbPeriod)
    {
        mOrbPeriod = orbPeriod;
    }

    public double getDay()
    {
        return mDay;
    }

    public void setDay(double day)
    {
        mDay = day;
    }

    public boolean isResonantPeriod()
    {
        return mResonantPeriod;
    }

    public void setResonantPeriod(boolean resonantPeriod)
    {
        mResonantPeriod = resonantPeriod;
    }

    public double getEscVelocity()
    {
        return mEscVelocity;
    }

    public void setEscVelocity(double escVelocity)
    {
        mEscVelocity = escVelocity;
    }

    public double getSurfAccel()
    {
        return mSurfAccel;
    }

    public void setSurfAccel(double surfAccel)
    {
        mSurfAccel = surfAccel;
    }

    public double getSurfGrav()
    {
        return mSurfGrav;
    }

    public void setSurfGrav(double surfGrav)
    {
        mSurfGrav = surfGrav;
    }

    public double getRMSVelocity()
    {
        return mRMSVelocity;
    }

    public void setRMSVelocity(double rMSVelocity)
    {
        mRMSVelocity = rMSVelocity;
    }

    public double getMolecWeight()
    {
        return mMolecWeight;
    }

    public void setMolecWeight(double molecWeight)
    {
        mMolecWeight = molecWeight;
    }

    public double getVolatileGasInventory()
    {
        return mVolatileGasInventory;
    }

    public void setVolatileGasInventory(double volatileGasInventory)
    {
        mVolatileGasInventory = volatileGasInventory;
    }

    public double getSurfPressure()
    {
        return mSurfPressure;
    }

    public void setSurfPressure(double surfPressure)
    {
        mSurfPressure = surfPressure;
    }

    public boolean isGreenhouseEffect()
    {
        return mGreenhouseEffect;
    }

    public void setGreenhouseEffect(boolean greenhouseEffect)
    {
        mGreenhouseEffect = greenhouseEffect;
    }

    public double getBoilPoint()
    {
        return mBoilPoint;
    }

    public void setBoilPoint(double boilPoint)
    {
        mBoilPoint = boilPoint;
    }

    public double getAlbedo()
    {
        return mAlbedo;
    }

    public void setAlbedo(double albedo)
    {
        mAlbedo = albedo;
    }

    public double getExosphericTemp()
    {
        return mExosphericTemp;
    }

    public void setExosphericTemp(double exosphericTemp)
    {
        mExosphericTemp = exosphericTemp;
    }

    public double getEstimatedTemp()
    {
        return mEstimatedTemp;
    }

    public void setEstimatedTemp(double estimatedTemp)
    {
        mEstimatedTemp = estimatedTemp;
    }

    public double getEstimatedTerrTemp()
    {
        return mEstimatedTerrTemp;
    }

    public void setEstimatedTerrTemp(double estimatedTerrTemp)
    {
        mEstimatedTerrTemp = estimatedTerrTemp;
    }

    public double getSurfTemp()
    {
        return mSurfTemp;
    }

    public void setSurfTemp(double surfTemp)
    {
        mSurfTemp = surfTemp;
    }

    public double getGreenhsRise()
    {
        return mGreenhsRise;
    }

    public void setGreenhsRise(double greenhsRise)
    {
        mGreenhsRise = greenhsRise;
    }

    public double getHighTemp()
    {
        return mHighTemp;
    }

    public void setHighTemp(double highTemp)
    {
        mHighTemp = highTemp;
    }

    public double getLowTemp()
    {
        return mLowTemp;
    }

    public void setLowTemp(double lowTemp)
    {
        mLowTemp = lowTemp;
    }

    public double getMaxTemp()
    {
        return mMaxTemp;
    }

    public void setMaxTemp(double maxTemp)
    {
        mMaxTemp = maxTemp;
    }

    public double getMinTemp()
    {
        return mMinTemp;
    }

    public void setMinTemp(double minTemp)
    {
        mMinTemp = minTemp;
    }

    public double getHydrosphere()
    {
        return mHydrosphere;
    }

    public void setHydrosphere(double hydrosphere)
    {
        mHydrosphere = hydrosphere;
    }

    public double getCloudCover()
    {
        return mCloudCover;
    }

    public void setCloudCover(double cloudCover)
    {
        mCloudCover = cloudCover;
    }

    public double getIceCover()
    {
        return mIceCover;
    }

    public void setIceCover(double iceCover)
    {
        mIceCover = iceCover;
    }

    public SunBean getSun()
    {
        for (BodyBean b = getParent(); b != null; b = b.getParent())
            if (b instanceof SunBean)
                return (SunBean)b;
        return null;
    }

    public List<GasBean> getAtmosphere()
    {
        return mAtmosphere;
    }

    public void setAtmosphere(List<GasBean> atmosphere)
    {
        mAtmosphere = atmosphere;
    }

    public PlanetType getType()
    {
        return mType;
    }

    public void setType(PlanetType type)
    {
        mType = type;
    }

    public int getMinorMoons()
    {
        return mMinorMoons;
    }

    public void setMinorMoons(int minorMoons)
    {
        mMinorMoons = minorMoons;
    }

    public boolean isTerraformed()
    {
        return mTerraformed;
    }

    public void setTerraformed(boolean terraformed)
    {
        mTerraformed = terraformed;
    }

    public Properties getProps()
    {
        return mProps;
    }

    public void setProps(Properties props)
    {
        mProps = props;
    }

    public double getRockCover()
    {
        return mRockCover;
    }

    public void setRockCover(double rockCover)
    {
        mRockCover = rockCover;
    }

    public double getHydroAlbedo()
    {
        return mHydroAlbedo;
    }

    public void setHydroAlbedo(double hydroAlbedo)
    {
        mHydroAlbedo = hydroAlbedo;
    }

    public double getCloudAlbedo()
    {
        return mCloudAlbedo;
    }

    public void setCloudAlbedo(double cloudAlbedo)
    {
        mCloudAlbedo = cloudAlbedo;
    }

    public double getIceAlbedo()
    {
        return mIceAlbedo;
    }

    public void setIceAlbedo(double iceAlbedo)
    {
        mIceAlbedo = iceAlbedo;
    }

    public double getRockAlbedo()
    {
        return mRockAlbedo;
    }

    public void setRockAlbedo(double rockAlbedo)
    {
        mRockAlbedo = rockAlbedo;
    }
}
