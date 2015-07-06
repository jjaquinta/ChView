package jo.d4w.tools;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import jo.d4w.web.data.DockCargoBean;
import jo.d4w.web.data.OnDockBean;
import jo.d4w.web.data.PortBean;
import jo.d4w.web.data.PortsBean;
import jo.d4w.web.logic.DockLogic;
import jo.d4w.web.logic.PortLogic;
import jo.util.html.URIBuilder;

public class FindDeals
{
    private static double X = 27138;
    private static double Y = 20;
    private static double Z = 6;
    private static double RADIUS = 3.26*2;
    private static double RANGE = 3.26;
    
    public static void main(String[] args) throws UnsupportedEncodingException
    {        
        Map<PortBean,OnDockBean> available = new HashMap<PortBean, OnDockBean>();
        PortsBean ports = PortLogic.getPorts(X, Y, Z, RADIUS);
        for (PortBean port : ports.getPorts())
        {
            URIBuilder portURI = new URIBuilder(port.getURI());
            URIBuilder ondockURI = new URIBuilder();
            ondockURI.setScheme("ondock");
            ondockURI.setAuthority(portURI.getAuthority());
            ondockURI.setPath("/"+port.getName().replace(" ", "+"));
            //ondockURI.setQuery("date", "1100-001");
            OnDockBean ondock = DockLogic.getOnDock(ondockURI);
            available.put(port, ondock);
        }
        for (int i = 0; i < ports.getPorts().size(); i++)
        {
            PortBean p1 = ports.getPorts().get(i);
            for (int j = 0; j < ports.getPorts().size(); j++)
            {
                if (i == j)
                    continue;
                PortBean p2 = ports.getPorts().get(j);
                if (distance(p1, p2) > RANGE)
                    continue;
                System.out.println(p1.getName()+" -> "+p2.getName());
                System.out.println(p1.getURI()+" -> "+p2.getURI());
                for (DockCargoBean onDock : available.get(p1).getCargo())
                {
                    URIBuilder inHoldURI = new URIBuilder(onDock.getURI());
                    inHoldURI.setQuery("at", p2.getURI());
                    DockCargoBean inHold = DockLogic.getInHold(inHoldURI);
                    System.out.println("  "+onDock.getURI()+" -> "+inHold.getURI());
                    System.out.println("  "+onDock.getName()+" $"+onDock.getPurchasePrice()+" -> $"+inHold.getSalePrice());
                }
            }
        }
    }

    private static double distance(PortBean p1, PortBean p2)
    {
        double x = p1.getX() - p2.getX();
        double y = p1.getY() - p2.getY();
        double z = p1.getZ() - p2.getZ();
        return Math.sqrt(x*x + y*y + z*z);
    }
}
