package jo.util.dao.data.props;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import jo.util.dao.IDBUtil;
import jo.util.dao.data.DAOProperty;
import jo.util.intro.PropInfo;

public abstract class DAOPropertyBinary extends DAOProperty
{
    protected Method  mGetter;
    protected Method  mSetter;
    
    public DAOPropertyBinary(IDBUtil util, PropInfo info)
    {
        super(util);
        mName = mUtil.makeSQLName(info.getName());
        mGetter = info.getGetter();
        mSetter = info.getSetter();        
    }

    protected abstract Object fromBytes(byte[] bytes) throws Exception;
    protected abstract byte[] toBytes(Object val) throws Exception;
    
    @Override
    public void setObjectValue(ResultSet result, Object obj) throws Exception
    {
        byte[] bytes = result.getBytes(mName);
        Object val = fromBytes(bytes);
        mSetter.invoke(obj, val);
    }

    @Override
    public void getObjectValue(Object obj, PreparedStatement cmd, int idx) throws Exception
    {
        Object val = mGetter.invoke(obj);
        if (val == null)
            cmd.setNull(idx, Types.BLOB);
        else
        {
            byte[] bytes = toBytes(val);
            cmd.setBytes(idx, bytes);
        }
    }

    @Override
    public String getColumnType()
    {
        return "BLOB";
    }
}
