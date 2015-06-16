package chuck.terran.admin.logic;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jo.util.utils.io.URLUtils;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class UpdateLogic
{
    private static final String BASE_DIR = "http://www.ocean-of-storms.com/chview/dist/";
    private static final String MANIFEST = BASE_DIR+"manifest.txt";
    
    public static List<URL> checkForUpdates()
    {
        List<URL> updates = new ArrayList<URL>();
        try
        {
            String manifest = URLUtils.readURLAsString(MANIFEST);
            if (manifest != null)
            {
                for (StringTokenizer st = new StringTokenizer(manifest, "\r\n"); st.hasMoreTokens(); )
                {
                    String updateName = st.nextToken();
                    int o = updateName.indexOf('_');
                    if (o < 0)
                        continue;
                    String id = updateName.substring(0, o);
                    String newVersion = updateName.substring(o + 1);
                    if (newVersion.endsWith(".jar"))
                        newVersion = newVersion.substring(0, newVersion.length() - 4);
                    Bundle ext = Platform.getBundle(id);
                    if (ext == null)
                        break;
                    String oldVersion = ext.getVersion().toString();
                    int cmp = newVersion.compareTo(oldVersion);
                    if (cmp > 0)
                        updates.add(new URL(BASE_DIR+updateName));
                    //DebugUtils.trace(id+": new="+newVersion+", old="+oldVersion+", cmp="+cmp);                    
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return updates;
    }
    
    public static boolean performUpdates(List<URL> updates)
    {
        try
        {
            File installDir = new File(Platform.getInstallLocation().getURL().toURI());
            File pluginDir = new File(installDir, "plugins");
            boolean success = true;
            for (URL update : updates)
                success &= performUpdate(update, pluginDir);
            return success;
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    private static boolean performUpdate(URL update, File pluginDir)
    {
        String name = update.getPath();
        int o = name.lastIndexOf('/');
        if (o >= 0)
            name = name.substring(o + 1);
        File localFile = new File(pluginDir, name);
        try
        {
            URLUtils.copy(update.toString(), localFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
