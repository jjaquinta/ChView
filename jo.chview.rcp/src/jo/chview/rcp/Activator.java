package jo.chview.rcp;

import java.util.List;
import java.util.Map;

import jo.d2k.data.logic.DataLogic;
import jo.d2k.data.logic.RuntimeLogic;
import jo.util.logic.ThreadLogic;
import jo.util.logic.UIThreadHandler;
import jo.util.ui.utils.ColorUtils;
import jo.util.ui.utils.FontUtils;
import jo.util.utils.DebugUtils;
import jo.util.utils.ExtensionPointUtils;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import chuck.terran.admin.logic.EclipseLogger;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "jo.chview.rcp"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
        DebugUtils.info("Starting up "+context.getBundle().getSymbolicName()+", version "+context.getBundle().getVersion());
        DebugUtils.mLoggers.clear();
        DebugUtils.mLoggers.add(new EclipseLogger());
        ThreadLogic.setUIThreadHandler(new UIThreadHandler());
        loadDataSources();
        ColorUtils.init();
        FontUtils.init();
	}
    
    private static final String[] DATASOURCE_TEXT = { "name", "type", "uri", "readOnly", "default" };
    
    private void loadDataSources()
    {
        DataLogic.setDBRoot(Platform.getLocation().toFile());
        List<Map<String,Object>> map = ExtensionPointUtils.getExecutableExtensionInfo("jo.chview.rcp.datasource", DATASOURCE_TEXT, null);
        for (int i = 0; i < map.size(); i++)
        {
            Map<String,Object> dsMap = map.get(i);
            String name = (String)dsMap.get(DATASOURCE_TEXT[0]);
            String type = (String)dsMap.get(DATASOURCE_TEXT[1]);
            String uri = (String)dsMap.get(DATASOURCE_TEXT[2]);
            String readOnly = (String)dsMap.get(DATASOURCE_TEXT[3]);
            String def = (String)dsMap.get(DATASOURCE_TEXT[4]);
            DataLogic.addDataSource(name, type, uri, readOnly, def);
        }
    }

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	    RuntimeLogic.getInstance().getDataSource().close();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
