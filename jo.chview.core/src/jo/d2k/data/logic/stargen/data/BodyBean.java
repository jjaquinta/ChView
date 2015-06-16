package jo.d2k.data.logic.stargen.data;

import java.util.ArrayList;
import java.util.List;

import jo.d2k.data.data.URIBean;
import jo.util.utils.obj.StringUtils;

public abstract class BodyBean implements URIBean
{
    private long             mOID;
    private String           mURI;
    private String           mName;
    private double           mMass;      // mass (in solar masses)
    private double           mRadius;    // equatorial radius (in km)
    private double           mA;         // semi-major axis of solar orbit
                                         // (in AU)
    private double           mE;         // eccentricity of solar orbit
    private double           mAxialTilt; // units of degrees
    protected BodyBean       mParent;
    protected List<BodyBean> mChildren;

    public BodyBean()
    {
        mChildren = new ArrayList<BodyBean>();
    }

    @Override
    public String toString()
    {
        return mName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof BodyBean))
            return super.equals(o);
        BodyBean b2 = (BodyBean)o;
        if (mOID != b2.getOID())
            return false;
        return StringUtils.compareTo(mName, b2.getName()) == 0;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        this.mName = name;
    }

    public double getMass()
    {
        return mMass;
    }

    public void setMass(double mass)
    {
        this.mMass = mass;
    }

    public double getRadius()
    {
        return mRadius;
    }

    public void setRadius(double radius)
    {
        mRadius = radius;
    }

    public BodyBean getParent()
    {
        return mParent;
    }

    public void setParent(BodyBean parent)
    {
        mParent = parent;
    }

    public BodyBean getNextBody()
    {
        if (mParent == null)
            return null;
        int idx = mParent.getChildren().indexOf(this);
        if (idx < 0)
            throw new IllegalStateException(
                    "We are not the child of our parent!");
        if (idx + 1 < mParent.getChildren().size())
            return mParent.getChildren().get(idx + 1);
        return null;
    }

    public BodyBean getFirstChild()
    {
        if (mChildren.size() > 0)
            return mChildren.get(0);
        else
            return null;
    }

    public long getOID()
    {
        return mOID;
    }

    public void setOID(long oID)
    {
        mOID = oID;
    }

    public String getURI()
    {
        return mURI;
    }

    public void setURI(String uRI)
    {
        mURI = uRI;
    }

    public List<BodyBean> getChildren()
    {
        return mChildren;
    }

    public void setChildren(List<BodyBean> children)
    {
        mChildren = children;
    }

    public double getA()
    {
        return mA;
    }

    public void setA(double a)
    {
        mA = a;
    }

    public double getE()
    {
        return mE;
    }

    public void setE(double e)
    {
        mE = e;
    }

    public double getAxialTilt()
    {
        return mAxialTilt;
    }

    public void setAxialTilt(double axialTilt)
    {
        mAxialTilt = axialTilt;
    }
}
