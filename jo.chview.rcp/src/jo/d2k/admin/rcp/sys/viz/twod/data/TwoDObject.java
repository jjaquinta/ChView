package jo.d2k.admin.rcp.sys.viz.twod.data;

import jo.util.beans.Bean;
import jo.util.geom2d.Point2D;

public class TwoDObject extends Bean
{
    private Point2D     mLocation;
    
    public Point2D getLocation()
    {
        return mLocation;
    }
    public void setLocation(Point2D location)
    {
        mLocation = location;
    }
}
