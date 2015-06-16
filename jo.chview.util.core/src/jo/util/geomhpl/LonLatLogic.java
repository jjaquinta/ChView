package jo.util.geomhpl;

import jo.util.geom3d.Point3D;
import jo.util.geom3d.Point3DLogic;


public class LonLatLogic
{
    public static double dist(LonLat ll1, LonLat ll2)
    {
        // http://www.movable-type.co.uk/scripts/latlong.html
        double dLat = ll2.getLatitude() - ll1.getLatitude();
        double dLon = ll2.getLongitude() - ll1.getLongitude();
        if (dLon < -Math.PI*2)
            dLon += Math.PI*2;
        else if (dLon > Math.PI*2)
            dLon -= Math.PI*2;

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(ll1.getLatitude()) * Math.cos(ll2.getLatitude()) * 
                Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return c;
    }
    
    public static Point3D toVector(LonLat ll)
    {
        double x = Math.cos(ll.getLongitude())*Math.cos(ll.getLatitude()); 
        double y = Math.sin(ll.getLongitude())*Math.cos(ll.getLatitude()); 
        double z = Math.sin(ll.getLatitude());
        return new Point3D(x, y, z);
    }
    
    public static LonLat toLonLat(Point3D v)
    {
        v = v.normal();
        double lon = Math.atan2(v.x, v.y);
        double lat = Math.atan2(Math.sqrt(v.x*v.x + v.y*v.y), v.z);
        return new LonLat(lon, lat);
    }

    public static LonLat interpolate(double v, double low, double high, LonLat from, LonLat to)
    {
        Point3D fvec = toVector(from);
        Point3D tvec = toVector(to);
        Point3D ivec = Point3DLogic.interpolate(v, low, high, fvec, tvec);
        LonLat ill = toLonLat(ivec);
        return ill;
    }
    
    public static void normalize(LonLat ll)
    {
        while (ll.getLongitude() < -Math.PI)
            ll.setLongitude(ll.getLongitude() + Math.PI*2);
        while (ll.getLongitude() >= Math.PI)
            ll.setLongitude(ll.getLongitude() - Math.PI*2);
        if (ll.getLatitude() > Math.PI/2)
            throw new IllegalArgumentException("Unsupported lattitude:"+ll.getLatitude());
        if (ll.getLatitude() < -Math.PI/2)
            throw new IllegalArgumentException("Unsupported lattitude:"+ll.getLatitude());
    }
}
