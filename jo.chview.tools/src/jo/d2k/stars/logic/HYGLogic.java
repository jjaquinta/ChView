package jo.d2k.stars.logic;

import java.beans.IntrospectionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.StarLogic;
import jo.d2k.stars.data.HYGStar;
import jo.util.logic.CSVLogic;
import jo.util.utils.FormatUtils;
import jo.util.utils.obj.DoubleUtils;
import jo.util.utils.obj.StringUtils;

public class HYGLogic
{
    private static List<HYGStar> mStars;
    
    /*
    Proportions: 
    Rad   O    B    A    F    G    K    M
     0    0    0    0    0  500  250  250   (4)
     1    0    0  250    0    0    0  750   (4)
     2    0    0    0   33   33  266  666   (30)
     3    0    0   18    0   56  245  679   (53)
     4    0    0   25    0   50  200  725   (40)
     5    0    0   23   59   83  178  654   (84)
     6    0    0    0    0   75  258  666   (93)
     7    0    0   23   78   62  212  622   (127)
     8    0    6    0   18   91  219  664   (164)
     9    0    0   16   70   86  250  576   (184)
    Class:
    O    0    0    0    0    0    0
    B  111    9    9  162  135  684
    A  844    7    5  107  190  688
    F 3760    0    3   35   92  868
    G 4055    1    1   54   86  856
    K 2033    0    2  287   79  629
    M   90    0    0  233   11  755
    Magnitudes:
    O (    2) :  -5.2~ -5.2   8.8~  8.8   0.0~  0.0   0.0~  0.0   0.0~  0.0   0.0~  0.0   0.0~  0.0   0.0~  0.0
    B (  173) :  -6.6~ -1.4  -1.3~ -0.6  -0.5~ -0.0  -0.0~  0.3   0.4~  0.7   0.7~  1.1   1.1~  2.1   2.2~  5.6
    A ( 1566) :  -8.7~  0.8   0.8~  1.2   1.2~  1.6   1.6~  1.8   1.8~  2.1   2.1~  2.3   2.3~  2.7   2.7~  8.2
    F ( 7034) :  -5.5~  2.5   2.5~  2.9   2.9~  3.1   3.1~  3.4   3.4~  3.6   3.6~  3.8   3.8~  4.2   4.2~ 13.3
    G ( 7704) :  -1.5~  3.5   3.5~  3.9   3.9~  4.2   4.2~  4.5   4.5~  4.7   4.7~  5.0   5.0~  5.3   5.3~ 15.9
    K ( 4443) :  -4.1~  1.8   1.8~  4.7   4.7~  5.6   5.6~  6.0   6.0~  6.4   6.4~  6.9   6.9~  7.6   7.6~ 14.0
    M ( 1968) :  -5.2~  7.8   7.8~  8.6   8.6~  9.5   9.5~ 10.5  10.5~ 11.3  11.3~ 12.0  12.0~ 13.0  13.0~ 19.2
     */
    
    public static void convertData()
    {
        try
        {
            readData();
            overrideData();
            groupData();
            spliceData();
            writeDataToDisk();
            analyseData();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
    }
    
    private static void groupData()
    {
        for (int i = 0; i < mStars.size() - 1; i++)
            for (int j = i + 1; j < mStars.size(); j++)
            {
                HYGStar star1 = mStars.get(i);
                HYGStar star2 = mStars.get(j);
                double d = dist(star1, star2);
                if (d < .25)
                {
                    if (biggerThan(star1, star2))
                    {
                        if (star1.getSecondary() != null)
                        {
                            if (star2.getSecondary() != null)
                                System.out.println("*** Quad system not supported: "+star1.getD2KName()+", "+star1.getSecondary().getD2KName()
                                        +", "+star2.getD2KName()+", "+star2.getSecondary().getD2KName());
                            star2.setSecondary(star1.getSecondary());
                        }
                        star1.setSecondary(star2);
                        mStars.remove(j);
                        j--;
                        System.out.println("Merge "+star1.getD2KName()+"<-"+star2.getD2KName());
                        if (star2.getSecondary() != null)
                            System.out.println("  <-"+star2.getSecondary().getD2KName());
                    }
                    else
                    {
                        if (star2.getSecondary() != null)
                        {
                            if (star1.getSecondary() != null)
                                System.out.println("*** Quad system not supported: "+star1.getD2KName()+", "+star1.getSecondary().getD2KName()
                                        +", "+star2.getD2KName()+", "+star2.getSecondary().getD2KName());
                            star1.setSecondary(star2.getSecondary());
                        }
                        star2.setSecondary(star1);
                        mStars.remove(i);
                        i--;
                        j = mStars.size();
                        System.out.println("merge "+star2.getD2KName()+"<-"+star1.getD2KName());
                        if (star1.getSecondary() != null)
                            System.out.println("  <-"+star1.getSecondary().getD2KName());
                    }
                }
            }
    }
    
    private static boolean biggerThan(HYGStar star1, HYGStar star2)
    {
        int s1 = getSpectrum(star1);
        int s2 = getSpectrum(star2);
        if (s1 != s2)
            return s1 < s2;
        return star1.getAbsMag() > star2.getAbsMag();
    }

    private static double dist(HYGStar star1, HYGStar star2)
    {
        return dist(star1.getD2KX(), star1.getD2KY(), star1.getD2KZ(), star2.getD2KX(), star2.getD2KY(), star2.getD2KZ());
    }

    private static double dist(HYGStar star1, double x2, double y2, double z2)
    {
        return dist(star1.getD2KX(), star1.getD2KY(), star1.getD2KZ(), x2, y2, z2);
    }

    private static double dist(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
        return dist;
    }
    
    private static boolean isCloseQuad(String quad)
    {
        for (int i = 0; i < quad.length(); i++)
            switch (quad.charAt(i))
            {
                case '0':
                case 'a':
                case 'A':
//                case 'b':
//                case 'B':
                    break;
                default:
                    return false;
            }
        return true;
    }
    
    private static void analyseData()
    {
        Map<Integer, int[]> quadStars = new HashMap<Integer, int[]>();
        Map<Integer, List<Double>> quadMags = new HashMap<Integer, List<Double>>();
        Map<String, Double> quadPop = new HashMap<String, Double>();
        int[][] depthFreq = new int[7][3];
        int[][] depthType = new int[7][8];
        int[][] depthClass = new int[7][6];
        List<Double> depthDist = new ArrayList<Double>();
        for (HYGStar star : mStars)
        {
            int s = getSpectrum(star);
            double radius = dist(star, 0, 0, 0);
            int r = (int)(radius/5);
            for (int rr = r; rr <= 10; rr++)
            {
                int[] stars = quadStars.get(rr);
                if (stars == null)
                {
                    stars = new int[8];
                    quadStars.put(rr, stars);
                }
                stars[s]++;
                stars[7]++;
            }
            int c = getClass(star);
            if (c > 0)
            {
                depthClass[s][c]++;
                depthClass[s][0]++;
            }
            List<Double> mags = quadMags.get(s);
            if (mags == null)
            {
                mags = new ArrayList<Double>();
                quadMags.put(s, mags);
            }
            mags.add(star.getAbsMag());
            int d = 0;
            for (HYGStar ss = star.getSecondary(); ss != null; ss = ss.getSecondary())
            {
                int s2 = getSpectrum(ss);
                depthType[s][s2]++;
                depthType[s][7]++;
                d++;
                double dist = dist(star, ss);
                depthDist.add(dist);
            }
            if (radius < 25.0)
                depthFreq[s][d]++;
            if (isCloseQuad(star.getD2KQuadrant()))
            {
                Double pop = quadPop.get(star.getD2KQuadrant());
                if (pop == null)
                    pop = new Double(1);
                else
                    pop = new Double(pop.doubleValue() + 1);
                quadPop.put(star.getD2KQuadrant(), pop);
            }
        }
        Integer[] rads = quadStars.keySet().toArray(new Integer[0]);
        Arrays.sort(rads);        
        System.out.println("Proportions:");
        for (int i = 0; i < 10; i++)
        {
            System.out.print(FormatUtils.prefix(Integer.toString(rads[i]), 2, " "));
            int[] stars = quadStars.get(rads[i]);
            for (int j = 0; j < 7; j++)
            {
                double percent = (double)stars[j]/(double)stars[7];
                int p = (int)(percent*1000);
                System.out.print(" "+FormatUtils.prefix(Integer.toString(p), 4, " "));
            }
            System.out.print("   ("+FormatUtils.prefix(Integer.toString(stars[7]), 3, " ")+")");
            double r = (i + 1)*5;
            double v = 4.0/3.0*Math.PI*r*r*r;
            double density = stars[7]/v;
            double quadDensity = density*StarLogic.QUAD_SIZE*StarLogic.QUAD_SIZE*StarLogic.QUAD_SIZE;
            System.out.println("   "+DoubleUtils.format(quadDensity, 4, 1)+"/quad");
        }
        System.out.println("Class:");
        for (int i = 0; i < 7; i++)
        {
            System.out.print(SPECTRA.charAt(i)+" "+FormatUtils.prefix(Integer.toString(depthClass[i][0]), 4, " "));
            for (int j = 1; j < 6; j++)
            {
                double percent = (double)depthClass[i][j]/(double)depthClass[i][0];
                int p = (int)(percent*1000);
                System.out.print(" "+FormatUtils.prefix(Integer.toString(p), 4, " "));
            }
            System.out.println();
        }
        System.out.println("Quad Population ("+quadPop.size()+"):");
        List<Double> quadPops = new ArrayList<Double>();
        quadPops.addAll(quadPop.values());
        Collections.sort(quadPops);
        double[][] bucketValues = bucketize(quadPops, 8);
        for (int j = 0; j < bucketValues.length; j++)
            System.out.println("  "+DoubleUtils.format(bucketValues[j][0], 5, 1)+"~"+DoubleUtils.format(bucketValues[j][1], 5, 1));
        System.out.println();
        System.out.println("Magnitudes:");
        for (int i = 0; i < 7; i++)
        {
            List<Double> mags = quadMags.get(i);
            if (mags == null)
                continue;
            System.out.print(SPECTRA.charAt(i)+" ("+FormatUtils.prefix(Integer.toString(mags.size()), 5, " ")+") :");
            Collections.sort(mags);
            bucketValues = bucketize(mags, 8);
            for (int j = 0; j < bucketValues.length; j++)
                System.out.print(" "+DoubleUtils.format(bucketValues[j][0], 5, 1)+"~"+DoubleUtils.format(bucketValues[j][1], 5, 1));
            System.out.println();
        }
        System.out.println("Secondary Frequency:");
        for (int i = 0; i < 7; i++)
        {
            int tot = depthFreq[i][0] + depthFreq[i][1] + depthFreq[i][2]; 
            System.out.print(SPECTRA.charAt(i)+" ("+FormatUtils.prefix(Integer.toString(tot), 5, " ")+") :");
            for (int j = 0; j < 3; j++)
            {
                double percent = (double)depthFreq[i][j]/(double)tot;
                int p = (int)(percent*1000);
                System.out.print(" "+FormatUtils.prefix(Integer.toString(p), 4, " "));
            }
            System.out.println();
        }
        System.out.println("Secondary Types:");
        for (int i = 0; i < 7; i++)
        {
            System.out.print(SPECTRA.charAt(i)+" ("+FormatUtils.prefix(Integer.toString(depthType[i][7]), 5, " ")+") :");
            for (int j = 0; j < 7; j++)
            {
                double percent = (double)depthType[i][j]/(double)depthType[i][7];
                int p = (int)(percent*1000);
                System.out.print(" "+FormatUtils.prefix(Integer.toString(p), 4, " "));
            }
            System.out.println();
        }
        System.out.println("Secondary Distances:");
        Collections.sort(depthDist);
        bucketValues = bucketize(depthDist, 8);
        for (int j = 0; j < bucketValues.length; j++)
            System.out.println("  "+DoubleUtils.format(bucketValues[j][0], 5, 3)+"~"+DoubleUtils.format(bucketValues[j][1], 5, 3));
        System.out.println();
    }
    
    private static double[][] bucketize(List<Double> values, int buckets)
    {
        double[][] bucketValues = new double[buckets][2];
        if (values.size() < buckets)
            buckets = values.size();
        int bucketNum = values.size()/buckets;
        for (int i = 0; i < buckets; i++)
        {
            int first = i*bucketNum;
            int last = (i + 1)*bucketNum - 1;
            if (last >= values.size())
                last = values.size() - 1;
            bucketValues[i][0] = values.get(first);
            bucketValues[i][1] = values.get(last);
        }
        return bucketValues;
    }

    private static void overrideData() throws IOException
    {
        File chvInput = new File("D:\\temp\\data\\chview2\\HYG-Database-master\\hip_overrides.txt");
        BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream(chvInput), "utf-8"));
        for (;;)
        {
            String inbuf = rdr.readLine();
            if (inbuf == null)
                break;
            inbuf = inbuf.trim();
            int o = inbuf.indexOf(":");
            if (o < 0)
                continue;
            String hip = inbuf.substring(0, o).trim();
            inbuf = inbuf.substring(o  + 1).trim();
            StringTokenizer st1 = new StringTokenizer(inbuf, "(),");
            String properName = null;
            while (st1.hasMoreTokens())
                properName = st1.nextToken();
            for (HYGStar star : mStars)
            {
                if (star.getHIP().equals(hip))
                {
                    if (!properName.equals(star.getProperName()))
                    {
                        System.out.println(star.getHIP()+": "+star.getProperName()+"->"+properName);
                        star.setD2KName(properName);
                    }
                    break;
                }
            }
        }
        rdr.close();
    }
    
    private static void spliceData() throws IOException
    {
        System.out.println("Splicing data");
        double lastX = 0;
        double lastY = 0;
        double lastZ = 0;
        File chvInput = new File("D:\\temp\\data\\chview2\\CJC25LY.LST");
        BufferedReader rdr = new BufferedReader(new FileReader(chvInput));
        for (;;)
        {
            String inbuf = rdr.readLine();
            if (inbuf == null)
                break;
            inbuf = " "+inbuf;
            inbuf = inbuf.replace("//", "/ /");
            inbuf = inbuf.replace("//", "/ /");
            StringTokenizer st1 = new StringTokenizer(inbuf, "/");
            st1.nextToken(); // CJC Name
            String properName = st1.nextToken();
            if (isNameTaken(properName))
                continue;
            //System.out.println(properName);
            st1.nextToken(); // distance            
            st1.nextToken(); // class
            st1.nextToken(); // mag
            st1.nextToken(); // constellation
            st1.nextToken(); // notes
            StringTokenizer st2 = new StringTokenizer(st1.nextToken(), ",");
            double x = Double.parseDouble(st2.nextToken());
            double y = Double.parseDouble(st2.nextToken());
            double z = Double.parseDouble(st2.nextToken());
            double lastDist = dist(x, y, z, lastX, lastY, lastZ);
            if (lastDist < .25)
            {
                System.out.println(properName+" is a close companion, skipping");
                continue; // close companion, skip
            }
            lastX = x;
            lastY = y;
            lastZ = z;
            HYGStar best = null;
            double bestDist = 0;
            for (HYGStar star : mStars)
            {
                double dist = dist(star, x, y, z);
                if ((best == null) || (bestDist > dist))
                {
                    best = star;
                    bestDist = dist;
                }
            }
            if (!StringUtils.isTrivial(best.getProperName()))
            {
                System.out.println(properName+" already has a proper name of "+best.getProperName());
                continue; // already has a good name
            }
            if (!best.getD2KName().startsWith("HIP") && !best.getD2KName().startsWith("UN"))
            {
                System.out.println(properName+" already has a qualified name of "+best.getD2KName());
                continue; // already has a given name
            }
            if (bestDist > 2)
            {
                System.out.println(properName+" has no nearby star in database");
                continue; // too far away
            }
            System.out.println(bestDist+"ly to "+best.getD2KName()+" -> "+properName);
            best.setD2KName(properName);
            takeName(properName);
        }
        rdr.close();
    }

    private static void readData() throws Exception
    {
        File hygInput = new File("D:\\temp\\data\\chview2\\HYG-Database-master\\hygxyz.csv");
        mStars = new ArrayList<HYGStar>();
        Collection<Object> stars = CSVLogic.fromCSV(hygInput, HYGStar.class);
        for (Iterator<Object> i = stars.iterator(); i.hasNext(); )
            mStars.add((HYGStar)i.next());
        for (Iterator<HYGStar> i = mStars.iterator(); i.hasNext(); )
        {
            HYGStar star = i.next();
            if (StringUtils.isTrivial(star.getSpectrum()))
            {
                i.remove();
                continue;
            }
            int s = getSpectrum(star);
            if (s < 0)
            {
                i.remove();
                continue;
            }
            if (StringUtils.isTrivial(star.getProperName()) && (star.getDistance() > 100))
            {
                i.remove();
                continue;
            }
            String name = getName(star);
            takeName(name);
            star.setD2KName(name);
            String d2kSpectra = SPECTRA.substring(s, s+1);
            if ((star.getSpectrum().length() > 1) && Character.isDigit(star.getSpectrum().charAt(1)))
                d2kSpectra += star.getSpectrum().substring(1, 2);
            if (star.getSpectrum().contains("IV"))
                d2kSpectra += "IV";
            else if (star.getSpectrum().contains("V"))
                d2kSpectra += "V";
            else if (star.getSpectrum().contains("III"))
                d2kSpectra += "III";
            else if (star.getSpectrum().contains("II"))
                d2kSpectra += "II";
            else if (star.getSpectrum().contains("I"))
                d2kSpectra += "I";
            star.setD2KSpectra(d2kSpectra);
            star.setD2KX(star.getX()*3.3620579420583403  - 0.017749703345074235*star.getY() - 0.034456315231951369*star.getZ());
            star.setD2KY(star.getX()*0.10637122323734075 + 3.2648305150678061  *star.getY() - 0.034622935461212444*star.getZ());
            star.setD2KZ(star.getX()*0.28139405131517042 - 0.001138386887119961*star.getY() + 3.162168413851302*star.getZ());
            star.setD2KQuadrant(getQuadrant(star));
        }
        System.out.println("Read "+stars.size()+" stars");
    }
    
    private static void writeDataToDisk() throws IOException, IntrospectionException
    {
        List<StarBean> d2kStars = makeD2KStars();
        File d2kOutput = new File("D:\\temp\\data\\chview2\\HYG-Database-master\\d2k.csv");
        CSVLogic.toCSV(d2kOutput, StarBean.class, d2kStars);
    }

    private static List<StarBean> makeD2KStars()
    {
        List<StarBean> d2kStars = new ArrayList<StarBean>();
        long oid = System.currentTimeMillis();
        for (HYGStar hygStar : mStars)
        {
            for (HYGStar hygSec = hygStar; hygSec != null; hygSec = hygSec.getSecondary())
            {
                StarBean d2kStar = new StarBean();
                d2kStar.setOID(oid++);
                d2kStar.setName(hygSec.getD2KName());
                d2kStar.setQuadrant(hygSec.getD2KQuadrant());
                d2kStar.setX(hygSec.getD2KX());
                d2kStar.setY(hygSec.getD2KY());
                d2kStar.setZ(hygSec.getD2KZ());
                d2kStar.setSpectra(hygSec.getD2KSpectra());
                d2kStar.setAbsMag(hygSec.getAbsMag());
                if (hygSec != hygStar)
                    d2kStar.setParent(hygStar.getOID());
                d2kStars.add(d2kStar);
            }
        }
        return d2kStars;
    }

    private static final String SPECTRA = "OBAFGKM";
    
    private static int getSpectrum(HYGStar star)
    {
        String spectrum = star.getSpectrum().toUpperCase();
        char s = spectrum.charAt(0);
        int i = SPECTRA.indexOf(s);        
        return i;
    }
    
    private static int getClass(HYGStar star)
    {
        String spectrum = star.getSpectrum().toUpperCase();
        if (spectrum.endsWith("IV"))
            return 4;
        if (spectrum.endsWith("V"))
            return 5;
        if (spectrum.endsWith("III"))
            return 3;
        if (spectrum.endsWith("II"))
            return 2;
        if (spectrum.endsWith("I"))
            return 1;
        return 0;
    }
    
    private static String getName(HYGStar star)
    {
        if (!StringUtils.isTrivial(star.getProperName()))
        {
            System.out.println(star.getProperName());
            return star.getProperName();
        }
        if (!StringUtils.isTrivial(star.getHIP()) && !"0".equals(star.getHIP()))
        {
            String name = "HIP"+star.getHIP();
            if (!isNameTaken(name))
                return name;
        }
        if (!StringUtils.isTrivial(star.getHD()))
        {
            String name = "HD"+star.getHIP();
            if (!isNameTaken(name))
                return name;
        }
        if (!StringUtils.isTrivial(star.getHR()))
        {
            String name = "HR"+star.getHIP();
            if (!isNameTaken(name))
                return name;
        }
        if (!StringUtils.isTrivial(star.getGliese()))
        {
            String name = "G"+star.getHIP();
            if (!isNameTaken(name))
                return name;
        }
        if (!StringUtils.isTrivial(star.getBayerFlamsteed()))
        {
            String name = "BF"+star.getHIP();
            if (!isNameTaken(name))
                return name;
        }
        return "UN"+star.getStarID();
    }
    
    private static String getQuadrant(HYGStar star)
    {
        return StarLogic.getQuadrant(star.getD2KX(), star.getD2KY(), star.getD2KZ());
    }
    
    private static Set<String> mNames = new HashSet<String>();
    
    private static boolean isNameTaken(String name)
    {
        return mNames.contains(name);
    }
    
    private static void takeName(String name)
    {
        mNames.add(name);
    }
    
    public static void main(String[] argv)
    {
        convertData();
    }
}
