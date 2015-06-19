package jo.d2k.data.logic.stargen;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.StringTokenizer;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.StarExtraLogic;
import jo.d2k.data.logic.StarGenLogic;
import jo.d2k.data.logic.StarLogic;
import jo.d2k.data.logic.stargen.data.BodyBean;
import jo.d2k.data.logic.stargen.data.StellarSystem;
import jo.d2k.data.logic.stargen.data.SunBean;
import jo.d2k.data.logic.stargen.logic.ConstLogic;
import jo.d2k.data.logic.stargen.logic.SolSystemLogic;
import jo.d2k.data.logic.stargen.logic.StargenLogic;
import jo.util.beans.WeakCache;
import jo.util.utils.obj.LongUtils;

public class SystemLogic
{
    private static WeakCache<StarBean, SunBean> mSystemCache = new WeakCache<StarBean, SunBean>();
    
    public static SunBean generateSystem(StarBean star, StarBean companion)
    {
        SunBean sun = mSystemCache.get(star);
        if (sun != null)
            return sun;
        long seed = makeSeed(star.getQuadrant()+star.getName()+star.getSpectra());
        String spectra = normalizeSpectra(star.getSpectra(), seed);
        StellarSystem system = new StellarSystem();
        system.setRndSeed(seed);
        system.setSun(new SunBean());
        system.getSun().setStar(star);
        system.getSun().setCompanion(companion);
        system.setUseSeedSystem(false);
        system.setSysNo(1);
        system.setSystemName(star.getName());
        system.setOuterPlanetLimit(0); // for double stars
        system.setDoGases(true);
        system.setDoMoons(true);
        system.getSun().setName(star.getName());
        system.getSun().setMass(StarExtraLogic.calcMassFromTypeAndClass(spectra));
        system.getSun().setLuminosity(StarExtraLogic.calcLuminosityFromTypeAndClass(spectra));
        system.getSun().setRadius(StarExtraLogic.calcRadiusFromTypeAndClass(spectra));
        if (companion != null)
        {
            /*
             * The following is Holman & Wiegert's equation 1 from
             * Long-Term Stability of Planets in Binary Systems The
             * Astronomical Journal, 117:621-628, Jan 1999
             */
            double m1 = system.getSun().getMass();
            String cspectra = normalizeSpectra(companion.getSpectra(), seed+1);
            double m2 = StarExtraLogic.calcMassFromTypeAndClass(cspectra);
            double mu = m2 / (m1 + m2);
            double e = 0; // eccentricity, make it a circle
            double d = StarExtraLogic.distance(star, companion)*63241.1; // conver to AU
            double a = d/2; // semi-major axis

            system.setOuterPlanetLimit((0.464 + (-0.380 * mu) + (-0.631 * e)
                    + (0.586 * mu * e) + (0.150 * ConstLogic.pow2(e)) + (-0.198
                    * mu * ConstLogic.pow2(e)))
                    * a);
        }
        if ((star.getX() == 0) && (star.getY() == 0) && (star.getZ() == 0))
        {
            system.setSeedSystem(SolSystemLogic.solar_system);
            system.setUseSeedSystem(true);
        }
        StargenLogic.generate_stellar_system(system);
        mSystemCache.put(star, system.getSun());
        return system.getSun();
    }
    
    private static String normalizeSpectra(String spectra, long seed)
    {
        if (spectra.length() >= 3)
            return spectra;
        Random rnd = new Random(seed);
        int s = StarExtraLogic.getSpectra(spectra);
        if (s < 0)
            s = StarGenLogic.makeSpectra(rnd);
        String norm = StarLogic.SPECTRA.substring(s, s+1);
        int idx = StarExtraLogic.getIndex(spectra);
        if ((idx < 0) || (idx > 9))
            idx = rnd.nextInt(10);
        norm += (char)(idx + '0');
        int c = StarExtraLogic.getClassOff(spectra);
        if (c == 0)
            norm += StarGenLogic.makeClass(s, rnd);
        else
            norm += StarGenLogic.PARAMS.CLASS_NAME[c];
        return norm;
    }

    public static SunBean generateSystem(String name, String spectra)
    {
        long seed = makeSeed(name+spectra);
        StellarSystem system = new StellarSystem();
        system.setRndSeed(seed);
        system.setSun(new SunBean());
        system.setUseSeedSystem(false);
        system.setSysNo(1);
        system.setSystemName(name);
        system.setOuterPlanetLimit(0); // for double stars
        system.setDoGases(true);
        system.setDoMoons(true);
        StargenLogic.generate_stellar_system(system);
        return system.getSun();
    }
    
    private static long makeSeed(String txt)
    {
        long seed = 0;
        for (int i = 0; i < txt.length(); i++)
        {
            char c = txt.charAt(i);
            seed ^= (c<<(8*(i%4)));
        }
        return seed;
    }
    
    public static SunBean getSun(BodyBean body)
    {
        while (!(body instanceof SunBean))
        {
            body = body.getParent();
        }
        return (SunBean)body;
    }
    
    public static BodyBean findByOID(BodyBean body, long oid)
    {
        if (body.getOID() == oid)
            return body;
        for (BodyBean child = body.getFirstChild(); child != null; child = child.getNextBody())
        {
            BodyBean hit = findByOID(child, oid);
            if (hit != null)
                return hit;
        }
        return null;
    }
    
    public static String getURI(BodyBean body)
    {
        SunBean sun = getSun(body);
        StringBuffer uri = new StringBuffer("body://");
        uri.append(Long.toHexString(sun.getStar().getOID()));
        uri.append("@");
        uri.append(sun.getStar().getQuadrant());
        if (sun.getCompanion() != null)
        {
            uri.append("/");
            uri.append(Long.toHexString(sun.getCompanion().getOID()));
            uri.append("@");
            uri.append(sun.getCompanion().getQuadrant());
        }
        uri.append("/");
        uri.append(body.getOID());
        return uri.toString();
    }
    
    public static BodyBean getByURI(String uri)
    {        
        try
        {
            URI u = new URI(uri);
            String primaryURI = "star://"+u.getUserInfo()+"@"+u.getHost();
            StarBean primary = StarLogic.getByURI(primaryURI);
            StringTokenizer path = new StringTokenizer(u.getPath(), "/");
            StarBean companion = null;
            String seg = path.nextToken();
            if (seg.indexOf('@') >= 0)
            {
                String secondaryURI = "star://"+seg;
                companion = StarLogic.getByURI(secondaryURI);
                seg = path.nextToken();
            }
            long oid = LongUtils.parseLong(seg);
            SunBean system = generateSystem(primary, companion);
            BodyBean body = findByOID(system, oid);
            return body;
        }
        catch (URISyntaxException e)
        {
            throw new IllegalArgumentException("Cannot fathom '"+uri+"'", e);
        }
    }
}
