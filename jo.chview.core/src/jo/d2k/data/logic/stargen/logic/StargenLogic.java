package jo.d2k.data.logic.stargen.logic;

/*
 *  StarGen API
 *
 *  This file provides the main program interface to StarGen.
 *  An example of calling it is the command-line interface defined in
 *  main.c.
 *
 *  $Id: stargen.c,v 1.43 2008/12/30 23:15:13 brons Exp $
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import jo.d2k.data.logic.stargen.data.BodyBean;
import jo.d2k.data.logic.stargen.data.GasBean;
import jo.d2k.data.logic.stargen.data.GasComparator;
import jo.d2k.data.logic.stargen.data.PlanetType;
import jo.d2k.data.logic.stargen.data.SolidBodyBean;
import jo.d2k.data.logic.stargen.data.StellarSystem;
import jo.d2k.data.logic.stargen.data.SunBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

public class StargenLogic
{
    /*
     * StarGen main API
     */

    // Values of out_format
    public static final String ffHTML                       = "HTML";
    public static final String ffTEXT                       = "TEXT";

    // Values of graphic_format
    public static final String gfGIF                        = ".GIF";
    public static final String gfSVG                        = ".SVG";

    public static final String DIRSEP                       = File.separator;
    public static final String SUBDIR                       = "html" + DIRSEP;

    public static final String stargen_revision             = "$Revision: 1.43 $";

    // These are the global variables used during accretion:
    public static double       dust_density_coeff           = ConstLogic.DUST_DENSITY_COEFF;

    public static long         flag_seed                    = 0;

    public static int          earthlike                    = 0;
    public static int          total_earthlike              = 0;
    public static int          habitable                    = 0;
    public static int          habitable_jovians            = 0;
    public static int          total_habitable              = 0;

    public static double       min_breathable_terrestrial_g = 1000.0;
    public static double       min_breathable_g             = 1000.0;
    public static double       max_breathable_terrestrial_g = 0.0;
    public static double       max_breathable_g             = 0.0;
    public static double       min_breathable_temp          = 1000.0;
    public static double       max_breathable_temp          = 0.0;
    public static double       min_breathable_p             = 100000.0;
    public static double       max_breathable_p             = 0.0;
    public static double       min_breathable_terrestrial_l = 1000.0;
    public static double       min_breathable_l             = 1000.0;
    public static double       max_breathable_terrestrial_l = 0.0;
    public static double       max_breathable_l             = 0.0;
    public static double       max_moon_mass                = 0.0;

    public static int[]        type_counts                  = new int[] { 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0                   };
    public static int          type_count                   = 0;
    public static int          max_type_count               = 0;

    public static double EM(double x)
    {
        return (x) / ConstLogic.SUN_MASS_IN_EARTH_MASSES;
    }

    public static double AVE(double x, double y)
    {
        return ((x + y) / 2);
    }



    public static final Random    rnd           = new Random();

    public static void init()
    {
        initRnd(flag_seed);
    }
    public static void initRnd(long flag_seed)
    {
        if (flag_seed == 0L)
        {
            flag_seed = System.currentTimeMillis();
        }
        rnd.setSeed(flag_seed);
    }

    public static void generate_stellar_system(StellarSystem system)
    {
        initRnd(system.getRndSeed());
        double outer_dust_limit;

        if ((system.getSun().getMass() < 0.2) || (system.getSun().getMass() > 1.5))
            system.getSun().setMass(UtilsLogic.random_number(0.7, 1.4));

        outer_dust_limit = AccreteLogic.stellar_dust_limit(system.getSun().getMass());

        if (system.getSun().getLuminosity() == 0)
            system.getSun().setLuminosity(EnviroLogic.luminosity(system.getSun().getMass()));

        system.getSun().setREcosphere(Math.sqrt(system.getSun().getLuminosity()));
        system.getSun().setLife(1.0E10 * (system.getSun().getMass() / system.getSun().getLuminosity()));

        if (system.isUseSeedSystem())
        {
            system.getSun().getChildren().addAll(system.getSeedSystem());
            system.getSun().setAge(5.0E9);
        }
        else
        {
            double min_age = 1.0E9;
            double max_age = 6.0E9;

            if (system.getSun().getLife() < max_age)
                max_age = system.getSun().getLife();

            system.getSun().getChildren().addAll(AccreteLogic.dist_planetary_masses(system.getSun().getMass(),
                    system.getSun().getLuminosity(), 0.0, outer_dust_limit, system.getOuterPlanetLimit(),
                    dust_density_coeff, system.getSeedSystem(), system.isDoMoons()));

            system.getSun().setAge(UtilsLogic.random_number(min_age, max_age));
        }
        String u = "body://" + system.getSun().getStar().getURI().substring(7);
        system.getSun().setURI(u);
        UtilsLogic.stitch(system.getSun());

        StargenLogic.generate_planets(system.getSun(), !system.isUseSeedSystem(), system.getSysNo(),
                system.getSystemName(), system.isDoGases(), system.isDoMoons());
        system.getSun().setOID(1);
        StargenLogic.connect_planets(system.getSun());
    }
    
    private static void connect_planets(BodyBean body)
    {
        long oid = body.getOID()*16;
        for (BodyBean child = body.getFirstChild(); child != null; child = child.getNextBody())
        {
            child.setOID(++oid);
            child.setParent(body);
            connect_planets(child);
        }
    }

    public static void calculate_gases(SunBean sun, SolidBodyBean planet)
    {
        if (planet.getSurfPressure() <= 0)
            return;
        double[] amount = new double[ConstLogic.gases.length];
        double totamount = distributeGases(sun, planet, amount);

        if (totamount <= 0)
            return;
        assignGases(planet, amount, totamount);
    }

    private static double distributeGases(SunBean sun, SolidBodyBean planet, double[] amount)
    {
        double totamount = 0;
        double pressure = planet.getSurfPressure() / ConstLogic.MILLIBARS_PER_BAR;
        
        for (int i = 0; i < ConstLogic.gases.length; i++)
        {
            double yp = ConstLogic.gases[i].boil
                    / (373. * ((Math.log((pressure) + 0.001) / -5050.5) + (1.0 / 373.)));

            if ((yp >= 0 && yp < planet.getLowTemp())
                    && (ConstLogic.gases[i].weight >= planet.getMolecWeight()))
            {
                double vrms = EnviroLogic.rms_vel(ConstLogic.gases[i].weight,
                        planet.getExosphericTemp());
                double pvrms = Math
                        .pow(1 / (1 + vrms / planet.getEscVelocity()),
                                sun.getAge() / 1e9);
                double abund = ConstLogic.gases[i].abunds; // gases[i].abunde
                double react = 1.0;
                double fract = 1.0;
                double pres2 = 1.0;

                if (ConstLogic.gases[i].symbol.equals("Ar"))
                {
                    react = .15 * sun.getAge() / 4e9;
                }
                else if (ConstLogic.gases[i].symbol.equals("He"))
                {
                    abund = abund
                            * (0.001 + (planet.getGasMass() / planet.getMass()));
                    pres2 = (0.75 + pressure);
                    react = Math.pow(1 / (1 + ConstLogic.gases[i].reactivity), sun.getAge()
                            / 2e9 * pres2);
                }
                else if (ConstLogic.gases[i].symbol.equals("O")
                        || ConstLogic.gases[i].symbol.equals("O2") && sun.getAge() > 2e9
                        && planet.getSurfTemp() > 270 && planet.getSurfTemp() < 400)
                {
                    // pres2 = (0.65 + pressure/2); Breathable - M: .55-1.4
                    pres2 = (0.89 + pressure / 4); // Breathable - M: .6
                                                   // -1.8
                    react = Math.pow(1 / (1 + ConstLogic.gases[i].reactivity),
                            Math.pow(sun.getAge() / 2e9, 0.25) * pres2);
                }
                else if (ConstLogic.gases[i].symbol.equals("CO2") && sun.getAge() > 2e9
                        && planet.getSurfTemp() > 270 && planet.getSurfTemp() < 400)
                {
                    pres2 = (0.75 + pressure);
                    react = Math.pow(1 / (1 + ConstLogic.gases[i].reactivity),
                            Math.pow(sun.getAge() / 2e9, 0.5) * pres2);
                    react *= 1.5;
                }
                else
                {
                    pres2 = (0.75 + pressure);
                    react = Math.pow(1 / (1 + ConstLogic.gases[i].reactivity), sun.getAge()
                            / 2e9 * pres2);
                }

                fract = (1 - (planet.getMolecWeight() / ConstLogic.gases[i].weight));

                amount[i] = abund * pvrms * react * fract;

                if ((ConstLogic.verbosity.x4000)
                        && (ConstLogic.gases[i].symbol.equals("O")
                                || ConstLogic.gases[i].symbol.equals("N")
                                || ConstLogic.gases[i].symbol.equals("Ar")
                                || ConstLogic.gases[i].symbol.equals("He") || ConstLogic.gases[i].symbol
                                    .equals("CO2")))
                {
                    System.err
                            .println((planet.getMass() * ConstLogic.SUN_MASS_IN_EARTH_MASSES)
                                    + " "
                                    + ConstLogic.gases[i].symbol
                                    + ", "
                                    + amount[i]
                                    + " = a "
                                    + abund
                                    + " * p "
                                    + pvrms
                                    + " * r "
                                    + react
                                    + " * p2 "
                                    + pres2
                                    + " * f "
                                    + fract
                                    + "\t("
                                    + (100.0 * (planet.getGasMass() / planet.getMass()))
                                    + "%)");
                }

                totamount += amount[i];
            }
            else
                amount[i] = 0.0;
        }
        return totamount;
    }

    private static void assignGases(SolidBodyBean planet, double[] amount, double totamount)
    {
        planet.setAtmosphere(new ArrayList<GasBean>());
        for (int i = 0; i < ConstLogic.gases.length; i++)
        {
            if (amount[i] > 0.0)
            {
                GasBean atmosphere = new GasBean();
                planet.getAtmosphere().add(atmosphere);
                atmosphere.setChem(ConstLogic.gases[i]);
                atmosphere.setSurfacePressure(planet.getSurfPressure()
                        * amount[i] / totamount);

                if (ConstLogic.verbosity.x2000)
                {
                    if ((atmosphere.getChem().num == ConstLogic.AN_O)
                            && EnviroLogic.inspired_partial_pressure(
                                    planet.getSurfPressure(),
                                    atmosphere.getSurfacePressure()) > ConstLogic.gases[i].max_ipp)
                    {
                        DebugUtils.trace(planet.getName() + "\t Poisoned by O2");
                    }
                }
            }
        }

        Collections.sort(planet.getAtmosphere(), new GasComparator());

        if (ConstLogic.verbosity.x0010)
        {
            DebugUtils.trace("\n" + planet.getName() + " (" + planet.getA()
                    + " AU) gases:");

            for (int i = 0; i < planet.getAtmosphere().size(); i++)
            {
                System.err
                        .println(planet.getAtmosphere().get(i).getChem()
                                + ": "
                                + planet.getAtmosphere().get(i).getSurfacePressure()
                                + ", "
                                + (100. * (planet.getAtmosphere().get(i).getSurfacePressure() / planet.getSurfPressure()))
                                + "%");
            }
        }
    }

    public static void generate_planet(SolidBodyBean body, int planet_no, SunBean sun,
            boolean random_tilt, boolean do_gases,
            boolean do_moons)
    {
        body.getAtmosphere().clear();
        body.setSurfTemp(0);
        body.setHighTemp(0);
        body.setLowTemp(0);
        body.setMaxTemp(0);
        body.setMinTemp(0);
        body.setGreenhsRise(0);
        body.setPlanetNo(planet_no);
        body.setResonantPeriod(false);

        body.setOrbitZone(EnviroLogic.orb_zone(sun.getLuminosity(), body.getA()));

        body.setOrbPeriod(EnviroLogic.period(body.getA(), body.getMass(), sun.getMass()));
        if (random_tilt)
            body.setAxialTilt(EnviroLogic.inclination(body.getA()));
        body.setExosphericTemp(ConstLogic.EARTH_EXOSPHERE_TEMP
                / ConstLogic.pow2(body.getA() / sun.getREcosphere()));
        body.setRMSVelocity(EnviroLogic.rms_vel(ConstLogic.MOL_NITROGEN,
                body.getExosphericTemp()));
        body.setCoreRadius(EnviroLogic.kothari_radius(body.getDustMass(),
                false, body.getOrbitZone()));

        // Calculate the radius as a gas giant, to verify it will retain gas.
        // Then if mass > Earth, it's at least 5% gas and retains He, it's
        // some flavor of gas giant.

        body.setDensity(EnviroLogic.empirical_density(body.getMass(), body.getA(),
                sun.getREcosphere(), true));
        body.setRadius(EnviroLogic.volume_radius(body.getMass(), body.getDensity()));

        body.setSurfAccel(EnviroLogic
                .acceleration(body.getMass(), body.getRadius()));
        body.setSurfGrav(EnviroLogic.gravity(body.getSurfAccel()));

        body.setMolecWeight(EnviroLogic.min_molec_weight(body));

        if (((body.getMass() * ConstLogic.SUN_MASS_IN_EARTH_MASSES) > 1.0)
                && ((body.getGasMass() / body.getMass()) > 0.05)
                && (EnviroLogic.min_molec_weight(body) <= 4.0))
        {
            if ((body.getGasMass() / body.getMass()) < 0.20)
                body.setType(PlanetType.tSubSubGasGiant);
            else if ((body.getMass() * ConstLogic.SUN_MASS_IN_EARTH_MASSES) < 20.0)
                body.setType(PlanetType.tSubGasGiant);
            else
                body.setType(PlanetType.tGasGiant);
        }
        else
        // If not, it's rocky.
        {
            body.setRadius(EnviroLogic.kothari_radius(body.getMass(), false,
                    body.getOrbitZone()));
            body.setDensity(EnviroLogic.volume_density(body.getMass(),
                    body.getRadius()));

            body.setSurfAccel(EnviroLogic.acceleration(body.getMass(),
                    body.getRadius()));
            body.setSurfGrav(EnviroLogic.gravity(body.getSurfAccel()));

            if ((body.getGasMass() / body.getMass()) > 0.000001)
            {
                double h2_mass = body.getGasMass() * 0.85;
                double he_mass = (body.getGasMass() - h2_mass) * 0.999;

                double h2_loss = 0.0;
                double he_loss = 0.0;

                double h2_life = EnviroLogic.gas_life(ConstLogic.MOL_HYDROGEN,
                        body);
                double he_life = EnviroLogic
                        .gas_life(ConstLogic.HELIUM, body);

                if (h2_life < sun.getAge())
                {
                    h2_loss = ((1.0 - (1.0 / Math.exp(sun.getAge() / h2_life))) * h2_mass);

                    body.setGasMass(body.getGasMass() - h2_loss);
                    body.setMass(body.getMass() - h2_loss);

                    body.setSurfAccel(EnviroLogic.acceleration(body.getMass(),
                            body.getRadius()));
                    body.setSurfGrav(EnviroLogic.gravity(body.getSurfAccel()));
                }

                if (he_life < sun.getAge())
                {
                    he_loss = ((1.0 - (1.0 / Math.exp(sun.getAge() / he_life))) * he_mass);

                    body.setGasMass(body.getGasMass() - he_loss);
                    body.setMass(body.getMass() - he_loss);

                    body.setSurfAccel(EnviroLogic.acceleration(body.getMass(),
                            body.getRadius()));
                    body.setSurfGrav(EnviroLogic.gravity(body.getSurfAccel()));
                }

                if (((h2_loss + he_loss) > .000001)
                        && (ConstLogic.verbosity.x0080))
                    DebugUtils.trace(body.getName() + "\tLosing gas: H2: "
                            + (h2_loss * ConstLogic.SUN_MASS_IN_EARTH_MASSES)
                            + " EM, He: "
                            + (he_loss * ConstLogic.SUN_MASS_IN_EARTH_MASSES)
                            + " EM");
            }
        }

        body.setDay(EnviroLogic.day_length(body)); // Modifies
                                                     // planet.resonant_period
        body.setEscVelocity(EnviroLogic
                .escape_vel(body.getMass(), body.getRadius()));

        if ((body.getType() == PlanetType.tGasGiant)
                || (body.getType() == PlanetType.tSubGasGiant)
                || (body.getType() == PlanetType.tSubSubGasGiant))
        {
            body.setGreenhouseEffect(false);
            body.setVolatileGasInventory(ConstLogic.INCREDIBLY_LARGE_NUMBER);
            body.setSurfPressure(ConstLogic.INCREDIBLY_LARGE_NUMBER);

            body.setBoilPoint(ConstLogic.INCREDIBLY_LARGE_NUMBER);

            body.setSurfTemp(ConstLogic.INCREDIBLY_LARGE_NUMBER);
            body.setGreenhsRise(0);
            body.setAlbedo(UtilsLogic.about(ConstLogic.GAS_GIANT_ALBEDO, 0.1));
            body.setHydrosphere(1.0);
            body.setCloudCover(1.0);
            body.setIceCover(0.0);
            body.setSurfGrav(EnviroLogic.gravity(body.getSurfAccel()));
            body.setMolecWeight(EnviroLogic.min_molec_weight(body));
            body.setSurfGrav(ConstLogic.INCREDIBLY_LARGE_NUMBER);
            body.setEstimatedTemp(EnviroLogic.est_temp(sun.getREcosphere(),
                    body.getA(), body.getAlbedo()));
            body.setEstimatedTerrTemp(EnviroLogic.est_temp(sun.getREcosphere(),
                    body.getA(), ConstLogic.EARTH_ALBEDO));

            {
                double temp = body.getEstimatedTerrTemp();

                if ((temp >= ConstLogic.FREEZING_POINT_OF_WATER)
                        && (temp <= ConstLogic.EARTH_AVERAGE_KELVIN + 10.)
                        && (sun.getAge() > 2.0E9))
                {
                    habitable_jovians++;

                    if (ConstLogic.verbosity.x8000)
                    {
                        System.err
                                .println(body.getName()
                                        + "\t"
                                        + (body.getType() == PlanetType.tGasGiant ? "Jovian"
                                                : body.getType() == PlanetType.tSubGasGiant ? "Sub-Jovian"
                                                        : body.getType() == PlanetType.tSubSubGasGiant ? "Gas Dwarf"
                                                                : "Big")
                                        + " ("
                                        + (body.getMass() * ConstLogic.SUN_MASS_IN_EARTH_MASSES)
                                        + "EM "
                                        + (sun.getAge() / 1.0E9)
                                        + " By)"
                                        + (body.getFirstChild() == null ? ""
                                                : " WITH MOON")
                                        + " with earth-like temperature ("
                                        + (temp - ConstLogic.FREEZING_POINT_OF_WATER)
                                        + " C, "
                                        + (32 + ((temp - ConstLogic.FREEZING_POINT_OF_WATER) * 1.8))
                                        + " F, "
                                        + (temp - ConstLogic.EARTH_AVERAGE_KELVIN)
                                        + " C Earth).");
                    }
                }
            }
        }
        else
        {
            body.setEstimatedTemp(EnviroLogic.est_temp(sun.getREcosphere(),
                    body.getA(), ConstLogic.EARTH_ALBEDO));
            body.setEstimatedTerrTemp(EnviroLogic.est_temp(sun.getREcosphere(),
                    body.getA(), ConstLogic.EARTH_ALBEDO));

            body.setSurfGrav(EnviroLogic.gravity(body.getSurfAccel()));
            body.setMolecWeight(EnviroLogic.min_molec_weight(body));

            body.setGreenhouseEffect(EnviroLogic.grnhouse(sun.getREcosphere(),
                    body.getA()));
            EnviroLogic.calcVolInventory(body);
            EnviroLogic.calcSurfPressure(body);
            EnviroLogic.calcBoilingPoint(body);

            EnviroLogic.iterate_surface_temp(body); /*
                                                       * Sets: planet.surf_temp
                                                       * planet.greenhs_rise
                                                       * planet.albedo
                                                       * planet.hydrosphere
                                                       * planet.cloud_cover
                                                       * planet.ice_cover
                                                       */

            if (do_gases
                    && (body.getMaxTemp() >= ConstLogic.FREEZING_POINT_OF_WATER)
                    && (body.getMinTemp() <= body.getBoilPoint()))
                calculate_gases(sun, body);

            /*
             * Next we assign a type to the planet.
             */

            if (body.getSurfPressure() < 1.0)
            {
                if ((body instanceof SolidBodyBean)
                        && ((body.getMass() * ConstLogic.SUN_MASS_IN_EARTH_MASSES) < ConstLogic.ASTEROID_MASS_LIMIT))
                    body.setType(PlanetType.tAsteroids);
                else
                    body.setType(PlanetType.tRock);
            }
            else if ((body.getSurfPressure() > 6000.0)
                    && (body.getMolecWeight() <= 2.0)) // Retains Hydrogen
            {
                body.setType(PlanetType.tSubSubGasGiant);
                body.getAtmosphere().clear();
            }
            else
            { // Atmospheres:
                if (((int)body.getDay() == (int)(body.getOrbPeriod() * 24.0) || (body.isResonantPeriod())))
                    body.setType(PlanetType.t1Face);
                else if (body.getHydrosphere() >= 0.95)
                    body.setType(PlanetType.tWater); // >95% water
                else if (body.getIceCover() >= 0.95)
                    body.setType(PlanetType.tIce); // >95% ice
                else if (body.getHydrosphere() > 0.05)
                    body.setType(PlanetType.tTerrestrial); // Terrestrial
                // else <5% water
                else if (body.getMaxTemp() > body.getBoilPoint())
                    body.setType(PlanetType.tVenusian); // Hot = Venusian
                else if ((body.getGasMass() / body.getMass()) > 0.0001)
                { // Accreted gas
                    body.setType(PlanetType.tIce); // But no Greenhouse
                    body.setIceCover(1.0); // or liquid water
                } // Make it an Ice World
                else if (body.getSurfPressure() <= 250.0)// Thin air = Martian
                    body.setType(PlanetType.tMartian);
                else if (body.getSurfTemp() < ConstLogic.FREEZING_POINT_OF_WATER)
                    body.setType(PlanetType.tIce);
                else
                {
                    body.setType(PlanetType.tUnknown);

                    if (ConstLogic.verbosity.x0001)
                        System.err
                                .println("type_string(planet.type)+\tp="
                                        + (body.getSurfPressure())
                                        + "\tm="
                                        + (body.getMass() * ConstLogic.SUN_MASS_IN_EARTH_MASSES)
                                        + "\tg="
                                        + (body.getSurfGrav())
                                        + "\tt="
                                        + (body.getSurfTemp() - ConstLogic.EARTH_AVERAGE_KELVIN)
                                        + "\t"
                                        + (body.getName())
                                        + "\t Unknown "
                                        + (((int)body.getDay() == (int)(body.getOrbPeriod() * 24.0) || (body.isResonantPeriod())) ? "(1-Face)"
                                                : ""));
                }
            }
        }

        if (do_moons && (body instanceof SolidBodyBean))
        {
            SolidBodyBean planet = (SolidBodyBean)body;
            if (planet.getChildren().size() > 0)
            {
                for (int n = 0; n < planet.getChildren().size(); n++)
                {
                    BodyBean b = planet.getChildren().get(n);
                    if (!(b instanceof SolidBodyBean))
                        continue;
                    SolidBodyBean ptr = (SolidBodyBean)b;
                    if (ptr.getMass() * ConstLogic.SUN_MASS_IN_EARTH_MASSES > .000001)
                    {
                        double roche_limit = 0.0;
                        double hill_sphere = 0.0;

                        ptr.setA(planet.getA());
                        ptr.setE(planet.getE());

                        n++;

                        if (StringUtils.isTrivial(ptr.getName()))
                            ptr.setName(planet.getName() + "." + n);

                        generate_planet(ptr, n, sun, random_tilt,
                                do_gases, do_moons); // Adjusts
                                                           // ptr.density

                        roche_limit = 2.44
                                * planet.getRadius()
                                * Math.pow((planet.getDensity() / ptr.getDensity()),
                                        (1.0 / 3.0));
                        hill_sphere = planet.getA()
                                * ConstLogic.KM_PER_AU
                                * Math.pow((planet.getMass() / (3.0 * sun.getMass())),
                                        (1.0 / 3.0));

                        if ((roche_limit * 3.0) < hill_sphere)
                        {
                            ptr.setA(UtilsLogic.random_number(
                                    roche_limit * 1.5, hill_sphere / 2.0)
                                    / ConstLogic.KM_PER_AU);
                            ptr.setE(UtilsLogic.random_eccentricity());
                        }
                        else
                        {
                            ptr.setA(0);
                            ptr.setE(0);
                        }

                        if (ConstLogic.verbosity.x40000)
                        {
                            System.err
                                    .println("   Roche limit: R = "
                                            + planet.getRadius()
                                            + ", rM = "
                                            + planet.getDensity()
                                            + ", rm = "
                                            + ptr.getDensity()
                                            + " . "
                                            + roche_limit
                                            + " km\n"
                                            + "   Hill Sphere: a = "
                                            + (planet.getA() * ConstLogic.KM_PER_AU)
                                            + ", m = "
                                            + (planet.getMass() * ConstLogic.SOLAR_MASS_IN_KILOGRAMS)
                                            + ", M = "
                                            + (sun.getMass() * ConstLogic.SOLAR_MASS_IN_KILOGRAMS)
                                            + " . "
                                            + hill_sphere
                                            + " km\n"
                                            + ptr.getName()
                                            + ""
                                            + " Moon orbit: a = "
                                            + (ptr.getA() * ConstLogic.KM_PER_AU)
                                            + " km, e = " + ptr.getE());
                        }

                        if (ConstLogic.verbosity.x1000)
                        {
                            System.err
                                    .println("  "
                                            + planet.getName()
                                            + ": ("
                                            + (planet.getMass() * ConstLogic.SUN_MASS_IN_EARTH_MASSES)
                                            + "EM) "
                                            + n
                                            + " "
                                            + (ptr.getMass() * ConstLogic.SUN_MASS_IN_EARTH_MASSES)
                                            + "EM");
                        }
                    }
                }
            }
        }

    }

    public static void check_planet(SolidBodyBean planet, boolean is_moon)
    {
        int tIndex = ConstLogic.getTypeIndex(planet.getType());
        if (type_counts[tIndex] == 0)
            ++type_count;
        ++type_counts[tIndex];

        // Check for and list planets with breathable atmospheres

        {
            int breathe = EnviroLogic.breathability(planet);

            if ((breathe == EnviroLogic.BREATHABLE)
                    && (!planet.isResonantPeriod()) && // Option needed?
                    ((int)planet.getDay() != (int)(planet.getOrbPeriod() * 24.0)))
            {
                boolean list_it = false;
                double illumination = ConstLogic.pow2(1.0 / planet.getA())
                        * (planet.getSun()).getLuminosity();

                habitable++;

                if (min_breathable_temp > planet.getSurfTemp())
                {
                    min_breathable_temp = planet.getSurfTemp();

                    if (ConstLogic.verbosity.x0002)
                        list_it = true;
                }

                if (max_breathable_temp < planet.getSurfTemp())
                {
                    max_breathable_temp = planet.getSurfTemp();

                    if (ConstLogic.verbosity.x0002)
                        list_it = true;
                }

                if (min_breathable_g > planet.getSurfGrav())
                {
                    min_breathable_g = planet.getSurfGrav();

                    if (ConstLogic.verbosity.x0002)
                        list_it = true;
                }

                if (max_breathable_g < planet.getSurfGrav())
                {
                    max_breathable_g = planet.getSurfGrav();

                    if (ConstLogic.verbosity.x0002)
                        list_it = true;
                }

                if (min_breathable_l > illumination)
                {
                    min_breathable_l = illumination;

                    if (ConstLogic.verbosity.x0002)
                        list_it = true;
                }

                if (max_breathable_l < illumination)
                {
                    max_breathable_l = illumination;

                    if (ConstLogic.verbosity.x0002)
                        list_it = true;
                }

                if (planet.getType() == PlanetType.tTerrestrial)
                {
                    if (min_breathable_terrestrial_g > planet.getSurfGrav())
                    {
                        min_breathable_terrestrial_g = planet.getSurfGrav();

                        if (ConstLogic.verbosity.x0002)
                            list_it = true;
                    }

                    if (max_breathable_terrestrial_g < planet.getSurfGrav())
                    {
                        max_breathable_terrestrial_g = planet.getSurfGrav();

                        if (ConstLogic.verbosity.x0002)
                            list_it = true;
                    }

                    if (min_breathable_terrestrial_l > illumination)
                    {
                        min_breathable_terrestrial_l = illumination;

                        if (ConstLogic.verbosity.x0002)
                            list_it = true;
                    }

                    if (max_breathable_terrestrial_l < illumination)
                    {
                        max_breathable_terrestrial_l = illumination;

                        if (ConstLogic.verbosity.x0002)
                            list_it = true;
                    }
                }

                if (min_breathable_p > planet.getSurfPressure())
                {
                    min_breathable_p = planet.getSurfPressure();

                    if (ConstLogic.verbosity.x0002)
                        list_it = true;
                }

                if (max_breathable_p < planet.getSurfPressure())
                {
                    max_breathable_p = planet.getSurfPressure();

                    if (ConstLogic.verbosity.x0002)
                        list_it = true;
                }

                if (ConstLogic.verbosity.x0004)
                    list_it = true;

                if (list_it)
                    System.err
                            .println("%12s\tp=%4.2Lf\tm=%4.2Lf\tg=%4.2Lf\tt=%+.1Lf\tl=%4.2Lf\t%s\n"/*
                                                                                                    * ,
                                                                                                    * type_string
                                                                                                    * (
                                                                                                    * planet
                                                                                                    * .
                                                                                                    * type
                                                                                                    * )
                                                                                                    * ,
                                                                                                    * planet
                                                                                                    * .
                                                                                                    * surf_pressure
                                                                                                    * ,
                                                                                                    * planet
                                                                                                    * .
                                                                                                    * mass
                                                                                                    * *
                                                                                                    * ConstLogic
                                                                                                    * .
                                                                                                    * SUN_MASS_IN_EARTH_MASSES
                                                                                                    * ,
                                                                                                    * planet
                                                                                                    * .
                                                                                                    * surf_grav
                                                                                                    * ,
                                                                                                    * planet
                                                                                                    * .
                                                                                                    * surf_temp
                                                                                                    * -
                                                                                                    * ConstLogic
                                                                                                    * .
                                                                                                    * EARTH_AVERAGE_KELVIN
                                                                                                    * ,
                                                                                                    * illumination
                                                                                                    * ,
                                                                                                    * planet.id
                                                                                                    */);
            }
        }

        if (is_moon && max_moon_mass < planet.getMass())
        {
            max_moon_mass = planet.getMass();

            if (ConstLogic.verbosity.x0002)
                System.err
                        .println("%12s\tp=%4.2Lf\tm=%4.2Lf\tg=%4.2Lf\tt=%+.1Lf\t%s Moon Mass\n"/*
                                                                                                * ,
                                                                                                * type_string
                                                                                                * (
                                                                                                * planet
                                                                                                * .
                                                                                                * type
                                                                                                * )
                                                                                                * ,
                                                                                                * planet
                                                                                                * .
                                                                                                * surf_pressure
                                                                                                * ,
                                                                                                * planet
                                                                                                * .
                                                                                                * mass
                                                                                                * *
                                                                                                * ConstLogic
                                                                                                * .
                                                                                                * SUN_MASS_IN_EARTH_MASSES
                                                                                                * ,
                                                                                                * planet
                                                                                                * .
                                                                                                * surf_grav
                                                                                                * ,
                                                                                                * planet
                                                                                                * .
                                                                                                * surf_temp
                                                                                                * -
                                                                                                * ConstLogic
                                                                                                * .
                                                                                                * EARTH_AVERAGE_KELVIN
                                                                                                * ,
                                                                                                * planet.id
                                                                                                */);
        }

        if ((ConstLogic.verbosity.x0800)
                && (planet.getDustMass() * ConstLogic.SUN_MASS_IN_EARTH_MASSES >= 0.0006)
                && (planet.getGasMass() * ConstLogic.SUN_MASS_IN_EARTH_MASSES >= 0.0006)
                && (planet.getType() != PlanetType.tGasGiant)
                && (planet.getType() != PlanetType.tSubGasGiant))
        {
            int core_size = (int)((50. * planet.getCoreRadius()) / planet.getRadius());

            if (core_size <= 49)
            {
                System.err
                        .println("%12s\tp=%4.2Lf\tr=%4.2Lf\tm=%4.2Lf\t%s\t%d\n"/*
                                                                                * ,
                                                                                * type_string
                                                                                * (
                                                                                * planet
                                                                                * .
                                                                                * type
                                                                                * )
                                                                                * ,
                                                                                * planet
                                                                                * .
                                                                                * core_radius
                                                                                * ,
                                                                                * planet
                                                                                * .
                                                                                * radius
                                                                                * ,
                                                                                * planet
                                                                                * .
                                                                                * mass
                                                                                * *
                                                                                * ConstLogic
                                                                                * .
                                                                                * SUN_MASS_IN_EARTH_MASSES
                                                                                * ,
                                                                                * planet.id
                                                                                * ,
                                                                                * 50
                                                                                * -
                                                                                * core_size
                                                                                */);
            }
        }

        {
            double rel_temp = (planet.getSurfTemp() - ConstLogic.FREEZING_POINT_OF_WATER)
                    - ConstLogic.EARTH_AVERAGE_CELSIUS;
            double seas = (planet.getHydrosphere() * 100.0);
            double clouds = (planet.getCloudCover() * 100.0);
            double pressure = (planet.getSurfPressure() / ConstLogic.EARTH_SURF_PRES_IN_MILLIBARS);
            double ice = (planet.getIceCover() * 100.0);
            double gravity = planet.getSurfGrav();
            int breathe = EnviroLogic.breathability(planet);

            if ((gravity >= .8) && (gravity <= 1.2) && (rel_temp >= -2.0)
                    && (rel_temp <= 3.0) && (ice <= 10.) && (pressure >= 0.5)
                    && (pressure <= 2.0) && (clouds >= 40.) && (clouds <= 80.)
                    && (seas >= 50.) && (seas <= 80.)
                    && (planet.getType() != PlanetType.tWater)
                    && (breathe == EnviroLogic.BREATHABLE))
            {
                earthlike++;

                if (ConstLogic.verbosity.x0008)
                    System.err
                            .println("%12s\tp=%4.2Lf\tm=%4.2Lf\tg=%4.2Lf\tt=%+.1Lf\t%d %s\tEarth-like\n"/*
                                                                                                         * ,
                                                                                                         * type_string
                                                                                                         * (
                                                                                                         * planet
                                                                                                         * .
                                                                                                         * type
                                                                                                         * )
                                                                                                         * ,
                                                                                                         * planet
                                                                                                         * .
                                                                                                         * surf_pressure
                                                                                                         * ,
                                                                                                         * planet
                                                                                                         * .
                                                                                                         * mass
                                                                                                         * *
                                                                                                         * ConstLogic
                                                                                                         * .
                                                                                                         * SUN_MASS_IN_EARTH_MASSES
                                                                                                         * ,
                                                                                                         * planet
                                                                                                         * .
                                                                                                         * surf_grav
                                                                                                         * ,
                                                                                                         * planet
                                                                                                         * .
                                                                                                         * surf_temp
                                                                                                         * -
                                                                                                         * ConstLogic
                                                                                                         * .
                                                                                                         * EARTH_AVERAGE_KELVIN
                                                                                                         * ,
                                                                                                         * habitable
                                                                                                         * ,
                                                                                                         * planet.id
                                                                                                         */);
            }
            else if ((ConstLogic.verbosity.x0008)
                    && (breathe == EnviroLogic.BREATHABLE) && (gravity > 1.3)
                    && (habitable > 1) && ((rel_temp < -2.0) || (ice > 10.)))
            {
                System.err
                        .println("%12s\tp=%4.2Lf\tm=%4.2Lf\tg=%4.2Lf\tt=%+.1Lf\t%s\tSphinx-like\n"/*
                                                                                                   * ,
                                                                                                   * type_string
                                                                                                   * (
                                                                                                   * planet
                                                                                                   * .
                                                                                                   * type
                                                                                                   * )
                                                                                                   * ,
                                                                                                   * planet
                                                                                                   * .
                                                                                                   * surf_pressure
                                                                                                   * ,
                                                                                                   * planet
                                                                                                   * .
                                                                                                   * mass
                                                                                                   * *
                                                                                                   * ConstLogic
                                                                                                   * .
                                                                                                   * SUN_MASS_IN_EARTH_MASSES
                                                                                                   * ,
                                                                                                   * planet
                                                                                                   * .
                                                                                                   * surf_grav
                                                                                                   * ,
                                                                                                   * planet
                                                                                                   * .
                                                                                                   * surf_temp
                                                                                                   * -
                                                                                                   * ConstLogic
                                                                                                   * .
                                                                                                   * EARTH_AVERAGE_KELVIN
                                                                                                   * ,
                                                                                                   * planet.id
                                                                                                   */);
            }
        }
    }

    public static void generate_planets(SunBean sun, boolean random_tilt,
            int sys_no, String system_name, boolean do_gases,
            boolean do_moons)
    {
        for (int planet_no = 1; planet_no <= sun.getChildren().size(); planet_no++)
        {
            BodyBean body = sun.getChildren().get(planet_no - 1);
            if (!(body instanceof SolidBodyBean))
                continue;
            SolidBodyBean planet = (SolidBodyBean)body;
            if (StringUtils.isTrivial(planet.getName()))
                planet.setName(system_name + " " + planet_no);
            generate_planet(planet, planet_no, sun, random_tilt, do_gases, do_moons);
            /*
             * Now we're ready to test for habitable planets, so we can count
             * and log them and such
             */
            check_planet(planet, false);
            for (BodyBean m : planet.getChildren())
                if (m instanceof SolidBodyBean)
                    check_planet((SolidBodyBean)m, true);
        }
    }

}
