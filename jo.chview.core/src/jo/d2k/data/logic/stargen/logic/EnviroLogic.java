package jo.d2k.data.logic.stargen.logic;

import jo.d2k.data.logic.stargen.data.GasBean;
import jo.d2k.data.logic.stargen.data.PlanetType;
import jo.d2k.data.logic.stargen.data.SolidBodyBean;
import jo.util.utils.DebugUtils;

public class EnviroLogic
{
    public static final int      NONE                 = 0;
    public static final int      BREATHABLE           = 1;
    public static final int      UNBREATHABLE         = 2;
    public static final int      POISONOUS            = 3;

    public static final String[] breathability_phrase = { "none", "breathable",
            "unbreathable", "poisonous"              };

    public static double luminosity(double mass_ratio)
    {
        double n;

        if (mass_ratio < 1.0)
            n = 1.75 * (mass_ratio - 0.1) + 3.325;
        else
            n = 0.5 * (2.0 - mass_ratio) + 4.4;
        return (Math.pow(mass_ratio, n));
    }

    // --------------------------------------------------------------------------
    // This function, given the orbital radius of a planet in AU, returns
    // the orbital 'zone' of the particle.
    // --------------------------------------------------------------------------
    public static int orb_zone(double luminosity, double orb_radius)
    {
        if (orb_radius < (4.0 * Math.sqrt(luminosity)))
            return (1);
        else if (orb_radius < (15.0 * Math.sqrt(luminosity)))
            return (2);
        else
            return (3);
    }

    // --------------------------------------------------------------------------
    // The mass is in units of solar masses, and the density is in units
    // of grams/cc. The radius returned is in units of km.
    // --------------------------------------------------------------------------

    public static double volume_radius(double mass, double density)
    {
        double volume;

        mass = mass * ConstLogic.SOLAR_MASS_IN_GRAMS;
        volume = mass / density;
        return (Math.pow((3.0 * volume) / (4.0 * Math.PI), (1.0 / 3.0)) / ConstLogic.CM_PER_KM);
    }

    // --------------------------------------------------------------------------
    // Returns the radius of the planet in kilometers.
    // The mass passed in is in units of solar masses.
    // This formula is listed as eq.9 in Fogg's article, although some typos
    // crop up in that eq. See "The Internal Constitution of Planets", by
    // Dr. D. S. Kothari, Mon. Not. of the Royal Astronomical Society, vol 96
    // pp.833-843, 1936 for the derivation. Specifically, this is Kothari's
    // eq.23, which appears on page 840.
    // --------------------------------------------------------------------------
    public static double kothari_radius(double mass, boolean giant, int zone)
    {
        double temp1;
        double temp, temp2, atomic_weight, atomic_num;

        if (zone == 1)
        {
            if (giant)
            {
                atomic_weight = 9.5;
                atomic_num = 4.5;
            }
            else
            {
                atomic_weight = 15.0;
                atomic_num = 8.0;
            }
        }
        else if (zone == 2)
        {
            if (giant)
            {
                atomic_weight = 2.47;
                atomic_num = 2.0;
            }
            else
            {
                atomic_weight = 10.0;
                atomic_num = 5.0;
            }
        }
        else
        {
            if (giant)
            {
                atomic_weight = 7.0;
                atomic_num = 4.0;
            }
            else
            {
                atomic_weight = 10.0;
                atomic_num = 5.0;
            }
        }

        temp1 = atomic_weight * atomic_num;

        temp = (2.0 * ConstLogic.BETA_20 * Math.pow(
                ConstLogic.SOLAR_MASS_IN_GRAMS, (1.0 / 3.0)))
                / (ConstLogic.A1_20 * Math.pow(temp1, (1.0 / 3.0)));

        temp2 = ConstLogic.A2_20 * Math.pow(atomic_weight, (4.0 / 3.0))
                * Math.pow(ConstLogic.SOLAR_MASS_IN_GRAMS, (2.0 / 3.0));
        temp2 = temp2 * Math.pow(mass, (2.0 / 3.0));
        temp2 = temp2 / (ConstLogic.A1_20 * ConstLogic.pow2(atomic_num));
        temp2 = 1.0 + temp2;
        temp = temp / temp2;
        temp = (temp * Math.pow(mass, (1.0 / 3.0))) / ConstLogic.CM_PER_KM;

        temp /= ConstLogic.JIMS_FUDGE; // Make Earth = actual earth

        return (temp);
    }

    // --------------------------------------------------------------------------
    // The mass passed in is in units of solar masses, and the orbital radius
    // is in units of AU. The density is returned in units of grams/cc.
    // --------------------------------------------------------------------------

    public static double empirical_density(double mass, double orb_radius,
            double r_ecosphere, boolean gas_giant)
    {
        double temp;

        temp = Math
                .pow(mass * ConstLogic.SUN_MASS_IN_EARTH_MASSES, (1.0 / 8.0));
        temp = temp * ConstLogic.pow1_4(r_ecosphere / orb_radius);
        if (gas_giant)
            return (temp * 1.2);
        else
            return (temp * 5.5);
    }

    // --------------------------------------------------------------------------
    // The mass passed in is in units of solar masses, and the equatorial
    // radius is in km. The density is returned in units of grams/cc.
    // --------------------------------------------------------------------------

    public static double volume_density(double mass, double equat_radius)
    {
        double volume;

        mass = mass * ConstLogic.SOLAR_MASS_IN_GRAMS;
        equat_radius = equat_radius * ConstLogic.CM_PER_KM;
        volume = (4.0 * Math.PI * ConstLogic.pow3(equat_radius)) / 3.0;
        return (mass / volume);
    }

    // --------------------------------------------------------------------------
    // The separation is in units of AU, and both masses are in units of solar
    // masses. The period returned is in terms of Earth days.
    // --------------------------------------------------------------------------

    public static double period(double separation, double small_mass,
            double large_mass)
    {
        double period_in_years;

        period_in_years = Math.sqrt(ConstLogic.pow3(separation)
                / (small_mass + large_mass));
        return (period_in_years * ConstLogic.DAYS_IN_A_YEAR);
    }

    // --------------------------------------------------------------------------
    // Fogg's information for this routine came from Dole "Habitable Planets
    // for Man", Blaisdell Publishing Company, NY, 1964. From this, he came
    // up with his eq.12, which is the equation for the 'base_angular_velocity'
    // below. He then used an equation for the change in angular velocity per
    // time (dw/dt) from P. Goldreich and S. Soter's paper "Q in the Solar
    // System" in Icarus, vol 5, pp.375-389 (1966). Using as a comparison the
    // change in angular velocity for the Earth, Fogg has come up with an
    // approximation for our new planet (his eq.13) and take that into account.
    // This is used to find 'change_in_angular_velocity' below.
    //
    // Input parameters are mass (in solar masses), radius (in Km), orbital
    // period (in days), orbital radius (in AU), density (in g/cc),
    // eccentricity, and whether it is a gas giant or not.
    // The length of the day is returned in units of hours.
    // --------------------------------------------------------------------------
    public static double day_length(SolidBodyBean planet)
    {
        double planetary_mass_in_grams = planet.getMass()
                * ConstLogic.SOLAR_MASS_IN_GRAMS;
        double equatorial_radius_in_cm = planet.getRadius() * ConstLogic.CM_PER_KM;
        double year_in_hours = planet.getOrbPeriod() * 24.0;
        boolean giant = (planet.getType() == PlanetType.tGasGiant
                || planet.getType() == PlanetType.tSubGasGiant || planet.getType() == PlanetType.tSubSubGasGiant);
        double k2;
        double base_angular_velocity;
        double change_in_angular_velocity;
        double ang_velocity;
        double spin_resonance_factor;
        double day_in_hours;

        boolean stopped = false;

        planet.setResonantPeriod(false); // Warning: Modify the planet

        if (giant)
            k2 = 0.24;
        else
            k2 = 0.33;

        base_angular_velocity = Math.sqrt(2.0 * ConstLogic.J
                * (planetary_mass_in_grams)
                / (k2 * ConstLogic.pow2(equatorial_radius_in_cm)));

        // This next calculation determines how much the planet's rotation is
        // slowed by the presence of the star.

        change_in_angular_velocity = ConstLogic.CHANGE_IN_EARTH_ANG_VEL
                * (planet.getDensity() / ConstLogic.EARTH_DENSITY)
                * (equatorial_radius_in_cm / ConstLogic.EARTH_RADIUS)
                * (ConstLogic.EARTH_MASS_IN_GRAMS / planetary_mass_in_grams)
                * Math.pow(planet.getSun().getMass(), 2.0)
                * (1.0 / Math.pow(planet.getA(), 6.0));
        ang_velocity = base_angular_velocity
                + (change_in_angular_velocity * planet.getSun().getAge());

        // Now we change from rad/sec to hours/rotation.

        if (ang_velocity <= 0.0)
        {
            stopped = true;
            day_in_hours = ConstLogic.INCREDIBLY_LARGE_NUMBER;
        }
        else
            day_in_hours = ConstLogic.RADIANS_PER_ROTATION
                    / (ConstLogic.SECONDS_PER_HOUR * ang_velocity);

        if ((day_in_hours >= year_in_hours) || stopped)
        {
            if (planet.getE() > 0.1)
            {
                spin_resonance_factor = (1.0 - planet.getE()) / (1.0 + planet.getE());
                planet.setResonantPeriod(true);
                return (spin_resonance_factor * year_in_hours);
            }
            else
                return (year_in_hours);
        }

        return (day_in_hours);
    }

    // --------------------------------------------------------------------------
    // The orbital radius is expected in units of Astronomical Units (AU).
    // Inclination is returned in units of degrees.
    // --------------------------------------------------------------------------
    public static int inclination(double orb_radius)
    {
        int temp;

        temp = (int)(Math.pow(orb_radius, 0.2) * UtilsLogic.about(
                ConstLogic.EARTH_AXIAL_TILT, 0.4));
        return (temp % 360);
    }

    // --------------------------------------------------------------------------
    // This function implements the escape velocity calculation. Note that
    // it appears that Fogg's eq.15 is incorrect.
    // The mass is in units of solar mass, the radius in kilometers, and the
    // velocity returned is in cm/sec.
    // --------------------------------------------------------------------------

    public static double escape_vel(double mass, double radius)
    {
        double mass_in_grams, radius_in_cm;

        mass_in_grams = mass * ConstLogic.SOLAR_MASS_IN_GRAMS;
        radius_in_cm = radius * ConstLogic.CM_PER_KM;
        return (Math.sqrt(2.0 * ConstLogic.GRAV_CONSTANT * mass_in_grams
                / radius_in_cm));
    }

    // --------------------------------------------------------------------------
    // This is Fogg's eq.16. The molecular weight (usually assumed to be N2)
    // is used as the basis of the Root Mean Square (RMS) velocity of the
    // molecule or atom. The velocity returned is in cm/sec.
    // Orbital radius is in A.U.(ie: in units of the earth's orbital radius).
    // --------------------------------------------------------------------------
    public static double rms_vel(double molecular_weight, double exospheric_temp)
    {
        return (Math.sqrt((3.0 * ConstLogic.MOLAR_GAS_CONST * exospheric_temp)
                / molecular_weight) * ConstLogic.CM_PER_METER);
    }

    // --------------------------------------------------------------------------
    // This function returns the smallest molecular weight retained by the
    // body, which is useful for determining the atmosphere composition.
    // Mass is in units of solar masses, and equatorial radius is in units of
    // kilometers.
    // --------------------------------------------------------------------------

    public static double molecule_limit(double mass, double equat_radius,
            double exospheric_temp)
    {
        double esc_velocity = escape_vel(mass, equat_radius);

        return ((3.0 * ConstLogic.MOLAR_GAS_CONST * exospheric_temp) / (ConstLogic
                .pow2((esc_velocity / ConstLogic.GAS_RETENTION_THRESHOLD)
                        / ConstLogic.CM_PER_METER)));

    }

    public static double min_molec_weight(SolidBodyBean planet)
    {
        double mass = planet.getMass();
        double radius = planet.getRadius();
        double temp = planet.getExosphericTemp();
        double target = 5.0E9;

        double guess_1 = molecule_limit(mass, radius, temp);
        double guess_2 = guess_1;

        double life = gas_life(guess_1, planet);

        int loops = 0;

        if (null != planet.getSun())
        {
            target = planet.getSun().getAge();
        }

        if (life > target)
        {
            while ((life > target) && (loops++ < 25))
            {
                guess_1 = guess_1 / 2.0;
                life = gas_life(guess_1, planet);
            }
        }
        else
        {
            while ((life < target) && (loops++ < 25))
            {
                guess_2 = guess_2 * 2.0;
                life = gas_life(guess_2, planet);
            }
        }

        loops = 0;

        while (((guess_2 - guess_1) > 0.1) && (loops++ < 25))
        {
            double guess_3 = (guess_1 + guess_2) / 2.0;
            life = gas_life(guess_3, planet);

            if (life < target)
                guess_1 = guess_3;
            else
                guess_2 = guess_3;
        }

        life = gas_life(guess_2, planet);

        return (guess_2);
    }

    // --------------------------------------------------------------------------
    // This function calculates the surface acceleration of a planet. The
    // mass is in units of solar masses, the radius in terms of km, and the
    // acceleration is returned in units of cm/sec2.
    // --------------------------------------------------------------------------
    public static double acceleration(double mass, double radius)
    {
        return (ConstLogic.GRAV_CONSTANT
                * (mass * ConstLogic.SOLAR_MASS_IN_GRAMS) / ConstLogic
                    .pow2(radius * ConstLogic.CM_PER_KM));
    }

    // --------------------------------------------------------------------------
    // This function calculates the surface gravity of a planet. The
    // acceleration is in units of cm/sec2, and the gravity is returned in
    // units of Earth gravities.
    // --------------------------------------------------------------------------
    public static double gravity(double acceleration)
    {
        return (acceleration / ConstLogic.EARTH_ACCELERATION);
    }

    // --------------------------------------------------------------------------
    // This implements Fogg's eq.17. The 'inventory' returned is unitless.
    // --------------------------------------------------------------------------

    public static void calcVolInventory(SolidBodyBean planet)
    {
        double mass = planet.getMass();
        double escape_vel = planet.getEscVelocity();
        double rms_vel = planet.getRMSVelocity();
        double stellar_mass = planet.getSun().getMass();
        int zone = planet.getOrbitZone();
        boolean greenhouse_effect = planet.isGreenhouseEffect();
        boolean accreted_gas = (planet.getGasMass() / planet.getMass()) > 0.000001;
        
        double velocity_ratio, proportion_const, temp1, temp2, earth_units;

        velocity_ratio = escape_vel / rms_vel;
        if (velocity_ratio >= ConstLogic.GAS_RETENTION_THRESHOLD)
        {
            switch (zone)
            {
                case 1:
                    proportion_const = 140000.0; // 100 . 140 JLB
                    break;
                case 2:
                    proportion_const = 75000.0;
                    break;
                case 3:
                    proportion_const = 250.0;
                    break;
                default:
                    proportion_const = 0.0;
                    System.out
                            .println("Error: orbital zone not initialized correctly!\n");
                    break;
            }
            earth_units = mass * ConstLogic.SUN_MASS_IN_EARTH_MASSES;
            temp1 = (proportion_const * earth_units) / stellar_mass;
            temp2 = UtilsLogic.about(temp1, 0.2);
            temp2 = temp1;
            if (greenhouse_effect || accreted_gas)
                planet.setVolatileGasInventory(temp2);
            else
                planet.setVolatileGasInventory(temp2 / 140.0); // 100 . 140 JLB
        }
        else
            planet.setVolatileGasInventory(0.0);
    }

    // --------------------------------------------------------------------------
    // This implements Fogg's eq.18. The pressure returned is in units of
    // millibars (mb). The gravity is in units of Earth gravities, the radius
    // in units of kilometers.
    //
    // JLB: Aparently this assumed that earth pressure = 1000mb. I've added a
    // fudge factor (EARTH_SURF_PRES_IN_MILLIBARS / 1000.) to correct for that
    // --------------------------------------------------------------------------

    public static void calcSurfPressure(SolidBodyBean planet)
    {
        double volatile_gas_inventory = planet.getVolatileGasInventory();
        double equat_radius = ConstLogic.KM_EARTH_RADIUS / planet.getRadius();
        double gravity = planet.getSurfGrav();
        double surfacePressure =  (volatile_gas_inventory * gravity
                * (ConstLogic.EARTH_SURF_PRES_IN_MILLIBARS / 1000.) / ConstLogic
                    .pow2(equat_radius));
        planet.setSurfPressure(surfacePressure);
    }

    public static void calcBoilingPoint(SolidBodyBean planet)
    {
        if ((planet.getSurfPressure() == 0.0))
            planet.setBoilPoint(0.0);
        else
        {
            double surface_pressure_in_bars = planet.getSurfPressure() / ConstLogic.MILLIBARS_PER_BAR;
            double boilingPoint = (1.0 / ((Math.log(surface_pressure_in_bars) / -5050.5) + (1.0 / 373.0)));
            planet.setBoilPoint(boilingPoint);
        }
    }

    // --------------------------------------------------------------------------
    // This function is Fogg's eq.22. Given the volatile gas inventory and
    // planetary radius of a planet (in Km), this function returns the
    // fraction of the planet covered with water.
    // I have changed the function very slightly: the fraction of Earth's
    // surface covered by water is 71%, not 75% as Fogg used.
    // --------------------------------------------------------------------------

    public static double hydro_fraction(double volatile_gas_inventory,
            double planet_radius)
    {
        double temp;

        temp = (0.71 * volatile_gas_inventory / 1000.0)
                * ConstLogic.pow2(ConstLogic.KM_EARTH_RADIUS / planet_radius);
        if (temp >= 1.0)
            return (1.0);
        else
            return (temp);
    }

    // --------------------------------------------------------------------------
    // Given the surface temperature of a planet (in Kelvin), this function
    // returns the fraction of cloud cover available. This is Fogg's eq.23.
    // See Hart in "Icarus" (vol 33, pp23 - 39, 1978) for an explanation.
    // This equation is Hart's eq.3.
    // I have modified it slightly using constants and relationships from
    // Glass's book "Introduction to Planetary Geology", p.46.
    // The 'CLOUD_COVERAGE_FACTOR' is the amount of surface area on Earth
    // covered by one Kg. of cloud.
    // --------------------------------------------------------------------------

    public static void calcCloudFraction(SolidBodyBean planet)
    {
        double smallest_MW_retained = planet.getMolecWeight();

        if (smallest_MW_retained > ConstLogic.WATER_VAPOR)
            planet.setCloudCover(0.0);
        else
        {
            double surf_area = 4.0 * Math.PI * ConstLogic.pow2(planet.getRadius());
            double hydro_mass = planet.getHydrosphere() * surf_area * ConstLogic.EARTH_WATER_MASS_PER_AREA;
            double water_vapor_in_kg = (0.00000001 * hydro_mass)
                    * Math.exp(ConstLogic.Q2_36
                            * (planet.getSurfTemp() - ConstLogic.EARTH_AVERAGE_KELVIN));
            double fraction = ConstLogic.CLOUD_COVERAGE_FACTOR * water_vapor_in_kg
                    / surf_area;
            if (fraction >= 1.0)
                planet.setCloudCover(1.0);
            else
                planet.setCloudCover(fraction);
        }
    }

    public static void calcIceFraction(SolidBodyBean planet)
    {
        double hydro_fraction = planet.getHydrosphere();
        double surf_temp = planet.getSurfTemp();

        if (surf_temp > 328.0)
            surf_temp = 328.0;
        double cover = Math.pow(((328.0 - surf_temp) / 90.0), 5.0);
        if (cover > (1.5 * hydro_fraction))
            cover = (1.5 * hydro_fraction);
        if (cover > 1.0)
            cover = 1.0;
        planet.setIceCover(cover);
    }

    // --------------------------------------------------------------------------
    // This is Fogg's eq.19. The ecosphere radius is given in AU, the orbital
    // radius in AU, and the temperature returned is in Kelvin.
    // --------------------------------------------------------------------------

    public static double eff_temp(double ecosphere_radius, double orb_radius,
            double albedo)
    {
        return (Math.sqrt(ecosphere_radius / orb_radius)
                * ConstLogic.pow1_4((1.0 - albedo)
                        / (1.0 - ConstLogic.EARTH_ALBEDO)) * ConstLogic.EARTH_EFFECTIVE_TEMP);
    }

    public static double est_temp(double ecosphere_radius, double orb_radius,
            double albedo)
    {
        return (Math.sqrt(ecosphere_radius / orb_radius)
                * ConstLogic.pow1_4((1.0 - albedo)
                        / (1.0 - ConstLogic.EARTH_ALBEDO)) * ConstLogic.EARTH_AVERAGE_KELVIN);
    }

    // --------------------------------------------------------------------------
    // Old grnhouse:
    // Note that if the orbital radius of the planet is greater than or equal
    // to R_inner, 99% of it's volatiles are assumed to have been deposited in
    // surface reservoirs (otherwise, it suffers from the greenhouse effect).
    // --------------------------------------------------------------------------
    // if ((orb_radius < r_greenhouse) && (zone == 1))

    // --------------------------------------------------------------------------
    // The new definition is based on the inital surface temperature and what
    // state water is in. If it's too hot, the water will never condense out
    // of the atmosphere, rain down and form an ocean. The albedo used here
    // was chosen so that the boundary is about the same as the old method
    // Neither zone, nor r_greenhouse are used in this version JLB
    // --------------------------------------------------------------------------

    public static boolean grnhouse(double r_ecosphere, double orb_radius)
    {
        double temp = eff_temp(r_ecosphere, orb_radius,
                ConstLogic.GREENHOUSE_TRIGGER_ALBEDO);

        if (temp > ConstLogic.FREEZING_POINT_OF_WATER)
            return (true);
        else
            return (false);
    }

    // --------------------------------------------------------------------------
    // This is Fogg's eq.20, and is also Hart's eq.20 in his "Evolution of
    // Earth's Atmosphere" article. The effective temperature given is in
    // units of Kelvin, as is the rise in temperature produced by the
    // greenhouse effect, which is returned.
    // I tuned this by changing a Math.pow(x,.25) to Math.pow(x,.4) to match
    // Venus - JLB
    // --------------------------------------------------------------------------

    public static double green_rise(double optical_depth,
            double effective_temp, double surf_pressure)
    {
        double convection_factor = ConstLogic.EARTH_CONVECTION_FACTOR
                * Math.pow(surf_pressure
                        / ConstLogic.EARTH_SURF_PRES_IN_MILLIBARS, 0.4);
        double rise = (ConstLogic.pow1_4(1.0 + 0.75 * optical_depth) - 1.0)
                * effective_temp * convection_factor;

        if (rise < 0.0)
            rise = 0.0;

        return rise;
    }

    public static void calcPlanetAlbedo(SolidBodyBean planet, double target_albedo)
    {
        double water_fraction = planet.getHydrosphere();
        double cloud_fraction = planet.getCloudCover();
        double ice_fraction = planet.getIceCover();
        double rock_fraction = 1.0 - water_fraction - ice_fraction;
        planet.setRockCover(rock_fraction);
        // factor in clouds
        water_fraction *= (1.0 - cloud_fraction);
        ice_fraction *= (1.0 - cloud_fraction);
        rock_fraction *= (1.0 - cloud_fraction);

        planet.setTerraformed(false);
        if (planet.getSurfPressure() == 0.0)
        {
            planet.setCloudAlbedo(ConstLogic.CLOUD_ALBEDO); // about(...,0.2);
            planet.setRockAlbedo(ConstLogic.ROCKY_AIRLESS_ALBEDO); // about(...,0.3);
            planet.setIceAlbedo(ConstLogic.AIRLESS_ICE_ALBEDO); // about(...,0.4);
            rock_fraction += water_fraction;
            water_fraction = 0;
            planet.setHydroAlbedo(.5); // doesn't matter
        }
        else
        {
            planet.setCloudAlbedo(ConstLogic.CLOUD_ALBEDO); // about(...,0.2);
            planet.setHydroAlbedo(ConstLogic.WATER_ALBEDO); // about(...,0.2);
            planet.setIceAlbedo(ConstLogic.ICE_ALBEDO); // about(...,0.1);
            planet.setRockAlbedo(ConstLogic.ROCKY_ALBEDO);
        }
        if (target_albedo > 0)
        {
            planet.setCloudAlbedo(terraformAlbedo(planet.getCloudAlbedo(), target_albedo));
            planet.setHydroAlbedo(terraformAlbedo(planet.getHydroAlbedo(), target_albedo));
            planet.setIceAlbedo(terraformAlbedo(planet.getIceAlbedo(), target_albedo));
            planet.setRockAlbedo(terraformAlbedo(planet.getRockAlbedo(), target_albedo));
            planet.setTerraformed(true);
        }

        double cloud_part = cloud_fraction*planet.getCloudAlbedo();
        double rock_part = rock_fraction*planet.getRockAlbedo();
        double ice_part = ice_fraction*planet.getIceAlbedo();
        double hydro_part = water_fraction*planet.getHydroAlbedo();
        planet.setAlbedo(cloud_part + rock_part + hydro_part + ice_part);
    }

    private static final double TERRAFORM_DELTA = .2;
    
    private static double terraformAlbedo(double actual, double target)
    {
        if (target < actual - TERRAFORM_DELTA)
            return actual - TERRAFORM_DELTA;
        else if (target > actual + TERRAFORM_DELTA)
            return actual + TERRAFORM_DELTA;
        else
            return target;
    }
    
    // --------------------------------------------------------------------------
    // This function returns the dimensionless quantity of optical depth,
    // which is useful in determining the amount of greenhouse effect on a
    // planet.
    // --------------------------------------------------------------------------
    public static double opacity(double molecular_weight, double surf_pressure)
    {
        double optical_depth;

        optical_depth = 0.0;
        if ((molecular_weight >= 0.0) && (molecular_weight < 10.0))
            optical_depth = optical_depth + 3.0;
        if ((molecular_weight >= 10.0) && (molecular_weight < 20.0))
            optical_depth = optical_depth + 2.34;
        if ((molecular_weight >= 20.0) && (molecular_weight < 30.0))
            optical_depth = optical_depth + 1.0;
        if ((molecular_weight >= 30.0) && (molecular_weight < 45.0))
            optical_depth = optical_depth + 0.15;
        if ((molecular_weight >= 45.0) && (molecular_weight < 100.0))
            optical_depth = optical_depth + 0.05;

        if (surf_pressure >= (70.0 * ConstLogic.EARTH_SURF_PRES_IN_MILLIBARS))
            optical_depth = optical_depth * 8.333;
        else if (surf_pressure >= (50.0 * ConstLogic.EARTH_SURF_PRES_IN_MILLIBARS))
            optical_depth = optical_depth * 6.666;
        else if (surf_pressure >= (30.0 * ConstLogic.EARTH_SURF_PRES_IN_MILLIBARS))
            optical_depth = optical_depth * 3.333;
        else if (surf_pressure >= (10.0 * ConstLogic.EARTH_SURF_PRES_IN_MILLIBARS))
            optical_depth = optical_depth * 2.0;
        else if (surf_pressure >= (5.0 * ConstLogic.EARTH_SURF_PRES_IN_MILLIBARS))
            optical_depth = optical_depth * 1.5;

        return (optical_depth);
    }

    // calculates the number of years it takes for 1/e of a gas to escape
    // from a planet's atmosphere.
    // Taken from Dole p. 34. He cites Jeans (1916) & Jones (1923)
    public static double gas_life(double molecular_weight, SolidBodyBean planet)
    {
        double v = rms_vel(molecular_weight, planet.getExosphericTemp());
        double g = planet.getSurfGrav() * ConstLogic.EARTH_ACCELERATION;
        double r = (planet.getRadius() * ConstLogic.CM_PER_KM);
        double t = (ConstLogic.pow3(v) / (2.0 * ConstLogic.pow2(g) * r))
                * Math.exp((3.0 * g * r) / ConstLogic.pow2(v));
        double years = t
                / (ConstLogic.SECONDS_PER_HOUR * 24.0 * ConstLogic.DAYS_IN_A_YEAR);

        // double ve = planet.esc_velocity;
        // double k = 2;
        // double t2 = ((k * pow3(v) * r) / pow4(ve)) * exp((3.0 * pow2(ve)) /
        // (2.0 * pow2(v)));
        // double years2 = t2 / (SECONDS_PER_HOUR * 24.0 * DAYS_IN_A_YEAR);

        // if ((StargenLogic.flag_verbose&0x0040) != 0)
        // fprintf (stderr, "gas_life: %LGs, V ratio: %Lf\n",
        // years, ve / v);

        if (years > 2.0E10)
            years = ConstLogic.INCREDIBLY_LARGE_NUMBER;

        return years;
    }

    // --------------------------------------------------------------------------
    // The temperature calculated is in degrees Kelvin.
    // Quantities already known which are used in these calculations:
    // planet.molec_weight
    // planet.surf_pressure
    // R_ecosphere
    // planet.a
    // planet.volatile_gas_inventory
    // planet.radius
    // planet.boil_point
    // --------------------------------------------------------------------------

    public static void calculate_surface_temp(SolidBodyBean planet, boolean first,
            double last_water, double last_clouds, double last_ice,
            double last_temp, double last_albedo,
            double target_albedo)
    {
        double greenhouse_temp;
        boolean boil_off = false;

        if (first)
        {
            //planet.setAlbedo(ConstLogic.EARTH_ALBEDO);
            double effective_temp = eff_temp(planet.getSun().getREcosphere(), planet.getA(), planet.getAlbedo());
            greenhouse_temp = green_rise(
                    opacity(planet.getMolecWeight(), planet.getSurfPressure()),
                    effective_temp, planet.getSurfPressure());
            planet.setSurfTemp(effective_temp + greenhouse_temp);
            set_temp_range(planet);
        }

        if (planet.isGreenhouseEffect() && planet.getMaxTemp() < planet.getBoilPoint())
        {
            if (ConstLogic.verbosity.x0010)
                DebugUtils.trace(String.format(
                        "Deluge: %s %d max (%Lf) < boil (%Lf)\n",
                        planet.getSun().getName(), planet.getPlanetNo(), planet.getMaxTemp(),
                        planet.getBoilPoint()));
            planet.setGreenhouseEffect(false);
            calcVolInventory(planet);
            calcSurfPressure(planet);
            calcBoilingPoint(planet);
        }

        planet.setHydrosphere(hydro_fraction(planet.getVolatileGasInventory(), planet.getRadius()));
        double water_raw = planet.getHydrosphere();
        calcCloudFraction(planet);
        double clouds_raw = planet.getCloudCover(); 
        calcIceFraction(planet);

        if ((planet.isGreenhouseEffect()) && (planet.getSurfPressure() > 0.0))
            planet.setCloudCover(1.0);

        if ((planet.getHighTemp() >= planet.getBoilPoint())
                && (!first)
                && !((int)planet.getDay() == (int)(planet.getOrbPeriod() * 24.0) || (planet.isResonantPeriod())))
        {
            planet.setHydrosphere(0.0);
            boil_off = true;
            if (planet.getMolecWeight() > ConstLogic.WATER_VAPOR)
                planet.setCloudCover(0.0);
            else
                planet.setCloudCover(1.0);
        }

        if (planet.getSurfTemp() < (ConstLogic.FREEZING_POINT_OF_WATER - 3.0))
            planet.setHydrosphere(0.0);

        calcPlanetAlbedo(planet, target_albedo);

        double effective_temp = eff_temp(planet.getSun().getREcosphere(), planet.getA(), planet.getAlbedo());
        greenhouse_temp = green_rise(opacity(planet.getMolecWeight(), planet.getSurfPressure()), effective_temp, planet.getSurfPressure());
        planet.setSurfTemp(effective_temp + greenhouse_temp);

        if (!first)
        {
            if (!boil_off)
                planet.setHydrosphere((planet.getHydrosphere() + (last_water * 2)) / 3);
            planet.setCloudCover((planet.getCloudCover() + (last_clouds * 2)) / 3);
            planet.setIceCover((planet.getIceCover() + (last_ice * 2)) / 3);
            planet.setAlbedo((planet.getAlbedo() + (last_albedo * 2)) / 3);
            planet.setSurfTemp((planet.getSurfTemp() + (last_temp * 2)) / 3);
        }

        set_temp_range(planet);

        if (ConstLogic.verbosity.x0020)
            System.err
                    .println(String
                            .format("%5.1Lf AU: %5.1Lf = %5.1Lf ef + %5.1Lf gh%c (W: %4.2Lf (%4.2Lf) C: %4.2Lf (%4.2Lf) I: %4.2Lf A: (%4.2Lf))\n",
                                    planet.getA(),
                                    planet.getSurfTemp()
                                            - ConstLogic.FREEZING_POINT_OF_WATER,
                                    effective_temp
                                            - ConstLogic.FREEZING_POINT_OF_WATER,
                                    greenhouse_temp,
                                    (planet.isGreenhouseEffect()) ? '*' : ' ',
                                    planet.getHydrosphere(), water_raw,
                                    planet.getCloudCover(), clouds_raw,
                                    planet.getIceCover(), planet.getAlbedo()));
    }

    public static void iterate_surface_temp(SolidBodyBean planet)
    {
        double target_albedo = -1;
        boolean terraformed = iterate_albedo(planet);
        double initial_temp = est_temp(planet.getSun().getREcosphere(), planet.getA(), planet.getAlbedo());
        if (terraformed)
            target_albedo = planet.getAlbedo();
        
        double h2_life = gas_life(ConstLogic.MOL_HYDROGEN, planet);
        double h2o_life = gas_life(ConstLogic.WATER_VAPOR, planet);
        double n2_life = gas_life(ConstLogic.MOL_NITROGEN, planet);
        double n_life = gas_life(ConstLogic.ATOMIC_NITROGEN, planet);

        if (ConstLogic.verbosity.x20000)
            System.out
                    .println(String
                            .format("%d:                     %5.1Lf it [%5.1Lf re %5.1Lf a %5.1Lf alb]\n",
                                    planet.getPlanetNo(), initial_temp,
                                    planet.getSun().getREcosphere(), planet.getA(),
                                    planet.getAlbedo()));

        if (ConstLogic.verbosity.x0040)
            System.out
                    .println(String
                            .format("\nGas lifetimes: H2 - %Lf, H2O - %Lf, N - %Lf, N2 - %Lf\n",
                                    h2_life, h2o_life, n_life, n2_life));

        calculate_surface_temp(planet, true, 0, 0, 0, 0, 0, target_albedo);

        for (int count = 0; count <= 25; count++)
        {
            double last_water = planet.getHydrosphere();
            double last_clouds = planet.getCloudCover();
            double last_ice = planet.getIceCover();
            double last_temp = planet.getSurfTemp();
            double last_albedo = planet.getAlbedo();

            calculate_surface_temp(planet, false, last_water, last_clouds, last_ice, last_temp, last_albedo, target_albedo);

            if (Math.abs(planet.getSurfTemp() - last_temp) < 0.25)
                break;
        }

        planet.setGreenhsRise(planet.getSurfTemp() - initial_temp);

        if (ConstLogic.verbosity.x20000)
            System.out
                    .println(String
                            .format("%d: %5.1Lf gh = %5.1Lf (%5.1Lf C) st - %5.1Lf it [%5.1Lf re %5.1Lf a %5.1Lf alb]\n",
                                    planet.getPlanetNo(),
                                    planet.getGreenhsRise(),
                                    planet.getSurfTemp(),
                                    planet.getSurfTemp()
                                            - ConstLogic.FREEZING_POINT_OF_WATER,
                                    initial_temp, planet.getSun().getREcosphere(),
                                    planet.getA(), planet.getAlbedo()));
    }

    private static boolean iterate_albedo(SolidBodyBean planet)
    {
        if (planet.getSurfPressure() == 0.0)
        {
            planet.setAlbedo(ConstLogic.EARTH_ALBEDO);
            return false;
        }
        double low = .99;
        double lowTemp = est_temp(planet.getSun().getREcosphere(), planet.getA(), low);
        double high = .01;
        double highTemp = est_temp(planet.getSun().getREcosphere(), planet.getA(), high);
        if ((lowTemp > ConstLogic.FREEZING_POINT_OF_WATER + 30) || (highTemp < ConstLogic.FREEZING_POINT_OF_WATER))
        {   // not worth it
            planet.setAlbedo(ConstLogic.EARTH_ALBEDO);
            return false;
        }
        double mid = 0;
        for (int i = 0; i < 25; i++)
        {
            mid = (low + high)/2;
            double temp = est_temp(planet.getSun().getREcosphere(), planet.getA(), mid);
            if (temp < ConstLogic.FREEZING_POINT_OF_WATER + 20)
                low = mid;
            else
                high = mid;
        }
        planet.setAlbedo(mid);
        planet.getProps().setProperty("terraformCandidate", "true");
        return true;
    }
    
    // --------------------------------------------------------------------------
    // Inspired partial pressure, taking into account humidification of the
    // air in the nasal passage and throat This formula is on Dole's p. 14
    // --------------------------------------------------------------------------

    public static double inspired_partial_pressure(double surf_pressure,
            double gas_pressure)
    {
        double pH2O = (ConstLogic.H20_ASSUMED_PRESSURE);
        double fraction = gas_pressure / surf_pressure;

        return (surf_pressure - pH2O) * fraction;
    }

    // --------------------------------------------------------------------------
    // This function uses figures on the maximum inspired partial pressures
    // of Oxygen, other atmospheric and traces gases as laid out on pages 15,
    // 16 and 18 of Dole's Habitable Planets for Man to derive breathability
    // of the planet's atmosphere. JLB
    // --------------------------------------------------------------------------

    public static int breathability(SolidBodyBean planet)
    {
        if (planet.getAtmosphere().size() == 0)
            return NONE;
        boolean anyOxygen = false;
        for (GasBean gas : planet.getAtmosphere())
        {
            double ipp = inspired_partial_pressure(planet.getSurfPressure(), gas.getSurfacePressure());
            if (ipp > gas.getChem().max_ipp)
                return POISONOUS;
            if (gas.getChem().num == ConstLogic.AN_O)
                anyOxygen = ((ipp >= ConstLogic.MIN_O2_IPP) && (ipp <= ConstLogic.MAX_O2_IPP));
        }
        if (anyOxygen)
            return BREATHABLE;
        else
            return UNBREATHABLE;
    }

    // function for 'soft limiting' temperatures
    public static double lim(double x)
    {
        return x / Math.sqrt(Math.sqrt(1 + x * x * x * x));
    }

    public static double soft(double v, double max, double min)
    {
        double dv = v - min;
        double dm = max - min;
        return (lim(2 * dv / dm - 1) + 1) / 2 * dm + min;
    }

    public static void set_temp_range(SolidBodyBean planet)
    {
        double pressmod = 1 / Math.sqrt(1 + 20 * planet.getSurfPressure() / 1000.0);
        double ppmod = 1 / Math.sqrt(10 + 5 * planet.getSurfPressure() / 1000.0);
        double tiltmod = Math.abs(Math.cos(planet.getAxialTilt() * Math.PI / 180)
                * Math.pow(1 + planet.getE(), 2));
        double daymod = 1 / (200 / planet.getDay() + 1);
        double mh = Math.pow(1 + daymod, pressmod);
        double ml = Math.pow(1 - daymod, pressmod);
        double hi = mh * planet.getSurfTemp();
        double lo = ml * planet.getSurfTemp();
        double sh = hi + Math.pow((100 + hi) * tiltmod, Math.sqrt(ppmod));
        double wl = lo - Math.pow((150 + lo) * tiltmod, Math.sqrt(ppmod));
        double max = planet.getSurfTemp() + Math.sqrt(planet.getSurfTemp()) * 10;
        double min = planet.getSurfTemp() / Math.sqrt(planet.getDay() + 24);

        if (lo < min)
            lo = min;
        if (wl < 0)
            wl = 0;

        planet.setHighTemp(soft(hi, max, min));
        planet.setLowTemp(soft(lo, max, min));
        planet.setMaxTemp(soft(sh, max, min));
        planet.setMinTemp(soft(wl, max, min));
    }

}
