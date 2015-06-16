package jo.chview.web.logic;

import java.io.IOException;

import jo.chview.web.servlet.BaseServlet;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.logic.StarLogic;

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
        //else if ("json".equals(format))
        //    StarRequestLogic.respondJSON(srv, star);
        //else if ("csv".equals(format))
        //    StarRequestLogic.respondCSV(srv, star);
        else
            srv.respondError("Unknown format: "+format);
    }

    private static void respondHTML(BaseServlet srv, StarBean star) throws IOException
    {
        srv.println("<HTML>");
        srv.println("<HEAD><TITLE>Dawnfire 2000 - Atlas</TITLE></HEAD>");
        srv.println("<BODY><H1>Dawnfire 2000 - Atlas</H1>");
        srv.println("<TABLE>");
        srv.println("<TR><TD>Name</TD><TD>"+star.getName()+"</TD></TR>");
        srv.println("<TR><TD>Quad</TD><TD>");
        srv.println("<A HREF='"+srv.getRequest().getContextPath()+"/chview/"+star.getQuadrant()+"?f=html'>");
        srv.println(star.getQuadrant());
        srv.println("</A");
        srv.println("</TD></TR>");
        srv.println("<TR><TD>x</TD><TD>"+star.getX()+"</TD></TR>");
        srv.println("<TR><TD>y</TD><TD>"+star.getY()+"</TD></TR>");
        srv.println("<TR><TD>z</TD><TD>"+star.getZ()+"</TD></TR>");
        srv.println("<TR><TD>spectra</TD><TD>"+star.getSpectra()+"</TD></TR>");
        srv.println("<TR><TD>absmag</TD><TD>"+star.getAbsMag()+"</TD></TR>");
        srv.println("</TABLE>");

        srv.println("</BODY></HTML>");
    }

}
