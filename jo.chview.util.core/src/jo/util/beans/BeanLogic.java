/*
 * Created on Apr 11, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import jo.util.logic.ConvertLogic;

public class BeanLogic
{   
    private static long nextOID = System.currentTimeMillis();
    
    public static synchronized long getNextOID()
    {
        return nextOID++;
    }
    
    public static Map<String,PropertyDescriptor> getDescriptors(Class<?> beanClass) throws IntrospectionException
    {
        Map<String,PropertyDescriptor> ret = new HashMap<String,PropertyDescriptor>();
        BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
        PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < props.length; i++)
        {
            if ((props[i].getReadMethod() == null)
                || (props[i].getWriteMethod() == null))
                continue; // must be r/w
            String name = props[i].getName();
            ret.put(name.toLowerCase(), props[i]);
        }
        return ret;
    }

    public static Map<String,Object> getValues(Map<String,PropertyDescriptor> descriptors, Bean bean)
    {
        Object[] args = new Object[0];
        Map<String,Object> ret = new HashMap<String,Object>();
        for (String name : descriptors.keySet())
        {
            PropertyDescriptor prop = (PropertyDescriptor)descriptors.get(name);
            try
            {
                Object val = prop.getReadMethod().invoke(bean, args);
                /*
                if (val instanceof Bean)
                {
                    HashMap subValues = getValues((Bean)val);
                    for (Iterator j = subValues.keySet().iterator(); j.hasNext(); )
                    {
                        String subName = (String)j.next();
                        Object subVal = subValues.get(subName);
                        ret.put(name+"."+subName, subVal);
                    }
                }
                else
                */
                    ret.put(name, val);
            }
            catch (Exception e)
            {
            }
        }
        return ret;
    }
    
    public static Map<String,Object> getValues(Bean bean)
    {
        if (bean == null)
            return new HashMap<String,Object>();
        try
        {
            return getValues(getDescriptors(bean.getClass()), bean);
        }
        catch (IntrospectionException e)
        {
            return new HashMap<String,Object>();
        }
    }
    public static void dumpMethods(Class<?> c)
    {
        System.out.println("Methods on "+c.getName()+":");
        for (Method m : c.getMethods())
        {
            System.out.print("  "+m.getName()+"(");
            Class<?>[] params = m.getParameterTypes();
            for (int i = 0; i < params.length; i++)
            {
                if (i > 0)
                    System.out.print(", ");
                System.out.print(params[i].getName());
            }
            System.out.print(")");
            if (m.getReturnType() != null)
                System.out.print(" = "+m.getReturnType().getName());
            System.out.println();
        }
        
    }

    public static Method findMethod(Object bean, String func, Object[] args)
    {
        if (bean == null)
            return null;
        Class<?> beanClass;
        if (bean instanceof Class)
            beanClass = (Class<?>)bean;
        else
            beanClass = bean.getClass();
        Method[] ms = beanClass.getMethods();
        for (Method m : ms)
        {
            if (!m.getName().equalsIgnoreCase(func))
                continue;
            if ((args == null) || (args.length == 0))
                return m;
            Class<?>[] params = m.getParameterTypes();
            if (params.length != args.length)
                continue;
            Object[] funcArgs = new Object[params.length];
            if (ConvertLogic.convert(args, 0, args.length, funcArgs, params))
                return m;
        }
        return null;
    }
    
    public static Object invoke(Object bean, String func, Object[] args)
    {
        if (bean == null)
            throw new IllegalArgumentException("Cannot invoke on null object");
        Class<?> beanClass;
        if (bean instanceof Class)
            beanClass = (Class<?>)bean;
        else
            beanClass = bean.getClass();
        Method m = findMethod(bean, func, args);
        if (m == null)
        {
            System.out.println("While invoking '"+func+"' on '"+beanClass.getName()+"', no method found.");
            System.out.println("Passed arguments (raw):");
            for (Object o : args)
                if (o == null)
                    System.out.println("  <null>");
                else
                    System.out.println("  "+o.getClass().getName());
            dumpMethods(beanClass);
            throw new IllegalStateException("Cannot invoke '"+func+"' on '"+beanClass.getName()+"', no method found.");
        }
        if (args == null)
            args = new Object[0];
        Object[] funcArgs = new Object[args.length];
        ConvertLogic.convert(args, 0, args.length, funcArgs, m.getParameterTypes());
        try
        {
            if (bean instanceof Class)
                return m.invoke(null, funcArgs);
            else
                return m.invoke(bean, funcArgs);
        }
        catch (Exception e)
        {
            if ((e.getMessage() != null) && e.getMessage().contains("argument type mismatch"))
            {
                System.out.println("Argument type mismatch while invoking '"+func+"' on '"+((bean.getClass() == Class.class) ? ((Class<?>)bean).getName() : bean.getClass().getName())+"'");
                System.out.println("Declared arguments:");
                for (Class<?> c : m.getParameterTypes())
                    System.out.println("  "+c.getName());
                System.out.println("Passed arguments (raw):");
                for (Object o : args)
                    if (o == null)
                        System.out.println("  <null>");
                    else
                        System.out.println("  "+o.getClass().getName());
                System.out.println("Passed arguments (processed):");
                for (Object o : funcArgs)
                    if (o == null)
                        System.out.println("  <null>");
                    else
                        System.out.println("  "+o.getClass().getName());
            }
            if (e instanceof InvocationTargetException)
                throw new IllegalStateException("Error while invoking '"+func+"' on '"+bean.getClass().getName()+"'", e);
            else
                throw new IllegalStateException("Cannot invoke '"+func+"' on '"+bean.getClass().getName()+"'", e);
        }
    }
}
