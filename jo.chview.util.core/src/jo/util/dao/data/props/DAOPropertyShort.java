package jo.util.dao.data.props;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import jo.util.dao.IDBUtil;
import jo.util.intro.PropInfo;

public class DAOPropertyShort extends DAOPropertyDirect
{
    public DAOPropertyShort(IDBUtil util, PropInfo info)
    {
        super(util, info);
        mColumnType = "SMALLINT";
    }

    @Override
    protected Object getValueFromResults(ResultSet result) throws Exception
    {
        return new Short(result.getShort(mName));
    }

    @Override
    public void getObjectValue(Object obj, PreparedStatement cmd, int idx) throws Exception
    {
        Object val = mGetter.invoke(obj);
        if (val == null)
            cmd.setNull(idx, Types.SMALLINT);
        else
            cmd.setShort(idx, ((Short)val).shortValue());
    }
}
