package jo.util.dao.data.props;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jo.util.dao.IDBUtil;
import jo.util.intro.PropInfo;

public abstract class DAOPropertyObjectStream extends DAOPropertyBinary
{
    public DAOPropertyObjectStream(IDBUtil util, PropInfo info)
    {
        super(util, info);        
    }

    protected Object fromBytes(byte[] bytes) throws Exception
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return fromObjectStream(ois);
    }
    
    protected byte[] toBytes(Object val) throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        toObjectStream(val, oos);
        oos.flush();
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    protected abstract Object fromObjectStream(ObjectInputStream ois) throws Exception;
    protected abstract void toObjectStream(Object val, ObjectOutputStream oos) throws Exception;
}
