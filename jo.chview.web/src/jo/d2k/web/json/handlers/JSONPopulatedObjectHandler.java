package jo.d2k.web.json.handlers;

import java.util.Map;

import jo.d2k.web.json.JSONGenericHandler;
import jo.d4w.data.PopulatedObjectBean;
import jo.d4w.logic.D4WPopulationLogic;
import jo.util.utils.FormatUtils;

import org.json.simple.JSONObject;

public class JSONPopulatedObjectHandler extends JSONGenericHandler
{
    public JSONPopulatedObjectHandler()
    {
        super(PopulatedObjectBean.class);
    }
    
    public JSONPopulatedObjectHandler(Class<?> handledClass)
    {
        super(handledClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getJSON(Object obj, Map<String, String> params)
    {
        JSONObject json = super.getJSON(obj, params);
        PopulatedObjectBean pop = (PopulatedObjectBean)obj;
        json.put("populationDesc", FormatUtils.formatCommaNumber((long)pop.getPopulation()));
        json.put("techTierDesc", PopulatedObjectBean.TECH_DESCRIPTION[pop.getTechTier()]);
        json.put("agriculturalProductivity", pop.getAgriculturalProductivity());
        json.put("materialProductivity", pop.getMaterialProductivity());
        json.put("energyProductivity", pop.getEnergyProductivity());
        json.put("name", D4WPopulationLogic.getName(pop));
        return json;
    }
}
