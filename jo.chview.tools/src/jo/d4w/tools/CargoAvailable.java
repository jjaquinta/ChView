package jo.d4w.tools;

import java.io.UnsupportedEncodingException;

import jo.d4w.web.data.DockCargoBean;
import jo.d4w.web.data.OnDockBean;
import jo.d4w.web.data.PortBean;
import jo.d4w.web.logic.DockLogic;
import jo.d4w.web.logic.URILogic;
import jo.util.html.URIBuilder;

public class CargoAvailable
{
    private static String PORT = "port://11901070f@070f00010001/Northfleet";
    
    public static void main(String[] args) throws UnsupportedEncodingException
    {        
        PortBean port = (PortBean)URILogic.getFromURI(PORT);
        URIBuilder portURI = new URIBuilder(port.getURI());
        URIBuilder ondockURI = new URIBuilder();
        ondockURI.setScheme("ondock");
        ondockURI.setAuthority(portURI.getAuthority());
        ondockURI.setPath("/"+port.getName());
        ondockURI.setQuery("date", "1100-001");
        OnDockBean ondock = DockLogic.getOnDock(new URIBuilder("ondock://11901070f@070f00010001/Northfleet?date=1100-1"));
        for (DockCargoBean cargo : ondock.getCargo())
            System.out.println(cargo.getName()+": "+cargo.getSalePrice());
    }
}
