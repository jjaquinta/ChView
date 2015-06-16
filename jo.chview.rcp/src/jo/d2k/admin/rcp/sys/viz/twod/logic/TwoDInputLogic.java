package jo.d2k.admin.rcp.sys.viz.twod.logic;

import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDDisplay;
import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDObject;
import jo.d2k.admin.rcp.sys.viz.twod.ui.TwoDPanel;
import jo.util.geom2d.Point2D;
import jo.util.geom2d.Point2DLogic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;

public class TwoDInputLogic
{
    public static void doMouseDown(TwoDDisplay disp, int x, int y,
            Transform trans, int stateMask)
    {
        Point2D p = convert(x, y, trans);
        TwoDObject closest = TwoDDataLogic.getObjectAt(disp, p);
        if (closest != null)
        {
            //DebugUtils.trace("stateMask="+Integer.toHexString(stateMask)+", shift="+Integer.toHexString(SWT.SHIFT));
            if ((stateMask&SWT.SHIFT) != 0)
                TwoDDataLogic.toggleSelection(disp, closest);
            else
            {
                TwoDDataLogic.setFocused(disp, closest);
                disp.setMouseDown(p);
            }
        }
        else
        {
            TwoDDataLogic.setFocused(disp, null);
            if ((stateMask&SWT.SHIFT) != 0)
            {
                TwoDDataLogic.setSelectionBand(disp, new Rectangle((int)p.x, (int)p.y, 0, 0));
            }
            else
            {
                disp.setMouseDown(p);
            }
        }
    }

    public static void doMouseMove(TwoDDisplay disp, int x, int y,
            Transform trans, int stateMask)
    {
        Point2D p = convert(x, y, trans);
        if ((stateMask&SWT.SHIFT) != 0)
        {
            Rectangle old = disp.getSelectionBand();
            if (old == null)
                return;
            TwoDDataLogic.setSelectionBand(disp, new Rectangle(old.x, old.y, (int)(p.x - old.x), (int)(p.y - old.y)));
        }
        else
        {
            if (disp.getMouseDown() == null)
                return;
            Point2D d = Point2DLogic.sub(p, disp.getMouseDown());
            disp.setMouseDown(p);
            TwoDDataLogic.move(disp, d);
        }
    }

    public static void doMouseUp(TwoDDisplay disp, int x, int y,
            Transform trans, int stateMask)
    {
        if ((stateMask&SWT.SHIFT) != 0)
        {
            Rectangle old = disp.getSelectionBand();
            if (old != null)
            {
                Point2D p = convert(x, y, trans);
                TwoDDataLogic.selectWithin(disp, new Rectangle(old.x, old.y, (int)(p.x - old.x), (int)(p.y - old.y)));
            }
        }
        else
            doMouseMove(disp, x, y, trans, stateMask);
        disp.setSelectionBand(null);
        disp.setMouseDown(null);
    }

    public static void doDoubleClick(TwoDDisplay disp, int x, int y,
            Transform trans, int stateMask)
    {
        TwoDDataLogic.selectNone(disp);
    }

    public static void doMouseHover(TwoDDisplay disp, int x, int y,
            Transform trans, int stateMask, TwoDPanel twoDPanel)
    {
    }

    public static void doMouseExit(TwoDDisplay disp)
    {
        disp.setMouseDown(null);
    }

    public static void doMouseScrolled(TwoDDisplay disp, int count,
            int stateMask)
    {
    }

    private static Point2D convert(int x, int y, Transform trans)
    {
        float[] elements = new float[6];
        trans.getElements(elements);
        trans = new Transform(trans.getDevice(), elements);
        trans.invert();
        float[] pointArray = new float[] { (float)x, (float)y };
        trans.transform(pointArray);
        return new Point2D(pointArray[0], pointArray[1]);
    }
}
