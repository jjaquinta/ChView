package jo.d2k.data.logic.schema;

import jo.util.utils.obj.StringUtils;

public class TextSchemaComparator extends SimpleSchemaComparator
{
    public static final int EQUALS = 0;
    public static final int NOTEQUALS = 1;
    public static final int CONTAINS = 2;
    public static final int EMPTY = 3;
    public static final int NOTEMPTY = 4;
    
    private static final String[] OPTIONS = {
        "Equals",
        "Not Equals",
        "Contains",
        "Empty",
        "Not Empty",
    };
    private static final boolean[] ARGS = {
        true,
        true,
        true,
        false,
        false,
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
    public boolean isMatch(Object v, int option, Object arg)
    {
        String val;
        if (v == null)
            val = "";
        else
            val = v.toString();
        switch (option)
        {
            case 0: // equals
                return val.equalsIgnoreCase(arg.toString());
            case 1: // not equals
                return !val.equalsIgnoreCase(arg.toString());
            case 2: // contains
                return val.toLowerCase().indexOf(arg.toString().toLowerCase()) >= 0;
            case 3: // empty
                return StringUtils.isTrivial(val);
            case 4: // not empty
                return !StringUtils.isTrivial(val);
        }
        return false;
    }

    @Override
    public int getDefaultOption()
    {
        return 0;
    }
}
