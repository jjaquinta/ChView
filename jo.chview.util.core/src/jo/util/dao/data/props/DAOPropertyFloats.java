package jo.util.dao.data.props;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jo.util.dao.IDBUtil;
import jo.util.intro.PropInfo;

public class DAOPropertyFloats extends DAOPropertyObjectStream
{
    public DAOPropertyFloats(IDBUtil util, PropInfo info)
    {
        super(util, info);
    }

    @Override
    protected Object fromObjectStream(ObjectInputStream ois) throws Exception
    {
        int len = ois.readInt();
        float[] txt = new float[len];
        for (int i = 0; i < txt.length; i++)
            txt[i] = ois.readFloat();
        return txt;
    }

    @Override
    protected void toObjectStream(Object val, ObjectOutputStream oos)
            throws Exception
    {
        float[] txt = (float[])val;
        oos.writeInt(txt.length);
        for (float t : txt)
            oos.writeFloat(t);
    }
}
