package jo.d2k.data.io.derby;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import jo.d2k.data.logic.ApplicationLogic;
import jo.d2k.data.logic.IDataSource;
import jo.util.dao.derby.DerbyBeanHandler;
import jo.util.dao.derby.DerbyBeanHandler2;
import jo.util.dao.derby.DerbyConnectionHandler;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.PropertiesLogic;

public class DerbyDataSource implements IDataSource
{
    public static final String DEFAULT_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

    public static final String SQL_DRIVER = "chuck.io.sql.driver";
    
    private File mRootDir;
    private String mURL;
    private String mName;
    private boolean mReadOnly;
    private String mDBName;
    private DerbyConnectionHandler mConnection = null;
    private Map<String, Object> mApp;

    public DerbyDataSource(File rootDir, String dbName)
    {
        mRootDir = rootDir;
        mDBName = dbName;
        mURL = "jdbc:derby:"+mDBName+";create=true;user=terran;password=republic";
        File propsFile = new File(mRootDir, mDBName+".properties");
        try
        {
            Properties props = PropertiesLogic.readProperties(propsFile);
            mName = props.getProperty("name");
        }
        catch (IOException e)
        {
            mName = mDBName;
        }
        mReadOnly = false;
    }

    @Override
    public void initApp(Map<String, Object> app)
    {
        mApp = app;
        String workspace = mRootDir.toString();
        System.setProperty("derby.system.home", workspace);
        
        setupHandlers();
        try
        {
            String mDriver = System.getProperty("sqldriver", DEFAULT_DRIVER);
            mURL = System.getProperty("sqlurl", mURL);
            mConnection = new DerbyConnectionHandler(mDriver, mURL, mDBName);
            mApp.put(SQL_DRIVER, mConnection);
            DebugUtils.info("Loaded Derby Connection Handler");
            mConnection.addPropertyChangeListener("busyCount", new PropertyChangeListener() {                
                @Override
                public void propertyChange(PropertyChangeEvent evt)
                {
                    int delta = (Integer)evt.getNewValue() - (Integer)evt.getOldValue();
                    synchronized (jo.d2k.data.logic.RuntimeLogic.getInstance())
                    {
                        int busyCount = jo.d2k.data.logic.RuntimeLogic.getInstance().getBusyCount();
                        busyCount += delta;
                        jo.d2k.data.logic.RuntimeLogic.getInstance().setBusyCount(busyCount);
                    }
                }
            });
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public void setupHandlers()
    {
        mApp.put(ApplicationLogic.STAR_HANDLER, DerbyStarHandler.class.getName());
        mApp.put(ApplicationLogic.STAR_ROUTE_HANDLER, DerbyStarRouteHandler.class.getName());
        mApp.put(ApplicationLogic.METADATA_HANDLER, DerbyMetadataHandler.class.getName());
        mApp.put(ApplicationLogic.DELETION_HANDLER, DerbyDeletionHandler.class.getName());
    }
    
    public void close()
    {
        if (mConnection != null)
        {
            mConnection.close();
            try
            {
                DriverManager.getConnection("jdbc:derby:"+mDBName+";shutdown=true;user=terran;password=republic");
            }
            catch (SQLException e)
            {
                // exception expected;
            }
        }
    }

    @Override
    public Object getHandler(Map<String, Object> app, String handlerRef) throws Exception
    {
        Class<?> handlerClass = Class.forName((String)handlerRef);
        if (DerbyBeanHandler2.class.isAssignableFrom(handlerClass))
        {
            Object[] args = new Object[1];
            args[0] = mConnection;
            Class<?>[] profile = new Class[1];
            profile[0] = args[0].getClass();
            Constructor<?> constructor = handlerClass.getConstructor(profile);
            DerbyBeanHandler2<?> handler = (DerbyBeanHandler2<?>)constructor.newInstance(args);
            if (mReadOnly)
                handler.setReadOnly(true);
            return handler;
        }
        else if (DerbyBeanHandler.class.isAssignableFrom(handlerClass))
        {
            Object[] args = new Object[1];
            args[0] = mConnection;
            Class<?>[] profile = new Class[1];
            profile[0] = args[0].getClass();
            Constructor<?> constructor = handlerClass.getConstructor(profile);
            return constructor.newInstance(args);
        }
        else
            throw new IllegalArgumentException("Cannot load "+handlerRef);
    }

    @Override
    public String getName()
    {
        return mName;
    }

    public String getDBName()
    {
        return mDBName;
    }

    public boolean isReadOnly()
    {
        return mReadOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        mReadOnly = readOnly;
        if (mApp != null)
            setupHandlers(); // force handlers to re-load
    }
}
