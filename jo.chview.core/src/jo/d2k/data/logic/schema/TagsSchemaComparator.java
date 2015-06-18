package jo.d2k.data.logic.schema;

import jo.util.utils.obj.StringUtils;

public class TagsSchemaComparator extends SimpleSchemaComparator
{
    private static final String[] OPTIONS = {
        "Contains",
        "Does Not Contain",
        "Contains Only",
        "Contains Anything",
        "Empty",
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
    public boolean isMatch(Object v, int option, Object a)
    {
        String val = " "+v.toString().toLowerCase()+" ";
        String arg = " "+a.toString().trim().toLowerCase()+" ";
        switch (option)
        {
            case 0: // contains
                return val.indexOf(arg) >= 0;
            case 1: // does not contain
                return val.indexOf(arg) < 0;
            case 2: // contains only
                return val.equals(arg);
            case 3: // contains anything
                return !StringUtils.isTrivial(val);
            case 4: // is empty
                return StringUtils.isTrivial(val);
        }
        return false;
    }
}
