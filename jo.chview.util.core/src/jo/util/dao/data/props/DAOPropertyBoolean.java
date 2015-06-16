package jo.util.dao.data.props;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import jo.util.dao.IDBUtil;
import jo.util.intro.PropInfo;

public class DAOPropertyBoolean extends DAOPropertyDirect
{
    public DAOPropertyBoolean(IDBUtil util, PropInfo info)
    {
        super(util, info);
        mColumnType = "TINYINT";
    }

    @Override
    protected Object getValueFromResults(ResultSet result) throws Exception
    {
        return new Boolean(result.getBoolean(mName));
    }

    @Override
    public void getObjectValue(Object obj, PreparedStatement cmd, int idx) throws Exception
    {
        Object val = mGetter.invoke(obj);
        if (val == null)
            cmd.setNull(idx, Types.TINYINT);
        else
            cmd.setBoolean(idx, ((Boolean)val).booleanValue());
    }
}
