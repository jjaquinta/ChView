package jo.d2k.data.io.sql;

import java.beans.IntrospectionException;
import java.sql.SQLException;

import jo.d2k.data.data.MetadataBean;
import jo.d2k.data.io.IOMetadataHandler;
import jo.util.dao.sql.SQLBeanHandler2;
import jo.util.dao.sql.SQLConnectionHandler;

public class SQLMetadataHandler extends SQLBeanHandler2<MetadataBean> implements
        IOMetadataHandler
{
    public SQLMetadataHandler(SQLConnectionHandler sqlHandler) throws IntrospectionException, SQLException
    {
        super(sqlHandler, MetadataBean.class);
        mCaching = true;
    }
}
