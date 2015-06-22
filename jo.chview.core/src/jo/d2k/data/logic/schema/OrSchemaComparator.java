package jo.d2k.data.logic.schema;

import java.util.List;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.FilterConditionBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.FilterLogic;

public class OrSchemaComparator implements ISchemaComparator
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
        List<FilterConditionBean> conds = (List<FilterConditionBean>)cond.getArgument();
        return FilterLogic.isFiltered(context, star, conds, false);
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
        if (list.size() == 0)
            return null;
        return arg;
    }

}
