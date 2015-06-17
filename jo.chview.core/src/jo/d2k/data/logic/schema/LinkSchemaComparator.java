package jo.d2k.data.logic.schema;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.StarLogic;
import jo.util.utils.obj.StringUtils;

public class LinkSchemaComparator implements ISchemaComparator
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
    public String getName()
    {
        return "Link";
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
        String val = StarLogic.getMetadata(star).get(id).trim().toLowerCase();
        switch (option)
        {
            case 0: // empty
                return StringUtils.isTrivial(val);
            case 1: // not empty
                return !StringUtils.isTrivial(val);
        }
        return false;
    }
}
