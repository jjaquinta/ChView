package jo.d2k.admin.rcp.viz.sky;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.data.data.SkyBean;
import jo.d2k.data.data.SkyConstellationBean;
import jo.d2k.data.data.SkyLinkBean;
import jo.d2k.data.data.StarBean;
import jo.util.beans.PropChangeSupport;
import jo.util.geom3d.Point3D;
import jo.util.ui.utils.GCUtils;
import jo.util.utils.MathUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class ChViewSkyPanel extends Canvas implements PaintListener
{
    private boolean       mDrawBorder;
    private boolean       mDrawGrid;
    private boolean       mDrawConstellations;
    private List<SkyBean> mSky;
    private Point3D       mCenterPoint;
    private StarBean      mCenterStar;
    private List<SkyConstellationBean> mConstellations;
    private Map<SkyBean, Point> mLocations;
    private Map<SkyBean, Integer> mRadii;
    private double        mZoom;
    private int           mScrollX;
    private int           mScrollY;
    private int           mMouseX;
    private int           mMouseY;
    private SkyBean       mHover;
    
    private PropChangeSupport   mPCS;

    public ChViewSkyPanel(Composite parent, int style)
    {
        super(parent, style | SWT.DOUBLE_BUFFERED);
        mPCS = new PropChangeSupport(this);
        mLocations = new HashMap<SkyBean, Point>();
        mRadii = new HashMap<SkyBean, Integer>();
        mZoom = 1.0;
        mMouseX = -1;
        mMouseY = -1;
        addPaintListener(this);
        addMouseWheelListener(new MouseWheelListener() {            
            @Override
            public void mouseScrolled(MouseEvent e)
            {
                doMouseScroll(e.count);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e)
            {
                doMouseDown(e.x, e.y);
            }
            @Override
            public void mouseUp(MouseEvent e)
            {
                doMouseUp(e.x, e.y);
            }
        });
        addMouseMoveListener(new MouseMoveListener() {            
            @Override
            public void mouseMove(MouseEvent e)
            {
                doMouseMove(e.x, e.y);
            }
        });
    }
    
    public void doMouseScroll(int clicks)
    {
        double zoomBefore = mZoom;
        while (clicks > 0)
        {
            mZoom *= 1.01;
            clicks--;
        }
        while (clicks < 0)
        {
            mZoom /= 1.01;
            clicks++;
        }
        if (mZoom < 1.0)
        {
            mZoom = 1.0;
            mScrollX = 0;
            mScrollY = 0;
        }
        else
        {
            mScrollX = (int)((mScrollX/zoomBefore)*mZoom);
            mScrollY = (int)((mScrollY/zoomBefore)*mZoom);
        }
        redraw();
    }
    
    private void doMouseDown(int x, int y)
    {
        mMouseX = x;
        mMouseY = y;
    }
    
    private void doMouseMove(int x, int y)
    {
        setHover(findStar(x, y));
        if (mMouseX == -1)
            return;
        mScrollX += x - mMouseX;
        mScrollY += y - mMouseY;
        doMouseDown(x, y);
                redraw();;
    }
    
    private void doMouseUp(int x, int y)
    {
        doMouseMove(x, y);
        mMouseX = -1;
        mMouseY = -1;
    }

    private SkyBean findStar(int x, int y)
    {
        if (mSky == null)
            return null;
        for (SkyBean sky : mSky)
        {
            Point p = mLocations.get(sky);
            if (p == null)
                continue;
            int d = Math.abs(x - p.x) + Math.abs(y - p.y);
            if (d <= mRadii.get(sky))
                return sky;
        }
        return null;
    }
    
    @Override
    public void paintControl(PaintEvent paint)
    {
        paint.gc.setAdvanced(true);
        Point size = getSize();
        Rectangle portal = new Rectangle((int)(-size.x*(mZoom - 1)/2) + mScrollX, (int)(-size.y*(mZoom - 1)/2) + mScrollY, 
                (int)(size.x*mZoom), (int)(size.y*mZoom));
        int r;
        Point N;
        Point S;
        double aspect = (double)portal.width/(double)portal.height;
        int mode ;
        if (aspect > 2.0)
        {
            r = portal.height/2;
            N = new Point(portal.x + r, portal.y + r);
            S = new Point(portal.x + r*3, portal.y + r);
            mode = 0;
        }
        else if (aspect < .5)
        {
            r = portal.width/2;
            N = new Point(portal.x + r, portal.y + r);
            S = new Point(portal.x + r, portal.y + r*3);
            mode = 1;
        }
        else
        {
            r = (int)(.5*(-1*Math.sqrt(2)*Math.sqrt(portal.width)*Math.sqrt(portal.height) + portal.width + portal.height));
            N = new Point(portal.x + r, portal.y + r);
            S = new Point(portal.x + portal.width - r, portal.y + portal.height - r);
            mode = 2;
        }
        GC gc = paint.gc;
        gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
        gc.fillRectangle(0, 0, size.x, size.y);
        if (mSky == null)
            return;
        paintLegend(gc, size, mode, r);
        for (double a = Math.PI/4; a < Math.PI*2; a += Math.PI/2)
        {
            if ((mode != 2) || (a != Math.PI/4))
                GCUtils.drawCurvedText(gc, "Galactic Northern Hemisphere", N.x, N.y, r, a, SWT.CENTER);
            if ((mode != 2) || (a != Math.PI*5/4))
                GCUtils.drawCurvedText(gc, "Galactic Southern Hemisphere", S.x, S.y, r, a, SWT.CENTER);
        }
        if (mDrawBorder)
        {
            int r2 = r - r/10;
            paintBorder(gc, N, r, r2);
            paintBorder(gc, S, r, r2);
            r = r2;
        }
        paintStars(gc, N, S, r);
        if (mDrawGrid)
        {
            paintGrid(gc, N, r);
            paintGrid(gc, S, r);
        }
    }
    
    private void paintLegend(GC gc, Point size, int mode, int r)
    {
        String legend;
        if (mCenterStar != null)
            legend = "Night Sky Above "+ChViewRenderLogic.getStarName(mCenterStar);
        else if (mCenterPoint != null)
            legend = "Night Sky Above "+mCenterPoint.toIntString();
        else
            return;
        Point extent = gc.textExtent(legend);
        FontMetrics fm = gc.getFontMetrics();
        if (mode == 0)
        {
            gc.drawString(legend, r*2 - extent.x/2, 0 + fm.getLeading());
            gc.drawString(legend, r*2 - extent.x/2, size.y - fm.getHeight()-fm.getLeading());  
        }
        else if (mode == 1)
        {
            
        }
        else if (mode == 2)
        {
            gc.drawString(legend, size.x - extent.x - fm.getAverageCharWidth()*1, 0 + fm.getLeading());
            gc.drawString(legend, fm.getAverageCharWidth()*1, size.y - fm.getHeight()-fm.getLeading());
        }
    }

    private void paintStars(GC gc, Point N, Point S, int radius)
    {
        mLocations.clear();
        mRadii.clear();
        paintDisk(gc, N, S, radius);
        for (int i = mSky.size() - 1; i >= 0; i--)
        {
            SkyBean sky = mSky.get(i);
            if (sky.getDec() < 0)
                paintStar(gc, N, radius, sky);
            else
                paintStar(gc, S, radius, sky);
        }
        if (mDrawConstellations && (mConstellations != null))
            paintConstellations(gc, N, S, radius);
    }
    
    private void paintStar(GC gc, Point center, int radius, SkyBean sky)
    {
        Point p = getPoint(sky.getRA(), sky.getDec(), center, radius);
        double b = sky.getBrightness() + 64;
        if (b > 8192)
            b = 8192;
        int m = (int)(b*mZoom*mZoom);
        int r = GCUtils.drawDisk(gc, p, m);
        mLocations.put(sky, p);
        mRadii.put(sky, r);
    }

    private void paintConstellations(GC gc, Point N, Point S, int radius)
    {
        gc.setLineWidth(1);
        gc.setAlpha(128);
        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_YELLOW));
        for (SkyConstellationBean constellation : mConstellations)
            for (SkyLinkBean link : constellation.getLinks())
                paintLink(gc, N, S, radius, link);
    }
    
    private void paintLink(GC gc, Point n, Point s, int radius, SkyLinkBean link)
    {
        Point p1 = mLocations.get(link.getStar1());
        Point p2 = mLocations.get(link.getStar2());
        if (!inSameHemisphere(gc, n, s, radius, p1, p2))
            return;
        gc.drawLine(p1.x, p1.y, p2.x, p2.y);
    }
    
    private boolean inSameHemisphere(GC gc, Point n, Point s, int radius, Point p1, Point p2)
    {
        double s1dn = dist(n, p1);
        double s1ds = dist(s, p1);
        double s2dn = dist(n, p2);
        double s2ds = dist(s, p2);
        if ((s1dn <= radius) && (s2dn <= radius))
            return true;
        if ((s1ds <= radius) && (s2ds <= radius))
            return true;
        return false;
    }

    private double dist(Point p1, Point p2)
    {
        return Math.sqrt((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y));
    }

    private void paintDisk(GC gc, Point N, Point S, int radius)
    {
        gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
        gc.fillOval(N.x - radius, N.y - radius, radius*2, radius*2);   
        gc.fillOval(S.x - radius, S.y - radius, radius*2, radius*2);
    }
    
    private void paintBorder(GC gc, Point center, int r1, int r2)
    {
        gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
        gc.fillOval(center.x - r1, center.y - r1, r1*2, r1*2);
        gc.setLineWidth(2);
        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
        gc.drawOval(center.x - r1, center.y - r1, r1*2, r1*2);
        gc.setLineWidth(3);
        gc.drawOval(center.x - r2, center.y - r2, r2*2, r2*2);
        gc.setLineWidth(1);
        Transform tr = new Transform(getDisplay());
        int ascent = gc.getFontMetrics().getAscent();
        int em = gc.getFontMetrics().getAverageCharWidth();
        int degPP = 30;
        if (mZoom >= 2)
            degPP = 10;
        for (int deg = 0; deg < 360; deg += degPP)
        {
            double ra = deg/360.0*24.0;
            Point p1 = getPoint(ra, 0, center, r1);
            Point p4 = getPoint(ra, 0, center, r2);
            gc.drawLine(p1.x, p1.y, p4.x, p4.y);
            int angle = deg + 180;
            tr.identity();
            tr.translate(center.x, center.y);
            tr.rotate(angle);
            gc.setTransform(tr);
            gc.drawText(deg+"\u00b0", em, (r1 + r2)/2 - ascent/2);
            gc.setTransform(null);
        }
        tr.dispose();
    }
    
    private void paintGrid(GC gc, Point center, int radius)
    {
        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_GREEN));
        gc.setAlpha(128);
        int degPP = 30;
        if (mZoom >= 2)
            degPP = 10;
        for (int deg = 0; deg < 360; deg += degPP)
        {
            if (deg%30 == 0)
                gc.setLineWidth(mZoom < 2 ? 1 : 2);
            else
                gc.setLineWidth(1);
            double ra = deg/360.0*24.0;
            Point p1 = getPoint(ra, 0, center, radius);
            Point p2 = getPoint(ra, 80, center, radius);
            gc.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        for (int dec = 10; dec <= 80; dec += 10)
        {
            if ((dec%30 == 0) || (dec == 80))
                gc.setLineWidth(mZoom < 2 ? 1 : 2);
            else
            {
                if (mZoom < 2)
                    continue;
                gc.setLineWidth(1);
            }
            Point p1 = getPoint(0, dec, center, radius);
            int d = center.y - p1.y;
            gc.drawOval(center.x - d, center.y - d, d*2, d*2);
        }
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

    public boolean isDrawBorder()
    {
        return mDrawBorder;
    }

    public void setDrawBorder(boolean drawBorder)
    {
        mDrawBorder = drawBorder;
        redraw();
    }

    public boolean isDrawGrid()
    {
        return mDrawGrid;
    }

    public void setDrawGrid(boolean drawGrid)
    {
        mDrawGrid = drawGrid;
        redraw();
    }

    public boolean isDrawConstellations()
    {
        return mDrawConstellations;
    }

    public void setDrawConstellations(boolean drawConstellations)
    {
        mDrawConstellations = drawConstellations;
        redraw();
    }

    public List<SkyConstellationBean> getConstellations()
    {
        return mConstellations;
    }

    public void setConstellations(List<SkyConstellationBean> constellations)
    {
        mConstellations = constellations;
        redraw();
    }

    public SkyBean getHover()
    {
        return mHover;
    }

    public void setHover(SkyBean hover)
    {
        if (hover == mHover)
            return;
        mPCS.queuePropertyChange("hover", mHover, hover);
        mHover = hover;
        mPCS.firePropertyChange();
    }
    
    // listeners
    public void addPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
        mPCS.addPropertyChangeListener(prop, pcl);
    }
    public void addPropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.addPropertyChangeListener(pcl);
    }
    public void addUIPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
        mPCS.addUIPropertyChangeListener(prop, pcl);
    }
    public void addUIPropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.addUIPropertyChangeListener(pcl);
    }
    public void addWeakPropertyChangeListener(String prop, PropertyChangeListener pcl)
    {
        mPCS.addWeakPropertyChangeListener(prop, pcl);
    }
    public void addWeakPropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.addWeakPropertyChangeListener(pcl);
    }
    public void removePropertyChangeListener(PropertyChangeListener pcl)
    {
        mPCS.removePropertyChangeListener(pcl);
    }

    public Point3D getCenterPoint()
    {
        return mCenterPoint;
    }

    public void setCenterPoint(Point3D centerPoint)
    {
        mCenterPoint = centerPoint;
        redraw();
    }

    public StarBean getCenterStar()
    {
        return mCenterStar;
    }

    public void setCenterStar(StarBean centerStar)
    {
        mCenterStar = centerStar;
        redraw();
    }
}
