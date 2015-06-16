package jo.util.geom2d;

public class Line2D
{
	public Point2D	p1;
	public Point2D	p2;
	
	public Line2D()
	{
		p1 = new Point2D();
		p2 = new Point2D();
	}
	
	public Line2D(Point2D _p1, Point2D _p2)
	{
		p1 = new Point2D(_p1);
		p2 = new Point2D(_p2);
	}
	
	public Line2D(Line2D l)
	{
		p1 = new Point2D(l.p1);
		p2 = new Point2D(l.p2);
	}
	
	public Line2D(double x1, double y1, double x2, double y2)
	{
	    p1 = new Point2D(x1, y1);
	    p2 = new Point2D(x2, y2);
	}
}
