package jo.util.dao.data.props;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jo.util.dao.IDBUtil;
import jo.util.intro.PropInfo;

public class DAOPropertyInts extends DAOPropertyObjectStream
{
    public DAOPropertyInts(IDBUtil util, PropInfo info)
    {
        super(util, info);
    }

    @Override
    protected Object fromObjectStream(ObjectInputStream ois) throws Exception
    {
        int len = ois.readInt();
        int[] txt = new int[len];
        for (int i = 0; i < txt.length; i++)
            txt[i] = ois.readInt();
        return txt;
    }

    @Override
    protected void toObjectStream(Object val, ObjectOutputStream oos)
            throws Exception
    {
        int[] txt = (int[])val;
        oos.writeInt(txt.length);
        for (int t : txt)
            oos.writeInt(t);
    }
}
