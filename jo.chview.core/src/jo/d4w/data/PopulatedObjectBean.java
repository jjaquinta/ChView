package jo.d4w.data;

import jo.d2k.data.data.URIBean;
import jo.d4w.logic.D4WPopulationLogic;
import jo.util.beans.Bean;

public class PopulatedObjectBean extends Bean implements URIBean
{
    private String  mURI;
    private long  mPopulation;
    private double  mProductivity;

    public static final int TECH_STONE = 0;
    public static final int TECH_IRON = 1;
    public static final int TECH_STEEL = 2;
    public static final int TECH_COMPUTER = 3;
    public static final int TECH_SPACE = 4;
    public static final int TECH_STAR1 = 5;
    public static final int TECH_STAR2 = 6;
    public static final int TECH_STAR3 = 7;
    
    public static final String[] TECH_DESCRIPTION = {
        "Stone Age",
        "Iron Age",
        "Steel Age",
        "Computer Age",
        "Space Age",
        "Interstellar 1",
        "Interstellar 2",
        "Interstellar 3",
        "Interstellar 4",
        "Interstellar 5",
        "Interstellar 6",
        "Interstellar 7",
        "Interstellar 8",
    };
    
    private int     mTechTier;
    /*
     * -5 = Production not possible
     * -2 = Minimal production, most imported
     * -1 = Some production, some imports needed
     * 0 = Sufficient production to meet needs
     * 1 = Overproduction, goods exported
     */
    private double  mAgriculturalProduction;
    /*
     * -2 = No production, all imported
     * -1 = Some production, some imports needed
     * 0 = Sufficient production to meet needs
     * 1 = Overproduction, goods exported
     */
    private double  mMaterialProduction;
    /*
     * -2 = No production, all imported
     * -1 = Some production, some imports needed
     * 0 = Sufficient production to meet needs
     * 1 = Overproduction, goods exported
     */
    private double  mEnergyProduction;
    
    public PopulatedObjectBean()
    {        
    }
    
    public PopulatedObjectBean(PopulatedObjectBean copy)
    {
        mAgriculturalProduction = copy.mAgriculturalProduction;
        mEnergyProduction = copy.mEnergyProduction;
        mMaterialProduction = copy.mMaterialProduction;
        mPopulation = copy.mPopulation;
        mProductivity = copy.mProductivity;
        mURI = copy.mURI;
        mTechTier = copy.mTechTier;
        setOID(copy.getOID());
    }
    
    // utility
    public double   getAgriculturalProductivity()
    {
        return D4WPopulationLogic.getProductivityFactor(getAgriculturalProduction(), getTechTier());
    }

    public double   getMaterialProductivity()
    {
        return D4WPopulationLogic.getProductivityFactor(getMaterialProduction(), getTechTier());
    }

    public double   getEnergyProductivity()
    {
        return D4WPopulationLogic.getProductivityFactor(getEnergyProduction(), getTechTier());
    }

    // getters/setters
    
    public long getPopulation()
    {
        return mPopulation;
    }
    public void setPopulation(long population)
    {
        mPopulation = population;
    }
    public int getTechTier()
    {
        return mTechTier;
    }
    public void setTechTier(int techTier)
    {
        mTechTier = techTier;
    }
    public double   getAgriculturalProduction()
    {
        return mAgriculturalProduction;
    }
    public void setAgriculturalProduction(double   agriculturalProduction)
    {
        mAgriculturalProduction = agriculturalProduction;
    }
    public double   getMaterialProduction()
    {
        return mMaterialProduction;
    }
    public void setMaterialProduction(double   materialProduction)
    {
        mMaterialProduction = materialProduction;
    }
    public double   getEnergyProduction()
    {
        return mEnergyProduction;
    }
    public void setEnergyProduction(double   energyProduction)
    {
        mEnergyProduction = energyProduction;
    }
    public String getURI()
    {
        return mURI;
    }
    public void setURI(String uRI)
    {
        mURI = uRI;
    }
    public double getProductivity()
    {
        return mProductivity;
    }
    public void setProductivity(double productivity)
    {
        mProductivity = productivity;
    }
}
