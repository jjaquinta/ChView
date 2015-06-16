package jo.d4w.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jo.d2k.data.data.RegionCubeBean;
import jo.d2k.data.data.RegionQuadBean;
import jo.d2k.data.data.RegionSphereBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.StarLogic;
import jo.util.html.URIBuilder;
import jo.util.utils.DebugUtils;

public class D4WStarLogic
{

    public static RegionQuadBean getByQuadrant(String quadrant)
    {
        List<String> quads = new ArrayList<String>();
        quads.add(quadrant);
        RegionQuadBean q = new RegionQuadBean();
        q.setURI("quad://"+quadrant);
        q.setQuad(quadrant);
        q.setStars(getByQuadrants(quads));
        return q;
    }
    
    public static StarBean getByQuadrantID(String quadrant, long oid)
    {
        List<StarBean> expanded = D4WStarGenLogic.genQuadrant(quadrant);
        for (StarBean s : expanded)
            if (s.getOID() == oid)
                return s;
        DebugUtils.error("Can't find q="+quadrant+", id="+oid);
        for (StarBean s : expanded)
            DebugUtils.error("  "+s.getOID()+" - "+s.getName());
        return null;
    }

    public static List<StarBean> getSystemByQuadrantID(String quadrant, long oid)
    {
        List<StarBean> stars = new ArrayList<StarBean>();
        RegionQuadBean q = getByQuadrant(quadrant);
        for (StarBean s : q.getStars())
            if ((s.getOID() == oid) || (s.getParent() == oid))
                stars.add(s);
        return stars;
    }
    
    public static List<StarBean> getByQuadrants(Collection<String> quadrants)
    {
        List<StarBean> allQuads = new ArrayList<StarBean>();
        for (String q : quadrants)
        {
            List<StarBean> quad = D4WStarGenLogic.genQuadrant(q);
            allQuads.addAll(quad);
//            DebugUtils.trace("Quad:"+q);
//            for (StarBean star : quad)
//                DebugUtils.trace("  "+star.getURI()+", OID="+star.getOID());
        }
        return allQuads;
    }

    public static RegionCubeBean getAllWithin(double X1, double Y1, double Z1, double X2, double Y2, double Z2)
    {
        double lowX = Math.min(X1, X2);
        double lowY = Math.min(Y1, Y2);
        double lowZ = Math.min(Z1, Z2);
        double highX = Math.max(X1, X2);
        double highY = Math.max(Y1, Y2);
        double highZ = Math.max(Z1, Z2);
        //DebugUtils.trace("X: "+lowX+" to "+highX);
        Set<String> quads = new HashSet<String>();
        for (double x = lowX; x <= highX; x += StarLogic.QUAD_SIZE/2)
        {
            //DebugUtils.trace("Testing "+x);
            for (double y = lowY; y <= highY; y += StarLogic.QUAD_SIZE/2)
                for (double z = lowZ; z <= highZ; z += StarLogic.QUAD_SIZE/2)
                {
                    String quadrant = D4WStarGenLogic.quadCoordToStr(x, y, z);
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
        RegionCubeBean region = new RegionCubeBean();
        region.setURI("region://cube/"+lowX+"/"+lowY+"/"+lowZ+"/"+highX+"/"+highY+"/"+highZ);
        region.setX1(lowX);
        region.setY1(lowY);
        region.setZ1(lowZ);
        region.setX2(highX);
        region.setY2(highY);
        region.setZ2(highZ);
        region.setStars(hits);
        return region;
    }

    public static RegionSphereBean getAllWithin(double x, double y, double z, double radius)
    {
        RegionCubeBean hits = getAllWithin(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
        for (Iterator<StarBean> i = hits.getStars().iterator(); i.hasNext(); )
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
        RegionSphereBean region = new RegionSphereBean();
        region.setURI("region://sphere/"+x+"/"+y+"/"+z+"/"+radius);
        region.setX(x);
        region.setY(y);
        region.setZ(z);
        region.setR(radius);
        region.setStars(hits.getStars());
        return region;
    }
    
    public static String getURI(StarBean star)
    {
        StringBuffer uri = new StringBuffer("star://");
        uri.append(Long.toHexString(star.getOID()));
        uri.append("@");
        uri.append(star.getQuadrant());
        return uri.toString();
    }
    
    public static StarBean getByURI(String uri)
    {
        try
        {            
            URIBuilder u = new URIBuilder(uri);
            String sOID = u.getUser();
            String quadrant = u.getAuthority().substring(sOID.length() + 1);
            long oid = Long.parseLong(sOID, 16);
            return getByQuadrantID(quadrant, oid);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Cannot fathom uri '"+uri+"'", e);
        }
    }
}
