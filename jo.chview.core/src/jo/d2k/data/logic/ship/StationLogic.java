package jo.d2k.data.logic.ship;

import java.util.List;

import jo.d2k.data.io.IOStationHandler;
import jo.d2k.data.logic.ApplicationLogic;
import jo.d2k.data.ship.StationBean;
import jo.util.dao.sql.SQLBeanHandler2;
import jo.util.utils.obj.LongUtils;

public class StationLogic
{
    private static IOStationHandler getHandler()
    {
        return (IOStationHandler)ApplicationLogic.getHandler(ApplicationLogic.STATION_HANDLER);
    }
    
    public static StationBean getByID(long oid)
    {
        return getHandler().findByOID(oid);
    }
    
    public static StationBean getByName(String name)
    {
        return getHandler().find("Name", name);
    }
    
    public static List<StationBean> getBySystem(long system)
    {
        return getHandler().findMultiple("SystemOID", system, "Name");
    }
    
    public static List<StationBean> getByTier(int tier)
    {
        return getHandler().findMultiple("Tier", tier, "Name");
    }

    public static List<StationBean> getByTierOrGreater(int tier)
    {
        return getHandler().findMultiple("Tier", SQLBeanHandler2.GREATERTHANOREQUAL+tier, "Name");
    }

    public static List<StationBean> getAll()
    {
        return getHandler().findAll();
    }
    
    public static void deleteAll()
    {
        getHandler().deleteAll();
    }

    public static void delete(StationBean bean)
    {
        getHandler().delete(bean);
    }

    public static void delete(List<StationBean> beans)
    {
        getHandler().delete(beans);
    }
    
    public static StationBean create(String name, String uri, int tier)
    {
        StationBean bean = getByName(name);
        if (bean == null)
            bean = getHandler().newInstance();
        bean.setName(name);
        bean.setLocation(uri);
        bean.setTier(tier);
        updateSystemOID(bean);
        getHandler().update(bean);
        return bean;
    }

    public static void updateLocation(StationBean bean, String uri)
    {
        bean.setLocation(uri);
        updateSystemOID(bean);
        getHandler().update(bean);
    }

    public static void updateTier(StationBean bean, int tier)
    {
        bean.setTier(tier);
        getHandler().update(bean);
    }
    
    private static void updateSystemOID(StationBean bean)
    {
        String uri = bean.getLocation();
        int start = uri.indexOf("//");
        if (start < 0)
            throw new IllegalArgumentException("Illegal URI '"+uri+"'");
        int end = uri.indexOf("@");
        if (end < 0)
            throw new IllegalArgumentException("Illegal URI '"+uri+"'");
        String systemOID = uri.substring(start + 2, end);
        bean.setSystemOID(LongUtils.parseLong(systemOID));
    }
}
