package jo.chview.web.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jo.chview.web.servlet.BaseServlet;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.RuntimeLogic;
import jo.d2k.data.logic.StarLogic;
import jo.util.logic.CSVLogic;
import jo.util.utils.FormatUtils;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class StarRequestLogic
{

    public static void doStarRequest(BaseServlet srv, String quad, long oid) throws IOException
    {
        StarBean star = StarLogic.getByQuadrantID(quad, oid);
        if (star == null)
        {
            srv.respondError("Unknown star oid="+oid+", quadrant="+quad);
            return;
        }
        String format = srv.getStringParameter("f", "html");
        if ("html".equals(format))
            StarRequestLogic.respondHTML(srv, star);
        //else if ("svg".equals(format))
        //    StarRequestLogic.respondSVG(srv, star);
        else if ("json".equals(format))
            StarRequestLogic.respondJSON(srv, star);
        else if ("csv".equals(format))
            StarRequestLogic.respondCSV(srv, star);
        else
            srv.respondError("Unknown format: "+format);
    }

    private static void respondHTML(BaseServlet srv, StarBean star) throws IOException
    {
        srv.println("<HTML>");
        srv.println("<HEAD><TITLE>"+RuntimeLogic.getInstance().getDataSource().getName()+" - Atlas</TITLE></HEAD>");
        srv.println("<BODY><H1>"+RuntimeLogic.getInstance().getDataSource().getName()+" - Atlas</H1>");
        srv.println("<TABLE>");
        srv.println("<TR><TD>Name</TD><TD>"+star.getName()+"</TD></TR>");
        srv.println("<TR><TD>Quad</TD><TD>");
        srv.println("<A HREF='"+srv.getRequest().getContextPath()+"/chview/"+star.getQuadrant()+"?f=html'>");
        srv.println(star.getQuadrant());
        srv.println("</A");
        srv.println("</TD></TR>");
        srv.println("<TR><TD>x</TD><TD>"+FormatUtils.formatDouble(star.getX(), 2)+"</TD></TR>");
        srv.println("<TR><TD>y</TD><TD>"+FormatUtils.formatDouble(star.getY(), 2)+"</TD></TR>");
        srv.println("<TR><TD>z</TD><TD>"+FormatUtils.formatDouble(star.getZ(), 2)+"</TD></TR>");
        srv.println("<TR><TD>Spectra</TD><TD>"+star.getSpectra()+"</TD></TR>");
        srv.println("<TR><TD>Absolute Magnitude</TD><TD>"+FormatUtils.formatDouble(star.getAbsMag(), 2)+"</TD></TR>");
        if (star.getParentRef() != null)
        {
            srv.println("<TR><TD>Parent</TD><TD>");
            srv.println("<A HREF='"+srv.getRequest().getContextPath()+"/chview/"+star.getParentRef().getQuadrant()+"/"+star.getParentRef().getOID()+"?f=html'>"+star.getParentRef().getName()+"</A>");
            srv.println("</TD></TR>");
        }
        srv.println("</TABLE>");
        srv.println("<HR/>");
        srv.println("<TABLE>");
        srv.println("<TR><TD>Common Name</TD><TD>"+star.getCommonName()+"</TD></TR>");
        srv.println("<TR><TD>Hipparcos</TD><TD>"+star.getHIPName()+"</TD></TR>");
        srv.println("<TR><TD>Gliese-Jahreiﬂ</TD><TD>"+star.getGJName()+"</TD></TR>");
        srv.println("<TR><TD>Henry Draper</TD><TD>"+star.getHDName()+"</TD></TR>");
        srv.println("<TR><TD>Harvard Revised</TD><TD>"+star.getHRName()+"</TD></TR>");
        srv.println("<TR><TD>SAO</TD><TD>"+star.getSAOName()+"</TD></TR>");
        srv.println("<TR><TD>2Mass</TD><TD>"+star.getTwoMassName()+"</TD></TR>");
        srv.println("</TABLE>");

        srv.println("</BODY></HTML>");
    }

    private static void respondJSON(BaseServlet srv, StarBean star) throws IOException
    {
        JSONObject jStar = StarsRequestLogic.makeStar(star, true);
        String json = JSONValue.toJSONString(jStar);
        srv.setContentType("text/json");
        srv.println(json);
    }

    private static void respondCSV(BaseServlet srv, StarBean star) throws IOException
    {
        srv.setContentType("text/plain");
        List<Object> line = new ArrayList<Object>();
        StarsRequestLogic.makeStar(star, line);
        srv.println(CSVLogic.toCSVLine(line));
    }

}
