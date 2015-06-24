package jo.d2k.data.logic;

import java.util.ArrayList;
import java.util.List;

import jo.d2k.data.data.LifeScenarioBean;
import jo.d2k.data.io.IOLifeScenarioHandler;
import jo.util.dao.sql.SQLBeanHandler2;

public class LifeScenarioLogic
{
    private static IOLifeScenarioHandler getHandler()
    {
        return (IOLifeScenarioHandler)ApplicationLogic.getHandler(ApplicationLogic.LIFE_SCENARIO_HANDLER);
    }
    
    public static LifeScenarioBean getByOID(long oid)
    {
        return getHandler().findByOID(oid);
    }
    
    public static LifeScenarioBean getByID(String id)
    {
        return getHandler().find("ID", id);
    }
    
    public static List<LifeScenarioBean> getActive()
    {
        return getHandler().findMultiple("Active", "1", "ID");
    }
    
    public static List<LifeScenarioBean> getByQuad(String quad)
    {
        return getHandler().findMultiple("StarQuad", quad, "ID");
    }
    
    public static List<LifeScenarioBean> getByStar(String quad, String id)
    {
        return getHandler().findMultiple("StarQuad", quad, "StarID", id, "ID");
    }
    
    public static List<LifeScenarioBean> getByPlanet(String quad, String starID, String planetID)
    {
        String[] cols = new String[] { "StarQuad", "StarID", "PlanetID" };
        String[] vals = new String[] { quad, starID, planetID };
        try
        {
            return getHandler().find(cols, vals, true, "ID");
        }
        catch (Exception e)
        {
            return new ArrayList<LifeScenarioBean>();
        }
    }
    
    public static List<LifeScenarioBean> getActiveAndChanged(long since)
    {
        return getHandler().findMultiple("Active", String.valueOf(true),
                "LastChange", SQLBeanHandler2.GREATERTHAN+since,
                "ID");
    }
    
    public static List<LifeScenarioBean> getAll()
    {
        return getHandler().findAll();
    }
    
    public static void deleteAll()
    {
        getHandler().deleteAll();
    }

    public static void delete(LifeScenarioBean bean)
    {
        getHandler().delete(bean);
    }

    public static void delete(List<LifeScenarioBean> beans)
    {
        getHandler().delete(beans);
    }
    
    public static LifeScenarioBean create(String id)
    {
        LifeScenarioBean bean = getByID(id);
        if (bean != null)
            return bean;
        bean = getHandler().newInstance();
        bean.setID(id);
        bean.setActive(false);
        bean.setLastChange(System.currentTimeMillis());
        getHandler().update(bean);
        return bean;
    }
    
    public static void update(LifeScenarioBean bean)
    {
        bean.setLastChange(System.currentTimeMillis());
        getHandler().update(bean);
    }
}
