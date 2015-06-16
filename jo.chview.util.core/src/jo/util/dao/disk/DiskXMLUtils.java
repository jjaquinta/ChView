package jo.util.dao.disk;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import jo.util.beans.Bean;
import jo.util.beans.BeanLogic;
import jo.util.utils.ArrayUtils;
import jo.util.utils.BeanUtils;
import jo.util.utils.obj.StringUtils;
import jo.util.utils.xml.XMLEditUtils;
import jo.util.utils.xml.XMLUtils;

import org.w3c.dom.Node;

public class DiskXMLUtils
{
    public static Node toXML(Node root, Bean bean)
    {
        Map<String,Object> values = BeanLogic.getValues(bean);
        Node beanNode = XMLEditUtils.addElement(root, "bean");
        XMLEditUtils.addAttribute(beanNode, "class", bean.getClass().getName());
        for (String name : values.keySet())
        {
            Object val = values.get(name);
            Node n = toXMLObject(beanNode, val);
            if (n != null)
                XMLEditUtils.addAttribute(n, "name", name);
        }
        return beanNode;
    }
    
    @SuppressWarnings("rawtypes")
    private static Node toXMLObject(Node parent, Object val)
    {
        if (val == null)
            return null;
        if (val instanceof Collection)
        {
            Node collection = XMLEditUtils.addElement(parent, "collection");
            XMLEditUtils.addAttribute(collection, "class", val.getClass().getName());
            Object[] children = ((Collection)val).toArray();
            for (int i = 0; i < children.length; i++)
                toXMLObject(collection, children[i]);
            return collection;
        }
        else if (val instanceof Map)
        {
            Node mapNode = XMLEditUtils.addElement(parent, "map");
            XMLEditUtils.addAttribute(mapNode, "class", val.getClass().getName());
            Map map = (Map)val;
            for (Iterator i = map.keySet().iterator(); i.hasNext(); )
            {
                Node entry = XMLEditUtils.addElement(mapNode, "entry");
                Object mapkey = i.next();
                Object mapval = map.get(mapkey);
                toXMLObject(entry, mapkey);
                toXMLObject(entry, mapval);
            }
            return mapNode;
        }
        else if (val.getClass().isArray())
        {
            Node array = XMLEditUtils.addElement(parent, "array");
            XMLEditUtils.addAttribute(array, "class", val.getClass().getName());
            Object[] children = ArrayUtils.toArray(val);
            for (int i = 0; i < children.length; i++)
                toXMLObject(array, children[i]);
            return array;
        }
        else if (val instanceof Bean)
        {
            Node b = toXML(parent, (Bean)val);
            return b;
        }
        else
        {
            Node simple = XMLEditUtils.addElement(parent, "simple");
            XMLEditUtils.addAttribute(simple, "class", val.getClass().getName());
            XMLEditUtils.addAttribute(simple, "value", val.toString());
            return simple;
        }
        
    }

    public static Bean fromXML(Node beanNode, ClassLoader loader)
    {
        String className = XMLUtils.getAttribute(beanNode, "class");
        Bean bean = null;
        try
        {
            bean = (Bean)loader.loadClass(className).newInstance();
            for (Node n = beanNode.getFirstChild(); n != null; n = n.getNextSibling())
            {
                String type = n.getNodeName();
                if (type.startsWith("#"))
                    continue;
                String name = XMLUtils.getAttribute(n, "name");
                if (StringUtils.isTrivial(name))
                    continue;
                Object val = fromXMLObject(n, loader);
                BeanUtils.set(bean, name, val);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bean;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Object fromXMLObject(Node n, ClassLoader loader) throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        String type = n.getNodeName();
        if (type.equals("simple"))
        {
            String className = XMLUtils.getAttribute(n, "class");
            String value = XMLUtils.getAttribute(n, "value");
            if (className.equals("java.lang.Integer"))
                return new Integer(value);
            if (className.equals("java.lang.Long"))
                return new Long(value);
            if (className.equals("java.lang.Double"))
                return new Double(value);
            if (className.equals("java.lang.Float"))
                return new Float(value);
            if (className.equals("java.lang.Character"))
                return new Character(value.charAt(0));
            if (className.equals("java.lang.Boolean"))
                return new Boolean(value);
            if (className.equals("java.lang.String"))
                return value;            
        }
        else if (type.equals("bean"))
            return fromXML(n, loader);
//        else if (type.equals("array"))
//        {
//            String className = XMLUtils.getAttribute(n, "class");
//            ArrayList coll = new ArrayList();
//            for (Node nn = n.getFirstChild(); nn != null; nn = nn.getNextSibling())
//            {
//                if (nn.getNodeName().startsWith("#"))
//                    continue;
//                coll.add(fromXMLObject(nn, loader));
//            }
//            return ArrayUtils.toArray(coll.toArray(), className);
//        }
        else if (type.equals("map"))
        {
            String className = XMLUtils.getAttribute(n, "class");
            Map map = (Map)loader.loadClass(className).newInstance();
            for (Node nn = n.getFirstChild(); nn != null; nn = nn.getNextSibling())
            {
                if (!nn.getNodeName().equals("entry"))
                    continue;
                Node keyNode = nn.getFirstChild();
                while (keyNode.getNodeName().startsWith("#"))
                    keyNode = keyNode.getNextSibling();
                Node valNode = keyNode.getNextSibling();
                while (valNode.getNodeName().startsWith("#"))
                    valNode = valNode.getNextSibling();
                Object key = fromXMLObject(keyNode, loader);
                Object val = fromXMLObject(valNode, loader);
                map.put(key, val);
            }
            return map;
        }
        else if (type.equals("collection"))
        {
            String className = XMLUtils.getAttribute(n, "class");
            Collection coll = (Collection)loader.loadClass(className).newInstance();
            for (Node nn = n.getFirstChild(); nn != null; nn = nn.getNextSibling())
            {
                if (!nn.getNodeName().equals("entry"))
                    continue;
                coll.add(fromXMLObject(nn, loader));
            }
            return coll;
        }
        return null;
    }
}
