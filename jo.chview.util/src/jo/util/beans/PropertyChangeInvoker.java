package jo.util.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.widgets.Widget;

public class PropertyChangeInvoker implements PropertyChangeListener
{
    private Widget      mUIDependence;
    private Object      mObject;
    private String      mMethod;
    private Object[]    mArgs;

    public PropertyChangeInvoker()
    {
        mUIDependence = null;
        mObject = null;
        mMethod = null;
        mArgs = new Object[0];
    }
    
    public PropertyChangeInvoker(Object object, String method)
    {
        this();
        mObject = object;
        mMethod = method;
        if (mObject instanceof Widget)
            mUIDependence = (Widget)mObject;
    }
    
    public PropertyChangeInvoker(Object object, String method, Widget uiDependence)
    {
        this(object, method);
        mUIDependence = uiDependence;
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (mUIDependence != null)
            if (mUIDependence.isDisposed())
            {
                BeanLogic.invoke(evt.getSource(), "removePropertyChangeListener", new Object[] { this });
                return;
            }
        if (mObject != null)
            BeanLogic.invoke(mObject, mMethod, mArgs);
    }

    public Widget getUIDependence()
    {
        return mUIDependence;
    }

    public void setUIDependence(Widget uIDependence)
    {
        mUIDependence = uIDependence;
    }

    public Object getObject()
    {
        return mObject;
    }

    public void setObject(Object object)
    {
        mObject = object;
    }

    public String getMethod()
    {
        return mMethod;
    }

    public void setMethod(String method)
    {
        mMethod = method;
    }

    public Object[] getArgs()
    {
        return mArgs;
    }

    public void setArgs(Object[] args)
    {
        mArgs = args;
    }

}
