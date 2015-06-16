package jo.d2k.data.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jo.d2k.data.data.DeletionBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.io.IODeletionHandler;
import jo.util.beans.Bean;
import jo.util.beans.CSVLogic;
import jo.util.utils.IProgMon;

public class DeletionLogic
{
    private static IODeletionHandler getHandler()
    {
        return (IODeletionHandler)ApplicationLogic.getHandler(ApplicationLogic.DELETION_HANDLER);
    }
    
    public static DeletionBean getByID(long oid)
    {
        return getHandler().findByOID(oid);
    }

    public static List<DeletionBean> getAll()
    {
        return getHandler().findAll();
    }
    
    public static void deleteAll()
    {
        getHandler().deleteAll();
    }

    public static void delete(DeletionBean bean)
    {
        getHandler().delete(bean);
    }

    public static void delete(List<DeletionBean> beans)
    {
        getHandler().delete(beans);
    }
    
    public static DeletionBean create(DeletionBean Deletion)
    {
        DeletionBean bean = getHandler().newInstance();
        long oid = bean.getOID();
        bean.set(Deletion);
        bean.setOID(oid);
        getHandler().update(bean);
        return bean;
    }

    public static void update(DeletionBean bean)
    {
        if (bean.getOID() == -1)
            bean.setOID(System.currentTimeMillis());
        getHandler().update(bean);
    }

    public static void update(List<DeletionBean> beans)
    {
        long uid = System.currentTimeMillis();
        for (DeletionBean bean : beans)
            if (bean.getOID() == -1)
                bean.setOID(uid++);
        getHandler().update(beans);
    }
    
    public static int exportData(OutputStream os, IProgMon pm) throws IOException
    {
        int done = 0;
        OutputStreamWriter wtr = new OutputStreamWriter(os, "utf-8");
        try
        {
            List<DeletionBean> beans = getHandler().findAll();
            if (pm != null)
                pm.beginTask("Export Deletions", beans.size());
            CSVLogic.toCSV(wtr, DeletionBean.class, beans);
            done += beans.size();
        }
        catch (Exception e)
        {
            throw new IOException("Error fetching deletions from database", e);
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
            Collection<Bean> beans = CSVLogic.fromCVS(rdr, DeletionBean.class);
            if (!merge)
                getHandler().deleteAll();
            if (pm != null)
                pm.beginTask("Import Deletions", beans.size());
            for (Bean b : beans)
            {
                DeletionBean route = (DeletionBean)b;
                DeletionLogic.update(route);
                done++;
                if (pm != null)
                {
                    pm.subTask("Done "+done+" deletions");
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

    private static DeletionBean newInstance(StarBean star)
    {
        DeletionBean bean = getHandler().newInstance();
        bean.setStarQuad(star.getQuadrant());
        bean.setStarOID(star.getOID());
        bean.setName(star.getName());
        bean.setX(star.getX());
        bean.setY(star.getY());
        bean.setZ(star.getZ());
        return bean;
    }
    
    public static Set<Long> findByQuad(String quad)
    {
        List<DeletionBean> dels = getHandler().findMultiple("starquad", quad, null);
        Set<Long> oids = new HashSet<Long>();
        for (DeletionBean del : dels)
            oids.add(del.getStarOID());
        return oids;
    }
    
    public static Set<Long> findByQuads(Collection<String> quadrants)
    {
        String[] vals = quadrants.toArray(new String[0]);
        String[] cols = new String[vals.length];
        for (int i = 0; i < cols.length; i++)
            cols[i] = "starquad";
        try
        {
            List<DeletionBean> dels = getHandler().find(cols, vals, false, true, null);
            Set<Long> oids = new HashSet<Long>();
            for (DeletionBean del : dels)
                oids.add(del.getStarOID());
            return oids;
        }
        catch (Exception e)
        {
            return new HashSet<Long>();
        }
    }

    public static void deleteStar(StarBean star)
    {
        if (!star.isGenerated())
            return;
        DeletionBean bean = newInstance(star);
        update(bean);
    }

    public static void deleteStars(List<StarBean> stars)
    {
        List<DeletionBean> beans = new ArrayList<DeletionBean>();
        for (StarBean star : stars)
        {
            if (!star.isGenerated())
                continue;
            DeletionBean bean = newInstance(star);
            beans.add(bean);
        }
        update(beans);        
    }
}
