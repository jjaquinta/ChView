package jo.d2k.web.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.d2k.data.data.RegionCubeBean;
import jo.d2k.data.data.RegionQuadBean;
import jo.d2k.data.data.RegionSphereBean;
import jo.d2k.data.logic.stargen.data.SolidBodyBean;
import jo.d2k.data.logic.stargen.data.SunBean;
import jo.d2k.web.json.handlers.JSONBodyHandler;
import jo.d2k.web.json.handlers.JSONPopulatedObjectHandler;
import jo.d2k.web.json.handlers.JSONPopulatedStationHandler;
import jo.d2k.web.json.handlers.JSONPopulatedSystemHandler;
import jo.d2k.web.json.handlers.JSONPopulatedWorldHandler;
import jo.d2k.web.json.handlers.JSONPortHandler;
import jo.d2k.web.json.handlers.JSONStarHandler;
import jo.util.utils.xml.XMLEditUtils;
import jo.util.utils.xml.XMLUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class JSONLogic
{
    private static final List<IJSONHandler> mHandlers = new ArrayList<IJSONHandler>();
    static
    {
        mHandlers.add(new JSONHashHandler());
        mHandlers.add(new JSONGenericHandler(RegionCubeBean.class));
        mHandlers.add(new JSONGenericHandler(RegionQuadBean.class));
        mHandlers.add(new JSONGenericHandler(RegionSphereBean.class));
        mHandlers.add(new JSONStarHandler());
        mHandlers.add(new JSONBodyHandler(SunBean.class));
        mHandlers.add(new JSONBodyHandler(SolidBodyBean.class));
        mHandlers.add(new JSONPopulatedObjectHandler());
        mHandlers.add(new JSONPopulatedSystemHandler());
        mHandlers.add(new JSONPopulatedWorldHandler());
        mHandlers.add(new JSONPopulatedStationHandler());
        mHandlers.add(new JSONPortHandler());
    }
    private static Map<Class<?>, IJSONHandler> mIndex = new HashMap<Class<?>, IJSONHandler>();
    
    public static IJSONHandler getHandler(Class<?> clazz)
    {
        if (mIndex == null)
            mIndex = new HashMap<Class<?>, IJSONHandler>(); // not sure why this is necessary
        if (mIndex.containsKey(clazz))
            return mIndex.get(clazz);
        for (IJSONHandler h : mHandlers)
            if (h.isHandlerFor(clazz))
            {
                mIndex.put(clazz, h);
                return h;
            }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public static JSONObject getJSON(Object o, Map<String,String> params)
    {
        if (o == null)
            return null;
        if (o instanceof Collection)
        {
            JSONObject shell = new JSONObject();
            shell.put("array", JSONGenericHandler.makeArray(o, params));
            return shell;
        }
        //System.out.println("getJSON("+o.getClass().getSimpleName()+")");
        IJSONHandler h = getHandler(o.getClass());
        if (h == null)
        {
            System.out.println("No handler for "+o.getClass().getName()+", making a generic one");
            h = new JSONGenericHandler(o.getClass());
            mIndex.put(o.getClass(), h);
        }
        return h.getJSON(o, params);
    }
    
    public static Document getXML(Object o, Map<String,String> params)
    {
        JSONObject json;
        if (o instanceof JSONObject)
            json = (JSONObject)o;
        else
            json = getJSON(o, params);
        if (json == null)
            return null;
        Document doc = XMLUtils.newDocument();
        String rootName = getRootName(o);
        Node root = XMLEditUtils.addElement(doc, rootName);
        convertJSON(root, json);
        return doc;
    }

    public static String getRootName(Object o)
    {
        String rootName = o.getClass().getName();
        int idx = rootName.lastIndexOf('.');
        if (idx > 0)
            rootName = rootName.substring(idx + 1);
        if (rootName.startsWith("JSON"))
            rootName = rootName.substring(4);
        if (rootName.endsWith("Handler"))
            rootName = rootName.substring(0, rootName.length() - 6);
        if (rootName.endsWith("Bean"))
            rootName = rootName.substring(0, rootName.length() - 4);
        return rootName.toLowerCase();
    }
    
    private static void convertJSON(Node node, JSONObject json)
    {
        for (Object k : json.keySet())
        {
            String key = k.toString();
            Object v = json.get(k);
            if (v instanceof JSONArray)
                convertJSONArray(node, key, (JSONArray)v);
            else if (v instanceof JSONObject)
            {
                Node c = XMLEditUtils.addElement(node, key);
                convertJSON(c, (JSONObject)v);
            }
            else if (v != null)
            {
                if (!(v instanceof String) && !(v instanceof Number))
                    System.out.println("Warning: unexpected value of type "+v.getClass().getName());
                XMLEditUtils.addAttribute(node, key, v.toString());
            }
        }
    }

    private static void convertJSONArray(Node node, String key, JSONArray v)
    {
        Node cs = XMLEditUtils.addElement(node, key);
        String singular = makeSingular(key);
        for (Object o : v.toArray())
        {
            if (o instanceof JSONObject)
            {
                Node c = XMLEditUtils.addElement(cs, singular);
                convertJSON(c, (JSONObject)o);
            }
            else if (isSimple(o.getClass()))
                XMLEditUtils.addTextTag(cs, singular, o.toString());
            else if (o instanceof JSONArray)
                convertJSONArray(cs, key, (JSONArray)o);
            else
                throw new IllegalArgumentException("Don't know how to handle an array of "+o.getClass().getName());
        }
    }
    
    private static final String[][] plurals = {
        { "children", "child" },
    };

    private static String makeSingular(String key)
    {
        if (key.endsWith("s"))
            return key.substring(0, key.length() - 1);
        for (int i = 0; i < plurals.length; i++)
            if (key.equals(plurals[i][0]))
                return plurals[i][1];
        return "a"+key;
    }

    public static boolean isSimple(Class<?> clazz)
    {
        return clazz.isPrimitive() || clazz.getName().startsWith("java.lang.");
    }
}
