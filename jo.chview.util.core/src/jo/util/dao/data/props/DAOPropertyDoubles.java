package jo.util.dao.data.props;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jo.util.dao.IDBUtil;
import jo.util.intro.PropInfo;

public class DAOPropertyDoubles extends DAOPropertyObjectStream
{
    public DAOPropertyDoubles(IDBUtil util, PropInfo info)
    {
        super(util, info);
    }

    @Override
    protected Object fromObjectStream(ObjectInputStream ois) throws Exception
    {
        int len = ois.readInt();
        double[] txt = new double[len];
        for (int i = 0; i < txt.length; i++)
            txt[i] = ois.readDouble();
        return txt;
    }

    @Override
    protected void toObjectStream(Object val, ObjectOutputStream oos)
            throws Exception
    {
        double[] txt = (double[])val;
        oos.writeInt(txt.length);
        for (double t : txt)
            oos.writeDouble(t);
    }
}
