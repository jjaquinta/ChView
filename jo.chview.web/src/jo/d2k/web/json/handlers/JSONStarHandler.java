package jo.d2k.web.json.handlers;

import java.util.Map;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.StarExtraLogic;
import jo.d2k.data.logic.stargen.SystemLogic;
import jo.d2k.data.logic.stargen.data.SunBean;
import jo.d2k.web.json.JSONGenericHandler;
import jo.d2k.web.json.JSONLogic;
import jo.d4w.data.PopulatedSystemBean;
import jo.d4w.logic.D4WPopulationLogic;
import jo.d4w.logic.D4WSystemLogic;

import org.json.simple.JSONObject;

public class JSONStarHandler extends JSONGenericHandler
{
    public JSONStarHandler()
    {
        super(StarBean.class);
        mSkipProps.add("parentRef");
    }
    
    @SuppressWarnings("unchecked")
    public JSONObject getJSON(Object obj, Map<String,String> params)
    {
        String starsAs = params.get("starAs");
        if ("uri".equals(starsAs))
        {
            JSONObject uri = new JSONObject();
            uri.put("id", ((StarBean)obj).getURI());
            return uri;
        }
        else if ("sun".equals(starsAs))
        {
            StarBean star = (StarBean)obj;
            SunBean sun = D4WSystemLogic.generateSystem(star);
            return JSONLogic.getJSON(sun, params);
        }
        else if ("sys".equals(starsAs))
        {
            StarBean star = (StarBean)obj;
            SunBean sun = SystemLogic.generateSystem(star);
            PopulatedSystemBean sys = D4WPopulationLogic.getInstance(sun);
            return JSONLogic.getJSON(sys, params);
        }
        else
        {
            JSONObject j = super.getJSON(obj, params);
            j.put("rgb", StarExtraLogic.getStarColorRGB((StarBean)obj));
            return j;
        }
    }
}
