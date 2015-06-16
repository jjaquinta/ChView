package jo.d4w.logic;

import java.util.ArrayList;
import java.util.Random;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.stargen.data.BodyBean;
import jo.d2k.data.logic.stargen.data.SolidBodyBean;
import jo.d2k.data.logic.stargen.data.SunBean;
import jo.d2k.data.logic.stargen.logic.ConstLogic;
import jo.d2k.data.logic.stargen.logic.UtilsLogic;
import jo.d4w.data.PopulatedObjectBean;
import jo.d4w.data.PopulatedStationBean;
import jo.d4w.data.PopulatedSystemBean;
import jo.d4w.data.PopulatedWorldBean;
import jo.util.beans.WeakCache;
import jo.util.html.URIBuilder;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.IntegerUtils;

public class D4WPopulationLogic
{
    private static double KELVIN = 273;
    private static double PERFECT = 10 + KELVIN;
    private static double RANGE_BAND = 20;
    
    private static double ENERGY_NONE = -3.5129112438275; 
    private static double ENERGY_MEDIUM = -1.670135555762562; 
    private static double ENERGY_ABUNDANT = 0.3032233380861304; 
    private static double ENERGY_LOW = (ENERGY_NONE+ENERGY_MEDIUM)/2;
    private static double ENERGY_HIGH = (ENERGY_ABUNDANT+ENERGY_MEDIUM)/2; 
    
    private static double MAX_EARTH_POP = 10000000000.0; // ten billion 
    
    private static WeakCache<Long, PopulatedSystemBean> mPopulationCache = new WeakCache<Long, PopulatedSystemBean>();
    
    public static PopulatedSystemBean getInstance(SunBean sun)
    {
        PopulatedSystemBean sys = mPopulationCache.get(sun.getStar().getOID());
        if (sys != null)
            return sys;
        DebugUtils.trace(sun.getName()+", OID="+sun.getOID()+", StarOID="+sun.getStar().getOID());
        Random rnd = new Random(sun.getStar().getOID());
        sys = new PopulatedSystemBean();
        sys.setOID(rnd.nextLong());
        sys.setSun(sun);
        firstPass(sys, sys.getSun(), rnd);      
        secondPass(sys, rnd);
        thirdPass(sys, rnd);
        mPopulationCache.put(sun.getStar().getOID(), sys);
        return sys;
    }
    
    private static void computeTotals(PopulatedSystemBean sys)
    {
        sys.setPopulation(0);
        for (PopulatedObjectBean pop : sys.getPopulations())
        {
            sys.setPopulation(sys.getPopulation() + pop.getPopulation());
        }
    }
    
    private static void thirdPass(PopulatedSystemBean sys, Random rnd)
    {
        String uri = "pop://" + sys.getSun().getStar().getURI().substring(7);
        String biggestName = null;
        long biggestPop = 0;
        int idx = 0;
        sys.setURI(uri + "/" + idx++);
        for (PopulatedObjectBean pop : sys.getPopulations())
        {
            String name = NameLogic.getCityName(rnd);
            if (pop.getPopulation() > biggestPop)
            {
                biggestPop = pop.getPopulation();
                biggestName = name;
            }
            if (pop instanceof PopulatedWorldBean)
                ((PopulatedWorldBean)pop).getBody().setName(name);
            else if (pop instanceof PopulatedStationBean)
                ((PopulatedStationBean)pop).setName(name);
            pop.setURI(uri + "/" + idx++);
        }
        if (biggestName != null)
            sys.getSun().getStar().setName(biggestName);
    }
    
    private static void secondPass(PopulatedSystemBean sys, Random rnd)
    {
        if (sys.getTechTier() >= PopulatedObjectBean.TECH_SPACE)
            secondPassA(sys, rnd);
        else
            secondPassB(sys);
    }
    
    private static void secondPassA(PopulatedSystemBean sys, Random rnd)
    {   // Interplanetary flight. All worlds have same tech.
        for (PopulatedObjectBean pop : sys.getPopulations())
            pop.setTechTier(sys.getTechTier());
        computeTotals(sys);
        /*
        long agriculturalSurplus = sys.getAgriculturalSupply() - sys.getAgriculturalDemand();
        //long materialSurplus = sys.getMaterialSupply() - sys.getMaterialDemand();
        //long energySurplus = sys.getEnergySupply() - sys.getEnergyDemand();
        int stationNum = 1;
        // agro orbitals
        while (agriculturalSurplus < 0)
        {
            PopulatedStationBean station = addAgriStation(sys, rnd);
            if (station == null)
                break;
            agriculturalSurplus += station.getAgriculturalSupply() - station.getAgriculturalDemand();
            //materialSurplus += station.getMaterialSupply() - station.getMaterialDemand();
            //energySurplus += station.getEnergySupply() - station.getEnergyDemand();
            addPopulation(sys, station);
            station.setName(sys.getSun().getName()+" #"+stationNum++);
        }
        // TODO: add in additional orbitals
        computeTotals(sys);
        */
    }
    
    /*
    private static PopulatedStationBean addAgriStation(PopulatedSystemBean sys, Random rnd)
    {
        PopulatedWorldBean planet = findWorldByAgriculturalDemand(sys, rnd);
        if (planet == null)
            return null;
        PopulatedStationBean station = new PopulatedStationBean();
        station.setBody(planet.getBody());
        station.setLocationType(PopulatedStationBean.ORBIT);
        station.setOrbitalRadius(planet.getBody().getRadius()*(rnd.nextInt(6)+1));
        setPopulationByRating(station, rnd, sys.getTechTier() - 3);
        station.setAgriculturalProduction(1);
        station.setMaterialProduction(-5);
        station.setEnergyProduction(-5);
        station.setTechTier(sys.getTechTier());
        computeSupplyDemand(sys, station);
        return station;
    }  
    
    private static PopulatedWorldBean findWorldByAgriculturalDemand(PopulatedSystemBean sys, Random rnd)
    {
        // find planet most in need
        List<PopulatedWorldBean> worlds = new ArrayList<PopulatedWorldBean>();
        List<Long> demand = new ArrayList<Long>();
        long totalDemand = 0;
        for (PopulatedObjectBean pop : sys.getPopulations())
            if ((pop instanceof PopulatedWorldBean) && (pop.getAgriculturalDemand() > pop.getAgriculturalSupply()))
            {
                worlds.add((PopulatedWorldBean)pop);
                long d = pop.getAgriculturalDemand() - pop.getAgriculturalSupply();
                demand.add(d);
                totalDemand += d;
            }
        if (totalDemand == 0)
            return null;
        long target = rnd.nextLong()%totalDemand;
        for (int i = 0; i < worlds.size(); i++)
        {
            target -= demand.get(i);
            if (target <= 0)
            {
                return worlds.get(i);
            }
        }
        return null;
    }
    */

    private static void secondPassB(PopulatedSystemBean sys)
    {   // No interplanetary flight. Each world is on it's own.
        computeTotals(sys);
    }
    
    private static void firstPass(PopulatedSystemBean sys, BodyBean body, Random rnd)
    {
        if (body instanceof SunBean)
            ;
        else if ((body instanceof SolidBodyBean) && !((SolidBodyBean)body).isGasGiant())
        {
            SolidBodyBean b = (SolidBodyBean)body;
            PopulatedWorldBean w = new PopulatedWorldBean();
            w.setOID(rnd.nextLong());
            w.setBody(body);
            setBaseAgriculture(w, b);
            setBaseMaterial(w, b);
            setBaseEnergy(w, b);
            setBaseTech(w, b);
            setBasePopulation(w, b, rnd);
            if (w.getPopulation() >= 1000)
            {
                addPopulation(sys, w);
                sys.setTechTier(Math.max(sys.getTechTier(), w.getTechTier()));
            }
        }
        for (BodyBean c = body.getFirstChild(); c != null; c = c.getNextBody())
            firstPass(sys, c, rnd);
    }

    private static void addPopulation(PopulatedSystemBean sys,
            PopulatedObjectBean w)
    {
        BodyBean body = null;
        if (w instanceof PopulatedWorldBean)
            body = ((PopulatedWorldBean)w).getBody();
        else if (w instanceof PopulatedStationBean)
            body = ((PopulatedStationBean)w).getBody();
        sys.getPopulations().add(w);
        if (body != null)
        {
            if (!sys.getPopulationsIndex().containsKey(body))
                sys.getPopulationsIndex().put(body, new ArrayList<PopulatedObjectBean>());
            sys.getPopulationsIndex().get(body).add(w);
        }
    }
    
    public static double getProductivityFactor(double rating, int techTier)
    {
        if (rating <= -5)
            return 0;
        double factor = 1.0;
        factor /= Math.pow(2, rating);
        if (techTier > 1)
            factor /= techTier;
        return factor;
    }

    private static void setBaseTech(PopulatedWorldBean w, SolidBodyBean b)
    {
       double tech = w.getAgriculturalProduction() + w.getMaterialProduction()*2 + w.getEnergyProduction()*3/2
                /*+ w.getManufacturedProduction()*2 + w.getScienceProduction()*2*/;
        //tech /= 2;
        if (tech < 0)
            tech = 0;
        w.setTechTier((int)(tech + .5));
    }
    
    private static void setBasePopulation(PopulatedWorldBean w, SolidBodyBean b, Random rnd)
    {
        // is viable?
        if (w.getAgriculturalProduction() <= -5)
            return;
        if (w.getMaterialProduction() <= -5)
            return;
        if (w.getEnergyProduction() <= -5)
            return;
        double hours = 8*w.getAgriculturalProductivity();
        hours += 8*w.getMaterialProductivity();
        hours += 8*w.getEnergyProductivity();
        if (hours > 16)
            return; // can't work the number of hours to break even
        w.setProductivity(hours);
        double earthArea = 4*Math.PI*ConstLogic.KM_EARTH_RADIUS*ConstLogic.KM_EARTH_RADIUS;
        double planetArea = BodyLogic.getHabitableSurface(b, w.getTechTier());
        double pop = MAX_EARTH_POP/earthArea*planetArea;
        pop *= BodyLogic.getHabitability(b);
        if (pop < 10000)
            return; // too small
        w.setPopulation((long)pop);
        /*
        int rating = w.getAgriculturalProduction() + w.getMaterialProduction() + w.getEnergyProduction();
        setPopulationByRating(w, rnd, rating);
        */
    }

    private static void setBaseEnergy(PopulatedWorldBean w, SolidBodyBean b)
    {
        double r = UtilsLogic.getDistanceToSun(b);
        double energy = b.getSun().getLuminosity()/(r*r);
        energy = Math.log10(energy);
        if (energy < ENERGY_NONE)
            w.setEnergyProduction(-2);
        else if (energy < ENERGY_LOW)
            w.setEnergyProduction(-1);
        else if (energy < ENERGY_HIGH)
            w.setEnergyProduction(0);
        else if (energy < ENERGY_ABUNDANT)
            w.setEnergyProduction(1);
        else
            w.setEnergyProduction(2);
    }

    private static void setBaseMaterial(PopulatedWorldBean w, SolidBodyBean b)
    {
        switch (b.getType())
        {
            case t1Face:
                w.setMaterialProduction(-1);
                break;
            case tAsteroids:
                w.setMaterialProduction(3);
                break;
            case tGasGiant:
                w.setMaterialProduction(-2);
                break;
            case tIce:
                w.setMaterialProduction(-1);
                break;
            case tMartian:
                w.setMaterialProduction(1);
                break;
            case tRock:
                w.setMaterialProduction(1);
                break;
            case tSubGasGiant:
                w.setMaterialProduction(-2);
                break;
            case tSubSubGasGiant:
                w.setMaterialProduction(-2);
                break;
            case tTerrestrial:
                w.setMaterialProduction(2);
                break;
            case tUnknown:
                w.setMaterialProduction(-1);
                break;
            case tVenusian:
                w.setMaterialProduction(0);
                break;
            case tWater:
                w.setMaterialProduction(1);
                break;
        }
    }

    private static void setBaseAgriculture(PopulatedWorldBean w, SolidBodyBean b)
    {
        if (b.isGasGiant() || (b.getMaxTemp() <= 0))
        {
            w.setAgriculturalProduction(-4);
            return;
        }
        double max = b.getMaxTemp();
        double min = b.getMinTemp();
        double range = max - min;
        double pcAcruued = 0;
        double production = -4;
        //DebugUtils.trace("Range: "+min+"->"+max);
        for (int band = 1; band <= 6; band++)
        {
            double timeInBand = overlap(min, max, PERFECT - RANGE_BAND*band, PERFECT + RANGE_BAND*band);
            double pcInBand = timeInBand/range;
            pcInBand -= pcAcruued;
            double prodFromBand = (7 - band)*pcInBand;
            production += prodFromBand;
            pcAcruued += pcInBand;
            //DebugUtils.trace("  band"+band+" range="+(PERFECT - RANGE_BAND*band)+"->"+(PERFECT + RANGE_BAND*band)+" overlap="+timeInBand+"/"+(int)(pcInBand*100)+" %="+(int)(pcInBand*100));
        }
        w.setAgriculturalProduction(production);
        //DebugUtils.trace("  production="+production);
    }
    
    private static double overlap(double min1, double max1, double min2, double max2)
    {
        if ((min1 >= max2) || (min2 >= max1))
            return 0;
        if ((min1 >= min2) && (max1 <= max2))   // 1 inside of 2
            return max1 - min1;
        if ((min2 >= min1) && (max2 <= max1))   // 2 inside of 1
            return max2 - min2;
        if (min1 <= min2)
            return max1 - min2;
        if (max1 >= max2)
            return max2 - min1;
        throw new IllegalStateException("Shouldn't happen 1="+min1+"->"+max1+", 2="+min2+"->"+max2); 
    }
    
    public static String getName(PopulatedObjectBean pop)
    {
        if (pop instanceof PopulatedWorldBean)
            return ((PopulatedWorldBean)pop).getBody().getName();
        else if (pop instanceof PopulatedStationBean)
            return ((PopulatedStationBean)pop).getName();
        else if (pop instanceof PopulatedSystemBean)
            return ((PopulatedSystemBean)pop).getSun().getName();
        else
            return "???";
    }

    public static PopulatedObjectBean getByURI(String uri)
    {
        URIBuilder u = new URIBuilder(uri);
        String starURI = "star://" + u.getAuthority();
        StarBean star = D4WStarLogic.getByURI(starURI);
        if (star == null)
            return null;
        SunBean sun = D4WSystemLogic.generateSystem(star);
        PopulatedSystemBean sys = getInstance(sun);
        String path = u.getPath();
        if (path.startsWith("/"))
            path = path.substring(1);
        if (Character.isDigit(path.charAt(0)))
        {
            int idx = IntegerUtils.parseInt(u.getPath().substring(1));
            if (idx == 0)
                return sys;
            if (sys.getPopulations().size() >= idx)
                return sys.getPopulations().get(idx - 1);
        }
        for (PopulatedObjectBean pop : sys.getPopulations())
            if (pop instanceof PopulatedWorldBean)
            {
                if (((PopulatedWorldBean)pop).getBody().getName().equals(path))
                    return pop;
            }
            else if (pop instanceof PopulatedStationBean)
            {
                if (((PopulatedStationBean)pop).getName().equals(path))
                    return pop;
            }
        return null;
    }
}
