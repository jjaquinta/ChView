package jo.util.dao.data.props;

import jo.util.dao.IDBUtil;
import jo.util.intro.PropInfo;

public class DAOPropertyBytes extends DAOPropertyBinary
{
    public DAOPropertyBytes(IDBUtil util, PropInfo info)
    {
        super(util, info);        
    }

    protected Object fromBytes(byte[] bytes) throws Exception
    {
        return bytes;
    }
    
    protected byte[] toBytes(Object val) throws Exception
    {
        return (byte[])val;
    }
}
