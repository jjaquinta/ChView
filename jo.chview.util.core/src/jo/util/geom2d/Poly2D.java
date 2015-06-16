package jo.util.geom2d;

public class Poly2D
{
    public Point2D[] points;

    public Poly2D()
    {
        points = new Point2D[0];
    }

    public Poly2D(Point2D... _points)
    {
        points = new Point2D[_points.length];
        for (int i = 0; i < _points.length; i++)
            points[i] = new Point2D(_points[i]);
    }

    public Poly2D(Poly2D l)
    {
        this(l.points);
    }
    
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        for (Point2D p : points)
        {
            if (sb.length() > 0)
                sb.append("..");
            sb.append(p.toString());
        }
        return sb.toString();
    }

    public Point2D[] getPoints()
    {
        return points;
    }

    public void setPoints(Point2D[] points)
    {
        this.points = points;
    }

    public boolean contains(Point2D p)
    {
        return contains(p.x, p.y);
    }
    
    public boolean contains(double x, double y)
    {
        int j = points.length - 1;
        boolean oddNodes = false;

        for (int i = 0; i < points.length; j = i++)
        {
            if ((((points[i].y <= y) && (y < points[j].y)) || ((points[j].y <= y) && (y < points[i].y)))
                    && (x < (points[j].x - points[i].x) * (y - points[i].y)
                            / (points[j].y - points[i].y) + points[i].x))
                oddNodes = !oddNodes;
        }
        return oddNodes;
    }
}
