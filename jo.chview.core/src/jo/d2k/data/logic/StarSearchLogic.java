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
import java.util.StringTokenizer;

import jo.d2k.data.data.ChViewContextBean;
import jo.d2k.data.data.SearchParams;
import jo.d2k.data.data.StarBean;
import jo.util.geom3d.Point3D;

public class StarSearchLogic
{
    public static void findStars(SearchParams params, ChViewContextBean context)
    {
        params.setDone(false);
        params.setCancel(false);
        params.setPattern(params.getPattern().toLowerCase());
        findInCache(params, context);
        if (isDone(params))
        {
            params.setDone(true);
            return;
        }
        findInRadius(params, context);
        params.setDone(true);
    }
    
    private static boolean isDone(SearchParams params)
    {
        if (params.isCancel())
            return true;
        if (params.isFindFirst() && params.getResults().size() > 0)
            return true;
        return false;
    }
    
    private static boolean match(StarBean star, SearchParams params, boolean filter, String abbr, ChViewContextBean context)
    {
        if (star.getName().toLowerCase().indexOf(params.getPattern()) < 0)
        {
            if (ChViewFormatLogic.getStarName(context, star).toLowerCase().indexOf(params.getPattern()) < 0)
            {
                String name = abbreviate(star.getName());
                if (name.indexOf(abbr) < 0)
                    return false;
            }
        }
        if (filter)
            if (!FilterLogic.isFiltered(context, star, params.getFilter()))
                return false;
        return true;
    }
    
    private static void findInCache(SearchParams params, ChViewContextBean context)
    {
        findInList(params, params.getCache(), context);
    }
    
    private static void findInList(SearchParams params, List<StarBean> stars, ChViewContextBean context)
    {
        if (stars == null)
            return;
        String abbr = abbreviate(params.getPattern());
        boolean anyFilter = FilterLogic.isAnyFilter(params.getFilter());
        for (StarBean star : stars)
            if (match(star, params, anyFilter, abbr, context))
            {
                if (!params.getResults().contains(star))
                    params.getResults().add(star);
                if (isDone(params))
                    return;
            }
    }
    
    private static void findInRadius(SearchParams params, ChViewContextBean context)
    {
        List<String> quads = findQuads(params.getCenter(), params.getSearchRadius());
        params.setTotalSteps(quads.size());
        params.setTakenSteps(0);
        for (String quad : quads)
        {
            findInQuad(params, quad, context);
            if (isDone(params))
                return;
            params.setTakenSteps(params.getTakenSteps() + 1);
        }
    }

    private static void findInQuad(SearchParams params, String quad, ChViewContextBean context)
    {
        List<String> quads = new ArrayList<String>();
        quads.add(quad);        
        List<StarBean> stars;
        if (params.getFilter().getGenerated() == null)
            stars = StarLogic.getByQuadrants(quads);
        else if (params.getFilter().getGenerated())
        {
            stars = StarLogic.getByQuadrants(quads);
            for (Iterator<StarBean> i = stars.iterator(); i.hasNext(); )
                if (!i.next().isGenerated())
                    i.remove();
        }
        else
        {
            stars = StarLogic.getByQuadrants(quads);
            for (Iterator<StarBean> i = stars.iterator(); i.hasNext(); )
                if (i.next().isGenerated())
                    i.remove();
        }
        findInList(params, stars, context);
    }

    private static List<String> findQuads(Point3D center, double searchRadius)
    {
        Set<String> quadSet = new HashSet<String>();
        final Map<String,Double> quadDist = new HashMap<String, Double>();
        for (double dx = center.x - searchRadius - StarLogic.QUAD_SIZE; dx <= center.x + searchRadius + StarLogic.QUAD_SIZE; dx += StarLogic.QUAD_SIZE)
            for (double dy = center.y - searchRadius - StarLogic.QUAD_SIZE; dy <= center.y + searchRadius + StarLogic.QUAD_SIZE; dy += StarLogic.QUAD_SIZE)
                for (double dz = center.z - searchRadius - StarLogic.QUAD_SIZE; dz <= center.z + searchRadius + StarLogic.QUAD_SIZE; dz += StarLogic.QUAD_SIZE)
                {
                    Point3D q = new Point3D(dx, dy, dz);
                    double d = q.dist(center);
                    if (d > searchRadius + StarLogic.QUAD_SIZE)
                        continue;
                    String quad = StarLogic.getQuadrant(q.x, q.y, q.z);
                    quadSet.add(quad);
                    quadDist.put(quad, d);
                }
        List<String> quadList = new ArrayList<String>();
        quadList.addAll(quadSet);
        Collections.sort(quadList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2)
            {
                double d1 = quadDist.get(o1);
                double d2 = quadDist.get(o2);
                if (d1 < d2)
                    return -1;
                else if (d1 > d2)
                    return 1;
                return 0;
            }
        });
        return quadList;
    }
    
    
    private static final String[][] ABBREVIATIONS = {
            { "And", "Andromeda", "Andromedae" }, // the  Chained Maiden
            { "Ant", "Antlia", "Antliae" }, // the Air Pump
            { "Aps", "Apus", "Apodis" }, // the Bird of Paradise
            { "Aqr", "Aquarius", "Aquarii" }, // the Water Bearer
            { "Aql", "Aquila", "Aquilae" }, // the Eagle
            { "Ara", "Ara", "Arae" }, // the Altar
            { "Ari", "Aries", "Arietis" }, // the Ram
            { "Aur", "Auriga", "Aurigae", },
            { "Boo", "Bootes", "Bootis", "BoÃ¶tes", "BoÃ¶tis" }, // the Herdsman
            { "Cae", "Caelum", "Caeli" }, // the Engraving Tool
            { "Cam", "Camelopardalis", "Camelopardalis" }, // the Giraffe
            { "Cnc", "Cancer", "Cancri" }, // the Crab
            { "CVn", "Canes Venatici", "Canum Venaticorum" }, // the Hunting Dogs
            { "CMa", "Canis Major", "Canis Majoris" }, // the Great Dog
            { "CMi", "Canis Minor", "Canis Minoris" }, // the Lesser Dog
            { "Cap", "Capricornus", "Capricorni" }, // the Sea Goat
            { "Car", "Carina", "Carinae" }, // the Keel
            { "Cas", "Cassiopeia", "Cassiopeiae" }, // the Seated Queen
            { "Cen", "Centaurus", "Centauri" }, // the Centaur
            { "Cep", "Cepheus", "Cephei" }, // the King
            { "Cet", "Cetus", "Ceti" }, // the Sea Monster
            { "Cha", "Chamaeleon", "Chamaeleontis" }, // the Chameleon
            { "Cir", "Circinus", "Circini" }, // the Drafting Compass
            { "Col", "Columba", "Columbae" }, // the Dove
            { "Com", "Coma Berenices", "Comae Berenices" }, // Berenice's Hair
            { "CrA", "Corona Australis", "Coronae Australis" }, // the Southern Crown
            { "CrB", "Corona Borealis", "Coronae Borealis" }, // the Northern Crown
            { "Crv", "Corvus", "Corvi" }, // the Crow
            { "Crt", "Crater", "Crateris" }, // the Cup
            { "Cru", "Crux", "Crucis" }, // the Southern Cross
            { "Cyg", "Cygnus", "Cygni" }, // the Swan
            { "Del", "Delphinus", "Delphini" }, // the Dolphin
            { "Dor", "Dorado", "Doradus" }, // the Dolphinfish
            { "Dra", "Draco", "Draconis" }, // the Dragon
            { "Eql", "Equuleus", "Equulei" }, // the Little Horse
            { "Eri", "Eridanus", "Eridani" }, // the River
            { "For", "Fornax", "Fornacis" }, // the Furnace
            { "Gem", "Gemini", "Geminorum" }, // the Twins
            { "Gru", "Grus", "Gruis" }, // the Crane
            { "Her", "Hercules", "Herculis", "Hercules" },
            { "Hor", "Horologium", "Horologii" }, // the Clock
            { "Hya", "Hydra", "Hydrae" }, // the Water Snake
            { "Hyi", "Hydrus", "Hydri" }, // the Male Water Snake
            { "Ind", "Indus", "Indi" }, // the Indian
            { "Lac", "Lacerta", "Lacertae" }, // the Lizard
            { "Leo", "Leo", "Leonis" }, // the Lion
            { "LMi", "Leo Minor", "Leonis Minoris" }, // the Lesser Lion
            { "Lep", "Lepus", "Leporis" }, // the Hare
            { "Lib", "Libra", "Librae" }, // the Scales
            { "Lup", "Lupus", "Lupi" }, // the Wolf
            { "Lyn", "Lynx", "Lyncis" }, // the Lynx
            { "Lyr", "Lyra", "Lyrae" }, // the Lyre
            { "Men", "Mensa", "Mensae" }, // the Table
            { "Mic", "Microscopium", "Microscopii" }, // the Microscope
            { "Mon", "Monoceros", "Monocerotis" }, // the Unicorn
            { "Mus", "Musca", "Muscae" }, // the Fly
            { "Nor", "Norma", "Normae" }, // the Carpenter's Square
            { "Oct", "Octans", "Octantis" }, // the Octant
            { "Oph", "Ophiuchus", "Ophiuchi" }, // the Serpent Bearer
            { "Ori", "Orion", "Orionis" }, // the Hunter
            { "Pav", "Pavo", "Pavonis" }, // the Peacock
            { "Peg", "Pegasus", "Pegasi" }, // the Winged Horse
            { "Per", "Perseus", "Persei" }, // the Hero
            { "Phe", "Phoenix", "Phoenicis" }, // the Phoenix
            { "Pic", "Pictor", "Pictoris" }, // the Painter
            { "Psc", "Pisces", "Piscium" }, // the Fishes
            { "PsA", "Piscis Austrinus", "Piscis Austrini" }, // the Southern Fish
            { "Pup", "Puppis", "Puppis" }, // the Stern
            { "Pyx", "Pyxis", "Pyxidis" }, // the Magnetic Compass
            { "Ret", "Reticulum", "Reticulii" }, // the Reticle
            { "Sge", "Sagitta", "Sagittae" }, // the Arrow
            { "Sgr", "Sagittarius", "Sagittarii" }, // the Archer
            { "Sco", "Scorpius", "Scorpii" }, // the Scorpion
            { "Scl", "Sculptor", "Sculptoris" }, // the Sculptor
            { "Sct", "Scutum", "Scuti" }, // the Shield
            { "Ser", "Serpens", "Serpentis" }, // the Serpent
            { "Sex", "Sextans", "Sextantis" }, // the Sextant
            { "Tau", "Taurus", "Tauri" }, // the Bull
            { "Tel", "Telescopium", "Telescopii" }, // the Telescope
            { "Tri", "Triangulum", "Trianguli" }, // the Triangle
            { "TrA", "Triangulum Australe", "Trianguli Australis" }, // the Southern Triangle
            { "Tuc", "Tucana", "Tucanae" }, // the Toucan
            { "UMa", "Ursa Major", "Ursae Majoris" }, // the Great Bear
            { "UMi", "Ursa Minor", "Ursae Minoris" }, // the Lesser Bear
            { "Vel", "Vela", "Velorum" }, // the Sails
            { "Vir", "Virgo", "Virginis" }, // the Maiden
            { "Vol", "Volans", "Volantis" }, // the Flying Fish
            { "Vul", "Vulpecula", "Vulpeculae" }, // the Fox
            { "Α", "α", "alpha", "άλφα",                                                },
            { "Β", "β", "beta", "βήτα", },
            { "Γ", "γ", "gamma", "γάμμα", },
            { "Δ", "δ", "delta", "δέλτα", },
            { "Ε", "ε", "epsilon", "έψιλον", },
            { "Ζ", "ζ", "zeta", "ζήτα", },
            { "Η", "η", "eta", "ήτα", },
            { "Θ", "θ", "theta", "θήτα", },
            { "Ι", "ι", "iota", "ιώτα", },
            { "Κ", "κ", "kappa", "κάππα", },
            { "Λ", "λ", "lambda", "λάμδα", },
            { "Μ", "μ", "mu", "μυ", },
            { "Ν", "ν", "nu", "νυ", },
            { "Ξ", "ξ", "xi", "ξι", },
            { "Ο", "ο", "omicron", "όμικρον", },
            { "Π", "π", "pi", "πι", },
            { "Ρ", "ρ", "rho", "ρώ", },
            { "Σ", "σ", "ς", "sigma", "σίγμα", },
            { "Τ", "τ", "tau", "ταυ", },
            { "Υ", "υ", "upsilon", "ύψιλον", },
            { "Φ", "φ", "phi", "φι", },
            { "Χ", "χ", "chi", "χι", },
            { "Ψ", "ψ", "psi", "ψι", },
            { "Ω", "ω", "omega", "ωμέγα", },
        };    
    
    private static Map<String, String> ABBREVIATION_MAP = null;
    
    private static String abbreviate(String txt)
    {
        if (ABBREVIATION_MAP == null)
        {
            ABBREVIATION_MAP = new HashMap<String, String>();
            for (String[] abb : ABBREVIATIONS)
            {
                for (int i = 1; i < abb.length; i++)
                    ABBREVIATION_MAP.put(abb[i].toLowerCase(), abb[0].toLowerCase());
            }
        }
        txt = txt.toLowerCase();
        StringBuffer name = new StringBuffer();
        for (StringTokenizer st = new StringTokenizer(txt, " \t\r\n"); st.hasMoreTokens(); )
        {
            String tok = st.nextToken();
            if (ABBREVIATION_MAP.containsKey(tok))
                tok = ABBREVIATION_MAP.get(tok);
            if (name.length() > 0)
                name.append(" ");
            name.append(tok);
        }
        return name.toString();
    }
}
