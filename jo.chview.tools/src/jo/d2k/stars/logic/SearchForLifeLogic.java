package jo.d2k.stars.logic;

import java.util.List;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.StarLogic;
import jo.d2k.data.logic.stargen.SystemLogic;
import jo.d2k.data.logic.stargen.data.BodyBean;
import jo.d2k.data.logic.stargen.data.GasBean;
import jo.d2k.data.logic.stargen.data.PlanetType;
import jo.d2k.data.logic.stargen.data.SolidBodyBean;
import jo.d2k.data.logic.stargen.data.SunBean;
import jo.d2k.data.logic.stargen.logic.ConstLogic;
import jo.d2k.data.logic.stargen.logic.EnviroLogic;

public class SearchForLifeLogic
{
    public static void main(String[] argv)
    {
        double r = 50;
        System.out.println("Radius "+r);
        List<StarBean> stars = StarLogic.getAllWithin(0, 0, 0, r);
        int[] habitable = new int[4];
        int terraformed = 0;
        for (StarBean star : stars)
        {
            SunBean sun = SystemLogic.generateSystem(star);
            terraformed = scoreSystem(habitable, terraformed, star, sun, new boolean[] { false });
        }
        int total = 0;
        for (int i = 1; i < habitable.length; i++)
        {
            total += habitable[i];
            System.out.println("Score "+i+"="+habitable[i]);
        }
        System.out.println("Found "+total+" out of "+stars.size()+" systems, "+(total*100/stars.size())+"%");
        System.out.println("Terraformed="+terraformed);        
    }

    private static int scoreSystem(int[] habitable, int terraformed,
            StarBean star, BodyBean parent, boolean[] doneAny)
    {
        for (BodyBean p : parent.getChildren())
        {
            doneAny[0] = scorePlanet(star, p, habitable, doneAny[0]);
            if ((p instanceof SolidBodyBean) && ((SolidBodyBean)p).isTerraformed())
                terraformed++;
            terraformed = scoreSystem(habitable, terraformed, star, p, doneAny);
        }
        return terraformed;
    }

    private static boolean scorePlanet(StarBean star, BodyBean b,
            int[] habitable, boolean doneAny)
    {
        if (!(b instanceof SolidBodyBean))
            return false;
        SolidBodyBean p = (SolidBodyBean)b;
        int score = suitability(p);
        if (score > 0)
        {
            if (!doneAny)
            {
                doneAny = true;
                System.out.println("System: "+star.getName()+" distance="+Math.sqrt(star.getX()*star.getX() + star.getY()*star.getY() + star.getZ()*star.getZ())
                        +", x="+star.getX()+"&y="+star.getY()+"&z="+star.getZ());
            }
            System.out.println("Score="+score+" for "+p.getName());
            if (score >= 3)
                text_describe_planet(p, true);
            habitable[score]++;
        }
        return doneAny;
    }
    
    private static int suitability(SolidBodyBean p)
    {
        int score = 0;
        if ((p.getSurfTemp() >= 273-20) && (p.getSurfTemp() <= 237+40))
            score++;
        if (p.getAtmosphere().size() > 0)
            score++;
        if (p.getType() == PlanetType.tTerrestrial)
            score++;
        return score;
    }

    public static void text_describe_planet(SolidBodyBean planet, boolean do_gases)
    {
        System.out.print(String.format("Planet %s", planet.getName()));
        if (planet.isTerraformed())
            System.out.print("\tTERRAFORMED");
        System.out.print(String.format("\t"+ConstLogic.getTypeName(planet.getType())+"*\n"));
        if (planet.isTidallyLocked())
            System.out.print(String.format("Planet is tidally locked with one face to star.\n"));
        if (planet.isResonantPeriod())
            System.out.print(String.format("Planet's rotation is in a resonant spin lock with the star\n"));
        System.out.print(String.format("   Distance from primary star:\t%5.3f\tAU\n",planet.getA()));
        System.out.print(String.format("   Mass:\t\t\t%5.3f\tEarth masses\n",planet.getMass() * ConstLogic.SUN_MASS_IN_EARTH_MASSES));
        if (!planet.isGasGiant())
        {
            System.out.print(String.format("   Surface gravity:\t\t%4.2f\tEarth gees\n",planet.getSurfGrav()));
            System.out.print(String.format("   Surface pressure:\t\t%5.3f\tEarth atmospheres",(planet.getSurfPressure() / 1000.0)));
            if ((planet.isGreenhouseEffect()) && (planet.getSurfPressure() > 0.0))
                System.out.print(String.format("\tGREENHOUSE EFFECT\n"));
            else 
                System.out.print(String.format("\n"));
            System.out.print(String.format("   Surface temperature:\t\t%4.2f\tdegrees Celcius\n",
                    (planet.getSurfTemp() - ConstLogic.FREEZING_POINT_OF_WATER)));
        }
        System.out.print(String.format("   Equatorial radius:\t\t%3.1f\tKm\n",planet.getRadius()));
        System.out.print(String.format("   Density:\t\t\t%5.3f\tgrams/cc\n",planet.getDensity()));
        System.out.print(String.format("   Eccentricity of orbit:\t%5.3f\n",planet.getE()));
        System.out.print(String.format("   Escape Velocity:\t\t%4.2f\tKm/sec\n",planet.getEscVelocity() / ConstLogic.CM_PER_KM));
        System.out.print(String.format("   Molecular weight retained:\t%4.2f and above\n",planet.getMolecWeight()));
        System.out.print(String.format("   Surface acceleration:\t%4.2f\tcm/sec2\n",planet.getSurfAccel()));
        System.out.print(String.format("   Axial tilt:\t\t\t%2.0f\tdegrees\n",planet.getAxialTilt()));
        System.out.print(String.format("   Planetary albedo:\t\t%5.3f\n",planet.getAlbedo()));
        System.out.print(String.format("   Length of year:\t\t%4.2f\tdays\n",planet.getOrbPeriod()));
        System.out.print(String.format("   Length of day:\t\t%4.2f\thours\n",planet.getDay()));
        if (!planet.isGasGiant())
        {
            System.out.print(String.format("   Boiling point of water:\t%3.1f\tdegrees Celcius\n",(planet.getBoilPoint() - ConstLogic.FREEZING_POINT_OF_WATER)));
            System.out.print(String.format("   Hydrosphere percentage:\t%4.2f\n",(planet.getHydrosphere() * 100.0)));
            System.out.print(String.format("   Cloud cover percentage:\t%4.2f\n",(planet.getCloudCover() * 100)));
            System.out.print(String.format("   Ice cover percentage:\t%4.2f\n",(planet.getIceCover() * 100)));
        }
        System.out.print(String.format("\n\n"));

        if (do_gases
         && (planet.getType() != PlanetType.tGasGiant) 
         && (planet.getType() != PlanetType.tSubGasGiant) 
         && (planet.getType() != PlanetType.tSubSubGasGiant))
        {
            for (GasBean gas : planet.getAtmosphere())
            {
                boolean poisonous = false;

                if (EnviroLogic.inspired_partial_pressure(planet.getSurfPressure(),
                        gas.getSurfacePressure()) > gas.getChem().max_ipp)
                    poisonous = true;

                if (((gas.getSurfacePressure() / planet.getSurfPressure()) > .0005)
                        || poisonous)
                {
                    System.out.print(String
                            .format("%20s |"
                                    + "|%4.1f%% "
                                    + "|%5.0f mb "
                                    + "|(ipp:%5.0f)"
                                    + "| %s\n",
                                    gas.getChem().name,
                                    100. * (gas.getSurfacePressure() / planet.getSurfPressure()),
                                    gas.getSurfacePressure(),
                                    EnviroLogic
                                            .inspired_partial_pressure(
                                                    planet.getSurfPressure(),
                                                    gas.getSurfacePressure()),
                                    poisonous ? "poisonous" : ""));
                }
            }
        }
    }
}
