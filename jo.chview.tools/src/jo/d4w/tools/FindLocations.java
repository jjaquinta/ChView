package jo.d4w.tools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jo.d4w.web.data.PortBean;
import jo.d4w.web.data.PortsBean;
import jo.d4w.web.logic.PortLogic;

public class FindLocations
{
    private static double X = 27138;
    private static double Y = 20;
    private static double Z = 6;
    private static double RADIUS = 3.26*10;
    private static String prefix = "<word>";
    private static String suffix = "</word>";
    
    public static void main(String[] args)
    {        
        Set<String> names = new HashSet<String>();
        PortsBean ports = PortLogic.getPorts(X, Y, Z, RADIUS);
        for (PortBean port : ports.getPorts())
            names.add(port.getName());
        String[] n = names.toArray(new String[0]);
        Arrays.sort(n);
        for (String name : n)
            System.out.println(prefix+name+suffix);
    }
}
