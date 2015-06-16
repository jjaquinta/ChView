package jo.util.dao.data.props;

import java.lang.reflect.Method;
import java.sql.ResultSet;

import jo.util.dao.IDBUtil;
import jo.util.dao.data.DAOProperty;
import jo.util.intro.PropInfo;

public abstract class DAOPropertyDirect extends DAOProperty
{
    protected String  mColumnType;
    protected Method  mGetter;
    protected Method  mSetter;
    
    public DAOPropertyDirect(IDBUtil util, PropInfo info)
    {
        super(util);
        mName = mUtil.makeSQLName(info.getName());
        mGetter = info.getGetter();
        mSetter = info.getSetter();        
    }

    protected abstract Object getValueFromResults(ResultSet result) throws Exception;
    
    @Override
    public void setObjectValue(ResultSet result, Object obj) throws Exception
    {
        Object val = getValueFromResults(result);
        mSetter.invoke(obj, val);
    }

    @Override
    public String getColumnType()
    {
        return mColumnType;
    }
}
