package jo.d4w.web.logic;

import java.util.Iterator;
import java.util.List;

import jo.d4w.logic.D4WURILogic;
import jo.util.html.URIBuilder;
import jo.util.utils.obj.StringUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class URILogic
{
	public static Object getFromURI(String uri)
	{
        String json = ApplicationLogic.getFromStore(uri);
        if (!StringUtils.isTrivial(json))
            return JSONValue.parse(json);
        URIBuilder u = new URIBuilder(uri);
        System.out.println("URILogic: "+uri+" -> "+u.toString());
        uri = u.toString();
        Object bean;
        if ("listof".equals(u.getScheme()))
            bean = getListOf(u);
        else if ("ports".equals(u.getScheme()))
            bean = PortLogic.getPorts(u);
        else if ("port".equals(u.getScheme()))
            bean = PortLogic.getPort(u);
        else if ("ondock".equals(u.getScheme()))
            bean = DockLogic.getOnDock(u);
        else if ("inhold".equals(u.getScheme()))
            bean = DockLogic.getInHold(u);
//        else if ("quest".equals(u.getScheme()))
//            bean = MiniQuestLogic.getFromURI(u);
//        else if ("shipmt".equals(u.getScheme()))
//            bean = ShipMTLogic.getShipFromURI(u);
//        else if ("corsairs".equals(u.getScheme()))
//            bean = ShipMTLogic.getCorsairsFromURI(u);
//        else if ("corsair".equals(u.getScheme()))
//            bean = ShipMTLogic.getCorsairFromURI(u);
//        else if ("merchants".equals(u.getScheme()))
//            bean = ShipMTLogic.getMerchantsFromURI(u);
//        else if ("merchant".equals(u.getScheme()))
//            bean = ShipMTLogic.getMerchantFromURI(u);
//        else if ("escorts".equals(u.getScheme()))
//            bean = ShipMTLogic.getEscortsFromURI(u);
//        else if ("escort".equals(u.getScheme()))
//            bean = ShipMTLogic.getEscortFromURI(u);
        else
            bean = D4WURILogic.getFromURI(uri); // look up in canonical form
//        if (bean instanceof SystemBean)
//            customizeSystem(u, (SystemBean)bean);
//        else if (bean instanceof MainWorldsBean)
//            customizeMainWorlds(u, (MainWorldsBean)bean);
//        else if (bean instanceof CargoBean)
//            bean = customizeCargo(u, (CargoBean)bean);
//        else if (bean instanceof CargoLotBean)
//            bean = customizeCargoLot(u, (CargoLotBean)bean);
        return bean;
    }
	
	@SuppressWarnings("unchecked")
    private static Object getListOf(URIBuilder u)
	{
	    JSONArray arr = new JSONArray();
	    List<String> uris = ApplicationLogic.getListFromStore(u.getAuthority());
	    if (u.getQuery().size() > 0)
	        for (Iterator<String> i = uris.iterator(); i.hasNext(); )
	        {
	            String uri = i.next();
	            URIBuilder u2 = new URIBuilder(uri);
	            for (Object k : u.getQuery().keySet())
	            {
	                String v1 = u.getQuery((String)k);
	                String v2 = u2.getQuery((String)k);
	                if (!v1.equals(v2))
	                {
	                    i.remove();
	                    break;
	                }
	            }
	        }
        arr.addAll(uris);
        JSONObject ret = new JSONObject();
        ret.put("list", arr);
	    return ret;
	}
}
