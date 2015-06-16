/*
 * Created on May 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.dao.derby;

import java.beans.IntrospectionException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import java.util.StringTokenizer;

import jo.util.beans.Bean;
import jo.util.dao.IDBUtil;
import jo.util.dao.IOBeanHandler2;
import jo.util.dao.data.DAOProperty;
import jo.util.dao.logic.DAOEvent;
import jo.util.dao.logic.DAOLogic;
import jo.util.dao.logic.SchemaLogic;
import jo.util.dao.sql.SQLStatement;
import jo.util.utils.DebugUtils;


/**
 * @author jgrant
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class DerbyBeanHandler2<TheBean extends Bean> implements IOBeanHandler2<TheBean>
{
    private static final String  ESCAPE = "\ufeff";
    public static final String  NOT_EQUAL = ESCAPE+"<>"+ESCAPE;
    public static final String  GREATERTHAN = ESCAPE+">"+ESCAPE;
    public static final String  LESSTHAN = ESCAPE+"<"+ESCAPE;
    public static final String  GREATERTHANOREQUAL = ESCAPE+">="+ESCAPE;
    public static final String  LESSTHANOREQUAL = ESCAPE+"<="+ESCAPE;
    public static final String  REVERSE = ESCAPE+"DESC"+ESCAPE;
    
	private DerbyConnectionHandler	mSQLConnectionHandler;
	private Class<?>				mBeanClass;
	private String					mBeanName;
    private String                  mTableName;
    private Map<String,DAOProperty> mDescriptors;
	private String					mSortBy;
    protected boolean               mDebug = false;
    private Set<String>             mNumeric;
    private Set<String>             mPrimaryKeys = new HashSet<String>();
    private Set<String>             mSkip = new HashSet<String>();
    private boolean                 mReadOnly = false;
    protected IDBUtil               mUtils;
    
    public DerbyBeanHandler2()
    {
    }
    
    public DerbyBeanHandler2(DerbyConnectionHandler sqlHandler, Class<?> beanClass, String primaryKey) throws IntrospectionException, SQLException
    {
        init(sqlHandler, beanClass, primaryKey, "");
    }
	
	public DerbyBeanHandler2(DerbyConnectionHandler sqlHandler, Class<?> beanClass) throws IntrospectionException, SQLException
	{
	    init(sqlHandler, beanClass, "oid", "");
	}
	
	protected void init(DerbyConnectionHandler sqlHandler, Class<?> beanClass, String primaryKey, String skip) throws IntrospectionException, SQLException
	{
	    mUtils = DerbyUtil.getInstance();
		mSQLConnectionHandler = sqlHandler;
		mBeanClass = beanClass;
		for (StringTokenizer st = new StringTokenizer(primaryKey, ","); st.hasMoreTokens(); )
		    mPrimaryKeys.add(st.nextToken());
        for (StringTokenizer st = new StringTokenizer(skip, ","); st.hasMoreTokens(); )
            mSkip.add(st.nextToken().toUpperCase());
		mBeanName = mUtils.makeBeanName(mBeanClass);
        String tableName = mBeanName;
        if (tableName.endsWith("Bean"))
            tableName = tableName.substring(0, tableName.length() - 4);
        else if (tableName.endsWith("Data"))
            tableName = tableName.substring(0, tableName.length() - 4);
        else if (tableName.endsWith("Model"))
            tableName = tableName.substring(0, tableName.length() - 5);
        mTableName = mUtils.makeSQLName(tableName);
        mNumeric = new HashSet<String>();
		initDescriptors();
		initTable();
		mSortBy = null;
		mDebug = DebugUtils.debug;
	}
	
	private void initDescriptors() throws IntrospectionException
	{
        mDescriptors = new HashMap<String, DAOProperty>();
        List<DAOProperty> props = SchemaLogic.getSchema(mUtils, mBeanClass);
        DebugUtils.trace("Descriptors:");
        for (DAOProperty prop : props)
        {
            mDescriptors.put(prop.getName(), prop);
            DebugUtils.trace("  "+prop.getName());
            if (DerbyUtil.isNumeric(prop.getColumnType()))
                mNumeric.add(prop.getName());
        }
	}

	private void initTable() throws SQLException
	{
		String tableName = mTableName;
		List<String> names = mSQLConnectionHandler.getTableNames();
		boolean exists = false;
		for (String name : names)
		{
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
        if (mReadOnly)
            return;
		DebugUtils.debug("Table " + mTableName + " exists, ensuring columns.");
		Set<String> missing = new HashSet<String>();
		Map<String,DAOProperty> descriptorsMap = new HashMap<String, DAOProperty>();
		for (String name : mDescriptors.keySet())
		{
		    String sqlName = mUtils.makeSQLName(name);
		    missing.add(sqlName);
		    descriptorsMap.put(sqlName, mDescriptors.get(name));
		}
		for (String name : mSQLConnectionHandler.getColumns(mTableName))
		{
			DebugUtils.debug("Present: "+name);
			missing.remove(name.toUpperCase());
		}
		for (String name : missing)
		{
            DebugUtils.debug("Missing: "+name);
            String sqlType = mDescriptors.get(name).getColumnType();
			StringBuffer cmd = new StringBuffer("ALTER TABLE ");
			cmd.append(mTableName);
			cmd.append(" ADD COLUMN ");
			cmd.append(mUtils.makeSQLName(name));
			cmd.append(" ");
			cmd.append(sqlType);
            DebugUtils.debug("  creating "+cmd);
			mSQLConnectionHandler.executeUpdate(cmd);
		}
	}

	private void makeTable() throws SQLException
	{
        if (mReadOnly)
            return;
		DebugUtils.debug("Table " + mTableName + " does not exist, creating.");
		StringBuffer cmd = new StringBuffer("CREATE TABLE ");
		cmd.append(mTableName);
		cmd.append(" (");
		boolean anyAdded = false;
		boolean oidAdded = false;
		for (String name : mDescriptors.keySet())
		{
		    String sqlType = mDescriptors.get(name).getColumnType();
			if (sqlType == null)
				continue;
			if (anyAdded)
				cmd.append(",\n");
			else
				anyAdded = true;
			cmd.append(mUtils.makeSQLName(name));
			cmd.append(" ");
			cmd.append(sqlType);
			if (mPrimaryKeys.contains(name))
			{
				oidAdded = true;
				cmd.append(" NOT NULL");
			}
		}
		if (oidAdded)
		{
			cmd.append(", PRIMARY KEY (");
			boolean first = true;
			for (String key : mPrimaryKeys)
			{
			    if (!first)
		            cmd.append(",");
	            cmd.append(key);
	            first = false;
			}
            cmd.append(")");
		}
		cmd.append(")");
		mSQLConnectionHandler.executeUpdate(cmd.toString());
	}

    private void getColsAndVals(SQLStatement cmd, TheBean b)
    {
        cmd.append("(");
        boolean first = true;
        for (String key : mDescriptors.keySet())
        {
            if (first)
                first = false;
            else
                cmd.append(", ");
            cmd.append(key);
        }
        cmd.append(") VALUES (");
        first = true;
        for (String key : mDescriptors.keySet())
        {
            DAOProperty desc = mDescriptors.get(key);
            if (first)
                first = false;
            else
                cmd.append(", ");
            cmd.append(b, desc);
        }
        cmd.append(")");
    }

    private void getCols(SQLStatement cmd)
    {
        boolean first = true;
        for (String key : mDescriptors.keySet())
        {
            if (first)
                first = false;
            else
                cmd.append(", ");
            cmd.append(key);
        }
    }
    
    private void getVals(SQLStatement cmd, TheBean bean)
    {
        boolean first = true;
        for (String key : mDescriptors.keySet())
        {
            DAOProperty desc = mDescriptors.get(key);
            if (first)
                first = false;
            else
                cmd.append(", ");
            cmd.append(bean, desc);
        }
    }

    protected TheBean readBean(ResultSet result) throws SQLException
    {
        TheBean ret = newInstance();
        for (String name : mDescriptors.keySet()) 
        {
            DAOProperty desc = mDescriptors.get(name);
            try
            {
                desc.setObjectValue(result, ret);
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
    public List<TheBean> find(String[] cols, String[] vals, boolean single, boolean isOr, String sortBy) throws SQLException
    {
        return find(cols, vals, single, isOr, sortBy, false);
    }

    /* (non-Javadoc)
     * @see house.wish.io.IOBeanHandler#findByOID(long)
     */
    public List<TheBean> find(String[] cols, String[] vals, boolean single, boolean isOr, String sortBy, boolean fuzzy) throws SQLException
    {
        return find(cols, vals, isOr, sortBy, fuzzy, 0, single ? 1 : 0);
    }

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#findByOID(long)
	 */
	public List<TheBean> find(String[] cols, String[] vals, boolean isOr, String sortBy, boolean fuzzy, int offset, int limit) throws SQLException
	{
		StringBuffer cmd = new StringBuffer();
		cmd.append("SELECT * FROM ");
        cmd.append(mTableName);
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
                    cmd.append(mUtils.makeSQLName(cols[i]));
                    cmd.append(" LIKE '%");
                    String val = vals[i];
                    if (!val.startsWith("'"))
                        val = mUtils.quote(val);
                    // strip quotes
                    val = val.substring(1, val.length() - 1);
                    cmd.append(val);
                    cmd.append("%' ");
                    
                }
                else
                {
                    String name = mUtils.makeSQLName(cols[i]);
    				cmd.append(name);
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
    				else if (mNumeric.contains(name))
                        cmd.append(val);
    				else
    				    cmd.append(mUtils.quote(val));
                }
			}
		}
		if (sortBy != null)
		{
		    cmd.append(" ORDER BY ");
            if (sortBy.startsWith(REVERSE))
            {
                cmd.append(mUtils.makeSQLName(sortBy.substring(REVERSE.length())));
                cmd.append(" DESC");
            }
            else
                cmd.append(mUtils.makeSQLName(sortBy));
		}
//		if (cmd.toString().equals("SELECT * FROM log WHERE oid = '1106350613961' LIMIT 1;"))
//		{
//		    Throwable t = new Throwable();
//		    t.printStackTrace();
//		}
		return query(cmd.toString(), offset, limit);
	}
	
    public List<TheBean> query(String cmd, int offset, int limit) throws SQLException
    {

		List<TheBean> ret = new ArrayList<TheBean>();
		for (int i = 3; i >= 0; i--)
		{
		    try
		    {
				ResultSet result = mSQLConnectionHandler.executeQuery(cmd);
				int row = 1;
				while (result.next())
				{
	                if ((offset > 0) && (row < offset))
	                    continue;
					ret.add(readBean(result));
                    if ((limit > 0) && (ret.size() >= limit))
                        break;
					row++;
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
	public List<TheBean> find(String[] cols, String[] vals, boolean single, String sortBy) throws SQLException
	{
		return find(cols, vals, single, false, sortBy);
	}
	
	public TheBean find(String colName, long colVal)
	{
		String[] colNames = new String[1];
		colNames[0] = colName;
		String[] colVals = new String[1];
		colVals[0] = String.valueOf(colVal);
		List<TheBean> ret;
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
		return ret.get(0);
	}
	
	public TheBean find(String colName, String colVal)
	{
		String[] colNames = new String[1];
		colNames[0] = colName;
		String[] colVals = new String[1];
		colVals[0] = mUtils.quote(colVal);
		List<TheBean> ret;
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
		return ret.get(0);
	}

	/* (non-Javadoc)
	 * @see house.list.io.IOMailingListAddressHandler#findByMailingList(long)
	 */
	public TheBean find(String colName1, String colVal1, String colName2, String colVal2)
	{
		String[] colNames = new String[2];
		colNames[0] = colName1;
		colNames[1] = colName2;
		String[] colVals = new String[2];
		colVals[0] = colVal1;
		colVals[1] = colVal2;
		List<TheBean> ret;
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
		return ret.get(0);
	}
	
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
		catch (SQLException e)
		{
			DebugUtils.error("Can't find multiple for "+colName+"="+colVal+" in "+mBeanClass.getName(), e);
			return new ArrayList<TheBean>();
		}
	}
	
	public List<TheBean> findMultiple(String colName, String colVal, String sortBy)
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
			return new ArrayList<TheBean>();
		}
	}
	
	public List<TheBean> findMultiple(String colName1, String colVal1, String colName2, String colVal2, String sortBy)
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
			return new ArrayList<TheBean>();
		}
	}
	
	public String getTableName()
	{
	    return mTableName;
	}

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#newInstance()
	 */
	public TheBean newInstance()
	{
		try
		{
		    @SuppressWarnings("unchecked")
            TheBean ret = (TheBean)mBeanClass.newInstance();
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
    public void doUpdate(TheBean b)
    {
        if (mReadOnly)
            return;
        synchronized (mSQLConnectionHandler)
        {
            try
            {
                mSQLConnectionHandler.getConnection().setAutoCommit(false);
                DebugUtils.trace(mTableName+": doDelete on "+b.getOID());
                doDelete(b);
                SQLStatement cmd = new SQLStatement();
                cmd.append("INSERT INTO ");
                cmd.append(mTableName);
                cmd.append(" ");
                getColsAndVals(cmd, b);
                mSQLConnectionHandler.executeUpdate(cmd);
                mSQLConnectionHandler.getConnection().commit();
            }
            catch (Exception e)
            {
                try
                {
                    mSQLConnectionHandler.getConnection().rollback();
                }
                catch (SQLException e2)
                {
                    throw new IllegalStateException("Error rolling back", new IllegalStateException("Error updating", e));
                }
                throw new IllegalStateException("Can't update", e);
            }
            finally
            {
                try
                {
                    mSQLConnectionHandler.getConnection().setAutoCommit(true);
                }
                catch (SQLException e2)
                {
                    throw new IllegalStateException("Error resetting auto commit", e2);
                }
            }
        }
    }

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#update(house.wish.beans.Bean)
	 */
	public void update(TheBean b)
	{
        doUpdate(b);
        DAOLogic.fireEvent(DAOEvent.UPDATE, mBeanName, b);
	}

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#update(house.wish.beans.Bean)
	 */
	public void update(List<TheBean> beans)
	{
        if (mReadOnly)
            return;
	    if (beans.size() == 0)
	        return;
	    delete(beans);
	    TheBean b = beans.get(0);
	    SQLStatement cmd = new SQLStatement();
		cmd.append("INSERT INTO ");
		cmd.append(mTableName);
		cmd.append(" (");
		getCols(cmd);
		cmd.append(") VALUES ");
		boolean first = true;
		for (Iterator<TheBean> i = beans.iterator(); i.hasNext(); )
		{
		    if (first)
		        first = false;
		    else
		        cmd.append(", ");
		    b = i.next();
		    cmd.append("(");
		    getVals(cmd, b);
		    cmd.append(")");
		}
		try
		{
			mSQLConnectionHandler.executeUpdate(cmd);
		}
		catch (Exception e)
		{
			DebugUtils.error("Can't execute "+cmd.toString(), e);
		}
        DAOLogic.fireEvent(DAOEvent.UPDATE, mBeanName, b);
	}

    /* (non-Javadoc)
     * @see house.wish.io.IOBeanHandler#delete(house.wish.beans.Bean)
     */
    public void doDelete(TheBean b)
    {
        if (mReadOnly)
            return;
        StringBuffer cmd = new StringBuffer();
        cmd.append("DELETE FROM ");
        cmd.append(mTableName);
        cmd.append(" WHERE OID=");
        cmd.append(b.getOID());
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
	public void delete(TheBean b)
	{
        doDelete(b);
        DAOLogic.fireEvent(DAOEvent.DELETION, mBeanName, b);
	}

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#delete(house.wish.beans.Bean)
	 */
	public void delete(String field, String value)
	{
        if (mReadOnly)
            return;
		StringBuffer cmd = new StringBuffer();
		cmd.append("DELETE FROM ");
		cmd.append(mTableName);
		cmd.append(" WHERE ");
		cmd.append(mUtils.makeSQLName(field));
		cmd.append("=");
		cmd.append(mUtils.quote(value));
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
        if (mReadOnly)
            return;
		StringBuffer cmd = new StringBuffer();
		cmd.append("DELETE FROM ");
		cmd.append(mTableName);
		cmd.append(" WHERE ");
		cmd.append(mUtils.makeSQLName(field1));
		cmd.append("=");
		cmd.append(mUtils.quote(value1));
		cmd.append(" AND ");
		cmd.append(mUtils.makeSQLName(field2));
		cmd.append("=");
		cmd.append(mUtils.quote(value2));
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
	public List<TheBean> findAll()
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

    public int countAll()
    {
        StringBuffer cmd = new StringBuffer();
        cmd.append("SELECT COUNT(*)");
        cmd.append(" FROM ");
        cmd.append(mTableName);
        DebugUtils.trace(cmd.toString());
        int ret = 0;
        for (int i = 3; i >= 0; i--)
        {
            try
            {
                ResultSet result = getSQLConnectionHandler().executeQuery(cmd.toString());
                if (result.next())
                {
                    ret = result.getInt(1);
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }
        }
        return ret;     
    }

	public boolean isValidOID(long oid)
	{
		return findByOID(oid) != null;
	}

	/* (non-Javadoc)
	 * @see house.wish.io.IOBeanHandler#findByOID(long)
	 */
	public TheBean findByOID(long oid)
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
    public DerbyConnectionHandler getSQLConnectionHandler()
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

    public void update(Collection<TheBean> beans)
    {
        for (TheBean b : beans)
            doUpdate(b);
        DAOLogic.fireEvent(DAOEvent.UPDATE, mBeanName, null);
    }

    public void delete(Collection<TheBean> beans)
    {
        if (beans.size() == 0)
            return;
        StringBuffer cmd = new StringBuffer();
        cmd.append("DELETE FROM ");
        cmd.append(mTableName);
        cmd.append(" WHERE");
        boolean first = true;
        for (TheBean bean : beans)
        {
            if (!first)
                cmd.append(" OR");
            else
                first = false;
            cmd.append(" OID=");
            cmd.append(bean.getOID());
        }
        try
        {
            mSQLConnectionHandler.executeUpdate(cmd.toString());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        DAOLogic.fireEvent(DAOEvent.DELETION, mBeanName, null);
    }
    
    public Collection<String> findColumn(String colName)
    {
		StringBuffer cmd = new StringBuffer();
		cmd.append("SELECT DISTINCT ");
		cmd.append(mUtils.makeSQLName(colName));
		cmd.append(" FROM ");
		cmd.append(mTableName);
		//DebugUtils.debug(cmd.toString());
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

    public Map<String,DAOProperty> getDescriptors()
    {
        return mDescriptors;
    }
    
    @Override
    public void deleteAll()
    {
        if (mReadOnly)
            return;
        StringBuffer cmd = new StringBuffer();
        cmd.append("DROP TABLE ");
        cmd.append(mTableName);
        try
        {
            getSQLConnectionHandler().clearCache();
            getSQLConnectionHandler().executeUpdate(cmd.toString());
            makeTable();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void dump(File f) throws IOException
    {
        BufferedWriter wtr = new BufferedWriter(new FileWriter(f));
        try
        {
            wtr.write("TABLE '"+mTableName+"'");
            wtr.newLine();
            String query = "select columnname, referenceid from sys.syscolumns";
            ResultSet results = getSQLConnectionHandler().executeQuery(query);
            int cols = results.getMetaData().getColumnCount();
            for (int i = 1; i <= cols; i++)
            {
                if (i > 1)
                    wtr.write(",");
                wtr.write(results.getMetaData().getColumnName(i));
            }
            wtr.newLine();
            while (results.next())
            {
                String tableid = results.getString(2);
                if (tableid.equalsIgnoreCase(mTableName))
                {
                    for (int i = 1; i <= cols; i++)
                    {
                        if (i > 1)
                            wtr.write(",");
                        wtr.write(results.getString(i));
                    }
                    wtr.newLine();                    
                }
            }
            wtr.newLine();
            wtr.write("CONTENTS");
            wtr.newLine();
            query = "select * from "+mTableName;
            results = getSQLConnectionHandler().executeQuery(query);
            cols = results.getMetaData().getColumnCount();
            for (int i = 1; i <= cols; i++)
            {
                if (i > 1)
                    wtr.write(",");
                wtr.write(results.getMetaData().getColumnName(i));
            }
            wtr.newLine();
            int count = 0;
            while (results.next())
            {
                for (int i = 1; i <= cols; i++)
                {
                    if (i > 1)
                        wtr.write(",");
                    wtr.write(results.getString(i));
                }
                wtr.newLine();                    
                if (++count > 100)
                    break;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        wtr.close();
    }

    public boolean isReadOnly()
    {
        return mReadOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        mReadOnly = readOnly;
    }
}
