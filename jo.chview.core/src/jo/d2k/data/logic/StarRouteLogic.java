package jo.d2k.data.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarRouteBean;
import jo.d2k.data.io.IOStarRouteHandler;
import jo.util.beans.Bean;
import jo.util.beans.CSVLogic;
import jo.util.utils.IProgMon;

public class StarRouteLogic
{
    private static IOStarRouteHandler getHandler()
    {
        return (IOStarRouteHandler)ApplicationLogic.getHandler(ApplicationLogic.STAR_ROUTE_HANDLER);
    }
    
    public static StarRouteBean getByID(long oid)
    {
        return getHandler().findByOID(oid);
    }

    public static List<StarRouteBean> getAll()
    {
        return getHandler().findAll();
    }
    
    public static void deleteAll()
    {
        getHandler().deleteAll();
    }

    public static void delete(StarRouteBean bean)
    {
        getHandler().delete(bean);
    }

    public static void delete(List<StarRouteBean> beans)
    {
        getHandler().delete(beans);
    }
    
    public static StarRouteBean create(StarRouteBean starRoute)
    {
        StarRouteBean bean = getHandler().newInstance();
        long oid = bean.getOID();
        bean.set(starRoute);
        bean.setOID(oid);
        getHandler().update(bean);
        return bean;
    }

    public static void update(StarRouteBean star)
    {
        if (star.getOID() == -1)
            star.setOID(System.currentTimeMillis());
        getHandler().update(star);
    }

    public static void update(List<StarRouteBean> stars)
    {
        long uid = System.currentTimeMillis();
        for (StarRouteBean star : stars)
            if (star.getOID() == -1)
                star.setOID(uid++);
        getHandler().update(stars);
    }

    public static List<StarRouteBean> getAllLinking(List<StarBean> stars)
    {
        if (stars.size() == 0)
            return new ArrayList<StarRouteBean>();
        List<StarRouteBean> hits = getHandler().findAllLinking(stars);
        Map<Long, StarBean> index = new HashMap<Long, StarBean>();
        for (StarBean star : stars)
            index.put(star.getOID(), star);
        for (StarRouteBean route : hits)
        {
            route.setStar1Ref(index.get(route.getStar1()));
            route.setStar2Ref(index.get(route.getStar2()));
        }
        return hits;
    }
    
    public static void getReferences(StarRouteBean route)
    {
        if (route.getStar1Ref() == null)
            route.setStar1Ref(StarLogic.getByQuadrantID(route.getStar1Quad(), route.getStar1()));
        if (route.getStar2Ref() == null)
            route.setStar2Ref(StarLogic.getByQuadrantID(route.getStar2Quad(), route.getStar2()));
    }
    
    public static int exportData(OutputStream os, IProgMon pm) throws IOException
    {
        int done = 0;
        OutputStreamWriter wtr = new OutputStreamWriter(os, "utf-8");
        try
        {
            List<StarRouteBean> beans = getHandler().findAll();
            if (pm != null)
                pm.beginTask("Export Routes", beans.size());
            CSVLogic.toCSV(wtr, StarRouteBean.class, beans);
            done += beans.size();
        }
        catch (Exception e)
        {
            throw new IOException("Error fetching routes from database", e);
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
            Collection<Bean> beans = CSVLogic.fromCVS(rdr, StarRouteBean.class);
            if (!merge)
                getHandler().deleteAll();
            if (pm != null)
                pm.beginTask("Import Routes", beans.size());
            for (Bean b : beans)
            {
                StarRouteBean route = (StarRouteBean)b;
                StarRouteLogic.update(route);
                done++;
                if (pm != null)
                {
                    pm.subTask("Done "+done+" routes");
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
        if (pm != null)
            pm.done();
        return done;
    }
}
