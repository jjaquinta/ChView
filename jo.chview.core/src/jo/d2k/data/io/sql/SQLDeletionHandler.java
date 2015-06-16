package jo.d2k.data.io.sql;

import java.beans.IntrospectionException;
import java.sql.SQLException;

import jo.d2k.data.data.DeletionBean;
import jo.d2k.data.io.IODeletionHandler;
import jo.util.dao.sql.SQLBeanHandler2;
import jo.util.dao.sql.SQLConnectionHandler;

public class SQLDeletionHandler extends SQLBeanHandler2<DeletionBean> implements
        IODeletionHandler
{
    public SQLDeletionHandler(SQLConnectionHandler sqlHandler) throws IntrospectionException, SQLException
    {
        super(sqlHandler, DeletionBean.class);
        mCaching = true;
    }
}
