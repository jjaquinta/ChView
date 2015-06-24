package jo.d2k.web.json;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jo.d2k.data.data.URIBean;
import jo.util.utils.ArrayUtils;
import jo.util.utils.BeanUtils;
import jo.util.utils.MapUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONGenericHandler implements IJSONHandler
{
    protected Class<?>  mHandledClass;
    private boolean mNeedInit = false;
    protected Map<String,String>    mSimpleProps = new HashMap<String, String>();
    protected Map<String,String>    mArrayProps = new HashMap<String, String>();
    protected Map<String,String>    mObjProps = new HashMap<String, String>();
    protected Set<String>           mSkipProps = new HashSet<String>();
    
    public JSONGenericHandler()
    {
    }
    
    public JSONGenericHandler(Class<?> handledClass)
    {
        mHandledClass = handledClass;
        mNeedInit = true;
    }
    
    protected void addKnownProps()
    {
        List<String> props = BeanUtils.getProperties(mHandledClass);
        for (String prop : props)
        {
            if (mSkipProps.contains(prop))
                continue;
            Method m;
            try
            {
                m = BeanUtils.getMethod(mHandledClass, "get"+prop);
                if (m == null)
                {
                    m = BeanUtils.getMethod(mHandledClass, "is"+prop);
                    if (m == null)
                        throw new IllegalStateException("can't find getter for '"+prop+"' in '"+mHandledClass.getName()+"'");
                }
                Class<?> ret = m.getReturnType();
                if (JSONLogic.isSimple(ret))
                {
                    //System.out.println("Adding simple prop '"+prop+"' for '"+mHandledClass.getName()+"'");
                    if (prop.equalsIgnoreCase("URI"))
                        mSimpleProps.put("id", prop);
                    else
                        mSimpleProps.put(prop, prop);
                    continue;
                }
                if (JSONLogic.getHandler(ret) != null)
                {
                    //System.out.println("Adding object prop '"+prop+"' for '"+mHandledClass.getName()+"'");
                    mObjProps.put(prop, prop);
                    continue;
                }
                if (Collection.class.isAssignableFrom(ret) || (ret.getName().indexOf("[") >= 0))
                {
                    //System.out.println("Adding array prop '"+prop+"' for '"+mHandledClass.getName()+"'");
                    mArrayProps.put(prop, prop);
                    continue;
                }
            }
            catch (Exception e)
            {
                throw new IllegalStateException("can't find getter for '"+prop+"' in '"+mHandledClass.getName()+"'", e);
            }            
        }
    }
    
    @Override
    public boolean isHandlerFor(Class<?> clazz)
    {
        return clazz == mHandledClass;
    }

    @Override
    public JSONObject getJSON(Object obj, Map<String,String> params)
    {
        if (mNeedInit)
        {
            addKnownProps();
            mNeedInit = false;
        }
        JSONObject p = new JSONObject();
        addSimpleProps(p, obj);
        addArraryProps(p, obj, params);
        addObjectProps(p, obj, params);
        return p;
    }

    @SuppressWarnings("unchecked")
    public void addObjectProps(JSONObject p, Object obj, Map<String,String> params)
    {
        int depth = 1;
        if (params.containsKey("depth"))
            depth = Integer.parseInt(params.get("depth"));
        for (Entry<String, String> e : mObjProps.entrySet())
        {
            String tagName = e.getKey();
            String propName = e.getValue();
            Object value = BeanUtils.get(obj, propName);
            if (value == null)
                p.put(tagName, null);
            else if ((depth <= 0) && (value instanceof URIBean))
            {
                JSONObject o = new JSONObject();
                o.put("id", ((URIBean)value).getURI());
                p.put(tagName, o);
            }
            else
            {
                Map<String,String> newParams = new HashMap<String, String>();
                MapUtils.copy(newParams, params);
                if (params.containsKey("depth"))
                    newParams.put("depth", String.valueOf(depth - 1));
                //System.out.println(">>"+tagName);
                p.put(tagName, JSONLogic.getJSON(value, newParams));
                //System.out.println("<<"+tagName);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void addArraryProps(JSONObject p, Object obj, Map<String,String> params)
    {
        for (Entry<String, String> e : mArrayProps.entrySet())
        {
            String tagName = e.getKey();
            String propName = e.getValue();
            Object value = BeanUtils.get(obj, propName);
            JSONArray os = makeArray(value, params);
            if (os.size() > 0)
                p.put(tagName, os);            
        }
    }

    @SuppressWarnings("unchecked")
    public static JSONArray makeArray(Object value, Map<String,String> params)
    {
        JSONArray os = new JSONArray();
        int depth = 1;
        if (params.containsKey("depth"))
            depth = Integer.parseInt(params.get("depth"));
        if (value != null)
        {
            Object[] arr = ArrayUtils.toArray(value);
            for (Object elem : arr)
            {
                if ((depth <= 0) && (elem instanceof URIBean))
                {
                    JSONObject o = new JSONObject();
                    o.put("id", ((URIBean)elem).getURI());
                    os.add(o);
                }
                else if (JSONLogic.isSimple(elem.getClass()))
                {
                    //System.out.println("Adding primitive array element '"+elem+"' to '"+propName+"'");
                    os.add(elem);
                }
                else if (elem instanceof Object[])
                {
                    JSONArray oss = makeArray(elem, params);
                    os.add(oss);
                }
                else
                    os.add(JSONLogic.getJSON(elem, params));
            }
        }
        return os;
    }

    @SuppressWarnings("unchecked")
    public void addSimpleProps(JSONObject p, Object obj)
    {
        for (Entry<String, String> e : mSimpleProps.entrySet())
        {
            String tagName = e.getKey();
            String propName = e.getValue();
            Object value = BeanUtils.get(obj, propName);
            if (value == null)
                p.put(tagName, null);
            else if (value instanceof Number)
            {
                if ("OID".equalsIgnoreCase(propName) && (((Number)value).intValue() == -1))
                    continue;
                p.put(tagName, value);
            }
            else
                p.put(tagName, value.toString());
        }
    }
}
