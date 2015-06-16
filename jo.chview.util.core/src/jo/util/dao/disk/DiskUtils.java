/*
 * Created on Jun 27, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.dao.disk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jo.util.beans.Bean;

public class DiskUtils
{
    private static long UNID = System.currentTimeMillis();
    private static List<DiskBeanHandler>    SAVE_HANDLERS = new ArrayList<DiskBeanHandler>();
    private static DiskSaveThread   SAVE_THREAD = null;
    
    public static synchronized long getUniqueID()
    {
        return UNID++;
    }
    
    public static String makeBeanName(String beanName)
    {
        int o = beanName.lastIndexOf(".");
        if (o > 0)
            beanName = beanName.substring(o+1);
        return beanName;
    }
    
    public static String makeBeanName(Class<?> beanClass)
    {
        return makeBeanName(beanClass.getName());
    }
    
    public static String makeBeanBame(Bean bean)
    {
        return makeBeanName(bean.getClass());
    }
    
    public static void addToSaveList(DiskBeanHandler h)
    {
        synchronized (SAVE_HANDLERS)
        {
            SAVE_HANDLERS.add(h);
            if (SAVE_THREAD == null)
            {
                SAVE_THREAD = new DiskSaveThread();
                SAVE_THREAD.start();
            }
        }
    }
    
    public static void removeFromSaveList(DiskBeanHandler h)
    {
        synchronized (SAVE_HANDLERS)
        {
            SAVE_HANDLERS.remove(h);
        }
    }
    
    public static void saveList()
    {
        synchronized (SAVE_HANDLERS)
        {
            for (DiskBeanHandler h : SAVE_HANDLERS)
            {
                try
                {
                    h.saveBeans();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
