package jo.d2k.web.json.handlers;

import jo.d4w.data.PopulatedWorldBean;

import org.json.simple.JSONObject;

public class JSONPopulatedWorldHandler extends JSONPopulatedObjectHandler
{
    public JSONPopulatedWorldHandler()
    {
        super(PopulatedWorldBean.class);
        mSkipProps.add("body");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void addSimpleProps(JSONObject p, Object obj)
    {
        super.addSimpleProps(p, obj);
        p.put("body", ((PopulatedWorldBean)obj).getBody().getURI());
    }
}
