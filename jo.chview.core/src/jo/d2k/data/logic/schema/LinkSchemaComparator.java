package jo.d2k.data.logic.schema;

import jo.util.utils.obj.StringUtils;

public class LinkSchemaComparator extends SimpleSchemaComparator
{
    private static final String[] OPTIONS = {
        "Empty",
        "Not Empty",
    };
    private static final boolean[] ARGS = {
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
        String val = (String)v;
        switch (option)
        {
            case 0: // empty
                return StringUtils.isTrivial(val);
            case 1: // not empty
                return !StringUtils.isTrivial(val);
        }
        return false;
    }

    @Override
    public int getDefaultOption()
    {
        return 1;
    }

    @Override
    public Object isValidArgFor(int option, Object arg)
    {
        if (arg != null)
        {
            String link = arg.toString();
            if (link.indexOf("://") < 0)
                link = "http://"+link;
            arg = link;
        }
        return arg;
    }
}
