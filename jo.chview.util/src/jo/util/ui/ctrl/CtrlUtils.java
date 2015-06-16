/*
 * Created on Oct 4, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.ctrl;

import jo.util.utils.DebugUtils;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class CtrlUtils
{
    public static void dump(Control c, String indent)
    {
        StringBuffer info = new StringBuffer(indent);
        info.append(c.getClass().getName());
        Rectangle bounds = c.getBounds();
        info.append(" ("+bounds.x+","+bounds.y+"x"+bounds.width+","+bounds.height+")");
        if (c instanceof Label)
            info.append(" text="+((Label)c).getText());
        DebugUtils.info(info.toString());
        if (c instanceof Composite)
        {
            Control[] children = ((Composite)c).getChildren();
            for (int i = 0; i < children.length; i++)
                dump(children[i], indent+"  ");
        }
    }

    public static Point getActualLocation(Control ctrl)
    {
        Point p = new Point(0, 0);
        for (Control c = ctrl; c != null; c = c.getParent())
        {
            Rectangle l = c.getBounds();
            p.x += l.x;
            p.y += l.y;
        }
        Point shellSize = ctrl.getShell().getSize();
        Rectangle shellWithTrim = ctrl.getShell().computeTrim(0, 0, shellSize.x, shellSize.y);
        p.x += shellWithTrim.x;
        p.y += shellWithTrim.y;
        return p;
    }
}
