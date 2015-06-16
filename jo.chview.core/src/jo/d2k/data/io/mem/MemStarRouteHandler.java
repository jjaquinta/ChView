package jo.d2k.data.io.mem;

import java.beans.IntrospectionException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarRouteBean;
import jo.d2k.data.io.IOStarRouteHandler;
import jo.util.dao.mem.MemBeanHandler2;

public class MemStarRouteHandler extends MemBeanHandler2<StarRouteBean> implements
        IOStarRouteHandler
{
    public MemStarRouteHandler() throws IntrospectionException, SQLException
    {
        super(StarRouteBean.class);
    }
    
    public List<StarRouteBean> findAllLinking(List<StarBean> stars)
    {
        Set<String> ids = new HashSet<String>();
        for (StarBean star : stars)
            ids.add(star.getQuadrant()+star.getOID());
        List<StarRouteBean> matching = new ArrayList<StarRouteBean>();
        for (StarRouteBean route : findAll())
            if (ids.contains(route.getStar1Quad()+route.getStar1()) || ids.contains(route.getStar2Quad()+route.getStar2()))
                matching.add(route);
        return matching;
    }
}
