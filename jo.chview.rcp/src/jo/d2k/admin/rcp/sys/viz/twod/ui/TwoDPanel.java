package jo.d2k.admin.rcp.sys.viz.twod.ui;

import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDDisplay;
import jo.d2k.admin.rcp.sys.viz.twod.logic.TwoDInputLogic;
import jo.d2k.admin.rcp.sys.viz.twod.logic.TwoDRenderLogic;
import jo.util.beans.PropertyChangeInvoker;
import jo.util.ui.utils.ColorUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class TwoDPanel extends Canvas implements PaintListener
{
    private Transform   mTrans;
    private TwoDDisplay mDisp;

    public TwoDPanel(Composite parent, int style)
    {
        super(parent, style|SWT.DOUBLE_BUFFERED);
        addPaintListener(this);
    }

    @Override
    public void paintControl(PaintEvent paint)
    {
        if (mDisp == null)
            return;
        Point size = getSize();
        paint.gc.setBackground(ColorUtils.getColor("black"));
        paint.gc.fillRectangle(0, 0, size.x, size.y);
        mTrans = TwoDRenderLogic.calculateTransform(getDisplay(), size.x, size.y, mDisp);
        TwoDRenderLogic.draw(paint.gc, mTrans, mDisp, false);
    }

    public TwoDDisplay getDisp()
    {
        return mDisp;
    }


    public void setDisp(TwoDDisplay disp)
    {
        mDisp = disp;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e)
            {
                TwoDInputLogic.doMouseUp(mDisp, e.x, e.y, mTrans, e.stateMask);
            }
            @Override
            public void mouseDown(MouseEvent e)
            {
                TwoDInputLogic.doMouseDown(mDisp, e.x, e.y, mTrans, e.stateMask);
            }
            @Override
            public void mouseDoubleClick(MouseEvent e)
            {
                TwoDInputLogic.doDoubleClick(mDisp, e.x, e.y, mTrans, e.stateMask);
            }
        });
        addMouseMoveListener(new MouseMoveListener() {            
            @Override
            public void mouseMove(MouseEvent e)
            {
                if (mTrans != null)
                    TwoDInputLogic.doMouseMove(mDisp, e.x, e.y, mTrans, e.stateMask);
            }
        });
        addMouseTrackListener(new MouseTrackListener() {            
            @Override
            public void mouseHover(MouseEvent e)
            {
                TwoDInputLogic.doMouseHover(mDisp, e.x, e.y, mTrans, e.stateMask, TwoDPanel.this);
            }
            
            @Override
            public void mouseExit(MouseEvent e)
            {
                TwoDInputLogic.doMouseExit(mDisp);
            }
            
            @Override
            public void mouseEnter(MouseEvent e)
            {
            }
        });     
        addMouseWheelListener(new MouseWheelListener() {            
            @Override
            public void mouseScrolled(MouseEvent e)
            {
                TwoDInputLogic.doMouseScrolled(mDisp, e.count, e.stateMask);
            }
        });
        mDisp.addUIPropertyChangeListener("data", new PropertyChangeInvoker(this, "redraw"));
        redraw();
    }

}
