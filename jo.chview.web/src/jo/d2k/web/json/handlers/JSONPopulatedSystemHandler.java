package jo.d2k.web.json.handlers;

import jo.d4w.data.PopulatedSystemBean;

public class JSONPopulatedSystemHandler extends JSONPopulatedObjectHandler
{
    public JSONPopulatedSystemHandler()
    {
        super(PopulatedSystemBean.class);
        mSkipProps.add("populationsIndex");
    }
}
