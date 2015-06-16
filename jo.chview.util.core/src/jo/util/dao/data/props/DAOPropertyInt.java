package jo.util.dao.data.props;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import jo.util.dao.IDBUtil;
import jo.util.intro.PropInfo;

public class DAOPropertyInt extends DAOPropertyDirect
{
    public DAOPropertyInt(IDBUtil util, PropInfo info)
    {
        super(util, info);
        mColumnType = "INT";
    }

    @Override
    protected Object getValueFromResults(ResultSet result) throws Exception
    {
        return new Integer(result.getInt(mName));
    }

    @Override
    public void getObjectValue(Object obj, PreparedStatement cmd, int idx) throws Exception
    {
        Object val = mGetter.invoke(obj);
        if (val == null)
            cmd.setNull(idx, Types.INTEGER);
        else
            cmd.setInt(idx, ((Integer)val).intValue());
    }
}
