package jo.d2k.stars.logic;

import java.beans.IntrospectionException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.StarLogic;
import jo.d2k.stars.data.SimbadStar;
import jo.util.logic.CSVLogic;
import jo.util.utils.FormatUtils;
import jo.util.utils.obj.DoubleUtils;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.StringArrayUtils;
import jo.util.utils.obj.StringUtils;
public class Simbad2Logic
{
    private static final String BASE = "D:\\temp\\data\\chview2\\simbad\\simbad_data2";
    
    private static Map<String,SimbadStar> mStarIndex;
    private static List<SimbadStar> mStars;
    private static Map<Integer,SimbadStar> mStarIDIndex;
    
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
            readData("simbad", 0);
            readData("bright", 100000);
            readExtraData("simbad", "NAME", 0);
            readExtraData("bright", "NAME", 100000);
            readExtraData("simbad", "HIP", 0);
            readExtraData("bright", "HIP", 100000);
            readExtraData("simbad", "GJ", 0);
            readExtraData("bright", "GJ", 100000);
            readExtraData("simbad", "HD", 0);
            readExtraData("bright", "HD", 100000);
            readExtraData("simbad", "HR", 0);
            readExtraData("bright", "HR", 100000);
            readExtraData("simbad", "SAO", 0);
            readExtraData("bright", "SAO", 100000);
            readExtraData("simbad", "2MASS", 0);
            readExtraData("bright", "2MASS", 100000);
            eliminateDoubleStarEntriesManual();
            eliminateDoubleStarEntriesHD();
            eliminateDoubleStarEntriesGJ();
            //eliminateDoubleStarEntriesName();
            wikipediaData();
            overrideData();
            spliceData();
            groupData();
            writeDataToDisk();
            analyseData();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
    }
    
    private static Set<String> MANUAL_DOUBLES = new HashSet<String>();
    static
    {
        MANUAL_DOUBLES.add("* 36 Oph A");
        MANUAL_DOUBLES.add("HIP 21088");
    }
    
    private static void eliminateDoubleStarEntriesManual()
    {
        for (Iterator<SimbadStar> i = mStars.iterator(); i.hasNext(); )
        {
            SimbadStar star = i.next();
            if (MANUAL_DOUBLES.contains(star.getName()))
            {
                System.out.println(star.getName()+" is a double star entry (manual). Removing.");
                i.remove();
                removeFromIndex(star);
            }   
        }
    }
    
    private static void eliminateDoubleStarEntriesHD()
    {
        Map<String,SimbadStar> hdIndex = new HashMap<String, SimbadStar>();
        Set<String> doubleCandidates = new HashSet<String>();
        for (SimbadStar star : mStars)
        {
            String hdName = star.getCatalogNames().get("HD");
            if (StringUtils.isTrivial(hdName))
                continue;
            hdIndex.put(hdName, star);
            if (hdName.endsWith("J"))
                doubleCandidates.add(hdName);
        }
        System.out.println(doubleCandidates.size()+" double candidates (HD)");
        for (String dName : doubleCandidates)
        {
            SimbadStar dStar = hdIndex.get(dName);
            String sName = dName.substring(0, dName.length() - 1);
            if (hdIndex.containsKey(sName))
            {
                System.out.println(dName+"/"+dStar.getName()+" is a double star entry (HD). Removing.");
                mStars.remove(dStar);
                removeFromIndex(dStar);
            }
        }
    }
    
    private static void removeFromIndex(SimbadStar star)
    {
        for (String key : mStarIndex.keySet().toArray(new String[0]))
        {
            SimbadStar s = mStarIndex.get(key);
            if (s == star)
                mStarIndex.remove(key);
        }
        for (Integer key : mStarIDIndex.keySet().toArray(new Integer[0]))
        {
            SimbadStar s = mStarIDIndex.get(key);
            if (s == star)
                mStarIDIndex.remove(key);
        }
    }

    private static void eliminateDoubleStarEntriesGJ()
    {
        Map<String,SimbadStar> gjIndex = new HashMap<String, SimbadStar>();
        Set<String> doubleCandidates = new HashSet<String>();
        for (SimbadStar star : mStars)
        {
            String gjName = star.getCatalogNames().get("GJ");
            if (StringUtils.isTrivial(gjName))
                continue;
            gjIndex.put(gjName, star);
            if (gjName.toUpperCase().endsWith(" A"))
                doubleCandidates.add(gjName);
        }
        System.out.println(doubleCandidates.size()+" double candidates (GJ)");
        for (String dName : doubleCandidates)
        {
            SimbadStar dStar = gjIndex.get(dName);
            String sName = dName.substring(0, dName.length() - 2);
            if (gjIndex.containsKey(sName))
            {
                SimbadStar sStar = gjIndex.get(sName);
                System.out.println(sName+"/"+dStar.getName()+" is a double star entry "+sStar.getSpectrum()+"/"+dStar.getSpectrum()+" (GJ). Removing.");
                mStars.remove(sStar);
                removeFromIndex(sStar);
            }
        }
    }

    /*
    private static void eliminateDoubleStarEntriesName()
    {
        for (SimbadStar star : mStars.toArray(new SimbadStar[0]))
        {
            String sName = star.getName();
            if (!sName.startsWith("CCDM"))
                continue;
            if (sName.indexOf('+') < 0)
                continue;
            System.out.println(star.getName()+" is a double star entry "+star.getSpectrum()+" (Name). Removing.");
            mStars.remove(star);
            removeFromIndex(star);
        }
    }
    */
    
    private static void groupData()
    {
        // group stars
        List<SimbadStar> binaries = new ArrayList<SimbadStar>();
        for (int i = 0; i < mStars.size() - 1; i++)
        {
            SimbadStar star1 = mStars.get(i);
            //Point3D center1 = getCenter(star1);
            for (int j = mStars.size() - 1; j > i; j--)
            {
                SimbadStar star2 = mStars.get(j);
                //Point3D center2 = getCenter(star2);
                if ((star1.getRA() == star2.getRA()) && (star1.getDec() == star2.getDec()))
                    System.out.println("IDENTICAL! "+star1.getName()+" & "+star2.getName());
                //double d = center1.dist(center2);
                double d = dist(star1, star2);
                if (d < .25)
                {
                    star2.setSecondary(star1.getSecondary());
                    star1.setSecondary(star2);
                    mStars.remove(j);
                    //center1 = getCenter(star1);
                }
            }
            if (star1.getSecondary() != null)
                binaries.add(star1);
        }
        System.out.println(binaries.size()+" binaries");
        // sort groups
        for (SimbadStar base : binaries)
        {
            List<SimbadStar> system = new ArrayList<SimbadStar>();
            for (SimbadStar s = base; s != null; s = s.getSecondary())
                system.add(s);
            Collections.sort(system, new SimbadComparator());
            mStars.remove(base);
            for (int i = 0; i < system.size(); i++)
            {
                SimbadStar thisStar = system.get(i);
                if (i + 1 < system.size())
                    thisStar.setSecondary(system.get(i+1));
                else
                    thisStar.setSecondary(null);
                if (i > 0)
                    System.out.print(" --> ");
                else
                    System.out.print("  ");
                System.out.print(thisStar.getName());
            }
            System.out.println();
            mStars.add(system.get(0));
        }
    }

    private static double dist(SimbadStar star1, SimbadStar star2)
    {
        return dist(star1.getD2KX(), star1.getD2KY(), star1.getD2KZ(), star2.getD2KX(), star2.getD2KY(), star2.getD2KZ());
    }

    private static double dist(SimbadStar star1, double x2, double y2, double z2)
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
        //Map<Integer, int[]> quadStars = new HashMap<Integer, int[]>();
        Map<String,Integer> specClassFreq = new HashMap<String, Integer>();
        Map<String,Double> specClassMag = new HashMap<String, Double>();
        Map<String,Integer> specFreq = new HashMap<String, Integer>();
        Map<String,Integer> specSecFreq = new HashMap<String, Integer>();
        Map<String,Integer> specSecSpecFreq = new HashMap<String, Integer>();
        int maxComp = 0;
        
        Map<Integer, List<Double>> quadMags = new HashMap<Integer, List<Double>>();
        Map<String, Double> quadPop = new HashMap<String, Double>();
        int[][] depthFreq = new int[SPECTRA.length()][4];
        int[][] depthType = new int[SPECTRA.length()][11];
        int[][] depthClass = new int[SPECTRA.length()][11];
        List<Double> depthDist = new ArrayList<Double>();
        for (SimbadStar star : mStars)
        {
            int spOff = getSpectrum(star.getD2KSpectra());
            if (spOff < 0)
                System.out.println("UNKNOWN SPECTRUM: "+star.getD2KSpectra());
            String sp = SPECTRA.substring(spOff, spOff + 1);
            int clOff = getClass(star);
            String cl = CLASSES[clOff];
            if (specClassFreq.containsKey(sp+cl))
                specClassFreq.put(sp+cl, specClassFreq.get(sp+cl) + 1);
            else
                specClassFreq.put(sp+cl, 1);
            if (specClassMag.containsKey(sp+cl))
                specClassMag.put(sp+cl, specClassMag.get(sp+cl) + getAbsMag(star));
            else
                specClassMag.put(sp+cl, getAbsMag(star));
            if (specFreq.containsKey(sp))
                specFreq.put(sp, specFreq.get(sp) + 1);
            else
                specFreq.put(sp, 1);
            int numSec = 0;
            for (SimbadStar s = star.getSecondary(); s != null; s = s.getSecondary())
                numSec++;
            if (specSecFreq.containsKey(sp+numSec))
                specSecFreq.put(sp+numSec, specSecFreq.get(sp+numSec) + 1);
            else
                specSecFreq.put(sp+numSec, 1);
            maxComp = Math.max(maxComp, numSec);
            for (SimbadStar s = star.getSecondary(); s != null; s = s.getSecondary())
            {
                int sp2Off = getSpectrum(s.getD2KSpectra());
                if (sp2Off < 0)
                    System.out.println("UNKNOWN SPECTRUM: "+s.getD2KSpectra());
                String sp2 = SPECTRA.substring(sp2Off, sp2Off + 1);
                if (specSecSpecFreq.containsKey(sp+sp2))
                    specSecSpecFreq.put(sp+sp2, specSecSpecFreq.get(sp+sp2) + 1);
                else
                    specSecSpecFreq.put(sp+sp2, 1);
            }
            
            double radius = dist(star, 0, 0, 0);
            int c = getClass(star);
            if (c > 0)
            {
                depthClass[spOff][c]++;
                depthClass[spOff][0]++;
            }
            List<Double> mags = quadMags.get(spOff);
            if (mags == null)
            {
                mags = new ArrayList<Double>();
                quadMags.put(spOff, mags);
            }
            mags.add(star.getAbsMag());
            int d = 0;
            for (SimbadStar ss = star.getSecondary(); ss != null; ss = ss.getSecondary())
            {
                int s2 = getSpectrum(ss.getD2KSpectra());
                depthType[spOff][s2]++;
                depthType[spOff][7]++;
                d++;
                double dist = dist(star, ss);
                depthDist.add(dist);
            }
            if (radius < 25.0)
                depthFreq[spOff][d]++;
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
        int[] line = new int[SPECTRA.length()];
        for (int i = 0; i < SPECTRA.length(); i++)
            if (specFreq.containsKey(SPECTRA.substring(i, i+1)))
                line[i] = specFreq.get(SPECTRA.substring(i, i+1));
            else
                line[i] = 0;
        normalize(line);
        System.out.println("public int[] SPECTRUM_FREQ = {");
        System.out.println("    "+toString(line));
        System.out.println("    };");
        
        System.out.println("public int[][] SPECTRUM_CLASS_FREQ = {");
        System.out.print("  //");
        for (int j = 0; j < CLASSES.length; j++)
            System.out.print(StringUtils.prefix(CLASSES[j], ' ', 6));
        System.out.println();
        line = new int[CLASSES.length];
        for (int i = 0; i < SPECTRA.length(); i++)
        {
            String sp = SPECTRA.substring(i, i+1);
            for (int j = 0; j < CLASSES.length; j++)
                if (specClassFreq.containsKey(sp+CLASSES[j]))
                    line[j] = specClassFreq.get(sp+CLASSES[j]);
                else
                    line[j] = 0;
            normalize(line);
            System.out.println("    {"+toString(line)+" }, // "+sp);
        }
        System.out.println("    };");

        System.out.println("public double[][] ABS_MAG_FREQ = {");
        System.out.print("  //");
        for (int j = 0; j < CLASSES.length; j++)
            System.out.print(StringUtils.prefix(CLASSES[j], ' ', 6));
        System.out.println();
        double[] dline = new double[CLASSES.length];
        for (int i = 0; i < SPECTRA.length(); i++)
        {
            String sp = SPECTRA.substring(i, i+1);
            for (int j = 0; j < CLASSES.length; j++)
                if (specClassFreq.containsKey(sp+CLASSES[j]))
                    dline[j] = specClassMag.get(sp+CLASSES[j])/specClassFreq.get(sp+CLASSES[j]);
                else
                    dline[j] = 0;
            System.out.println("    {"+toString(dline)+" }, // "+sp);
        }
        System.out.println("    };");

        System.out.println("public int[][] SECONDARY_NUM_FREQ = {");
        System.out.print("  //");
        for (int j = 0; j <= maxComp; j++)
            System.out.print(StringUtils.prefix(String.valueOf(j), ' ', 6));
        System.out.println();
        line = new int[maxComp+1];
        for (int i = 0; i < SPECTRA.length(); i++)
        {
            String sp = SPECTRA.substring(i, i+1);
            for (int j = 0; j <= maxComp; j++)
                if (specSecFreq.containsKey(sp+j))
                    line[j] = specSecFreq.get(sp+j);
                else
                    line[j] = 0;
            normalize(line);
            System.out.println("    {"+toString(line)+" }, // "+sp);
        }
        System.out.println("    };");
        
        System.out.println("public int[][] SECONDARY_TYPE_FREQ = {");
        System.out.print("  //");
        for (int j = 0; j < SPECTRA.length(); j++)
            System.out.print(StringUtils.prefix(SPECTRA.substring(j, j+1), ' ', 6));
        System.out.println();
        line = new int[SPECTRA.length()];
        for (int i = 0; i < SPECTRA.length(); i++)
        {
            String sp = SPECTRA.substring(i, i+1);
            for (int j = 0; j < SPECTRA.length(); j++)
                if (specSecSpecFreq.containsKey(sp+SPECTRA.substring(j, j+1)))
                    line[j] = specSecSpecFreq.get(sp+SPECTRA.substring(j, j+1));
                else
                    line[j] = 0;
            normalize(line);
            System.out.println("    {"+toString(line)+" }, // "+sp);
        }
        System.out.println("    };");

        System.out.println("Class:");
        for (int i = 0; i < SPECTRA.length(); i++)
        {
            System.out.print(SPECTRA.charAt(i)+" "+FormatUtils.prefix(Integer.toString(depthClass[i][0]), 4, " "));
            for (int j = 1; j < depthClass[i].length; j++)
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
        for (int i = 0; i < SPECTRA.length(); i++)
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
        for (int i = 0; i < SPECTRA.length(); i++)
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
    
    private static String toString(int[] line)
    {
        StringBuffer sb = new StringBuffer();
        for (int i : line)
        {
            if (sb.length() > 0)
                sb.append(",");
            sb.append(StringUtils.spacePrefix(i, 5));
        }
        return sb.toString();
    }
    
    private static String toString(double[] line)
    {
        StringBuffer sb = new StringBuffer();
        for (double i : line)
        {
            if (sb.length() > 0)
                sb.append(",");
            sb.append(StringUtils.prefix(FormatUtils.formatDouble(i, 1), ' ', 5));
        }
        return sb.toString();
    }

    private static void normalize(int[] line)
    {
        int tot = 0;
        for (int i : line)
            tot += i;
        double mult = tot/1000.0;
        tot = 1000;
        for (int i = 0; i < line.length; i++)
        {
            line[i] = (int)(line[i]/mult + .5);
            tot -= line[i];
        }
        while (tot != 0)
        {
            int delta = (int)Math.signum(tot);
            line[line.length/2] += delta; // TODO: spread more proportionally
            tot -= delta;
        }
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
    
    private static final String[][] OVERRIDES = 
    {
        { "HR 5459", "Alpha Centauri A" },
        { "HR 5460", "Alpha Centauri B" },
    };

    private static void overrideData()
    {
        for (int i = 0; i < OVERRIDES.length; i++)
        {
            SimbadStar star = mStarIndex.get(OVERRIDES[i][0]);
            if (star == null)
            {
                System.out.println("Overrides: No star found for "+OVERRIDES[i][0]);
                continue;
            }
            star.setD2KName(OVERRIDES[i][1]);
        }
    }
    
    private static void wikipediaData() throws IOException
    {
        File siInput = new File("D:\\temp\\data\\chview2\\simbad\\simbad_idents.txt");
        readOverrides(siInput);
        readOverridesFromWeb(siInput);
        writeOverrides(siInput);
    }
    
    private static void readOverridesFromWeb(File siInput) throws IOException
    {
        System.out.println("Reading overrides from web");
        int done = 0;
        for (int i = 0; i < mStars.size(); i++)
        {   
            SimbadStar star = mStars.get(i);
            String url = "http://simbad.u-strasbg.fr/simbad/sim-id?Ident="+star.getName();
            url = url.replace(" ", "%20");
            url = url.replace("+", "%2B");
            url = url.replace("#", "%23");
            //System.out.println(url);
            if (star.getIdents().size() > 0)
            {
                star.setSimbadURL(url);
                continue;
            }
            String html = StarsInFictionOverrides.getHTML(url, null);
            if (html == null)
            {
                System.out.println("No answer on "+url);
                continue;
            }
            //System.out.println(html);
            int o;
            o = html.indexOf(" -- ");
            if (o > 0)
            {
                String desc = html.substring(o + 4).trim();
                o = desc.indexOf("<");
                if (o > 0)
                {
                    desc = desc.substring(0, o).trim();
                    star.setNotes(desc);
                }
            }
            o = html.toUpperCase().indexOf("IDENTIFIERS (");
            if (o < 0)
            {
                System.out.println("Cant find "+star.getName());
                continue;
            }
            html = html.substring(o);
            o = html.indexOf("<HR");
            if (o < 0)
            {
                System.out.println("Cant find "+star.getName());
                continue;
            }       
            System.out.print(i+"/"+mStars.size()+" "+star.getName()+":");
            html = html.substring(0, o);
            for (;;)
            {
                o = html.indexOf("<TT>");
                if (o < 0)
                    break;
                html = html.substring(o + 4);
                o = html.indexOf("</TT>");
                if (o < 0)
                    break;
                String tt = html.substring(0, o).trim();
                html = html.substring(o + 5);
                tt = tt.replaceAll("<[^>]*>", "").trim();
                star.getIdents().add(tt);
                System.out.print(tt+", ");
            }
            star.setSimbadURL(url);
            System.out.println();
            done++;
            if (done%10 == 0)
                writeOverrides(siInput);
        }        
    }
    
    private static void writeOverrides(File f) throws IOException
    {
        System.out.println("Writing overrides from web");
        BufferedWriter wtr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "utf-8"));
        wtr.write('\ufeff');
        for (SimbadStar star : mStars)
        {
            if (StringUtils.isTrivial(star.getNotes()) && (star.getIdents().size() == 0))
                continue;
            wtr.write(star.getName());
            if (!StringUtils.isTrivial(star.getNotes()))
            {
                wtr.write("\t$");
                wtr.write(star.getNotes());
            }
            for (String desig : star.getIdents())
            {
                wtr.write("\t");
                wtr.write(desig);
            }
            wtr.newLine();
        }        
        wtr.close();
    }

    private static void readOverrides(File f) throws IOException
    {
        if (!f.exists())
            return;
        System.out.println("Reading overrides from disk");
        BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"));
        for (;;)
        {
            String inbuf = rdr.readLine();
            if (inbuf == null)
                break;
            if (inbuf.startsWith("\ufeff"))
                inbuf = inbuf.substring(1);
            StringTokenizer st = new StringTokenizer(inbuf, "\t");
            if (st.countTokens() == 0)
                continue;
            String simbadName = st.nextToken();
            SimbadStar star = mStarIndex.get(simbadName);
            if (star == null)
            {
                System.out.println("Overrides for missing star "+simbadName);
                continue;
            }
            while (st.hasMoreElements())
            {
                String desig = st.nextToken();
                if (desig.endsWith("."))
                    desig = desig.substring(0, desig.length() - 1);
                if (desig.startsWith("$"))
                    star.setNotes(desig.substring(1));
                else
                    star.getIdents().add(desig);
            }
        }
        rdr.close();
    }
    
    private static Set<String> TOO_FAR = new HashSet<String>();
    static
    {
        TOO_FAR.add("Xi Puppis");
        TOO_FAR.add("Sheliak");
        TOO_FAR.add("Omicron Persei");
        TOO_FAR.add("Phi Orionis");
        TOO_FAR.add("Polaris");
        TOO_FAR.add("Rigel");
        TOO_FAR.add("Mira");
        TOO_FAR.add("Mintaka");
        TOO_FAR.add("Lambda Scorpii");
        TOO_FAR.add("Maia");
        TOO_FAR.add("Gamma Andromedae");
        TOO_FAR.add("Epsilon Pegasi");
        TOO_FAR.add("Ensis");
        TOO_FAR.add("Deneb");
        TOO_FAR.add("Delta Sagittarii");
        TOO_FAR.add("Alnilam");
        TOO_FAR.add("Alpha Draconis");
        TOO_FAR.add("Antares");
        TOO_FAR.add("Beta Aquarii");
        TOO_FAR.add("Betelgeuse");
        TOO_FAR.add("Canopus");
    }
    
    private static void spliceData() throws IOException
    {
        System.out.println("Splicing data");
        File f = new File("d:\\temp\\data\\chview2\\simbad\\stars_in_fiction.txt");
        BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"));
        int matched = 0;
        int unmatched = 0;
        for (;;)
        {
            String inbuf = rdr.readLine();
            if (inbuf == null)
                break;
            if (inbuf.startsWith("\ufeff"))
                inbuf = inbuf.substring(1);
            StringTokenizer st = new StringTokenizer(inbuf, "\t");
            if (st.countTokens() == 0)
                continue;
            String properName = st.nextToken();
            if (TOO_FAR.contains(properName))
                continue;
            String wikiURL = st.nextToken();
            if (!StringUtils.isTrivial(wikiURL) && !wikiURL.startsWith("http://www.wikipedia.org"))
                wikiURL = "http://www.wikipedia.org" + wikiURL;
            Set<String> idents = new HashSet<String>();
            while (st.hasMoreElements())
            {
                String desig = st.nextToken();
                if (desig.endsWith("."))
                    desig = desig.substring(0, desig.length() - 1);
                idents.add(desig);
            }
            expandIdents(idents);
            boolean found = false;
            for (String ident : idents)
                if (mStarIndex.containsKey(ident))
                {
                    SimbadStar star = mStarIndex.get(ident);
                    System.out.println(star.getName()+" -> "+properName);
                    star.setD2KName(properName);
                    star.setWikipediaURL(wikiURL);
                    found = true;
                    matched++;
                    break;
                }
            if (!found)
                for (SimbadStar star : mStars)
                {
                    if (isIdentMatch(idents, star))
                    {
                        System.out.println(star.getName()+" -> "+properName);
                        star.setD2KName(properName);
                        star.setWikipediaURL(wikiURL);
                        found = true;
                        matched++;
                        break;
                    }
                }
            if (!found)
            {
                System.out.print(properName+" did not find as");
                for (String name : idents)
                    System.out.print(",  "+name);
                System.out.println();
                unmatched++;
            }
        }
        rdr.close();
        System.out.println("Splice matched "+matched+", missed "+unmatched);
    }

    private static void expandIdents(Set<String> idents)
    {
        for (String ident : idents.toArray(new String[0]))
        {
            expandIdent(idents, ident);
        }
    }

    private static void expandIdent(Set<String> idents, String ident)
    {
        int o = isAlphaNumNoSpace(ident);
        if (o > 0)
        {
            String newIdent = ident.substring(0, o) + " " + ident.substring(o);
            idents.add(newIdent);
        }
        o = isAlphaNumSpace(ident);
        if (o > 0)
        {
            String newIdent = ident.substring(0, o).trim() + ident.substring(o).trim();
            idents.add(newIdent);
        }
        if (ident.toLowerCase().startsWith("gliese"))
        {
            idents.add("GJ" + ident.substring(6).trim());
            idents.add("GJ " + ident.substring(6).trim());
        }
        o = ident.indexOf('/');
        if (o > 0)
        {
            idents.add(ident.substring(0, o).trim());
            idents.add(ident.substring(o+1).trim());
            int o2 = ident.indexOf(' ');
            if ((o2 > 0) && (o2 < o))
            {
                idents.add(ident.substring(0, o2).trim() + ident.substring(o2, o).trim());
                idents.add(ident.substring(0, o2).trim() + ident.substring(o + 1).trim());
                idents.add(ident.substring(0, o2).trim() + " " + ident.substring(o2, o).trim());
                idents.add(ident.substring(0, o2).trim() + " " + ident.substring(o + 1).trim());
            }
        }
        o = ident.indexOf('+');
        if (o > 0)
        {
            String id1 = ident.substring(0, o).trim();
            String id2 = ident.substring(o+1).trim();
            if (isAlphaNumNoSpace(id1) >= 0)
                expandIdent(idents, id1);
            if (isAlphaNumNoSpace(id2) >= 0)
                expandIdent(idents, id2);
        }
        if (ident.endsWith(" A"))
            idents.add(ident.substring(0, ident.length() - 2).trim());
    }
    
    private static Pattern ALPHA_NUM_NO_SPACE = Pattern.compile("[A-Za-z]*[0-9]*");

    private static int isAlphaNumNoSpace(String ident)
    {
        if (!ALPHA_NUM_NO_SPACE.matcher(ident).matches())
            return -1;
        for (int i = 0; i < ident.length(); i++)
            if (Character.isDigit(ident.charAt(i)))
                return i;
        return -1;
    }

    private static Pattern ALPHA_NUM_SPACE = Pattern.compile("[A-Za-z]* *[0-9]*");

    private static int isAlphaNumSpace(String ident)
    {
        if (!ALPHA_NUM_SPACE.matcher(ident).matches())
            return -1;
        for (int i = 0; i < ident.length(); i++)
            if (Character.isWhitespace(ident.charAt(i)))
                return i;
        return -1;
    }

    private static boolean isIdentMatch(Set<String> idents, SimbadStar star)
    {
        if (StringArrayUtils.containsIgnoreCase(star.getName(), idents))
            return true;
        if (StringArrayUtils.containsIgnoreCase(star.getD2KName(), idents))
            return true;
        return StringArrayUtils.isIntersectionIgnoreCase(idents, star.getIdents());
    }
    
    private static String[] FIXED = {
        "0   |Sol                      |Star                                             |00 00 00.00000 +00 00 00.0000|  00.00|10.51 |G2V",
    };

    private static void readData(String fname, int base) throws Exception
    {
        File simbadInput = new File(BASE+"\\"+fname+".txt");
        if (base == 0)
        {
            mStars = new ArrayList<SimbadStar>();
            mStarIndex = new HashMap<String, SimbadStar>();
            mStarIDIndex = new HashMap<Integer, SimbadStar>();
            for (String fixed : FIXED)
            {
                SimbadStar star = parseSimbad(fixed, true);
                star.setAbsMag(10.51);
                mStars.add(star);
                mStarIndex.put(star.getName(), star);
                mStarIDIndex.put(star.getStarID(), star);
            }
        }
        SimbadStar star;
        int state = 0;
        BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream(simbadInput), "UTF-8"));
        for (;;)
        {
            String inbuf = rdr.readLine();
            if (inbuf == null)
                break;
            switch (state)
            {
                case 0: // looking for start
                    if (inbuf.indexOf("-------------------------") >= 0)
                        state = 1;
                    break;
                case 1: // looking for end
                    if (inbuf.indexOf("================") >= 0)
                        state = 2;
                    else
                    {
                        star = parseSimbad(inbuf, true);
                        if (star != null)
                        {
                            star.setStarID(star.getStarID() + base);
                            mStars.add(star);
                            mStarIndex.put(star.getName(), star);
                            mStarIDIndex.put(star.getStarID(), star);
                        }
                    }
                    break;
                case 2: // skipping to end
                    break;
            }
        }   
        rdr.close();
        
        for (Iterator<SimbadStar> i = mStars.iterator(); i.hasNext(); )
        {
            star = i.next();
            String spectrum = star.getSpectrum();
            if (StringUtils.isTrivial(spectrum))
            {
                System.out.println(star.getStarID()+" has no spectrum");
                i.remove();
                continue;
            }
            int o = spectrum.indexOf('+');
            if (o > 0)
                spectrum = spectrum.substring(0, o);
            if (spectrum.startsWith(">"))
                spectrum = spectrum.substring(1);
            spectrum = spectrum.toUpperCase();
            if (spectrum.startsWith("D"))
                spectrum = "M9D";
            int s = getSpectrum(spectrum);
            if (s < 0)
            {
                System.out.println(star.getStarID()+" unknown spectrum "+star.getSpectrum());
                i.remove();
                continue;
            }
            if (StringUtils.isTrivial(star.getName()) && (star.getDistance() > 100))
            {
                System.out.println(star.getStarID()+" bad distance "+star.getDistance());
                i.remove();
                continue;
            }
            String name = star.getName().trim();
            if  (name.startsWith("V"))
                name = name.substring(1).trim();
            if  (name.startsWith("*"))
                name = name.substring(1).trim();
            if  (name.startsWith("NAME"))
                name = name.substring(4).trim();
            star.setD2KName(name);
            String d2kSpectra = SPECTRA.substring(s, s+1);
            if ((spectrum.length() > 1) && Character.isDigit(spectrum.charAt(1)))
                d2kSpectra += spectrum.substring(1, 2);
            if (spectrum.contains("IV"))
                d2kSpectra += "IV";
            else if (spectrum.contains("VI"))
                d2kSpectra += "VI";
            else if (spectrum.contains("D") || spectrum.contains("d"))
                d2kSpectra += "D";
            else if (spectrum.contains("V"))
                d2kSpectra += "V";
            else if (spectrum.contains("III"))
                d2kSpectra += "III";
            else if (spectrum.contains("II"))
                d2kSpectra += "II";
            else if (spectrum.contains("I"))
                d2kSpectra += "I";
            star.setD2KSpectra(d2kSpectra);
            star.setD2KX(star.getX());
            star.setD2KY(star.getY());
            star.setD2KZ(star.getZ());
            star.setD2KQuadrant(getQuadrant(star));
        }
        System.out.println("Read "+mStars.size()+" stars");
        FileWriter wtr = new FileWriter("c:\\temp\\data\\chview2\\simbad\\stats.csv");
        for (SimbadStar s : mStars)
            wtr.append(s.getRA()+","+s.getDec()+","+s.getParalax()+","+s.getX()+","+s.getY()+","+s.getZ()+"\r\n");
        wtr.close();
    }

    private static void readExtraData(String fname, String catalog, int base) throws Exception
    {
        File simbadInput = new File(BASE+"\\"+fname+"-"+catalog+".txt");
        int state = 0;
        BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream(simbadInput), "UTF-8"));
        for (;;)
        {
            String inbuf = rdr.readLine();
            if (inbuf == null)
                break;
            switch (state)
            {
                case 0: // looking for start
                    if (inbuf.indexOf("-------------------------") >= 0)
                        state = 1;
                    break;
                case 1: // looking for end
                    if (inbuf.indexOf("================") >= 0)
                        state = 2;
                    else
                    {
                        SimbadStar star = parseSimbad(inbuf, false);
                        if (star != null)
                        {
                            star.setStarID(star.getStarID() + base);
                            addName(star, catalog);
                        }
                    }
                    break;
                case 2: // skipping to end
                    break;
            }
        }        
        rdr.close();
    }
    
    private static void addName(SimbadStar star, String catalog)
    {
        if ("NAME".equals(catalog) && star.getName().startsWith("NAME "))
            star.setName(star.getName().substring(5));
        SimbadStar base = mStarIDIndex.get(star.getStarID());
        if (base == null)
        {
            System.out.println("Can't find base for #"+star.getStarID()+", name="+star.getName());
            return;
        }
        if (!base.getName().equals(star.getName()))
            base.getCatalogNames().put(catalog, star.getName());
        else if (star.getName().startsWith(catalog))
            base.getCatalogNames().put(catalog, star.getName());
        base.getIdents().add(star.getName());
        mStarIndex.put(star.getName(), base);
    }
    
    /*
     *   #  
     *   |         identifier          
     *   |                       typ                       
     *   |  coord1 (ICRS,J2000/2000)   
     *   |  plx  
     *   |Mag V 
     *   |   spec. type    

     *     18956
     *     |V* V453 And                  
     *     |Variable of BY Dra type                          
     *     |23 21 36.51306 +44 05 52.3818
     *     |  46.46
     *     | 7.36 
     *     |K1V              
     */
    private static SimbadStar parseSimbad(String inbuf, boolean domag)
    {
        StringTokenizer st = new StringTokenizer(inbuf, "|");
        int count = st.countTokens();
        int ident = IntegerUtils.parseInt(st.nextToken().trim());
        String identifier = st.nextToken().trim();
        if (count > 7)
            st.nextToken(); // typ
        st.nextToken(); // all types
        String coord1 = st.nextToken();
        int o = Math.max(coord1.indexOf('-'), coord1.indexOf('+'));
        if (o < 0)
        {
            System.out.println("Bad coord: "+inbuf);
            return null;
        }
        StringTokenizer lonST = new StringTokenizer(coord1.substring(0, o).trim(), " ");
        double lonHours = DoubleUtils.parseDouble(lonST.nextToken());
        double lonMin = lonST.hasMoreTokens() ? DoubleUtils.parseDouble(lonST.nextToken()) : 0;
        double lonSec = lonST.hasMoreTokens() ? DoubleUtils.parseDouble(lonST.nextToken()) : 0;
        double lon = lonHours + lonMin/60 + lonSec/60/60;
        StringTokenizer latST = new StringTokenizer(coord1.substring(o).trim(), " ");
        double latDeg = DoubleUtils.parseDouble(latST.nextToken());
        double latMin = latST.hasMoreTokens() ? DoubleUtils.parseDouble(latST.nextToken()) : 0;
        double latSec = latST.hasMoreTokens() ? DoubleUtils.parseDouble(latST.nextToken()) : 0;
        double lat = latDeg;
        if (lat >= 0)
            lat += latMin/60 + latSec/60/60;
        else
            lat -= latMin/60 + latSec/60/60;
        String splx = st.nextToken().trim();
        double plx = DoubleUtils.parseDouble(splx);
        if ((plx <= 0) && (inbuf.indexOf("Sol") < 0))
        {
            System.out.println("Bad parallax '"+splx+"' in: "+inbuf);
            return null;
        }
        double mag = 0;
        if (domag)
            mag = DoubleUtils.parseDouble(st.nextToken().trim());
        String spectra = st.nextToken().trim();
        SimbadStar star = new SimbadStar();
        star.setRA(lon);
        star.setDec(lat);
        star.setParalax(plx);
        star.setStarID(ident);
        if (identifier.length() > 25)
        {
            star.setName(identifier.substring(0, 25).trim());
            star.getIdents().add(identifier.substring(25).trim());
        }
        else
            star.setName(identifier);
        star.setAppMag(mag);
        star.setSpectrum(spectra);
        if (star.getParalax() == 0)
        {
            star.setX(0);
            star.setY(0);
            star.setZ(0);
        }
        else
        {
            // derived values
            // http://answers.yahoo.com/question/index?qid=20080117234726AAzJxjb
            double arcsecs = star.getParalax()/1000;
            double distParsecs = 1/arcsecs;
            double distLY = distParsecs*3.261631;
            star.setDistance(distLY);
            // http://www.shodor.org/refdesk/Resources/Applications/AstronomicalCoordinates/
            double q = star.getRA()/12*Math.PI;
            double f = (90 - star.getDec())/180*Math.PI;
            double x = distLY*Math.cos(q)*Math.sin(f);
            double y = distLY*Math.sin(q)*Math.sin(f);
            double z = distLY*Math.cos(f);
            star.setX(x);
            star.setY(y);
            star.setZ(z);
        }
        return star;
    }

    private static void writeDataToDisk() throws IOException, IntrospectionException
    {
        List<StarBean> d2kStars = makeD2KStars();
        File d2kOutput = new File("D:\\temp\\data\\chview2\\simbad\\d2k.csv");
        CSVLogic.toCSV(d2kOutput, StarBean.class, d2kStars);
    }

    private static List<StarBean> makeD2KStars()
    {
        List<StarBean> d2kStars = new ArrayList<StarBean>();
        long oid = 1;
        for (SimbadStar star : mStars)
        {
            StarBean parent = null;
            for (SimbadStar sec = star; sec != null; sec = sec.getSecondary())
            {
                StarBean d2kStar = new StarBean();
                d2kStar.setOID(oid++);
                d2kStar.setName(sec.getD2KName());
                if (sec.getCatalogNames().containsKey("NAME"))
                    d2kStar.setCommonName(sec.getCatalogNames().get("NAME"));
                if (sec.getCatalogNames().containsKey("HIP"))
                    d2kStar.setHIPName(sec.getCatalogNames().get("HIP"));
                if (sec.getCatalogNames().containsKey("GJ"))
                    d2kStar.setGJName(sec.getCatalogNames().get("GJ"));
                if (sec.getCatalogNames().containsKey("HD"))
                    d2kStar.setHDName(sec.getCatalogNames().get("HD"));
                if (sec.getCatalogNames().containsKey("HR"))
                    d2kStar.setHRName(sec.getCatalogNames().get("HR"));
                if (sec.getCatalogNames().containsKey("SAO"))
                    d2kStar.setSAOName(sec.getCatalogNames().get("SAO"));
                if (sec.getCatalogNames().containsKey("2MASS"))
                    d2kStar.setTwoMassName(sec.getCatalogNames().get("2MASS"));
                d2kStar.setQuadrant(sec.getD2KQuadrant());
                d2kStar.setX(sec.getD2KX());
                d2kStar.setY(sec.getD2KY());
                d2kStar.setZ(sec.getD2KZ());
                d2kStar.setSpectra(sec.getD2KSpectra());
                d2kStar.setAbsMag(getAbsMag(sec));
                if (sec != star)
                    d2kStar.setParent(parent.getOID());
                else
                    parent = d2kStar;
                if (StringUtils.isTrivial(sec.getWikipediaURL()))
                    d2kStar.setWikipediaURL("");
                else
                    d2kStar.setWikipediaURL(sec.getWikipediaURL());
                if (StringUtils.isTrivial(sec.getSimbadURL()))
                    d2kStar.setSimbadURL("");
                else
                    d2kStar.setSimbadURL(sec.getSimbadURL());
                d2kStars.add(d2kStar);
            }
        }
        return d2kStars;
    }

    private static double getAbsMag(SimbadStar sec)
    {
        double d = sec.getParalax()/1000;
        double absMag = sec.getAppMag() + 5*(1 + Math.log10(d));
        if (Double.isInfinite(absMag) || Double.isNaN(absMag))
        {
            System.out.println("Magnitude problem: "+sec.getName()+" plx="+sec.getParalax()+", appMag="+sec.getAppMag()+", absMag="+absMag);
            return sec.getAppMag();
        }
        else
            return absMag;
    }
    
    public static final String SPECTRA = "OBAFGKMLTY";
    
    private static int getSpectrum(String spectrum)
    {
        if (spectrum.equals("~"))
            return 6;
        char s = spectrum.charAt(0);
        int i = SPECTRA.indexOf(s);
        if (i >= 0)
            return i;
        if (spectrum.startsWith("D"))
            return getSpectrum(spectrum.substring(1));
        if (spectrum.startsWith("SD:"))
            return getSpectrum(spectrum.substring(3));
        if (spectrum.startsWith("SD"))
            return getSpectrum(spectrum.substring(2));
        if (spectrum.startsWith("ESD"))
            return getSpectrum(spectrum.substring(3));
        return -1;
    }
    
    public static final String[] CLASSES = {
        "I", "II", "III", "IV", "V", "VI", "D",
    };
    
    private static int getClass(SimbadStar star)
    {
        String spectrum = star.getSpectrum().toUpperCase();
        if (spectrum.contains("IV"))
            return 3;
        else if (spectrum.contains("VI"))
            return 4;
        else if (spectrum.contains("D"))
            return 6;
        else if (spectrum.contains("V"))
            return 4;
        else if (spectrum.contains("III"))
            return 2;
        else if (spectrum.contains("II"))
            return 1;
        else if (spectrum.contains("I"))
            return 0;
        //System.out.println("Unknown class: "+spectrum);
        return 4;
    }
    
    private static String getQuadrant(SimbadStar star)
    {
        return StarLogic.getQuadrant(star.getD2KX(), star.getD2KY(), star.getD2KZ());
    }
    
    public static void main(String[] argv)
    {
        convertData();
    }
}

class SimbadComparator implements Comparator<SimbadStar>
{
    @Override
    public int compare(SimbadStar o1, SimbadStar o2)
    {
        /*
        String s1 = o1.getD2KSpectra();
        String s2 = o2.getD2KSpectra();
        if (s1.charAt(0) != s2.charAt(0))
        {
            int idx1 = Simbad2Logic.SPECTRA.indexOf(s1.charAt(0));
            int idx2 = Simbad2Logic.SPECTRA.indexOf(s2.charAt(0));
            return idx1 - idx2;
        }
        if ((s2.length() > 1) && (s1.length() > 1))
        {
            char idx1 = s1.charAt(1);
            char idx2 = s2.charAt(1);
            return idx1 - idx2;
        }
        */
        String n1 = getGJName(o1);
        String n2 = getGJName(o2);
        int d = compareNames(n1, n2);
        if (d != 0)
            return d;
        n1 = getHDName(o1);
        n2 = getHDName(o2);
        d = compareNames(n1, n2);
        if (d != 0)
            return d;
        n1 = o1.getName();
        n2 = o2.getName();
        d = compareNames(n1, n2);
        if (d != 0)
            return d;
        System.out.println("Nearly a tie for "+o1.getName()+" and "+o2.getName());
        return (int)Math.signum(o2.getAbsMag() - o1.getAbsMag());
    }

    private int compareNames(String n1, String n2)
    {
        if ((n1 == null) || (n2 == null))
            return 0;
        if (n1.equals(n2))
            return 0;
        if (n1.startsWith(n2))
            return 1;
        if (n2.startsWith(n1))
            return -1;
        char e1 = Character.toUpperCase(n1.charAt(n1.length() - 1));
        char e2 = Character.toUpperCase(n2.charAt(n2.length() - 1));
        if ((e1 >= 'A') && (e1 <= 'D') && (e2 >= 'A') && (e2 <= 'D'))
            return e1 - e2;
        return 0;
    }
    
    private String getGJName(SimbadStar star)
    {
        if (star.getCatalogNames().containsKey("GJ"))
            return star.getCatalogNames().get("GJ");
        if (star.getName().startsWith("GJ"))
            return star.getName();
        return null;
    }
    
    private String getHDName(SimbadStar star)
    {
        if (star.getCatalogNames().containsKey("HD"))
            return star.getCatalogNames().get("HD");
        if (star.getName().startsWith("HD"))
            return star.getName();
        return null;
    }
}