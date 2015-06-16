package jo.d4w.data;

import jo.d2k.data.logic.stargen.data.BodyBean;

public class PopulatedStationBean extends PopulatedObjectBean
{
    public static final int ORBIT = 0;
    public static final int L1 = 1;
    public static final int L2 = 2;
    public static final int L3 = 3;
    public static final int L4 = 4;
    public static final int L5 = 5;
    
    private BodyBean    mBody;
    private String      mName;
    private int         mLocationType; // 0 = orbit, 1 = L1, etc
    private double      mOrbitalRadius;
    public BodyBean getBody()
    {
        return mBody;
    }
    public void setBody(BodyBean body)
    {
        mBody = body;
    }
    public int getLocationType()
    {
        return mLocationType;
    }
    public void setLocationType(int locationType)
    {
        mLocationType = locationType;
    }
    public double getOrbitalRadius()
    {
        return mOrbitalRadius;
    }
    public void setOrbitalRadius(double orbitalRadius)
    {
        mOrbitalRadius = orbitalRadius;
    }
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
}
