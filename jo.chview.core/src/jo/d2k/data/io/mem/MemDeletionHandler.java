package jo.d2k.data.io.mem;

import jo.d2k.data.data.DeletionBean;
import jo.d2k.data.io.IODeletionHandler;
import jo.util.dao.mem.MemBeanHandler2;

public class MemDeletionHandler extends MemBeanHandler2<DeletionBean> implements
        IODeletionHandler
{
    public MemDeletionHandler()
    {
        super(DeletionBean.class);
    }
}
