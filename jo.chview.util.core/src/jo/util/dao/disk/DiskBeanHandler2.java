/*
 * Created on May 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.dao.disk;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import jo.util.beans.Bean;
import jo.util.dao.IOBeanHandler2;


/**
 * @author jgrant
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
@SuppressWarnings("unchecked")
public abstract class DiskBeanHandler2<TheBean extends Bean> implements IOBeanHandler2<TheBean>
{
    private DiskBeanHandler mProxy;
	
	public DiskBeanHandler2(File baseDir, Class<?> beanClass)
	{
	    mProxy = new DiskBeanHandler(baseDir, beanClass)
	    {
	    };
	}

	public void saveBeans() throws IOException
	{
	    mProxy.saveBeans();
	}
	
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
    public void delete(String field, String value)
    {
        throw new IllegalStateException("Not implemented");
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
        return (TheBean)mProxy.find(colName, colVal);
    }

    @Override
    public TheBean find(String colName, String colVal)
    {
        return (TheBean)mProxy.find(colName, colVal);
    }

    @Override
    public TheBean find(String colName1, String colVal1, String colName2,
            String colVal2)
    {
        return (TheBean)mProxy.find(colName1, colVal1, colName2, colVal2);
    }

    @Override
    public List<TheBean> findMultiple(String colName, long colVal, String sortBy)
    {
        return (List<TheBean>)mProxy.findMultiple(colName, colVal, sortBy);
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
