package jo.d2k.data.io.mem;

import java.beans.IntrospectionException;
import java.sql.SQLException;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.io.IOStarHandler;
import jo.util.dao.mem.MemBeanHandler2;

public class MemStarHandler extends MemBeanHandler2<StarBean> implements
        IOStarHandler
{
    public MemStarHandler() throws IntrospectionException, SQLException
    {
        super(StarBean.class);
    }
}
