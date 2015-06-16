package jo.util.dao.data.props;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import jo.util.dao.IDBUtil;
import jo.util.intro.PropInfo;

public class DAOPropertyLong extends DAOPropertyDirect
{
    public DAOPropertyLong(IDBUtil util, PropInfo info)
    {
        super(util, info);
        mColumnType = "BIGINT";
    }

    @Override
    protected Object getValueFromResults(ResultSet result) throws Exception
    {
        return new Long(result.getLong(mName));
    }

    @Override
    public void getObjectValue(Object obj, PreparedStatement cmd, int idx) throws Exception
    {
        Object val = mGetter.invoke(obj);
        if (val == null)
            cmd.setNull(idx, Types.BIGINT);
        else
            cmd.setLong(idx, ((Long)val).longValue());
    }
}
