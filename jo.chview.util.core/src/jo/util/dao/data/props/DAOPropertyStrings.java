package jo.util.dao.data.props;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jo.util.dao.IDBUtil;
import jo.util.intro.PropInfo;

public class DAOPropertyStrings extends DAOPropertyObjectStream
{
    public DAOPropertyStrings(IDBUtil util, PropInfo info)
    {
        super(util, info);
    }

    @Override
    protected Object fromObjectStream(ObjectInputStream ois) throws Exception
    {
        int len = ois.readInt();
        String[] txt = new String[len];
        for (int i = 0; i < txt.length; i++)
            txt[i] = ois.readUTF();
        return txt;
    }

    @Override
    protected void toObjectStream(Object val, ObjectOutputStream oos)
            throws Exception
    {
        String[] txt = (String[])val;
        oos.writeInt(txt.length);
        for (String t : txt)
            oos.writeUTF(t);
    }
}
