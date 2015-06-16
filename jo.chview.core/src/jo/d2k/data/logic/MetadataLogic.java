package jo.d2k.data.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jo.d2k.data.data.MetadataBean;
import jo.d2k.data.io.IOMetadataHandler;
import jo.util.beans.Bean;
import jo.util.beans.CSVLogic;
import jo.util.utils.IProgMon;

public class MetadataLogic
{
    private static IOMetadataHandler getHandler()
    {
        return (IOMetadataHandler)ApplicationLogic.getHandler(ApplicationLogic.METADATA_HANDLER);
    }
    
    public static MetadataBean getByID(long oid)
    {
        return getHandler().findByOID(oid);
    }

    public static List<MetadataBean> getAll()
    {
        return getHandler().findAll();
    }
    
    public static void deleteAll()
    {
        getHandler().deleteAll();
    }

    public static void delete(MetadataBean bean)
    {
        getHandler().delete(bean);
    }

    public static void delete(List<MetadataBean> beans)
    {
        getHandler().delete(beans);
    }
    
    public static MetadataBean create(MetadataBean Metadata)
    {
        MetadataBean bean = getHandler().newInstance();
        long oid = bean.getOID();
        bean.set(Metadata);
        bean.setOID(oid);
        getHandler().update(bean);
        return bean;
    }

    public static void update(MetadataBean star)
    {
        if (star.getOID() == -1)
            star.setOID(System.currentTimeMillis());
        getHandler().update(star);
    }

    public static void update(List<MetadataBean> stars)
    {
        long uid = System.currentTimeMillis();
        for (MetadataBean star : stars)
            if (star.getOID() == -1)
                star.setOID(uid++);
        getHandler().update(stars);
    }
    
    public static int exportData(OutputStream os, IProgMon pm) throws IOException
    {
        int done = 0;
        OutputStreamWriter wtr = new OutputStreamWriter(os, "utf-8");
        try
        {
            List<MetadataBean> beans = getHandler().findAll();
            if (pm != null)
                pm.beginTask("Export Metadata", beans.size());
            // remove password
            for (Iterator<MetadataBean> i = beans.iterator(); i.hasNext(); )
            {
                MetadataBean bean = i.next();
                if ("db.info".equals(bean.getDomain()) && "password".equals(bean.getKey()))
                {
                    i.remove();
                    break;
                }
            }
            CSVLogic.toCSV(wtr, MetadataBean.class, beans);
            done += beans.size();
        }
        catch (Exception e)
        {
            throw new IOException("Error fetching metadata from database", e);
        }      
        finally
        {
            if (pm != null)
                pm.done();
        }
        return done;
    }
    
    public static int importData(InputStream is, boolean merge, IProgMon pm) throws IOException
    {
        int done = 0;
        InputStreamReader rdr = new InputStreamReader(is, "utf-8");
        try
        {
            Collection<Bean> beans = CSVLogic.fromCVS(rdr, MetadataBean.class);
            if (!merge)
                getHandler().deleteAll();
            if (pm != null)
                pm.beginTask("Import Metadata", beans.size());
            for (Bean b : beans)
            {
                MetadataBean route = (MetadataBean)b;
                MetadataLogic.update(route);
                done++;
                if (pm != null)
                {
                    pm.subTask("Done "+done+" metadata");
                    pm.worked(1);
                    if (pm.isCanceled())
                        break;
                }
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e); // should never happen
        }
        finally
        {
            if (pm != null)
                pm.done();
        }
        return done;
    }

    public static List<MetadataBean> findByDomainIndex(String domain,
            long index)
    {
        List<MetadataBean> md;
        if (index == -1)
            md = getHandler().findMultiple("domain", domain, null);
        else
            md = getHandler().findMultiple("domain", domain, "index", String.valueOf(index), null);
        return md;
    }

    public static MetadataBean findByDomainIndexKey(String domain,
            long index, String key)
    {
        String[] cols;
        String[] vals;
        if (index == -1)
        {
            cols = new String[] { "domain", "key" };
            vals = new String[] { domain, key };
        }
        else
        {
            cols = new String[] { "domain", "index", "key" };
            vals = new String[] { domain, String.valueOf(index), key };
        }
        try
        {
            List<MetadataBean> mds = getHandler().find(cols, vals, true, null);
            if (mds.size() > 0)
                return mds.get(0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String getValue(String domain, long index, String key)
    {
        MetadataBean md = findByDomainIndexKey(domain, index, key);
        if (md == null)
            return null;
        else
            return md.getStringValue();
    }
    
    public static void setValue(String domain, long index, String key, String value)
    {
        MetadataBean md = findByDomainIndexKey(domain, index, key);
        if (md == null)
        {
            md = new MetadataBean();
            md.setDomain(domain);
            md.setIndex(index);
            md.setKey(key);
            md.setStringValue(value);
            create(md);
        }
        else
        {
            md.setStringValue(value);
            update(md);
        }
    }

    public static Map<String, String> getAsMap(String domain, long index)
    {
        List<MetadataBean> md = findByDomainIndex(domain, index);
        Map<String,String> map = new HashMap<String, String>();
        for (MetadataBean m : md)
            map.put(m.getKey(), m.getStringValue());
        return map;
    }

    public static void setAsMap(String domain, long index, Map<String, String> map)
    {
        delete(domain, index);
        List<MetadataBean> md = new ArrayList<MetadataBean>();
        for (String key : map.keySet())
        {
            String val = map.get(key);
            MetadataBean m = getHandler().newInstance();
            m.setDomain(domain);
            m.setIndex(index);
            m.setKey(key);
            m.setStringValue(val);
            md.add(m);
        }
        update(md);
    }

    public static void delete(String domain, long index)
    {
        List<MetadataBean> md = findByDomainIndex(domain, index);
        delete(md);
    }

    public static void deleteByOID(long oid)
    {
        getHandler().delete("OID", String.valueOf(oid));
    }
}
