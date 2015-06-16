/*
 * Created on May 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.dao.sql;

import java.beans.IntrospectionException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jo.util.beans.Bean;
import jo.util.dao.IOBeanHandler2;
import jo.util.dao.logic.SQLUtil;
import jo.util.utils.DebugUtils;


/**
 * @author jgrant
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
@SuppressWarnings("unchecked")
public abstract class SQLBeanHandler2Old<TheBean extends Bean> implements IOBeanHandler2<TheBean>
{
    private SQLBeanHandler mProxy;
	
	public SQLBeanHandler2Old(SQLConnectionHandler sqlHandler, Class<?> beanClass) throws IntrospectionException, SQLException
	{
	    mProxy = new SQLBeanHandler(sqlHandler, beanClass)
	    {
	        @Override
	        public String getTableName()
	        {
	            return SQLBeanHandler2Old.this.getTableName();
	        }
	    };
	}
	
	public abstract String getTableName();
	
	@Override
    public TheBean newInstance()
    {
        return (TheBean)mProxy.newInstance();
    }

    @Override
    public void update(TheBean bean)
    {
        mProxy.update((Bean)bean);
    }

    @Override
    public void update(Collection<TheBean> beans)
    {
        mProxy.update((Collection<Bean>)beans);
    }

    @Override
    public void delete(TheBean bean)
    {
        mProxy.delete(bean);
    }

    @Override
    public void delete(Collection<TheBean> beans)
    {
        mProxy.delete((Collection<Bean>)beans);
    }

    @Override
    public void deleteAll()
    {
        mProxy.deleteAll();
    }

    @Override
    public TheBean findByOID(long oid)
    {
        return (TheBean)mProxy.findByOID(oid);
    }

    @Override
    public List<TheBean> findAll()
    {
        return (List<TheBean>)mProxy.findAll();
    }

    @Override
    public Class<?> getBeanType()
    {
        return mProxy.getBeanType();
    }

    @Override
    public boolean isValidOID(long oid)
    {
        return mProxy.isValidOID(oid);
    }

    @Override
    public Collection<String> findColumn(String colName)
    {
        return mProxy.findColumn(colName);
    }

    @Override
    public List<TheBean> find(String[] cols, String[] vals, boolean single,
            boolean isOr, String sortBy) throws Exception
    {
        return (List<TheBean>)mProxy.find(cols, vals, single, isOr, sortBy);
    }

    @Override
    public List<TheBean> find(String[] cols, String[] vals, boolean single,
            boolean isOr, String sortBy, boolean fuzzy) throws Exception
    {
        return (List<TheBean>)mProxy.find(cols, vals, single, isOr, sortBy, fuzzy);
    }

    @Override
    public List<TheBean> find(String[] cols, String[] vals, boolean single,
            String sortBy) throws Exception
    {
        return (List<TheBean>)mProxy.find(cols, vals, single, sortBy);
    }

    @Override
    public TheBean find(String colName, long colVal)
    {
        return find(colName, colVal);
    }

    @Override
    public TheBean find(String colName, String colVal)
    {
        String[] colNames = new String[1];
        colNames[0] = colName;
        String[] colVals = new String[1];
        colVals[0] = SQLUtil.quote(colVal);
        List<TheBean> ret;
        try
        {
            ret = find(colNames, colVals, true, null);
        }
        catch (Exception e)
        {
            DebugUtils.error("Can't do find", e);
            return null;
        }
        if (ret.size() == 0)
            return null;
        return ret.get(0);
    }

    @Override
    public TheBean find(String colName1, String colVal1, String colName2,
            String colVal2)
    {
        return find(colName1, colVal1, colName2, colVal2);
    }

    @Override
    public List<TheBean> findMultiple(String colName, long colVal, String sortBy)
    {
        String[] colNames = new String[1];
        colNames[0] = colName;
        String[] colVals = new String[1];
        colVals[0] = String.valueOf(colVal);
        try
        {
            return find(colNames, colVals, false, sortBy);
        }
        catch (Exception e)
        {
            DebugUtils.error("Can't find multiple for "+colName+"="+colVal+" in "+getBeanType().getName(), e);
            return new ArrayList<TheBean>();
        }
    }

    @Override
    public List<TheBean> findMultiple(String colName, String colVal,
            String sortBy)
    {
        return (List<TheBean>)mProxy.findMultiple(colName, colVal, sortBy);
    }

    @Override
    public List<TheBean> findMultiple(String colName1, String colVal1,
            String colName2, String colVal2, String sortBy)
    {
        return (List<TheBean>)mProxy.findMultiple(colName1, colVal1, colName2, colVal2, sortBy);
    }
}
