package jo.chview.web.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import jo.chview.web.logic.StarRequestLogic;
import jo.chview.web.logic.StarsRequestLogic;
import jo.d2k.data.logic.DataLogic;

/**
 * Servlet implementation class ChViewServlet
@WebServlet(
        description = "ChView Data", 
        urlPatterns = { "/chview" }, 
        initParams = { 
                @WebInitParam(name = "dataSourceType", value = "MYSQL", description = "Underlying type of data source: MYSQL, DERBY, MEM"), 
                @WebInitParam(name = "dataSourceName", value = "Dawnfire 2000", description = "Display name of data source"), 
                @WebInitParam(name = "dataSourceURI", value = "jdbc:mysql://www.ocean-of-storms.com/ocean50_d2k?user=ocean50_d2kchuck&amp;password=1oBBTGd6Z5uq", description = "JDBC URI to data source"), 
                @WebInitParam(name = "dataSourceReadyOnly", value = "true", description = "Read/Write capability of data source"), 
                @WebInitParam(name = "dataSourceDefault", value = "true", description = "If this is the default data source")
        })
 */
public class ChViewServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChViewServlet() {
        super();
    }
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        String name = config.getInitParameter("dataSourceName");
        String type = config.getInitParameter("dataSourceType");
        String uri = config.getInitParameter("dataSourceURI");
        String readOnly = config.getInitParameter("dataSourceReadOnly");
        String def = config.getInitParameter("dataSourceDefault");
        DataLogic.addDataSource(name, type, uri, readOnly, def);
        DataLogic.setDataSource(DataLogic.getDefaultDataSource());
    }

    @Override
    protected void doRequest() throws IOException
    {
        String pathInfo = getRequest().getPathInfo();
        System.out.println("pathInfo="+pathInfo);
        if (pathInfo == null)
            pathInfo = "000";
        if (pathInfo.startsWith("/"))
            pathInfo = pathInfo.substring(1);
        String[] path = pathInfo.split("/");
        if (path.length == 1)
            StarsRequestLogic.doStarsRequest(this, path[0]);
        else if (path.length == 2)
            StarRequestLogic.doStarRequest(this, path[0], Long.parseLong(path[1]));
        else
            respondError("Unknown request '"+pathInfo);
    }
}
