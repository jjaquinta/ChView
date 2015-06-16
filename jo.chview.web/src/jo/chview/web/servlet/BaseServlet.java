package jo.chview.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.DoubleUtils;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.LongUtils;

/**
 * Servlet implementation class AtlasServlet
 */
public abstract class BaseServlet extends HttpServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private Map<Thread, HttpServletRequest> mRequests;
    private Map<Thread, HttpServletResponse> mResponses;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BaseServlet()
    {
        super();
        mRequests = new HashMap<Thread, HttpServletRequest>();
        mResponses = new HashMap<Thread, HttpServletResponse>();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        mRequests.put(Thread.currentThread(), request);
        mResponses.put(Thread.currentThread(), response);
        try
        {
            doRequest();
        }
        catch (Exception e)
        {
            getResponse().setContentType("text/plain");
            PrintWriter ps = new PrintWriter(getResponse().getWriter());
            e.printStackTrace(ps);
        }
        Writer out = getResponse().getWriter();
        out.close();
        mRequests.remove(Thread.currentThread());
        mResponses.remove(Thread.currentThread());
    }
    
    protected abstract void doRequest() throws IOException;
    
    public HttpServletRequest getRequest()
    {
        return mRequests.get(Thread.currentThread());
    }

    public HttpServletResponse getResponse()
    {
        return mResponses.get(Thread.currentThread());
    }
    
    public void respondError(String err) throws IOException
    {
        getResponse().setContentType("text/html");
        println("<HTML><BODY><H1>Error:</H1><P>");
        println(err);       
        println("</P></BODY></HTML>");
    }

    public void setContentType(String type)
    {
        getResponse().setContentType(type);
    }

    public void setHTML()
    {
        setContentType("text/html");
    }
    
    public void setPlain()
    {
        setContentType("text/plain");
    }
    
    public void print(String msg) throws IOException
    {
        PrintWriter out = getResponse().getWriter();
        out.print(msg);
    }

    public void println(String msg) throws IOException
    {
        PrintWriter out = getResponse().getWriter();
        out.println(msg);
    }
    
    public Map<String,String> getAllParameters()
    {
        Map<String,String[]> complexParams = getRequest().getParameterMap();
        Map<String,String> simpleParams = new HashMap<String, String>();
        for (String key : complexParams.keySet())
        {
            String[] val = complexParams.get(key);
            if ((val != null) && (val.length > 0))
                simpleParams.put(key, convVal(val[0]));
        }
        return simpleParams;
    }
    
    public String getStringParameter(String key)
    {
        String val = getRequest().getParameter(key);
        if (val != null)
            return convVal(val);
        val = getRequest().getParameter(key.toUpperCase());
        if (val != null)
            return convVal(val);
        val = getRequest().getParameter(key.toLowerCase());
        return convVal(val);
    }
    
    private String convVal(String val)
    {
        if (val == null)
            return val;
        byte[] bytes = val.getBytes();
        try
        {
            return new String(bytes, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return val;
        }
    }
    
    public String getStringParameter(String key, String def)
    {
        String val = getStringParameter(key);
        if (val != null)
            return val;
        else
            return def;
    }

    public boolean getBooleanParameter(String key, boolean def)
    {
        String val = getStringParameter(key);
        if (val != null)
            return BooleanUtils.parseBoolean(val);
        else
            return def;
    }

    public int getIntParameter(String key, int def)
    {
        String val = getStringParameter(key);
        if (val != null)
            return IntegerUtils.parseInt(val);
        else
            return def;
    }

    public long getLongParameter(String key, long def)
    {
        String val = getStringParameter(key);
        if (val != null)
            return LongUtils.parseLong(val);
        else
            return def;
    }

    public double getDoubleParameter(String key, double def)
    {
        String val = getStringParameter(key);
        if (val != null)
            return DoubleUtils.parseDouble(val);
        else
            return def;
    }
}
