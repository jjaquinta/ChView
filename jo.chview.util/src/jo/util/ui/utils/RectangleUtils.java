package jo.util.ui.utils;

import org.eclipse.swt.graphics.Rectangle;

public class RectangleUtils 
{
    public static boolean intersects(Rectangle r1, Rectangle r2)
    {
        return r1.intersects(r2);
    }

    public static boolean intersects(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) 
    {
        return (x1 < x2 + w2) && (y1 < y2 + h2) &&
            (x1 + w1 > x2) && (y1 + h1 > y2);
    }

    public static boolean intersects2(int left1, int top1, int right1, int bottom1, int left2, int top2, int right2, int bottom2)
    {
        return (left1 < right2) && (top1 < bottom2) &&
            (right1 > left2) && (bottom1 > top2);
    }
    
    public static boolean contains(Rectangle r1, Rectangle r2)
    {
    	return r1.contains(r2.x, r2.y) && r1.contains(r2.x + r2.width, r2.y + r2.height);
    }
    
    public static boolean contains2(int left, int top, int right, int bottom, int x, int y)
    {
        return ((x >= left) && (x < right) && (y >= top) && (y < bottom));
    }

    public static void normalize(Rectangle r)
    {
    	if (r.width < 0)
    	{
    		r.x = r.x - r.width;
    		r.width = -r.width;
    	}
    	if (r.height < 0)
    	{
    		r.y = r.y - r.height;
    		r.height = -r.height;
    	}
    }
    
    public static void enlargeToInclude(Rectangle r, int x, int y)
    {
    	if (x > r.x + r.width)
    		r.width = x - r.x;
    	else if (x < r.x)
    	{
    		r.width = r.x + r.width - x;
    		r.x = x;
    	}
    	if (y > r.y + r.height)
    		r.height = y - r.y;
    	else if (y < r.y)
    	{
    		r.height = r.y + r.height - y;
    		r.y = y;
    	}
    }
    
    public static String toString(Rectangle r)
    {
        return "[" + r.x+","+r.y+" - "+r.width+"x"+r.height+"]";
    }
}
