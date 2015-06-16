package jo.d2k.data.io.derby;

import java.beans.IntrospectionException;
import java.sql.SQLException;

import jo.d2k.data.data.DeletionBean;
import jo.d2k.data.io.IODeletionHandler;
import jo.util.dao.derby.DerbyBeanHandler2;
import jo.util.dao.derby.DerbyConnectionHandler;

public class DerbyDeletionHandler extends DerbyBeanHandler2<DeletionBean> implements
        IODeletionHandler
{
    public DerbyDeletionHandler(DerbyConnectionHandler sqlHandler) throws IntrospectionException, SQLException
    {
        super(sqlHandler, DeletionBean.class);
    }
}
