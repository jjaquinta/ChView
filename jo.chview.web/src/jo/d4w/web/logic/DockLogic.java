package jo.d4w.web.logic;

import jo.d4w.data.CargoLot;
import jo.d4w.data.PopulatedObjectBean;
import jo.d4w.data.TradeGood;
import jo.d4w.logic.CargoLogic;
import jo.d4w.logic.D4WPopulationLogic;
import jo.d4w.logic.TradeGoodLogic;
import jo.d4w.web.data.DockCargoBean;
import jo.d4w.web.data.OnDockBean;
import jo.d4w.web.data.PortBean;
import jo.util.html.URIBuilder;
import jo.util.utils.obj.LongUtils;
import jo.util.utils.obj.StringUtils;

public class DockLogic
{

    public static OnDockBean getOnDock(URIBuilder u)
    {
        OnDockBean dock = new OnDockBean();
        dock.setURI(u.toString());
        String popURI = "pop://"+u.getAuthority() + u.getPath();
        PopulatedObjectBean port = D4WPopulationLogic.getByURI(popURI);
        String date = u.getQuery("date");
        if (date == null)
            date = "0";
        int o = date.indexOf('-');
        long endDate;
        if (o >= 0)
            endDate = LongUtils.parseLong(date.substring(0, o))*365 + LongUtils.parseLong(date.substring(o + 1));
        else
            endDate = LongUtils.parseLong(date);
        for (long startDate = endDate - CargoLogic.MAX_CARGO_AVAILABLE; startDate <= endDate; startDate++)
            for (CargoLot cargo : CargoLogic.getCargoLots(port, startDate))
                if (cargo.getDateUnAvailable() > endDate)
                    dock.getCargo().add(makeDockCargo(cargo, port));
        return dock;
    }

    private static DockCargoBean makeDockCargo(CargoLot cargo, PopulatedObjectBean pop)
    {
        TradeGood clazz = TradeGoodLogic.getTradeGood(cargo.getClassification());
        TradeGood subClazz = getSubClass(clazz);
        double value = clazz.getValueMod()*cargo.getSize()*cargo.getValueMod()*1000;
        double purchasePrice = value*TradeGoodLogic.hoursToProduce(clazz, pop);
        //System.out.println(clazz.getDescription()+": value="+clazz.getValueMod()+", hours at "+D4WPopulationLogic.getName(pop)+"="+TradeGoodLogic.hoursToProduce(clazz, pop));
        //System.out.println("where A="+pop.getAgriculturalProductivity()+" M="+pop.getMaterialProductivity()+" E="+pop.getEnergyProductivity());
        //System.out.println("cargo: value="+cargo.getValueMod()+", size="+cargo.getSize());
        //System.out.println("   value="+value+", price="+purchasePrice);
        DockCargoBean dockCargo = new DockCargoBean();
        dockCargo.setOID(cargo.getOID());
        dockCargo.setURI("inhold://"+cargo.getURI().substring(8));
        dockCargo.setClassification(cargo.getClassification());
        dockCargo.setName(subClazz.getName());
        dockCargo.setDesc(clazz.getDescription());
        dockCargo.setPort(pop.getURI());
        dockCargo.setPurchasePrice((long)purchasePrice);
        dockCargo.setSalePrice((long)purchasePrice);
        dockCargo.setSize(cargo.getSize());
        dockCargo.setValue((long)value);
        double a = clazz.getAgricultural();
        double m = clazz.getMaterial();
        double e = clazz.getEnergy();
        if (a > 0)
            a /= (a + m + e);
        if (m > 0)
            m /= (a + m + e);
        if (e > 0)
            e /= (a + m + e);
        dockCargo.setAgricultural((int)(a*100));
        dockCargo.setMaterial((int)(m*100));
        dockCargo.setEnergy((int)(e*100));
        return dockCargo;
    }

    private static TradeGood getSubClass(TradeGood clazz)
    {
        for (;;)
        {
            if ((clazz.getCategory() == null) || ((clazz.getCategory().getCategory() == null)))
                return clazz;
            clazz = clazz.getCategory();
        }
    }

    public static DockCargoBean getInHold(URIBuilder u)
    {
        String cargoURI = "cargo://" + u.toString().substring(9);
        CargoLot cargo = CargoLogic.getByURI(cargoURI);
        if (cargo == null)
            return null;
        String popURI = "pop://"+u.getAuthority() + u.getPath();
        PopulatedObjectBean port = D4WPopulationLogic.getByURI(popURI);
        if (port == null)
            return null;
        DockCargoBean dockCargo = makeDockCargo(cargo, port);
        dockCargo.setURI(u.toString());
        String portURI = u.getQuery("at");
        if (!StringUtils.isTrivial(portURI))
        {
            PortBean p = PortLogic.getPort(new URIBuilder(portURI));
            if (p != null)
            {
                TradeGood clazz = TradeGoodLogic.getTradeGood(cargo.getClassification());
                double salePrice = dockCargo.getValue()*TradeGoodLogic.hoursToProduce(clazz, p.getPopStats());
                //System.out.println("hours at "+p.getName()+"="+TradeGoodLogic.hoursToProduce(clazz, p.getPopStats()));
                //System.out.println("where A="+p.getPopStats().getAgriculturalProductivity()+" M="+p.getPopStats().getMaterialProductivity()+" E="+p.getPopStats().getEnergyProductivity());
                //System.out.println("sale price="+salePrice);
                dockCargo.setSalePrice((long)salePrice);
            }
        }
        return dockCargo;
    }

}
