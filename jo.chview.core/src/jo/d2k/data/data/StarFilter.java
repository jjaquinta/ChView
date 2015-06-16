package jo.d2k.data.data;

import java.util.HashMap;
import java.util.Map;

import jo.util.beans.Bean;

public class StarFilter extends Bean
{
    private boolean mSpectraO;
    private boolean mSpectraB;
    private boolean mSpectraA;
    private boolean mSpectraF;
    private boolean mSpectraG;
    private boolean mSpectraK;
    private boolean mSpectraM;
    private boolean mSpectraL;
    private boolean mSpectraT;
    private boolean mSpectraY;
    private Boolean mGenerated;
    private Map<String,String>  mExtraFields;
    
    public StarFilter()
    {
        mExtraFields = new HashMap<String, String>();
    }
    
    public boolean isSpectraO()
    {
        return mSpectraO;
    }
    public void setSpectraO(boolean spectraO)
    {
        mSpectraO = spectraO;
    }
    public boolean isSpectraB()
    {
        return mSpectraB;
    }
    public void setSpectraB(boolean spectraB)
    {
        mSpectraB = spectraB;
    }
    public boolean isSpectraA()
    {
        return mSpectraA;
    }
    public void setSpectraA(boolean spectraA)
    {
        mSpectraA = spectraA;
    }
    public boolean isSpectraF()
    {
        return mSpectraF;
    }
    public void setSpectraF(boolean spectraF)
    {
        mSpectraF = spectraF;
    }
    public boolean isSpectraG()
    {
        return mSpectraG;
    }
    public void setSpectraG(boolean spectraG)
    {
        mSpectraG = spectraG;
    }
    public boolean isSpectraK()
    {
        return mSpectraK;
    }
    public void setSpectraK(boolean spectraK)
    {
        mSpectraK = spectraK;
    }
    public boolean isSpectraM()
    {
        return mSpectraM;
    }
    public void setSpectraM(boolean spectraM)
    {
        mSpectraM = spectraM;
    }
    public boolean isSpectraL()
    {
        return mSpectraL;
    }
    public void setSpectraL(boolean spectraL)
    {
        mSpectraL = spectraL;
    }
    public boolean isSpectraT()
    {
        return mSpectraT;
    }
    public void setSpectraT(boolean spectraT)
    {
        mSpectraT = spectraT;
    }
    public boolean isSpectraY()
    {
        return mSpectraY;
    }
    public void setSpectraY(boolean spectraY)
    {
        mSpectraY = spectraY;
    }
    public Boolean getGenerated()
    {
        return mGenerated;
    }
    public void setGenerated(Boolean generated)
    {
        mGenerated = generated;
    }
    public Map<String, String> getExtraFields()
    {
        return mExtraFields;
    }
    public void setExtraFields(Map<String, String> extraFields)
    {
        mExtraFields = extraFields;
    }
}
