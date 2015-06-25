package jo.d4w.web.data;

import java.util.ArrayList;
import java.util.List;

import jo.d2k.data.data.URIBean;
import jo.util.beans.Bean;

public class PortsBean extends Bean implements URIBean
{
    private String  mURI;
    private double  mX;
    private double  mY;
    private double  mZ;
    private List<PortBean>  mPorts;
    
    public PortsBean()
    {
        mPorts = new ArrayList<PortBean>();
    }
    
    public String toString()
    {
        return mURI;
    }
    
    public String getURI()
    {
        return mURI;
    }
    public void setURI(String uRI)
    {
        mURI = uRI;
    }
    public List<PortBean> getPorts()
    {
        return mPorts;
    }
    public void setPorts(List<PortBean> ports)
    {
        mPorts = ports;
    }

    public double getX()
    {
        return mX;
    }

    public void setX(double x)
    {
        mX = x;
    }

    public double getY()
    {
        return mY;
    }

    public void setY(double y)
    {
        mY = y;
    }

    public double getZ()
    {
        return mZ;
    }

    public void setZ(double z)
    {
        mZ = z;
    }
    
}
