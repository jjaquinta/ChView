package jo.d2k.data.data;

import jo.d2k.data.logic.StarColumnLogic;

public class FilterConditionBean
{
    private String  mID;
    private int mOption;
    private Object  mArgument;
    
    @Override
    public String toString()
    {
        StringBuffer desc = new StringBuffer();
        StarColumn col = StarColumnLogic.getColumn(mID);
        desc.append(col.getTitle());
        if ((mOption >= 0) && (mOption < col.getComparator().getOptions().length))
        {
            desc.append(" ");
            desc.append(col.getComparator().getOptions()[mOption]);
            if (col.getComparator().isArgFor(getOption()) && (mArgument != null))
            {
                desc.append(" ");
                desc.append(mArgument.toString());
            }
        }
        return desc.toString();
    }
    
    public String getID()
    {
        return mID;
    }
    public void setID(String iD)
    {
        mID = iD;
    }
    public int getOption()
    {
        return mOption;
    }
    public void setOption(int option)
    {
        mOption = option;
    }
    public Object getArgument()
    {
        return mArgument;
    }
    public void setArgument(Object argument)
    {
        mArgument = argument;
    }
}
