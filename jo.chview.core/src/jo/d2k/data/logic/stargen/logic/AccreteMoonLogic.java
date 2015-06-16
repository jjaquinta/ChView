package jo.d2k.data.logic.stargen.logic;

import jo.d2k.data.logic.stargen.data.PlanetType;
import jo.d2k.data.logic.stargen.data.SolidBodyBean;
import jo.util.utils.DebugUtils;

public class AccreteMoonLogic
{
    public  static boolean checkPlanetesimalCapture(double mass,
            double crit_mass, double dust_mass, double gas_mass,
            SolidBodyBean the_planet)
    {
        if (mass >= crit_mass)
            return false;
        double existing_mass = UtilsLogic.summateMasses(the_planet.getChildren());
        if ((ConstLogic.toEM(mass) < 2.5)
                && (ConstLogic.toEM(mass) > .0001)
                && (existing_mass < the_planet.getMass()*.05))
        {
            AccreteMoonLogic.capturePlanetesimalAsMoon(mass, dust_mass, gas_mass, the_planet);
            if (ConstLogic.verbosity.x0100)
                DebugUtils.trace(String.format("Moon Captured... %5.3f AU (%.2fEM) <- %.2fEM\n",
                                        the_planet.getA(),
                                        ConstLogic.toEM(the_planet.getMass()),
                                        mass*ConstLogic.SUN_MASS_IN_EARTH_MASSES));
            return true;
        }
        else
        {
            if (ConstLogic.verbosity.x0100)
                DebugUtils.trace(String.format("Moon Escapes... %5.3f AU (%.2fEM)%s %.2fEM%s\n",
                                        the_planet.getA(),
                                        the_planet.getMass()*ConstLogic.SUN_MASS_IN_EARTH_MASSES,
                                        existing_mass < (the_planet.getMass() * .05) ? "" : " (big moons)",
                                        ConstLogic.toEM(mass),
                                        ConstLogic.toEM(mass) >= 2.5 ? ", too big"
                                                : ConstLogic.toEM(mass) <= .0001 ? ", too small"
                                                        : ""));
            return false;
        }
    }


    private static void capturePlanetesimalAsMoon(double mass,
            double dust_mass, double gas_mass, SolidBodyBean the_planet)
    {
        SolidBodyBean the_moon = new SolidBodyBean();
    
        the_moon.setType(PlanetType.tUnknown);
        // the_moon.a = a;
        // the_moon.e = e;
        the_moon.setMass(mass);
        the_moon.setDustMass(dust_mass);
        the_moon.setGasMass(gas_mass);
        the_moon.getAtmosphere().clear();
        the_moon.setGasGiant(false);
        the_moon.setAlbedo(0);
        the_moon.setSurfTemp(0);
        the_moon.setHighTemp(0);
        the_moon.setLowTemp(0);
        the_moon.setMaxTemp(0);
        the_moon.setMinTemp(0);
        the_moon.setGreenhsRise(0);
        the_moon.setMinorMoons(0);
    
        if ((the_moon.getDustMass() + the_moon.getGasMass()) > (the_planet.getDustMass() + the_planet.getGasMass()))
        {
            double temp_dust = the_planet.getDustMass();
            double temp_gas = the_planet.getGasMass();
            double temp_mass = the_planet.getMass();
    
            the_planet.setDustMass(the_moon.getDustMass());
            the_planet.setGasMass(the_moon.getGasMass());
            the_planet.setMass(the_moon.getMass());
    
            the_moon.setDustMass(temp_dust);
            the_moon.setGasMass(temp_gas);
            the_moon.setMass(temp_mass);
        }

        the_planet.getChildren().add(the_moon);
    }

}
