package jo.d2k.web.json.handlers;

import java.util.Map;

import jo.d2k.web.json.JSONGenericHandler;
import jo.d4w.web.data.PortBean;

import org.json.simple.JSONObject;

public class JSONPortHandler extends JSONGenericHandler
{
    public JSONPortHandler()
    {
        super(PortBean.class);
    }
    
    @SuppressWarnings("unchecked")
    public JSONObject getJSON(Object obj, Map<String,String> params)
    {
        String starsAs = params.get("portAs");
        if ("uri".equals(starsAs))
        {
            JSONObject uri = new JSONObject();
            uri.put("id", ((PortBean)obj).getURI());
            return uri;
        }
        else
        {
            JSONObject j = super.getJSON(obj, params);
            return j;
        }
    }
}
