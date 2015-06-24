package jo.d2k.web.json.handlers;

import jo.d2k.data.data.RegionCubeBean;
import jo.d2k.web.json.JSONGenericHandler;

public class JSONRegionCubeHandler extends JSONGenericHandler
{
    public JSONRegionCubeHandler()
    {
        super(RegionCubeBean.class);
        mSimpleProps.put("id", "URI");
        mSimpleProps.put("x1", "X1");
        mSimpleProps.put("y1", "Y1");
        mSimpleProps.put("z1", "Z1");
        mSimpleProps.put("x2", "X2");
        mSimpleProps.put("y2", "Y2");
        mSimpleProps.put("z2", "Z2");
        mArrayProps.put("stars", "Stars");
    }
}
