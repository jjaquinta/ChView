package jo.d2k.data.io.sql;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.util.Map;

import jo.d2k.data.logic.ApplicationLogic;
import jo.d2k.data.logic.IDataSource;
import jo.util.dao.sql.SQLBeanHandler2;
import jo.util.dao.sql.SQLConnectionHandler;
import jo.util.utils.DebugUtils;

public class SQLDataSource implements IDataSource
{
    public static final String DEFAULT_DRIVER = "org.gjt.mm.mysql.Driver";
//    public static final String DEFAULT_URL =
//        //"jdbc:mysql://www.ocean-of-storms.com/ocean50_d2k?user=ocean50_d2kadmin&password=d2kadmin";
//        "jdbc:mysql://www.ocean-of-storms.com/ocean50_d2k?user=ocean50_d2kchuck&password=1oBBTGd6Z5uq";

    public static final String SQL_DRIVER = "jo.sym3d.io.sql.driver";

    private String mName;
    private String mURL;
    private boolean mReadOnly;
    private SQLConnectionHandler mConnection = null;
    private Map<String, Object> mApp;

    public SQLDataSource(String name, String url, boolean readOnly)
    {
        mName = name;
        mURL = url;
        mReadOnly = readOnly;
    }

    @Override
    public void initApp(Map<String, Object> app)
    {
        mApp = app;
        setupHandlers();
        try
        {
            String mDriver = System.getProperty("sqldriver", DEFAULT_DRIVER);
            DebugUtils.trace("Loading SQL Connection Handler");
            DebugUtils.trace("  driver="+mDriver);
            DebugUtils.trace("  url="+mURL);
            mConnection = new SQLConnectionHandler(mDriver, mURL);
            mApp.put(SQL_DRIVER, mConnection);
            DebugUtils.trace("Loaded MYSQL Connection Handler");
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
        mApp.put(ApplicationLogic.STAR_HANDLER, SQLStarHandler.class.getName());
        mApp.put(ApplicationLogic.STAR_ROUTE_HANDLER, SQLStarRouteHandler.class.getName());
        mApp.put(ApplicationLogic.METADATA_HANDLER, SQLMetadataHandler.class.getName());
        mApp.put(ApplicationLogic.DELETION_HANDLER, SQLDeletionHandler.class.getName());
    }
    
    public void close()
    {
        if (mConnection != null)
            mConnection.close();
    }

    @Override
    public Object getHandler(Map<String, Object> app, String handlerRef) throws Exception
    {
        Class<?> handlerClass = Class.forName((String)handlerRef);
        if (SQLBeanHandler2.class.isAssignableFrom(handlerClass))
        {
            Object[] args = new Object[1];
            args[0] = mConnection;
            Class<?>[] profile = new Class[1];
            profile[0] = args[0].getClass();
            Constructor<?> constructor = handlerClass.getConstructor(profile);
            SQLBeanHandler2<?> handler = (SQLBeanHandler2<?>)constructor.newInstance(args);
            if (mReadOnly)
                handler.setReadOnly(true);
            return handler;
        }
        else
            return handlerClass.newInstance();
    }

    @Override
    public String getName()
    {
        return mName;
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
