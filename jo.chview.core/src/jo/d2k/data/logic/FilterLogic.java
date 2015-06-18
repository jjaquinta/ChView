package jo.d2k.data.logic;

import java.util.List;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.FilterConditionBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarColumn;
import jo.d2k.data.data.StarFilter;
import jo.d2k.data.logic.schema.ISchemaComparator;

public class FilterLogic
{
    public static boolean isFiltered(ChViewContextBean context, StarBean star, StarFilter filter)
    {
        return isFiltered(context, star, filter.getConditions(), filter.isAnd());
    }

    public static boolean isAnyFilter(StarFilter filter)
    {
        return filter.getConditions().size() > 0;
    }
    
    public static boolean isFiltered(ChViewContextBean context, StarBean star, FilterConditionBean cond)
    {
        StarColumn col = StarColumnLogic.getColumn(cond.getID());
        ISchemaComparator comp = col.getComparator();
        boolean match = comp.isMatch(context, star, cond);
        return match;
    }
    
    public static boolean isFiltered(ChViewContextBean context, StarBean star, List<FilterConditionBean> conds, boolean and)
    {
        if (and)
        {
            for (FilterConditionBean cond : conds)
                if (!isFiltered(context, star, cond))
                    return false;
            return true;
        }
        else
        {
            for (FilterConditionBean cond : conds)
                if (isFiltered(context, star, cond))
                    return true;
            return false;
        }
    }
}
