package jo.d2k.data.logic;

import jo.d2k.data.logic.stargen.logic.ConstLogic;
import jo.util.utils.FormatUtils;

public class UnitUtils extends FormatUtils
{
    // mass in solar masses
    public static String formatMass(double sm)
    {
        double kg = ConvLogic.convSMtoKG(sm);
        if (kg > ConvLogic.MASS_SUN_KG/10)
            return UnitUtils.formatDouble(ConvLogic.convKGtoSM(kg), 2) + " x Sun";
        if (kg > ConvLogic.MASS_JUPITER_KG/10)
            return UnitUtils.formatDouble(ConvLogic.convKGtoJM(kg), 2) + " x Jupiter";
        if (kg > ConvLogic.MASS_EARTH_KG/10)
            return UnitUtils.formatDouble(ConvLogic.convKGtoEM(kg), 2) + " x Earth";
        if (kg > ConvLogic.MASS_MOON_KG/10)
            return UnitUtils.formatDouble(ConvLogic.convKGtoLM(kg), 2) + " x Moon";
        if (kg > ConvLogic.MASS_TON_KG*10)
            return UnitUtils.formatDouble(ConvLogic.convKGtoTons(kg), 0) + " tons";
        return UnitUtils.formatDouble(kg, 0) + " kg";
    }

    // radius in km
    public static String formatRadius(double radius)
    {
        if (radius > ConvLogic.KM_PER_LY/10)
            return UnitUtils.formatDouble(radius/ConvLogic.KM_PER_LY, 2)+ " light years";
        if (radius > ConvLogic.KM_PER_AU/10)
            return UnitUtils.formatDouble(radius/ConvLogic.KM_PER_AU, 2)+ " AU";
        if (radius >= 1E6)
            return UnitUtils.formatDouble(radius/1E6, 2)+ " million km";
        if (radius >= 1E3)
            return UnitUtils.formatDouble(radius/1E3, 2)+ " thousand km";
        return UnitUtils.formatDouble(radius, 0) + " km";
    }

    public static String formatLuminosity(double luminosity)
    {
        return UnitUtils.formatDouble(luminosity, 2);
    }

    // age in years
    public static String formatTimeSpan(double age)
    {
        if (age > ConvLogic.ONE_BILLION_YEARS/10)
            return UnitUtils.formatDouble(age/ConvLogic.ONE_BILLION_YEARS, 2)+ " billion years";
        if (age > ConvLogic.ONE_MILLION_YEARS/10)
            return UnitUtils.formatDouble(age/ConvLogic.ONE_MILLION_YEARS, 2)+ " million years";
        if (age > ConvLogic.ONE_THOUSAND_YEARS*10)
            return UnitUtils.formatDouble(age/ConvLogic.ONE_THOUSAND_YEARS, 0)+ " thousand years";
        return UnitUtils.formatDouble(age, 2)+" years";
    }

    // gravity in Gs
    public static String formatGravity(double g)
    {
        return UnitUtils.formatDouble(g, 2)+" G";
    }

    // pressure in mb
    public static String formatPressure(double mb)
    {
        return UnitUtils.formatDouble(mb/ConstLogic.EARTH_SURF_PRES_IN_MILLIBARS, 2)+" Atm";
    }

    // temp in kelvin
    public static String formatTemperature(double temp)
    {
        return UnitUtils.formatDouble(ConvLogic.convKelvinToCentigrade(temp), 1)+"\u00b0C/"
                +UnitUtils.formatDouble(ConvLogic.convKelvinToFarenheight(temp), 1)+"\u00b0F";
    }

    // density in gm/cc
    public static String formatDensity(double dens)
    {
        return UnitUtils.formatDouble(dens, 1)+" grams/cc";
    }
}
