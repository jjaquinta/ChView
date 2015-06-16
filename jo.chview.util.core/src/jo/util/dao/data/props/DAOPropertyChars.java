package jo.util.dao.data.props;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import jo.util.dao.IDBUtil;
import jo.util.dao.data.DAOProperty;
import jo.util.intro.PropInfo;

public class DAOPropertyChars extends DAOProperty
{
    private Method  mGetter;
    private Method  mSetter;
    
    public DAOPropertyChars(IDBUtil util, PropInfo info)
    {
        super(util);
        mName = mUtil.makeSQLName(info.getName());
        mGetter = info.getGetter();
        mSetter = info.getSetter();        
    }
    
    public DAOPropertyChars(IDBUtil util, String name, Method getter, Method setter)
    {
        super(util);
        mName = name;
        mGetter = getter;
        mSetter = setter;        
    }

    @Override
    public void setObjectValue(ResultSet result, Object obj) throws Exception
    {
        String txt = result.getString(mName);
        Object val = txt.toCharArray();
        mSetter.invoke(obj, val);
    }

    @Override
    public void getObjectValue(Object obj, PreparedStatement cmd, int idx) throws Exception
    {
        Object val = mGetter.invoke(obj);
        if (val == null)
            cmd.setNull(idx, Types.CHAR);
        else
            cmd.setString(idx, new String((char[])val));
    }

    @Override
    public String getColumnType()
    {
        return mUtil.calcSQLType("java.lang.String");
    }
}
