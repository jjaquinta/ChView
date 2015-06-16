package jo.d2k.data.data;

import java.io.UnsupportedEncodingException;

import jo.util.beans.Bean;
import jo.util.intro.PseudoProp;

public class MetadataBean extends Bean
{
    private String  mDomain;
    private long    mIndex;
    private String  mKey;
    private byte[]  mValue;
    
    public String getDomain()
    {
        return mDomain;
    }
    public void setDomain(String domain)
    {
        mDomain = domain;
    }
    public long getIndex()
    {
        return mIndex;
    }
    public void setIndex(long index)
    {
        mIndex = index;
    }
    public String getKey()
    {
        return mKey;
    }
    public void setKey(String key)
    {
        mKey = key;
    }
    public byte[] getValue()
    {
        return mValue;
    }
    public void setValue(byte[] value)
    {
        mValue = value;
    }
    @PseudoProp
    public String getStringValue()
    {
        if (mValue == null)
            return null;
        try
        {
            return new String(mValue, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return new String(mValue);
        }
    }
    @PseudoProp
    public void setStringValue(String value)
    {
        if (value == null)
            mValue = null;
        else
        {
            try
            {
                mValue = value.getBytes("utf-8");
            }
            catch (UnsupportedEncodingException e)
            {
                mValue = value.getBytes();
            }
        }
    }
}
