package jo.d2k.data.logic.stargen.logic;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jo.d2k.data.logic.stargen.data.BodyBean;
import jo.d2k.data.logic.stargen.data.SolidBodyBean;
import jo.d2k.data.logic.stargen.data.SunBean;

public class UtilsLogic
{
    /*----------------------------------------------------------------------*/
    /*  This function returns a random real number between the specified    */
    /* inner and outer bounds.                                              */
    /*----------------------------------------------------------------------*/

    public static double random_number(double inner, double outer)
    {
        double range = outer - inner;
        return StargenLogic.rnd.nextDouble() * range + inner;
    }


    /*----------------------------------------------------------------------*/
    /*   This function returns a value within a certain variation of the    */
    /*   exact value given it in 'value'.                                   */
    /*----------------------------------------------------------------------*/

    public static double about(double value, double variation)
    {
        return(value + (value * random_number(-variation,variation)));
    }

    public static double random_eccentricity()
    {
        double e = 1.0 - Math.pow(random_number(0.0, 1.0), ConstLogic.ECCENTRICITY_COEFF);
        
        if (e > .99)    // Note that this coresponds to a random
            e = .99;    // number less than 10E-26
                        // It happens with GNU C for -S254 -W27
        return(e);
    }

    public static SolidBodyBean getPrevPlanet(List<SolidBodyBean> planets, SolidBodyBean planet)
    {
        for (int i = 1; i < planets.size(); i++)
            if (planets.get(i) == planet)
                return planets.get(i-1);
        return null;
    }

    public static SolidBodyBean getLastPlanet(List<SolidBodyBean> planets)
    {
        if (planets.size() > 0)
            return planets.get(planets.size() - 1);
        return null;
    }


    public static double summateMasses(List<BodyBean> planets)
    {
        double mass = 0.0;
        for (BodyBean planet : planets)
            mass += planet.getMass();
        return mass;
    }
    
    public static void sortByA(List<SolidBodyBean> planets)
    {
        Collections.sort(planets, new Comparator<SolidBodyBean>() {
            @Override
            public int compare(SolidBodyBean object1, SolidBodyBean object2)
            {
                return (int)Math.signum(object1.getA() - object2.getA());
            }
        });
    }


    public static void stitch(BodyBean parent)
    {
        int idx = 0;
        for (BodyBean child : parent.getChildren())
        {
            child.setParent(parent);
            child.setURI(parent.getURI()+"/"+idx);
            stitch(child);
            idx++;
        }
    }
    
    public static int depth(BodyBean p)
    {
        int depth = 0;
        while (p != null)
        {
            depth++;
            p = p.getParent();
        }
        return depth;
    }
    
    public static int maxDepth(BodyBean p)
    {
        int max = 1;
        for (BodyBean b : p.getChildren())
            max = Math.max(max, maxDepth(b) + 1);
        return max;
    }
    
    public static int maxDepth(List<BodyBean> p)
    {
        int max = 1;
        for (BodyBean b : p)
            max = Math.max(max, maxDepth(b) + 1);
        return max;
    }


    public static BodyBean find(BodyBean body, String uri)
    {
        if (uri.equals(body.getURI()))
            return body;
        for (BodyBean child : body.getChildren())
        {
            BodyBean hit = find(child, uri);
            if (hit != null)
                return hit;
        }
        return null;
    }


    public static double getDistanceToSun(BodyBean b)
    {
        if (b.getParent() instanceof SunBean)
            return ((SolidBodyBean)b).getA();
        else
            return getDistanceToSun(b.getParent());
    }
}
