/*
 * Created on May 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.dao.sql;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jo.util.beans.Bean;
import jo.util.dao.IOBeanHandler;
import jo.util.dao.logic.DAOEvent;
import jo.util.dao.logic.DAOLogic;
import jo.util.dao.logic.SQLUtil;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.IntegerUtils;


/**
 * @author jgrant
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class SQLBeanHandler implements IOBeanHandler
{
    private static final String  ESCAPE = "\ufeff";
    public static final String  NOT_EQUAL = ESCAPE+"<>"+ESCAPE;
    public static final String  GREATERTHAN = ESCAPE+">"+ESCAPE;
    public static final String  LESSTHAN = ESCAPE+"<"+ESCAPE;
    public static final String  GREATERTHANOREQUAL = ESCAPE+">="+ESCAPE;
    public static final String  LESSTHANOREQUAL = ESCAPE+"<="+ESCAPE;
    public static final String  REVERSE = ESCAPE+"DESC"+ESCAPE;
    
	private SQLConnectionHandler	mSQLConnectionHandler;
	private Class<?>					mBeanClass;
	private String					mBeanName;
	private Map<String,PropertyDescriptor>					mDescriptors;
	private String					mSortBy;
	
	public SQLBeanHandler(SQLConnectionHandler sqlHandler, Class<?> beanClass) throws IntrospectionException, SQLException
	{
		mSQLConnectionHandler = sqlHandler;
		mBeanClass = beanClass;
		mBeanName = SQLUtil.makeBeanName(mBeanClass);
		initDescriptors();
		initTable();
		mSortBy = null;
	}
	
	private void initDescriptors() throws IntrospectionException
	{
		mDescriptors = new HashMap<String, PropertyDescriptor>();
		BeanInfo beanInfo = Introspector.getBeanInfo(mBeanClass);
		PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
		for (int i = 0; i < props.length; i++)
		{
			if ((props[i].getReadMethod() == null)
				|| (props[i].getWriteMethod() == null))
				continue; // must be r/w
			String name = props[i].getName();
			mDescriptors.put(name.toLowerCase(), props[i]);
		}
	}

	private void initTable() throws SQLException
	{
		String tableName = SQLUtil.makeSQLName(getTableName());
		ResultSet result = mSQLConnectionHandler.executeQuery("show tables;");
		boolean exists = false;
		while (result.next())
		{
			String name = result.getString(1);
			if (name.equalsIgnoreCase(tableName))
			{
				exists = true;
				break;
			}
		}
		if (!exists)
			makeTable();
		else
			ensureColumns();
	}

	private void ensureColumns() throws SQLException
	{
		DebugUtils.trace("Table " + getTableName() + " exists, ensuring columns.");
		Set<String> missing = new HashSet<String>();
		missing.addAll(mDescriptors.keySet());
		ResultSet result = mSQLConnectionHandler.executeQuery("describe " + SQLUtil.makeSQLName(getTableName()) + ";");
		while (result.next())
		{
			String name = SQLUtil.unmakeSQLName(result.getString(1));
			DebugUtils.trace("Present: "+name);
			missing.remove(name.toLowerCase());
		}
		for (String name : missing)
		{
			String type = ((PropertyDescriptor)mDescriptors.get(name)).getPropertyType().getName();
			String sqlType = SQLUtil.calcSQLType(type);
			StringBuffer cmd = new StringBuffer("ALTER TABLE ");
			cmd.append(SQLUtil.makeSQLName(getTableName()));
			cmd.append(" ADD COLUMN (");
			cmd.append(SQLUtil.makeSQLName(name));
			cmd.append(" ");
			cmd.append(sqlType);
			cmd.append(");");
			mSQLConnectionHandler.executeUpdate(cmd);
		}
	}

	private void makeTable() throws SQLException
	{
	    DebugUtils.info("Table " + getTableName() + " does not exist, creating.");
		StringBuffer cmd = new StringBuffer("CREATE TABLE ");
		cmd.append(SQLUtil.makeSQLName(getTableName()));
		cmd.append(" (");
		boolean anyAdded = false;
		boolean oidAdded = false;
		for (String name : mDescriptors.keySet())
		{
			String type = ((PropertyDescriptor)mDescriptors.get(name)).getPropertyType().getName();
			String sqlType = SQLUtil.calcSQLType(type);
			if (sqlType == null)
				continue;
			if (anyAdded)
				cmd.append(",\n");
			else
				anyAdded = true;
			cmd.append(SQLUtil.makeSQLName(name));
			cmd.append(" ");
			cmd.append(sqlType);
			if (name.equals("oid"))
			{
				oidAdded = true;
				cmd.append(" NOT NULL");
			}
		}
		if (oidAdded)
			cmd.append(", PRIMARY KEY (oid)");
		cmd.append(");");
		mSQLConnectionHandler.executeUpdate(cmd.toString());
	}

	private String getColsAndVals(Bean bean)
	{
		Object[] args = new Object[0];
		StringBuffer cols = new StringBuffer();
		StringBuffer vals = new StringBuffer();
		boolean first = true;
		for (String key : mDescriptors.keySet())
		{
			PropertyDescriptor desc = (PropertyDescriptor)mDescriptors.get(key);
			Object val;
			try
			{
				val = desc.getReadMethod().invoke(bean, args);
			}
			catch (Exception e)
			{
				DebugUtils.error("Error reading value of bean "+bean.getClass().getName(), e);
				continue;
			}
			if (first)
				first = false;
			else
			{
				cols.append(", ");
				vals.append(", ");
			}
			cols.append(SQLUtil.makeSQLName(key));
			if (val == null)
				vals.append("NULL");
			else if (val instanceof String)
			{
				vals.append(SQLUtil.quote((String)val));
			}
			else if (val instanceof Boolean)
			{
				vals.append(((Boolean)val).booleanValue() ? "'T'" : "'F'");
			}
            else if (val instanceof Character)
            {
                vals.append(String.valueOf((int)((Character)val).charValue()));
            }
            else if (val instanceof byte[])
                vals.append(SQLUtil.byteToSQL((byte[])val));
            else if (val instanceof int[])
                vals.append(SQLUtil.byteToSQL(IntegerUtils.toBytes((int[])val)));
            else if (val instanceof boolean[])
                vals.append(SQLUtil.byteToSQL(BooleanUtils.toBytes((boolean[])val)));
			else
				vals.append(val.toString());
		}
		StringBuffer ret = new StringBuffer();
		ret.append("(");
		ret.append(cols);
		ret.append(") VALUES (");
		ret.append(vals);
		ret.append(")");
		return ret.toString();
	}

    private String getCols(Bean bean)
	{
		Object[] args = new Object[0];
		StringBuffer cols = new StringBuffer();
		boolean first = true;
		for (String key : mDescriptors.keySet())
		{
			PropertyDescriptor desc = (PropertyDescriptor)mDescriptors.get(key);
			try
			{
				desc.getReadMethod().invoke(bean, args);
			}
			catch (Exception e)
			{
				DebugUtils.error("Error reading method on bean "+bean.getClass().getName(), e);
				continue;
			}
			if (first)
				first = false;
			else
			{
				cols.append(", ");
			}
			cols.append(SQLUtil.makeSQLName(key));
		}
		return cols.toString();
	}
	
	private String getVals(Bean bean)
	{
		Object[] args = new Object[0];
		StringBuffer vals = new StringBuffer();
		boolean first = true;
		for (String key : mDescriptors.keySet())
		{
			PropertyDescriptor desc = (PropertyDescriptor)mDescriptors.get(key);
			Object val;
			try
			{
				val = desc.getReadMethod().invoke(bean, args);
			}
			catch (Exception e)
			{
				DebugUtils.error("Error reading method on bean "+bean.getClass().getName(), e);
				continue;
			}
			if (first)
				first = false;
			else
			{
				vals.append(", ");
			}
			if (val == null)
				vals.append("NULL");
			else if (val instanceof String)
			{
				vals.append(SQLUtil.quote((String)val));
			}
			else if (val instanceof Boolean)
			{
				vals.append(((Boolean)val).booleanValue() ? "'T'" : "'F'");
			}
            else if (val instanceof Character)
            {
                vals.append(String.valueOf((int)((Character)val).charValue()));
            }
            else if (val instanceof byte[])
                vals.append(SQLUtil.byteToSQL((byte[])val));
            else if (val instanceof int[])
                vals.append(SQLUtil.byteToSQL(IntegerUtils.toBytes((int[])val)));
            else if (val instanceof boolean[])
                vals.append(SQLUtil.byteToSQL(BooleanUtils.toBytes((boolean[])val)));
			else
				vals.append(val.toString());
		}
		return vals.toString();
	}

	protected Bean readBean(ResultSet result) throws SQLException
	{
		Bean ret = newInstance();
		Object[] args = new Object[1];
		for (String name : mDescriptors.keySet()) 
		{
			String sqlName = SQLUtil.makeSQLName(name);
			PropertyDescriptor desc = (PropertyDescriptor)mDescriptors.get(name);
			String type = desc.getPropertyType().getName();
			if (type.equals("java.lang.String"))
				args[0] = SQLUtil.unquote(result.getString(sqlName));
			else if (type.equals("char"))
				args[0] = new Character((char)result.getInt(sqlName));
            else if (type.equals("int"))
                args[0] = new Integer(result.getInt(sqlName));
			else if (type.equals("long"))
				args[0] = new Long(result.getLong(sqlName));
			else if (type.equals("double"))
				args[0] = new Double(result.getDouble(sqlName));
			else if (type.equals("boolean"))
				args[0] = new Boolean(result.getBoolean(sqlName));
            else if (type.equals("[B"))
                args[0] = result.getBytes(sqlName);
            else if (type.equals("[I"))
                args[0] = IntegerUtils.fromBytes(result.getBytes(sqlName));
            else if (type.equals("[Z"))
                args[0] = BooleanUtils.fromBytes(result.getBytes(sqlName));
			try
			{
				desc.getWriteMethod().invoke(ret, args);
			}
			catch (Exception e)
			{
				DebugUtils.error("Error writing method "+name, e);
			}
		}
		return ret;
	}

    /* (non-Javadoc)
     * @see house.wish.io.IOBeanHandler#findByOID(long)
     */
    public List<Bean> find(String[] cols, String[] vals, boolean single, boolean isOr, String sortBy) throws SQLException
    {
        return find(cols, vals, single, isOr, sortBy, false);
    }

    /* (non-Javadoc)
     * @see house.wish.io.IOBeanHandler#findByOID(long)
     */
    public List<Bean> find(String[] cols, String[] vals, boolean single, boolean isOr, String sortBy, boolean fuzzy) throws SQLException
    {
        return find(cols, vals, isOr, sortBy, fuzzy, 0, single ? 1 : 0);
    }

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#findByOID(long)
	 */
	public List<Bean> find(String[] cols, String[] vals, boolean isOr, String sortBy, boolean fuzzy, int offset, int limit) throws SQLException
	{
		StringBuffer cmd = new StringBuffer();
		cmd.append("SELECT * FROM ");
		cmd.append(SQLUtil.makeSQLName(getTableName()));
		if ((cols != null) && (cols.length > 0))
		{
			cmd.append(" WHERE ");
			for (int i = 0; i < cols.length; i++)
			{
				if (i > 0)
					if (isOr)
						cmd.append(" OR ");
					else
						cmd.append(" AND ");
                if (fuzzy)
                {
                    cmd.append(SQLUtil.makeSQLName(cols[i]));
                    cmd.append(" LIKE '%");
                    String val = vals[i];
                    if (!val.startsWith("'"))
                        val = SQLUtil.quote(val);
                    // strip quotes
                    val = val.substring(1, val.length() - 1);
                    cmd.append(val);
                    cmd.append("%' ");
                    
                }
                else
                {
    				cmd.append(SQLUtil.makeSQLName(cols[i]));
                    String val = vals[i];
                    if (val.startsWith(ESCAPE))
                    {
                        val = val.substring(1);
                        int o = val.indexOf(ESCAPE);
                        String op = val.substring(0, o);
                        val = val.substring(o + 1);
                        cmd.append(" ");
                        cmd.append(op);
                        cmd.append(" ");
                    }
                    else
                        cmd.append(" = ");
    				if (val.startsWith("'"))
    				    cmd.append(val);
    				else
    				    cmd.append(SQLUtil.quote(val));
                }
			}
			if (limit > 0)
				cmd.append(" LIMIT "+limit);
            if (offset > 0)
                cmd.append(" OFFSET "+offset);
		}
		if (sortBy != null)
		{
		    cmd.append(" ORDER BY ");
            if (sortBy.startsWith(REVERSE))
            {
                cmd.append(SQLUtil.makeSQLName(sortBy.substring(REVERSE.length())));
                cmd.append(" DESC");
            }
            else
                cmd.append(SQLUtil.makeSQLName(sortBy));
		}
		cmd.append(";");
//		if (cmd.toString().equals("SELECT * FROM log WHERE oid = '1106350613961' LIMIT 1;"))
//		{
//		    Throwable t = new Throwable();
//		    t.printStackTrace();
//		}
		DebugUtils.debug(cmd.toString());
		List<Bean> ret = new ArrayList<Bean>();
		for (int i = 3; i >= 0; i--)
		{
		    try
		    {
				ResultSet result = mSQLConnectionHandler.executeQuery(cmd.toString());
				while (result.next())
				{
					ret.add(readBean(result));
				}
				return ret;
		    }
		    catch (SQLException ex)
		    {
		        if (i == 0)
		            throw ex;
		        ex.printStackTrace();
		    }
		    ret.clear();
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#findByOID(long)
	 */
	public List<Bean> find(String[] cols, String[] vals, boolean single, String sortBy) throws SQLException
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
		catch (SQLException e)
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
		colVals[0] = SQLUtil.quote(colVal);
		List<Bean> ret;
		try
		{
			ret = find(colNames, colVals, true, null);
		}
		catch (SQLException e)
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
		catch (SQLException e)
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
		catch (SQLException e)
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
		catch (SQLException e)
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
		catch (SQLException e)
		{
			DebugUtils.error("Can't find multiple for "+colName1+"="+colVal1+" && "+colName2+"="+colVal2, e);
			return new ArrayList<Bean>();
		}
	}
	
	public abstract String getTableName();

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#newInstance()
	 */
	public Bean newInstance()
	{
		try
		{
			Bean ret = (Bean)mBeanClass.newInstance();
			ret.setOID(mSQLConnectionHandler.getUniqueID());
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
    	synchronized (mSQLConnectionHandler)
    	{
    	    DebugUtils.trace(getTableName()+": doDelete on "+b.getOID());
	        doDelete(b);
	        StringBuffer cmd = new StringBuffer();
	        cmd.append("INSERT INTO ");
	        cmd.append(SQLUtil.makeSQLName(getTableName()));
	        cmd.append(" ");
	        cmd.append(getColsAndVals(b));
	        cmd.append(";");
	        try
	        {
	            mSQLConnectionHandler.executeUpdate(cmd.toString());
	        }
	        catch (SQLException e)
	        {
	            DebugUtils.error("Can't execute "+cmd.toString(), e);
	        }
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
	    Bean b = (Bean)beans.get(0);
		StringBuffer cmd = new StringBuffer();
		cmd.append("REPLACE INTO ");
		cmd.append(SQLUtil.makeSQLName(getTableName()));
		cmd.append(" (");
		cmd.append(getCols(b));
		cmd.append(") VALUES ");
		boolean first = true;
		for (Iterator<Bean> i = beans.iterator(); i.hasNext(); )
		{
		    if (first)
		        first = false;
		    else
		        cmd.append(", ");
		    b = i.next();
		    cmd.append("(");
		    cmd.append(getVals(b));
		    cmd.append(")");
		}
		cmd.append(";");
		try
		{
			mSQLConnectionHandler.executeUpdate(cmd.toString());
		}
		catch (SQLException e)
		{
			DebugUtils.error("Can't execute "+cmd.toString(), e);
		}
        DAOLogic.fireEvent(DAOEvent.UPDATE, mBeanName, b);
	}

    /* (non-Javadoc)
     * @see house.wish.io.IOBeanHandler#delete(house.wish.beans.Bean)
     */
    public void doDelete(Bean b)
    {
        StringBuffer cmd = new StringBuffer();
        cmd.append("DELETE FROM ");
        cmd.append(SQLUtil.makeSQLName(getTableName()));
        cmd.append(" WHERE OID=");
        cmd.append(b.getOID());
        cmd.append(";");
        try
        {
            mSQLConnectionHandler.executeUpdate(cmd.toString());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
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
		StringBuffer cmd = new StringBuffer();
		cmd.append("DELETE FROM ");
		cmd.append(SQLUtil.makeSQLName(getTableName()));
		cmd.append(" WHERE ");
		cmd.append(SQLUtil.makeSQLName(field));
		cmd.append("=");
		cmd.append(SQLUtil.quote(value));
		cmd.append(";");
		try
		{
			mSQLConnectionHandler.executeUpdate(cmd);
		}
		catch (SQLException e)
		{
			DebugUtils.error("Can't execute "+cmd.toString(), e);
		}
        DAOLogic.fireEvent(DAOEvent.DELETION, mBeanName);
	}

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#delete(house.wish.beans.Bean)
	 */
	public void delete(String field1, String value1, String field2, String value2)
	{
		StringBuffer cmd = new StringBuffer();
		cmd.append("DELETE FROM ");
		cmd.append(SQLUtil.makeSQLName(getTableName()));
		cmd.append(" WHERE ");
		cmd.append(SQLUtil.makeSQLName(field1));
		cmd.append("=");
		cmd.append(SQLUtil.quote(value1));
		cmd.append(" AND ");
		cmd.append(SQLUtil.makeSQLName(field2));
		cmd.append("=");
		cmd.append(SQLUtil.quote(value2));
		cmd.append(";");
		try
		{
			mSQLConnectionHandler.executeUpdate(cmd);
		}
		catch (SQLException e)
		{
			DebugUtils.error("Can't execute "+cmd.toString(), e);
		}
		DAOLogic.fireEvent(DAOEvent.DELETION, mBeanName);
	}

	@Override
	public void deleteAll()
	{
        StringBuffer cmd = new StringBuffer();
        cmd.append("DELETE FROM ");
        cmd.append(SQLUtil.makeSQLName(getTableName()));
        cmd.append(";");
        try
        {
            mSQLConnectionHandler.executeUpdate(cmd);
        }
        catch (SQLException e)
        {
            DebugUtils.error("Can't execute "+cmd.toString(), e);
        }
        DAOLogic.fireEvent(DAOEvent.DELETION, mBeanName);
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
		catch (SQLException e)
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
    /**
     * @return Returns the sQLConnectionHandler.
     */
    public SQLConnectionHandler getSQLConnectionHandler()
    {
        return mSQLConnectionHandler;
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
    
    public Collection<String> findColumn(String colName)
    {
		StringBuffer cmd = new StringBuffer();
		cmd.append("SELECT DISTINCT ");
		cmd.append(SQLUtil.makeSQLName(colName));
		cmd.append(" FROM ");
		cmd.append(SQLUtil.makeSQLName(getTableName()));
		cmd.append(";");
		DebugUtils.trace(cmd.toString());
		Set<String> set = new HashSet<String>();
		for (int i = 3; i >= 0; i--)
		{
		    try
		    {
				ResultSet result = getSQLConnectionHandler().executeQuery(cmd.toString());
				while (result.next())
				{
					set.add(result.getString(1));
				}
		    }
		    catch (SQLException ex)
		    {
		        ex.printStackTrace();
		    }
		}
		List<String> ret = new ArrayList<String>();
		ret.addAll(set);
		Collections.sort(ret);
		return set;
    }

    public Map<String,PropertyDescriptor> getDescriptors()
    {
        return mDescriptors;
    }
    
}
