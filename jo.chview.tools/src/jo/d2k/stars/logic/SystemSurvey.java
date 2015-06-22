package jo.d2k.stars.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.stargen.SystemLogic;
import jo.d2k.data.logic.stargen.data.SolidBodyBean;
import jo.d2k.data.logic.stargen.data.SunBean;
import jo.d4w.data.PopulatedObjectBean;
import jo.d4w.data.PopulatedSystemBean;
import jo.d4w.data.PopulatedWorldBean;
import jo.d4w.logic.D4WStarGenLogic;
import jo.d4w.logic.D4WStarLogic;

public class SystemSurvey
{
    public static void main(String[] argv)
    {
        double r = 25;
        System.out.println("Radius "+r);
        List<StarBean> stars = D4WStarLogic.getAllWithin(D4WStarGenLogic.GALAXY_EARTH_RADIUS, 0, 0, r).getStars();
        System.out.println("TotalStars="+stars.size());
        long totalPop = 0;
        int totalCenters = 0;
        List<Number> maxTech = new ArrayList<Number>();
        List<Number> agriProd = new ArrayList<Number>();
        List<Number> mateProd = new ArrayList<Number>();
        List<Number> enerProd = new ArrayList<Number>();
        List<Number> techTiers = new ArrayList<Number>();
        List<Number> rawEnergy= new ArrayList<Number>();
        for (int i = 0; i < stars.size(); i++)
        {
            StarBean star = stars.get(i);
            System.out.print(".");
            if ((i%80) == 79)
                System.out.println();
            if ((i%100 == 99))
                System.out.print(i);
            SunBean sun = SystemLogic.generateSystem(star);
            PopulatedSystemBean sys = jo.d4w.logic.D4WPopulationLogic.getInstance(sun);
            totalPop += sys.getPopulation();
            maxTech.add(sys.getTechTier());
            for (PopulatedObjectBean pop : sys.getPopulations())
            {
                totalCenters++;
                agriProd.add(pop.getAgriculturalProduction());
                mateProd.add(pop.getMaterialProduction());
                enerProd.add(pop.getEnergyProduction());
                techTiers.add(pop.getTechTier());
                if (pop instanceof PopulatedWorldBean)
                {
                    SolidBodyBean b = (SolidBodyBean)((PopulatedWorldBean)pop).getBody();
                    double energy = b.getSun().getLuminosity()/(b.getA()*b.getA());
                    energy = Math.log10(energy);
                    rawEnergy.add(energy);
                }
            }
        }
        System.out.println();
        System.out.println("TotalPop="+totalPop);
        System.out.println("TotalCenters="+totalCenters);
        System.out.println("Stars:");
        printFullRange("MaxTech", maxTech);
        System.out.println("Stats:");
        printFullRange("Agri", agriProd);
        printFullRange("RawMat", mateProd);
        printFullRange("Energy", enerProd);
        printFullRange("Tech", techTiers);
        //printRange("Raw Energy", rawEnergy);
    }

    private static void printFullRange(String title, List<Number> values)
    {
        printRange(title, values);
        int low = values.get(0).intValue();
        int high = values.get(values.size() - 1).intValue();
        if (high - low > 32)
            return;
        int[] spread = new int[high - low + 1];
        for (Number n : values)
            spread[n.intValue() - low]++;
        for (int i = 0; i < spread.length; i++)
            System.out.print((i + low)+":"+spread[i]+"  ");
        System.out.println();
    }
    
    private static void printRange(String title, List<Number> values)
    {
        Collections.sort(values, new Comparator<Number>() {
            @Override
            public int compare(Number object1, Number object2)
            {
                return (int)Math.signum(object1.doubleValue() - object2.doubleValue());
            }
        });
        System.out.println(title+":");
        System.out.println("Min : "+values.get(0));
        System.out.println("LowQ: "+values.get(values.size()/4));
        System.out.println("Med : "+values.get(values.size()/2));
        System.out.println("HiQ : "+values.get(values.size()*3/4));
        System.out.println("Max : "+values.get(values.size()-1));
        
    }
}
