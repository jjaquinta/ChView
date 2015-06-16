package jo.d2k.data.logic.stargen;

import java.util.ArrayList;
import java.util.List;

import jo.d2k.data.logic.stargen.data.GasBean;
import jo.d2k.data.logic.stargen.data.PlanetType;
import jo.d2k.data.logic.stargen.data.SolidBodyBean;
import jo.d2k.data.logic.stargen.logic.ConstLogic;
import jo.d2k.data.logic.stargen.logic.EnviroLogic;
import jo.d4w.logic.BodyLogic;

public class PlanetExtraLogic
{
    public static List<String> getTags(SolidBodyBean planet)
    {
        List<String> tags = new ArrayList<String>();
        tags.add(ConstLogic.getTypeName(planet.getType()));
        if ((int)planet.getDay() == (int)(planet.getOrbPeriod() * 24.0))
            tags.add("Tidally Locked 1 Face");
        if (planet.isResonantPeriod())
            tags.add("Resonant Spin Locked");
        if ((planet.getType() == PlanetType.tGasGiant)
                || (planet.getType() == PlanetType.tSubGasGiant)
                || (planet.getType() == PlanetType.tSubSubGasGiant))
        {
            // Nothing, for now.
        }
        else
        {
            tags.addAll(BodyLogic.getTags(planet));
            if (planet.getAtmosphere().size() > 0)
            {
                int temp;
                for (GasBean gas : planet.getAtmosphere())
                {
                    if ((gas.getSurfacePressure() / planet.getSurfPressure()) > .01)
                        tags.add(gas.getChem().unicode_symbol);
                }
                if ((temp = EnviroLogic.breathability(planet)) != EnviroLogic.NONE)
                    tags.add(String.format(" - %s)",
                            EnviroLogic.breathability_phrase[temp]));
            }

            if ((int)planet.getDay() == (int)(planet.getOrbPeriod() * 24.0)
                    || (planet.isResonantPeriod()))
                tags.add("1-Face");
        }

        return tags;
    }
}
