package jo.d2k.data.logic.ship;

import java.util.List;

import jo.d2k.data.io.IOShipHandler;
import jo.d2k.data.logic.ApplicationLogic;
import jo.d2k.data.ship.ShipBean;

public class ShipLogic
{
    private static IOShipHandler getHandler()
    {
        return (IOShipHandler)ApplicationLogic.getHandler(ApplicationLogic.SHIP_HANDLER);
    }
    
    public static ShipBean getByID(long oid)
    {
        return getHandler().findByOID(oid);
    }
    
    public static ShipBean getByShipname(String name)
    {
        return getHandler().find("Name", name);
    }
    
    public static List<ShipBean> getByUserID(long user)
    {
        return getHandler().findMultiple("UserOID", user, "Name");
    }

    public static List<ShipBean> getAll()
    {
        return getHandler().findAll();
    }
    
    public static void deleteAll()
    {
        getHandler().deleteAll();
    }

    public static void delete(ShipBean bean)
    {
        getHandler().delete(bean);
        ModuleLogic.deleteByShipID(bean.getOID());
    }

    public static void delete(List<ShipBean> beans)
    {
        getHandler().delete(beans);
        for (ShipBean bean : beans)
            ModuleLogic.deleteByShipID(bean.getOID());
    }
    
    public static ShipBean create(String name, long userid, int coldModuleCapacity, int warmModuleCapacity)
    {
        ShipBean bean = getByShipname(name);
        if (bean != null)
            return null;
        bean = getHandler().newInstance();
        bean.setName(name);
        bean.setUserOID(userid);
        bean.setNextMove(System.currentTimeMillis() - 24*60*60*1000L);
        bean.setColdModuleCapacity(coldModuleCapacity);
        bean.setWarmModuleCapacity(warmModuleCapacity);
        getHandler().update(bean);
        return bean;
    }

    public static void updateName(ShipBean ship, String newName)
    {
        ship.setName(newName);
        getHandler().update(ship);
    }

    public static void updateWarmModuleCapacity(ShipBean ship, int warmModuleCapacity)
    {
        ship.setWarmModuleCapacity(warmModuleCapacity);
        getHandler().update(ship);
    }

    public static void updateColdModuleCapacity(ShipBean ship, int coldModuleCapacity)
    {
        ship.setColdModuleCapacity(coldModuleCapacity);
        getHandler().update(ship);
    }

    public static void updateLocation(ShipBean ship, String uri)
    {
        ship.setLocation(uri);
        getHandler().update(ship);
    }

    public static void updateHeading(ShipBean ship, String uri)
    {
        ship.setHeading(uri);
        getHandler().update(ship);
    }
    
    public static void advanceNextMove(ShipBean ship, long increment)
    {
        ship.setNextMove(ship.getNextMove() + increment);
        getHandler().update(ship);
    }
}
