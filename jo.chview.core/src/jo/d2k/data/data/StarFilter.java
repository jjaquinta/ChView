package jo.d2k.data.data;

import java.util.ArrayList;
import java.util.List;

import jo.d2k.data.logic.schema.TextSchemaComparator;
import jo.util.beans.Bean;
import jo.util.intro.PseudoProp;
import jo.util.utils.obj.BooleanUtils;

public class StarFilter extends Bean
{
    private List<FilterConditionBean> mConditions;
    private boolean mAnd;
    
    public StarFilter()
    {
        mConditions = new ArrayList<FilterConditionBean>();
        mAnd = false;
    }

    public List<FilterConditionBean> getConditions()
    {
        return mConditions;
    }

    public void setConditions(List<FilterConditionBean> conditions)
    {
        mConditions = conditions;
    }

    public boolean isAnd()
    {
        return mAnd;
    }

    public void setAnd(boolean and)
    {
        mAnd = and;
    }

    @PseudoProp
    public Boolean getGenerated()
    {
        for (FilterConditionBean cond : mConditions)
            if ("generated".equals(cond.getID()))
            {
                switch (cond.getOption())
                {
                    case TextSchemaComparator.EQUALS:
                        return BooleanUtils.parseBoolean(cond.getArgument());
                    case TextSchemaComparator.NOTEQUALS:
                        return !BooleanUtils.parseBoolean(cond.getArgument());
                    case TextSchemaComparator.CONTAINS:
                        return BooleanUtils.parseBoolean(cond.getArgument());
                    case TextSchemaComparator.EMPTY:
                        return false;
                    case TextSchemaComparator.NOTEMPTY:
                        return true;
                }
            }
        return null;
    }
}
