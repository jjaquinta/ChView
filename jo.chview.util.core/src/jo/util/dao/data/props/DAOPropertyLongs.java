package jo.util.dao.data.props;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jo.util.dao.IDBUtil;
import jo.util.intro.PropInfo;

public class DAOPropertyLongs extends DAOPropertyObjectStream
{
    public DAOPropertyLongs(IDBUtil util, PropInfo info)
    {
        super(util, info);
    }

    @Override
    protected Object fromObjectStream(ObjectInputStream ois) throws Exception
    {
        int len = ois.readInt();
        long[] txt = new long[len];
        for (int i = 0; i < txt.length; i++)
            txt[i] = ois.readLong();
        return txt;
    }

    @Override
    protected void toObjectStream(Object val, ObjectOutputStream oos)
            throws Exception
    {
        long[] txt = (long[])val;
        oos.writeInt(txt.length);
        for (long t : txt)
            oos.writeLong(t);
    }
}
