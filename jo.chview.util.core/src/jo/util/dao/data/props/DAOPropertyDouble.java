package jo.util.dao.data.props;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import jo.util.dao.IDBUtil;
import jo.util.intro.PropInfo;

public class DAOPropertyDouble extends DAOPropertyDirect
{
    public DAOPropertyDouble(IDBUtil util, PropInfo info)
    {
        super(util, info);
        mColumnType = "DOUBLE";
    }

    @Override
    protected Object getValueFromResults(ResultSet result) throws Exception
    {
        return new Double(result.getDouble(mName));
    }

    @Override
    public void getObjectValue(Object obj, PreparedStatement cmd, int idx) throws Exception
    {
        Object val = mGetter.invoke(obj);
        if (val == null)
            cmd.setNull(idx, Types.DOUBLE);
        else
            cmd.setDouble(idx, ((Double)val).doubleValue());
    }
}
