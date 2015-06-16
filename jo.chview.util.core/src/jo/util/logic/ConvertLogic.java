package jo.util.logic;

import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.IntegerUtils;

public class ConvertLogic
{
    public static Object convert(Object obj, Class<?> newClass)
    {
        if (obj == null)
            return null;
        if (newClass == Object.class)
            return obj;
        Class<?> oldClass = obj.getClass();
        if (oldClass == newClass)
            return obj;
        if (newClass.isAssignableFrom(oldClass))
            return obj;
        if (newClass == String.class)
            return oldClass.toString();
        else if ((newClass == Integer.class) || (newClass == int.class))
        {
            return IntegerUtils.parseInt(obj);
        }
        else if ((newClass == Boolean.class) || (newClass == boolean.class))
        {
            return BooleanUtils.parseBoolean(obj);
        }
        System.out.println("Cannot convert a '"+oldClass.getName()+"' to a '"+newClass.getName()+"'");
        return obj;
    }
    
    public static boolean convert(Object[] oldObjs, int o, int l, Object[] newObjs, Class<?>[] newClasses)
    {
        boolean didIt = true;
        for (int i = 0; i < l; i++)
        {
            newObjs[i] = convert(oldObjs[o+i], newClasses[i]);
            if ((newObjs[i] == null) && (oldObjs[o+i] != null))
                didIt = false;
        }
        return didIt;
    }
}
