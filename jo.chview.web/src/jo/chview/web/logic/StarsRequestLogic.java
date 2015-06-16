package jo.chview.web.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jo.chview.web.servlet.BaseServlet;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarLink;
import jo.d2k.data.logic.StarExtraLogic;
import jo.d2k.data.logic.StarLogic;
import jo.util.utils.obj.StringUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class StarsRequestLogic
{
    
    public static void doStarsRequest(BaseServlet srv, String quad) throws IOException
    {
        List<String> quads = new ArrayList<String>();
        quads.add(quad);
        List<StarBean> stars = StarLogic.getByQuadrants(quads);
        String format = srv.getStringParameter("f", "html");
        if ("html".equals(format))
            StarsRequestLogic.respondHTML(srv, quad, stars);
        else if ("svg".equals(format))
            StarsRequestLogic.respondSVG(srv, stars, quad);
        else if ("json".equals(format))
            StarsRequestLogic.respondJSON(srv, stars, quad);
        else if ("csv".equals(format))
            StarsRequestLogic.respondCSV(srv, stars);
        else
            srv.respondError("Unknown format: "+format);
    }

    private static void respondHTML(BaseServlet srv, String quad, List<StarBean> stars) throws IOException
    {
        double x = StarLogic.getOrd(quad.charAt(0));
        double y = StarLogic.getOrd(quad.charAt(1));
        double z = StarLogic.getOrd(quad.charAt(2));
        srv.setHTML();
        srv.println("<HTML>");
        srv.println("<HEAD><TITLE>Dawnfire 2000 - Atlas</TITLE></HEAD>");
        srv.println("<BODY>");
        srv.println("<H1>Dawnfire 2000 - Atlas</H1>");
        srv.println("<H2>Quadrant "+quad+" - "+x+","+y+","+z+"</H2>");
        srv.println("<TABLE>");
        srv.println("<TR>");
        srv.println("<TH>Quad</TH>");
        srv.println("<TH>ID</TH>");
        srv.println("<TH>Name</TH>");
        srv.println("<TH>Spectra</TH>");
        srv.println("<TH>Magnitude</TH>");
        srv.println("<TH>X,Y,Z</TH>");
        srv.println("<TH>Parent</TH>");
        srv.println("</TR>");
        for (StarBean star : stars)
        {
            srv.println("<TR>");
            srv.println("<TD>"+star.getQuadrant()+"</TD>");
            srv.println("<TD>"+star.getOID()+"</TD>");
            srv.println("<TD>");
            srv.println("<A HREF='"+srv.getRequest().getContextPath()+"/chview/"+star.getQuadrant()+"/"+star.getOID()+"?f=html'>"+star.getName()+"</A>");
            if (!StringUtils.isTrivial(star.getWikipediaURL()))
                srv.println("<A HREF='http://en.wikipedia.org"+star.getWikipediaURL()+"'><IMG src='"+srv.getRequest().getContextPath()+"/images/wikipedia.png'/></A>");
            if (!StringUtils.isTrivial(star.getSimbadURL()))
                srv.println("<A HREF='"+star.getSimbadURL()+"'><IMG src='"+srv.getRequest().getContextPath()+"/images/simbad.gif'/></A>");
            srv.println("</TD>");
            srv.println("<TD>"+star.getSpectra()+"</TD>");
            srv.println("<TD>"+star.getAbsMag()+"</TD>");
            srv.println("<TD>"+star.getX()+","+star.getY()+","+star.getZ()+"</TD>");
            srv.println("<TD>"+star.getParent()+"</TD>");
            srv.println("</TR>");
        }
        srv.println("</TABLE>");
        srv.println("<H3>Nearby</H3>");
        srv.println("<UL>");
        addQuadLink(srv, "+X", x + StarLogic.QUAD_SIZE, y, z);
        addQuadLink(srv, "-X", x - StarLogic.QUAD_SIZE, y, z);
        addQuadLink(srv, "+Y", x, y + StarLogic.QUAD_SIZE, z);
        addQuadLink(srv, "-Y", x, y - StarLogic.QUAD_SIZE, z);
        addQuadLink(srv, "+Z", x, y, z + StarLogic.QUAD_SIZE);
        addQuadLink(srv, "-Z", x, y, z - StarLogic.QUAD_SIZE);
        srv.println("</UL>");
        srv.println("</BODY></HTML>");
    }

    private static void addQuadLink(BaseServlet srv, String label, double x, double y, double z) throws IOException
    {
        String q = StarLogic.getQuadrant(x, y, z);
        srv.println("<LI>");
        srv.println("<A HREF='"+srv.getRequest().getContextPath()+"/chview/"+q+"?f=html'>");
        srv.println(label+": "+q+" - "+x+", "+y+", "+z);
        srv.println("</A>");
        srv.println("</LI>");        
    }
    
    private static void respondSVG(BaseServlet srv, List<StarBean> stars, String quad) throws IOException
    {
        
        double x = StarLogic.getOrd(quad.charAt(0));
        double y = StarLogic.getOrd(quad.charAt(1));
        //double z = StarLogic.getOrd(quad.charAt(2));
        double r = StarLogic.QUAD_SIZE;
        double linkSize = srv.getDoubleParameter("linkSize", -1);        
        List<StarBean[]> linkPairs = new ArrayList<StarBean[]>();
        srv.setHTML();
        srv.println("<HTML>");
        srv.println("<HEAD><TITLE>Dawnfire 2000 - Atlas</TITLE>");
        srv.println("<script>");
        srv.println("if (typeof String.prototype.startsWith != 'function') {");
        srv.println("String.prototype.startsWith = function (str){");
        srv.println("return this.indexOf(str) == 0;");
        srv.println("};");
        srv.println("  }");
        srv.println("var ordinates = [");
        for (int i = 0; i < stars.size() - 1; i++)
        {
            StarBean star1 = stars.get(i);
            if (star1.getParent() > 0)
                continue;
            srv.print("["+star1.getX()+","+star1.getY()+","+star1.getZ()+",'c"+star1.getOID()+"','t"+star1.getOID()+"'");
            if (linkSize > 0)
            {
                for (int j = 0; j < linkPairs.size(); j++)
                {
                    StarBean[] pair = linkPairs.get(j);
                    if (pair[1] == star1)
                    {
                        srv.print(", 'L"+pair[0].getOID()+"x"+pair[1].getOID()+"'");
                    }
                }
                for (int j = i + 1; j < stars.size(); j++)
                {
                    StarBean star2 = stars.get(j);
                    if (star2.getParent() > 0)
                        continue;
                    double d = StarExtraLogic.distance(star1, star2);
                    if (d > linkSize)
                        continue;
                    linkPairs.add(new StarBean[] { star1, star2 });
                    srv.print(", 'l"+star1.getOID()+"x"+star2.getOID()+"'");
                }
            }
            srv.println("],");
        }
        srv.println("];");
        srv.println("var rotY = 0;");
        srv.println("var rotX = 0;");
        srv.println("function updatePositions()");
        srv.println("{");
        srv.println("  var rotYcos = Math.cos(rotY);");
        srv.println("  var rotYsin = Math.sin(rotY);");
        srv.println("  var rotXcos = Math.cos(rotX);");
        srv.println("  var rotXsin = Math.sin(rotX);");
        srv.println("  for (var i = 0; i < ordinates.length; i++)");
        srv.println("  {");
        srv.println("    var x=ordinates[i][0];");
        srv.println("    var y=ordinates[i][1];");
        srv.println("    var z=ordinates[i][2];");
        srv.println("    var y1= y*rotXcos - z*rotXsin;");
        srv.println("    var z1= y*rotXsin + z*rotXcos;");
        srv.println("    var x2= x*rotYcos - z*rotYsin;");
        srv.println("    var z2= x*rotYsin + z*rotYcos;");
        srv.println("    for (var j = 3; j < ordinates[i].length; j++)");
        srv.println("    {");
        srv.println("      var id = ordinates[i][j];");
        srv.println("      if (id.startsWith('c'))");
        srv.println("      {");
        srv.println("        var o = document.getElementById(id);");
        srv.println("        if (o == null) alert('no object for id='+id);");
        srv.println("        o.setAttribute('cx', x2);");
        srv.println("        o.setAttribute('cy', y1);");
        srv.println("      }");
        srv.println("      else if (id.startsWith('t'))");
        srv.println("      {");
        srv.println("        var o = document.getElementById(id);");
        srv.println("        if (o == null) alert('no object for id='+id);");
        srv.println("        o.setAttribute('x', x2);");
        srv.println("        o.setAttribute('y', y1);");
        srv.println("      }");
        srv.println("      else if (id.startsWith('l'))");
        srv.println("      {");
        srv.println("        var o = document.getElementById(id);");
        srv.println("        if (o == null) alert('no object for id='+id);");
        srv.println("        o.setAttribute('x1', x2);");
        srv.println("        o.setAttribute('y1', y1);");
        srv.println("      }");
        srv.println("      else if (id.startsWith('L'))");
        srv.println("      {");
        srv.println("        var o = document.getElementById('l'+id.substring(1));");
        srv.println("        if (o == null) alert('no object for id='+id);");
        srv.println("        o.setAttribute('x2', x2);");
        srv.println("        o.setAttribute('y2', y1);");
        srv.println("      }");
        srv.println("    }");
        srv.println("  }");
        //srv.println("  alert('wow rotX='+rotX);");
        srv.println("}");
        srv.println("function rotate(dX, dY)");
        srv.println("{");
        srv.println("  rotX += dX;");
        srv.println("  rotY += dY;");
        srv.println("  updatePositions();");
        srv.println("}");
        srv.println("</script>");
        srv.println("</HEAD>");
        srv.println("<BODY><H1>Dawnfire 2000 - Atlas</H1>");
        srv.println("<svg id='canvas' xmlns='http://www.w3.org/2000/svg' version='1.1' style='background: black;' width='400' height='400'");
        srv.println("viewbox='"+(-r)+","+(-r)+","+(r*2)+","+(r*2)+"'>");
        for (StarBean[] pair : linkPairs)
        {
            double x1 = pair[0].getX() - x;
            double y1 = pair[0].getY() - y;
            double x2 = pair[1].getX() - x;
            double y2 = pair[1].getY() - y;
            srv.println("<line id='l"+pair[0].getOID()+"x"+pair[1].getOID()+"' x1='"+x1+"' y1='"+y1+"' x2='"+x2+"' y2='"+y2+"' style='stroke:rgb(128,255,128);stroke-width:.025'/>");
        }
        for (StarBean star : stars)
        {
            if (star.getParent() > 0)
                continue;
            double sx = star.getX() - x;
            double sy = star.getY() - y;
            double sr = .1;
            String color = "#"+StarExtraLogic.getStarColorRGB(star);
            srv.println("<circle id='c"+star.getOID()+"' cx='"+sx+"' cy='"+sy+"' r='"+sr+"' fill='"+color+"'/>");
            srv.print("<text id='t"+star.getOID()+"' x='"+(sx+sr*2)+"' y='"+(sy-sr*2)+"' style='font-size: 1px;' fill='"+color+"'>");
            srv.print(star.getName());
            srv.println("</text>");
        }
        srv.println("</svg>");
        srv.println("<span onMouseDown='rotate(.1, 0);'>X+</span>");
        srv.println("<span onMouseDown='rotate(-.1, 0);'>X-</span>");
        srv.println("<span onMouseDown='rotate(0,.1);'>Y+</span>");
        srv.println("<span onMouseDown='rotate(0,-.1);'>Y-</span>");
        srv.println("</BODY></HTML>");
    }

    @SuppressWarnings("unchecked")
    private static void respondJSON(BaseServlet srv, List<StarBean> stars, String quad) throws IOException
    {
        JSONObject root = new JSONObject();
        root.put("quad", quad);
        JSONArray jStars = new JSONArray();
        root.put("stars", jStars);
        Map<Long, JSONObject> oidToStar = new HashMap<Long, JSONObject>();
        Map<Long, Integer> oidToIndex = new HashMap<Long, Integer>();
        List<StarBean> todoChildren = new ArrayList<StarBean>();
        for (StarBean star : stars)
        {
            if (star.getParent() > 0)
            {   // child
                if (!addChild(star, oidToStar))
                    todoChildren.add(star);
            }
            else
            {   // parent
                System.out.println("Parent: "+star.getName());
                JSONObject jStar = makeStar(star);
                jStars.add(jStar);
                oidToStar.put(star.getOID(), jStar);
                oidToIndex.put(star.getOID(), jStars.size() - 1);
            }
        }
        for (StarBean star : todoChildren)
            addChild(star, oidToStar);
        String json = JSONValue.toJSONString(root);
        srv.setContentType("text/json");
        srv.println(json);
    }

    private static void respondCSV(BaseServlet srv, List<StarBean> stars) throws IOException
    {
        srv.setContentType("text/plain");
        double linkSize = srv.getDoubleParameter("linkSize", -1);
        int firstLine = srv.getIntParameter("firstLine", 0);
        int lastLine = srv.getIntParameter("lastLine", Integer.MAX_VALUE);
        StringBuffer line = new StringBuffer();
        int lineNo = 0;
        for (StarBean star : stars)
        {
            line.setLength(0);
            line.append("O");
            line.append(","+star.getName());
            line.append(","+star.getSpectra());
            line.append(","+String.format("%.2f", star.getX()));
            line.append(","+String.format("%.2f", star.getY()));
            line.append(","+String.format("%.2f", star.getZ()));
            if ((lineNo >= firstLine) && (lineNo < lastLine))
                srv.println(line.toString());
            lineNo++;
        }
        if (linkSize > 0)
        {
            //int linkLimit = getIntParameter("linkLimit", 0);
            List<StarLink> links = StarExtraLogic.findLinks(stars, linkSize);
            for (Iterator<StarLink> i = links.iterator(); i.hasNext(); )
                if (i.next().getDistance() < 1)
                    i.remove();
            //if (linkLimit > 0)
            //    StarExtraLogic.pruneLinks(links, linkLimit);
            for (StarLink link : links)
            {
                line.setLength(0);
                line.append("L");
                line.append(","+String.format("%.2f", link.getDistance()));
                line.append(","+String.format("%.2f", link.getStar1().getX()));
                line.append(","+String.format("%.2f", link.getStar1().getY()));
                line.append(","+String.format("%.2f", link.getStar1().getZ()));
                line.append(","+String.format("%.2f", link.getStar2().getX()));
                line.append(","+String.format("%.2f", link.getStar2().getY()));
                line.append(","+String.format("%.2f", link.getStar2().getZ()));
                if ((lineNo >= firstLine) && (lineNo < lastLine))
                    srv.println(line.toString());
                lineNo++;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static JSONObject makeStar(StarBean star)
    {
        JSONObject jStar = new JSONObject();
        jStar.put("oid", star.getOID());
        jStar.put("x", star.getX());
        jStar.put("y", star.getY());
        jStar.put("z", star.getZ());
        jStar.put("name", star.getName());
        jStar.put("spectra", star.getSpectra());
        jStar.put("absmag", star.getAbsMag());
        jStar.put("color", StarExtraLogic.getStarColorRGB(star));
        return jStar;
    }
    
    @SuppressWarnings("unchecked")
    private static boolean addChild(StarBean child, Map<Long, JSONObject> oidToStar)
    {
        //System.out.println("Child: "+child.getName());
        JSONObject jParent = oidToStar.get(child.getParent());
        if (jParent == null)
            return false;
        JSONArray jChildren = (JSONArray)jParent.get("children");
        if (jChildren == null)
        {
            jChildren = new JSONArray();
            jParent.put("children", jChildren);
        }
        JSONObject jChild = makeStar(child);
        jChildren.add(jChild);
        return true;
    }
}
