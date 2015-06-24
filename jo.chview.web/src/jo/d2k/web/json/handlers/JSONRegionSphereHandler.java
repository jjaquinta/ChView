package jo.d2k.web.json.handlers;

import jo.d2k.data.data.RegionQuadBean;
import jo.d2k.web.json.JSONGenericHandler;

public class JSONRegionSphereHandler extends JSONGenericHandler
{
    public JSONRegionSphereHandler()
    {
        super(RegionQuadBean.class);
        mSimpleProps.put("id", "URI");
        mSimpleProps.put("x", "X");
        mSimpleProps.put("y", "Y");
        mSimpleProps.put("z", "Z");
        mSimpleProps.put("x2", "X2");
        mArrayProps.put("stars", "Stars");
    }
}
