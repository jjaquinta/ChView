package jo.d2k.data.logic.stargen.logic;

import java.util.Arrays;

import jo.d2k.data.logic.stargen.data.ChemTable;
import jo.d2k.data.logic.stargen.data.ChemTableComparator;
import jo.d2k.data.logic.stargen.data.PlanetType;
import jo.d2k.data.logic.stargen.data.Verbose;

public class ConstLogic
{
    public static final double RAND_MAX                     = 2147483647.0;

    public static final double PI                           = Math.PI;
    public static final double RADIANS_PER_ROTATION         = 2.0 * PI;

    public static final double ECCENTRICITY_COEFF           = 0.077;                                  // Dole's
                                                                                                       // was
                                                                                                       // 0.077
    public static final double PROTOPLANET_MASS             = 1.0E-15;                                // Units
                                                                                                       // of
                                                                                                       // solar
                                                                                                       // masses
    public static final double CHANGE_IN_EARTH_ANG_VEL      = -1.3E-15;                               // Units
                                                                                                       // of
                                                                                                       // radians/sec/year
    public static final double SOLAR_MASS_IN_GRAMS          = 1.989E33;                               // Units
                                                                                                       // of
                                                                                                       // grams
    public static final double SOLAR_MASS_IN_KILOGRAMS      = 1.989E30;                               // Units
                                                                                                       // of
                                                                                                       // kg
    public static final double EARTH_MASS_IN_GRAMS          = 5.977E27;                               // Units
                                                                                                       // of
                                                                                                       // grams
    public static final double EARTH_RADIUS                 = 6.378E8;                                // Units
                                                                                                       // of
                                                                                                       // cm
    public static final double EARTH_DENSITY                = 5.52;                                   // Units
                                                                                                       // of
                                                                                                       // g/cc
    public static final double KM_EARTH_RADIUS              = 6378.0;                                 // Units
                                                                                                       // of
                                                                                                       // km
                                                                                                       // EARTH_ACCELERATION
                                                                                                       // =
                                                                                                       // 981.0;
                                                                                                       // //
                                                                                                       // Units
                                                                                                       // of
                                                                                                       // cm/sec2
    public static final double EARTH_ACCELERATION           = 980.7;                                  // Units
                                                                                                       // of
                                                                                                       // cm/sec2
    public static final double EARTH_AXIAL_TILT             = 23.4;                                   // Units
                                                                                                       // of
                                                                                                       // degrees
    public static final double EARTH_EXOSPHERE_TEMP         = 1273.0;                                 // Units
                                                                                                       // of
                                                                                                       // degrees
                                                                                                       // Kelvin
    public static final double SUN_MASS_IN_EARTH_MASSES     = 332775.64;
    public static final double ASTEROID_MASS_LIMIT          = 0.001;                                  // Units
                                                                                                       // of
                                                                                                       // Earth
                                                                                                       // Masses
    public static final double EARTH_EFFECTIVE_TEMP         = 250.0;                                  // Units
                                                                                                       // of
                                                                                                       // degrees
                                                                                                       // Kelvin
                                                                                                       // =
                                                                                                       // was
                                                                                                       // 255;
    public static final double CLOUD_COVERAGE_FACTOR        = 1.839E-8;                               // Km2/kg
    public static final double EARTH_WATER_MASS_PER_AREA    = 3.83E15;                                // grams
                                                                                                       // per
                                                                                                       // square
                                                                                                       // km
    public static final double EARTH_SURF_PRES_IN_MILLIBARS = 1013.25;
    public static final double EARTH_SURF_PRES_IN_MMHG      = 760.;                                   // Dole
                                                                                                       // p.
                                                                                                       // 15
    public static final double EARTH_SURF_PRES_IN_PSI       = 14.696;                                 // Pounds
                                                                                                       // per
                                                                                                       // square
                                                                                                       // inch
    public static final double MMHG_TO_MILLIBARS            = EARTH_SURF_PRES_IN_MILLIBARS
                                                                    / EARTH_SURF_PRES_IN_MMHG;
    public static final double PSI_TO_MILLIBARS             = EARTH_SURF_PRES_IN_MILLIBARS
                                                                    / EARTH_SURF_PRES_IN_PSI;
    public static final double H20_ASSUMED_PRESSURE         = 47. * MMHG_TO_MILLIBARS;                // Dole
                                                                                                       // p.
                                                                                                       // 15
    public static final double MIN_O2_IPP                   = 72. * MMHG_TO_MILLIBARS;                // Dole,
                                                                                                       // p.
                                                                                                       // 15
    public static final double MAX_O2_IPP                   = 400. * MMHG_TO_MILLIBARS;               // Dole,
                                                                                                       // p.
                                                                                                       // 15
    public static final double MAX_HE_IPP                   = 61000. * MMHG_TO_MILLIBARS;             // Dole,
                                                                                                       // p.
                                                                                                       // 16
    public static final double MAX_NE_IPP                   = 3900. * MMHG_TO_MILLIBARS;              // Dole,
                                                                                                       // p.
                                                                                                       // 16
    public static final double MAX_N2_IPP                   = 2330. * MMHG_TO_MILLIBARS;              // Dole,
                                                                                                       // p.
                                                                                                       // 16
    public static final double MAX_AR_IPP                   = 1220. * MMHG_TO_MILLIBARS;              // Dole,
                                                                                                       // p.
                                                                                                       // 16
    public static final double MAX_KR_IPP                   = 350. * MMHG_TO_MILLIBARS;               // Dole,
                                                                                                       // p.
                                                                                                       // 16
    public static final double MAX_XE_IPP                   = 160. * MMHG_TO_MILLIBARS;               // Dole,
                                                                                                       // p.
                                                                                                       // 16
    public static final double MAX_CO2_IPP                  = 7. * MMHG_TO_MILLIBARS;                 // Dole,
                                                                                                       // p.
                                                                                                       // 16
    public static final double MAX_HABITABLE_PRESSURE       = 118 * PSI_TO_MILLIBARS;                 // Dole,
                                                                                                       // p.
                                                                                                       // 16
    // The next gases are listed as poisonous in parts per million by volume at
    // 1 atm:
    public static final double PPM_PRSSURE                  = EARTH_SURF_PRES_IN_MILLIBARS / 1000000.;
    public static final double MAX_F_IPP                    = 0.1 * PPM_PRSSURE;                      // Dole,
                                                                                                       // p.
                                                                                                       // 18
    public static final double MAX_CL_IPP                   = 1.0 * PPM_PRSSURE;                      // Dole,
                                                                                                       // p.
                                                                                                       // 18
    public static final double MAX_NH3_IPP                  = 100. * PPM_PRSSURE;                     // Dole,
                                                                                                       // p.
                                                                                                       // 18
    public static final double MAX_O3_IPP                   = 0.1 * PPM_PRSSURE;                      // Dole,
                                                                                                       // p.
                                                                                                       // 18
    public static final double MAX_CH4_IPP                  = 50000. * PPM_PRSSURE;                   // Dole,
                                                                                                       // p.
                                                                                                       // 18

    public static final double EARTH_CONVECTION_FACTOR      = 0.43;                                   // from
                                                                                                       // Hart,
                                                                                                       // eq.20
                                                                                                       // FREEZING_POINT_OF_WATER
                                                                                                       // =
                                                                                                       // 273.0;
                                                                                                       // //
                                                                                                       // Units
                                                                                                       // of
                                                                                                       // degrees
                                                                                                       // Kelvin
    public static final double FREEZING_POINT_OF_WATER      = 273.15;                                 // Units
                                                                                                       // of
                                                                                                       // degrees
                                                                                                       // Kelvin
                                                                                                       // EARTH_AVERAGE_CELSIUS
                                                                                                       // =
                                                                                                       // 15.5;
                                                                                                       // //
                                                                                                       // Average
                                                                                                       // Earth
                                                                                                       // Temperature
    public static final double EARTH_AVERAGE_CELSIUS        = 14.0;                                   // Average
                                                                                                       // Earth
                                                                                                       // Temperature
    public static final double EARTH_AVERAGE_KELVIN         = EARTH_AVERAGE_CELSIUS
                                                                    + FREEZING_POINT_OF_WATER;
    public static final double DAYS_IN_A_YEAR               = 365.256;                                // Earth
                                                                                                       // days
                                                                                                       // per
                                                                                                       // Earth
                                                                                                       // year
                                                                                                       // gas_retention_threshold
                                                                                                       // =
                                                                                                       // 5.0;
                                                                                                       // //
                                                                                                       // ratio
                                                                                                       // of
                                                                                                       // esc
                                                                                                       // vel
                                                                                                       // to
                                                                                                       // RMS
                                                                                                       // vel
    public static final double GAS_RETENTION_THRESHOLD      = 6.0;                                    // ratio
                                                                                                       // of
                                                                                                       // esc
                                                                                                       // vel
                                                                                                       // to
                                                                                                       // RMS
                                                                                                       // vel

    public static final double ICE_ALBEDO                   = 0.7;
    public static final double CLOUD_ALBEDO                 = 0.52;
    public static final double GAS_GIANT_ALBEDO             = 0.5;                                    // albedo
                                                                                                       // of
                                                                                                       // a
                                                                                                       // gas
                                                                                                       // giant
    public static final double AIRLESS_ICE_ALBEDO           = 0.5;
    public static final double EARTH_ALBEDO                 = 0.3;                                    // was
                                                                                                       // .33
                                                                                                       // for
                                                                                                       // a
                                                                                                       // while
    public static final double GREENHOUSE_TRIGGER_ALBEDO    = 0.20;
    public static final double ROCKY_ALBEDO                 = 0.15;
    public static final double ROCKY_AIRLESS_ALBEDO         = 0.07;
    public static final double WATER_ALBEDO                 = 0.04;

    public static final double SECONDS_PER_HOUR             = 3600.0;
    public static final double CM_PER_AU                    = 1.495978707E13;                          // number
                                                                                                       // of
                                                                                                       // cm
                                                                                                       // in
                                                                                                       // an
                                                                                                       // AU
    public static final double CM_PER_KM                    = 1.0E5;                                  // number
                                                                                                       // of
                                                                                                       // cm
                                                                                                       // in
                                                                                                       // a
                                                                                                       // km
    public static final double KM_PER_AU                    = CM_PER_AU
                                                                    / CM_PER_KM;
    public static final double CM_PER_METER                 = 100.0;
    // public static final double MILLIBARS_PER_BAR = 1013.25;
    public static final double MILLIBARS_PER_BAR            = 1000.00;

    public static final double GRAV_CONSTANT                = 6.672E-8;                               // units
                                                                                                       // of
                                                                                                       // dyne
                                                                                                       // cm2/gram2
    public static final double MOLAR_GAS_CONST              = 8314.41;                                // units:
                                                                                                       // g*m2/=
                                                                                                       // sec2*K*mol;
    public static final double K                            = 50.0;                                   // K
                                                                                                       // =
                                                                                                       // gas/dust
                                                                                                       // ratio
    public static final double B                            = 1.2E-5;                                 // Used
                                                                                                       // in
                                                                                                       // Crit_mass
                                                                                                       // calc
    public static final double DUST_DENSITY_COEFF           = 2.0E-3;                                 // A
                                                                                                       // in
                                                                                                       // Dole's
                                                                                                       // paper
    public static final double ALPHA                        = 5.0;                                    // Used
                                                                                                       // in
                                                                                                       // density
                                                                                                       // calcs
    public static final double N                            = 3.0;                                    // Used
                                                                                                       // in
                                                                                                       // density
                                                                                                       // calcs
    public static final double J                            = 1.46E-19;                               // Used
                                                                                                       // in
                                                                                                       // day-length
                                                                                                       // calcs
                                                                                                       // =
                                                                                                       // cm2/sec2
                                                                                                       // g;
    public static final double INCREDIBLY_LARGE_NUMBER      = 9.9999E37;

    // Now for a few molecular weights (used for RMS velocity calc);:
    // This table is from Dole's book "Habitable Planets for Man", p. 38

    public static final double ATOMIC_HYDROGEN              = 1.0;                                    // H
    public static final double MOL_HYDROGEN                 = 2.0;                                    // H2
    public static final double HELIUM                       = 4.0;                                    // He
    public static final double ATOMIC_NITROGEN              = 14.0;                                   // N
    public static final double ATOMIC_OXYGEN                = 16.0;                                   // O
    public static final double METHANE                      = 16.0;                                   // CH4
    public static final double AMMONIA                      = 17.0;                                   // NH3
    public static final double WATER_VAPOR                  = 18.0;                                   // H2O
    public static final double NEON                         = 20.2;                                   // Ne
    public static final double MOL_NITROGEN                 = 28.0;                                   // N2
    public static final double CARBON_MONOXIDE              = 28.0;                                   // CO
    public static final double NITRIC_OXIDE                 = 30.0;                                   // NO
    public static final double MOL_OXYGEN                   = 32.0;                                   // O2
    public static final double HYDROGEN_SULPHIDE            = 34.1;                                   // H2S
    public static final double ARGON                        = 39.9;                                   // Ar
    public static final double CARBON_DIOXIDE               = 44.0;                                   // CO2
    public static final double NITROUS_OXIDE                = 44.0;                                   // N2O
    public static final double NITROGEN_DIOXIDE             = 46.0;                                   // NO2
    public static final double OZONE                        = 48.0;                                   // O3
    public static final double SULPH_DIOXIDE                = 64.1;                                   // SO2
    public static final double SULPH_TRIOXIDE               = 80.1;                                   // SO3
    public static final double KRYPTON                      = 83.8;                                   // Kr
    public static final double XENON                        = 131.3;                                  // Xe

    // And atomic numbers, for use in ChemTable indexes
    public static final int    AN_H                         = 1;
    public static final int    AN_HE                        = 2;
    public static final int    AN_N                         = 7;
    public static final int    AN_O                         = 8;
    public static final int    AN_F                         = 9;
    public static final int    AN_NE                        = 10;
    public static final int    AN_P                         = 15;
    public static final int    AN_CL                        = 17;
    public static final int    AN_AR                        = 18;
    public static final int    AN_BR                        = 35;
    public static final int    AN_KR                        = 36;
    public static final int    AN_I                         = 53;
    public static final int    AN_XE                        = 54;
    public static final int    AN_HG                        = 80;
    public static final int    AN_AT                        = 85;
    public static final int    AN_RN                        = 86;
    public static final int    AN_FR                        = 87;

    public static final int    AN_NH3                       = 900;
    public static final int    AN_H2O                       = 901;
    public static final int    AN_CO2                       = 902;
    public static final int    AN_O3                        = 903;
    public static final int    AN_CH4                       = 904;
    public static final int    AN_CH3CH2OH                  = 905;

    // The following defines are used in the kothari_radius function in
    // file enviro.c.
    public static final double A1_20                        = 6.485E12;                               // All
                                                                                                       // units
                                                                                                       // are
                                                                                                       // in
                                                                                                       // cgs
                                                                                                       // system.
    public static final double A2_20                        = 4.0032E-8;                              // ie:
                                                                                                       // cm,
                                                                                                       // g,
                                                                                                       // dynes,
                                                                                                       // etc.
    public static final double BETA_20                      = 5.71E12;

    public static final double JIMS_FUDGE                   = 1.004;

    // The following defines are used in determining the fraction of a planet
    // covered with clouds in function cloud_fraction in file enviro.c.
    public static final double Q1_36                        = 1.258E19;                               // grams
    public static final double Q2_36                        = 0.0698;                                 // 1/Kelvin

    // macros:
    public static double pow2(double a)
    {
        return a * a;
    }

    public static double pow3(double a)
    {
        return a * a * a;
    }

    public static double pow4(double a)
    {
        return a * a * a * a;
    }

    public static double pow1_4(double a)
    {
        return Math.sqrt(Math.sqrt(a));
    }

    public static double pow1_3(double a)
    {
        return Math.pow(a, (1.0 / 3.0));
    }

    // convert solar masses to earth masses
    public static double toEM(double sm)
    {
        return sm*SUN_MASS_IN_EARTH_MASSES;
    }
    
    public static Verbose verbosity                 = new Verbose();

    public static final ChemTable gases[]       = {
    // An sym HTML symbol name Aw melt boil dens ABUNDe ABUNDs Rea Max
    // inspired pp
    new ChemTable(AN_H, "H", "H<SUB><SMALL>2</SMALL></SUB>",
            "Hydrogen", 1.0079, 14.06, 20.40, 8.99e-05, 0.00125893,
            27925.4, 1, 0.0),
    new ChemTable(AN_HE, "He", "He", "Helium", 4.0026, 3.46,
            4.20, 0.0001787, 7.94328e-09, 2722.7, 0,
            MAX_HE_IPP),
    new ChemTable(AN_N, "N", "N<SUB><SMALL>2</SMALL></SUB>",
            "Nitrogen", 14.0067, 63.34, 77.40, 0.0012506, 1.99526e-05,
            3.13329, 0, MAX_N2_IPP),
    new ChemTable(AN_O, "O", "O<SUB><SMALL>2</SMALL></SUB>",
            "Oxygen", 15.9994, 54.80, 90.20, 0.001429, 0.501187,
            23.8232, 10, MAX_O2_IPP),
    new ChemTable(AN_NE, "Ne", "Ne", "Neon", 20.1700, 24.53,
            27.10, 0.0009, 5.01187e-09, 3.4435e-5, 0,
            MAX_NE_IPP),
    new ChemTable(AN_AR, "Ar", "Ar", "Argon", 39.9480,
            84.00, 87.30, 0.0017824, 3.16228e-06, 0.100925, 0,
            MAX_AR_IPP),
    new ChemTable(AN_KR, "Kr", "Kr", "Krypton", 83.8000,
            116.60, 119.70, 0.003708, 1e-10, 4.4978e-05, 0,
            MAX_KR_IPP),
    new ChemTable(AN_XE, "Xe", "Xe", "Xenon", 131.3000,
            161.30, 165.00, 0.00588, 3.16228e-11, 4.69894e-06, 0,
            MAX_XE_IPP),
    // from here down, these columns were originally: 0.001, 0
    new ChemTable(AN_NH3, "NH3",
            "NH<SUB><SMALL>3</SMALL></SUB>", "Ammonia", 17.0000,
            195.46, 239.66, 0.001, 0.002, 0.0001, 1,
            MAX_NH3_IPP),
    new ChemTable(AN_H2O, "H2O",
            "H<SUB><SMALL>2</SMALL></SUB>O", "Water", 18.0000, 273.16,
            373.16, 1.000, 0.03, 0.001, 0, 0.0),
    new ChemTable(AN_CO2, "CO2",
            "CO<SUB><SMALL>2</SMALL></SUB>", "CarbonDioxide", 44.0000,
            194.66, 194.66, 0.001, 0.01, 0.0005, 0,
            MAX_CO2_IPP),
    new ChemTable(AN_O3, "O3",
            "O<SUB><SMALL>3</SMALL></SUB>", "Ozone", 48.0000, 80.16,
            161.16, 0.001, 0.001, 0.000001, 2, MAX_O3_IPP),
    new ChemTable(AN_CH4, "CH4",
            "CH<SUB><SMALL>4</SMALL></SUB>", "Methane", 16.0000, 90.16,
            109.16, 0.010, 0.005, 0.0001, 1, MAX_CH4_IPP), 

//     new ChemTable(AN_NH3, "NH3", "NH<SUB><SMALL>3</SMALL></SUB>", "Ammonia", 17.0000, 195.46, 239.66, 0.001, 0.002, 0.001, 0.001, MAX_NH3_IPP), 
//     new ChemTable(AN_H2O, "H2O", "H<SUB><SMALL>2</SMALL></SUB>O", "Water", 18.0000, 273.16, 373.16, 1.000, 0.03, 0.001, 0, (9.9999E37)), 
//     new ChemTable(AN_CO2, "CO2", "CO<SUB><SMALL>2</SMALL></SUB>", "CarbonDioxide", 44.0000, 194.66, 194.66, 0.001, 0.01, 0.001, 0, MAX_CO2_IPP), 
//     new ChemTable(AN_O3, "O3", "O<SUB><SMALL>3</SMALL></SUB>", "Ozone", 48.0000, 80.16, 161.16, 0.001, 0.001, 0.001, 0.001, MAX_O3_IPP), 
//     new ChemTable(AN_CH4, "CH4", "CH<SUB><SMALL>4</SMALL></SUB>", "Methane", 16.0000, 90.16, 109.16, 0.010, 0.005, 0.001, 0, MAX_CH4_IPP),
     
     new ChemTable(AN_F, "F", "F", "Fluorine", 18.9984, 53.58, 85.10, 0.001696, 0.000630957, 0.000843335, 50, MAX_F_IPP), 
     new ChemTable(AN_CL, "Cl", "Cl", "Chlorine", 35.4530, 172.22, 239.20, 0.003214, 0.000125893, 0.005236, 40, MAX_CL_IPP),
     
     /*
     new ChemTable( 910, "H2", "H<SUB><SMALL>2</SMALL></SUB>", "Di-Hydrogen", 2, 14.06, 20.40, 8.99e-05, 0.00125893, 27925.4 ), 
     new ChemTable( 911, "N2", "N<SUB><SMALL>2</SMALL></SUB>", 28, 63.34, 77.40, 0.0012506, 1.99526e-05,3.13329 ), 
     new ChemTable( 912, "O2", "O<SUB><SMALL>2</SMALL></SUB>", 32, 54.80, 90.20, 0.001429, 0.501187, 23.8232, 10),
     new ChemTable(AN_CH3CH2OH, "CH<SUB><SMALL>3</SMALL></SUB>CH<SUB><SMALL>2</SMALL></SUB>OH", "Ethanol", 46.0000, 159.06, 351.66, 0.895, 0.001, 0.001, 0),
     */
    };

    /*
     * Sort ChemTable by decreasing pressure.
     */
    static
    {
        for (int index = 0; index < ConstLogic.gases.length; index++)
            if (ConstLogic.gases[index].max_ipp == 0.0)
                ConstLogic.gases[index].max_ipp = ConstLogic.INCREDIBLY_LARGE_NUMBER;
        Arrays.sort(ConstLogic.gases, new ChemTableComparator());
    }
    
    public static final String[] TYPE_NAME = 
    {
        "Unknown",
        "Rock",
        "Venusian",
        "Terrestrial",
        "Gas Dwarf",
        "Sub-Jovian",
        "Jovian",
        "Martian",
        "Water",
        "Ice",
        "Asteroids",
        "One Face",
    };

    public static String getTypeName(PlanetType type)
    {
        return TYPE_NAME[getTypeIndex(type)];
    }

    public static int getTypeIndex(PlanetType type)
    {
        int tIndex = 0;
        switch (type)
        {
            case tUnknown:
                tIndex = 0;
                break;
            case tRock:
                tIndex = 1;
                break;
            case tVenusian:
                tIndex = 2;
                break;
            case tTerrestrial:
                tIndex = 3;
                break;
            case tSubSubGasGiant:
                tIndex = 4;
                break;
            case tSubGasGiant:
                tIndex = 5;
                break;
            case tGasGiant:
                tIndex = 6;
                break;
            case tMartian:
                tIndex = 7;
                break;
            case tWater:
                tIndex = 8;
                break;
            case tIce:
                tIndex = 9;
                break;
            case tAsteroids:
                tIndex = 10;
                break;
            case t1Face:
                tIndex = 11;
                break;
        }
        return tIndex;
    }

    public static int getSpectraIndex(String spectra)
    {
        switch (Character.toUpperCase(spectra.charAt(0)))
        {
            case 'O':
                return 0;
            case 'B':
                return 1;
            case 'A':
                return 2;
            case 'F':
                return 3;
            case 'G':
                return 4;
            case 'K':
                return 5;
            case 'M':
                return 6;
        }
        return 6;
    }
}
