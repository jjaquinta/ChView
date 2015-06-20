package jo.chview.web.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jo.chview.web.servlet.BaseServlet;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.RuntimeLogic;
import jo.d2k.data.logic.StarLogic;
import jo.d2k.data.logic.stargen.SystemLogic;
import jo.d2k.data.logic.stargen.data.BodyBean;
import jo.d2k.data.logic.stargen.data.GasBean;
import jo.d2k.data.logic.stargen.data.SolidBodyBean;
import jo.d2k.data.logic.stargen.data.SunBean;
import jo.util.logic.CSVLogic;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class SystemRequestLogic
{

    public static void doSystemRequest(BaseServlet srv, String quad, long starOID, long systemOID) throws IOException
    {
        StarBean star = StarLogic.getByQuadrantID(quad, starOID);
        if (star == null)
        {
            srv.respondError("Unknown star oid="+starOID+", quadrant="+quad);
            return;
        }
        SunBean sun = SystemLogic.generateSystem(star);
        List<BodyBean> todo = new ArrayList<BodyBean>();
        addBodies(todo, sun, systemOID);
        if (todo.size() == 0)
        {
            srv.respondError("Unknown object oid="+systemOID);
            return;
        }
        String format = srv.getStringParameter("f", "html");
        if ("html".equals(format))
            SystemRequestLogic.respondHTML(srv, star, todo);
        //else if ("svg".equals(format))
        //    StarRequestLogic.respondSVG(srv, todo);
        else if ("json".equals(format))
            SystemRequestLogic.respondJSON(srv, todo);
        else if ("csv".equals(format))
            SystemRequestLogic.respondCSV(srv, todo);
        else
            srv.respondError("Unknown format: "+format);
    }
    
    private static void addBodies(List<BodyBean> todo, BodyBean body, long inSearchOf)
    {
        if ((inSearchOf == 0) || (inSearchOf == body.getOID()))
            todo.add(body);
        for (BodyBean child : body.getChildren())
        {
            if ((inSearchOf != 0) && todo.size() > 0)
                return;
            addBodies(todo, child, inSearchOf);
        }
    }

    private static void respondHTML(BaseServlet srv, StarBean star, List<BodyBean> todo) throws IOException
    {
        srv.setHTML();
        srv.println("<HTML>");
        srv.println("<HEAD><TITLE>"+RuntimeLogic.getInstance().getDataSource().getName()+" - Atlas</TITLE></HEAD>");
        srv.println("<BODY><H1>"+RuntimeLogic.getInstance().getDataSource().getName()+" - Atlas</H1>");
        srv.println("<H2>");
        srv.println("<A HREF='"+srv.getRequest().getContextPath()+"/chview/"+star.getQuadrant()+"/"+star.getOID()+"?f=html'>");
        srv.println(star.getName());
        srv.println("</A");
        srv.println("</H2>");
        srv.println("<H3>Quad ");
        srv.println("<A HREF='"+srv.getRequest().getContextPath()+"/chview/"+star.getQuadrant()+"?f=html'>");
        srv.println(star.getQuadrant());
        srv.println("</A");
        srv.println("</H3>");
        //if (todo.size() > 1)
        {
            srv.println("<TABLE>");
            srv.println("<TR>");
            srv.println("<TH>Name</TH>");
            srv.println("<TH>Mass</TH>");
            srv.println("<TH>Radius</TH>");
            srv.println("<TH>A</TH>");
            srv.println("<TH>E</TH>");
            srv.println("<TH>AxialTilt</TH>");
            srv.println("<TH>Parent</TH>");
            srv.println("</TR>");
            for (BodyBean body : todo)
            {
                srv.println("<TR>");
                srv.println("<TD>"+body.getName()+"</TD>");
                srv.println("<TD>"+body.getMass()+"</TD>");
                srv.println("<TD>"+body.getRadius()+"</TD>");
                srv.println("<TD>"+body.getA()+"</TD>");
                srv.println("<TD>"+body.getE()+"</TD>");
                srv.println("<TD>"+body.getAxialTilt()+"</TD>");
                srv.println("<TD>");
                if (body.getParent() != null)
                    srv.println(body.getParent().getName());
                srv.println("</TD>");
                srv.println("</TR>");
            }
        }
        srv.println("</BODY></HTML>");
    }

    @SuppressWarnings("unchecked")
    private static void respondJSON(BaseServlet srv, List<BodyBean> todo) throws IOException
    {
        JSONArray a = new JSONArray();
        for (BodyBean body : todo)
        {
            JSONObject b = new JSONObject();
            b.put("OID", body.getOID());
            b.put("Name", body.getName());
            b.put("Mass", body.getMass());
            b.put("Radius", body.getRadius());
            b.put("A", body.getA());
            b.put("E", body.getE());
            b.put("AxialTilt", body.getAxialTilt());
            if (body.getParent() != null)
                b.put("Parent", body.getParent().getOID());
            if (body instanceof SunBean)
            {
                SunBean sun = (SunBean)body;
                b.put("Luminosity", sun.getLuminosity());
                b.put("Life", sun.getLife());
                b.put("Age", sun.getAge());
                b.put("Recosphere", sun.getREcosphere());
            }
            else if (body instanceof SolidBodyBean)
            {
                SolidBodyBean solid = (SolidBodyBean)body;
                b.put("GasGiant", solid.isGasGiant());
                b.put("DustMass", solid.getDustMass());
                b.put("GasMass", solid.getGasMass());
                b.put("CoreRadius", solid.getCoreRadius());
                b.put("OrbitZone", solid.getOrbitZone());
                b.put("Density", solid.getDensity());
                b.put("OrbPeriod", solid.getOrbPeriod());
                b.put("Day", solid.getDay());
                b.put("ResonantPeriod", solid.isResonantPeriod());
                b.put("EscVelocity", solid.getEscVelocity());
                b.put("SurfAccel", solid.getSurfAccel());
                b.put("SurfGrav", solid.getSurfGrav());
                b.put("RMSVelocity", solid.getRMSVelocity());
                b.put("MolecWeight", solid.getMolecWeight());
                b.put("VolatileGasInventory", solid.getVolatileGasInventory());
                b.put("SurfPressure", solid.getSurfPressure());
                b.put("GreenhouseEffect", solid.isGreenhouseEffect());
                b.put("BoilPoint", solid.getBoilPoint());
                b.put("Albedo", solid.getAlbedo());
                b.put("ExosphericTemp", solid.getExosphericTemp());
                b.put("EstimatedTemp", solid.getEstimatedTemp());
                b.put("EstimatedTerrTemp", solid.getEstimatedTerrTemp());
                b.put("SurfTemp", solid.getSurfTemp());
                b.put("GreenhsRise", solid.getGreenhsRise());
                b.put("HighTemp", solid.getHighTemp());
                b.put("LowTemp", solid.getLowTemp());
                b.put("MaxTemp", solid.getMaxTemp());
                b.put("MinTemp", solid.getMinTemp());
                b.put("Hydrosphere", solid.getHydrosphere());
                b.put("CloudCover", solid.getCloudCover());
                b.put("IceCover", solid.getIceCover());
                b.put("RockCover", solid.getRockCover());
                b.put("HydroAlbedo", solid.getHydroAlbedo());
                b.put("CloudAlbedo", solid.getCloudAlbedo());
                b.put("IceAlbedo", solid.getIceAlbedo());
                b.put("RockAlbedo", solid.getRockAlbedo());
                b.put("MinorMoons", solid.getMinorMoons());
                b.put("Terraformed", solid.isTerraformed());
                b.put("Type", solid.getType());
                JSONArray at = new JSONArray();
                b.put("Atmosphere", at);
                for (GasBean gas : solid.getAtmosphere())
                {
                    JSONObject g = new JSONObject();
                    at.add(g);
                    g.put("SurfacePressure", gas.getSurfacePressure());
                    JSONObject c = new JSONObject();
                    g.put("Chem", c);
                    c.put("num", gas.getChem().num);
                    c.put("num", gas.getChem().num);
                    c.put("symbol", gas.getChem().symbol);
                    c.put("html_symbol", gas.getChem().html_symbol);
                    c.put("name", gas.getChem().name);
                    c.put("weight", gas.getChem().weight);
                    c.put("melt", gas.getChem().melt);
                    c.put("boil", gas.getChem().boil);
                    c.put("density", gas.getChem().density);
                    c.put("abunde", gas.getChem().abunde);
                    c.put("abunds", gas.getChem().abunds);
                    c.put("reactivity", gas.getChem().reactivity);
                    c.put("max_ipp", gas.getChem().max_ipp);
                    c.put("unicode_symbol", gas.getChem().unicode_symbol);
                }
                JSONArray ps = new JSONArray();
                b.put("Props", ps);
                for (Object key : solid.getProps().keySet())
                {
                    JSONObject p = new JSONObject();
                    ps.add(p);
                    p.put("key", key);
                    p.put("value", solid.getProps().get(key));
                }
            }
            a.add(b);
        }
        String json = JSONValue.toJSONString(a);
        srv.setContentType("text/json");
        srv.println(json);
    }

    private static void respondCSV(BaseServlet srv, List<BodyBean> todo) throws IOException
    {
        srv.setContentType("text/plain");
        List<Object> line = new ArrayList<Object>();
        line.add("OID");
        line.add("Name");
        line.add("Mass");
        line.add("Radius");
        line.add("A");
        line.add("E");
        line.add("AxialTilt");
        line.add("Parent");
        srv.println(CSVLogic.toCSVLine(line));
        for (BodyBean body : todo)
        {
            line.clear();
            line.add(body.getOID());
            line.add(body.getName());
            line.add(body.getMass());
            line.add(body.getRadius());
            line.add(body.getA());
            line.add(body.getE());
            line.add(body.getAxialTilt());
            if (body.getParent() != null)
                line.add(body.getParent().getOID());
            else
                line.add(0);
            srv.println(CSVLogic.toCSVLine(line));
        }
    }

}
