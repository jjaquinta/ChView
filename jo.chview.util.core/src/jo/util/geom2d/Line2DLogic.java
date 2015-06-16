package jo.util.geom2d;

public class Line2DLogic
{

    public static Point2D intersectSegment(Line2D line1, Line2D line2)
    {
        return intersectSegment(line1.p1, line1.p2, line2.p1, line2.p2);
    }

    public static Point2D intersectSegment(Point2D p0, Point2D p1, Point2D p2, Point2D p3)
    {
        return intersectSegment(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
    }

    
    // http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
     // Returns 1 if the lines intersect, otherwise 0. In addition, if the lines 
     // intersect the intersection point may be stored in the floats i_x and i_y.
     public static Point2D intersectSegment(double p0_x, double p0_y, double p1_x, double p1_y, 
         double p2_x, double p2_y, double p3_x, double p3_y)
     {
         double s1_x, s1_y, s2_x, s2_y;
         s1_x = p1_x - p0_x;     s1_y = p1_y - p0_y;
         s2_x = p3_x - p2_x;     s2_y = p3_y - p2_y;
    
         double s, t;
         s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
         t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);
    
         if (s >= 0 && s <= 1 && t >= 0 && t <= 1) // Collision detected
             return new Point2D(p0_x + (t * s1_x), p0_y + (t * s1_y));
         return null; // No collision
     }
}
