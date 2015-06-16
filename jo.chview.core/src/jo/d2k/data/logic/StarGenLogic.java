package jo.d2k.data.logic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarGenParams;
import jo.util.utils.obj.StringUtils;

public class StarGenLogic
{
    public static StarGenParams PARAMS = new StarGenParams();
    
    public static void init()
    {
        RuntimeLogic.getInstance().addPropertyChangeListener("dataSource", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                StarGenParamLogic.loadFromMetadata(PARAMS);
                updatedParams();
            }
        });
        if (RuntimeLogic.getInstance().getDataSource() != null)
        {
            StarGenParamLogic.loadFromMetadata(PARAMS);
            updatedParams();
        }
    }
    
    public static void updatedParams()
    {
        StarLogic.clearCache();        
    }
    
    public static int getSpectrum(String spectrum)
    {
        if (StringUtils.isTrivial(spectrum))
            return StarLogic.SPECTRA.length() - 1;
        char s = spectrum.charAt(0);
        int i = StarLogic.SPECTRA.indexOf(s);        
        return i;
    }
    
    private static int lookup(int[] freq, int idx)
    {
        for (int i = 0; i < freq.length; i++)
            if (idx < freq[i])
                return i;
            else
                idx -= freq[i];
        throw new IllegalStateException("Fell off end of table");
    }
    
    private static double lookup(Random rnd, double[] range)
    {
        int bucket = rnd.nextInt(range.length - 1);
        double low = range[bucket];
        double span = range[bucket + 1] - range[bucket];
        return rnd.nextDouble()*span + low;
    }
    
    public static int genSpectrum(Random rnd)
    {
        return lookup(PARAMS.SPECTRUM_FREQ, rnd.nextInt(1000));                
    }
    
    private static int genQuadPopulation(Random rnd)
    {
        int numStars = (int)(rnd.nextGaussian()*3 + 9.6);
        return numStars;
    }
    
    private static int[] genQuadPopDistribution(Random rnd)
    {
        int[] popDist = new int[7];
        int numStars = genQuadPopulation(rnd);
        while (numStars-- > 0)
        {
            int s = genSpectrum(rnd);
            popDist[s]++;
        }
        return popDist;
    }
    
    public static double genAbsMag(Random rnd, int spectrum)
    {
        try
        {
            return lookup(rnd, PARAMS.ABS_MAG_FREQ[spectrum]);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            System.err.println("Searching for spectrum #"+spectrum+" in array size="+PARAMS.ABS_MAG_FREQ.length);
            throw e;
        }
    }
    
    public static int genNumSecondaries(Random rnd, int spectrum)
    {
        return lookup(PARAMS.SECONDARY_NUM_FREQ[spectrum], rnd.nextInt(1000));
    }
    
    public static int genSecondaryType(Random rnd, int spectrum)
    {
        return lookup(PARAMS.SECONDARY_TYPE_FREQ[spectrum], rnd.nextInt(1000));
    }
    
    public static double genSecondaryDistance(Random rnd)
    {
        double delta = lookup(rnd, PARAMS.SECONDARY_DISTANCE_FREQ)*.707;
        if (rnd.nextBoolean())
            delta = -delta;
        return delta;
    }
    
    public static List<StarBean> genQuadrant(String quad, List<StarBean> known)
    {
        if (!PARAMS.GENERATE)
            return new ArrayList<StarBean>();
        List<StarBean> generated = generateQuadStars(quad);
        for (StarBean star : known)
        {
            if (star.getParent() <= 0)
                removeStar(generated, getSpectrum(star.getSpectra()));
        }
//        DebugUtils.trace(off+" surplus stars generated for "+quad);
//        for (int i = known.size(); i < stars.size(); i++)
//        {
//            StarBean star = stars.get(i);
//            DebugUtils.trace("  "+star.getName()+" "+star.getX()+","+star.getY()+","+star.getZ());
//        }
        return generated;
    }

    private static void removeStar(List<StarBean> stars, int spectrum)
    {
        for (int i = stars.size() - 1; i >= 0; i--)
        {
            StarBean star = stars.get(i);
            if (getSpectrum(star.getSpectra()) == spectrum)
            {
                stars.remove(i);
                return;
            }
        }
    }

    private static List<StarBean> generateQuadStars(String quad)
    {
        List<StarBean> stars = new ArrayList<StarBean>(); 
        long seed = quad.charAt(0) + 256*quad.charAt(1) + 256*256*quad.charAt(2);
        Random rnd = new Random(seed);
        int[] popDist = genQuadPopDistribution(rnd);
        adjustQuadPopDistribution(popDist, quad);
        int off = 0;
        for (int s = 0; s < popDist.length; s++)
            while (popDist[s]-- > 0)
            {
                StarBean star = new StarBean();
                seed += 256L*256L*256L;
                star.setOID(seed);
                star.setGenerated(true);
                String name = PARAMS.PREFIX+" "+quad+" ";
                if (off < PARAMS.GREEK_NAMES.length)
                    name += PARAMS.GREEK_NAMES[off];
                else
                    name += Integer.toString(off + 1);
                star.setName(name);
                //DebugUtils.trace(star.getName()+" -> "+Long.toHexString(star.getOID()));
                star.setQuadrant(quad);
                star.setX(StarLogic.getOrd(quad.charAt(0)) + (rnd.nextDouble() - .5)*StarLogic.QUAD_SIZE*.95);
                star.setY(StarLogic.getOrd(quad.charAt(1)) + (rnd.nextDouble() - .5)*StarLogic.QUAD_SIZE*.95);
                star.setZ(StarLogic.getOrd(quad.charAt(2)) + (rnd.nextDouble() - .5)*StarLogic.QUAD_SIZE*.95);
                String spectra = StarLogic.SPECTRA.charAt(s)+Integer.toString(rnd.nextInt(10));
                spectra += makeClass(s, rnd);
                star.setSpectra(spectra);
                star.setAbsMag(genAbsMag(rnd, s));
                double dpc = StarExtraLogic.distance(star, 0, 0, 0)/3.26;
                double appMag = StarExtraLogic.getAppMag(star.getAbsMag(), dpc);
                if (appMag < 6)
                {
                    //System.out.println(star.getName()+" "+star.getSpectra()+" visible from Earth, appmag="+appMag
                    //        +", absmag="+star.getAbsMag()+", dpc="+dpc  );
                    continue;
                }
                stars.add(star);
                off++;
                int numSec = genNumSecondaries(rnd, s);
                for (int i = 0; i < numSec; i++)
                {
                    StarBean sec = new StarBean();
                    seed += 256L*256L*256L;
                    sec.setOID(seed);
                    sec.setGenerated(true);
                    sec.setName(star.getName()+" "+"ABCDE".charAt(i));
                    //DebugUtils.trace(sec.getName()+" -> "+Long.toHexString(sec.getOID()));
                    sec.setQuadrant(quad);
                    sec.setX(star.getX() + genSecondaryDistance(rnd));
                    sec.setY(star.getY() + genSecondaryDistance(rnd));
                    sec.setZ(star.getZ() + genSecondaryDistance(rnd));
                    int ss = genSecondaryType(rnd, s);
                    String secSpec = StarLogic.SPECTRA.charAt(ss)+Integer.toString(rnd.nextInt(10));
                    secSpec += makeClass(ss, rnd);
                    sec.setSpectra(secSpec);
                    sec.setAbsMag(genAbsMag(rnd, ss));
                    dpc = StarExtraLogic.distance(sec, 0, 0, 0)/3.26;
                    appMag = StarExtraLogic.getAppMag(sec.getAbsMag(), dpc);
                    if (appMag < 6)
                    {
                        //System.out.println(star.getName()+" "+star.getSpectra()+" visible from Earth, appmag="+appMag
                        //        +", absmag="+star.getAbsMag()+", dpc="+dpc  );
                        continue;
                    }
                    sec.setParent(star.getOID());
                    sec.setParentRef(star);
                    star.getChildren().add(sec);
                    stars.add(sec);
                }
            }
        return stars;
    }

    private static void adjustQuadPopDistribution(int[] popDist, String quad)
    {
        if (inExclusionZone(quad))
        {   // no additionals for earth
            for (int i = 0; i < popDist.length; i++)
                popDist[i] = 0;
        }
        if (Pattern.matches("[0aA][0aA][0aA]", quad))
        {   // if nearby, no bright stars
            for (int i = 0; i < popDist.length; i++)
                if (i < 5)
                    popDist[i] = 0;
        }
    }

    private static boolean inExclusionZone(String quad)
    {
        if (PARAMS.EXCLUSION_ZONE <= 0)
            return false;
        if (Math.abs(StarLogic.getOrd(quad.charAt(0))) > PARAMS.EXCLUSION_ZONE)
            return false;
        if (Math.abs(StarLogic.getOrd(quad.charAt(1))) > PARAMS.EXCLUSION_ZONE)
            return false;
        if (Math.abs(StarLogic.getOrd(quad.charAt(2))) > PARAMS.EXCLUSION_ZONE)
            return false;
        return true;
    }

    public static String makeClass(int s, Random rnd)
    {
        int clazz = lookup(PARAMS.SPECTRUM_CLASS_FREQ[s], rnd.nextInt(1000));
        return PARAMS.CLASS_NAME[clazz];
    }

    public static int makeSpectra(Random rnd)
    {
        int s = lookup(PARAMS.SPECTRUM_FREQ, rnd.nextInt(1000));
        return s;
    }
}
