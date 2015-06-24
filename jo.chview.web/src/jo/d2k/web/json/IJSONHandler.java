package jo.d2k.web.json;

import java.util.Map;

import org.json.simple.JSONObject;

public interface IJSONHandler
{
    public boolean isHandlerFor(Class<?> clazz);
    public JSONObject  getJSON(Object obj, Map<String,String> params); 
}
