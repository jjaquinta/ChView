/*
 * Created on Apr 11, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.dao.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.util.beans.Bean;

public class DAOLogic
{
    private static Map<String,List<DAOListener>>      mListeners = new HashMap<String, List<DAOListener>>();

    private static List<DAOListener> getListeners(String beanName)
    {
        List<DAOListener> ret = new ArrayList<DAOListener>();
        List<DAOListener> base = mListeners.get("");
        if (base != null)
            ret.addAll(base);
        if (beanName.length() > 0)
        {
            List<DAOListener> ext = mListeners.get(makeBeanIndex(beanName));
            if (ext != null)
                ret.addAll(ext);
        }
        return ret;
    }
    
    public static void addDAOListener(DAOListener listener)
    {
        synchronized (mListeners)
        {
            List<DAOListener> list = mListeners.get("");
            if (list == null)
            {
                list = new ArrayList<DAOListener>();
                mListeners.put("", list);
            }
            list.add(listener);
        }
    }
    
    public static void addDAOListener(Class<?> beanClass, DAOListener listener)
    {
        addDAOListener(beanClass.getName(), listener);
    }
    
    public static void addDAOListener(String beanName, DAOListener listener)
    {
        synchronized (mListeners)
        {
            List<DAOListener> list = mListeners.get(makeBeanIndex(beanName));
            if (list == null)
            {
                list = new ArrayList<DAOListener>();
                mListeners.put(makeBeanIndex(beanName), list);
            }
            list.add(listener);
        }
    }
    
    public static void removeDAOListener(DAOListener listener)
    {
        synchronized (mListeners)
        {
            List<DAOListener> list = mListeners.get("");
            if (list != null)
                list.remove(listener);
        }
    }
    
    public static void removeDAOListener(Class<?> beanClass, DAOListener listener)
    {
        removeDAOListener(beanClass.getName(), listener);
    }
    
    public static void removeDAOListener(String beanName, DAOListener listener)
    {
        synchronized (mListeners)
        {
            List<DAOListener> list = mListeners.get(makeBeanIndex(beanName));
            if (list != null)
                list.remove(listener);
        }
    }
    
    public static void fireEvent(DAOEvent event)
    {
        Object[] listeners = getListeners(event.getBeanName()).toArray();
        for (int i = 0; i < listeners.length; i++)
            ((DAOListener)listeners[i]).DAOUpdate(event);
    }
    
    public static void fireEvent(int id, String beanName, Bean bean)
    {
        DAOEvent event = new DAOEvent();
        event.setId(id);
        event.setBeanName(beanName);
        event.setBean(bean);
        fireEvent(event);
    }
    
    public static void fireEvent(int id, String beanName)
    {
        fireEvent(id, beanName, null);
    }
    
    private static String makeBeanIndex(String beanName)
    {
        return SQLUtil.makeBeanName(beanName).toLowerCase();
    }
}
