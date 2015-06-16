package jo.d2k.data.io.derby;

import java.beans.IntrospectionException;
import java.sql.SQLException;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.io.IOStarHandler;
import jo.util.dao.derby.DerbyBeanHandler2;
import jo.util.dao.derby.DerbyConnectionHandler;

public class DerbyStarHandler extends DerbyBeanHandler2<StarBean> implements
        IOStarHandler
{
    public DerbyStarHandler(DerbyConnectionHandler sqlHandler) throws IntrospectionException, SQLException
    {
        super(sqlHandler, StarBean.class);
    }
}
