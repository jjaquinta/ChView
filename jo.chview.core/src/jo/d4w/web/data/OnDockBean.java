package jo.d4w.web.data;

import java.util.ArrayList;
import java.util.List;

import jo.d2k.data.data.URIBean;
import jo.util.beans.Bean;

public class OnDockBean extends Bean implements URIBean
{
    private String  mURI;
    private List<DockCargoBean>  mCargo;
    
    public OnDockBean()
    {
        mCargo = new ArrayList<DockCargoBean>();
    }
    
    public String getURI()
    {
        return mURI;
    }
    public void setURI(String uRI)
    {
        mURI = uRI;
    }
    public List<DockCargoBean> getCargo()
    {
        return mCargo;
    }
    public void setCargo(List<DockCargoBean> cargo)
    {
        mCargo = cargo;
    }
}
