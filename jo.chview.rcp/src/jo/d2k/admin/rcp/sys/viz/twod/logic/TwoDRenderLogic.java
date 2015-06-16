package jo.d2k.admin.rcp.sys.viz.twod.logic;

import java.io.File;

import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDDisplay;
import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDLinkBean;
import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDObject;
import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDRouteBean;
import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDStarBean;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.util.geom2d.Point2D;
import jo.util.geom2d.Point2DLogic;
import jo.util.ui.utils.ColorUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

public class TwoDRenderLogic
{
    public static Transform calculateTransform(Device device, int width, int height, TwoDDisplay disp)
    {
        double scaleX = width/(disp.getUpperBounds().x - disp.getLowerBounds().x);
        double scaleY = height/(disp.getUpperBounds().y - disp.getLowerBounds().y);
        double scale = Math.min(scaleX, scaleY)*.9;
        Point2D center = Point2DLogic.average(disp.getUpperBounds(), disp.getLowerBounds());
        Transform t = new Transform(device);
        t.translate(width/2, height/2);
        t.scale((float)scale, (float)scale);
        t.translate(-(float)center.x, -(float)center.y);
        return t;
    }
    
    public static void draw(GC gc, Transform trans, TwoDDisplay disp, boolean printing)
    {
        for (TwoDLinkBean link : disp.getLinks())
            drawLink(gc, trans, disp, printing, link);
        for (TwoDRouteBean route : disp.getRoutes())
            drawRoute(gc, trans, disp, printing, route);
        for (TwoDStarBean star : disp.getStars())
            drawStar(gc, trans, disp, printing, star);
        if (!printing)
            if (disp.getSelectionBand() != null)
                paintSelectionBand(gc, trans, disp);
    }

    private static Point2D transform(Transform trans, Point2D in)
    {
        float[] pointArray = new float[] { (float)in.x, (float)in.y };
        trans.transform(pointArray);
        return new Point2D(pointArray[0], pointArray[1]);
    }

    private static void paintSelectionBand(GC gc, Transform trans, TwoDDisplay disp)
    {
        Rectangle r = disp.getSelectionBand();
        float[] pointArray = new float[] { (float)r.x, (float)r.y, (float)(r.x + r.width), (float)(r.y + r.height) };
        trans.transform(pointArray);
        gc.setForeground(ChViewVisualizationLogic.mPreferences.getSelectColor());
        gc.drawRectangle((int)pointArray[0], (int)pointArray[1], (int)(pointArray[2] - pointArray[0]), (int)(pointArray[3] - pointArray[1]));
    }
    
    private static void drawLink(GC gc, Transform trans, TwoDDisplay disp, boolean printing,
            TwoDLinkBean link)
    {
        Point2D from = transform(trans, link.getStar1().getLocation());
        Point2D to = transform(trans, link.getStar2().getLocation());
        gc.setForeground(ChViewRenderLogic.getLinkColor(link.getLink()));
        gc.drawLine((int)from.x, (int)from.y, (int)to.x, (int)to.y);
        if (!StringUtils.isTrivial(link.getLabel()))
        {
            gc.setFont(ChViewRenderLogic.getLinkFont(link.getLink()));
            gc.drawText(link.getLabel(), (int)(from.x + to.x)/2, (int)(from.y + to.y)/2, true);
        }
        if (!printing)
            drawFocus(gc, disp, link, transform(trans, link.getLocation()), 2);
    }
    
    private static void drawRoute(GC gc, Transform trans, TwoDDisplay disp, boolean printing,
            TwoDRouteBean route)
    {
        Point2D from = transform(trans, route.getStar1().getLocation());
        Point2D to = transform(trans, route.getStar2().getLocation());
        gc.setForeground(ChViewRenderLogic.getRouteColor(route.getRoute()));
        gc.drawLine((int)from.x, (int)from.y, (int)to.x, (int)to.y);
        if (!StringUtils.isTrivial(route.getLabel()))
        {
            gc.setFont(ChViewRenderLogic.getLinkFont(null));
            gc.drawText(route.getLabel(), (int)(from.x + to.x)/2, (int)(from.y + to.y)/2, true);
        }
        if (!printing)
            drawFocus(gc, disp, route, transform(trans, route.getLocation()), 2);
    }
    
    private static void drawStar(GC gc, Transform trans, TwoDDisplay disp, boolean printing,
            TwoDStarBean star)
    {
        Point2D at = transform(trans, star.getLocation());
        Color c = ChViewRenderLogic.getStarColor(star.getStar());
        int r = ChViewRenderLogic.getStarRadius(star.getStar());
        gc.setForeground(c);
        gc.setBackground(c);
        if (!StringUtils.isTrivial(star.getLabel()))
        {
            gc.setFont(ChViewRenderLogic.getStarFont(star.getStar()));
            gc.drawText(star.getLabel(), (int)at.x + r, (int)at.y - r*2, true);
        }
        gc.fillOval((int)at.x - r, (int)at.y - r, r*2, r*2);
        if (!printing)
            drawFocus(gc, disp, star, at, r);
    }

    private static void drawFocus(GC gc, TwoDDisplay disp, TwoDObject obj,
            Point2D at, int r)
    {
        if (obj == disp.getFocus())
        {
            gc.setForeground(ChViewVisualizationLogic.mPreferences.getFocusColor());
            gc.drawRectangle((int)at.x - r - 2, (int)at.y - r - 2, r*2 + 4, r*2 + 4);
        }
        if (disp.getSelected().contains(obj))
        {
            gc.setForeground(ChViewVisualizationLogic.mPreferences.getSelectColor());
            gc.drawRectangle((int)at.x - r - 2, (int)at.y - r - 2, r*2 + 4, r*2 + 4);
        }
    }

    public static void saveFile(TwoDDisplay disp, int width, int height,
            int format, File file)
    {
        if (format == 0)
            savePNG(disp, width, height, file);
        else if (format == 1)
            TwoDAWTLogic.saveSVG(disp, width, height, file);
        else if (format == 2)
            TwoDAWTLogic.saveHTML(disp, width, height, file);
        else
            throw new IllegalArgumentException("Format="+format+" not supported yet");
    }
    
    private static void savePNG(TwoDDisplay disp, int width, int height,
            File file)
    {
        Image img = new Image(Display.getDefault(), width, height);
        GC gc = new GC(img);
        gc.setBackground(ColorUtils.getColor("black"));
        gc.fillRectangle(0, 0, width, height);
        Transform trans = TwoDRenderLogic.calculateTransform(Display.getDefault(), width, height, disp);
        TwoDRenderLogic.draw(gc, trans, disp, true);
        gc.dispose();
        ImageLoader loader = new ImageLoader();
        loader.data = new ImageData[] { img.getImageData() };
        loader.save(file.toString(), SWT.IMAGE_PNG);
        img.dispose();
    }
}
