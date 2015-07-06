package jo.d4w.tools;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jo.d4w.web.data.DockCargoBean;
import jo.d4w.web.data.OnDockBean;
import jo.d4w.web.data.PortBean;
import jo.d4w.web.data.PortsBean;
import jo.d4w.web.logic.DockLogic;
import jo.d4w.web.logic.PortLogic;
import jo.util.html.URIBuilder;

public class FindCargos
{
    private static double X = 27138;
    private static double Y = 20;
    private static double Z = 6;
    private static double RADIUS = 3.26*10;
    private static String prefix = "<word>";
    private static String suffix = "</word>";
    
    public static void main(String[] args) throws UnsupportedEncodingException
    {        
        Set<String> names = new HashSet<String>();
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
            for (DockCargoBean cargo : ondock.getCargo())
                names.add(cargo.getName());
        }
        String[] n = names.toArray(new String[0]);
        Arrays.sort(n);
        for (String name : n)
            System.out.println(prefix+name+suffix);
    }
}
