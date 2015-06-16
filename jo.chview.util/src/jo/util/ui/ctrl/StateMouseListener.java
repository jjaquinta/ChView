package jo.util.ui.ctrl;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;

public class StateMouseListener implements MouseListener, MouseMoveListener
{
    private void setState(MouseEvent e)
    {
        e.widget.setData("button", new Integer(e.button));
        e.widget.setData("stateMask", new Integer(e.stateMask));
        e.widget.setData("time", new Integer(e.time));
        e.widget.setData("x", new Integer(e.x));
        e.widget.setData("y", new Integer(e.y));
    }

    public void mouseDoubleClick(MouseEvent e)
    {
        setState(e);
    }

    public void mouseDown(MouseEvent e)
    {
        setState(e);
    }

    public void mouseUp(MouseEvent e)
    {
        setState(e);
    }

    public void mouseMove(MouseEvent e)
    {
        setState(e);
    }
}
