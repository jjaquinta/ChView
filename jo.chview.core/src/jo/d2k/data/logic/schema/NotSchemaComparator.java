package jo.d2k.data.logic.schema;

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
        FilterConditionBean subCond = (FilterConditionBean)cond.getArgument();
        return !FilterLogic.isFiltered(context, star, subCond);
    }

}
