package jo.util.dao.data.props;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import jo.util.dao.IDBUtil;
import jo.util.intro.PropInfo;

public class DAOPropertyFloat extends DAOPropertyDirect
{
    public DAOPropertyFloat(IDBUtil util, PropInfo info)
    {
        super(util, info);
        mColumnType = "FLOAT";
    }

    @Override
    protected Object getValueFromResults(ResultSet result) throws Exception
    {
        return new Float(result.getFloat(mName));
    }

    @Override
    public void getObjectValue(Object obj, PreparedStatement cmd, int idx) throws Exception
    {
        Object val = mGetter.invoke(obj);
        if (val == null)
            cmd.setNull(idx, Types.FLOAT);
        else
            cmd.setFloat(idx, ((Float)val).floatValue());
    }
}
