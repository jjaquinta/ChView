package jo.d4w.web.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jo.util.utils.obj.StringUtils;

public class ApplicationLogic
{
    private static Properties  mStore;
    private static boolean      mStoreDirty; 
    
    public static void init()
    {
        mStore = new Properties();
        loadStore();
    }
    
    private static File getStoreFile()
    {
        String user = System.getProperty("user.name");
        if ("root".equals(user))
            return new File("/home/ocean50/d4w_store.properties");
        else
            return new File("c:\\temp\\data\\d4w_store.properties");
    }
    
    private static void loadStore()
    {
        File f = getStoreFile();
        if (f.exists())
        {
            try
            {
                FileInputStream fis = new FileInputStream(f);
                mStore.load(fis);
                fis.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        mStoreDirty = false;
    }
    
    private static void saveStore()
    {
        if (!mStoreDirty)
            return;
        File f = getStoreFile();
        try
        {
            FileOutputStream fos = new FileOutputStream(f);
            mStore.store(fos, "Persistent properties");
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        mStoreDirty = false;
    }
            
    public static String getFromStore(String key)
    {
        String val = mStore.getProperty(key);
        String sval = val;
        if (sval != null)
        {
            int o = sval.indexOf('\n');
            if (o >= 0)
                sval = sval.substring(0, o).trim();
            if (sval.length() > 256)
                sval = sval.substring(0, 256);
        }
        System.out.println("STORE.GET "+key+"="+sval);
        return val;
    }
    
    public static void putInStore(String key, String val)
    {
        if (StringUtils.isTrivial(val))
            mStore.remove(key);
        else
            mStore.put(key, val);
        System.out.println("STORE.PUT "+key+"="+val);
        mStoreDirty = true;
        Thread t = new Thread("save") { public void run() { saveStore(); } };
        t.start();
    }

    public static List<String> getListFromStore(String scheme)
    {
        if (!scheme.endsWith("://"))
            scheme += "://";
        List<String> uris = new ArrayList<String>();
        for (Object uri : mStore.keySet())
            if (((String)uri).startsWith(scheme))
                uris.add((String)uri);
        return uris;
    }
}
