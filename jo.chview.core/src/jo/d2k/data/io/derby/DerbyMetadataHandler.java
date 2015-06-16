package jo.d2k.data.io.derby;

import java.beans.IntrospectionException;
import java.sql.SQLException;

import jo.d2k.data.data.MetadataBean;
import jo.d2k.data.io.IOMetadataHandler;
import jo.util.dao.derby.DerbyBeanHandler2;
import jo.util.dao.derby.DerbyConnectionHandler;

public class DerbyMetadataHandler extends DerbyBeanHandler2<MetadataBean> implements
        IOMetadataHandler
{
    public DerbyMetadataHandler(DerbyConnectionHandler sqlHandler) throws IntrospectionException, SQLException
    {
        super(sqlHandler, MetadataBean.class);
    }
}
