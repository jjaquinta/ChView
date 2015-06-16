package jo.d2k.data.logic.stargen.data;

import java.util.Comparator;

public class ChemTableComparator implements Comparator<ChemTable>
{
    /*
     * Sort a ChemTable by decreasing abundance.
     */
    private static int diminishing_abundance(ChemTable x, ChemTable y)
    {
        double xx = x.abunds * x.abunde;
        double yy = y.abunds * y.abunde;

        if (xx < yy)
            return +1;
        return (xx > yy ? -1 : 0);
    }

    @Override
    public int compare(ChemTable object1, ChemTable object2)
    {
        return diminishing_abundance(object1, object2);
    }

}
