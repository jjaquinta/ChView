package jo.d2k.data.logic.stargen.data;

import java.util.Comparator;

public class GasComparator implements Comparator<GasBean>
{
    private static int diminishing_pressure(GasBean x, GasBean y)
    {
        if (x.getSurfacePressure() < y.getSurfacePressure())
            return +1;
        return (x.getSurfacePressure() > y.getSurfacePressure() ? -1 : 0);
    }

    @Override
    public int compare(GasBean o1, GasBean o2)
    {
        return diminishing_pressure(o1, o2);
   }
}
