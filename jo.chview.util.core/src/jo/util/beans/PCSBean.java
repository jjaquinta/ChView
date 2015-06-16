package jo.util.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import jo.util.utils.BeanUtils;

public class PCSBean extends Bean
{
    private PropChangeSupport   mPCS;
    
    // constructor
    public PCSBean()
    {
        mPCS = new PropChangeSupport(this);
    }
    
    // listeners
    public void addPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
        mPCS.addPropertyChangeListener(prop, pcl);
    }
    public void addPropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.addPropertyChangeListener(pcl);
    }
    public void addUIPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
        mPCS.addUIPropertyChangeListener(prop, pcl);
    }
    public void addUIPropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.addUIPropertyChangeListener(pcl);
    }
    public void addWeakPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
        mPCS.addWeakPropertyChangeListener(prop, pcl);
    }
    public void addWeakPropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.addWeakPropertyChangeListener(pcl);
    }
    public void removePropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.removePropertyChangeListener(pcl);
    }

    protected void queuePropertyChange(String name, Object oldVal, Object newVal)
    {
        mPCS.queuePropertyChange(name, oldVal, newVal);
    }

    protected void queuePropertyChange(String name, int oldVal, int newVal)
    {
        mPCS.queuePropertyChange(name, oldVal, newVal);
    }

    protected void queuePropertyChange(String name, long oldVal, long newVal)
    {
        mPCS.queuePropertyChange(name, oldVal, newVal);
    }

    protected void queuePropertyChange(String name, double oldVal, double newVal)
    {
        mPCS.queuePropertyChange(name, oldVal, newVal);
    }

    protected void queuePropertyChange(String name, boolean oldVal, boolean newVal)
    {
        mPCS.queuePropertyChange(name, oldVal, newVal);
    }
    
    protected void firePropertyChange()
    {
        mPCS.firePropertyChange();
    }
    
    protected void firePropertyChange(PropertyChangeEvent queueEvent)
    {
        mPCS.firePropertyChange(queueEvent);
    }
    
    protected void firePropertyChange(Object newValue)
    {
        mPCS.firePropertyChange(newValue);
    }
    
    protected void firePropertyChanges()
    {
        mPCS.firePropertyChanges();
    }
    
    public void fireMonotonicPropertyChange(String name)
    {
        mPCS.fireMonotonicPropertyChange(name, BeanUtils.get(this, name));
    }
    
    public void fireMonotonicPropertyChange(String name, Object val)
    {
        mPCS.fireMonotonicPropertyChange(name, val);
    }

    public boolean isBroadcast() {
        return mPCS.isBroadcast();
    }

    public void setBroadcast(boolean broadcast) {
        mPCS.setBroadcast(broadcast);
    }
    
    public void suspend()
    {
        mPCS.suspend();
    }
    
    public boolean isSuspend()
    {
        return mPCS.isSuspend();
    }
    
    public void resume()
    {
        mPCS.resume();
    }
}
