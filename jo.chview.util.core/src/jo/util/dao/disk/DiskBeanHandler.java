/*
 * Created on May 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.dao.disk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jo.util.beans.Bean;
import jo.util.dao.IOBeanHandler;
import jo.util.dao.logic.DAOEvent;
import jo.util.dao.logic.DAOLogic;
import jo.util.utils.BeanUtils;
import jo.util.utils.DebugUtils;
import jo.util.utils.xml.XMLEditUtils;
import jo.util.utils.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * @author jgrant
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class DiskBeanHandler implements IOBeanHandler
{
    private File                    mBaseDir;
    private File                    mDataFile;
	private Class<?>				mBeanClass;
	private String					mBeanName;
	private String					mSortBy;
    private Map<Long,Bean>        mBeans;
    private boolean                 mLoaded;
    private boolean                 mSaved;
	
	public DiskBeanHandler(File baseDir, Class<?> beanClass)
	{
		mBaseDir = baseDir;
		mBeanClass = beanClass;
		mBeanName = DiskUtils.makeBeanName(mBeanClass);
        mBeans = new HashMap<Long, Bean>();
		mSortBy = null;
        mDataFile = new File(mBaseDir, getTableName()+".xml");
        mLoaded = false;
        mSaved = true;
        DiskUtils.addToSaveList(this);
	}
    
    public void saveBeans() throws IOException
    {
        if (mSaved)
            return;
        List<Bean> list = new ArrayList<Bean>();
        synchronized (mBeans)
        {
//            ArrayList list = new ArrayList();
//            list.addAll(mBeans.values());
//            ObjectOutputStream oos = EncodeUtils.getOutputStream(mDataFile);
//            oos.writeObject(list);
//            oos.close();
            list.addAll(mBeans.values());
            Document doc = XMLUtils.newDocument();
            //XMLBeanUtils.toXML(doc, list);
            Node beans = XMLEditUtils.addElement(doc, "beans");
            for (Bean bean : list)
                DiskXMLUtils.toXML(beans, bean);
            XMLUtils.writeFile(doc, mDataFile);
            mSaved = true;
        }
    }
    
    private void loadBeans() throws IOException
    {
        if (mLoaded)
            return;
        synchronized (mBeans)
        {
            if (mDataFile.exists())
            {
//                ObjectInputStream ois = DecodeUtils.getInputStream(mDataFile);
//                ArrayList list;
//                try
//                {
//                    list = (ArrayList)ois.readObject();
//                }
//                catch (ClassNotFoundException e)
//                {
//                    throw new IOException(e.toString());
//                }
//                ois.close();
                Document doc = XMLUtils.readFile(mDataFile);
                List<Bean> list;
                try
                {
//                    list = XMLBeanUtils.fromXMLContainer(doc.getFirstChild(), this.getClass().getClassLoader());
                    list = new ArrayList<Bean>();
                    for (Node n = doc.getFirstChild().getFirstChild(); n != null; n = n.getNextSibling())
                    {
                        if (n.getNodeName().startsWith("#"))
                            continue;
                        Bean b = DiskXMLUtils.fromXML(n, this.getClass().getClassLoader());
                        list.add(b);
                    }
                }
                catch (Exception e)
                {
                    return;
                }
                mBeans.clear();
                for (Bean bean : list)
                    mBeans.put(new Long(bean.getOID()), bean);
            }
            mLoaded = true;
        }
    }

    /* (non-Javadoc)
     * @see house.wish.io.IOBeanHandler#findByOID(long)
     */
    public List<Bean> find(String[] cols, String[] vals, boolean single, boolean isOr, String sortBy) throws IOException
    {
        return find(cols, vals, single, isOr, sortBy, false);
    }

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#findByOID(long)
	 */
	public List<Bean> find(String[] cols, String[] vals, boolean single, boolean isOr, String sortBy, boolean fuzzy) throws IOException
	{
        loadBeans();
        List<Bean> ret = new ArrayList<Bean>();
        if (cols == null)
            ret.addAll(mBeans.values());
        else
        {
            for (Bean b : mBeans.values())
            {
                if (BeanUtils.match(b, cols, vals, isOr, fuzzy))
                {
                    ret.add(b);
                    if (single)
                        break;
                }
            }
        }
        BeanUtils.sort(ret, sortBy);
		return ret;
	}

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#findByOID(long)
	 */
	public List<Bean> find(String[] cols, String[] vals, boolean single, String sortBy) throws IOException
	{
		return find(cols, vals, single, false, sortBy);
	}
	
	public Bean find(String colName, long colVal)
	{
		String[] colNames = new String[1];
		colNames[0] = colName;
		String[] colVals = new String[1];
		colVals[0] = String.valueOf(colVal);
		List<Bean> ret;
		try
		{
			ret = find(colNames, colVals, true, null);
		}
		catch (IOException e)
		{
			DebugUtils.error("Can't perform find", e);
			return null;
		}
		if (ret.size() == 0)
			return null;
		return (Bean)ret.get(0);
	}
	
	public Bean find(String colName, String colVal)
	{
		String[] colNames = new String[1];
		colNames[0] = colName;
		String[] colVals = new String[1];
		colVals[0] = colVal;
		List<Bean> ret;
		try
		{
			ret = find(colNames, colVals, true, null);
		}
		catch (IOException e)
		{
			DebugUtils.error("Can't do find", e);
			return null;
		}
		if (ret.size() == 0)
			return null;
		return (Bean)ret.get(0);
	}

	/* (non-Javadoc)
	 * @see house.list.io.IOMailingListAddressHandler#findByMailingList(long)
	 */
	public Bean find(String colName1, String colVal1, String colName2, String colVal2)
	{
		String[] colNames = new String[2];
		colNames[0] = colName1;
		colNames[1] = colName2;
		String[] colVals = new String[2];
		colVals[0] = colVal1;
		colVals[1] = colVal2;
		List<Bean> ret;
		try
		{
			ret = find(colNames, colVals, true, null);
		}
		catch (IOException e)
		{
			DebugUtils.error("Can't find "+colName1+"="+colVal1+" && "+colName2+"="+colVal2, e);
			return null;
		}
		if (ret.size() == 0)
			return null;
		return (Bean)ret.get(0);
	}
	
	public List<Bean> findMultiple(String colName, long colVal, String sortBy)
	{
		String[] colNames = new String[1];
		colNames[0] = colName;
		String[] colVals = new String[1];
		colVals[0] = String.valueOf(colVal);
		try
		{
			return find(colNames, colVals, false, sortBy);
		}
		catch (IOException e)
		{
			DebugUtils.error("Can't find multiple for "+colName+"="+colVal+" in "+mBeanClass.getName(), e);
			return new ArrayList<Bean>();
		}
	}
	
	public List<Bean> findMultiple(String colName, String colVal, String sortBy)
	{
		String[] colNames = new String[1];
		colNames[0] = colName;
		String[] colVals = new String[1];
		colVals[0] = colVal;
		try
		{
			return find(colNames, colVals, false, sortBy);
		}
		catch (IOException e)
		{
			DebugUtils.error("Can't find multiple for "+colName+"="+colVal, e);
			return new ArrayList<Bean>();
		}
	}
	
	public List<Bean> findMultiple(String colName1, String colVal1, String colName2, String colVal2, String sortBy)
	{
		String[] colNames = new String[2];
		colNames[0] = colName1;
		colNames[1] = colName2;
		String[] colVals = new String[2];
		colVals[0] = colVal1;
		colVals[1] = colVal2;
		try
		{
			return find(colNames, colVals, false, sortBy);
		}
		catch (IOException e)
		{
			DebugUtils.error("Can't find multiple for "+colName1+"="+colVal1+" && "+colName2+"="+colVal2, e);
            return new ArrayList<Bean>();
		}
	}
	
	public String getTableName()
	{
	    String name = mBeanClass.getSimpleName();
	    if (name.endsWith("Bean"))
	        name = name.substring(0, name.length() - 4);
	    int o = name.lastIndexOf('.');
	    if (o >= 0)
	        name = name.substring(0 + 1);
	    return name;
	}

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#newInstance()
	 */
	public Bean newInstance()
	{
		try
		{
			Bean ret = (Bean)mBeanClass.newInstance();
			ret.setOID(DiskUtils.getUniqueID());
			return ret;
		}
		catch (Exception e)
		{
			DebugUtils.error("Can't make new instance of bean.", e);
			return null;
		}
	}

    /* (non-Javadoc)
     * @see house.wish.io.IOBeanHandler#update(house.wish.beans.Bean)
     */
    public void doUpdate(Bean b)
    {
        doDelete(b);
        synchronized (mBeans)
        {
            mBeans.put(new Long(b.getOID()), b);
            mSaved = false;
        }
    }

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#update(house.wish.beans.Bean)
	 */
	public void update(Bean b)
	{
        doUpdate(b);
        DAOLogic.fireEvent(DAOEvent.UPDATE, mBeanName, b);
	}

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#update(house.wish.beans.Bean)
	 */
	public void update(List<Bean> beans)
	{
	    if (beans.size() == 0)
	        return;
		for (Bean b : beans)
		    doUpdate(b);
		DAOLogic.fireEvent(DAOEvent.UPDATE, mBeanName, (Bean)beans.get(0));
	}

    /* (non-Javadoc)
     * @see house.wish.io.IOBeanHandler#delete(house.wish.beans.Bean)
     */
    public void doDelete(Bean b)
    {
        synchronized (mBeans)
        {
            try
            {
                loadBeans();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            mBeans.remove(new Long(b.getOID()));
            mSaved = false;
        }
    }

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#delete(house.wish.beans.Bean)
	 */
	public void delete(Bean b)
	{
        doDelete(b);
        DAOLogic.fireEvent(DAOEvent.DELETION, mBeanName, b);
	}

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#delete(house.wish.beans.Bean)
	 */
	public void delete(String field, String value)
	{
        delete(find(field, value));
	}

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#delete(house.wish.beans.Bean)
	 */
	public void delete(String field1, String value1, String field2, String value2)
	{
        delete(find(field1, value1, field2, value2));
	}

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#findAll()
	 */
	public List<Bean> findAll()
	{
		try
		{
			return find(null, null, false, mSortBy);
		}
		catch (IOException e)
		{
			DebugUtils.error("Can't find all", e);
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean isValidOID(long oid)
	{
		return findByOID(oid) != null;
	}

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#findByOID(long)
	 */
	public Bean findByOID(long oid)
	{
		return find("oid", oid);
	}

	/* (non-Javadoc)
	 * @see autodev.dod.io.IOBeanHandler#getBeanType()
	 */
	public Class<?> getBeanType()
	{
		return mBeanClass;
	}
    public String getSortBy()
    {
        return mSortBy;
    }
    public void setSortBy(String sortBy)
    {
        mSortBy = sortBy;
    }

    public void update(Collection<Bean> beans)
    {
        for (Bean b : beans)
            doUpdate(b);
        DAOLogic.fireEvent(DAOEvent.UPDATE, mBeanName, null);
    }

    public void delete(Collection<Bean> beans)
    {
        for (Bean b : beans)
            doDelete(b);
        DAOLogic.fireEvent(DAOEvent.DELETION, mBeanName, null);
    }
    
    @Override
    public void deleteAll()
    {
        synchronized (mBeans)
        {
            try
            {
                loadBeans();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            mBeans.clear();
            mSaved = false;
        }
        DAOLogic.fireEvent(DAOEvent.DELETION, mBeanName, null);
    }
    
    public Collection<String> findColumn(String colName)
    {
    	Set<String> set = new HashSet<String>();
        for (Bean b : mBeans.values())
        {
            Object v = BeanUtils.get(b, colName);
            if (v != null)
            	set.add(v.toString());
        }
		List<String> ret = new ArrayList<String>();
		ret.addAll(set);
		Collections.sort(ret);
		return set;
    }
}
