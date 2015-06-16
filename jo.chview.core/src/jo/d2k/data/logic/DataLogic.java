package jo.d2k.data.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import jo.d2k.data.io.derby.DerbyDataSource;
import jo.d2k.data.io.mem.MemDataSource;
import jo.d2k.data.io.sql.SQLDataSource;
import jo.d2k.data.logic.imp.CHVLogic;
import jo.d2k.data.logic.imp.LSTLogic;
import jo.util.utils.IProgMon;
import jo.util.utils.io.FileUtils;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.PropertiesLogic;

public class DataLogic
{
    private static final List<IDataSource> mDataSources = new ArrayList<IDataSource>();
//    static
//    {
//        mDataSources.add(new SQLDataSource(
//                "Terran Republic Cloud Repository",                
//                "jdbc:mysql://www.ocean-of-storms.com/ocean50_chuck?user=ocean50_d2kchuck&password=1oBBTGd6Z5uq",
//                true));
//        mDataSources.add(new SQLDataSource(
//                "Dawnfire 2000 Baseline Data",
//                "jdbc:mysql://www.ocean-of-storms.com/ocean50_d2k?user=ocean50_d2kchuck&password=1oBBTGd6Z5uq",
//                true));
//    };
    private static int mBaseline = -1;
    private static int mDefault = 0;
    private static boolean mInitialized = false;
    private static File mDBRoot = null;
    
    private static void refreshDataSources()
    {
        if (mInitialized)
            return;
        IDataSource currentSource = RuntimeLogic.getInstance().getDataSource();
        String currentName = null;
        if (currentSource instanceof DerbyDataSource)
            currentName = ((DerbyDataSource)currentSource).getDBName();
        // remove derby ones
        for (int i = mDataSources.size() - 1; i >= mBaseline; i--)
        {
            IDataSource src = mDataSources.get(i);
            if (!(src instanceof DerbyDataSource))
                continue;
            DerbyDataSource dsrc = (DerbyDataSource)src;
            if (dsrc.getDBName().equals(currentName))
                continue;
            mDataSources.remove(i);
        }
        // add them back in again
        if (mDBRoot != null)
        {
            File ws = mDBRoot;
            File[] files = ws.listFiles();
            if (files != null)
                for (File file : files)
                {
                    if (!file.getName().endsWith(".properties"))
                        continue;
                    String dbName = file.getName();
                    dbName = dbName.substring(0, dbName.length() - 11);
                    File dbDir = new File(ws, dbName);
                    if (dbDir.exists())
                    {
                        DerbyDataSource dsrc = new DerbyDataSource(mDBRoot, dbName);
                        mDataSources.add(dsrc);
                    }
                }
        }
        mInitialized = true;
    }

    public static void setDBRoot(File dbRoot)
    {
        mDBRoot = dbRoot;
    }
    
    public static void addDataSource(String name, String type,
            String uri, String readOnly, String def)
    {
        IDataSource ds;
        if ("MYSQL".equalsIgnoreCase(type))
        {
            ds = new SQLDataSource(
                    name,                
                    uri,
                    BooleanUtils.parseBoolean(readOnly));
        }
        else if ("DERBY".equalsIgnoreCase(type))
        {
            ds = new DerbyDataSource(mDBRoot, name);
            ds.setReadOnly(BooleanUtils.parseBoolean(readOnly));
        }
        else if ("MEM".equalsIgnoreCase(type))
        {
            ds = new MemDataSource(name);
            ds.setReadOnly(BooleanUtils.parseBoolean(readOnly));
        }
        else
            throw new IllegalArgumentException("Unknown data source type '"+type+"'");
        mDataSources.add(ds);
        if (BooleanUtils.parseBoolean(def))
            mDefault = mDataSources.size() - 1;
        mBaseline = mDataSources.size();
    }

    public static IDataSource[] getDataSources()
    {
        refreshDataSources();
        return mDataSources.toArray(new IDataSource[0]);
    }
    
    public static IDataSource getDefaultDataSource()
    {
        refreshDataSources();
        return mDataSources.get(mDefault);
    }
    
    public static void setDataSource(final IDataSource src)
    {
        IDataSource old = RuntimeLogic.getInstance().getDataSource();
        if (old != null)
            old.close();
        Thread t = new Thread("Change dataSource") { public void run() { 
            RuntimeLogic.incrementBusy();
            RuntimeLogic.getInstance().setDataSource(src);
            StarLogic.getByID(0); // prime database for performance
            RuntimeLogic.decrementBusy();
        }};
        t.start();
    }
    
    public static void newDataSource(String name) throws IOException
    {
        refreshDataSources();
        if (mDBRoot == null)
            throw new IllegalStateException("Local databases not supported");
        String dbName = makeDBName(mDBRoot, name);
        File dbProps = new File(mDBRoot, dbName+".properties");
        Properties props = new Properties();
        props.setProperty("name", name);
        PropertiesLogic.writeProperties(props, dbProps);
        DerbyDataSource dsrc = new DerbyDataSource(mDBRoot, dbName);
        mDataSources.add(dsrc);
        setDataSource(dsrc);
    }
    
    public static void deleteDataSource(String dbName)
    {
        refreshDataSources();
        for (IDataSource src : mDataSources)
            if (src instanceof DerbyDataSource)
            {
                DerbyDataSource dsrc = (DerbyDataSource)src;
                if (dbName.equals(dsrc.getDBName()))
                {
                    if (src == RuntimeLogic.getInstance().getDataSource())
                        setDataSource(getDefaultDataSource());
                    final File dbDir = new File(mDBRoot, dbName);
                    // deferred delete, to give time to disconnect
                    Thread t = new Thread("Deleted DB delete") { public void run() { try { Thread.sleep(5000); } catch (InterruptedException e) {}; FileUtils.rmdir(dbDir); }};
                    t.start();
                    File dbProps = new File(mDBRoot, dbName+".properties");
                    FileUtils.rmdir(dbProps);
                    mDataSources.remove(src);
                    break;
                }
            }
    }
    
    public static void lockDataSource()
    {
        refreshDataSources();
        IDataSource dsrc = RuntimeLogic.getInstance().getDataSource();
        if (dsrc.isReadOnly())
            return;
        dsrc.setReadOnly(true);
        fireDataSourceChange();
    }
    
    public static void unlockDataSource()
    {
        refreshDataSources();
        IDataSource dsrc = RuntimeLogic.getInstance().getDataSource();
        if (!dsrc.isReadOnly())
            return;
        dsrc.setReadOnly(false);
        fireDataSourceChange();
    }

    public static void fireDataSourceChange()
    {
        Thread t = new Thread("Fire dataSource change") { public void run() { 
            RuntimeLogic.incrementBusy();
            RuntimeLogic.getInstance().fireMonotonicPropertyChange("dataSource");
            RuntimeLogic.decrementBusy();
        }};
        t.start();
    }
    
    public static String exportData(File zipFile, IProgMon pm) throws IOException
    {
        refreshDataSources();
        StringBuffer report = new StringBuffer();
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
        ZipEntry starsEntry = new ZipEntry("stars.csv");
        zos.putNextEntry(starsEntry);
        int done = StarLogic.exportData(zos, pm);
        zos.closeEntry();
        report.append(done+" stars");
        ZipEntry starRoutesEntry = new ZipEntry("routes.csv");
        zos.putNextEntry(starRoutesEntry);
        done = StarRouteLogic.exportData(zos, pm);
        zos.closeEntry();
        report.append(", "+done+" routes");
        ZipEntry metadataEntry = new ZipEntry("metadata.csv");
        zos.putNextEntry(metadataEntry);
        done = MetadataLogic.exportData(zos, pm);
        zos.closeEntry();
        report.append(", "+done+" metadata");
        ZipEntry deletionsEntry = new ZipEntry("deletions.csv");
        zos.putNextEntry(deletionsEntry);
        done = DeletionLogic.exportData(zos, pm);
        zos.closeEntry();
        report.append(", "+done+" deletions");
        zos.finish();
        zos.close();
        return report.toString();
    }
    
    public static String importData(File zipFile, boolean merge, IProgMon pm) throws IOException
    {
        refreshDataSources();
        String name = zipFile.getName().toLowerCase();
        if (name.endsWith(".zip"))
            return importZip(zipFile, merge, pm);
        else if (name.endsWith(".lst"))
            return importLst(zipFile, merge, pm);
        else if (name.endsWith(".chv"))
            return importChv(zipFile, merge, pm);
        else
            return "Unsupported file type '"+zipFile.getName()+"'";
    }
    
    public static String importLst(File lstFile, boolean merge, IProgMon pm) throws IOException
    {
        refreshDataSources();
        StringBuffer report = new StringBuffer();
        InputStream is = new FileInputStream(lstFile);
        int done = LSTLogic.importLSTData(is, merge, pm);
        report.append(done+" stars");
        is.close();
        return report.toString();
    }
    
    public static String importChv(File chvFile, boolean merge, IProgMon pm) throws IOException
    {
        refreshDataSources();
        InputStream is = new FileInputStream(chvFile);
        int done = CHVLogic.importCHVData(is, merge, pm);
        is.close();
        return done+" stars";
    }
    
    public static String importZip(File zipFile, boolean merge, IProgMon pm) throws IOException
    {
        refreshDataSources();
        StringBuffer report = new StringBuffer();
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        for (;;)
        {
            ZipEntry entry = zis.getNextEntry();
            if (entry == null)
                break;
            if (entry.getName().equals("stars.csv"))
            {
                int done = StarLogic.importData(zis, merge, pm);
                if (report.length() > 0)
                    report.append(", ");
                report.append(done+" stars");
            }
            else if (entry.getName().equals("routes.csv"))
            {
                int done = StarRouteLogic.importData(zis, merge, pm);
                if (report.length() > 0)
                    report.append(", ");
                report.append(done+" routes");
            }
            else if (entry.getName().equals("metadata.csv"))                
            {
                int done = MetadataLogic.importData(zis, merge, pm);
                if (report.length() > 0)
                    report.append(", ");
                report.append(done+" metadata");
            }
            else if (entry.getName().equals("deletions.csv"))                
            {
                int done = DeletionLogic.importData(zis, merge, pm);
                if (report.length() > 0)
                    report.append(", ");
                report.append(done+" deletions");
            }
            if (pm.isCanceled())
                break;
        }
        zis.close();
        pm.done();
        return report.toString();
    }
    
    private static String makeDBName(File ws, String name)
    {
        Set<String> taken = new HashSet<String>();
        for (IDataSource src : mDataSources)
            if (src instanceof DerbyDataSource)
            {
                DerbyDataSource dsrc = (DerbyDataSource)src;
                taken.add(dsrc.getDBName());
            }
        String dbName = "";
        for (char c : name.toCharArray())
            if (Character.isAlphabetic(c))
                dbName += Character.toUpperCase(c);
        if (dbName.length() == 0)
            dbName = "CHUCK";
        while (taken.contains(dbName) || (new File(ws, dbName)).exists())
            dbName += String.valueOf(System.currentTimeMillis()%10);
        return dbName;
    }
}
