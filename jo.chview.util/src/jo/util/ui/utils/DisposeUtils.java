package jo.util.ui.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;

public class DisposeUtils
{
    private static DisposeListener LISTENER = new DisposeListener(){
        public void widgetDisposed(DisposeEvent de)
        {
            doWidgetDisposed(de);
        }
    };
    private static HashMap<Control, Object> DISPOSE_QUEUE = new HashMap<Control, Object>();
    
    @SuppressWarnings("unchecked")
    public static void queueDispose(Object o, Control c)
    {
        c.addDisposeListener(LISTENER);
        if (DISPOSE_QUEUE.containsKey(c))
        {
            Object queued = DISPOSE_QUEUE.get(c);
            List<Object> list;
            if (queued instanceof ArrayList)
                list = (List<Object>)queued;
            else
            {
                list = new ArrayList<Object>();
                list.add(queued);
                DISPOSE_QUEUE.put(c, list);
            }
            list.add(o);
        }
        else
            DISPOSE_QUEUE.put(c, o);
    }
    
    private static void doWidgetDisposed(DisposeEvent de)
    {
        Control c = (Control)de.getSource();
        Object o = DISPOSE_QUEUE.get(c);
        if (o instanceof ArrayList)
        {
            for (Iterator<?> i = ((ArrayList<?>)o).iterator(); i.hasNext(); )
                doDispose(i.next());
        }
        else
            doDispose(o);
    }
    
    private static void doDispose(Object o)
    {
        if (o instanceof Font)
            ((Font)o).dispose();
        else if (o instanceof Image)
            ((Image)o).dispose();
    }
}
