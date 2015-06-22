package jo.d2k.stars.logic;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.StarExtraLogic;

public abstract class ConnectivityMetric
{
    public abstract double distance(StarBean star1, StarBean star2);

    public boolean isConnected(StarBean star1, StarBean star2, double val)
    {
        return distance(star1, star2) <= val;
    }
    
 // 50% = 7.658976411571846
 // 60% = 7.658976411571846
 // 70% = 7.658976411571846
 // 80% = 7.866528849405027
 // 90% = 8.410957602600835
 // 95% = 9.468969911320865
    public static ConnectivityMetric getInstanceDistance()
    {
        ConnectivityMetric m = new ConnectivityMetric() {
            @Override
            public double distance(StarBean star1, StarBean star2)
            {                
                return StarExtraLogic.distance(star1, star2);
            }
        };
        return m;
    }
    
    //50% = 58.659919673013945
    //60% = 58.659919673013945
    //70% = 58.659919673013945
    //80% = 62.77830056058522
    //90% = 70.7442077927488
    //95% = 89.3383507977559
    public static ConnectivityMetric getInstanceDistanceSquared()
    {
        ConnectivityMetric m = new ConnectivityMetric() {
            @Override
            public double distance(StarBean star1, StarBean star2)
            {                
                double d = StarExtraLogic.distance(star1, star2);
                return d*d;
            }
        };
        return m;
    }
    
    //50% = 7.658976411571846
    //60% = 7.658976411571846
    //70% = 7.658976411571846
    //80% = 7.866528849405027
    //90% = 8.410957602600835
    //95% = 9.468969911320865
    public static ConnectivityMetric getInstanceDistanceSqrt()
    {
        ConnectivityMetric m = new ConnectivityMetric() {
            @Override
            public double distance(StarBean star1, StarBean star2)
            {                
                double d = StarExtraLogic.distance(star1, star2);
                return Math.sqrt(d*d);
            }
        };
        return m;
    }
    
    //50% = 3.1120536838471016E-4
    //60% = 3.1120536838471016E-4
    //70% = 3.1120536838471016E-4
    //80% = 8.598141269813756E-4
    //90% = 0.0015571884702694943
    //95% = 0.0025764278950752063
    public static ConnectivityMetric getInstanceGravitation()
    {
        ConnectivityMetric m = new ConnectivityMetric() {
            @Override
            public double distance(StarBean star1, StarBean star2)
            {                
                double m1 = StarExtraLogic.calcMassFromTypeAndClass(star1.getSpectra());
                double m2 = StarExtraLogic.calcMassFromTypeAndClass(star2.getSpectra());
                double dist = StarExtraLogic.distance(star1, star2);
                if (dist < .2)
                    dist = .2;
                double v = (m1 + m2)/(dist*dist);
                return v;
            }
        };
        return m;
    }

}
