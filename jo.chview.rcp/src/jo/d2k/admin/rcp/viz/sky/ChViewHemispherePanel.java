package jo.d2k.admin.rcp.viz.sky;

import java.util.List;

import jo.d2k.data.data.SkyBean;
import jo.util.ui.utils.ColorUtils;
import jo.util.utils.MathUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class ChViewHemispherePanel extends Canvas implements PaintListener
{
    private boolean       mHemisphere;
    private List<SkyBean> mSky;

    public ChViewHemispherePanel(Composite parent, int style, boolean hemisphere)
    {
        super(parent, style | SWT.DOUBLE_BUFFERED);
        mHemisphere = hemisphere;
        addPaintListener(this);
    }

    @Override
    public void paintControl(PaintEvent paint)
    {
        Point size = getSize();
        GC gc = paint.gc;
        gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
        gc.fillRectangle(0, 0, size.x, size.y);
        if (mSky == null)
            return;
        Point center = new Point(size.x/2, size.y/2);
        int radius = Math.min(size.x, size.y)/2;
        paintDisk(gc, center, radius);
        paintStars(gc, center, radius);
    }
    
    private void paintStars(GC gc, Point center, int radius)
    {
        for (SkyBean sky : mSky)
            paintStar(gc, center, radius, sky);
    }
    
    private void paintStar(GC gc, Point center, int radius, SkyBean sky)
    {
        if (mHemisphere)
        {
            if (sky.getDec() < 0)
                return;
        }
        else
        {
            if (sky.getDec() > 0)
                return;
        }
        Point p = getPoint(sky.getRA(), sky.getDec(), center, radius);
        //int m = (int)MathUtils.interpolate(sky.getApparentMagnitude(), 6, -6, 64, 255);
        //int m = (int)MathUtils.interpolate(Math.exp(sky.getApparentMagnitude()), Math.exp(6), Math.exp(-6), 64, 255);
        int m = (int)MathUtils.interpolate(Math.pow(2.512, -sky.getApparentMagnitude()), Math.pow(2.512, -6), Math.pow(2.512, 2), 64, 255);
        if (m < 0)
            m = 0;
        else if (m > 255)
            m = 255;
        gc.setBackground(ColorUtils.getColor(m, m, m));
        gc.fillRectangle(p.x, p.y, 1, 1);
    }

    private void paintDisk(GC gc, Point center, int radius)
    {
        gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
        gc.fillOval(center.x - radius, center.y - radius, radius*2, radius*2);   
        /*
        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_YELLOW));
        for (int ra = 0; ra < 12; ra++)
        {
            Point p1 = getPoint(ra, 0, center, radius);
            Point p2 = getPoint(ra + 12, 0, center, radius);
            gc.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        */
    }
    
    private Point getPoint(double ra, double dec, Point center, int radius)
    {
        double r = MathUtils.interpolate(Math.abs(dec), 90, 0, 0, radius);
        double a = ra/24*Math.PI*2;
        double x = Math.sin(a)*r;
        double y = Math.cos(a)*r;
        Point p = new Point(center.x + (int)x, center.y + (int)y);
        return p;
    }

    public List<SkyBean> getSky()
    {
        return mSky;
    }

    public void setSky(List<SkyBean> sky)
    {
        mSky = sky;
        redraw();
    }
}
