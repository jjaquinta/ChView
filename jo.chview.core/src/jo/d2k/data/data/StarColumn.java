package jo.d2k.data.data;


public class StarColumn
{
    public static final int TYPE_INTRINSIC = 0;
    public static final int TYPE_EXTRA = 1;
    public static final int TYPE_CALCULATED = 2;
    
    private int mType;
    private String  mTitle;
    private String  mID;
    private int mSortBy;
    private int mWidth;
    
    public StarColumn(int type, String id, String title)
    {
        mType = type;
        mID = id;
        mTitle = title;
        mWidth = 125;
        mSortBy = StarSchemaBean.SORT_BY_TEXT_INSENSITIVE;
    }
    
    public StarColumn(int type, String id, String title, int width, int sortBy)
    {
        mType = type;
        mID = id;
        mTitle = title;
        mWidth = width;
        mSortBy = sortBy;
    }
    
    @Override
    public String toString()
    {
        return mTitle;
    }
    
    public int getType()
    {
        return mType;
    }
    public void setType(int type)
    {
        mType = type;
    }
    public String getTitle()
    {
        return mTitle;
    }
    public void setTitle(String title)
    {
        mTitle = title;
    }
    public String getID()
    {
        return mID;
    }
    public void setID(String iD)
    {
        mID = iD;
    }

    public int getSortBy()
    {
        return mSortBy;
    }

    public void setSortBy(int sortBy)
    {
        mSortBy = sortBy;
    }

    public int getWidth()
    {
        return mWidth;
    }

    public void setWidth(int width)
    {
        mWidth = width;
    }
}
