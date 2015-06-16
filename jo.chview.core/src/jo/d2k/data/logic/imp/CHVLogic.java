package jo.d2k.data.logic.imp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.DeletionLogic;
import jo.d2k.data.logic.MetadataLogic;
import jo.d2k.data.logic.StarGenLogic;
import jo.d2k.data.logic.StarLogic;
import jo.d2k.data.logic.StarRouteLogic;
import jo.util.utils.IProgMon;
import jo.util.utils.obj.ByteUtils;
import jo.util.utils.obj.DoubleUtils;
import jo.util.utils.obj.StringUtils;

public class CHVLogic
{
    public static int importCHVData(InputStream is, boolean merge, IProgMon pm) throws IOException
    {
        Map<String,String> mdMap = StarLogic.getMetadataMap("mass", "constellation", "comment", "group");
        Random rnd = new Random();
        if (!merge)
        {
            StarLogic.deleteAll();
            StarRouteLogic.deleteAll();
            DeletionLogic.deleteAll();
        }
        Map<String,Object> style = importStyle(is);
        int numStars = readInt(is);
        if (pm != null)
            pm.beginTask("Import Stars", numStars);
        for (int i = 0; i < numStars; i++)
        {
            importStarSystem(is, rnd, mdMap, style);
            if (pm != null)
            {
                pm.worked(1);
                if (i%100 == 0)
                    pm.subTask("Done "+(i+1)+" stars");
                if (pm.isCanceled())
                    break;
            }
        }
        String postamble = readString(is);
        MetadataLogic.setValue("db.info", -1, "notes", postamble);
        if (pm != null)
            pm.done();
        return numStars;
    }

    private static void importStarSystem(InputStream is, Random rnd, Map<String,String> metadataMap, Map<String,Object> style) throws IOException
    {
        byte child = 0;
        int numChildren = 0;
        StarBean parent = null;
        do
        {
            StarBean s = importStar(is, parent, rnd, metadataMap, style);
            //System.out.println(i+": "+starName+" ... "+comment);
            Map<String,String> md = s.getMetadata();
            s = StarLogic.create(s);
            if (md.size() > 0)
                MetadataLogic.setAsMap("star.md", s.getOID(), md);
            child = (byte)is.read();
            numChildren++;
            if (parent == null)
                parent = s;
        } while (child != 0);
        for (int j = 0; j < numChildren; j++)
        {
            int numLinks = readInt(is);
            if (numLinks > 0)
                System.out.println(parent.getName()+" #"+numLinks+" links");
            for (int k = 0; k < numLinks; k++)
            {
                /*int type =*/ readInt(is);
                /*String destination =*/ readString(is);
            }
        }
    }

    private static StarBean importStar(InputStream is, StarBean parent, Random rnd, Map<String,String> metadataMap, Map<String,Object> style) throws IOException
    {
        String cjcName = readString(is);
        String starName = readString(is);
        /*double dToEarth =*/ readDouble(is);
        String spectra = readString(is);
        double mass = readDouble(is);
        double actualMass = readDouble(is);
        double sx = readDouble(is);
        double sy = readDouble(is);
        double sz = readDouble(is);
        String constellation =readString(is);
        String comment = readString(is);
        /*int selected =*/ readInt(is);
        /*int index =*/ readInt(is);
        int group = readInt(is);
        
        StarBean s = new StarBean();
        s.setMetadata(new HashMap<String,String>());
        if (parent != null)
            s.setParent(parent.getOID());
        s.setName(cjcName);
        s.setCommonName(starName);
        if (StringUtils.isTrivial(s.getName()))
            s.setName(s.getCommonName());
        setSpectra(s, spectra, rnd);
        int spec = StarLogic.SPECTRA.indexOf(s.getSpectra().charAt(0));
        s.setAbsMag(StarGenLogic.genAbsMag(rnd, spec));
        comment = parseCommentNames(s, comment);
        if (metadataMap.containsKey("mass"))
            s.getMetadata().put(metadataMap.get("mass"), String.valueOf(mass));
        if (metadataMap.containsKey("actualmass"))
            s.getMetadata().put(metadataMap.get("actualmass"), String.valueOf(actualMass));
        if (metadataMap.containsKey("constellation"))
            s.getMetadata().put(metadataMap.get("constellation"), constellation);
        if (metadataMap.containsKey("comment"))
            s.getMetadata().put(metadataMap.get("comment"), comment);
        if (metadataMap.containsKey("group") && (group != 0))
        {
            StringBuffer groups = new StringBuffer();
            if ((group&0x01) != 0)
                groups.append(" "+style.get("group1"));
            if ((group&0x02) != 0)
                groups.append(" "+style.get("group2"));
            if ((group&0x04) != 0)
                groups.append(" "+style.get("group3"));
            if ((group&0x08) != 0)
                groups.append(" "+style.get("group4"));
            s.getMetadata().put(metadataMap.get("group"), groups.toString().trim());
            System.out.println(s.getName()+" groups = "+group+" -> "+groups);
        }
        s.setX(sx);
        s.setY(sy);
        s.setZ(sz);
        s.setQuadrant(StarLogic.getQuadrant(sx, sy, sz));
        return s;
    }

    public static String parseCommentNames(StarBean s, String comment)
    {
        StringBuffer ret = new StringBuffer(comment.length());
        for (StringTokenizer st = new StringTokenizer(comment, ","); st.hasMoreTokens(); )
        {
            String tok = st.nextToken().trim();
            if (tok.startsWith("GJ"))
            {
                s.setGJName(tok);
                tok = null;
            }
            else if (tok.startsWith("HR"))
            {
                s.setHRName(tok);
                tok = null;
            }
            else if (tok.startsWith("SAO"))
            {
                s.setSAOName(tok);
                tok = null;
            }
            else if (tok.startsWith("2MASS"))
            {
                s.setTwoMassName(tok);
                tok = null;
            }
            else if (tok.startsWith("HD"))
            {
                s.setHDName(tok);
                tok = null;
            }
            else if (tok.startsWith("HIP"))
            {
                s.setHIPName(tok);
                tok = null;
            }
            if (tok != null)
            {
                if (ret.length() > 0)
                    ret.append(",");
                ret.append(tok);
            }
        }
        return ret.toString();
    }

    public static void setSpectra(StarBean s, String spectra, Random rnd)
    {
        s.setSpectra("M");
        for (int i = 0; i < spectra.length(); i++)
        {
            char ch = spectra.charAt(i);
            if (Character.isLowerCase(ch))
                ch = Character.toUpperCase(ch);
            if (StarLogic.SPECTRA.indexOf(ch) >= 0)
            {
                s.setSpectra(String.valueOf(ch));
                if (i + 1 < spectra.length() && Character.isDigit(spectra.charAt(i+1)))
                    s.setSpectra(s.getSpectra()+spectra.charAt(i+1));
                break;
            }
        }
        int spec = StarLogic.SPECTRA.indexOf(s.getSpectra().charAt(0));
        if (s.getSpectra().length() == 1)
            s.setSpectra(s.getSpectra()+Integer.toString(rnd.nextInt(10)));
        if (spectra.startsWith("d"))
            s.setSpectra(s.getSpectra()+"D");
        else if (spectra.startsWith("g"))
            s.setSpectra(s.getSpectra()+"III");
        else if (spectra.startsWith("("))
            s.setSpectra(s.getSpectra()+"II");
        else
            s.setSpectra(s.getSpectra()+StarGenLogic.makeClass(spec, rnd));
    }

    private static Map<String,Object> importStyle(InputStream is) throws IOException
    {
        Map<String,Object> style = new HashMap<String, Object>();
        /*long version =*/ readLong(is);
        /*int grid =*/ readInt(is);
        /*double gridSize =*/ readDouble(is);
        /*int link =*/ readInt(is);
        /*int linkNumbers =*/ readInt(is);
        /*double linkSize1 =*/ readDouble(is);
        /*double linkSize2 =*/ readDouble(is);
        /*double linkSize3 =*/ readDouble(is);
        /*double linkSize4 =*/ readDouble(is);
        /*int shorStarName =*/ readInt(is);
        /*double radius =*/ readDouble(is);
        /*int scale =*/ readInt(is);
        /*int gridStyle =*/ readInt(is);
        /*int linkStyle1 =*/ readInt(is);
        /*int linkStyle2 =*/ readInt(is);
        /*int linkStyle3 =*/ readInt(is);
        /*int stemStyle =*/ readInt(is);
        /*int starOutline =*/ readInt(is);
        /*int routeDisp =*/ readInt(is);
        /*long dummy1 =*/ readLong(is);
        /*long dummy2 =*/ readLong(is);
        /*long dummy3 =*/ readLong(is);
        /*long dummy4 =*/ readLong(is);
        /*long dummy5 =*/ readLong(is);
        /*long dummy6 =*/ readLong(is);
        /*long dummy7 =*/ readLong(is);
        /*long dummy8 =*/ readLong(is);
        /*long oColor =*/ readLong(is);
        /*long bColor =*/ readLong(is);
        /*long aColor =*/ readLong(is);
        /*long fColor =*/ readLong(is);
        /*long gColor =*/ readLong(is);
        /*long kColor =*/ readLong(is);
        /*long mColor =*/ readLong(is);
        /*long xColor =*/ readLong(is);
        /*long backColor =*/ readLong(is);
        /*long textColor =*/ readLong(is);
        /*long linkNumColor =*/ readLong(is);
        /*long link1Color =*/ readLong(is);
        /*long link2Color =*/ readLong(is);
        /*long link3Color =*/ readLong(is);
        /*long gridColor =*/ readLong(is);
        /*long stemColor =*/ readLong(is);
        /*int oRad =*/ readInt(is);
        /*int bRad =*/ readInt(is);
        /*int aRad =*/ readInt(is);
        /*int fRad =*/ readInt(is);
        /*int gRad =*/ readInt(is);
        /*int kRad =*/ readInt(is);
        /*int mRad =*/ readInt(is);
        /*int xRad =*/ readInt(is);
        /*int dwarfRad =*/ readInt(is);
        /*int giantRad =*/ readInt(is);
        /*int superGiantRad =*/ readInt(is);
        /*double x =*/ readDouble(is);
        /*double y =*/ readDouble(is);
        /*double z =*/ readDouble(is);
        /*double theta =*/ readDouble(is);
        /*double phi =*/ readDouble(is);
        /*double rho =*/ readDouble(is);
        /*double tscale =*/ readDouble(is);
        /*double pscale =*/ readDouble(is);
        /*double rscale =*/ readDouble(is);
        /*double xscale =*/ readDouble(is);
        /*double yscale =*/ readDouble(is);
        style.put("group1", readString(is));
        style.put("group2", readString(is));
        style.put("group3", readString(is));
        style.put("group4", readString(is));
        /*int displayGroup1 =*/ readInt(is);
        /*int displayGroup2 =*/ readInt(is);
        /*int displayGroup3 =*/ readInt(is);
        /*int displayGroup4 =*/ readInt(is);
        /*String route1Label =*/ readString(is);
        /*long route1Color =*/ readLong(is);
        /*int route1Style =*/ readInt(is);
        /*String route2Label =*/ readString(is);
        /*long route2Color =*/ readLong(is);
        /*int route2Style =*/ readInt(is);
        /*String route3Label =*/ readString(is);
        /*long route3Color =*/ readLong(is);
        /*int route3Style =*/ readInt(is);
        /*String route4Label =*/ readString(is);
        /*long route4Color =*/ readLong(is);
        /*int route4Style =*/ readInt(is);
        readFont(is); // LinkF
        readFont(is); // NameF
        readFont(is); // UNameF
        readDouble(is); // ??
        readDouble(is); // ??
        readDouble(is); // ??
        return style;
    }
    
    private static void readFont(InputStream is) throws IOException
    {
        byte[] data = new byte[50];
        is.read(data, 0, 50);
    }
    
    private static double readDouble(InputStream is) throws IOException
    {
        String str = readShortString(is);
        return DoubleUtils.parseDouble(str);
    }
    
    private static String readShortString(InputStream is) throws IOException
    {
        int len = is.read();
        StringBuffer sb = new StringBuffer(len);
        for (int i = 0; i < len; i++)
            sb.append((char)is.read());
        return sb.toString();
    }
    
    private static String readString(InputStream is) throws IOException
    {
        int len = readInt(is);
        StringBuffer sb = new StringBuffer(len);
        for (int i = 0; i < len; i++)
            sb.append((char)is.read());
        return sb.toString();
    }
    
    private static int readInt(InputStream is) throws IOException
    {
        byte b1 = (byte)is.read();
        byte b2 = (byte)is.read();
        return ByteUtils.toShort(b2, b1);
    }
    
    private static int readLong(InputStream is) throws IOException
    {
        byte b1 = (byte)is.read();
        byte b2 = (byte)is.read();
        byte b3 = (byte)is.read();
        byte b4 = (byte)is.read();
        return ByteUtils.toInt(b1, b2, b3, b4);
    }
}
