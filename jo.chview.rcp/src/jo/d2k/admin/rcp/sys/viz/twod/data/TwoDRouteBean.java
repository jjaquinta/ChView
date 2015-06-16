package jo.d2k.admin.rcp.sys.viz.twod.data;

import jo.d2k.data.data.StarRouteBean;

public class TwoDRouteBean extends TwoDConnectionBean
{
    private StarRouteBean   mRoute;
    
    public StarRouteBean getRoute()
    {
        return mRoute;
    }
    public void setRoute(StarRouteBean route)
    {
        mRoute = route;
    }
}
