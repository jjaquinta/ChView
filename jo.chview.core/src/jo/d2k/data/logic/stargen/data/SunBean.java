package jo.d2k.data.logic.stargen.data;

import jo.d2k.data.data.StarBean;

public class SunBean extends BodyBean
{
    private StarBean mStar;
    private StarBean mCompanion;
    private double mLuminosity;
    private double mLife;   // time on main sequence in years
    private double mAge;    // in years
    private double mRecosphere; // in AU

    // derived
    public double getMinREcosphere()
    {
        return Math.sqrt(getLuminosity() / 1.51);
    }
    public double getMaxREcosphere()
    {
        return Math.sqrt(getLuminosity() / 0.48);
    }
    
    public double getREcosphere()
    {
        return mRecosphere;
    }
    public void setREcosphere(double r_ecosphere)
    {
        this.mRecosphere = r_ecosphere;
    }
    public double getAge()
    {
        return mAge;
    }
    public void setAge(double age)
    {
        this.mAge = age;
    }
    public double getLife()
    {
        return mLife;
    }
    public void setLife(double life)
    {
        this.mLife = life;
    }
    public double getLuminosity()
    {
        return mLuminosity;
    }
    public void setLuminosity(double luminosity)
    {
        this.mLuminosity = luminosity;
    }
    public StarBean getStar()
    {
        return mStar;
    }
    public void setStar(StarBean star)
    {
        mStar = star;
    }
    public StarBean getCompanion()
    {
        return mCompanion;
    }
    public void setCompanion(StarBean companion)
    {
        mCompanion = companion;
    }
}
