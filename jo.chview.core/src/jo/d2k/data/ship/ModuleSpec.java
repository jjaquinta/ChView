package jo.d2k.data.ship;

import java.util.HashMap;
import java.util.Map;

import jo.util.beans.Bean;

public class ModuleSpec extends Bean
{
    // placement
    public static final int COLD = 0;
    public static final int WARM = 1;
    // group
    public static final String ENERGY_GENERATION = "energygeneration";
    public static final String ENERGY_STORE = "energystore";
    public static final String SPACE_DRIVE = "spacedrive";
    public static final String STAR_DRIVE = "stardrive";
    public static final String HABITATION = "habitation";
    public static final String SENSOR = "sensor";
    // lparams
    public static final int SENSOR_TYPE = 0; // group = SENSOR
    // dparams
    public static final int UNITS_PER_HOUR = 0; // group = ENERGY_GENERATION
    public static final int MAX_CAPACITY = 0; // group = ENERGY_STORE
    public static final int ENERGY_PER_HOUR = 0; // group = SENSOR
    public static final int MAX_RANGE = 1; // group = SENSOR, in AU
    public static final int SCAN_TIME = 2; // group = SENSOR, in minutes
    
    // registry
    public static Map<String,Map<String,Integer>> LONG_TAG_MAP = new HashMap<String, Map<String,Integer>>();
    public static Map<String,Map<String,Map<String,Long>>> LONG_VALUE_MAP = new HashMap<String, Map<String,Map<String,Long>>>();
    public static Map<String,Map<String,Integer>> DOUBLE_TAG_MAP = new HashMap<String, Map<String,Integer>>();
    
    static
    {
        registerDouble(ENERGY_GENERATION, "unitsPerHour", UNITS_PER_HOUR);
        registerDouble(ENERGY_STORE, "maxCapacity", MAX_CAPACITY);
        registerLong(SENSOR, "sensorType", SENSOR_TYPE);
        registerLongValue(SENSOR, "sensorType", "radar", ScanBean.RADAR);
        registerLongValue(SENSOR, "sensorType", "spectral", ScanBean.SPECTRAL);
        registerLongValue(SENSOR, "sensorType", "altimeter", ScanBean.ALTIMETER);
        registerDouble(SENSOR, "energyPerHour", ENERGY_PER_HOUR);
        registerDouble(SENSOR, "maxRange", MAX_RANGE);
        registerDouble(SENSOR, "scanTime", SCAN_TIME);
    }
    
    private int mType;
    private int mPlacement;
    private String mGroup;
    private String mName;
    private long mCost;
    private int mTier;
    
    private long[]  mLParams;
    private double[] mDParams;
    
    public ModuleSpec()
    {
        mLParams = new long[0];
        mDParams = new double[0];
    }
    
    @Override
    public String toString()
    {
        return mName+" ("+mType+")";
    }
    
    public int getType()
    {
        return mType;
    }
    public void setType(int type)
    {
        mType = type;
    }
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public long getCost()
    {
        return mCost;
    }
    public void setCost(long cost)
    {
        mCost = cost;
    }
    public String getGroup()
    {
        return mGroup;
    }
    public void setGroup(String group)
    {
        mGroup = group;
    }
    public int getTier()
    {
        return mTier;
    }
    public void setTier(int tier)
    {
        mTier = tier;
    }
    public int getPlacement()
    {
        return mPlacement;
    }
    public void setPlacement(int placement)
    {
        mPlacement = placement;
    }

    public long[] getLParams()
    {
        return mLParams;
    }

    public void setLParams(long[] lParams)
    {
        mLParams = lParams;
    }
    
    public void setLParam(int param, long value)
    {
        if (mLParams.length <= param)
        {
            long[] newParams = new long[param+1];
            System.arraycopy(mLParams, 0, newParams, 0, mLParams.length);
            mLParams = newParams;
        }
        mLParams[param] = value;
    }

    public long getLParam(int param)
    {
        if (param < mLParams.length)
            return mLParams[param];
        else
            return 0L;
    }
    
    public double[] getDParams()
    {
        return mDParams;
    }

    public void setDParams(double[] dParams)
    {
        mDParams = dParams;
    }

    public void setDParam(int param, double value)
    {
        if (mDParams.length <= param)
        {
            double[] newParams = new double[param+1];
            System.arraycopy(mDParams, 0, newParams, 0, mDParams.length);
            mDParams = newParams;
        }
        mDParams[param] = value;
    }

    public double getDParam(int param)
    {
        if (param < mDParams.length)
            return mDParams[param];
        else
            return 0.0;
    }

    private static void registerLong(String placement, String tag, int offset)
    {
        registerTag(LONG_TAG_MAP, placement, tag, offset);
    }
    
    private static void registerDouble(String placement, String tag, int offset)
    {
        registerTag(DOUBLE_TAG_MAP, placement, tag, offset);
    }
    
    private static void registerTag(Map<String,Map<String,Integer>> tagMap, String placement, String tag, int offset)
    {
        Map<String,Integer> tags = tagMap.get(placement);
        if (tags == null)
        {
            tags = new HashMap<String, Integer>();
            tagMap.put(placement, tags);
        }
        tags.put(tag, offset);
    }
    
    private static void registerLongValue(String placement, String tag, String sValue, long lValue)
    {
        Map<String,Map<String,Long>> tags = LONG_VALUE_MAP.get(placement);
        if (tags == null)
        {
            tags = new HashMap<String, Map<String,Long>>();
            LONG_VALUE_MAP.put(placement, tags);
        }
        Map<String,Long> values = tags.get(tag);
        if (values == null)
        {
            values = new HashMap<String,Long>();
            tags.put(tag, values);
        }        
        values.put(sValue, lValue);
    }
}
