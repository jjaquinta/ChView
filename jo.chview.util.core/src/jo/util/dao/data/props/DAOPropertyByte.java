package jo.util.dao.data.props;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import jo.util.dao.IDBUtil;
import jo.util.intro.PropInfo;

public class DAOPropertyByte extends DAOPropertyDirect
{
    public DAOPropertyByte(IDBUtil util, PropInfo info)
    {
        super(util, info);
        mColumnType = "TINYINT";
    }

    @Override
    protected Object getValueFromResults(ResultSet result) throws Exception
    {
        return new Byte(result.getByte(mName));
    }

    @Override
    public void getObjectValue(Object obj, PreparedStatement cmd, int idx) throws Exception
    {
        Object val = mGetter.invoke(obj);
        if (val == null)
            cmd.setNull(idx, Types.TINYINT);
        else
            cmd.setByte(idx, ((Byte)val).byteValue());
    }
}
