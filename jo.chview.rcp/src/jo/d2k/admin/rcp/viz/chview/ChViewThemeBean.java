package jo.d2k.admin.rcp.viz.chview;

import java.util.Properties;

import jo.util.beans.PCSBean;

public class ChViewThemeBean extends PCSBean
{
    private String  mName;
    private Properties  mProps = new Properties();
    
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public Properties getProps()
    {
        return mProps;
    }
    public void setProps(Properties props)
    {
        mProps = props;
    }
}
