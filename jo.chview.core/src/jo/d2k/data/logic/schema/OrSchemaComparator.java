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

}
