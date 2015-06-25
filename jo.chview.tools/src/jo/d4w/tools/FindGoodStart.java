package jo.d4w.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jo.d4w.web.data.PortBean;
import jo.d4w.web.data.PortsBean;
import jo.d4w.web.logic.PortLogic;

public class FindGoodStart
{
    private static final double X = 27138;
    private static final double Y = 20;
    private static final double Z = 6;
    private static final double JUMP = 3.26;
    private static final int JUMP_NUM = 3;
    private static final double RADIUS = JUMP*10;
    
    
    public static void main(String[] args)
    {        
        PortBean    best = null;
        int         bestVal = 0;
        
        PortsBean ports = PortLogic.getPorts(X, Y, Z, RADIUS);
        for (PortBean port : ports.getPorts())
        {
            int val = calcConnectivity(port, ports);
            if ((best == null) || (val > bestVal))
            {
                best = port;
                bestVal = val;
            }
        }
        System.out.println("Best is "+best.getURI());
        System.out.println("  connectivity="+bestVal);
    }
    
    private static int calcConnectivity(PortBean port, PortsBean ports)
    {
        Set<PortBean> from = new HashSet<PortBean>();
        Set<PortBean> to = new HashSet<PortBean>();
        from.add(port);
        for (int jump = 0; jump < JUMP_NUM; jump++)
        {
            to.clear();
            for (PortBean f : from)
                to.addAll(findAllWithin(ports, f, JUMP));
            from.clear();
            from.addAll(to);
        }
        return from.size();
    }

    private static List<PortBean> findAllWithin(PortsBean ports, PortBean port, double dist)
    {
        List<PortBean> within = new ArrayList<PortBean>();
        for (PortBean p : ports.getPorts())
        {
            double d = distance(p, port);
            //System.out.println(port.getName()+" -> "+p.getName()+" d="+d);
            if (d < dist)
                within.add(p);
        }
        return within;
    }

    private static double distance(PortBean p1, PortBean p2)
    {
        double x = p1.getX() - p2.getX();
        double y = p1.getY() - p2.getY();
        double z = p1.getZ() - p2.getZ();
        return Math.sqrt(x*x + y*y + z*z);
    }
}
