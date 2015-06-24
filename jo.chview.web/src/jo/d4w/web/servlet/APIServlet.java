package jo.d4w.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import jo.chview.web.servlet.BaseServlet;
import jo.d2k.data.data.URIBean;
import jo.d2k.web.json.JSONLogic;
import jo.d4w.web.logic.ApplicationLogic;
import jo.d4w.web.logic.URILogic;
import jo.util.utils.xml.XMLUtils;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.w3c.dom.Document;

/**
 * Servlet implementation class APIServlet
@WebServlet(name = "api", description = "Serves up Stellar data", urlPatterns = { "/api" })
 */
public class APIServlet extends BaseServlet
{
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public APIServlet()
    {
        super();
        ApplicationLogic.init();
    }

    @Override
    protected void doRequest() throws IOException
    {
        String format = getStringParameter("f", "html");
        String uri = getStringParameter("u", "");
        Map<String,String> params = getAllParameters();
        Object bean = URILogic.getFromURI(uri);
        transmitObj(uri, format, bean, params);
    }

    private void transmitObj(String uri, String style, Object bean, Map<String,String> params) throws IOException
    {
        getResponse().setCharacterEncoding("utf-8");
        PrintWriter out = getResponse().getWriter();     
        if (bean == null)
        {
            getResponse().setContentType("text/plain");
            out.println("Unresolvable URI: "+uri);
            return;
        }
        System.out.println(uri+" -> "+bean.getClass().getName()+" ("+((bean instanceof URIBean) ? ((URIBean)bean).getURI() : bean.toString())+")");
        if ("JSON".equalsIgnoreCase(style))
        {
            JSONObject obj;
            if (bean instanceof JSONObject)
                obj = (JSONObject)bean;
            else
                obj = JSONLogic.getJSON(bean, params);
            String json = JSONValue.toJSONString(obj);
            getResponse().setContentType("text/json");
            out.println(json);
        }
        else //if ("XML".equalsIgnoreCase(style))
        {
            Document doc = JSONLogic.getXML(bean, params);
            String xml = XMLUtils.writeString(doc.getFirstChild());
            getResponse().setContentType("text/xml");
            out.println("<?xml version=\"1.0\"?>");
            out.println(xml);
        }
    }
}
