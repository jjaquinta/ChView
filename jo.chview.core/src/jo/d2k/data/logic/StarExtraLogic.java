package jo.d2k.data.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarLink;
import jo.util.geom3d.Point3D;
import jo.util.logic.GroupMap;
import jo.util.utils.MathUtils;
import jo.util.utils.obj.SetUtils;
import jo.util.utils.obj.StringUtils;

public class StarExtraLogic
{
    private static int[][] STAR_COLORS = {
        { 155, 176, 255 },
        { 187, 204, 255 },
        { 226, 231, 255 },
        { 255, 248, 248 },
        { 255, 240, 227 },
        { 255, 152, 51 },
        { 210, 0, 51 },
        { 204, 0, 153 },
        { 153, 102, 51 },
        { 102, 51, 0 },
    };
    
    public static int[] getStarColorInts(StarBean star)
    {
        int[] color = new int[3];
        int s = StarLogic.SPECTRA.indexOf(Character.toUpperCase(star.getSpectra().charAt(0)));
        int idx = star.getSpectra().charAt(1) - '0';
        color[0] = (int)MathUtils.interpolate(idx, 0, 9, STAR_COLORS[s][0], STAR_COLORS[s+1][0]);
        color[1] = (int)MathUtils.interpolate(idx, 0, 9, STAR_COLORS[s][1], STAR_COLORS[s+1][1]);
        color[2] = (int)MathUtils.interpolate(idx, 0, 9, STAR_COLORS[s][2], STAR_COLORS[s+1][2]);
        return color;
    }
    
    public static String getStarColorRGB(StarBean star)
    {
        int[] color = getStarColorInts(star);
        StringBuffer sb = new StringBuffer();
        sb.append(StringUtils.prefix(Integer.toHexString(color[0]), '0', 2));
        sb.append(StringUtils.prefix(Integer.toHexString(color[1]), '0', 2));
        sb.append(StringUtils.prefix(Integer.toHexString(color[2]), '0', 2));
        return sb.toString();
    }
    
    public static double distance(StarBean star1, StarBean star2)
    {
        if (star2 == null)
            return 0;
        return distance(star1, star2.getX(), star2.getY(), star2.getZ());
    }
    
    public static double distance(StarBean star1, double x, double y, double z)
    {
        if (star1 == null)
            return 0;
        double dx = star1.getX() - x;
        double dy = star1.getY() - y;
        double dz = star1.getZ() - z;
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    
    public static int getSpectra(StarBean star)
    {
        return getSpectra(star.getSpectra());
    }
    
    public static int getSpectra(String spectra)
    {
        return StarLogic.SPECTRA.indexOf(Character.toUpperCase(spectra.charAt(0)));
    }
    
    public static int getIndex(StarBean star)
    {
        return getIndex(star.getSpectra());
    }
    
    public static int getIndex(String spectra)
    {
        if (spectra.length() < 2)
            return -1;
        return spectra.charAt(1) - '0';
    }
    
    public static int getClass(StarBean star)
    {
        return getClass(star.getSpectra());
    }
    
    public static int getClass(String spectra)
    {
        if (spectra.endsWith("IV"))
            return 4;
        if (spectra.endsWith("V"))
            return 5;
        if (spectra.endsWith("III"))
            return 3;
        if (spectra.endsWith("II"))
            return 2;
        if (spectra.endsWith("I"))
            return 1;
        return 0;
    }
    
    public static final double TABLE_STELLAR_MASS[][] =
    {   // O0    O5    B0    B5    A0    A5    F0    F5     G0    G5    K0    K5    M0
        { 60.0, 30.0, 18.0, 15.0, 13.0, 12.0, 12.0, 13.0,  14.0, 18.0, 20.0, 25.0, 30.0 }, // IA
        { 50.0, 25.0, 16.0, 13.0, 12.0, 10.0, 10.0, 12.0,  13.0, 16.0, 16.0, 20.0, 25.0 }, // IB
        { 30.0, 20.0, 14.0, 11.0, 10.0,  8.1,  8.1, 10.0,  11.0, 14.0, 14.0, 16.0, 18.0 }, // II
        { 25.0, 15.0, 12.0,  9.0,  8.0,  5.0,  2.5,  3.2,   4.0,  5.0,  6.3,  7.4,  9.2 }, // III
        { 20.0, 10.0,  6.0,  4.0,  2.5,  2.0,  1.75, 2.0,   2.3,  2.3,  2.3,  2.3,  2.3 }, // IV
        { 18.0,  6.5,  3.2,  2.1,  1.7,  1.3,  1.04,  0.94, 0.825,0.570,0.489,0.331,0.215 },//V
        {  0.8,  0.8,  0.8,  0.8,  0.8,  0.8,  0.6,   0.528,0.430,0.330,0.154,0.104,0.058 },//VI
        {  0.26, 0.26, 0.36, 0.36, 0.42, 0.42, 0.63,  0.63, 0.83, 0.83, 1.11, 1.11, 1.11 } // D
    };

    public static final double TABLE_STELLAR_LUMINOSITY[][] =
    {   //    O0      O5      B0     B5     A0     A5     F0     F5     G0        G5      K0      K5      M0
        { 560000, 204000, 107000, 81000, 61000, 51000, 67000, 89000, 97000,   107000, 117000, 129000, 141000 },       //IA
        { 270000,  46700,  15000, 11700,  7400,  5100,  6100,  8100, 11700,    20400,  46000,  89000, 117000 },       //IB
        { 170000,  18600,   2200,   850,   600,   510,   560,   740,   890,     2450,   4600,  14900,  16200 },       //II
        { 107000,   6700,    280,    90,    53,    43,    50,    75,    95,      320,    470,   2280,   2690 },       //III
        {  81000,   2000,    156,    37,    19,    12,     6.5,   4.9,   4.67,     4.67,   4.67,   4.67,   4.67 },    //IV
        {  56000,   1400,     90,    16,     8.1,   3.5,   1.21,   .67,   .42,      .08,    .04,    .007,   .001 },   //V
        {       .977,   .977,   .977,  .977,    .977,  .977,   .322,  .186,  .117,  .025,   .011,   .002,   .00006 }, //VI
        {       .046,   .046,   .005,  .005,    .0003, .0003,  .00006,.00006,.00004,.00004, .00003, .00003, .00003 }, // D
    };
    public static final double TABLE_STELLAR_RADIUS[][] = // in MKm
    {   //O0    O5    B0    B5    A0    A5    F0    F5    G0    G5    K0    K5    M0
        { 52,   75,  135,  149,  174,  204,  298,  454,  654, 1010, 1467, 3020, 3499 },        //IA
        { 30,   35,   50,   55,   59,   60,   84,  128,  216,  392,  857, 2073, 2876 },        //IB
        { 22,   20,   18,   14,   16,   18,   25,   37,   54,  124,  237,  712,  931 },        //II
        { 16,   10,    6.2,  4.6,  4.7,  5.2,  7.1, 11,   16,   42,   63,  228,  360 },        //III
        { 13,    5.3,  4.5,  2.7,  2.7,  2.6,  2.5,  2.8,  3.3,  3.3,  3.3,  3.3,  3.3 },      //IV
        { 10,    4.4,  3.2,  1.8,  1.7,  1.4,  1.03,  .91,  .908, .566, .549, .358, .201 },    //V
        {  1.14, 1.14, 1.14, 1.14, 1.14, 1.14, 1.02,  .55,  .4,   .308, .256, .104, .053 },    //VI
        {   .018, .018, .017, .017, .013, .013, .012, .012, .009, .009, .006, .006, .006 }     //D
    };
    
    private static double makeOff(String spectra)
    {
        int s = getSpectra(spectra);
        int idx = getIndex(spectra);
        if (s == 0)
            return idx/10.0;
        if (s == 1)
            return 1 + idx/10.0;
        double o = (s - 2)*2 + 2;
        o += idx/5.0;
        return o;
    }

    public static double calcMassFromTypeAndClass(String spectra)
    {
        double off = makeOff(spectra);
        int c = getClass(spectra);
        return MathUtils.interpolate(off, TABLE_STELLAR_MASS[c]);
    }

    public static double calcLuminosityFromTypeAndClass(String spectra)
    {
        double off = makeOff(spectra);
        int c = getClass(spectra);
        double lumen = MathUtils.interpolate(off, TABLE_STELLAR_LUMINOSITY[c]);
        if (lumen < 0)
            lumen = TABLE_STELLAR_LUMINOSITY[c][TABLE_STELLAR_LUMINOSITY[c].length - 1];
        return lumen;
    }

    public static double calcRadiusFromTypeAndClass(String spectra)
    {
        double off = makeOff(spectra);
        int c = getClass(spectra);
        double radiusMkm = MathUtils.interpolate(off, TABLE_STELLAR_RADIUS[c]);
        return radiusMkm*1000000; // convert to km
    }

    public static StarBean findCompanion(StarBean star, List<StarBean> stars)
    {
        StarBean companion = null;
        if (star.getParent() != 0)
        {
            for (StarBean s : stars)
                if (s.getOID() == star.getParent())
                {
                    companion = s;
                    break;
                }
        }
        else
        {
            double dist = 0;
            for (StarBean s : stars)
            {
                if (s == star)
                    continue;
                if (s.getParent() != star.getOID())
                    continue;
                double d = StarExtraLogic.distance(star, s);
                if ((companion == null) || (d < dist))
                {
                    companion = s;
                    dist = d;
                }
            }
        }
        return companion;
    }

    public static List<StarLink> findLinks(List<StarBean> stars, double linkSize)
    {
        List<StarLink> links = new ArrayList<StarLink>();
        for (int i = 0; i < stars.size() - 1; i++)
        {
            StarBean star1 = stars.get(i);
            if (star1.getParent() > 0)
                continue;
            for (int j = i + 1; j < stars.size(); j++)
            {
                StarBean star2 = stars.get(j);
                if (star2.getParent() > 0)
                    continue;
                double d = StarExtraLogic.distance(star1, star2);
                if (d > linkSize)
                    continue;
                StarLink link = new StarLink();
                link.setStar1(star1);
                link.setStar2(star2);
                link.setDistance(d);
                links.add(link);
            }
        }
        return links;
    }
    
    public static void sortLinks(List<StarLink> links)
    {
        Collections.sort(links, new Comparator<StarLink>() {
            @Override
            public int compare(StarLink object1, StarLink object2)
            {
                return (int)Math.signum(object1.getDistance() - object2.getDistance());
            }            
        });
    }
    
    public static void pruneLinks(List<StarLink> links, int limit, double minimum)
    {
        GroupMap<StarBean, StarBean> index = new GroupMap<StarBean, StarBean>();
        sortLinks(links);
        while ((links.size() > 0) && (links.get(0).getDistance() < minimum))
            links.remove(0);
        for (StarLink link : links)
        {
            index.add(link.getStar1(), link.getStar2());
            index.add(link.getStar2(), link.getStar1());
        }
        Set<StarBean> s1 = new HashSet<StarBean>();
        Set<StarBean> s2 = new HashSet<StarBean>();
        for (Iterator<StarLink> i = links.iterator(); i.hasNext(); )
        {
            StarLink link = i.next();
            s1.clear();
            s1.addAll(index.get(link.getStar1()));
            s2.clear();
            s2.addAll(index.get(link.getStar2()));
            Set<?> both = SetUtils.intersection(s1, s2);
            if (both.size() >= limit)
            {
                index.remove(link.getStar1(), link.getStar2());
                index.remove(link.getStar2(), link.getStar1());
                i.remove();
            }
        }
    }

    public static Point3D getLocation(StarBean star)
    {
        return new Point3D(star.getX(), star.getY(), star.getZ());
    }

    public static double getAppMag(double absMag, double dpc)
    {
        double appMag = absMag - 5*(1 - Math.log10(dpc));
        if (!Double.isFinite(appMag) || (appMag < -8) || (appMag > 32))
            System.out.println("AbsMag="+absMag+", dist="+dpc+", appMag="+appMag);
        return appMag;
    }
}
