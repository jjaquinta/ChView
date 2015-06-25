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
            return 0;
        return spectra.charAt(1) - '0';
    }
    
    public static int getClassOff(String spectra)
    {
        if (spectra.endsWith("IV"))
            return 4;
        if (spectra.endsWith("V"))
            return 5;
        if (spectra.endsWith("VI"))
            return 6;
        if (spectra.endsWith("D"))
            return 7;
        if (spectra.endsWith("III"))
            return 3;
        if (spectra.endsWith("II"))
            return 2;
        if (spectra.endsWith("IB"))
            return 1;
        if (spectra.endsWith("IA"))
            return 0;
        if (spectra.endsWith("I"))
            return 1;
        return 0;
    }
    
    private static int getSpectraOff(String spectra)
    {
        return StarLogic.SPECTRA.indexOf(Character.toUpperCase(spectra.charAt(0)));       
    }
    
    private static int getDecimalOff(String spectra)
    {
        if (spectra.length() < 2)
            return 5;
        int off = spectra.charAt(1) - '0';
        if (off < 0)
            off = 0;
        else if (off > 9)
            off = 9;
        return off;       
    }

    /*
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
    */
    
    private static double[][] STAR_LUMENOSITY = new double[8][100];
    private static double[][] STAR_TEMPERATURE = new double[8][100];

    private static double SUN_LUMEN = 0.73;
    private static double SUN_TEMP = 5660;
    //private static double SUN_MASS = 1.0;
    private static double SUN_RADIUS = 695500000.0; // meters
    
    private static void addStats(String clazz, String spectra, double temp, Double absmag, double lumen)
    {
        int classOff = getClassOff(clazz);
        int specOff = getSpectraOff(spectra)*10 + getDecimalOff(spectra);
        STAR_LUMENOSITY[classOff][specOff] = lumen;
        if (temp > 0)
            STAR_TEMPERATURE[classOff][specOff] = temp;
    }
    
    private static void normalizeTables()
    {
        normalizeTable(STAR_LUMENOSITY);
        normalizeTable(STAR_TEMPERATURE);
    }
    
    private static void normalizeTable(double[][] table)
    {
        for (int clOff = 0; clOff < table.length; clOff++)
        {
//            System.out.print("Normalizing: ");
//            for (int spOff = 0; spOff < table[clOff].length; spOff++)
//                System.out.print(table[clOff][spOff]+"   ");
//            System.out.println();
            int start = -1;
            int end = -1;
            for (int spOff = 0; spOff < table[clOff].length; spOff++)
            {
                if (table[clOff][spOff] <= 0)
                {
                    if (start < 0)
                    {
                        start = spOff;
                        end = spOff;
                    }
                    else
                        end = spOff;
                }
                else
                {
                    if (start >= 0)
                    {
                        normalizeSpan(table[clOff], start, end, (clOff == 0) ? table[0] : table[clOff-1]);
                        start = -1;
                        end = -1;
                    }
                }
            }
            if (start >= 0)
                normalizeSpan(table[clOff], start, end, (clOff == 0) ? table[0] : table[clOff-1]);
        }
    }
    
    private static void normalizeSpan(double[] data, int start, int end, double[] backup)
    {
//        System.out.println("Normalizing span "+start+" to "+end);
        if (start == 0)
        {
            if (end == data.length - 1)
                data = backup;
            else
            {
                double v = data[end + 1];
                for (int i = start; i <= end; i++)
                    data[i] = v;
            }
        }
        else if (end == data.length - 1)
        {
            double v = data[start - 1];
            for (int i = start; i <= end; i++)
                data[i] = v;
            return;
        }
        else
            for (int i = start; i <= end; i++)
                data[i] = MathUtils.interpolate(i, start - 1, end + 1, data[start - 1], data[end - 1]);
//        System.out.print("Result: ");
//        for (int spOff = start; spOff <= end; spOff++)
//            System.out.print(data[spOff]+"   ");
//        System.out.println();
    }

    static
    {
        // from Traveller
        String[] classAxis = { "IA", "IB", "II", "III", "IV", "V", "VI", "D" };
        String[] spectraAxis = { "O0", "O5", "B0", "B5", "A0", "A5", "F0", "F5", "G0", "G5", "K0", "K5", "M0" };
        double TABLE_STELLAR_LUMINOSITY[][] =
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
        for (int cl = 0; cl < classAxis.length; cl++)
            for (int sp = 0; sp < spectraAxis.length; sp++)
                addStats(classAxis[cl], spectraAxis[sp], -1, null, TABLE_STELLAR_LUMINOSITY[cl][sp]);
        //http://www.uni.edu/morgans/astro/course/Notes/section2/spectraltemps.html
        addStats("V", "O5", 54000, -4.5, 200000);
        addStats("V", "O6", 45000, -4.0, 140000);
        addStats("V", "O7", 43300, -3.9, 120000);
        addStats("V", "O8", 40600, -3.8, 80000);
        addStats("V", "O9", 37800, -3.6, 55000);
        addStats("V", "B0", 29200, -3.3, 24000);
        addStats("V", "B1", 23000, -2.3, 5550);
        addStats("V", "B2", 21000, -1.9, 3190);
        addStats("V", "B3", 17600, -1.1, 1060);
        addStats("V", "B5", 15200, -0.4, 380);
        addStats("V", "B6", 14300, 0.0, 240);
        addStats("V", "B7", 13500, 0.3, 140);
        addStats("V", "B8", 12300, 0.7, 73);
        addStats("V", "B9", 11400, 1.1, 42);
        addStats("V", "A0", 9600, 1.5, 24);
        addStats("V", "A1", 9330, 1.7, 20);
        addStats("V", "A2", 9040, 1.8, 17);
        addStats("V", "A3", 8750, 2.0, 14);
        addStats("V", "A4", 8480, 2.1, 12);
        addStats("V", "A5", 8310, 2.2, 11);
        addStats("V", "A7", 7920, 2.4, 8.8);
        addStats("V", "F0", 7350, 3.0, 5.1);
        addStats("V", "F2", 7050, 3.3, 3.8);
        addStats("V", "F3", 6850, 3.5, 3.2);
        addStats("V", "F5", 6700, 3.7, 2.7);
        addStats("V", "F6", 6550, 4.0, 2.0);
        addStats("V", "F7", 6400, 4.3, 1.5);
        addStats("V", "F8", 6300, 4.4, 1.4);
        addStats("V", "G0", 6050, 4.7, 1.2);
        addStats("V", "G1", 5930, 4.9, 1.1);
        addStats("V", "G2", 5800, 5.0, 1);
        addStats("V", "G5", 5660, 5.2, 0.73);
        addStats("V", "G8", 5440, 2.6, 0.51);
        addStats("V", "K0", 5240, 6.0, 0.38);
        addStats("V", "K1", 5110, 6.2, 0.32);
        addStats("V", "K2", 4960, 6.4, 0.29);
        addStats("V", "K3", 4800, 6.7, 0.24);
        addStats("V", "K4", 4600, 7.1, 0.18);
        addStats("V", "K5", 4400, 7.4, 0.15);
        addStats("V", "K7", 4000, 8.1, 0.11);
        addStats("V", "M0", 3750, 8.7, 0.080);
        addStats("V", "M1", 3700, 9.4, 0.055);
        addStats("V", "M2", 3600, 10.1, 0.035);
        addStats("V", "M3", 3500, 10.7, 0.027);
        addStats("V", "M4", 3400, 11.2, 0.022);
        addStats("V", "M5", 3200, 12.3, 0.011);
        addStats("V", "M6", 3100, 13.4, 0.0051);
        addStats("V", "M7", 2900, 13.9, 0.0032);
        addStats("V", "M8", 2700, 14.4, 0.0020);
        addStats("V", "L0", 2600, null, 0.00029);
        addStats("V", "L3", 2200, null, 0.00013);
        addStats("V", "L8", 1500, null, 0.000032);
        addStats("V", "T2", 1400, null, 0.000025);
        addStats("V", "T6", 1000, null, 0.0000056);
        addStats("V", "T8", 800, null, 0.0000036);
        addStats("V", "Y5", 600, null, 6.3095734448019324943436013662234e-6);
        addStats("III", "G5", 5010, 0.7, 127);
        addStats("III", "G8", 4870, 0.6, 113);
        addStats("III", "K0", 4720, 0.5, 96);
        addStats("III", "K1", 4580, 0.4, 82);
        addStats("III", "K2", 4460, 0.2, 70);
        addStats("III", "K3", 4210, 0.1, 58);
        addStats("III", "K4", 4010, 0.0, 45);
        addStats("III", "K5", 3780, -0.2, 32);
        addStats("III", "M0", 3660, -0.4, 15);
        addStats("III", "M1", 3600, -0.5, 13);
        addStats("III", "M2", 3500, -0.6, 11);
        addStats("III", "M3", 3300, -0.7, 9.5);
        addStats("III", "M4", 3100, -0.75, 7.4);
        addStats("III", "M5", 2950, -0.8, 5.1);
        addStats("III", "M6", 2800, -0.9, 3.3);
        addStats("I", "B0", 21000, -6.4, 320000);
        addStats("I", "B1", 16000, -6.4, 280000);
        addStats("I", "B2", 14000, -6.4, 220000);
        addStats("I", "B3", 12800, -6.3, 180000);
        addStats("I", "B5", 11500, -6.3, 140000);
        addStats("I", "B6", 11000, -6.3, 98000);
        addStats("I", "B7", 10500, -6.3, 82000);
        addStats("I", "B8", 10000, -6.2, 73000);
        addStats("I", "B9", 9700, -6.2, 61000);
        addStats("I", "A0", 9400, -6.2, 50600);
        addStats("I", "A1", 9100, -6.2, 44000);
        addStats("I", "A2", 8900, -6.2, 40000);
        addStats("I", "A5", 8300, -6.1, 36000);
        addStats("I", "F0", 7500, -6.0, 20000);
        addStats("I", "F2", 7200, -6.0, 18000);
        addStats("I", "F5", 6800, -5.9, 16000);
        addStats("I", "F8", 6150, -5.9, 12000);
        addStats("I", "G0", 5800, -5.9, 9600);
        addStats("I", "G2", 5500, -5.8, 9500);
        addStats("I", "G5", 5100, -5.8, 9800);
        addStats("I", "G8", 5050, -5.7, 11000);
        addStats("I", "K0", 4900, -5.7, 12000);
        addStats("I", "K1", 4700, -5.6, 13500);
        addStats("I", "K2", 4500, -5.6, 15200);
        addStats("I", "K3", 4300, -5.6, 17000);
        addStats("I", "K4", 4100, -5.5, 18300);
        addStats("I", "K5", 3750, -5.5, 20000);
        addStats("I", "M0", 3660, -5.3, 50600);
        addStats("I", "M1", 3600, -5.3, 52000);
        addStats("I", "M2", 3500, -5.3, 53000);
        addStats("I", "M3", 3300, -5.3, 54000);
        addStats("I", "M4", 3100, -5.2, 56000);
        addStats("I", "M5", 2950, -5.2, 58000);
        // http://www.uni.edu/morgans/astro/course/Notes/section2/lumclasses.html
        addStats("II", "O0", 0, null, 1300);
        addStats("II", "Y9", 0, null, 1300);
        addStats("IV", "O0", 0, null, 25);
        addStats("IV", "Y9", 0, null, 25);
        normalizeTables();
    }

    public static double calcMassFromTypeAndClass(String spectra)
    {   // https://en.wikipedia.org/wiki/Mass%E2%80%93luminosity_relation
        int classOff = getClassOff(spectra);
        double exp;
        if (classOff < 5)
            exp = MathUtils.interpolate(classOff, 0, 5, 1, 3.5);
        else
            exp = MathUtils.interpolate(classOff, 5, 8, 3.5, 6);
        double lumen = calcLuminosityFromTypeAndClass(spectra);
        double mass = Math.pow(lumen, 1/exp);
        return mass;
    }

    public static double calcLuminosityFromTypeAndClass(String spectra)
    {
        int classOff = getClassOff(spectra);
        int specOff = getSpectraOff(spectra)*10 + getDecimalOff(spectra);
        return STAR_LUMENOSITY[classOff][specOff];
    }

    public static double calcTemperatureFromTypeAndClass(String spectra)
    {
        int classOff = getClassOff(spectra);
        int specOff = getSpectraOff(spectra)*10 + getDecimalOff(spectra);
        return STAR_TEMPERATURE[classOff][specOff];
    }

    public static double calcRadiusFromTypeAndClass(String spectra)
    {   // http://skyserver.sdss.org/dr5/en/proj/advanced/hr/radius1.asp
        double lumen = calcLuminosityFromTypeAndClass(spectra);
        double temp = calcTemperatureFromTypeAndClass(spectra);
        // R/Rs = (Ts/T)2(L/Ls)1/2
        double radiusM = Math.pow(SUN_TEMP/temp, 2)*Math.pow(lumen/SUN_LUMEN, .5)*SUN_RADIUS;
        return radiusM/1000; // convert to km
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
