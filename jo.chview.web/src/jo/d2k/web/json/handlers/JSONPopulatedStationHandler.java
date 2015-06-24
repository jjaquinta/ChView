package jo.d2k.web.json.handlers;

import jo.d4w.data.PopulatedStationBean;

import org.json.simple.JSONObject;

public class JSONPopulatedStationHandler extends JSONPopulatedObjectHandler
{
    public JSONPopulatedStationHandler()
    {
        super(PopulatedStationBean.class);
        mSkipProps.add("body");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void addSimpleProps(JSONObject p, Object obj)
    {
        super.addSimpleProps(p, obj);
        p.put("body", ((PopulatedStationBean)obj).getBody().getURI());
    }
}
