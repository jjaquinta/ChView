package jo.d4w.web.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jo.util.html.URIBuilder;
import jo.util.utils.obj.StringUtils;

public class ApplicationLogic
{
    private static Properties  mStore;
    private static boolean      mStoreDirty;
    private static boolean      mInitialized = false;
    private static File         mStoreFile;
    
    public static void init()
    {
        if (!mInitialized)
        {
            mInitialized = true;
            //System.out.println("STORE.INIT");
            mStore = new Properties();
            loadStore();
        }
    }
    
    private static File getStoreFile()
    {
        if (mStoreFile != null)
            return mStoreFile;
        String loc = System.getProperty("d4w.store.location");
        if (!StringUtils.isTrivial(loc))
            mStoreFile = new File(loc);
        else
        {
            String user = System.getProperty("user.name");
            if ("root".equals(user))
                mStoreFile = new File(System.getProperty("user.home")+"/d4w_store.properties");
            else
                mStoreFile = new File("c:\\temp\\data\\d4w_store.properties");
        }
        return mStoreFile;
    }
    
    public static void setStoreFile(File f)
    {
        mStoreFile = f;
        mInitialized = false;
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
        //System.out.println("STORE.SAVE");
    }
            
    public static String getFromStore(String key)
    {
        init();
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
        //System.out.println("STORE.GET "+key+"="+sval);
        return val;
    }
    
    public static void putInStore(String key, String val)
    {
        init();
        if (StringUtils.isTrivial(val))
            mStore.remove(key);
        else
            mStore.put(key, val);
        //System.out.println("STORE.PUT "+key+"="+val);
        mStoreDirty = true;
        Thread t = new Thread("save") { public void run() { saveStore(); } };
        t.start();
    }

    public static List<String> getListFromStore(String scheme)
    {
        init();
        if (!scheme.endsWith("://"))
            scheme += "://";
        List<String> uris = new ArrayList<String>();
        for (Object uri : mStore.keySet())
            if (((String)uri).startsWith(scheme))
                uris.add((String)uri);
        return uris;
    }

    public static Object getStore(URIBuilder u)
    {
        String key = u.getAuthority()+u.getPath();
        String data = u.getQuery("data");
        if (!StringUtils.isTrivial(data))
            putInStore(key, data);
        return getFromStore(key);
    }
}
