package jo.d2k.admin.rcp.viz.chview;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewInputLogic;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.util.beans.PropertyChangeInvoker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class ChViewStarsPanel extends Canvas implements PaintListener
{
    private int mOX;
    private int mOY;

    public ChViewStarsPanel(Composite parent, int style)
    {
        super(parent, style|SWT.DOUBLE_BUFFERED);
        addPaintListener(this);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e)
            {
                ChViewInputLogic.doMouseUp(e.x - mOX, e.y - mOY, e.stateMask);
            }
            @Override
            public void mouseDown(MouseEvent e)
            {
                ChViewInputLogic.doMouseDown(e.x - mOX, e.y - mOY, e.stateMask);
            }
            @Override
            public void mouseDoubleClick(MouseEvent e)
            {
                ChViewInputLogic.doDoubleClick(e.x - mOX, e.y - mOY, e.stateMask);
            }
        });
        addMouseMoveListener(new MouseMoveListener() {            
            @Override
            public void mouseMove(MouseEvent e)
            {
                ChViewInputLogic.doMouseMove(e.x - mOX, e.y - mOY, e.stateMask);
            }
        });
        addMouseTrackListener(new MouseTrackListener() {            
            @Override
            public void mouseHover(MouseEvent e)
            {
                ChViewInputLogic.doMouseHover(e.x - mOX, e.y - mOY, e.stateMask, ChViewStarsPanel.this);
            }
            
            @Override
            public void mouseExit(MouseEvent e)
            {
                ChViewInputLogic.doMouseExit();
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
                ChViewInputLogic.doMouseScrolled(e.count, e.stateMask);
            }
        });
        ChViewVisualizationLogic.mPreferences.addUIPropertyChangeListener("data", new PropertyChangeInvoker(this, "updateDisplay"));
        updateData();
    }

    
    public void updateData()
    {
        setBackground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
        ChViewVisualizationLogic.updateData(true);
    }
    
    public void updateDisplay()
    {
        setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
        redraw();
    }

    @Override
    public void paintControl(PaintEvent paint)
    {
        if (ChViewVisualizationLogic.mPreferences.getFilteredStars() == null)
            return;
        Point size = getSize();
        Transform t = new Transform(getDisplay());
        paint.gc.getTransform(t);
        mOX = size.x/2;
        mOY = size.y/2;
        t.translate(mOX, mOY);
        paint.gc.setTransform(t);
        Rectangle bounds = new Rectangle(-mOX, -mOY, size.x, size.y);
        ChViewRenderLogic.visualize(paint.gc, bounds);
    }
}
