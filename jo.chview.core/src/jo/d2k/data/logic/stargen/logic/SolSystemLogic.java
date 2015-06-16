package jo.d2k.data.logic.stargen.logic;

import java.util.ArrayList;
import java.util.List;

import jo.d2k.data.logic.stargen.data.PlanetType;
import jo.d2k.data.logic.stargen.data.SolidBodyBean;

public class SolSystemLogic
{
    public static double EM(double x)
    {
        return (x) / ConstLogic.SUN_MASS_IN_EARTH_MASSES;
    }

    // No Orbit Eccen. Tilt Mass Gas Giant? Dust Mass Gas
    public static final SolidBodyBean    luna          = new SolidBodyBean("Luna", 1, 2.571e-3,
                                                        0.055, 1.53,
                                                        EM(.01229), false,
                                                        EM(.01229), 0, 0
                                                        );
    public static final SolidBodyBean    callisto      = new SolidBodyBean("Callisto", 4, 1.259e-2, 0, 0,
                                                        EM(1.62e-2), false,
                                                        EM(1.62 - 2), 0, 0);
    public static final SolidBodyBean    ganymede      = new SolidBodyBean("Ganymede", 3, 7.16e-3,
                                                        0.0796, 0, EM(2.6e-2),
                                                        false, EM(2.6e-2), 0,
                                                        0);
    public static final SolidBodyBean    europa        = new SolidBodyBean("Europa", 2, 4.49e-3,
                                                        0.0075, 0, EM(7.9e-3),
                                                        false, EM(7.9e-3), 0,
                                                        0);
    public static final SolidBodyBean    io            = new SolidBodyBean("Io", 1, 2.82e-3,
                                                        0.0006, 0, EM(1.21e-2),
                                                        false, EM(1.21e-2), 0,
                                                        0);
    public static final SolidBodyBean    iapetus       = new SolidBodyBean("Iapetus", 6, 2.38e-2, 0.029,
                                                        0, EM(8.4e-4), false,
                                                        EM(8.4e-4), 0, 0);
    public static final SolidBodyBean    hyperion      = new SolidBodyBean("Hyperion", 5, 9.89e-3, 0.110,
                                                        0, EM(1.82e-5), false,
                                                        EM(1.82e-5), 0, 0);
    public static final SolidBodyBean    titan         = new SolidBodyBean("Titan", 4, 8.17e-3,
                                                        0.0289, 0, EM(2.3e-2),
                                                        false, EM(2.3e-2), 0,
                                                        0);
    public static final SolidBodyBean    rhea          = new SolidBodyBean("Rhea", 3, 3.52e-3,
                                                        0.0009, 0, EM(3.85e-4),
                                                        false, EM(3.85e-4), 0,
                                                        0);
    public static final SolidBodyBean    dione         = new  SolidBodyBean("Dione", 2, 2.52e-3,
                                                        0.0021, 0, EM(1.74e-4),
                                                        false, EM(1.74e-4), 0,
                                                        0);
    public static final SolidBodyBean    tethys        = new SolidBodyBean("Tethys", 1, 1.97e-3, 0.000,
                                                        0, EM(1.09e-4), false,
                                                        EM(1.09e-4), 0, 0);
    public static final SolidBodyBean    triton        = new SolidBodyBean("Triton", 1, 2.36e-3, 0.000,
                                                        0, EM(2.31e-2), false,
                                                        EM(2.31e-2), 0, 0);
    public static final SolidBodyBean    charon        = new SolidBodyBean("Charon",
                                                        1,
                                                        19571 / ConstLogic.KM_PER_AU,
                                                        0.000, 0, EM(2.54e-4),
                                                        false, EM(2.54e-4), 0,
                                                        0);

    public static final SolidBodyBean    xena          = new SolidBodyBean("Xena", 11, 67.6681,
                                                        0.44177, 0, EM(.0025),
                                                        false, EM(.0025), 0, 0);
    public static final SolidBodyBean    pluto         = new SolidBodyBean("Pluto", 10, 39.529, 0.248,
                                                        122.5, EM(0.002),
                                                        false, EM(0.002), 0, 0);
    public static final SolidBodyBean    neptune       = new SolidBodyBean("Neptune", 9, 30.061, 0.010,
                                                        29.6, EM(17.14), true,
                                                        0, EM(17.14), 0);
    public static final SolidBodyBean    uranus        = new SolidBodyBean("Uranus", 8, 19.191, 0.046,
                                                        97.9, EM(14.530), true,
                                                        0, EM(14.530), 0);
    public static final SolidBodyBean    saturn        = new SolidBodyBean("Saturn", 7, 9.539, 0.056,
                                                        26.7, EM(95.18), true,
                                                        0, EM(95.18), 0);
    public static final SolidBodyBean    jupiter       = new SolidBodyBean("Jupiter", 6, 5.203, 0.048,
                                                        3.1, EM(317.9), true,
                                                        0, EM(317.9), 0);
    public static final SolidBodyBean    ceres         = new SolidBodyBean("Ceres",
                                                        5,
                                                        2.766,
                                                        0.080,
                                                        0,
                                                        9.5e20 / ConstLogic.SOLAR_MASS_IN_KILOGRAMS,
                                                        false,
                                                        9.5e20 / ConstLogic.SOLAR_MASS_IN_KILOGRAMS,
                                                        0, 0);
    public static final SolidBodyBean    mars          = new SolidBodyBean("Mars", 4, 1.524, 0.093,
                                                        25.2, EM(0.1074),
                                                        false, EM(0.1074), 0,
                                                        0);
    public static final SolidBodyBean    earth         = new SolidBodyBean("Earth", 3, 1.000, 0.017,
                                                        23.5, EM(1.00), false,
                                                        EM(1.00), 0, 0);
    public static final SolidBodyBean    venus         = new SolidBodyBean("Venus", 2, 0.723, 0.007,
                                                        177.3, EM(0.815),
                                                        false, EM(0.815), 0, 0);
    public static final SolidBodyBean    mercury       = new SolidBodyBean("Mercury", 1, 0.387, 0.206,
                                                        2, EM(0.055), false,
                                                        EM(0.055), 0, 0);
    public static final List<SolidBodyBean>    solar_system  = new ArrayList<SolidBodyBean>();
    static
    {
        solar_system.add(mercury);
        solar_system.add(venus);
        solar_system.add(earth);
        earth.getChildren().add(luna);
        solar_system.add(mars);
        solar_system.add(ceres);
        solar_system.add(jupiter);
        jupiter.getChildren().add(io);
        jupiter.getChildren().add(europa);
        jupiter.getChildren().add(ganymede);
        jupiter.getChildren().add(callisto);
        solar_system.add(saturn);
        saturn.getChildren().add(tethys);
        saturn.getChildren().add(dione);
        saturn.getChildren().add(rhea);
        saturn.getChildren().add(titan);
        saturn.getChildren().add(iapetus);
        solar_system.add(uranus);
        solar_system.add(neptune);
        neptune.getChildren().add(triton);
        solar_system.add(pluto);
        pluto.getChildren().add(charon);
        solar_system.add(xena);
        
        luna.setType(PlanetType.tRock);
        callisto.setType(PlanetType.tIce);
        ganymede.setType(PlanetType.tIce);
        europa.setType(PlanetType.tIce);
        io.setType(PlanetType.tRock);
        iapetus.setType(PlanetType.tIce);
        hyperion.setType(PlanetType.tIce);
        titan.setType(PlanetType.tMartian);
        rhea.setType(PlanetType.tIce);
        dione.setType(PlanetType.tIce);
        tethys.setType(PlanetType.tIce);
        triton.setType(PlanetType.tIce);
        charon.setType(PlanetType.tIce);
        xena.setType(PlanetType.tIce);
        pluto.setType(PlanetType.tIce);
        neptune.setType(PlanetType.tSubGasGiant);
        uranus.setType(PlanetType.tSubGasGiant);
        saturn.setType(PlanetType.tGasGiant);
        jupiter.setType(PlanetType.tGasGiant);
        ceres.setType(PlanetType.tRock);
        mars.setType(PlanetType.tMartian);
        earth.setType(PlanetType.tTerrestrial);
        venus.setType(PlanetType.tVenusian);
        mercury.setType(PlanetType.tRock);
    }
}
