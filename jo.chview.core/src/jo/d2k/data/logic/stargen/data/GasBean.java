package jo.d2k.data.logic.stargen.data;

public class GasBean
{
    private ChemTable mChem;
    private double mSurfacePressure;      // units of millibars (mb) 
    
    public double getSurfacePressure()
    {
        return mSurfacePressure;
    }
    public void setSurfacePressure(double surfacePressure)
    {
        this.mSurfacePressure = surfacePressure;
    }
    public ChemTable getChem()
    {
        return mChem;
    }
    public void setChem(ChemTable chem)
    {
        this.mChem = chem;
    }
}
