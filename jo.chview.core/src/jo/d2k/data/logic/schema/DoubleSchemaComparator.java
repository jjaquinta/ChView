package jo.d2k.data.logic.schema;

import jo.util.utils.obj.DoubleUtils;

public class DoubleSchemaComparator extends SimpleSchemaComparator
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
    public boolean isMatch(Object v, int option, Object a)
    {
        double val = DoubleUtils.parseDouble(v);
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

    @Override
    public int getDefaultOption()
    {
        return 2;
    }

    @Override
    public Object isValidArgFor(int option, Object arg)
    {
        return DoubleUtils.parseDouble(arg);
    }
}
