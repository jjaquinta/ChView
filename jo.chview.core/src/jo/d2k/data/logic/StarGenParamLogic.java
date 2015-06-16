package jo.d2k.data.logic;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import jo.d2k.data.data.StarGenParams;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.DoubleUtils;
import jo.util.utils.obj.IntegerUtils;

public class StarGenParamLogic
{
    public static void loadFromMetadata(StarGenParams params)
    {
        Map<String,String> map = MetadataLogic.getAsMap("stargen", -1);
        if (map.size() == 0)
        {   // set to default
            StarGenParams defs = new StarGenParams();
            toMap(map, defs);
        }
        fromMap(params, map);
    }
    
    public static void saveToMetadata(StarGenParams params)
    {
        Map<String,String> map = new HashMap<String, String>();
        toMap(map, params);
        MetadataLogic.setAsMap("stargen", -1, map);
    }
    
    public static void toMap(Map<String,String> map, StarGenParams params)
    {
        for (Field f : StarGenParams.class.getFields())
        {
            try
            {
                put(map, f.getName(), f.get(params));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
//        put(map, "GENERATE", params.GENERATE);
//        put(map, "EXCLUSION_ZONE", params.EXCLUSION_ZONE);
//        put(map, "SPECTRUM_FREQ", params.SPECTRUM_FREQ);
//        put(map, "SPECTRUM_CLASS_FREQ", params.SPECTRUM_CLASS_FREQ);
//        put(map, "CLASS_NAME", params.CLASS_NAME);
//        put(map, "ABS_MAG_FREQ", params.ABS_MAG_FREQ);
//        put(map, "SECONDARY_NUM_FREQ", params.SECONDARY_NUM_FREQ);
//        put(map, "SECONDARY_TYPE_FREQ", params.SECONDARY_TYPE_FREQ);
//        put(map, "SECONDARY_DISTANCE_FREQ", params.SECONDARY_DISTANCE_FREQ);
//        put(map, "GREEK_NAMES", params.GREEK_NAMES);
//        put(map, "PREFIX", params.PREFIX);
    }

    private static void put(Map<String,String> map, String key, Object val)
    {
        if (val == null)
            return;
        if (val instanceof String)
            map.put(key, (String)val);
        else if (val instanceof Number)
            map.put(key, val.toString());
        else if (val instanceof Boolean)
            map.put(key, val.toString());
        else if (val instanceof int[])
        {
            int[] arr = (int[])val;
            for (int i = 0; i < arr.length; i++)
                put(map, key+"."+i, arr[i]);
        }
        else if (val instanceof int[][])
        {
            int[][] arr = (int[][])val;
            for (int i = 0; i < arr.length; i++)
                put(map, key+"."+i, arr[i]);
        }
        else if (val instanceof double[])
        {
            double[] arr = (double[])val;
            for (int i = 0; i < arr.length; i++)
                put(map, key+"."+i, arr[i]);
        }
        else if (val instanceof double[][])
        {
            double[][] arr = (double[][])val;
            for (int i = 0; i < arr.length; i++)
                put(map, key+"."+i, arr[i]);
        }
        else if (val instanceof String[])
        {
            String[] arr = (String[])val;
            for (int i = 0; i < arr.length; i++)
                put(map, key+"."+i, arr[i]);
        }
        else
            throw new IllegalArgumentException("Type '"+val.getClass().getName()+"' not supported");
    }
    
    public static void fromMap(StarGenParams params, Map<String,String> map)
    {
        for (Field f : StarGenParams.class.getFields())
        {
            try
            {
                Object val = get(map, f.getName(), f.getType());
                if (val != null)
                    f.set(params, val);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private static Class<?> STRING_ARRAY = (new String[0]).getClass();
    private static Class<?> INT_ARRAY = (new int[0]).getClass();
    private static Class<?> DOUBLE_ARRAY = (new double[0]).getClass();
    private static Class<?> INT2_ARRAY = (new int[0][0]).getClass();
    private static Class<?> DOUBLE2_ARRAY = (new double[0][0]).getClass();
    
    private static Object get(Map<String,String> map, String key, Class<?> type)
    {
        if (type == String.class)
            return map.get(key);
        else if (type == boolean.class)
            if (!map.containsKey(key))
                return null;
            else
                return BooleanUtils.parseBoolean(map.get(key));
        else if (type == int.class)
            if (!map.containsKey(key))
                return null;
            else
                return IntegerUtils.parseInt(map.get(key));
        else if (type == double.class)
            if (!map.containsKey(key))
                return null;
            else
                return DoubleUtils.parseDouble(map.get(key));
        else if (type == STRING_ARRAY)
        {
            if (!map.containsKey(key+".0"))
                return null;
            int l = getSize(map, key);
            String[] arr = new String[l];
            for (int i = 0; i < l; i++)
                arr[i] = map.get(key + "." + i);
            return arr;
        }
        else if (type == INT_ARRAY)
        {
            if (!map.containsKey(key+".0"))
                return null;
            int l = getSize(map, key);
            int[] arr = new int[l];
            for (int i = 0; i < l; i++)
                arr[i] = IntegerUtils.parseInt(map.get(key + "." + i));
            return arr;
        }
        else if (type == DOUBLE_ARRAY)
        {
            if (!map.containsKey(key+".0"))
                return null;
            int l = getSize(map, key);
            double[] arr = new double[l];
            for (int i = 0; i < l; i++)
                arr[i] = DoubleUtils.parseDouble(map.get(key + "." + i));
            return arr;
        }
        else if (type == INT2_ARRAY)
        {
            if (!map.containsKey(key+".0.0"))
                return null;
            int l = getSize(map, key);
            int[][] arr = new int[l][];
            for (int i = 0; i < l; i++)
                arr[i] = (int[])get(map, key+"."+i, INT_ARRAY);
            return arr;
        }
        else if (type == DOUBLE2_ARRAY)
        {
            if (!map.containsKey(key+".0.0"))
                return null;
            int l = getSize(map, key);
            double[][] arr = new double[l][];
            for (int i = 0; i < l; i++)
                arr[i] = (double[])get(map, key+"."+i, DOUBLE_ARRAY);
            return arr;
        }
        else
            throw new IllegalArgumentException("Type '"+type.getName()+"' not supported");
    }
    
    private static int getSize(Map<String,String> map, String key)
    {
        if (map.containsKey(key+".0"))
        {
            for (int i = 0; i < 1000; i++)
                if (!map.containsKey(key+"."+i))
                    return i;
        }
        else if (map.containsKey(key+".0.0"))
        {
            for (int i = 0; i < 1000; i++)
                if (!map.containsKey(key+"."+i+".0"))
                    return i;
        }
        return 0;
    }
}
