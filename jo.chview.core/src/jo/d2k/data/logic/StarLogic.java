package jo.d2k.data.logic;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarSchemaBean;
import jo.d2k.data.io.IOStarHandler;
import jo.d4w.logic.D4WStarGenLogic;
import jo.util.beans.Bean;
import jo.util.beans.BeanLogic;
import jo.util.beans.CSVLogic;
import jo.util.beans.PropChangeSupport;
import jo.util.dao.sql.SQLBeanHandler2;
import jo.util.utils.DebugUtils;
import jo.util.utils.IProgMon;

public class StarLogic
{
    public static final double QUAD_SIZE = 15.0;

    public static final String SPECTRA = "OBAFGKMLTY";
    
    // quadrant cache
    private static final Map<String, List<StarBean>> mCache = new HashMap<String, List<StarBean>>();
    private static PropChangeSupport mPCS = new PropChangeSupport(StarLogic.class);
    
    private static IOStarHandler getHandler()
    {
        return (IOStarHandler)ApplicationLogic.getHandler(ApplicationLogic.STAR_HANDLER);
    }
    
    public static StarBean getByID(long oid)
    {
        return getHandler().findByOID(oid);
    }

    static void clearCache()
    {
        if (mCache.size() > 0)
        {
            mCache.clear();
            mPCS.fireMonotonicPropertyChange("data", mCache);
        }
    }
    
    private static void clearCache(String quad)
    {
        if (mCache.containsKey(quad))
        {
            mCache.remove(quad);
            mPCS.fireMonotonicPropertyChange("data", mCache);
        }
    }
    
    private static void clearCache(Collection<String> quads)
    {
        boolean doneAny = false;
        for (String q : quads)
            if (mCache.containsKey(q))
            {
                mCache.remove(q);
                doneAny = true;
            }
        if (doneAny)
            mPCS.fireMonotonicPropertyChange("data", mCache);
    }
    
    private static List<StarBean> getFromDatabaseByQuadrants(Collection<String> quadrants)
    {
        String[] vals = quadrants.toArray(new String[0]);
        String[] cols = new String[vals.length];
        for (int i = 0; i < cols.length; i++)
            cols[i] = "Quadrant";
        try
        {
            return getHandler().find(cols, vals, false, true, "Name");
        }
        catch (Exception e)
        {
            return new ArrayList<StarBean>();
        }
    }
    
    public static StarBean getByQuadrantID(String quadrant, long oid)
    {
        if ((quadrant == null) || (oid <= 0))
            return null;
        List<String> quads = new ArrayList<String>();
        quads.add(quadrant);
        List<StarBean> stars = getByQuadrants(quads);
        for (StarBean s : stars)
            if (s.getOID() == oid)
                return s;
        DebugUtils.error("Can't find q="+quadrant+", id="+oid);
        for (StarBean s : stars)
            DebugUtils.error("  "+s.getOID()+" - "+s.getName());
        return null;
    }

    private static Boolean noHandler = false;
    
    public static List<StarBean> getByQuadrants(Collection<String> quadrants)
    {
        List<StarBean> results = new ArrayList<StarBean>();
        synchronized (mCache)
        {
            if (noHandler == null)
                noHandler = (getHandler() == null);
            // first fetch from cache
            for (Iterator<String> i = quadrants.iterator(); i.hasNext(); )
            {
                String q = i.next();
                if (mCache.containsKey(q))
                {
                    results.addAll(mCache.get(q));
                    i.remove();
                }
            }
            if (quadrants.size() == 0)
                return results;
            // get from database
            List<StarBean> dbStars;
            if (noHandler)
                dbStars = new ArrayList<StarBean>();
            else
                dbStars = getFromDatabaseByQuadrants(quadrants);
            // split into quads
            Map<String, List<StarBean>> quads = new HashMap<String, List<StarBean>>();
            for (StarBean star : dbStars)
            {
                List<StarBean> quad = quads.get(star.getQuadrant());
                if (quad == null)
                {
                    quad = new ArrayList<StarBean>();
                    quads.put(star.getQuadrant(), quad);
                }
                quad.add(star);
            }
            // sort out parents and children
            for (List<StarBean> quad : quads.values())
            {
                Map<Long,StarBean> index = new HashMap<Long, StarBean>();
                for (StarBean star : quad)
                    index.put(star.getOID(), star);
                for (StarBean star : quad)
                    if (star.getParent() > 0)
                    {
                        StarBean parent = index.get(star.getParent());
                        if (parent == null)
                        {
                            DebugUtils.error("Lost child "+star.getName()+" can't find parent #"+star.getParent());
                            continue;
                        }
                        star.setParentRef(parent);
                        parent.getChildren().add(star);
                    }
            }
            // get generated stars
            for (String q : quadrants)
            {
                List<StarBean> quad = quads.get(q);
                if (quad == null)
                {
                    quad = new ArrayList<StarBean>();
                    quads.put(q, quad);
                }
                List<StarBean> generated = StarGenLogic.genQuadrant(q, quad);
                quad.addAll(generated);
            }
            // remove deletions and cache
            Set<Long> deletions = DeletionLogic.findByQuads(quadrants);
            for (String q : quadrants)
            {
                List<StarBean> quad = quads.get(q);
                for (Iterator<StarBean> i = quad.iterator(); i.hasNext(); )
                {
                    StarBean star = i.next();
                    if (deletions.contains(star.getOID()))
                        i.remove();
                }
                mCache.put(q, quad);
                results.addAll(quad);
            }
        }
        return results;
    }

    public static List<StarBean> getRange(int offset, int length)
    {
        try
        {
            return getHandler().find(null, null, false, null, false, offset, length);
        }
        catch (Exception e)
        {
            return new ArrayList<StarBean>();
        }
    }
    
    public static void deleteAll()
    {
        getHandler().deleteAll();
    }

    public static void delete(StarBean bean)
    {
        synchronized (mCache)
        {
            if (bean.isGenerated())
                DeletionLogic.deleteStar(bean);
            else
                getHandler().delete(bean);
            clearCache(bean.getQuadrant());
        }
    }

    public static void delete(List<StarBean> beans)
    {
        synchronized (mCache)
        {
            List<StarBean> nongenerated = new ArrayList<StarBean>();
            List<StarBean> generated = new ArrayList<StarBean>();
            Set<String> quads = new HashSet<String>();
            for (StarBean bean : beans)
            {
                if (bean.isGenerated())
                    generated.add(bean);
                else
                    nongenerated.add(bean);
                quads.add(bean.getQuadrant());
            }
            if (nongenerated.size() > 0)
                getHandler().delete(beans);
            if (generated.size() > 0)
                DeletionLogic.deleteStars(generated);
            clearCache(quads);
        }
    }
    
    public static StarBean create(StarBean star)
    {
        StarBean bean = getHandler().newInstance();
        synchronized (mCache)
        {
            long oid = bean.getOID();
            bean.set(star);
            bean.setOID(oid);
            bean.setQuadrant(getQuadrant(bean.getX(), bean.getY(), bean.getZ()));
            getHandler().update(bean);
            clearCache(bean.getQuadrant());
        }
        return bean;
    }

    public static void update(StarBean bean)
    {
        synchronized (mCache)
        {
            if (bean.isGenerated())
            {
                DeletionLogic.deleteStar(bean); // remove from generated
                create(bean);
            }
            else
            {
                bean.setQuadrant(getQuadrant(bean.getX(), bean.getY(), bean.getZ()));
                getHandler().update(bean);
            }
            clearCache(bean.getQuadrant());
        }        
    }

    public static void update(List<StarBean> stars)
    {
        synchronized (mCache)
        {
            long uid = System.currentTimeMillis();
            Set<String> quads = new HashSet<String>();
            for (StarBean star : stars)
            {
                if (star.getOID() == -1)
                    star.setOID(uid++);
                star.setQuadrant(getQuadrant(star.getX(), star.getY(), star.getZ()));
                quads.add(star.getQuadrant());
            }
            getHandler().update(stars);
            clearCache(quads);
        }
    }
    
    public static String getQuadrant(double x, double y, double z)
    {
        StringBuffer quad = new StringBuffer();
        quad.append(getQuadrant(x));
        quad.append(getQuadrant(y));
        quad.append(getQuadrant(z));
        return quad.toString();
    }
    
    private static char getQuadrant(double d)
    {
        int idx = (int)((Math.abs(d)+QUAD_SIZE/2)/QUAD_SIZE);
        if (idx > 26)
            idx = 26;
        if (idx == 0)
            return '0';
        if (d > 0)
            return (char)('A'+idx-1);
        else
            return (char)('a'+idx-1);
    }
    
    public static double getOrd(char q)
    {
        if (q == '0')
            return 0;
        else if (Character.isLowerCase(q))
            return -(q - 'a' + 1)*QUAD_SIZE;
        else if (Character.isUpperCase(q))
            return (q - 'A' + 1)*QUAD_SIZE;
        return 0;
    }

    public static List<StarBean> getAllWithin(double X1, double Y1, double Z1, double X2, double Y2, double Z2)
    {
        double lowX = Math.min(X1, X2);
        double lowY = Math.min(Y1, Y2);
        double lowZ = Math.min(Z1, Z2);
        double highX = Math.max(X1, X2);
        double highY = Math.max(Y1, Y2);
        double highZ = Math.max(Z1, Z2);
        //DebugUtils.trace("X: "+lowX+" to "+highX);
        Set<String> quads = new HashSet<String>();
        quads.add(D4WStarGenLogic.quadCoordToStr(lowX, lowY, lowZ));
        quads.add(D4WStarGenLogic.quadCoordToStr(lowX, lowY, highZ));
        quads.add(D4WStarGenLogic.quadCoordToStr(lowX, highY, lowZ));
        quads.add(D4WStarGenLogic.quadCoordToStr(lowX, highY, highZ));
        quads.add(D4WStarGenLogic.quadCoordToStr(highX, lowY, lowZ));
        quads.add(D4WStarGenLogic.quadCoordToStr(highX, lowY, highZ));
        quads.add(D4WStarGenLogic.quadCoordToStr(highX, highY, lowZ));
        quads.add(D4WStarGenLogic.quadCoordToStr(highX, highY, highZ));
        for (double x = lowX + QUAD_SIZE/2; x <= highX; x += QUAD_SIZE/2)
        {
            //DebugUtils.trace("Testing "+x);
            for (double y = lowY + QUAD_SIZE/2; y <= highY; y += QUAD_SIZE/2)
                for (double z = lowZ + QUAD_SIZE/2; z <= highZ; z += QUAD_SIZE/2)
                {
                    String quadrant = getQuadrant(x, y, z);
                    quads.add(quadrant);
                }
        }
        //DebugUtils.trace(quads.size()+" quadrants");
        List<StarBean> hits = getByQuadrants(quads);
        //DebugUtils.trace(hits.size()+" before bounds checks");
        //for (StarBean star : hits)
        //    DebugUtils.trace("  "+star.getName()+" "+star.getX()+","+star.getY()+","+star.getZ());
        for (Iterator<StarBean> i = hits.iterator(); i.hasNext(); )
        {
            StarBean star = i.next();
            if ((star.getX() < lowX) || (star.getX() > highX)
                    || (star.getY() < lowY) || (star.getY() > highY)
                    || (star.getZ() < lowZ) || (star.getZ() > highZ))
            {
                //DebugUtils.trace("Removing "+star.getName()+" "+star.getX()+","+star.getY()+","+star.getZ());
                i.remove();
            }
        }
        //DebugUtils.trace(hits.size()+" remaining after rectangular bounds checks");
        return hits;
    }

    public static List<StarBean> getAllWithin(double x, double y, double z, double radius)
    {
        List<StarBean> hits = getAllWithin(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
        for (Iterator<StarBean> i = hits.iterator(); i.hasNext(); )
        {
            StarBean star = i.next();
            double d = Math.sqrt((star.getX() - x)*(star.getX() - x)
                    +(star.getY() - y)*(star.getY() - y)
                    +(star.getZ() - z)*(star.getZ() - z));
            if (d > radius)
            {
                //DebugUtils.trace("Removing "+star.getName()+" "+star.getX()+","+star.getY()+","+star.getZ()+", r="+d);
                i.remove();
            }
        }
        //DebugUtils.trace(hits.size()+" remaining after spherical bounds checks");
        return hits;
    }
    
    public static StarBean getByURI(String uri)
    {
        try
        {
            URI u = new URI(uri);
            long oid = Long.parseLong(u.getUserInfo(), 16);
            String quadrant = u.getHost();
            return getByQuadrantID(quadrant, oid);
        }
        catch (URISyntaxException e)
        {
            throw new IllegalArgumentException("Cannot fathom uri '"+uri+"'", e);
        }
    }
    
    public static int exportData(OutputStream os, IProgMon pm) throws IOException
    {
        int done = 0;
        OutputStreamWriter wtr = new OutputStreamWriter(os, "utf-8");
        try
        {
            if (pm != null)
            {
                int tot = getHandler().countAll();
                pm.beginTask("Export Stars", tot);
            }
            Map<String,PropertyDescriptor> descriptors = BeanLogic.getDescriptors(StarBean.class);
            String[] columns = descriptors.keySet().toArray(new String[0]);
            wtr.write(CSVLogic.toCSVHeader(columns));
            wtr.write("\r\n");
            for (int offset = 0; ; offset += 1000)
            {
                List<StarBean> beans;
                try
                {
                    beans = getHandler().find(null, null, false, null, false, offset, 1000);
                }
                catch (Exception e)
                {
                    throw new IOException("Error fetching stars from database", e);
                }
                for (Bean bean : beans)
                {
                    wtr.write(CSVLogic.toCSVLine(columns, descriptors, bean));
                    wtr.write("\r\n");
                }
                done += beans.size();
                if (pm != null)
                {
                    pm.worked(beans.size());
                    pm.subTask("Done "+done+" stars");
                    if (pm.isCanceled())
                        break;
                }
                if (beans.size() < 1000)
                    break;
            }
            wtr.flush();
        }
        catch (IntrospectionException e)
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
    
    public static int importData(InputStream is, boolean merge, IProgMon pm) throws IOException
    {
        mPCS.setBroadcast(false);
        int done = 0;
        InputStreamReader rdr = new InputStreamReader(is, "utf-8");
        try
        {
            Collection<Bean> beans = CSVLogic.fromCVS(rdr, StarBean.class);
            if (!merge)
                getHandler().deleteAll();
            if (pm != null)
                pm.beginTask("Import Stars", beans.size());
            List<StarBean> stars = new ArrayList<StarBean>();
            for (Bean b : beans)
            {
                StarBean star = (StarBean)b;
                stars.add(star);
                if (stars.size() >= 100)
                {
                    StarLogic.update(stars);
                    done += stars.size();
                    if (pm != null)
                    {
                        pm.worked(stars.size());
                        pm.subTask("Done "+done+" stars");
                        if (pm.isCanceled())
                            break;
                    }
                    stars.clear();
                }
            }
            if (stars.size() > 0)
            {
                StarLogic.update(stars);
                done += stars.size();
                if (pm != null)
                {
                    pm.worked(stars.size());
                }
                stars.clear();
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e); // should never happen
        }      
        if (pm != null)
            pm.done();
        clearCache();
        mPCS.setBroadcast(true);
        return done;
    }
    
    public static Map<String, String> getMetadataMap(String... ids)
    {
        Map<String,String> map = new HashMap<String, String>();
        List<StarSchemaBean> schemas = StarSchemaLogic.getSchemas();
        for (StarSchemaBean schema : schemas)
            for (int i = 0; i < ids.length; i++)
                if (ids[i].equalsIgnoreCase(schema.getTitle()))
                {
                    map.put(ids[i], schema.getMetadataID());
                    break;
                }
        return map;
    }

    public static List<StarBean> getSecondaryStars()
    {
        return getHandler().findMultiple("parent", SQLBeanHandler2.NOT_EQUAL+0, null);
    }

    public static Map<String,String> getMetadata(StarBean star)
    {
        if (star.getMetadata() == null)
            star.setMetadata(MetadataLogic.getAsMap("star.md", star.getOID()));
        return star.getMetadata();
    }
    
    // listeners
    public static void addPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
        mPCS.addPropertyChangeListener(prop, pcl);
    }
    public static void addPropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.addPropertyChangeListener(pcl);
    }
    public static void addUIPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
        mPCS.addUIPropertyChangeListener(prop, pcl);
    }
    public static void addUIPropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.addUIPropertyChangeListener(pcl);
    }
    public static void removePropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.removePropertyChangeListener(pcl);
    }
}
