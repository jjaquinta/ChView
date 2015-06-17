package jo.d2k.data.data;

public class FilterConditionBean
{
    private int mType;
    private String  mID;
    private int mOption;
    private Object  mArgument;
    
    public int getType()
    {
        return mType;
    }
    public void setType(int type)
    {
        mType = type;
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
