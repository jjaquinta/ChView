package jo.d4w.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import jo.d2k.data.data.RegionBean;
import jo.d2k.data.data.StarBean;
import jo.util.html.URIBuilder;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.DoubleUtils;

public class D4WURILogic
{
    public static Object getFromURI(String uri)
    {
        URIBuilder u = new URIBuilder(uri);
        Object bean = null;
        if ("region".equals(u.getScheme()))
        {
            RegionBean stars = null;
            if ("cube".equals(u.getHost()))
            {
                List<Double> v = toDoubles(u.getPath());
                if (v.size() != 6)
                    throw new IllegalStateException("Expected six parameters '"+u.getPath()+"'");
                stars = D4WStarLogic.getAllWithin(v.get(0), v.get(1), v.get(2), v.get(3), v.get(4), v.get(5));
            }
            else if ("sphere".equals(u.getHost()))
            {
                List<Double> v = toDoubles(u.getPath());
                if (v.size() != 4)
                    throw new IllegalStateException("Expected four parameters '"+u.getPath()+"'");
                stars = D4WStarLogic.getAllWithin(v.get(0), v.get(1), v.get(2), v.get(3));
            }
            else if ("quad".equals(u.getHost()))
                stars = D4WStarLogic.getByQuadrant(u.getPath().substring(1));
            if (BooleanUtils.parseBoolean(u.getQuery("nochildren")) && (stars != null))
                for (Iterator<StarBean> i = stars.getStars().iterator(); i.hasNext(); )
                    if (i.next().getParent() > 0)
                        i.remove();
            bean = stars;
        }
        else if ("star".equals(u.getScheme()))
        {
            bean = D4WStarLogic.getByURI(uri);
        }
        else if ("body".equals(u.getScheme()))
        {
            bean = D4WSystemLogic.getByURI(uri);
        }
        else if ("pop".equals(u.getScheme()))
        {
            bean = D4WPopulationLogic.getByURI(uri);
        }
        else if ("cargolots".equals(u.getScheme()))
        {
            bean = CargoLogic.getLotsByURI(uri);
        }
        else if ("cargo".equals(u.getScheme()))
        {
            bean = CargoLogic.getByURI(uri);
        }
        //else
        //    throw new IllegalStateException("Unknown uri scheme '"+u.getScheme()+"'");
        return bean;
    }

    private static List<Double> toDoubles(String str)
    {
        List<Double> v = new ArrayList<Double>(); 
        for (StringTokenizer st = new StringTokenizer(str, "/"); st.hasMoreElements(); )
            v.add(DoubleUtils.parseDouble(st.nextToken()));
        return v;
    }
}
