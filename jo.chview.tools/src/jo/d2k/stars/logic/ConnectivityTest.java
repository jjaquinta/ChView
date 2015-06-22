package jo.d2k.stars.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.StarLogic;

public class ConnectivityTest
{
    private static final double RADIUS = 50;

    private static ConnectivityMetric mMetric = ConnectivityMetric.getInstanceDistance();
    //private static ConnectivityMetric mMetric = ConnectivityMetric.getInstanceGravitation();
    //private static ConnectivityMetric mMetric = ConnectivityMetric.getInstanceDistanceSquared();
    //private static ConnectivityMetric mMetric = ConnectivityMetric.getInstanceDistanceSqrt();

    private static double mLow;
    private static double mHigh;
    private static Map<Long, Map<Long,Double>>  mDistances = new HashMap<Long, Map<Long,Double>>();

    public static void main(String[] argv)
    {
        System.out.println("Radius "+RADIUS);
        List<StarBean> stars = StarLogic.getAllWithin(0, 0, 0, RADIUS);
        System.out.println("Stars "+stars.size());
        StarBean start = null;
        for (StarBean s : stars)
        {
            if (s.getName().equals("Sol"))
            {
                start = s;
                break;
            }
        }
        calcBounds(stars);
        double v;
        for (int target = 5; target <= 95; target += 5)
        {
            v = calcForTarget(start, stars, target);
            System.out.println(target+"% = "+v);
        }
    }

    private static double calcForTarget(StarBean start, List<StarBean> stars, int target)
    {
        double low = mLow;
        double high = mHigh;
        for (int i = 0; i < 100; i++)
        {
            double mid = (low + high)/2;
            int midV = countConnectedPC(start, stars, mid);
            if (midV == target)
                break;
            else if (midV > target)
                high = mid;
            else 
                low = mid;
        }
        return (low + high)/2;
    }

    private static int countConnectedPC(StarBean start, List<StarBean> stars, double v)
    {
        int connected = countConnected(start, stars, v, null); 
        double pc = (double)connected/(double)stars.size();
        return (int)(pc*100);
    }
    
    private static void calcBounds(List<StarBean> stars)
    {
        List<Double> dists = new ArrayList<Double>();
        for (int i = 0; i < stars.size() - 1; i++)
        {
            StarBean star1 = stars.get(i);
            for (int j = i + 1; j < stars.size(); j++)
            {
                StarBean star2 = stars.get(j);
                double d = mMetric.distance(star1, star2);
                dists.add(d);
                setDistance(star1, star2, d);
            }
        }
        Collections.sort(dists);
        mLow = dists.get(0); 
        mHigh = dists.get(dists.size() - 1);
    }

    public static int countConnected(StarBean center, List<StarBean> stars, double v, Set<Long> done)
    {
        if (done == null)
            done = new HashSet<Long>();
        List<StarBean> todo = new ArrayList<StarBean>();
        for (StarBean star : stars)
        {
            if (star.getOID() == center.getOID())
                continue;
            if (done.contains(star.getOID()))
                continue;
            double d = getDistance(center, star);
            //if (mMetric.isConnected(center, star, v))
            if (d <= v)
            {
                done.add(star.getOID());
                todo.add(star);
            }
        }
        for (StarBean star : todo)
            countConnected(star, stars, v, done);
        return done.size();
    }
    
    private static final void setDistance(StarBean star1, StarBean star2, double d)
    {
        long o1 = Math.max(star1.getOID(), star2.getOID());
        long o2 = Math.min(star1.getOID(), star2.getOID());
        Map<Long,Double> d2 = mDistances.get(o1);
        if (d2 == null)
        {
            d2 = new HashMap<Long, Double>();
            mDistances.put(o1, d2);
        }
        d2.put(o2, d);
    }
    
    private static final double getDistance(StarBean star1, StarBean star2)
    {
        long o1 = Math.max(star1.getOID(), star2.getOID());
        long o2 = Math.min(star1.getOID(), star2.getOID());
        Map<Long,Double> d2 = mDistances.get(o1);
        return d2.get(o2);
    }
}
