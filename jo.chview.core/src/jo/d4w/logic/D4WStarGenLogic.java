package jo.d4w.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.StarGenLogic;
import jo.d2k.data.logic.StarLogic;
import jo.util.beans.WeakCache;
import jo.util.utils.MathUtils;

public class D4WStarGenLogic
{
    // values in light years
    public static double GALAXY_DISK_4Q_RADIUS = 50000.0;
    public static double GALAXY_DISK_RADIUS = 60000.0;
    public static double GALAXY_DISK_4Q_THICK = 1000.0;
    public static double GALAXY_DISK_THICK = 1200.0;
    public static double GALAXY_BULGE_RADIUS = 10000.0;
    public static double GALAXY_EARTH_RADIUS = 27200.0;
    
    public static double SPIRAL_ANGLE = 20.0/180.0*Math.PI; // estimates 7 - 25
    public static double SPIRAL_TIGHT = 0.5;
    public static double SPIRAL_NUM = 4;
    public static double SPIRAL_THICK = 2*Math.PI*GALAXY_DISK_4Q_RADIUS/8;
    
    public static double GALAXY_BASE_DENSITY = 0.0487; // stars per ly^3
    
    private static WeakCache<String, List<StarBean>>    mQuadrantCache = new WeakCache<String, List<StarBean>>();
    
    private static double quartileDensity(double v, double q4, double max)
    {
        v = Math.abs(v);
        if (v < q4)
            return MathUtils.interpolate(v, 0, q4, 1.0, .02);
        else if (v > max)
            return 0;
        else
            return MathUtils.interpolate(v, q4, max, .02, 0);
    }
    
    private static double diskDensity(double x, double y, double z)
    {
        double r = Math.sqrt(x*x + y*y);
        double t = Math.atan2(y, x);
        double rDens = quartileDensity(r, GALAXY_DISK_4Q_RADIUS, GALAXY_DISK_RADIUS);
        double tDens = quartileDensity(z, GALAXY_DISK_4Q_THICK, GALAXY_DISK_THICK);
        double aDens;
        if (r == 0)
            aDens = 1;
        else
        {
            double dist = SPIRAL_THICK;
            for (int i = 0; i < SPIRAL_NUM; i++)
            {
                // calculate the floating point approximation for n
                double n = (Math.log(r/SPIRAL_ANGLE)/SPIRAL_TIGHT - t)/(2.0*Math.PI);
                // find the two possible radii for the closest point
                double upper_r = SPIRAL_ANGLE * Math.pow(Math.E, SPIRAL_TIGHT * (t + 2.0*Math.PI*Math.ceil(n)));
                double lower_r = SPIRAL_ANGLE * Math.pow(Math.E, SPIRAL_TIGHT * (t + 2.0*Math.PI*Math.floor(n)));           
                // return the minimum distance to the target point
                dist = Math.min(dist, Math.min(Math.abs(upper_r - r), Math.abs(r - lower_r)));
                t += Math.PI*2/SPIRAL_NUM;
            }
            aDens = MathUtils.interpolate(dist, 0, SPIRAL_THICK, 1.0, 0);
        }
        return rDens*tDens*aDens;
    }
    
    private static double bulgeDensity(double x, double y, double z)
    {
        double r = Math.sqrt(x*x + y*y + z*z);
        double rDens = MathUtils.interpolate(r, 0, GALAXY_BULGE_RADIUS, 1.0, 0.0);
        return rDens;
    }
    
    public static double galaxyDensity(double x, double y, double z)
    {
        double d = Math.max(diskDensity(x, y, z), bulgeDensity(x, y, z));
        return d;
    }
    
    public static double quadDensity(int qx, int qy, int qz)
    {
        return galaxyDensity(qx*StarLogic.QUAD_SIZE, qy*StarLogic.QUAD_SIZE, qz*StarLogic.QUAD_SIZE);
    }
    
    public static int genQuadPopulation(Random rnd, int qx, int qy, int qz)
    {
        double numStars = quadDensity(qx, qy, qz)*StarLogic.QUAD_SIZE*StarLogic.QUAD_SIZE*StarLogic.QUAD_SIZE*GALAXY_BASE_DENSITY;
        numStars *= (1.0 + rnd.nextGaussian()*.1); // +/- 30%
        return (int)numStars;
    }
    
    private static int[] genQuadPopDistribution(Random rnd, int qx, int qy, int qz)
    {
        int[] popDist = new int[7];
        int numStars = genQuadPopulation(rnd, qx, qy, qz);
        while (numStars-- > 0)
        {
            int s = StarGenLogic.genSpectrum(rnd);
            popDist[s]++;
        }
        return popDist;
    }
    
    public static List<StarBean> genQuadrant(String quad)
    {
        List<StarBean> stars = mQuadrantCache.get(quad);
        if (stars != null)
            return stars;
        stars = new ArrayList<StarBean>();
        int[] xyz = quadStrToNum(quad);
        long seed = xyz[0] + 0x10000L*xyz[1] + 0x10000L*0x10000L*xyz[2];
        //DebugUtils.trace(quad+"->"+xyz[0]+","+xyz[1]+","+xyz[2]+"->"+seed);
        Random rnd = new Random(seed);
        int[] popDist = genQuadPopDistribution(rnd, xyz[0], xyz[1], xyz[2]);
        int off = 0;
        for (int s = 0; s < popDist.length; s++)
            while (popDist[s]-- > 0)
            {
                StarBean star = new StarBean();
                seed += 256L*256L*256L;
                star.setOID(seed);
                String name = "Q"+quad+" ";
                if (off < StarGenLogic.PARAMS.GREEK_NAMES.length)
                    name += StarGenLogic.PARAMS.GREEK_NAMES[off];
                else
                    name += Integer.toString(off + 1);
                star.setName(name);
                //DebugUtils.trace(star.getName()+" -> "+Long.toHexString(star.getOID()));
                star.setQuadrant(quad);
                star.setX(xyz[0]*StarLogic.QUAD_SIZE + (rnd.nextDouble() - .5)*StarLogic.QUAD_SIZE*.95);
                star.setY(xyz[1]*StarLogic.QUAD_SIZE + (rnd.nextDouble() - .5)*StarLogic.QUAD_SIZE*.95);
                star.setZ(xyz[2]*StarLogic.QUAD_SIZE + (rnd.nextDouble() - .5)*StarLogic.QUAD_SIZE*.95);
                String spectra = StarLogic.SPECTRA.charAt(s)+Integer.toString(rnd.nextInt(10));
                spectra += StarGenLogic.makeClass(s, rnd);
                star.setSpectra(spectra);
                star.setAbsMag(StarGenLogic.genAbsMag(rnd, s));
                stars.add(star);
                off++;
                int numSec = StarGenLogic.genNumSecondaries(rnd, s);
                StarBean p = star;
                for (int i = 0; i < numSec; i++)
                {
                    StarBean sec = new StarBean();
                    p.getChildren().add(sec);
                    sec.setParentRef(p);
                    p = sec;
                    seed += 256L*256L*256L;
                    sec.setOID(seed);
                    sec.setName(star.getName()+" "+"ABCDE".charAt(i));
                    //DebugUtils.trace(sec.getName()+" -> "+Long.toHexString(sec.getOID()));
                    sec.setQuadrant(quad);
                    sec.setX(star.getX() + StarGenLogic.genSecondaryDistance(rnd));
                    sec.setY(star.getY() + StarGenLogic.genSecondaryDistance(rnd));
                    sec.setZ(star.getZ() + StarGenLogic.genSecondaryDistance(rnd));
                    int ss = StarGenLogic.genSecondaryType(rnd, s);
                    sec.setSpectra(StarLogic.SPECTRA.charAt(ss)+Integer.toString(rnd.nextInt(10)));
                    sec.setAbsMag(StarGenLogic.genAbsMag(rnd, ss));
                    sec.setParent(star.getOID());
                    stars.add(sec);
                }
            }
//        DebugUtils.trace(off+" surplus stars generated for "+quad);
//        for (int i = known.size(); i < stars.size(); i++)
//        {
//            StarBean star = stars.get(i);
//            DebugUtils.trace("  "+star.getName()+" "+star.getX()+","+star.getY()+","+star.getZ());
//        }
        mQuadrantCache.put(quad, stars);
        return stars;
    }
    
    public static int[] quadStrToNum(String quad)
    {
        return new int[] {
                qStrToNum(quad.substring(0, 4)),
                qStrToNum(quad.substring(4, 8)),
                qStrToNum(quad.substring(7, 12)),
        };
    }
    
    private static int qStrToNum(String s)
    {
        int q = Integer.parseInt(s, 16);
        if (q >= 0x8000)
            return q - 0x10000;
        else
            return q;
    }
    
    public static String quadNumToStr(int qx, int qy, int qz)
    {
        return qNumToStr(qx)+qNumToStr(qy)+qNumToStr(qz);
    }
    
    private static String qNumToStr(int q)
    {
        String s = "0000"+Integer.toHexString(q);
        if (s.length() > 4)
            return s.substring(s.length() - 4);
        else
            return s;
    }
    
    public static String quadCoordToStr(double x, double y, double z)
    {
        StringBuffer quad = new StringBuffer();
        quad.append(qCoordToStr(x));
        quad.append(qCoordToStr(y));
        quad.append(qCoordToStr(z));
        return quad.toString();
    }
    
    private static String qCoordToStr(double d)
    {
        int idx = (int)((Math.abs(d)+StarLogic.QUAD_SIZE/2)/StarLogic.QUAD_SIZE);
        return qNumToStr(idx);
    }

}
