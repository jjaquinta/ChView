package jo.d2k.data.data;

import jo.d2k.data.logic.schema.ISchemaComparator;
import jo.d2k.data.logic.schema.StarSchemaComparatorLogic;


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
    private ISchemaComparator mComparator;
    
    public StarColumn(int type, String id, String title)
    {
        mType = type;
        mID = id;
        mTitle = title;
        mWidth = 125;
        mSortBy = StarSchemaBean.SORT_BY_TEXT_INSENSITIVE;
        mComparator = StarSchemaComparatorLogic.getComparator(StarSchemaBean.TEXT);
    }
    
    public StarColumn(int type, String id, String title, int width, int sortBy, ISchemaComparator comp)
    {
        mType = type;
        mID = id;
        mTitle = title;
        mWidth = width;
        mSortBy = sortBy;
        mComparator = comp;
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

    public ISchemaComparator getComparator()
    {
        return mComparator;
    }

    public void setComparator(ISchemaComparator comparator)
    {
        mComparator = comparator;
    }
}
