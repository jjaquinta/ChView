package jo.d4w.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.d2k.data.logic.stargen.data.BodyBean;
import jo.d2k.data.logic.stargen.data.SunBean;

public class PopulatedSystemBean extends PopulatedObjectBean
{
    private SunBean mSun;
    private List<PopulatedObjectBean>   mPopulations;
    private Map<BodyBean,List<PopulatedObjectBean>> mPopulationsIndex;
    
    public PopulatedSystemBean()
    {
        mPopulations = new ArrayList<PopulatedObjectBean>();
        mPopulationsIndex = new HashMap<BodyBean, List<PopulatedObjectBean>>();
    }
    public SunBean getSun()
    {
        return mSun;
    }
    public void setSun(SunBean sun)
    {
        mSun = sun;
    }
    public List<PopulatedObjectBean> getPopulations()
    {
        return mPopulations;
    }
    public void setPopulations(List<PopulatedObjectBean> populations)
    {
        mPopulations = populations;
    }
    public Map<BodyBean, List<PopulatedObjectBean>> getPopulationsIndex()
    {
        return mPopulationsIndex;
    }
    public void setPopulationsIndex(
            Map<BodyBean, List<PopulatedObjectBean>> populationsIndex)
    {
        mPopulationsIndex = populationsIndex;
    }
}
