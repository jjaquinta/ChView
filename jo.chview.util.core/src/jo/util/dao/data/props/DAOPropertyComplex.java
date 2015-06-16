package jo.util.dao.data.props;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jo.util.dao.IDBUtil;
import jo.util.dao.data.DAOProperty;
import jo.util.intro.PropInfo;

public class DAOPropertyComplex extends DAOProperty
{
    private PropInfo    mParent;
    private DAOProperty mChild;
    
    public DAOPropertyComplex(IDBUtil util, PropInfo parent, DAOProperty child)
    {
        super(util);
        mParent = parent;
        mChild = child;
        setName(mUtil.makeSQLName(mParent.getName())+"_"+mChild.getName());
    }
    
    public void setName(String name) 
    {
        super.setName(name);
        mChild.setName(mName);
    };

    @Override
    public void setObjectValue(ResultSet result, Object obj) throws Exception
    {
        Object val = mParent.getGetter().invoke(obj);
        mChild.setObjectValue(result, val);
    }

    @Override
    public void getObjectValue(Object obj, PreparedStatement cmd, int idx)
            throws Exception
    {
        Object val = mParent.getGetter().invoke(obj);
        mChild.getObjectValue(val, cmd, idx);
    }

    @Override
    public String getColumnType()
    {
        return mChild.getColumnType();
    }

}
