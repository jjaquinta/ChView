package jo.d2k.admin.rcp.viz.chview.logic;

import jo.d2k.admin.rcp.viz.chview.prefs.ChViewPreferencesBean;
import jo.d2k.data.data.StarBean;
import jo.util.geom3d.Point3D;
import jo.util.geom3d.Point3DLogic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

public class ChViewInputLogic
{
    private static ChViewPreferencesBean mParams = ChViewVisualizationLogic.mPreferences;

    public static void doMouseDown(int x, int y, int stateMask)
    {
        StarBean closest = ChViewVisualizationLogic.getStarAt(x, y);
        if (closest != null)
        {
            //DebugUtils.trace("stateMask="+Integer.toHexString(stateMask)+", shift="+Integer.toHexString(SWT.SHIFT));
            if ((stateMask&SWT.SHIFT) != 0)
                ChViewVisualizationLogic.toggleSelection(closest);
            else
                ChViewVisualizationLogic.setFocused(closest);
        }
        else
        {
            if ((stateMask&SWT.SHIFT) != 0)
            {
                ChViewVisualizationLogic.setSelectionBand(new Rectangle(x, y, 0, 0));
            }
            else
            {
                mParams.setMouseDown(new Point(x, y));
            }
        }
    }
    
    public static void doDoubleClick(int x, int y, int stateMask)
    {
        StarBean closest = ChViewVisualizationLogic.getStarAt(x, y);
        if (closest != null)
            ChViewVisualizationLogic.setCenter(new Point3D(closest.getX(), closest.getY(), closest.getZ()));
    }
    
    public static void doMouseMove(int x, int y, int stateMask)
    {
        if ((stateMask&SWT.SHIFT) != 0)
        {
            Rectangle old = mParams.getSelectionBand();
            if (old == null)
                return;
            ChViewVisualizationLogic.setSelectionBand(new Rectangle(old.x, old.y, x - old.x, y - old.y));
        }
        else
        {
            if (mParams.getMouseDown() == null)
                return;
            int dx = x - mParams.getMouseDown().x;
            int dy = y - mParams.getMouseDown().y;
            mParams.getMouseDown().x = x;
            mParams.getMouseDown().y = y;
            ChViewVisualizationLogic.rotate(dx, dy);
        }
    }

    public static void doMouseUp(int x, int y, int stateMask)
    {
        if ((stateMask&SWT.SHIFT) != 0)
        {
            Rectangle old = mParams.getSelectionBand();
            if (old != null)
                ChViewVisualizationLogic.selectWithin(new Rectangle(old.x, old.y, x - old.x, y - old.y));
        }
        else
            doMouseMove(x, y, stateMask);
        mParams.setSelectionBand(null);
        mParams.setMouseDown(null);
    }

    public static void doMouseHover(int x, int y, int stateMask, Composite ctrl)
    {
        if (!mParams.isShowNames())
        {
            StarBean closest = ChViewVisualizationLogic.getStarAt(x, y);
            if (closest != null)
                ctrl.setToolTipText(ChViewRenderLogic.getStarName(closest));
            else
                ctrl.setToolTipText("");
        }
    }
    
    public static void doMouseExit()
    {
        mParams.setMouseDown(null);
    }

    public static void doMouseScrolled(int count, int stateMask)
    {
        if ((stateMask&SWT.SHIFT) != 0)
        {
            Point3D move = new Point3D(0, 0, count/5);
            move = Point3DLogic.rotate(move, mParams.getRotation());
            mParams.getCenter().incr(move);
            ChViewVisualizationLogic.updateData(true);
        }
        else
        {
            if (count > 0)
                while (count-- > 0)
                    mParams.setScale(mParams.getScale()*1.03);
            if (count < 0)
                while (count++ < 0)
                    mParams.setScale(mParams.getScale()/1.03);
            mParams.fireMonotonicPropertyChange("data");
        }
    }

}
