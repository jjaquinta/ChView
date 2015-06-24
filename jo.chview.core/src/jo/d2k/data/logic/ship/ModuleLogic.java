package jo.d2k.data.logic.ship;

import java.util.List;

import jo.d2k.data.io.IOModuleHandler;
import jo.d2k.data.logic.ApplicationLogic;
import jo.d2k.data.ship.ModuleBean;
import jo.d2k.data.ship.ModuleSpec;

public class ModuleLogic
{
    private static IOModuleHandler getHandler()
    {
        return (IOModuleHandler)ApplicationLogic.getHandler(ApplicationLogic.MODULE_HANDLER);
    }
    
    public static ModuleBean getByID(long oid)
    {
        return getHandler().findByOID(oid);
    }
    
    public static List<ModuleBean> getByShip(long ship)
    {
        return getHandler().findMultiple("ShipOID", ship, "Type");
    }

    public static List<ModuleBean> getAll()
    {
        return getHandler().findAll();
    }
    
    public static void deleteAll()
    {
        getHandler().deleteAll();
    }

    public static void delete(ModuleBean bean)
    {
        getHandler().delete(bean);
    }

    public static void delete(List<ModuleBean> beans)
    {
        getHandler().delete(beans);
    }
    
    public static ModuleBean create(long shipOID, int type)
    {
        ModuleBean bean = getHandler().newInstance();
        bean.setShipOID(shipOID);
        bean.setType(type);
        getHandler().update(bean);
        return bean;
    }
    
    public static void updateEnablement(ModuleBean module, boolean enabled)
    {
        module.setEnabled(enabled);
        getHandler().update(module);
    }

    public static void updateEnablement(List<ModuleBean> modules, boolean enabled)
    {
        for (ModuleBean module : modules)
            module.setEnabled(enabled);
        getHandler().update(modules);
    }

    public static void updateModule(ModuleBean module)
    {
        getHandler().update(module);
    }
    
    public static long getCost(ModuleBean module)
    {
        ModuleSpec spec = ModuleSpecLogic.getSpec(module);
        // TODO: calculation
        return spec.getCost();
    }
    
    public static String getName(ModuleBean module)
    {
        ModuleSpec spec = ModuleSpecLogic.getSpec(module);
        // TODO: calculation
        return spec.getName();
    }

    public static void deleteByShipID(long oid)
    {
        List<ModuleBean> beans = getByShip(oid);
        delete(beans);
    }

    public static double getEnergyStored(ModuleBean module)
    {
        ModuleSpec spec = ModuleSpecLogic.getSpec(module);
        if (!spec.getGroup().equals(ModuleSpec.ENERGY_STORE))
            return 0;
        return module.getDParam(ModuleBean.ENERGY_STORED);
    }

    public static double getEnergyGenerationRate(ModuleBean module)
    {
        ModuleSpec spec = ModuleSpecLogic.getSpec(module);
        if (!spec.getGroup().equals(ModuleSpec.ENERGY_GENERATION))
            return 0;
        return spec.getDParam(ModuleSpec.ENERGY_PER_HOUR);
    }

    public static double getEnergyCapacity(ModuleBean module)
    {
        ModuleSpec spec = ModuleSpecLogic.getSpec(module);
        if (!spec.getGroup().equals(ModuleSpec.ENERGY_STORE))
            return 0;
        return spec.getDParam(ModuleSpec.MAX_CAPACITY);
    }

    public static void updateEnergyStored(ModuleBean module, double energy)
    {
        ModuleSpec spec = ModuleSpecLogic.getSpec(module);
        if (!spec.getGroup().equals(ModuleSpec.ENERGY_STORE))
            throw new IllegalArgumentException("You cannot store any energy in this module");
        double capacity = spec.getDParam(ModuleSpec.MAX_CAPACITY);
        double stored = module.getDParam(ModuleBean.ENERGY_STORED);
        if (stored + energy > capacity)
            throw new IllegalArgumentException("You cannot store that much energy in this module");
        module.setDParam(ModuleBean.ENERGY_STORED, stored + energy);
        getHandler().update(module);
    }

    public static double getEnergyConsumptionRate(ModuleBean module)
    {
        ModuleSpec spec = ModuleSpecLogic.getSpec(module);
        if (spec.getGroup().equals(ModuleSpec.SENSOR))
            return spec.getDParam(ModuleSpec.ENERGY_PER_HOUR);
        return 0;
    }
}
