package jo.d2k.data.logic;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import jo.util.dao.IOBeanHandler2;
import jo.util.utils.DebugUtils;

public class ApplicationLogic
{
    public static final String STAR_HANDLER = "jo.d2k.data.io.IOStarHandler";
    public static final String STAR_ROUTE_HANDLER = "jo.d2k.data.io.IOStarRouteHandler";
    public static final String METADATA_HANDLER = "jo.d2k.data.io.IOMetadataHandler";
    public static final String DELETION_HANDLER = "jo.d2k.data.io.IODeletionHandler";

    public static final String USER_HANDLER = "jo.d2k.data.io.IOUserHandler";
    public static final String SCORE_HANDLER = "jo.d2k.data.io.IOScoreHandler";
    public static final String LIFE_SCENARIO_HANDLER = "jo.d2k.data.io.IOLifeScenarioHandler";
    public static final String SHIP_HANDLER = "jo.d2k.data.io.IOShipHandler";
    public static final String MODULE_HANDLER = "jo.d2k.data.io.IOModuleHandler";
    public static final String MODULE_SPEC_HANDLER = "jo.d2k.data.io.IOModuleSpecHandler";
    public static final String STATION_HANDLER = "jo.d2k.data.io.IOStationHandler";
    public static final String SCAN_HANDLER = "jo.d2k.data.io.IOScanHandler";

    private static Map<String,Object> mApp = null;

    public static synchronized Map<String,Object> getApp()
    {
        if (null == mApp)
        {
            DebugUtils.trace("Creating new application object");
            mApp = new HashMap<String, Object>();
            RuntimeLogic.getInstance().getDataSource().initApp(mApp);
            RuntimeLogic.getInstance().addPropertyChangeListener("dataSource", new PropertyChangeListener() {                
                @Override
                public void propertyChange(PropertyChangeEvent evt)
                {
                    mApp = null;
                    StarLogic.clearCache();
                }
            });
            StarGenLogic.init();
        }
        return mApp;
    }
    
    public static synchronized Object getHandler(String handlerId)
    {
        Object ret = getApp().get(handlerId);
        if (ret instanceof IOBeanHandler2)
            return ret;
        if (ret instanceof String)
        {
            try
            {
                DebugUtils.trace("Loading Handler "+handlerId);
                Object handler = RuntimeLogic.getInstance().getDataSource().getHandler(getApp(), (String)ret);
                getApp().put(handlerId, handler);
                DebugUtils.trace("Loaded Handler "+handlerId);
                ret = handler;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return ret;
    }
}
