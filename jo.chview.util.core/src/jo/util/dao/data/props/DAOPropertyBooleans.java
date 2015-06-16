package jo.util.dao.data.props;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import jo.util.dao.IDBUtil;
import jo.util.dao.data.DAOProperty;
import jo.util.intro.PropInfo;
import jo.util.utils.obj.BooleanUtils;

public class DAOPropertyBooleans extends DAOProperty
{
    private Method  mGetter;
    private Method  mSetter;
    
    public DAOPropertyBooleans(IDBUtil util, PropInfo info)
    {
        super(util);
        mName = mUtil.makeSQLName(info.getName());
        mGetter = info.getGetter();
        mSetter = info.getSetter();        
    }
    
    public DAOPropertyBooleans(IDBUtil util, String name, Method getter, Method setter)
    {
        super(util);
        mName = name;
        mGetter = getter;
        mSetter = setter;        
    }

    @Override
    public void setObjectValue(ResultSet result, Object obj) throws Exception
    {
        String txt = mUtil.unquote(result.getString(mName));
        boolean[] val = new boolean[txt.length()];
        for (int i = 0; i < txt.length(); i++)
            val[i] = BooleanUtils.parseBoolean(txt.substring(i, i+1));
        mSetter.invoke(obj, val);
    }

    @Override
    public void getObjectValue(Object obj, PreparedStatement cmd, int idx) throws Exception
    {
        boolean[] val = (boolean[])mGetter.invoke(obj);
        if (val == null)
            cmd.setNull(idx, Types.CHAR);
        else
        {
            StringBuffer sb = new StringBuffer();
            for (boolean b : val)
                if (b)
                    sb.append("T");
                else
                    sb.append("F");
            cmd.setString(idx, sb.toString());
        }
    }

    @Override
    public String getColumnType()
    {
        return mUtil.calcSQLType("java.lang.String");
    }
}
