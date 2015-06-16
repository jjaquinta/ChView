package jo.util.dao.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jo.util.dao.IDBUtil;

public abstract class DAOProperty
{
    protected IDBUtil   mUtil;
    protected String    mName;
    
    public DAOProperty(IDBUtil util)
    {
        mUtil = util;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }
    
    public abstract void setObjectValue(ResultSet result, Object obj) throws Exception;
    //public abstract String getObjectValue(Object obj) throws Exception;
    public abstract void getObjectValue(Object obj, PreparedStatement cmd, int idx) throws Exception;
    public abstract String getColumnType();
}
