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
import java.util.Arrays;
import java.util.Collections;
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
public class SimbadLogic
{
    private static Map<String,SimbadStar> mStarIndex;
    private static List<SimbadStar> mStars;
    
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
    
    private static void groupData()
    {
        for (int i = 0; i < mStars.size() - 1; i++)
            for (int j = i + 1; j < mStars.size(); j++)
            {
                SimbadStar star1 = mStars.get(i);
                SimbadStar star2 = mStars.get(j);
                if ((star1.getRA() == star2.getRA()) && (star1.getDec() == star2.getDec()))
                    System.out.println("IDENTICAL! "+star1.getName()+" & "+star2.getName());
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
    
    private static boolean biggerThan(SimbadStar star1, SimbadStar star2)
    {
        int s1 = getSpectrum(star1);
        int s2 = getSpectrum(star2);
        if (s1 != s2)
            return s1 < s2;
        return star1.getAbsMag() > star2.getAbsMag();
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
        Map<Integer, int[]> quadStars = new HashMap<Integer, int[]>();
        Map<Integer, List<Double>> quadMags = new HashMap<Integer, List<Double>>();
        Map<String, Double> quadPop = new HashMap<String, Double>();
        int[][] depthFreq = new int[7][4];
        int[][] depthType = new int[7][8];
        int[][] depthClass = new int[7][8];
        List<Double> depthDist = new ArrayList<Double>();
        for (SimbadStar star : mStars)
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
            for (SimbadStar ss = star.getSecondary(); ss != null; ss = ss.getSecondary())
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
        File siInput = new File("C:\\temp\\data\\chview2\\simbad\\simbad_idents.txt");
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
        File f = new File("c:\\temp\\data\\chview2\\simbad\\stars_in_fiction.txt");
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
    
    private static String SOL = "0    |Sol                          |Star                                             |00 00 00.00000 +00 00 00.0000|  00.00|10.51 |G2V              ";

    private static void readData() throws Exception
    {
        File simbadInput = new File("C:\\temp\\data\\chview2\\simbad\\simbad.txt");
        mStars = new ArrayList<SimbadStar>();
        mStarIndex = new HashMap<String, SimbadStar>();
        parseSimbad(SOL);
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
                        parseSimbad(inbuf);
                    break;
                case 2: // skipping to end
                    break;
            }
        }        
        rdr.close();
        
        for (Iterator<SimbadStar> i = mStars.iterator(); i.hasNext(); )
        {
            SimbadStar star = i.next();
            if (StringUtils.isTrivial(star.getSpectrum()))
            {
                //System.out.println(star.getStarID()+" has no spectrum");
                i.remove();
                continue;
            }
            int s = getSpectrum(star);
            if (s < 0)
            {
                //System.out.println(star.getStarID()+" unknown spectrum "+star.getSpectrum());
                i.remove();
                continue;
            }
            if (StringUtils.isTrivial(star.getName()) && (star.getDistance() > 100))
            {
                //System.out.println(star.getStarID()+" bad distance "+star.getDistance());
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
            if ((star.getSpectrum().length() > 1) && Character.isDigit(star.getSpectrum().charAt(1)))
                d2kSpectra += star.getSpectrum().substring(1, 2);
            if (star.getSpectrum().contains("IV"))
                d2kSpectra += "IV";
            else if (star.getSpectrum().contains("VI"))
                d2kSpectra += "VI";
            else if (star.getSpectrum().contains("D") || star.getSpectrum().contains("d"))
                d2kSpectra += "D";
            else if (star.getSpectrum().contains("V"))
                d2kSpectra += "V";
            else if (star.getSpectrum().contains("III"))
                d2kSpectra += "III";
            else if (star.getSpectrum().contains("II"))
                d2kSpectra += "II";
            else if (star.getSpectrum().contains("I"))
                d2kSpectra += "I";
            star.setD2KSpectra(d2kSpectra);
            star.setD2KX(star.getX());
            star.setD2KY(star.getY());
            star.setD2KZ(star.getZ());
            star.setD2KQuadrant(getQuadrant(star));
        }
        System.out.println("Read "+mStars.size()+" stars");
        FileWriter wtr = new FileWriter("c:\\temp\\data\\chview2\\simbad\\stats.csv");
        for (SimbadStar star : mStars)
            wtr.append(star.getRA()+","+star.getDec()+","+star.getParalax()+","+star.getX()+","+star.getY()+","+star.getZ()+"\r\n");
        wtr.close();
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
    private static void parseSimbad(String inbuf)
    {
        SimbadStar star = doParseSimbad(inbuf);
        mStars.add(star);
        mStarIndex.put(star.getName(), star);
    }

    public static SimbadStar doParseSimbad(String inbuf)
    {
        StringTokenizer st = new StringTokenizer(inbuf, "|");
        int ident = IntegerUtils.parseInt(st.nextToken().trim());
        String name = st.nextToken().trim();
        st.nextElement();
        String coord = st.nextToken();
        int o = Math.max(coord.indexOf('-'), coord.indexOf('+'));
        if (o < 0)
        {
            System.out.println("Bad coord: "+inbuf);
            return null;
        }
        StringTokenizer lonST = new StringTokenizer(coord.substring(0, o).trim(), " ");
        double lonHours = DoubleUtils.parseDouble(lonST.nextToken());
        double lonMin = lonST.hasMoreTokens() ? DoubleUtils.parseDouble(lonST.nextToken()) : 0;
        double lonSec = lonST.hasMoreTokens() ? DoubleUtils.parseDouble(lonST.nextToken()) : 0;
        double lon = lonHours + lonMin/60 + lonSec/60/60;
        StringTokenizer latST = new StringTokenizer(coord.substring(o).trim(), " ");
        double latDeg = DoubleUtils.parseDouble(latST.nextToken());
        double latMin = latST.hasMoreTokens() ? DoubleUtils.parseDouble(latST.nextToken()) : 0;
        double latSec = latST.hasMoreTokens() ? DoubleUtils.parseDouble(latST.nextToken()) : 0;
        double lat = latDeg;
        if (lat >= 0)
            lat += latMin/60 + latSec/60/60;
        else
            lat -= latMin/60 + latSec/60/60;
        double plx = DoubleUtils.parseDouble(st.nextToken().trim());
        double mag = DoubleUtils.parseDouble(st.nextToken().trim());
        String spectra = st.nextToken().trim();
        SimbadStar star = new SimbadStar();
        star.setRA(lon);
        star.setDec(lat);
        star.setParalax(plx);
        star.setStarID(ident);
        star.setName(name);
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
        File d2kOutput = new File("C:\\temp\\data\\chview2\\simbad\\d2k.csv");
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
                d2kStar.setQuadrant(sec.getD2KQuadrant());
                d2kStar.setX(sec.getD2KX());
                d2kStar.setY(sec.getD2KY());
                d2kStar.setZ(sec.getD2KZ());
                d2kStar.setSpectra(sec.getD2KSpectra());
                d2kStar.setAbsMag(sec.getAppMag());
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

    private static final String SPECTRA = "OBAFGKM";
    
    private static int getSpectrum(SimbadStar star)
    {
        String spectrum = star.getSpectrum().toUpperCase();
        return getSpectrum(spectrum);
    }
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
    
    private static int getClass(SimbadStar star)
    {
        String spectrum = star.getSpectrum().toUpperCase();
        if (spectrum.endsWith("VI"))
            return 6;
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
        if (spectrum.endsWith("D"))
            return 7;
        return 0;
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
