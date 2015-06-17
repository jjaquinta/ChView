package jo.d2k.data.logic.schema;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.StarLogic;
import jo.util.utils.obj.DoubleUtils;

public class DoubleSchemaComparator implements ISchemaComparator
{
    private static final String[] OPTIONS = {
        "Less Than",
        "Greater Than",
        "Equals",
        "Not Equals",
        "Less Than or Equals",
        "Greater Than or Equals",
    };
    private static final boolean[] ARGS = {
        true,
        true,
        true,
        true,
        true,
        true,
    };


    @Override
    public String getName()
    {
        return "Floating Point";
    }
    @Override
    public String[] getOptions()
    {
        return OPTIONS;
    }

    @Override
    public boolean isArgFor(int option)
    {
        return ARGS[option];
    }

    @Override
    public boolean isMatch(StarBean star, String id, int option, Object a)
    {
        double val = DoubleUtils.parseDouble(StarLogic.getMetadata(star).get(id).trim());
        double arg = DoubleUtils.parseDouble(a);
        switch (option)
        {
            case 0: // Less Than
                return DoubleUtils.lessThan(val, arg);
            case 1: // Greater Than
                return DoubleUtils.greaterThan(val, arg);
            case 2: // Equals
                return DoubleUtils.equals(val, arg);
            case 3: // Not Equals
                return !DoubleUtils.equals(val, arg);
            case 4: // Less Than or Equals
                return !DoubleUtils.greaterThan(val, arg);
            case 5: // Greater Than or Equals
                return !DoubleUtils.lessThan(val, arg);
        }
        return false;
    }
}
