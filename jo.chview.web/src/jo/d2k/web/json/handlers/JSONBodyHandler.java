package jo.d2k.web.json.handlers;

import java.util.Map;

import jo.d2k.data.logic.stargen.data.BodyBean;
import jo.d2k.web.json.JSONGenericHandler;

import org.json.simple.JSONObject;

public class JSONBodyHandler extends JSONGenericHandler
{
    @SuppressWarnings("rawtypes")
    public JSONBodyHandler(Class subClass)
    {
        super(subClass);
        mSkipProps.add("parent");
        mSkipProps.add("star");
        mSkipProps.add("sun");
    }
    
    @Override
    public boolean isHandlerFor(Class<?> clazz)
    {
        return mHandledClass.isAssignableFrom(clazz);
    }

    @SuppressWarnings("unchecked")
    public JSONObject getJSON(Object obj, Map<String,String> params)
    {
        String starsAs = params.get("sunAs");
        if ("uri".equals(starsAs))
        {
            JSONObject uri = new JSONObject();
            uri.put("id", ((BodyBean)obj).getURI());
            return uri;
        }
        else
            return super.getJSON(obj, params);
    }
}
