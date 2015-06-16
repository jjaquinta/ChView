package jo.d2k.data.io.mem;

import java.lang.reflect.Constructor;
import java.util.Map;

import jo.d2k.data.logic.ApplicationLogic;
import jo.d2k.data.logic.IDataSource;
import jo.util.dao.mem.MemBeanHandler2;

public class MemDataSource implements IDataSource
{
    private String mName;
    private boolean mReadOnly;
    private Map<String, Object> mApp;

    public MemDataSource(String name)
    {
        mName = name;
        mReadOnly = false;
    }

    @Override
    public void initApp(Map<String, Object> app)
    {
        mApp = app;
        
        setupHandlers();
    }

    public void setupHandlers()
    {
        mApp.put(ApplicationLogic.STAR_HANDLER, MemStarHandler.class.getName());
        mApp.put(ApplicationLogic.STAR_ROUTE_HANDLER, MemStarRouteHandler.class.getName());
        mApp.put(ApplicationLogic.METADATA_HANDLER, MemMetadataHandler.class.getName());
        mApp.put(ApplicationLogic.DELETION_HANDLER, MemDeletionHandler.class.getName());
    }
    
    public void close()
    {
    }

    @Override
    public Object getHandler(Map<String, Object> app, String handlerRef) throws Exception
    {
        Class<?> handlerClass = Class.forName((String)handlerRef);
        if (MemBeanHandler2.class.isAssignableFrom(handlerClass))
        {
            Object[] args = new Object[0];
            Class<?>[] profile = new Class[0];
            Constructor<?> constructor = handlerClass.getConstructor(profile);
            MemBeanHandler2<?> handler = (MemBeanHandler2<?>)constructor.newInstance(args);
            if (mReadOnly)
                handler.setReadOnly(true);
            return handler;
        }
        else
            throw new IllegalArgumentException("Cannot load "+handlerRef);
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

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }
}
