package jo.d2k.data.logic.ship;

import java.util.List;

import jo.d2k.data.logic.ConvLogic;
import jo.d2k.data.logic.stargen.SystemLogic;
import jo.d2k.data.logic.stargen.data.BodyBean;
import jo.d2k.data.logic.stargen.data.SolidBodyBean;
import jo.d2k.data.logic.stargen.data.SunBean;
import jo.d2k.data.ship.Location;
import jo.util.geom3d.Point3D;
import jo.util.geom3d.Point3DLogic;
import jo.util.utils.obj.DoubleUtils;
import jo.util.utils.obj.StringUtils;

public class LocationLogic
{
    public static String toURI(Location location)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(SystemLogic.getURI(location.getBody()));
        sb.replace(0, 4, "loc");
        sb.append("/");
        sb.append(location.getType());
        sb.append("/");
        sb.append(DoubleUtils.toString(location.getX()));
        sb.append("/");
        sb.append(DoubleUtils.toString(location.getY()));
        sb.append("/");
        sb.append(DoubleUtils.toString(location.getZ()));        
        return sb.toString();
    }
    
    public static Location fromURI(String uri)
    {
        Location location = new Location();
        BodyBean body = SystemLogic.getByURI(uri);
        List<String> elems = StringUtils.tokenize(uri, "/");
        String type = elems.get(elems.size() - 4);
        String x = elems.get(elems.size() - 3);
        String y = elems.get(elems.size() - 2);
        String z = elems.get(elems.size() - 1);
        location.setBody(body);
        location.setType(type);
        location.setX(DoubleUtils.parseDouble(x));
        location.setY(DoubleUtils.parseDouble(y));
        location.setZ(DoubleUtils.parseDouble(z));
        return location;
    }
    
    // in AU
    public static Point3D preciseLocation(Location l)
    {
        long now = System.currentTimeMillis();
        Point3D xyz = preciseLocation(l.getBody(), now);
        if (Location.LOW_ORBIT.equals(l.getType()))
            xyz.z += l.getBody().getRadius()*2;
        else if (Location.MEDIUM_ORBIT.equals(l.getType()))
            xyz.z += l.getBody().getRadius()*8;
        else if (Location.HIGH_ORBIT.equals(l.getType()))
            xyz.z += l.getBody().getRadius()*32;
        else if (l.getType().startsWith(Location.LAGRANGE_POINT))
        {
            Point3D parentXYZ = preciseLocation(l.getBody().getParent(), now);
            if (Location.L1.equals(l.getType()) || Location.L2.equals(l.getType()))
            {
                double R = xyz.dist(parentXYZ);
                double r = R*Math.pow(l.getBody().getMass()/(3*l.getBody().getParent().getMass()), .333333);
                Point3D l1 = Point3DLogic.sub(parentXYZ, xyz);
                l1.normalize();
                l1.mult(r);
                if (Location.L1.equals(l.getType()))
                    xyz.add(l1);
                else
                    xyz.sub(l1);
            }
            else if (Location.L3.equals(l.getType()))
            {
                double R = xyz.dist(parentXYZ);
                double r = R*Math.pow((7*l.getBody().getMass())/(12*l.getBody().getParent().getMass()), .333333);
                Point3D l3 = Point3DLogic.sub(parentXYZ, xyz);
                l3.normalize();
                l3.mult(R);
                xyz.add(l3);
                l3.normalize();
                l3.mult(R - r);
                xyz.add(l3);
            }
            else if (Location.L4.equals(l.getType()) || Location.L5.equals(l.getType()))
            {
                double sixthYear = ((SolidBodyBean)l.getBody()).getOrbPeriod()*24*60*60*1000/6.0;
                if (Location.L4.equals(l.getType()))
                    xyz = preciseLocation(l.getBody(), now + (long)sixthYear);
                else
                    xyz = preciseLocation(l.getBody(), now - (long)sixthYear);
            }
        }
        xyz.x += l.getX();
        xyz.y += l.getY();
        xyz.z += l.getZ();
        return xyz;
    }

    // in AU
    public static Point3D preciseLocation(BodyBean body, long time)
    {
        if (body instanceof SunBean)
        {
            SunBean sun = (SunBean)body;
            Point3D xyz = new Point3D();
            xyz.x = ConvLogic.convLYtoAU(sun.getStar().getX());
            xyz.y = ConvLogic.convLYtoAU(sun.getStar().getY());
            xyz.z = ConvLogic.convLYtoAU(sun.getStar().getZ());
            return xyz;
        }
        else if (body instanceof SolidBodyBean)
        {
            SolidBodyBean solid = (SolidBodyBean)body; 
            Point3D xyz = preciseLocation(solid.getParent(), time);
            double angle = Math.PI*2*(double)time/(double)(solid.getOrbPeriod()*24*60*60*1000);
            double r = solid.getA();
            xyz.x += r*Math.sin(angle);
            xyz.y += r*Math.cos(angle);
            return xyz;
        }
        else
            throw new IllegalArgumentException("Unknown body type '"+body.getClass().getName()+"'");
    }
    
    // in AU
    public static double distance(Location l1, Location l2)
    {
        Point3D p1 = preciseLocation(l1);
        Point3D p2 = preciseLocation(l2);
        return p1.dist(p2);
    }
}
