package jo.d2k.data.io.mem;

import jo.d2k.data.data.MetadataBean;
import jo.d2k.data.io.IOMetadataHandler;
import jo.util.dao.mem.MemBeanHandler2;

public class MemMetadataHandler extends MemBeanHandler2<MetadataBean> implements
        IOMetadataHandler
{
    public MemMetadataHandler()
    {
        super(MetadataBean.class);
    }
}
