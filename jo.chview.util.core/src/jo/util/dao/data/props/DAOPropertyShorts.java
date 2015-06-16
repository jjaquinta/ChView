package jo.util.dao.data.props;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jo.util.dao.IDBUtil;
import jo.util.intro.PropInfo;

public class DAOPropertyShorts extends DAOPropertyObjectStream
{
    public DAOPropertyShorts(IDBUtil util, PropInfo info)
    {
        super(util, info);
    }

    @Override
    protected Object fromObjectStream(ObjectInputStream ois) throws Exception
    {
        int len = ois.readInt();
        short[] txt = new short[len];
        for (int i = 0; i < txt.length; i++)
            txt[i] = ois.readShort();
        return txt;
    }

    @Override
    protected void toObjectStream(Object val, ObjectOutputStream oos)
            throws Exception
    {
        short[] txt = (short[])val;
        oos.writeInt(txt.length);
        for (short t : txt)
            oos.writeShort(t);
    }
}
