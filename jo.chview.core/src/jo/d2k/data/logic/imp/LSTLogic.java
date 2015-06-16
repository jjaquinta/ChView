package jo.d2k.data.logic.imp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.DeletionLogic;
import jo.d2k.data.logic.MetadataLogic;
import jo.d2k.data.logic.StarExtraLogic;
import jo.d2k.data.logic.StarGenLogic;
import jo.d2k.data.logic.StarLogic;
import jo.d2k.data.logic.StarRouteLogic;
import jo.util.geom3d.Point3D;
import jo.util.geom3d.Point3DLogic;
import jo.util.utils.DebugUtils;
import jo.util.utils.IProgMon;
import jo.util.utils.obj.StringUtils;

public class LSTLogic
{
    public static int importLSTData(InputStream is, boolean merge, IProgMon pm) throws IOException
    {
        Map<String,String> mdMap = StarLogic.getMetadataMap("mass", "constellation", "comment");
        int done = 0;
        Random rnd = new Random();
        BufferedReader rdr = new BufferedReader(new InputStreamReader(is, "utf-8"));
        if (!merge)
        {
            StarLogic.deleteAll();
            StarRouteLogic.deleteAll();
            DeletionLogic.deleteAll();
        }
        if (pm != null)
            pm.beginTask("Import Stars", 1);
        StarBean last = null;
        for (;;)
        {
            String inbuf = rdr.readLine();
            if (inbuf == null)
                break;
            inbuf = inbuf.trim();
            if (inbuf.length() == 0)
                continue;
            StarBean s = importLST(inbuf, last, rnd, mdMap);
            if (s == null)
            {
                DebugUtils.error("Error on line: "+inbuf);
                continue;
            }
            Map<String,String> md = s.getMetadata();
            s = StarLogic.create(s);
            if (md.size() > 0)
                MetadataLogic.setAsMap("star.md", s.getOID(), md);
            last = s;
            done++;
            if (pm != null)
            {
                //pm.worked(stars.size());
                if (done%100 == 0)
                    pm.subTask("Done "+done+" stars");
                if (pm.isCanceled())
                    break;
            }
        }
        rdr.close();
        if (pm != null)
            pm.done();
        return done;
    }
    private static StarBean importLST(String inbuf, StarBean last, Random rnd, Map<String,String> metadataMap)
    {
        String[] toks = StringUtils.splitString(inbuf, "/");
        if (toks.length < 8)
            return null;
        Point3D ords = Point3DLogic.fromString(toks[7]);
        StarBean s = new StarBean();
        s.setMetadata(new HashMap<String,String>());
        if ((last != null) && (StarExtraLogic.distance(last,  ords.x, ords.y, ords.z) < .2*.2))
            s.setParent(last.getOID());
        s.setName(toks[0]);
        s.setCommonName(toks[1]);
        if (StringUtils.isTrivial(s.getName()))
            s.setName(s.getCommonName());
        // distToEarth = toks[2];
        CHVLogic.setSpectra(s, toks[3], rnd);
        int spec = StarLogic.SPECTRA.indexOf(s.getSpectra().charAt(0));
        s.setAbsMag(StarGenLogic.genAbsMag(rnd, spec));
        toks[6] = CHVLogic.parseCommentNames(s, toks[6]);
        if (metadataMap.containsKey("mass"))
            s.getMetadata().put(metadataMap.get("mass"), toks[4]);
        if (metadataMap.containsKey("constellation"))
            s.getMetadata().put(metadataMap.get("constellation"), toks[5]);
        if (metadataMap.containsKey("comment"))
            s.getMetadata().put(metadataMap.get("comment"), toks[6]);
        s.setX(ords.x);
        s.setY(ords.y);
        s.setZ(ords.z);
        s.setQuadrant(StarLogic.getQuadrant(ords.x, ords.y, ords.z));
        return s;
    }
}
