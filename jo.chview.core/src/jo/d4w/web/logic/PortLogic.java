package jo.d4w.web.logic;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.StringTokenizer;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.StarExtraLogic;
import jo.d2k.data.logic.stargen.data.SunBean;
import jo.d4w.data.PopulatedObjectBean;
import jo.d4w.data.PopulatedSystemBean;
import jo.d4w.logic.D4WPopulationLogic;
import jo.d4w.logic.D4WStarLogic;
import jo.d4w.logic.D4WSystemLogic;
import jo.d4w.web.data.PortBean;
import jo.d4w.web.data.PortsBean;
import jo.util.html.URIBuilder;
import jo.util.utils.obj.DoubleUtils;

public class PortLogic
{
    public static PortsBean getPorts(URIBuilder u)
    {
        StringTokenizer st = new StringTokenizer(u.getHost(), ",");
        double x, y, z;
        if (st.countTokens() == 3)
        {
            x = DoubleUtils.parseDouble(st.nextToken());
            y = DoubleUtils.parseDouble(st.nextToken());
            z = DoubleUtils.parseDouble(st.nextToken());
        }
        else
        {
            StarBean star = D4WStarLogic.getByURI("star://"+u.getHost());
            if (star == null)
                throw new IllegalArgumentException("No star at '"+u.getHost()+"'");
            x = star.getX();
            y = star.getY();
            z = star.getZ();
        }
        double r = DoubleUtils.parseDouble(u.getPath().substring(1));
        if (r <= 0)
            r = 6;
        PortsBean ports = getPorts(x, y, z, r);
        ports.setURI(u.toString());
        ports.setX(x);
        ports.setY(y);
        ports.setZ(z);
        return ports;
    }
    
    public static PortsBean getPorts(double x, double y, double z, double r)
    {
        PortsBean ports = new PortsBean();
        List<StarBean> stars = D4WStarLogic.getAllWithin(x, y, z, r).getStars();
        for (StarBean star : stars)
        {
            PortBean port = makePort(star);
            if (port != null)
                ports.getPorts().add(port);
        }
        ports.setURI("ports://"+x+","+y+","+z+"/"+r);
        return ports;
    }
    
    public static PortBean getPort(URIBuilder u)
    {
        String starURI = "star://"+u.getHost();
        StarBean star = D4WStarLogic.getByURI(starURI);
        return makePort(star);
    }    
    
    public static PortBean getPort(double x, double y, double z, String name)
    {
        for (PortBean port : getPorts(x, y, z, .1).getPorts())
            if (port.getName().equals(name))
                return port;
        return null;
    }

    private static PortBean makePort(StarBean star)
    {
        if (star == null)
            return null;
        if (star.getParentRef() != null)
            return null;
        SunBean sun = D4WSystemLogic.generateSystem(star);
        PopulatedSystemBean sys = D4WPopulationLogic.getInstance(sun);
        PopulatedObjectBean topPop = null;
        for (PopulatedObjectBean pop : sys.getPopulations())
            if ((topPop == null) || (pop.getPopulation() > topPop.getPopulation()))
                topPop = pop;
        if (topPop == null)
            return null;
        return makePort(star, topPop);
    }

    private static PortBean makePort(StarBean star, PopulatedObjectBean pop)
    {
        PortBean port = new PortBean();
        port.setX(star.getX());
        port.setY(star.getY());
        port.setZ(star.getZ());
        port.setName(D4WPopulationLogic.getName(pop));
        port.setPopStats(new PopulatedObjectBean(pop));
        String uri;
        try
        {
            uri = "port://" + star.getURI().substring(7)+"/"+URLEncoder.encode(port.getName(), "utf-8");
        }
        catch (UnsupportedEncodingException e1)
        {
            throw new IllegalStateException(e1);
        } 
        port.setURI(uri);
        port.setRGB(StarExtraLogic.getStarColorRGB(star));
        double a = normProduction(pop.getAgriculturalProduction());
        double m = normProduction(pop.getMaterialProduction());
        double e = normProduction(pop.getEnergyProduction());
        if (a > 0)
            a /= (a + m + e);
        if (m > 0)
            m /= (a + m + e);
        if (e > 0)
            e /= (a + m + e);
        port.setAgricultural((int)(a*100));
        port.setMaterial((int)(m*100));
        port.setEnergy((int)(e*100));
        return port;
    }

    private static double normProduction(double p)
    {
        p = (p + 2)/4;
        if (p < 0)
            p = 0;
        if (p > 1)
            p = 1;
        return p;
    }
}
