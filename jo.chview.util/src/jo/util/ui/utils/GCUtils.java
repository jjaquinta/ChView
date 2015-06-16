/*
 * Created on Oct 7, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.utils;

import java.util.ArrayList;
import java.util.List;

import jo.util.utils.MathUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;

public class GCUtils
{
    public static void tileImage(GC gc, Image image, int x, int y, int width, int height, int tileWidth, int tileHeight)
    {
        Rectangle bounds = image.getBounds();
        for (int xx = 0; xx < width; xx += tileWidth)
            for (int yy = 0; yy < height; yy += tileHeight)
            {
                if ((xx + tileWidth > width) || (yy + tileHeight > height))
                {
                    int iw = bounds.width;
                    int tw = tileWidth;
                    if (xx + tileWidth > width)
                    {
                        double frac = (double)((xx + tileWidth) - width)/(double)tileWidth;
                        iw = (int)(iw*frac);
                        tw = (int)(tw*frac);
                    }
                    int ih = bounds.height;
                    int th = tileHeight;
                    if (yy + tileHeight > height)
                    {
                        double frac = (double)((yy + tileHeight) - height)/(double)tileHeight;
                        ih = (int)(ih*frac);
                        th = (int)(th*frac);
                    }
                    gc.drawImage(image, 0, 0, iw, ih, x+xx, y+yy, tw, th);
                }
                else
                {
                    gc.drawImage(image, 0, 0, bounds.width, bounds.height, x+xx, y+yy, tileWidth, tileHeight);
                }
            }
    }
    
    public static void tileImage(GC gc, Image image, int x, int y, int width, int height)
    {
        Rectangle bounds = image.getBounds();
        tileImage(gc, image, x, y, width, height, bounds.width, bounds.height);
    }
    
    public static void drawCurvedText(GC gc, String text, int x, int y, int r, double startAng, int style)
    {        
        r += gc.getFontMetrics().getHeight();
        r += gc.getFontMetrics().getLeading();
        if ((style&SWT.RIGHT) != 0)
            startAng -= ((double)gc.textExtent(text).x)/r;
        else if ((style&SWT.CENTER) != 0)
            startAng -= ((double)gc.textExtent(text).x)/r/2;
        Transform t = new Transform(gc.getDevice());
        for (int i = 0; i < text.length(); i++)
        {
            t.identity();
            t.translate(x, y);
            t.rotate(90);
            t.rotate((float)(startAng/Math.PI*180));
            t.translate(0, -r);
            gc.setTransform(t);
            String ch = text.substring(i, i+1);
            gc.drawText(ch, 0, 0, true);
            int w = gc.textExtent(ch).x;
            double ang = (double)w/(double)r;
            startAng += ang;
        }
        t.identity();
        gc.setTransform(t);
        t.dispose();
    }
    
    private static Disk makeDisk(int side)
    {
        Disk d = new Disk();
        d.disk = new double[side][side];
        d.dx = -side/2;
        d.dy = -side/2;
        double r = side/2.0;
        d.max = 0;
        d.tot = 0;
        for (int x = 0; x < side; x++)
            for (int y = 0; y < side; y++)
            {
                double cx = (x + .5) - r;
                double cy = (y + .5) - r;
                double rad = Math.sqrt(cx*cx + cy*cy);
                if (rad > r)
                    d.disk[x][y] = 0;
                else
                    d.disk[x][y] = MathUtils.interpolateSin(rad, r, 0, 0, 1);
                d.max = Math.max(d.max, d.disk[x][y]);
                d.tot += d.disk[x][y];
            }
        double maxLumensPerPixel = 256/d.max; 
        d.cutoff = (int)(maxLumensPerPixel*d.tot);
        return d;
    }
    
    private static final List<Disk> DISKS = new ArrayList<Disk>();
    
    private static Disk getDisk(int m)
    {
        for (int i = 0; true; i++)
        {
            if (DISKS.size() <= i)
                DISKS.add(makeDisk(i + 1));
            Disk d = DISKS.get(i);
            if (m < d.cutoff)
                return d;
        }
    }
    
    public static int drawDisk(GC gc, Point p, int m)
    {
        if (m < 0)
            m = 0;
        Disk d = getDisk(m);
        double lumensPerPip = m/d.tot;
        for (int x = 0; x < d.disk.length; x++)
            for (int y = 0; y < d.disk.length; y++)
                if (d.disk[x][y] > 0)
                {
                    int lumens = (int)(d.disk[x][y]*lumensPerPip); 
                    gc.setForeground(ColorUtils.getColor(lumens, lumens, lumens));
                    gc.drawPoint(p.x + d.dx + x, p.y + d.dy + y);
                }
        return d.disk.length;
    }
}

class Disk
{
    double[][] disk;
    double     max;
    double     tot;
    int        cutoff;
    int        dx;
    int        dy;
}