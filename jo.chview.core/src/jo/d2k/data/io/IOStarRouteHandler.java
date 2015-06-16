package jo.d2k.data.io;

import java.util.List;

import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarRouteBean;
import jo.util.dao.IOBeanHandler2;

public interface IOStarRouteHandler extends IOBeanHandler2<StarRouteBean>
{
    public List<StarRouteBean> findAllLinking(List<StarBean> stars);
}
