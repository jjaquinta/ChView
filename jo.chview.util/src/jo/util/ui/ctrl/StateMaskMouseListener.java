package jo.util.ui.ctrl;

import java.util.EventObject;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Control;

public class StateMaskMouseListener implements MouseListener
{
    public static final String DOUBLE_CLICK = "doubleClick";
    public static final String STATE_MASK = "stateMask";

    public void mouseDoubleClick(MouseEvent e)
    {
        ((Control)e.getSource()).setData(DOUBLE_CLICK, Boolean.TRUE);
    }

    public void mouseDown(MouseEvent e)
    {
        ((Control)e.getSource()).setData(DOUBLE_CLICK, Boolean.FALSE);
        ((Control)e.getSource()).setData(STATE_MASK, new Integer(e.stateMask));
    }

    public void mouseUp(MouseEvent e)
    {
    }

    public static boolean isDoubleClick(Object o)
    {
        if (o instanceof EventObject)
            o = ((EventObject)o).getSource();
        if (o instanceof Control)
        {
            Control c = (Control)o;
            if (c.getData(DOUBLE_CLICK) instanceof Boolean)
                return ((Boolean)(c.getData(DOUBLE_CLICK))).booleanValue();
        }
        return false;
    }
    
    public static int getStateMask(Object o)
    {
        if (o instanceof EventObject)
            o = ((EventObject)o).getSource();
        if (o instanceof Control)
        {
            Control c = (Control)o;
            if (c.getData(STATE_MASK) instanceof Integer)
                return ((Integer)(c.getData(STATE_MASK))).intValue();
        }
        return 0;
    }
}
