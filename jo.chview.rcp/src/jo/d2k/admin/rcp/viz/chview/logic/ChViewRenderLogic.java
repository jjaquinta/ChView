package jo.d2k.admin.rcp.viz.chview.logic;

import java.util.ArrayList;
import java.util.List;

import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDDisplay;
import jo.d2k.admin.rcp.sys.viz.twod.logic.TwoDDataLogic;
import jo.d2k.admin.rcp.viz.chview.prefs.ChViewPreferencesBean;
import jo.d2k.data.data.SkyBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarLink;
import jo.d2k.data.data.StarRouteBean;
import jo.d2k.data.logic.ChViewFormatLogic;
import jo.d2k.data.logic.FilterLogic;
import jo.d2k.data.logic.StarExtraLogic;
import jo.d2k.data.logic.StarLogic;
import jo.util.geom3d.Point3D;
import jo.util.geom3d.Point3i;
import jo.util.ui.utils.ColorUtils;
import jo.util.ui.utils.FontUtils;
import jo.util.ui.utils.GCUtils;
import jo.util.ui.utils.ImageUtils;
import jo.util.utils.FormatUtils;
import jo.util.utils.MathUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class ChViewRenderLogic
{
    private static ChViewPreferencesBean mParams = ChViewVisualizationLogic.mPreferences;
    
    public static void visualize(GC gc, Rectangle bounds)
    {
        gc.setAdvanced(true);
        gc.setAntialias(SWT.ON);
        gc.setTextAntialias(SWT.ON);
        if (mParams.isShowSky())
            for (SkyBean sky : mParams.getSky().toArray(new SkyBean[0]))
                paintSky(gc, sky);
        if (mParams.isShowGrid())
            paintGrid(gc);
        if (mParams.isShowLinks())
            for (StarLink link : mParams.getLinks().toArray(new StarLink[0]))
                paintLink(gc, link);
        if (mParams.isShowRoutes() && (mParams.getRoutes() != null))
            for (StarRouteBean route : mParams.getRoutes().toArray(new StarRouteBean[0]))
                paintRoute(gc, route);
        for (StarBean star : mParams.getStars().toArray(new StarBean[0]))
            paintStar(gc, star);
        if (mParams.getSelectionBand() != null)
            paintSelectionBand(gc);
        if (mParams.isShowScope())
            paintScope(gc, bounds);
    }
    
    public static TwoDDisplay takeTwoDSnapshot()
    {
        List<StarBean> stars = new ArrayList<>();
        for (StarBean star : mParams.getStars())
            if (!isHideStar(star))
                stars.add(star);
        List<StarLink> links = new ArrayList<>();
        if (mParams.isShowLinks())
            for (StarLink link : mParams.getLinks())
                if (!isHideLink(link))
                    links.add(link);
        List<StarRouteBean> routes = new ArrayList<>();
        if (mParams.isShowRoutes())
            for (StarRouteBean route : mParams.getRoutes())
                if (!isHideRoute(route))
                    routes.add(route);
        TwoDDisplay disp = TwoDDataLogic.makeDisplay(ChViewVisualizationLogic.mPreferences, stars, links, routes);
        return disp;
    }

    private static void paintSelectionBand(GC gc)
    {
        gc.setForeground(ChViewVisualizationLogic.mPreferences.getSelectColor());
        gc.drawRectangle(mParams.getSelectionBand());
    }
    
    private static void paintGrid(GC gc)
    {
        gc.setForeground(ChViewVisualizationLogic.mPreferences.getGridColor());
        gc.setLineAttributes(ChViewVisualizationLogic.mPreferences.getGridStyle());
        int numLines = (int)(mParams.getRadius()/ChViewVisualizationLogic.mPreferences.getGridGap());
        for (int line = -numLines; line <= numLines; line++)
        {
            Point3D left3 = new Point3D(mParams.getCenter().x - mParams.getRadius(), mParams.getCenter().y + line*ChViewVisualizationLogic.mPreferences.getGridGap(), mParams.getCenter().z);
            Point3D right3 = new Point3D(mParams.getCenter().x + mParams.getRadius(), mParams.getCenter().y + line*ChViewVisualizationLogic.mPreferences.getGridGap(), mParams.getCenter().z);
            Point3D top3 = new Point3D(mParams.getCenter().x + line*ChViewVisualizationLogic.mPreferences.getGridGap(), mParams.getCenter().y - mParams.getRadius(), mParams.getCenter().z);
            Point3D bottom3 = new Point3D(mParams.getCenter().x + line*ChViewVisualizationLogic.mPreferences.getGridGap(), mParams.getCenter().y + mParams.getRadius(), mParams.getCenter().z);
            Point3i left2 = ChViewVisualizationLogic.getLocation(left3);
            Point3i right2 = ChViewVisualizationLogic.getLocation(right3);
            Point3i top2 = ChViewVisualizationLogic.getLocation(top3);
            Point3i bottom2 = ChViewVisualizationLogic.getLocation(bottom3);
            int alpha = (int)MathUtils.interpolate(Math.abs(line), 0, numLines, 255, 0);
            gc.setAlpha(alpha);
            gc.drawLine(left2.x, left2.y, right2.x, right2.y);
            gc.drawLine(top2.x, top2.y, bottom2.x, bottom2.y);
        }
        gc.setLineAttributes(ChViewVisualizationLogic.mPreferences.getGridStemStyle());
        for (StarBean star : mParams.getStars())
        {
            if (star.getParentRef() != null)
                continue;
            if (isHidden(star))
                continue;
            Point3D plane3 = new Point3D(star.getX(), star.getY(), mParams.getCenter().z);
            Point3i plane2 = ChViewVisualizationLogic.getLocation(plane3);
            Point3i star2 = ChViewVisualizationLogic.getLocation(star);
            int alpha = getStarAlpha(star);
            gc.setAlpha(alpha);
            gc.drawLine(plane2.x, plane2.y, star2.x, star2.y);
        }
        gc.setAlpha(255);
    }
    
    private static void paintScope(GC gc, Rectangle bounds)
    {
        gc.setForeground(ChViewVisualizationLogic.mPreferences.getScopeColor());
        gc.setLineAttributes(ChViewVisualizationLogic.mPreferences.getScopeStyle());
        int ox = (bounds.x + bounds.width/2);
        int oy = (bounds.y + bounds.height/2);
        // horiz ticks
        for (int x = 0; x < bounds.width/2; x += ChViewVisualizationLogic.mPreferences.getScopeGap())
        {
            int height;
            if (x == 0)
                height = ChViewVisualizationLogic.mPreferences.getScopeHeight3();
            else if ((x/ChViewVisualizationLogic.mPreferences.getScopeGap())%5 == 0)
                height = ChViewVisualizationLogic.mPreferences.getScopeHeight2();
            else
                height = ChViewVisualizationLogic.mPreferences.getScopeHeight1();
            gc.drawLine(ox + x, bounds.y, ox + x, bounds.y + height);
            gc.drawLine(ox + x, bounds.y + bounds.height, ox + x, bounds.y + bounds.height - height);
            gc.drawLine(ox - x, bounds.y, ox - x, bounds.y + height);
            gc.drawLine(ox - x, bounds.y + bounds.height, ox - x, bounds.y + bounds.height - height);
        }
        // vert ticks
        for (int y = 0; y < bounds.height/2; y += ChViewVisualizationLogic.mPreferences.getScopeGap())
        {
            int height;
            if (y == 0)
                height = ChViewVisualizationLogic.mPreferences.getScopeHeight3();
            else if ((y/ChViewVisualizationLogic.mPreferences.getScopeGap())%5 == 0)
                height = ChViewVisualizationLogic.mPreferences.getScopeHeight2();
            else
                height = ChViewVisualizationLogic.mPreferences.getScopeHeight1();
            gc.drawLine(bounds.x, oy + y, bounds.x + height, oy + y);
            gc.drawLine(bounds.x + bounds.width, oy + y, bounds.x + bounds.width - height, oy + y);
            gc.drawLine(bounds.x, oy - y, bounds.x + height, oy - y);
            gc.drawLine(bounds.x + bounds.width, oy - y, bounds.x + bounds.width - height, oy - y);
        }
        gc.drawLine(ox - ChViewVisualizationLogic.mPreferences.getScopeHeight3(), oy, ox + ChViewVisualizationLogic.mPreferences.getScopeHeight3(), oy);
        gc.drawLine(ox, oy - ChViewVisualizationLogic.mPreferences.getScopeHeight3(), ox, oy + ChViewVisualizationLogic.mPreferences.getScopeHeight3());
    }

    private static void paintLink(GC gc, StarLink link)
    {
        if (isHideLink(link))
            return;
        Point3i l1 = ChViewVisualizationLogic.getLocation(link.getStar1());
        Point3i l2 = ChViewVisualizationLogic.getLocation(link.getStar2());
        gc.setForeground(getLinkColor(link));
        gc.setLineAttributes(getLinkAttributes(link));
        gc.drawLine(l1.x, l1.y, l2.x, l2.y);
        if (mParams.isShowLinkNumbers())
        {
            gc.setFont(getLinkFont(link));
            String ln = FormatUtils.formatDouble(link.getDistance(), 1);
            gc.drawString(ln, (l1.x + l2.x)/2, (l1.y + l2.y)/2, true);
        }
    }
    
    public static Font getLinkFont(StarLink link)
    {
        return FontUtils.getFont(ChViewVisualizationLogic.mPreferences.getLinkFont());
    }
    
    public static Color getLinkColor(StarLink link)
    {
        if (link.getDistance() < ChViewVisualizationLogic.mPreferences.getLinkDist2())
            return ChViewVisualizationLogic.mPreferences.getLinkColor1();
        else if (link.getDistance() < ChViewVisualizationLogic.mPreferences.getLinkDist3())
            return ChViewVisualizationLogic.mPreferences.getLinkColor2();
        else
            return ChViewVisualizationLogic.mPreferences.getLinkColor3();
    }
    
    public static LineAttributes getLinkAttributes(StarLink link)
    {
        if (link.getDistance() < ChViewVisualizationLogic.mPreferences.getLinkDist2())
            return ChViewVisualizationLogic.mPreferences.getLinkStyle1();
        else if (link.getDistance() < ChViewVisualizationLogic.mPreferences.getLinkDist3())
            return ChViewVisualizationLogic.mPreferences.getLinkStyle2();
        else
            return ChViewVisualizationLogic.mPreferences.getLinkStyle3();
    }

    private static boolean isHideLink(StarLink link)
    {
        return isHidden(link.getStar1()) || isHidden(link.getStar2());
    }

    private static void paintRoute(GC gc, StarRouteBean route)
    {
        if (isHideRoute(route))
            return;
        Point3i l1 = ChViewVisualizationLogic.getLocation(route.getStar1Ref());
        Point3i l2 = ChViewVisualizationLogic.getLocation(route.getStar2Ref());
        gc.setForeground(getRouteColor(route));
        gc.setLineAttributes(getRouteAttributes(route.getType()));
        gc.drawLine(l1.x, l1.y, l2.x, l2.y);
        if (mParams.isShowLinkNumbers())
        {
            gc.setFont(getLinkFont(null));
            Point3D p1 = new Point3D(route.getStar1Ref().getX(), route.getStar1Ref().getY(), route.getStar1Ref().getZ());
            Point3D p2 = new Point3D(route.getStar2Ref().getX(), route.getStar2Ref().getY(), route.getStar2Ref().getZ());
            String ln = FormatUtils.formatDouble(p1.dist(p2), 1);
            gc.drawString(ln, (l1.x + l2.x)/2, (l1.y + l2.y)/2, true);
        }
    }

    private static boolean isHideRoute(StarRouteBean route)
    {
        return isHidden(route.getStar1Ref()) || isHidden(route.getStar2Ref());
    }
    
    public static Color getRouteColor(StarRouteBean route)
    {
        return getRouteColor(route.getType());
    }

    public static Color getRouteColor(int type)
    {
        switch (type)
        {
            case 0: return ChViewVisualizationLogic.mPreferences.getRoute1Color();
            case 1: return ChViewVisualizationLogic.mPreferences.getRoute2Color();
            case 2: return ChViewVisualizationLogic.mPreferences.getRoute3Color();
            case 3: return ChViewVisualizationLogic.mPreferences.getRoute4Color();
            case 4: return ChViewVisualizationLogic.mPreferences.getRoute5Color();
            case 5: return ChViewVisualizationLogic.mPreferences.getRoute6Color();
            case 6: return ChViewVisualizationLogic.mPreferences.getRoute7Color();
            case 7: return ChViewVisualizationLogic.mPreferences.getRoute8Color();
        }
        throw new IllegalStateException(); // shouldn't happen
    }

    public static LineAttributes getRouteAttributes(int type)
    {
        switch (type)
        {
            case 0: return ChViewVisualizationLogic.mPreferences.getRoute1Style();
            case 1: return ChViewVisualizationLogic.mPreferences.getRoute2Style();
            case 2: return ChViewVisualizationLogic.mPreferences.getRoute3Style();
            case 3: return ChViewVisualizationLogic.mPreferences.getRoute4Style();
            case 4: return ChViewVisualizationLogic.mPreferences.getRoute5Style();
            case 5: return ChViewVisualizationLogic.mPreferences.getRoute6Style();
            case 6: return ChViewVisualizationLogic.mPreferences.getRoute7Style();
            case 7: return ChViewVisualizationLogic.mPreferences.getRoute8Style();
        }
        throw new IllegalStateException(); // shouldn't happen
    }

    private static void paintStar(GC gc, StarBean star)
    {
        if (isHideStar(star))
            return;
        Point3i l = ChViewVisualizationLogic.getLocation(star);
        Color c = getStarColor(star);
        int r = getStarRadius(star);
        if (mParams.getFocus() == star)
        {
            gc.setForeground(ChViewVisualizationLogic.mPreferences.getFocusColor());
            paintFocus(gc, l, r, ChViewVisualizationLogic.mPreferences.getFocusShape());
        }
        else if (mParams.getSelected().contains(star))
        {
            gc.setForeground(ChViewVisualizationLogic.mPreferences.getSelectColor());
            paintFocus(gc, l, r, ChViewVisualizationLogic.mPreferences.getSelectShape());
        }
        gc.setForeground(c);
        gc.setBackground(c);
        int alpha = getStarAlpha(star);
        gc.setAlpha(alpha);
        if (mParams.isShowNames())
        {
            gc.setFont(getStarFont(star));
            gc.setForeground(getStarFontColor(star));
            gc.drawText(getStarName(star), l.x + r, l.y - r*2, true);
        }
        gc.fillOval(l.x - r, l.y - r, r*2, r*2);
        gc.setAlpha(255);
    }

    private static void paintFocus(GC gc, Point3i l, int r, int shape)
    {
        gc.setLineAttributes(new LineAttributes(1f));
        switch (shape)
        {
            case ChViewPreferencesBean.FOCUS_SQUARE:
                gc.drawRectangle(l.x - r - 2, l.y - r - 2, r*2 + 4, r*2 + 4);
                break;
            case ChViewPreferencesBean.FOCUS_CROSS:
                gc.drawLine(l.x, l.y - r - 4, l.x, l.y - r);
                gc.drawLine(l.x, l.y + r + 4, l.x, l.y + r);
                gc.drawLine(l.x - r - 4, l.y, l.x - r, l.y);
                gc.drawLine(l.x + r + 4, l.y, l.x + r, l.y);
                break;
            case ChViewPreferencesBean.FOCUS_X:
                gc.drawLine(l.x - r - 3, l.y - r - 3, l.x - r, l.y - r);
                gc.drawLine(l.x + r + 3, l.y - r - 3, l.x + r, l.y - r);
                gc.drawLine(l.x - r - 3, l.y + r + 3, l.x - r, l.y + r);
                gc.drawLine(l.x + r + 3, l.y + r + 3, l.x + r, l.y + r);
                break;
            case ChViewPreferencesBean.FOCUS_ARROW:
                gc.drawLine(l.x - r - 9, l.y, l.x - r, l.y);
                gc.drawLine(l.x - r - 3, l.y - 3, l.x - r, l.y);
                gc.drawLine(l.x - r - 3, l.y + 3, l.x - r, l.y);
                break;
            case ChViewPreferencesBean.FOCUS_STAR:
            {
                int R = r*2 + 4;
                int[] polygon = new int[20];
                for (int i = 0; i < 20; i += 2)
                {
                    int rad = (i%4 == 0) ? r+2 : R;
                    double a = Math.PI*2/20*i;
                    int dx = (int)(Math.sin(a)*rad);
                    int dy = (int)(Math.cos(a)*rad);
                    polygon[i+0] = l.x + dx;
                    polygon[i+1] = l.y + dy;
                };
                gc.drawPolygon(polygon);
                break;
            }
            case ChViewPreferencesBean.FOCUS_DIAMOND:
            default:
            {
                int R = r + r/2 + 2;
                int[] polygon = {
                        l.x, l.y - R, l.x + R, l.y, l.x, l.y + R, l.x - R, l.y,
                };
                gc.drawPolygon(polygon);
                break;
            }
        }
    }

    private static void paintSky(GC gc, SkyBean sky)
    {
        if (sky == null)
            return;
        if (sky.getStar() == null)
            return;
        Point3i l = ChViewVisualizationLogic.getLocation(sky.getStar());
        if (l.z <= 0)
            return;
        if (sky.getDistance() <= mParams.getRadius())
            return;
        double b = sky.getBrightness()*4 + 64;
        if (b > 8192)
            b = 8192;
        GCUtils.drawDisk(gc, new Point(l.x, l.y), (int)b);
    }
    
    public static String getStarName(StarBean star)
    {
        return ChViewFormatLogic.getStarName(ChViewVisualizationLogic.mPreferences, star);
    }

    public static int getStarAlpha(StarBean star)
    {
        double d = StarExtraLogic.distance(star, mParams.getCenter().x, mParams.getCenter().y, mParams.getCenter().z);
        int alpha = (int)MathUtils.interpolate(d, 0, mParams.getRadius(), 255, 128);
        return alpha;
    }
    
    public static Font getStarFont(StarBean star)
    {
        return FontUtils.getFont(ChViewVisualizationLogic.mPreferences.getStarFont());
    }

    private static boolean isHideStar(StarBean star)
    {
        return (star.getParent() != 0) || isHidden(star);
    }
    
    public static Color getStarFontColor(StarBean star)
    {
        return ChViewVisualizationLogic.mPreferences.getStarFontColor();
    }
    
    public static Color getStarColor(StarBean star)
    {
        int s = StarLogic.SPECTRA.indexOf(Character.toUpperCase(star.getSpectra().charAt(0)));
        switch (s)
        {
            case 0:
                return ChViewVisualizationLogic.mPreferences.getStarOColor();
            case 1:
                return ChViewVisualizationLogic.mPreferences.getStarBColor();
            case 2:
                return ChViewVisualizationLogic.mPreferences.getStarAColor();
            case 3:
                return ChViewVisualizationLogic.mPreferences.getStarFColor();
            case 4:
                return ChViewVisualizationLogic.mPreferences.getStarGColor();
            case 5:
                return ChViewVisualizationLogic.mPreferences.getStarKColor();
            case 6:
                return ChViewVisualizationLogic.mPreferences.getStarMColor();
            case 7:
                return ChViewVisualizationLogic.mPreferences.getStarLColor();
            case 8:
                return ChViewVisualizationLogic.mPreferences.getStarTColor();
            case 9:
                return ChViewVisualizationLogic.mPreferences.getStarYColor();
        }
        throw new IllegalArgumentException("Unexpected spectra: "+star.getSpectra());
    }

    public static int getStarRadius(StarBean star)
    {
        return ChViewFormatLogic.getStarRadius(ChViewVisualizationLogic.mPreferences, star);
    }

    private static boolean isHidden(StarBean star)
    {
        if (mParams.getHidden().contains(star))
            return true;
        if (FilterLogic.isFiltered(star, mParams.getFilter()))
            return true;
        return false;
    }
    
    public static Image getStarIcon(StarBean star)
    {
        Image img = ImageUtils.getImage("star_icon_"+star.getSpectra());
        if (img != null)
            return img;
        img = new Image(Display.getDefault(), 16, 16);
        GC gc = new GC(img);
        gc.setBackground(ColorUtils.getColor("black"));
        gc.fillRectangle(0, 0, 16, 16);
        Color c = getStarColor(star);
        int r = getStarRadius(star);
        gc.setForeground(c);
        gc.setBackground(c);
        gc.fillOval(8 - r, 8 - r, r*2, r*2);
        gc.dispose();
        ImageUtils.setImage("sar_icon_"+star.getSpectra(), img);
        return img;
    }

    public static String getRouteName(int idx)
    {
        return ChViewFormatLogic.getRouteName(ChViewVisualizationLogic.mPreferences, idx);
    }
}
