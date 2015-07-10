package jo.d2k.data.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jo.d2k.data.data.SkyBean;
import jo.d2k.data.data.SkyConstellationBean;
import jo.d2k.data.data.SkyLinkBean;
import jo.d2k.data.data.StarBean;
import jo.util.geom2d.Line2DLogic;
import jo.util.geom2d.Point2D;
import jo.util.geom3d.Point3D;
import jo.util.utils.MathUtils;

public class SkyLogic
{
    public static List<SkyBean> getSky(Point3D center)
    {
        List<SkyBean> sky = new ArrayList<SkyBean>();
        getSky(center, sky);
        return sky;
    }
    public static void getSky(Point3D center, List<SkyBean> sky)
    {
        Set<Long> done = new HashSet<Long>();
        for (StarBean star : StarLogic.getAllWithin(center.x, center.y, center.z, 45))
        {
            addToSky(sky, star, center);
            if (!star.isGenerated())
                done.add(star.getOID());
        }
        for (int offset = 0; offset < 100000; offset += 1000)
        {
            List<StarBean> stars = StarLogic.getRange(offset, 1000);
            if (stars.size() == 0)
                break;
            for (StarBean star : stars)
                if (!done.contains(star.getOID()))
                    addToSky(sky, star, center);
        }
        // merge
        for (int i = 0; i < sky.size() - 1; i++)
        {
            SkyBean star1 = sky.get(i);
            for (int j = i + 1; j < sky.size(); j++)
            {
                SkyBean star2 = sky.get(j);
                double d = calcAngSep(star1, star2);
                if (d < 1) // within 1 degree of each other
                {
                    if (star1.getApparentMagnitude() < star2.getApparentMagnitude())
                    {
                        star1.setApparentMagnitude(combineAppMag(star1.getApparentMagnitude(), star2.getApparentMagnitude()));
                        sky.remove(j);
                        j--;
                    }
                    else
                    {
                        star2.setApparentMagnitude(combineAppMag(star1.getApparentMagnitude(), star2.getApparentMagnitude()));
                        sky.remove(i);
                        i--;
                        break;
                    }
                }
            }
        }
    }

    public static double calcAngSep(SkyBean star1, SkyBean star2)
    {
        double d = Math.abs(star1.getRA() - star2.getRA())/24*360 + Math.abs(star1.getDec() - star2.getDec());
        return d;
    }

    private static void addToSky(List<SkyBean> sky, StarBean star,
            Point3D center)
    {
        double dist = StarExtraLogic.distance(star, center.x, center.y, center.z);
        if (dist < .1)
            return;
        double dpc = dist/3.26;
        double apparentMagnitude = StarExtraLogic.getAppMag(star.getAbsMag(), dpc);
        for (StarBean child : star.getChildren())
        {
            double m2 = StarExtraLogic.getAppMag(child.getAbsMag(), dpc);
            apparentMagnitude = combineAppMag(apparentMagnitude, m2);
        }
        if (apparentMagnitude > 6)
            return;
        double q = Math.atan2(star.getY() - center.y, star.getX() - center.x);
        double ra = 12*q/Math.PI;
        if (ra < 0)
            ra += 24;
        double f = Math.acos((star.getZ() - center.z)/dist);
        double dec = 90 - (180*f/Math.PI);
        double brightness = Math.pow(2.512, 6 - apparentMagnitude);
        SkyBean s = new SkyBean();
        s.setStar(star);
        s.setApparentMagnitude(apparentMagnitude);
        s.setBrightness(brightness);
        s.setRA(ra);
        s.setDec(dec);
        s.setDistance(dist);
        sky.add(s);
    }
    
    private static double combineAppMag(double appMag1, double appMag2)
    {
        double appMag =-Math.log(Math.pow(2.512, -appMag1) + Math.pow(2.512, -appMag2))/Math.log(2.512);
        if (Double.isInfinite(appMag))
            System.out.println("Combining "+appMag1+" and "+appMag2+" to "+appMag);
        return appMag;
    }
    
    private final static double MAX_SEP = 20;
    private final static double MIN_SEP = 1;
    private final static int MAX_STARS = 88*12;
    private final static int MAX_LINKS = 88*24;
    
    public static List<SkyConstellationBean> getConstellations(List<SkyBean> allStars)
    {
        List<SkyBean> stars = getCandidateStars(allStars);
        List<SkyConstellationBean> constellations = new ArrayList<SkyConstellationBean>();
        Map<SkyBean,SkyConstellationBean> index = new HashMap<SkyBean, SkyConstellationBean>();
        
        List<SkyLinkBean> links = getAllLinks(stars);        
        triageLinks(links);
        constructConstellations(constellations, index, links);
        pruneConsteallations(constellations);
        for (SkyConstellationBean cons : constellations)
        {
            for (int i = 0; i < cons.getLinks().size() - 1; i++)
            {
                SkyLinkBean link1 = cons.getLinks().get(i);
                Point2D p1 = getPoint(link1.getStar1());
                Point2D p2 = getPoint(link1.getStar2());
                for (int j = 0; j < cons.getLinks().size() - 1; j++)
                {
                    SkyLinkBean link2 = cons.getLinks().get(j);
                    if ((link1.getStar1() == link2.getStar1()) || (link1.getStar1() == link2.getStar2())
                            || (link1.getStar2() == link2.getStar1()) || (link2.getStar1() == link2.getStar2()))
                        continue;
                    Point2D p3 = getPoint(link2.getStar1());
                    Point2D p4 = getPoint(link2.getStar2());
                    Point2D cross = Line2DLogic.intersectSegment(p1, p2, p3, p4);
                    if (cross != null)
                    {
                        if (link1.getSeparation() > link2.getSeparation())
                        {
                            cons.getLinks().remove(i);
                            i--;
                            break;
                        }
                        else
                        {
                            cons.getLinks().remove(j);
                            j--;
                        }
                    }
                }
            }
        }
        return constellations;
    }
    
    private static Point2D getPoint(SkyBean star)
    {
        double r = MathUtils.interpolate(Math.abs(star.getDec()), 90, 0, 0, 1.0);
        double a = star.getRA()/24*Math.PI*2;
        double x = Math.sin(a)*r;
        double y = Math.cos(a)*r;
        Point2D p = new Point2D(x, y);
        return p;
    }

    public static void pruneConsteallations(
            List<SkyConstellationBean> constellations)
    {
        for (Iterator<SkyConstellationBean> i = constellations.iterator(); i.hasNext(); )
            if (i.next().getLinks().size() <= 1)
                i.remove();
    }

    public static void constructConstellations(
            List<SkyConstellationBean> constellations,
            Map<SkyBean, SkyConstellationBean> index, List<SkyLinkBean> links)
    {
        for (SkyLinkBean link : links)
        {
            if (index.containsKey(link.getStar1()))
                if (index.containsKey(link.getStar2()))
                {
                    if (index.get(link.getStar1()) == index.get(link.getStar2()))
                        add(index.get(link.getStar1()), link, index);
                    else
                    {
                        merge(index.get(link.getStar1()), index.get(link.getStar2()), index, constellations);
                        add(index.get(link.getStar1()), link, index);
                    }
                }
                else
                    add(index.get(link.getStar1()), link, index);
            else
                if (index.containsKey(link.getStar2()))
                    add(index.get(link.getStar2()), link, index);
                else
                {
                    SkyConstellationBean cons = new SkyConstellationBean();
                    constellations.add(cons);
                    add(cons, link, index);
                }
        }
    }

    public static void triageLinks(List<SkyLinkBean> links)
    {
        Collections.sort(links, new Comparator<SkyLinkBean>() {
            @Override
            public int compare(SkyLinkBean o1, SkyLinkBean o2)
            {
                return (int)Math.signum(o2.getStrength() - o1.getStrength());
            }
        });
        System.out.println("Strength(0)="+links.get(0).getStrength()+", Strength(n)="+links.get(links.size()-1).getStrength());
        System.out.println("Links before pruning:"+links.size());
        while (links.size() > MAX_LINKS)
            links.remove(MAX_LINKS);
    }

    public static List<SkyLinkBean> getAllLinks(List<SkyBean> stars)
    {
        List<SkyLinkBean> links = new ArrayList<SkyLinkBean>();
        for (int i = 0; i < stars.size() - 1; i++)
        {
            SkyBean star1 = stars.get(i);
            double m1 = 6 - star1.getApparentMagnitude();
            for (int j = i + 1; j < stars.size(); j++)
            {
                SkyBean star2 = stars.get(j);
                double separation = calcAngSep(star1, star2);
                if (separation > MAX_SEP)
                    continue;
                if (separation < MIN_SEP)
                    continue;
                double m2 = 6 - star2.getApparentMagnitude();
                double strength = (m1 + m2)/(separation*separation);
                SkyLinkBean link = new SkyLinkBean();
                link.setStar1(star1);
                link.setStar2(star2);
                link.setSeparation(separation);        
                link.setStrength(strength);
                links.add(link);
            }
        }
        return links;
    }

    private static void merge(SkyConstellationBean cons1,
            SkyConstellationBean cons2,
            Map<SkyBean, SkyConstellationBean> index,
            List<SkyConstellationBean> constellations)
    {
        cons1.getLinks().addAll(cons2.getLinks());
        cons1.getStars().addAll(cons2.getStars());
        for (SkyBean star : cons1.getStars())
            index.put(star, cons1);
        constellations.remove(cons2);
    }

    private static void add(SkyConstellationBean cons,
            SkyLinkBean link, Map<SkyBean, SkyConstellationBean> index)
    {
        cons.getLinks().add(link);
        cons.getStars().add(link.getStar1());
        cons.getStars().add(link.getStar2());
        index.put(link.getStar1(), cons);
        index.put(link.getStar2(), cons);
    }

    public static List<SkyBean> getCandidateStars(List<SkyBean> allStars)
    {
        List<SkyBean> stars = new ArrayList<SkyBean>();
        stars.addAll(allStars);
        Collections.sort(stars, new Comparator<SkyBean>() {
            @Override
            public int compare(SkyBean o1, SkyBean o2)
            {
                return (int)Math.signum(o1.getApparentMagnitude() - o2.getApparentMagnitude());
            }
        });
        System.out.println("Stars before pruning:"+stars.size());
        while (stars.size() > MAX_STARS)
            stars.remove(MAX_STARS);
        return stars;
    }
}