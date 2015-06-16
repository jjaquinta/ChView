package jo.d2k.admin.rcp.sys.viz.twod.logic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDDisplay;
import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDLinkBean;
import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDObject;
import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDRouteBean;
import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDStarBean;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewRenderLogic;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewVisualizationLogic;
import jo.util.geom2d.Point2D;
import jo.util.geom2d.Point2DLogic;
import jo.util.utils.io.FileUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.jfree.graphics2d.svg.SVGGraphics2D;

public class TwoDAWTLogic
{
    public static AffineTransform calculateTransform(int width, int height, TwoDDisplay disp)
    {
        double scaleX = width/(disp.getUpperBounds().x - disp.getLowerBounds().x);
        double scaleY = height/(disp.getUpperBounds().y - disp.getLowerBounds().y);
        double scale = Math.min(scaleX, scaleY)*.9;
        Point2D center = Point2DLogic.average(disp.getUpperBounds(), disp.getLowerBounds());
        AffineTransform t = new AffineTransform();
        t.translate(width/2, height/2);
        t.scale((float)scale, (float)scale);
        t.translate(-(float)center.x, -(float)center.y);
        return t;
    }
    
    public static void draw(Graphics2D gc, AffineTransform trans, TwoDDisplay disp, boolean printing)
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

    private static Point2D transform(AffineTransform trans, Point2D in)
    {
        double[] pointArray = new double[] { in.x, in.y };
        trans.transform(pointArray, 0, pointArray, 0, 1);
        return new Point2D(pointArray[0], pointArray[1]);
    }

    private static void paintSelectionBand(Graphics2D gc, AffineTransform trans, TwoDDisplay disp)
    {
        Rectangle r = disp.getSelectionBand();
        float[] pointArray = new float[] { (float)r.x, (float)r.y, (float)(r.x + r.width), (float)(r.y + r.height) };
        trans.transform(pointArray, 0, pointArray, 0, 2);
        gc.setColor(toAWTColor(ChViewVisualizationLogic.mPreferences.getSelectColor()));
        gc.drawRect((int)pointArray[0], (int)pointArray[1], (int)(pointArray[2] - pointArray[0]), (int)(pointArray[3] - pointArray[1]));
    }
    
    private static void drawLink(Graphics2D gc, AffineTransform trans, TwoDDisplay disp, boolean printing,
            TwoDLinkBean link)
    {
        Point2D from = transform(trans, link.getStar1().getLocation());
        Point2D to = transform(trans, link.getStar2().getLocation());
        gc.setColor(toAWTColor(ChViewRenderLogic.getLinkColor(link.getLink())));
        gc.drawLine((int)from.x, (int)from.y, (int)to.x, (int)to.y);
        if (!StringUtils.isTrivial(link.getLabel()))
        {
            gc.setFont(toAWTFont(ChViewRenderLogic.getLinkFont(link.getLink())));
            gc.drawString(link.getLabel(), (float)(from.x + to.y)/2, (float)(from.y + to.y)/2);
        }
        if (!printing)
            drawFocus(gc, disp, link, transform(trans, link.getLocation()), 2);
    }
    
    private static void drawRoute(Graphics2D gc, AffineTransform trans, TwoDDisplay disp, boolean printing,
            TwoDRouteBean route)
    {
        Point2D from = transform(trans, route.getStar1().getLocation());
        Point2D to = transform(trans, route.getStar2().getLocation());
        gc.setColor(toAWTColor(ChViewRenderLogic.getRouteColor(route.getRoute())));
        gc.drawLine((int)from.x, (int)from.y, (int)to.x, (int)to.y);
        if (!printing)
            drawFocus(gc, disp, route, transform(trans, route.getLocation()), 2);
    }
    
    private static void drawStar(Graphics2D gc, AffineTransform trans, TwoDDisplay disp, boolean printing,
            TwoDStarBean star)
    {
        Point2D at = transform(trans, star.getLocation());
        Color c = toAWTColor(ChViewRenderLogic.getStarColor(star.getStar()));
        int r = ChViewRenderLogic.getStarRadius(star.getStar());
        gc.setColor(c);
        if (!StringUtils.isTrivial(star.getLabel()))
        {
            gc.setFont(toAWTFont(ChViewRenderLogic.getStarFont(star.getStar())));
            gc.drawString(star.getLabel(), (float)at.x + r, (float)at.y - r*2);
        }
        gc.fillOval((int)at.x - r, (int)at.y - r, r*2, r*2);
        if (!printing)
            drawFocus(gc, disp, star, at, r);
    }

    private static void drawFocus(Graphics2D gc, TwoDDisplay disp, TwoDObject obj,
            Point2D at, int r)
    {
        if (obj == disp.getFocus())
        {
            gc.setColor(toAWTColor(ChViewVisualizationLogic.mPreferences.getFocusColor()));
            gc.drawRect((int)at.x - r - 2, (int)at.y - r - 2, r*2 + 4, r*2 + 4);
        }
        if (disp.getSelected().contains(obj))
        {
            gc.setColor(toAWTColor(ChViewVisualizationLogic.mPreferences.getSelectColor()));
            gc.drawRect((int)at.x - r - 2, (int)at.y - r - 2, r*2 + 4, r*2 + 4);
        }
    }
    
    public static String toSVG(TwoDDisplay disp, int width, int height,
            File file)
    {
        SVGGraphics2D gc = new SVGGraphics2D(width, height);
        gc.setColor(Color.black);
        gc.fillRect(0, 0, width, height);
        AffineTransform trans = TwoDAWTLogic.calculateTransform(width, height, disp);
        TwoDAWTLogic.draw(gc, trans, disp, true);
        String svg = gc.getSVGDocument();
        gc.dispose();
        return svg;
    }
    
    public static void saveSVG(TwoDDisplay disp, int width, int height,
            File file)
    {
        String svg = toSVG(disp, width, height, file);
        try
        {
            FileUtils.writeFile(svg, file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void saveHTML(TwoDDisplay disp, int width, int height,
            File file)
    {
        String svg = toSVG(disp, width, height, file);
        int o = svg.indexOf("<svg");
        svg = svg.substring(o);
        String html = "<!DOCTYPE html><html><body>" + svg + "</body></html>";
        try
        {
            FileUtils.writeFile(html, file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private static Color toAWTColor(org.eclipse.swt.graphics.Color swtC)
    {
        Color awtC = new Color(swtC.getRed(), swtC.getGreen(), swtC.getBlue());
        return awtC;
    }
    
    private static Font toAWTFont(org.eclipse.swt.graphics.Font swtF)
    {
        FontData[] fd = swtF.getFontData();
        Font awtF = new Font(fd[0].getName(), fd[0].getStyle(), fd[0].getHeight());
        return awtF;
    }
}
