package jo.d2k.data.logic;

import jo.d2k.data.logic.stargen.logic.ConstLogic;

public class ConvLogic
{
    public static final long ONE_MILLISECOND = 1;
    public static final long ONE_SECOND = 1000;
    public static final long ONE_MINUTE = 60*ONE_SECOND;
    public static final long ONE_HOUR = 60*ONE_MINUTE;
    public static final long ONE_DAY = 24*ONE_HOUR;
    public static final long ONE_WEEK = 7*ONE_DAY;
    
    public static final double AU_PER_LY = 63239.7263;
    public static final double KM_PER_AU = 149597871;
    public static final double KM_PER_LY = KM_PER_AU*AU_PER_LY;
    
    public static final double MASS_SUN_KG = 1.989E30;
    public static final double MASS_JUPITER_KG = 1.898E27;
    public static final double MASS_EARTH_KG = 5.972E24;
    public static final double MASS_MARS_KG = 6.39E23;
    public static final double MASS_MOON_KG = 7.34767309E22;
    public static final double MASS_TON_KG = 1E3;

    public static final double ONE_BILLION_YEARS = 1E9;
    public static final double ONE_MILLION_YEARS = 1E6;
    public static final double ONE_THOUSAND_YEARS = 1E3;
    
    public static double convLYtoAU(double ly)
    {
        return ly*AU_PER_LY;
    }
    
    public static double convAUtoKM(double au)
    {
        return au*KM_PER_AU;
    }
    
    public static double convSMtoKG(double sm)
    {
        return sm*MASS_SUN_KG;
    }

    public static double convKGtoSM(double kg)
    {
        return kg/MASS_SUN_KG;
    }

    public static double convKGtoJM(double kg)
    {
        return kg/MASS_JUPITER_KG;
    }

    public static double convKGtoEM(double kg)
    {
        return kg/MASS_EARTH_KG;
    }

    public static double convKGtoLM(double kg)
    {
        return kg/MASS_MOON_KG;
    }

    public static double convKGtoTons(double kg)
    {
        return kg/MASS_TON_KG;
    }
    
    public static double convKelvinToFarenheight(double kv)
    {
        return 32 + ((kv - ConstLogic.FREEZING_POINT_OF_WATER)*1.8);
    }
    
    public static double convKelvinToCentigrade(double kv)
    {
        return kv - ConstLogic.FREEZING_POINT_OF_WATER;
    }
}
