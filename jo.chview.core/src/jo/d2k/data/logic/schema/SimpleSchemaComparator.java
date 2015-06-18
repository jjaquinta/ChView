package jo.d2k.data.logic.schema;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.FilterConditionBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarColumn;
import jo.d2k.data.logic.StarColumnLogic;

public abstract class SimpleSchemaComparator implements ISchemaComparator
{
    @Override
    public boolean isMatch(ChViewContextBean context, StarBean star,
            FilterConditionBean cond)
    {
        StarColumn col = StarColumnLogic.getColumn(cond.getID());
        Object value = StarColumnLogic.getValue(context, star, col);
        return isMatch(value, cond.getOption(), cond.getArgument());
    }

    public abstract boolean isMatch(Object value, int option, Object arg);   

}
