package jo.util.beans;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import jo.util.logic.ThreadLogic;
import jo.util.utils.DebugUtils;

public class PropChangeSupport
{
    private Set<PropertyChangeListener>                              mGenericListeners;
    private Map<String,Set<PropertyChangeListener>>                  mSpecificListerners;
    private Set<PropertyChangeListener>                              mGenericUIListeners;
    private Map<String,Set<PropertyChangeListener>>                  mSpecificUIListerners;
    private Set<WeakReference<PropertyChangeListener>>               mGenericWeakListeners;
    private Map<String, Set<WeakReference<PropertyChangeListener>>>  mSpecificWeakListerners;
    
    private Stack<PropertyChangeEvent>                               mQueueEvent;
    private boolean                                                  mBroadcast;
    private int                                                      mSuspendCount;
    
    private Object                                                   mThis;

    // constructor
    public PropChangeSupport(Object obj)
    {
        mThis = obj;
        mGenericListeners = new HashSet<PropertyChangeListener>();
        mSpecificListerners = new HashMap<String, Set<PropertyChangeListener>>();
        mGenericUIListeners = new HashSet<PropertyChangeListener>();
        mSpecificUIListerners = new HashMap<String, Set<PropertyChangeListener>>();
        mGenericWeakListeners = new HashSet<WeakReference<PropertyChangeListener>>();
        mSpecificWeakListerners = new HashMap<String, Set<WeakReference<PropertyChangeListener>>>();
        mQueueEvent = new Stack<PropertyChangeEvent>();
        mBroadcast = true;
    }
    
    public void dispose()
    {
        synchronized (mSpecificListerners)
        {
            mSpecificListerners.clear();
        }
        synchronized (mGenericListeners)
        {
            mGenericListeners.clear();
        }
        synchronized (mSpecificUIListerners)
        {
            mSpecificUIListerners.clear();
        }
        synchronized (mGenericUIListeners)
        {
            mGenericUIListeners.clear();
        }
    }
    
    private String shortName(Object o)
    {
        Class<?> c;
        if (o instanceof Class)
            c = (Class<?>)o;
        else
            c = o.getClass();
        String ret = c.getName();
        int off = ret.lastIndexOf(".");
        return ret.substring(off+1);
    }

    private static WeakReference<PropertyChangeListener> findRef(Collection<WeakReference<PropertyChangeListener>> list, PropertyChangeListener o)
    {
        for (Iterator<WeakReference<PropertyChangeListener>> i = list.iterator(); i.hasNext(); )
        {
            WeakReference<PropertyChangeListener> ref = i.next();
            if (o == ref.get())
                return ref; // already stored
            else if (ref.get() == null)
                i.remove(); // tidy
        }
        return null;
    }
    
    // listeners
    public void addPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
        if (pcl == null)
            throw new IllegalArgumentException("Adding null PropertyChangeListener");
        synchronized (mSpecificListerners)
        {
            Set<PropertyChangeListener> listeners = mSpecificListerners.get(prop);
            if (listeners == null)
            {
                listeners = new HashSet<PropertyChangeListener>();
                mSpecificListerners.put(prop, listeners);
            }
            listeners.add(pcl);
        }
        DebugUtils.trace(shortName(pcl)+" listening for changes to "+prop+" on "+shortName(mThis));
    }
    public void addPropertyChangeListener(PropertyChangeListener pcl)
    {
        if (pcl == null)
            throw new IllegalArgumentException("Adding null PropertyChangeListener");
        synchronized (mGenericListeners)
        {
            mGenericListeners.add(pcl);
        }
        DebugUtils.trace(shortName(pcl)+" listening for changes on "+shortName(mThis));
    }
    public void addUIPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
        if (pcl == null)
            throw new IllegalArgumentException("Adding null PropertyChangeListener");
        synchronized (mSpecificUIListerners)
        {
            Set<PropertyChangeListener> listeners = mSpecificUIListerners.get(prop);
            if (listeners == null)
            {
                listeners = new HashSet<PropertyChangeListener>();
                mSpecificUIListerners.put(prop, listeners);
            }
            listeners.add(pcl);
        }
        DebugUtils.trace(shortName(pcl)+" listening for changes to "+prop+" on "+shortName(mThis)+" (UI)");
    }
    public void addUIPropertyChangeListener(PropertyChangeListener pcl)
    {
        if (pcl == null)
            throw new IllegalArgumentException("Adding null PropertyChangeListener");
        synchronized (mGenericUIListeners)
        {
            mGenericUIListeners.add(pcl);
        }
        DebugUtils.trace(shortName(pcl)+" listening for changes on "+shortName(mThis)+" (UI)");
    }
    public void addWeakPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
        if (pcl == null)
            throw new IllegalArgumentException("Adding null PropertyChangeListener");
        synchronized (mSpecificWeakListerners)
        {
            Set<WeakReference<PropertyChangeListener>> listeners = mSpecificWeakListerners.get(prop);
            if (listeners == null)
            {
                listeners = new HashSet<WeakReference<PropertyChangeListener>>();
                mSpecificWeakListerners.put(prop, listeners);
            }
            else
            {
                WeakReference<PropertyChangeListener> ref = findRef(listeners, pcl);
                if (ref != null)
                    return; // already stored
            }
            listeners.add(new WeakReference<PropertyChangeListener>(pcl));
        }
        DebugUtils.trace(shortName(pcl)+" listening weakly for changes to "+prop+" on "+shortName(mThis));
    }
    public void addWeakPropertyChangeListener(PropertyChangeListener pcl)
    {
        if (pcl == null)
            throw new IllegalArgumentException("Adding null PropertyChangeListener");
        synchronized (mGenericWeakListeners)
        {
            WeakReference<PropertyChangeListener> ref = findRef(mGenericWeakListeners, pcl);
            if (ref != null)
                return; // already stored
            mGenericWeakListeners.add(new WeakReference<PropertyChangeListener>(pcl));
        }
        DebugUtils.trace(shortName(pcl)+" listening weakly for changes on "+shortName(mThis));
    }
    public void removePropertyChangeListener(PropertyChangeListener pcl)
    {
        if (pcl == null)
            throw new IllegalArgumentException("Removing null PropertyChangeListener");
        synchronized (mGenericListeners)
        {
            mGenericListeners.remove(pcl);
        }
        synchronized (mSpecificListerners)
        {
            for (Iterator<Set<PropertyChangeListener>> i = mSpecificListerners.values().iterator(); i.hasNext(); )
            {
                Set<PropertyChangeListener> listeners = i.next();
                listeners.remove(pcl);
            }
        }
        synchronized (mGenericUIListeners)
        {
            mGenericUIListeners.remove(pcl);
        }
        synchronized (mSpecificUIListerners)
        {
            for (Iterator<Set<PropertyChangeListener>> i = mSpecificUIListerners.values().iterator(); i.hasNext(); )
            {
                Set<PropertyChangeListener> listeners = i.next();
                listeners.remove(pcl);
            }
        }
        synchronized (mGenericWeakListeners)
        {
            WeakReference<PropertyChangeListener> ref = findRef(mGenericWeakListeners, pcl);
            if (ref != null)
                mGenericListeners.remove(ref);
        }
        synchronized (mSpecificWeakListerners)
        {
            for (Iterator<Set<WeakReference<PropertyChangeListener>>> i = mSpecificWeakListerners.values().iterator(); i.hasNext(); )
            {
                Set<WeakReference<PropertyChangeListener>> listeners = i.next();
                WeakReference<PropertyChangeListener> ref = findRef(listeners, pcl);
                if (ref != null)
                    listeners.remove(ref);
            }
        }
        DebugUtils.trace(shortName(pcl)+" no longer listening for changes on "+shortName(mThis));
    }
    
    private void pushChangeEvent(PropertyChangeEvent ev)
    {
        synchronized (mQueueEvent)
        {
            mQueueEvent.push(ev);
        }
    }
    
    protected PropertyChangeEvent popChangeEvent()
    {
        synchronized (mQueueEvent)
        {
            return (PropertyChangeEvent)mQueueEvent.pop();
        }
    }

    private PropertyChangeEvent newPropertyChangeEvent(Object source, String propertyName, Object oldValue, Object newValue)
    {
        return new PropertyChangeEvent(source, propertyName, oldValue, newValue);
    }
    
    public void queuePropertyChange(String name, Object oldVal, Object newVal)
    {
        if (newVal == null)
        {
            if (oldVal == null)
                pushChangeEvent(null);
            else
                pushChangeEvent(newPropertyChangeEvent(mThis, name, oldVal, newVal));
        }
        else
            if (newVal.equals(oldVal))
                pushChangeEvent(null);
            else
                pushChangeEvent(newPropertyChangeEvent(mThis, name, oldVal, newVal));
    }

    public void queuePropertyChange(String name, int oldVal, int newVal)
    {
        pushChangeEvent(newPropertyChangeEvent(mThis, name, new Integer(oldVal), new Integer(newVal)));
    }

    public void queuePropertyChange(String name, long oldVal, long newVal)
    {
        pushChangeEvent(newPropertyChangeEvent(mThis, name, new Long(oldVal), new Long(newVal)));
    }

    public void queuePropertyChange(String name, double oldVal, double newVal)
    {
        pushChangeEvent(newPropertyChangeEvent(mThis, name, new Double(oldVal), new Double(newVal)));
    }

    public void queuePropertyChange(String name, boolean oldVal, boolean newVal)
    {
        pushChangeEvent(newPropertyChangeEvent(mThis, name, new Boolean(oldVal), new Boolean(newVal)));
    }

    private PropertyChangeListener[] getPropertyChangeListenersBG(String prop)
    {
        Set<PropertyChangeListener> pcls = new HashSet<PropertyChangeListener>();
        synchronized (mGenericListeners)
        {
            pcls.addAll(mGenericListeners);
        }
        synchronized (mSpecificListerners)
        {
            Set<PropertyChangeListener> listeners = mSpecificListerners.get(prop);
            if (listeners != null)
                pcls.addAll(listeners);
        }
        synchronized (mGenericWeakListeners)
        {
            for (Iterator<WeakReference<PropertyChangeListener>> i = mGenericWeakListeners.iterator(); i.hasNext(); )
            {
                WeakReference<PropertyChangeListener> ref = i.next();
                if (ref.get() != null)
                    pcls.add(ref.get());
                else
                    i.remove();
            }
        }
        synchronized (mSpecificWeakListerners)
        {
            Set<WeakReference<PropertyChangeListener>> listeners = mSpecificWeakListerners.get(prop);
            if (listeners != null)
                for (Iterator<WeakReference<PropertyChangeListener>> i = listeners.iterator(); i.hasNext(); )
                {
                    WeakReference<PropertyChangeListener> ref = i.next();
                    if (ref.get() != null)
                        pcls.add(ref.get());
                    else
                        i.remove();
                }
        }
        PropertyChangeListener[] ret = pcls.toArray(new PropertyChangeListener[pcls.size()]);
        return ret;
    }

    private PropertyChangeListener[] getPropertyChangeListenersUI(String prop)
    {
        Set<PropertyChangeListener> pcls = new HashSet<PropertyChangeListener>();
        synchronized (mGenericUIListeners)
        {
            pcls.addAll(mGenericUIListeners);
        }
        synchronized (mSpecificUIListerners)
        {
            Set<PropertyChangeListener> listeners = mSpecificUIListerners.get(prop);
            if (listeners != null)
                pcls.addAll(listeners);
        }
        PropertyChangeListener[] ret = pcls.toArray(new PropertyChangeListener[pcls.size()]);
        return ret;
    }
    
    public void firePropertyChange()
    {
        if (isSuspend())
            return;
        PropertyChangeEvent queueEvent = popChangeEvent();
        if (queueEvent == null)
            return;
        firePropertyChange(queueEvent);
    }
    
    public void firePropertyChange(String name, Object oldVal, Object newVal)
    {
        PropertyChangeEvent e = newPropertyChangeEvent(mThis, name, oldVal, newVal);
        firePropertyChange(e);
    }
    
    public void firePropertyChange(PropertyChangeEvent queueEvent)
    {
        if (!mBroadcast)
            return;
        PropertyChangeListener[] bg = getPropertyChangeListenersBG(queueEvent.getPropertyName());
        PropertyChangeListener[] ui = getPropertyChangeListenersUI(queueEvent.getPropertyName());
        DebugUtils.trace("Firing change on "+shortName(mThis)+" for "+queueEvent.getPropertyName()+" to "+bg.length+" backgound and "+ui.length+" ui listeners");
        //runFirePropertyChange(queueEvent, bg);
        //runFirePropertyChange(queueEvent, ui);
        if (bg.length > 0)
            ThreadLogic.execOnBackgroundThread(this, "runFirePropertyChange", queueEvent, bg);
        if (ui.length > 0)
            ThreadLogic.execOnUIThread(this, "runFirePropertyChange", queueEvent, ui);
    }
    
    public void runFirePropertyChange(PropertyChangeEvent queueEvent, PropertyChangeListener[] pcls)
    {
        for (int i = 0; i < pcls.length; i++)
        {
            String action = shortName(pcls[i])+" hears change on "+shortName(mThis)+" for "+queueEvent.getPropertyName();
            DebugUtils.trace(action);
            if (mBroadcast && (pcls[i] != null))
                try
                {
                    pcls[i].propertyChange(queueEvent);
                }
                catch (Throwable t)
                {
                    DebugUtils.critical("Error while '"+action+"'", t);
                }
        }
    }
    
    public void firePropertyChange(Object newValue)
    {
        PropertyChangeEvent queueEvent = popChangeEvent();
        if (queueEvent == null)
            return;
        queueEvent = newPropertyChangeEvent(queueEvent.getSource(), queueEvent.getPropertyName(), queueEvent.getOldValue(), newValue);
        firePropertyChange(queueEvent);
    }
    
    public void firePropertyChanges()
    {
        if (isSuspend())
            return;
        while (mQueueEvent.size() > 0)
            firePropertyChange();
    }
    
    public void fireMonotonicPropertyChange(String name, Object val)
    {
        pushChangeEvent(newPropertyChangeEvent(mThis, name, null, val));
        firePropertyChange();
    }

    public boolean isBroadcast() {
        return mBroadcast;
    }

    public void setBroadcast(boolean broadcast) {
        mBroadcast = broadcast;
    }
    
    public void suspend()
    {
        mSuspendCount++;
    }
    
    public boolean isSuspend()
    {
        return mSuspendCount > 0;
    }
    
    public void resume()
    {
        mSuspendCount--;
        firePropertyChanges();
    }
    
    public boolean isAnyListeners()
    {
        return mGenericListeners.size() + 
            mSpecificListerners.size() + 
            mGenericUIListeners.size() + 
            mSpecificUIListerners.size() + 
            mGenericWeakListeners.size() + 
            mSpecificWeakListerners.size() > 0; 
    }
}