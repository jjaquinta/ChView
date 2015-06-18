package jo.d2k.data.logic.schema;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.FilterConditionBean;
import jo.d2k.data.data.StarBean;


public interface ISchemaComparator
{
    public String[] getOptions();
    public boolean isArgFor(int option);
    public boolean isMatch(ChViewContextBean context, StarBean star, FilterConditionBean cond);
    public int getDefaultOption();
}
