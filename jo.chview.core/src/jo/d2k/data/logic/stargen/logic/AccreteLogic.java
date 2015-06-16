package jo.d2k.data.logic.stargen.logic;

//----------------------------------------------------------------------
//                           BIBLIOGRAPHY                               
//  Dole, Stephen H.  "Formation of Planetary Systems by Aggregation:   
//      a Computer Simulation"  October 1969,  Rand Corporation Paper   
//      P-4226.                                                         
//----------------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;

import jo.d2k.data.logic.stargen.data.Dust;
import jo.d2k.data.logic.stargen.data.PlanetType;
import jo.d2k.data.logic.stargen.data.SolidBodyBean;
import jo.util.utils.DebugUtils;

public class AccreteLogic
{
    // Now for some variables global to the accretion process:
    private static boolean    dust_left;
    private static double     r_inner;
    private static double     r_outer;
    static double     reduced_mass;
    private static double     dust_density;
    private static double     cloud_eccentricity;
    private static Dust       dust_head   = null;
    static List<SolidBodyBean>     planet_head = new ArrayList<SolidBodyBean>();

    public static void set_initial_conditions(double inner_limit_of_dust,
            double outer_limit_of_dust)
    {
        dust_head = new Dust();
        planet_head.clear();
        dust_head.next_band = null;
        dust_head.outer_edge = outer_limit_of_dust;
        dust_head.inner_edge = inner_limit_of_dust;
        dust_head.dust_present = true;
        dust_head.gas_present = true;
        dust_left = true;
        cloud_eccentricity = 0.2;
    }

    public static double stellar_dust_limit(double stell_mass_ratio)
    {
        return (200.0 * Math.pow(stell_mass_ratio, (1.0 / 3.0)));
    }

    public static double nearest_planet(double stell_mass_ratio)
    {
        return (0.3 * Math.pow(stell_mass_ratio, (1.0 / 3.0)));
    }

    public static double farthest_planet(double stell_mass_ratio)
    {
        return (50.0 * Math.pow(stell_mass_ratio, (1.0 / 3.0)));
    }

    public static double inner_effect_limit(double a, double e, double mass)
    {
        return (a * (1.0 - e) * (1.0 - mass) / (1.0 + cloud_eccentricity));
    }

    public static double outer_effect_limit(double a, double e, double mass)
    {
        return (a * (1.0 + e) * (1.0 + mass) / (1.0 - cloud_eccentricity));
    }

    public static boolean dust_available(double inside_range,
            double outside_range)
    {
        Dust current_dust_band;
        boolean dust_here;

        current_dust_band = dust_head;
        while ((current_dust_band != null)
                && (current_dust_band.outer_edge < inside_range))
            current_dust_band = current_dust_band.next_band;
        if (current_dust_band == null)
            dust_here = false;
        else
            dust_here = current_dust_band.dust_present;
        while ((current_dust_band != null)
                && (current_dust_band.inner_edge < outside_range))
        {
            dust_here = dust_here || current_dust_band.dust_present;
            current_dust_band = current_dust_band.next_band;
        }
        return (dust_here);
    }

    public static void update_dust_lanes(double min, double max, double mass,
            double crit_mass, double body_inner_bound, double body_outer_bound)
    {
        boolean gas;
        Dust node1;
        Dust node2;
        Dust node3;

        dust_left = false;
        if ((mass > crit_mass))
            gas = false;
        else
            gas = true;
        node1 = dust_head;
        while ((node1 != null))
        {
            if (((node1.inner_edge < min) && (node1.outer_edge > max)))
            {
                node2 = new Dust();
                node2.inner_edge = min;
                node2.outer_edge = max;
                if ((node1.gas_present == true))
                    node2.gas_present = gas;
                else
                    node2.gas_present = false;
                node2.dust_present = false;
                node3 = new Dust();
                node3.inner_edge = max;
                node3.outer_edge = node1.outer_edge;
                node3.gas_present = node1.gas_present;
                node3.dust_present = node1.dust_present;
                node3.next_band = node1.next_band;
                node1.next_band = node2;
                node2.next_band = node3;
                node1.outer_edge = min;
                node1 = node3.next_band;
            }
            else if (((node1.inner_edge < max) && (node1.outer_edge > max)))
            {
                node2 = new Dust();
                node2.next_band = node1.next_band;
                node2.dust_present = node1.dust_present;
                node2.gas_present = node1.gas_present;
                node2.outer_edge = node1.outer_edge;
                node2.inner_edge = max;
                node1.next_band = node2;
                node1.outer_edge = max;
                if ((node1.gas_present == true))
                    node1.gas_present = gas;
                else
                    node1.gas_present = false;
                node1.dust_present = false;
                node1 = node2.next_band;
            }
            else if (((node1.inner_edge < min) && (node1.outer_edge > min)))
            {
                node2 = new Dust();
                node2.next_band = node1.next_band;
                node2.dust_present = false;
                if ((node1.gas_present == true))
                    node2.gas_present = gas;
                else
                    node2.gas_present = false;
                node2.outer_edge = node1.outer_edge;
                node2.inner_edge = min;
                node1.next_band = node2;
                node1.outer_edge = min;
                node1 = node2.next_band;
            }
            else if (((node1.inner_edge >= min) && (node1.outer_edge <= max)))
            {
                if ((node1.gas_present == true))
                    node1.gas_present = gas;
                node1.dust_present = false;
                node1 = node1.next_band;
            }
            else if (((node1.outer_edge < min) || (node1.inner_edge > max)))
                node1 = node1.next_band;
        }
        node1 = dust_head;
        while ((node1 != null))
        {
            if (((node1.dust_present) && (((node1.outer_edge >= body_inner_bound) && (node1.inner_edge <= body_outer_bound)))))
                dust_left = true;
            node2 = node1.next_band;
            if ((node2 != null))
            {
                if (((node1.dust_present == node2.dust_present) && (node1.gas_present == node2.gas_present)))
                {
                    node1.outer_edge = node2.outer_edge;
                    node1.next_band = node2.next_band;
                    node2 = null;
                }
            }
            node1 = node1.next_band;
        }
    }

    public static double collect_dust(double last_mass, double[] new_dust,
            double[] new_gas, double a, double e, double crit_mass,
            Dust dust_band)
    {
        double mass_density;
        double temp1;
        double temp2;
        double temp;
        double temp_density;
        double bandwidth;
        double width;
        double volume;
        double gas_density = 0.0;
        double new_mass;
        double next_mass;
        double[] next_dust = new double[] { 0 };
        double[] next_gas = new double[] { 0 };

        temp = last_mass / (1.0 + last_mass);
        reduced_mass = Math.pow(temp, (1.0 / 4.0));
        r_inner = inner_effect_limit(a, e, reduced_mass);
        r_outer = outer_effect_limit(a, e, reduced_mass);

        if ((r_inner < 0.0))
            r_inner = 0.0;

        if ((dust_band == null))
            return (0.0);
        else
        {
            if ((dust_band.dust_present == false))
                temp_density = 0.0;
            else
                temp_density = dust_density;

            if (((last_mass < crit_mass) || (dust_band.gas_present == false)))
                mass_density = temp_density;
            else
            {
                mass_density = ConstLogic.K
                        * temp_density
                        / (1.0 + Math.sqrt(crit_mass / last_mass)
                                * (ConstLogic.K - 1.0));
                gas_density = mass_density - temp_density;
            }

            if (((dust_band.outer_edge <= r_inner) || (dust_band.inner_edge >= r_outer)))
            {
                return (collect_dust(last_mass, new_dust, new_gas, a, e,
                        crit_mass, dust_band.next_band));
            }
            else
            {
                bandwidth = (r_outer - r_inner);

                temp1 = r_outer - dust_band.outer_edge;
                if (temp1 < 0.0)
                    temp1 = 0.0;
                width = bandwidth - temp1;

                temp2 = dust_band.inner_edge - r_inner;
                if (temp2 < 0.0)
                    temp2 = 0.0;
                width = width - temp2;

                temp = 4.0 * Math.PI * Math.pow(a, 2.0) * reduced_mass
                        * (1.0 - e * (temp1 - temp2) / bandwidth);
                volume = temp * width;

                new_mass = volume * mass_density;
                new_gas[0] = volume * gas_density;
                new_dust[0] = new_mass - new_gas[0];

                next_mass = collect_dust(last_mass, next_dust, next_gas, a, e,
                        crit_mass, dust_band.next_band);

                new_gas[0] = new_gas[0] + next_gas[0];
                new_dust[0] = new_dust[0] + next_dust[0];

                return (new_mass + next_mass);
            }
        }
    }

    // --------------------------------------------------------------------------
    // Orbital radius is in AU, eccentricity is unitless, and the stellar
    // luminosity ratio is with respect to the sun. The value returned is the
    // mass at which the planet begins to accrete gas as well as dust, and is
    // in units of solar masses.
    // --------------------------------------------------------------------------

    public static double critical_limit(double orb_radius, double eccentricity,
            double stell_luminosity_ratio)
    {
        double temp;
        double perihelion_dist;

        perihelion_dist = (orb_radius - orb_radius * eccentricity);
        temp = perihelion_dist * Math.sqrt(stell_luminosity_ratio);
        return (ConstLogic.B * Math.pow(temp, -0.75));
    }

    public static void accrete_dust(double[] seed_mass, double[] new_dust,
            double[] new_gas, double a, double e, double crit_mass,
            double body_inner_bound, double body_outer_bound)
    {
        double new_mass = seed_mass[0];
        double temp_mass;

        do
        {
            temp_mass = new_mass;
            new_mass = collect_dust(new_mass, new_dust, new_gas, a, e,
                    crit_mass, dust_head);
        } while (!(((new_mass - temp_mass) < (0.0001 * temp_mass))));

        seed_mass[0] = seed_mass[0] + new_mass;
        update_dust_lanes(r_inner, r_outer, seed_mass[0], crit_mass, body_inner_bound, body_outer_bound);
    }

    private static double recalcEccentricity(double a, double e, double mass,
            double new_a, SolidBodyBean the_planet)
    {
        double temp = the_planet.getMass() * Math.sqrt(the_planet.getA()) * Math.sqrt(1.0 - Math.pow(the_planet.getE(), 2.0));
        temp = temp + (mass * Math.sqrt(a) * Math.sqrt(Math.sqrt(1.0 - Math.pow(e, 2.0))));
        temp = temp / ((the_planet.getMass() + mass) * Math.sqrt(new_a));
        temp = 1.0 - Math.pow(temp, 2.0);
        if (((temp < 0.0) || (temp >= 1.0)))
            temp = 0.0;
        e = Math.sqrt(temp);
        return e;
    }

    private static void promotePlanetesimalToPlanet(double a, double e,
            double mass, double crit_mass, double dust_mass, double gas_mass)
    {
        SolidBodyBean the_planet = new SolidBodyBean();

        the_planet.setType(PlanetType.tUnknown);
        the_planet.setA(a);
        the_planet.setE(e);
        the_planet.setMass(mass);
        the_planet.setDustMass(dust_mass);
        the_planet.setGasMass(gas_mass);
        the_planet.getAtmosphere().clear();
        the_planet.setAlbedo(0);
        the_planet.setSurfTemp(0);
        the_planet.setHighTemp(0);
        the_planet.setLowTemp(0);
        the_planet.setMaxTemp(0);
        the_planet.setMinTemp(0);
        the_planet.setGreenhsRise(0);
        the_planet.setMinorMoons(0);

        if (mass >= crit_mass)
            the_planet.setGasGiant(true);
        else
            the_planet.setGasGiant(false);

        for (int i = 0; i < planet_head.size(); i++)
            if (planet_head.get(i).getA() > a)
            {
                planet_head.add(i, the_planet);
                the_planet = null;
                break;
            }
        if (the_planet != null)
            planet_head.add(the_planet);
    }

    public static List<SolidBodyBean> dist_planetary_masses(double stell_mass_ratio,
            double stell_luminosity_ratio, double inner_dust,
            double outer_dust, double outer_planet_limit,
            double dust_density_coeff, List<SolidBodyBean> seed_system, boolean do_moons)
    {
        double a;
        double e;
        double[] mass = new double[1];
        double[] dust_mass = new double[1];
        double[] gas_mass = new double[1];
        double crit_mass;
        double planet_inner_bound;
        double planet_outer_bound;
        List<SolidBodyBean> seeds = seed_system;

        set_initial_conditions(inner_dust, outer_dust);
        planet_inner_bound = nearest_planet(stell_mass_ratio);

        if (outer_planet_limit == 0)
            planet_outer_bound = farthest_planet(stell_mass_ratio);
        else
            planet_outer_bound = outer_planet_limit;

        int seed = 0;
        while (dust_left)
        {
            if ((seeds != null) && (seed < seeds.size()))
            {
                a = seeds.get(seed).getA();
                e = seeds.get(seed).getE();
                seed++;
            }
            else
            {
                a = UtilsLogic.random_number(planet_inner_bound, planet_outer_bound);
                e = UtilsLogic.random_eccentricity();
            }

            mass[0] = ConstLogic.PROTOPLANET_MASS;
            dust_mass[0] = 0;
            gas_mass[0] = 0;

            if (ConstLogic.verbosity.x0200)
                DebugUtils.trace("Checking "+a+" AU.\n");

            if (dust_available(inner_effect_limit(a, e, mass[0]), outer_effect_limit(a, e, mass[0])))
            {
                if (ConstLogic.verbosity.x0100)
                    DebugUtils.trace("Injecting protoplanet at "+a+" AU.\n");

                dust_density = dust_density_coeff
                        * Math.sqrt(stell_mass_ratio)
                        * Math.exp(-ConstLogic.ALPHA
                                * Math.pow(a, (1.0 / ConstLogic.N)));
                crit_mass = critical_limit(a, e, stell_luminosity_ratio);
                accrete_dust(mass, dust_mass, gas_mass, a, e, crit_mass, planet_inner_bound, planet_outer_bound);

                dust_mass[0] += ConstLogic.PROTOPLANET_MASS;

                if (mass[0] > ConstLogic.PROTOPLANET_MASS)
                    AccreteLogic.coalesce_planetesimals(a, e, mass[0], crit_mass,
                            dust_mass[0], gas_mass[0], stell_luminosity_ratio,
                            planet_inner_bound, planet_outer_bound, do_moons);
                else if (ConstLogic.verbosity.x0100)
                    DebugUtils.trace(".. failed due to large neighbor.\n");
            }
            else if (ConstLogic.verbosity.x0200)
                DebugUtils.trace(".. failed.\n");
        }
        return (planet_head);
    }

    public static void coalesce_planetesimals(double a, double e, double mass,
            double crit_mass, double dust_mass, double gas_mass,
            double stell_luminosity_ratio, double body_inner_bound,
            double body_outer_bound, boolean do_moons)
    {   
        // First we try to find an existing planet with an over-lapping orbit.
        SolidBodyBean planet = findOverlappingPlanet(planet_head, a, e);
        if (planet != null)
        {
            double new_a = (planet.getMass() + mass) / ((planet.getMass() / planet.getA()) + (mass / a));
            e = recalcEccentricity(a, e, mass, new_a, planet);
    
            boolean finished = false;
            if (do_moons)
                finished = AccreteMoonLogic.checkPlanetesimalCapture(mass, crit_mass, dust_mass, gas_mass, planet);
    
            if (!finished)
                AccreteLogic.collidePlanetesimals(a, e, mass, crit_mass,
                        dust_mass, gas_mass, stell_luminosity_ratio,
                        body_inner_bound, body_outer_bound, planet, new_a);
        }
        else // Planetesimals didn't collide. Make it a planet.
            promotePlanetesimalToPlanet(a, e, mass, crit_mass, dust_mass, gas_mass);
    }

    static void collidePlanetesimals(double a, double e,
            double mass, double crit_mass, double dust_mass, double gas_mass,
            double stell_luminosity_ratio, double body_inner_bound,
            double body_outer_bound, SolidBodyBean the_planet, double new_a)
    {
        if (ConstLogic.verbosity.x0100)
            DebugUtils.trace(String.format("Collision between two planetesimals! %4.2f AU (%.2fEM) + %4.2f AU (%.2fEM = %.2fEMd + %.2fEMg [%.3fEM]). %5.3f AU (%5.3f)\n",
                                    the_planet.getA(),
                                    the_planet.getMass()* ConstLogic.SUN_MASS_IN_EARTH_MASSES,
                                    a,
                                    mass * ConstLogic.SUN_MASS_IN_EARTH_MASSES,
                                    dust_mass * ConstLogic.SUN_MASS_IN_EARTH_MASSES,
                                    gas_mass * ConstLogic.SUN_MASS_IN_EARTH_MASSES,
                                    crit_mass * ConstLogic.SUN_MASS_IN_EARTH_MASSES,
                                    new_a, e));
    
        double[] new_dust = new double[1];
        double[] new_gas = new double[1];
        double[] temp = new double[] { the_planet.getMass() + mass };
        accrete_dust(temp, new_dust, new_gas, new_a, e,
                stell_luminosity_ratio, body_inner_bound,
                body_outer_bound);
    
        the_planet.setA(new_a);
        the_planet.setE(e);
        the_planet.setMass(temp[0]);
        the_planet.setDustMass(the_planet.getDustMass()
                + (dust_mass + new_dust[0]));
        the_planet.setGasMass(the_planet.getGasMass()
                + (gas_mass + new_gas[0]));
        if (temp[0] >= crit_mass)
            the_planet.setGasGiant(true);
        UtilsLogic.sortByA(planet_head);
    }

    static SolidBodyBean findOverlappingPlanet(List<SolidBodyBean> planets, double a, double e)
    {
        for (SolidBodyBean p : planets)
            if (AccreteLogic.isPlanetesmialOverlap(a, e, p))
                return p;
        return null;
    }

    private static boolean isPlanetesmialOverlap(double a, double e,
            SolidBodyBean the_planet)
    {
        double diff = the_planet.getA() - a;
        double dist1;
        double dist2;
    
        if ((diff > 0.0))
        {
            dist1 = (a * (1.0 + e) * (1.0 + reduced_mass)) - a;
            // x aphelion
            reduced_mass = Math.pow((the_planet.getMass() / (1.0 + the_planet.getMass())), (1.0 / 4.0));
            dist2 = the_planet.getA() - (the_planet.getA() * (1.0 - the_planet.getE()) * (1.0 - reduced_mass));
        }
        else
        {
            dist1 = a - (a * (1.0 - e) * (1.0 - reduced_mass));
            // x perihelion
            reduced_mass = Math.pow((the_planet.getMass() / (1.0 + the_planet.getMass())), (1.0 / 4.0));
            dist2 = (the_planet.getA() * (1.0 + the_planet.getE()) * (1.0 + reduced_mass)) - the_planet.getA();
        }
        boolean overlap = (Math.abs(diff) <= Math.abs(dist1)) || (Math.abs(diff) <= Math.abs(dist2));
        return overlap;
    }
}
