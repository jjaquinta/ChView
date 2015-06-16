package jo.d4w.data;

import jo.d2k.data.logic.stargen.data.BodyBean;

public class PopulatedWorldBean extends PopulatedObjectBean
{
    private BodyBean    mBody;

    public BodyBean getBody()
    {
        return mBody;
    }

    public void setBody(BodyBean body)
    {
        mBody = body;
    }
}
