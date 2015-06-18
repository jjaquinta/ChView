package jo.d2k.data.data;

import jo.util.beans.Bean;

public class StarSchemaBean extends Bean
{
    // copied from GenericTableTreeViewer2
    public static final int SORT_BY_TEXT = 0;
    public static final int SORT_BY_NUMBER = 1;
    public static final int SORT_BY_TEXT_INSENSITIVE = 2;

    public static final int TEXT = 0;
    public static final int INTEGER = 1;
    public static final int DOUBLE = 2;
    public static final int CHOICE = 3;
    public static final int TAGS = 4;
    public static final int LINK = 5;
    public static final int AND = 6;
    public static final int OR = 7;
    public static final int NOT = 8;
    
    public static final String[] LABELS = {
        "Text",
        "Integer",
        "Floating Point",
        "Choice",
        "Tags",
        "Link",
        "And",
        "Or",
        "Not",
    };
    
    private String  mMetadataID;
    private int     mType;
    private int     mIndex;
    private int     mWidth;
    private int     mSortBy;
    private String  mTitle;
    private String[] mSubType;
    
    public StarSchemaBean()
    {
        mTitle = "";
        mType = TEXT;
        mSubType = new String[0];
        mWidth = 125;
        mSortBy = SORT_BY_TEXT_INSENSITIVE;
    }
    
    public String getTitle()
    {
        return mTitle;
    }
    public void setTitle(String title)
    {
        mTitle = title;
    }
    public int getType()
    {
        return mType;
    }
    public void setType(int type)
    {
        mType = type;
    }
    public String[] getSubType()
    {
        return mSubType;
    }
    public void setSubType(String[] subType)
    {
        mSubType = subType;
    }

    public int getIndex()
    {
        return mIndex;
    }

    public void setIndex(int index)
    {
        mIndex = index;
    }

    public String getMetadataID()
    {
        return mMetadataID;
    }

    public void setMetadataID(String metadataID)
    {
        mMetadataID = metadataID;
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
