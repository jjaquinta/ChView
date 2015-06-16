package jo.d2k.data.io.sql;

import java.beans.IntrospectionException;
import java.sql.SQLException;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.io.IOStarHandler;
import jo.util.dao.sql.SQLBeanHandler2;
import jo.util.dao.sql.SQLConnectionHandler;

public class SQLStarHandler extends SQLBeanHandler2<StarBean> implements
        IOStarHandler
{
    public SQLStarHandler(SQLConnectionHandler sqlHandler) throws IntrospectionException, SQLException
    {
        super(sqlHandler, StarBean.class);
        mCaseSensitiveColumns.add("Quadrant");
        mCaching = true;
    }
}
