package jo.util.geomhpl;

import jo.util.geom2d.Point2D;

public class LonLat extends Point2D
{
    public static final LonLat NORTH_POLE = new LonLat(0, Math.PI/2);
    public static final LonLat SOUTH_POLE = new LonLat(0, -Math.PI/2);
    
    public LonLat()
    {
        x = 0;
        y = 0;
    }
    
    public LonLat(double lon, double lat)
    {
        super(lon, lat);
    }
    
    public LonLat(Point2D p)
    {
        super(p);
    }
    
    public String toString()
    {
        return (int)(x/Math.PI*180)+","+(int)(y/Math.PI*180);
    }
    
    public double getLongitude()
    {
        return x;
    }
    public void setLongitude(double longitude)
    {
        x = longitude;
    }
    public double getLatitude()
    {
        return y;
    }
    public void setLatitude(double latitude)
    {
        y = latitude;
    }
}
