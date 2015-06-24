package jo.d2k.web.json;

import java.util.Map;

import jo.d2k.data.data.HashBean;

import org.json.simple.JSONObject;

public class JSONHashHandler implements IJSONHandler
{
    @Override
    public boolean isHandlerFor(Class<?> clazz)
    {
        return clazz == HashBean.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getJSON(Object obj, Map<String,String> params)
    {
        HashBean hash = (HashBean)obj;
        JSONObject p = new JSONObject();
        for (String key : hash.keySet())
        {
            Object val = hash.get(key);
            if (val == null)
                p.put(key, val);
            else if (JSONLogic.isSimple(val.getClass()))
                p.put(key, val);
            else
                p.put(key, JSONLogic.getJSON(val, params));
        }
        return p;
    }

}
