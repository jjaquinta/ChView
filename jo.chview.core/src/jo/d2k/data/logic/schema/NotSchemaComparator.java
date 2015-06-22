package jo.d2k.data.logic.schema;

import java.util.List;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.FilterConditionBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.FilterLogic;

public class NotSchemaComparator implements ISchemaComparator
{
    @Override
    public String[] getOptions()
    {
        return new String[0];
    }

    @Override
    public boolean isArgFor(int option)
    {
        return false;
    }

    @Override
    public boolean isMatch(ChViewContextBean context, StarBean star,
            FilterConditionBean cond)
    {
        @SuppressWarnings("unchecked")
        List<FilterConditionBean> subCond = (List<FilterConditionBean>)cond.getArgument();
        if (subCond.size() == 0)
            return false;
        return !FilterLogic.isFiltered(context, star, subCond.get(0));
    }

    @Override
    public int getDefaultOption()
    {
        return 0;
    }

    @Override
    public Object isValidArgFor(int option, Object arg)
    {
        if (!(arg instanceof List<?>))
            return null;
        List<?> list = (List<?>)arg;
        if (list.size() != 1)
            return null;
        return arg;
    }

}
