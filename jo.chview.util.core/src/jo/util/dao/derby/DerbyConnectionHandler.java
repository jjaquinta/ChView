/*
 * Created on Jul 14, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.dao.derby;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jo.util.beans.PCSBean;
import jo.util.dao.sql.SQLStatement;
import jo.util.utils.DebugUtils;


/**
 * @author jgrant
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DerbyConnectionHandler extends PCSBean
{
    private static final int TRIES = 3;
    private static final int TIMEOUT = 15*1000;
    public static final String DEFAULT_DRIVER = 
            "org.apache.derby.jdbc.EmbeddedDriver"; 
            //"org.gjt.mm.mysql.Driver";
        public static final String DEFAULT_URL =
            //"jdbc:mysql://192.168.1.141/namenerds?user=namenerds";
            //"jdbc:mysql://111george.com/namenerds?user=namenerds";
            //"jdbc:mysql://alita/namenerds?user=namenerds&password=havarti";
            "jdbc:derby:trav_ar;create=true;user=trav_ar;password=redrobin";

	private long		mUniqueId;
	private String		mDriver;
	private String		mURL;
    private String      mSchema;
    private int         mBusyCount;
    
	public String getSchema()
    {
        return mSchema;
    }

    private Connection	mConnection;
    private Map<String,PreparedStatement>       mPreparedStatementMap;
	private Map<Thread,Statement>  mStatementMap;
	private Map<Thread,ResultSet>  mResultSetMap;
	
	/**
	 * z
	 */
	public DerbyConnectionHandler(String sqlDriver, String sqlUrl, String schema) throws ClassNotFoundException, SQLException
	{
		super();
		mBusyCount = 0;
		mUniqueId = System.currentTimeMillis();
		mDriver = sqlDriver;
		if ((mDriver == null) || (mDriver.length() == 0))
		    mDriver = DEFAULT_DRIVER;
		mURL = sqlUrl;
		if ((mURL == null) || (mURL.length() == 0))
		    mURL = DEFAULT_URL;
		mSchema = schema;
		DebugUtils.trace("SQLLoading "+mDriver+".");
		if (!mDriver.equals("-"))
		{
		    Class.forName(mDriver);
    		DebugUtils.info("SQLLoaded.");
    		DebugUtils.trace("SQLConnecting "+mURL+".");
    		mConnection = DriverManager.getConnection(mURL);
    		DebugUtils.info("SQLConnected.");
		}
        mPreparedStatementMap = new HashMap<String, PreparedStatement>();
		mStatementMap = new HashMap<Thread,Statement>();
		mResultSetMap = new HashMap<Thread,ResultSet>();
		ensureSchema();
	}
	
	private void ensureSchema()
	{
        List<String> schemas = getSchemas();
        for (String schema : schemas)
        {
            if (mSchema.equalsIgnoreCase(schema))
                return;
        }
	    try
        {
            executeUpdate("CREATE SCHEMA "+mSchema);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
	}

	/* (non-Javadoc)
	 * @see house.george.io.disk.DiskHandler#getUniqueID()
	 */
	public synchronized long getUniqueID()
	{
		mUniqueId++;
		return mUniqueId;
	}
	
	public Connection getConnection() throws SQLException
	{
        if ((mConnection == null) || mConnection.isClosed())
        {
            mConnection = DriverManager.getConnection(mURL);
    		DebugUtils.info("SQLConnected.");
        }
		return mConnection;
	}

	public void setConnection(Connection connection)
	{
	    mConnection = connection;
	}
	
    public void close()
    {
    	clearCache();
        try
        {
            mConnection.close();
        }
        catch (SQLException e)
        {
        }
    }

    public void clearCache()
    {
        synchronized (mResultSetMap)
    	{
	        for (ResultSet rs : mResultSetMap.values())
	        {
	            try
	            {
	                rs.close();
	            }
	            catch (SQLException e)
	            {
	            }
	        }
	        mResultSetMap.clear();
    	}
    	synchronized (mStatementMap)
    	{
	        for (Statement statement : mStatementMap.values())
	        {
	            try
	            {
	                if (statement != null)
	                    statement.close();
	            }
	            catch (SQLException e)
	            {
	            }
	        }
	        mStatementMap.clear();
    	}
        synchronized (mPreparedStatementMap)
        {
            for (PreparedStatement statement : mPreparedStatementMap.values())
                try
                {
                    statement.close();
                }
                catch (SQLException e)
                {
                }
            mPreparedStatementMap.clear();
            mPreparedStatementMap.clear();
        }
    }
	
	private Statement getStatement() throws SQLException
	{
		synchronized (mResultSetMap)
		{
		    ResultSet oldResults = mResultSetMap.get(Thread.currentThread());
		    if (oldResults != null)
		        oldResults.close();
		}
	    synchronized (mStatementMap)
	    {
		    Statement oldStatement = mStatementMap.get(Thread.currentThread());
		    if (oldStatement != null)
		        oldStatement.close();
			Statement newStatement = getConnection().createStatement();
			mStatementMap.put(Thread.currentThread(), newStatement);
			return newStatement;
	    }
	}
    
    private PreparedStatement getPreparedStatement(String cmd) throws SQLException
    {
        synchronized (mResultSetMap)
        {
            ResultSet oldResults = (ResultSet)mResultSetMap.get(Thread.currentThread());
            if (oldResults != null)
                oldResults.close();
        }
        synchronized (mStatementMap)
        {
            Statement oldStatement = (Statement)mStatementMap.get(Thread.currentThread());
            if (oldStatement != null)
                oldStatement.close();
            PreparedStatement newStatement /*= mPreparedStatementMap.get(cmd)*/;
            //if (newStatement == null)
            {
                newStatement = getConnection().prepareStatement(cmd);
                mPreparedStatementMap.put(cmd, newStatement);
            }
            mStatementMap.put(Thread.currentThread(), null);
            //newStatement.clearParameters();
            return newStatement;
        }
    }

	 public int executeUpdate(SQLStatement cmd) throws Exception
    {
	    DebugUtils.trace("SQL "+cmd.getStatement());
        incrementBusy();
        try
        {
            int ret = -1;
            for (int i = 0; i < TRIES; i++)
            {
                try
                {
                    DebugUtils.trace(cmd.toString());
                    PreparedStatement statement = getPreparedStatement(cmd.getStatement());
                    cmd.prepare(statement);
                    ret = statement.executeUpdate();
                    break;
                }
                catch (SQLException e)
                {
                    DebugUtils.error("Error executing SQL update \""+cmd+"\"", e);
                    if (e.getMessage().indexOf("Duplicate entry") >= 0)
                    {
                        DebugUtils.info("Aborting...");
                        throw e;
                    }
                    if (i == TRIES - 1)
                        throw e;
                    DebugUtils.info("Trying again...");
                }
            }
            return ret;
        }
        finally
        {
            decrementBusy();
        }
    }

	    public int executeUpdate(String cmd) throws SQLException
	    {
	        DebugUtils.trace("SQL "+cmd);
            incrementBusy();
            try
            {
    	        int ret = -1;
    	        for (int i = 0; i < TRIES; i++)
    	        {
    	            try
    	            {
    	                DebugUtils.trace(cmd.toString());
    	                Statement statement = getStatement();
    	                ret = statement.executeUpdate(cmd);
    	                statement.close();
    	                break;
    	            }
    	            catch (SQLException e)
    	            {
    	                DebugUtils.error("Error executing SQL update \""+cmd+"\"", e);
    	                if (e.getMessage().indexOf("Duplicate entry") >= 0)
    	                {
    	                    DebugUtils.info("Aborting...");
    	                    throw e;
    	                }
    	                if (i == TRIES - 1)
    	                    throw e;
    	                DebugUtils.info("Trying again...");
    	            }
    	        }
    	        return ret;
            }
            finally
            {
                decrementBusy();
            }
	    }

	public int executeUpdate(StringBuffer cmd) throws SQLException
	{
		return executeUpdate(cmd.toString());
	}
	
	public ResultSet executeQuery(String cmd) throws SQLException
	{
        incrementBusy();
        try
        {
    	    ResultSet ret = null;
    	    for (int i = 0; i < TRIES; i++)
    	    {
    			//Statement select = getConnection().createStatement();
    	    	Statement select = getStatement();
    	        SQLQueryThread qt = new SQLQueryThread(select, cmd);
    	        qt.start();
                try
                {
            	    cleanup();
                    Thread.sleep(TIMEOUT);
                }
                catch (InterruptedException e1)
                {
                }
    	        if (!qt.isDone())
    	        {
    	            mConnection.close();
    	            mConnection = null;
    	            DebugUtils.error("Timeout executing SQL query \""+cmd+"\"");
    	            if (i == TRIES - 1)
    	                throw new SQLException("Timeout!");
    	            DebugUtils.info("Trying again...");
    	        }
    	        else if (qt.getError() != null)
    	        {
    	            DebugUtils.error("Error executing SQL query \""+cmd+"\"", qt.getError());
    	            if (i == TRIES - 1)
    	                throw qt.getError();
    	            DebugUtils.info("Trying again...");
    	        }
    	        else if (qt.getResult() != null)
    	        {
    	            ret = qt.getResult();
    	            break;
    	        }
    	    }
    	    synchronized (mResultSetMap)
    	    {
    	    	mResultSetMap.put(Thread.currentThread(), ret);
    	    }
    	    return ret;
        }
        finally
        {
            decrementBusy();
        }
	}
	
	private void cleanup()
	{
		synchronized (mResultSetMap)
		{
	        for (Iterator<Thread> i = mResultSetMap.keySet().iterator(); i.hasNext(); )
	        {
	        	Thread t = i.next();
	        	if (t.isAlive())
	        		continue;
	            ResultSet rs = mResultSetMap.get(t);
	            try
	            {
	            	i.remove();
	                rs.close();
	            }
	            catch (SQLException e)
	            {
	            }
	        }
		}
		synchronized (mStatementMap)
		{
	        for (Iterator<Thread> i = mStatementMap.keySet().iterator(); i.hasNext(); )
	        {
	        	Thread t = i.next();
	        	if (t.isAlive())
	        		continue;
	            Statement statement = mStatementMap.get(t);
	            try
	            {
	            	i.remove();
	                statement.close();
	            }
	            catch (SQLException e)
	            {
	            }
	        }
		}
	}
    
    public List<String> getSchemas()
    {
        List<String> schemaNames = new ArrayList<String>();
        try
        {
            DatabaseMetaData md = getConnection().getMetaData();
            ResultSet results = md.getSchemas();
            while (results.next())
                schemaNames.add(results.getString(1));
            results.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        return schemaNames;
    }
	
	public List<String> getTableNames()
	{
	    List<String> tableNames = new ArrayList<String>();
        try
        {
            DatabaseMetaData md = getConnection().getMetaData();
            ResultSet results = md.getTables(null, null, null, null);
            while(results.next())
                tableNames.add(results.getString(3));
            results.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
	    return tableNames;
	}
	
	public String getTableID(String tableName)
	{
	    //String query = "select tableid from sys.systables where tablename = '"+tableName+"'";
        String query = "select tableid,tablename from sys.systables ";
	    try
        {
            ResultSet results = executeQuery(query);
            while (results.next())
            {
                String id = results.getString(1);
                String name = results.getString(2);
                if (name.equalsIgnoreCase(tableName))
                    return id;
            }
            return null;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
	}
	
	public List<String> getColumns(String tableName)
	{
	    String id = getTableID(tableName);
	    if (id == null)
	        return null;
	    String query = "select columnname, referenceid from sys.syscolumns";
        try
        {
            ResultSet results = executeQuery(query);
            List<String> columns = new ArrayList<String>();
            while (results.next())
            {
                String name = results.getString(1);
                String tableid = results.getString(2);
                if (tableid.equalsIgnoreCase(id))
                    columns.add(name);
            }
            return columns;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
	}
	
    private void incrementBusy()
    {
        synchronized (this)
        {
            setBusyCount(getBusyCount() + 1);
        }
    }
    
    private void decrementBusy()
    {
        synchronized (this)
        {
            setBusyCount(getBusyCount() - 1);
        }
    }

    public int getBusyCount()
    {
        return mBusyCount;
    }

    public void setBusyCount(int busyCount)
    {
        queuePropertyChange("busyCount", mBusyCount, busyCount);
        mBusyCount = busyCount;
        firePropertyChange();
    }
    
	private class SQLQueryThread extends Thread
	{
	    private Statement		mStatement;
	    private String			mCommand;
	    private ResultSet		mResult;
	    private SQLException	mError;
	    private boolean			mDone;
	    private Thread			mMaster;
	    
	    public SQLQueryThread(Statement statement, String command)
	    {
	        mStatement = statement;
	        mCommand = command;
	        mDone = false;
	        mMaster = Thread.currentThread();
	        setName("SQLQueryThread");
	    }
	    
	    public void run()
	    {
            incrementBusy();
            try
            {
    	        try
                {
    				DebugUtils.trace(mCommand);
                    mResult = mStatement.executeQuery(mCommand);
                }
                catch (SQLException e)
                {
                    mError = e;
                }
    	        mDone = true;
    	        mMaster.interrupt();
            }
            finally
            {
                decrementBusy();
            }
	    }
        public boolean isDone()
        {
            return mDone;
        }
        public SQLException getError()
        {
            return mError;
        }
        public ResultSet getResult()
        {
            return mResult;
        }
	}
}
