package jo.d4w.logic;

import java.util.HashSet;
import java.util.Set;

import jo.d2k.data.logic.stargen.data.PlanetType;
import jo.d2k.data.logic.stargen.data.SolidBodyBean;
import jo.d2k.data.logic.stargen.logic.ConstLogic;

public class BodyLogic
{
    private static final String LOW_G = "Low-G";
    private static final String HIGH_G = "High-G";
    private static final String COLD = "Cold";
    private static final String COOL = "Cool";
    private static final String HOT = "Hot";
    private static final String WARM = "Warm";
    private static final String ICY = "Icy";
    private static final String AIRLESS = "Airless";
    private static final String ARID = "Arid";
    private static final String DRY = "Dry";
    private static final String WET = "Wet";
    private static final String CLOUDLESS = "Cloudless";
    private static final String FEW_CLOUDS = "Few clouds";
    private static final String CLOUDY = "Cloudy";
    private static final String BOILING_OCEAN = "Boiling ocean";
    private static final String UNBREATHABLY_THIN_ATMOSPHERE = "Unbreathably thin atmosphere";
    private static final String THIN_ATMOSPHERE = "Thin atmosphere";
    private static final String UNBREATHABLY_THICK_ATMOSPHERE = "Unbreathably thick atmosphere";
    private static final String THICK_ATMOSPHERE = "Thick atmosphere";
    private static final String NORMAL_ATMOSPHERE = "Normal atmosphere";
    private static final String EARTH_LIKE = "Earth-like";

    public static Set<String> getTags(SolidBodyBean planet)
    {
        Set<String> tags = new HashSet<String>();
        if ((planet.getType() == PlanetType.tGasGiant)
                || (planet.getType() == PlanetType.tSubGasGiant)
                || (planet.getType() == PlanetType.tSubSubGasGiant))
        {
            // Nothing, for now.
        }
        else
        {
            double rel_temp = (planet.getSurfTemp() - ConstLogic.FREEZING_POINT_OF_WATER)
                    - ConstLogic.EARTH_AVERAGE_CELSIUS;
            double seas = (planet.getHydrosphere() * 100.0);
            double clouds = (planet.getCloudCover() * 100.0);
            double atmosphere = (planet.getSurfPressure() / ConstLogic.EARTH_SURF_PRES_IN_MILLIBARS);
            double ice = (planet.getIceCover() * 100.0);
            double gravity = planet.getSurfGrav();

            if (gravity < .8)
                tags.add(LOW_G); // .8 gees
            else if (gravity > 1.2)
                tags.add(HIGH_G);

            if (rel_temp < -5.)
                tags.add(COLD); // 5 C below earth
            else if (rel_temp < -2.0)
                tags.add(COOL);
            else if (rel_temp > 7.5)
                tags.add(HOT);
            else if (rel_temp > 3.0)
                tags.add(WARM);

            if (ice > 10.)
                tags.add(ICY); // 10% surface is ice

            if (atmosphere < 0.001)
                tags.add(AIRLESS);
            else
            {
                if (planet.getType() != PlanetType.tWater)
                {
                    if (seas < 25.)
                        tags.add(ARID); // 25% surface is water
                    else if (seas < 50.)
                        tags.add(DRY);
                    else if (seas > 80.)
                        tags.add(WET);
                }

                if (clouds < 10.)
                    tags.add(CLOUDLESS);// 10% cloud cover
                else if (clouds < 40.)
                    tags.add(FEW_CLOUDS);
                else if (clouds > 80.)
                    tags.add(CLOUDY);

                if (planet.getMaxTemp() >= planet.getBoilPoint())
                    tags.add(BOILING_OCEAN);

                if (planet.getSurfPressure() < ConstLogic.MIN_O2_IPP)
                    tags.add(UNBREATHABLY_THIN_ATMOSPHERE);
                else if (atmosphere < 0.5)
                    tags.add(THIN_ATMOSPHERE);
                else if (atmosphere > ConstLogic.MAX_HABITABLE_PRESSURE / // Dole,
                                                                          // pp.
                                                                          // 18-19
                        ConstLogic.EARTH_SURF_PRES_IN_MILLIBARS)
                    tags.add(UNBREATHABLY_THICK_ATMOSPHERE);
                else if (atmosphere > 2.0)
                    tags.add(THICK_ATMOSPHERE);
                else if (planet.getType() != PlanetType.tTerrestrial)
                    tags.add(NORMAL_ATMOSPHERE);
            }

            if (tags.size() == 0)
                tags.add(EARTH_LIKE);
        }
        return tags;
    }

    public static double getHabitability(SolidBodyBean planet)
    {
        Set<String> tags = getTags(planet);
        double habitability = 1.0;
        if (tags.contains(LOW_G))
            habitability *= .9;
        if (tags.contains(HIGH_G))
            habitability *= .9;
        if (tags.contains(COOL))
            habitability *= .95;
        if (tags.contains(COLD))
            habitability *= .9;
        if (tags.contains(WARM))
            habitability *= .95;
        if (tags.contains(HOT))
            habitability *= .9;
        if (tags.contains(ICY))
            habitability *= .95;
        if (tags.contains(AIRLESS))
            habitability *= .9;
        if (tags.contains(ARID))
            habitability *= .95;
        if (tags.contains(DRY))
            habitability *= .98;
        if (tags.contains(WET))
            habitability *= .98;
        if (tags.contains(BOILING_OCEAN))
            habitability *= .95;
        if (tags.contains(UNBREATHABLY_THICK_ATMOSPHERE))
            habitability *= .9;
        if (tags.contains(UNBREATHABLY_THIN_ATMOSPHERE))
            habitability *= .9;
        if (tags.contains(THICK_ATMOSPHERE))
            habitability *= .95;
        if (tags.contains(THIN_ATMOSPHERE))
            habitability *= .95;
        return habitability;
    }
    
    // in square kilometers
    public static double getHabitableSurface(SolidBodyBean planet, int techTier)
    {
        if ((planet.getType() == PlanetType.tGasGiant)
                || (planet.getType() == PlanetType.tSubGasGiant)
                || (planet.getType() == PlanetType.tSubSubGasGiant))
            return 0;
        double surfaceArea = 4*Math.PI*planet.getRadius()*planet.getRadius();
        double waterCover = planet.getHydrosphere()*surfaceArea;
        if (techTier > 1)
            waterCover *= Math.pow(.98, techTier); // land reclamation and aquatic environments
        surfaceArea -= waterCover;
        double iceCover = planet.getHydrosphere()*surfaceArea;
        waterCover *= Math.pow(.95, techTier); // cold environment adaption
        surfaceArea -= iceCover;
        return surfaceArea;
    }
}
