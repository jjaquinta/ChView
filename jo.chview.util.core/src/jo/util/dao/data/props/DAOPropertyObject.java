package jo.util.dao.data.props;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jo.util.dao.IDBUtil;
import jo.util.intro.PropInfo;

public class DAOPropertyObject extends DAOPropertyObjectStream
{
    public DAOPropertyObject(IDBUtil util, PropInfo info)
    {
        super(util, info);
    }

    @Override
    protected Object fromObjectStream(ObjectInputStream ois) throws Exception
    {
        return ois.readObject();
    }

    @Override
    protected void toObjectStream(Object val, ObjectOutputStream oos)
            throws Exception
    {
        oos.writeObject(val);
    }
}
