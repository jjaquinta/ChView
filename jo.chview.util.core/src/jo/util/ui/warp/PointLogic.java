package jo.util.ui.warp;

import java.awt.geom.Point2D;

import jo.util.utils.FormatUtils;

public class PointLogic
{
	public static final double DEG_TO_RAD = Math.PI/180.0;
	
	public static Point2D add(Point2D p1, Point2D p2)
	{
		return new Point2D.Double(p1.getX() + p2.getX(), p1.getY() + p2.getY());
	}
	
	public static Point2D sub(Point2D p1, Point2D p2)
	{
		return new Point2D.Double(p1.getX() - p2.getX(), p1.getY() - p2.getY());
	}
	
	public static void incr(Point2D p1, Point2D p2)
	{
		p1.setLocation(p1.getX() + p2.getX(), p1.getY() + p2.getY());
	}
	
	public static void decr(Point2D p1, Point2D p2)
	{
		p1.setLocation(p1.getX() - p2.getX(), p1.getY() - p2.getY());
	}
	
	public static Point2D mult(Point2D p, double scale)
	{
		return new Point2D.Double(p.getX()*scale, p.getY()*scale);
	}
	
	public static void scale(Point2D p, double scale)
	{
		p.setLocation(p.getX()*scale, p.getY()*scale);
	}
	
	public static double mag(Point2D p)
	{
		return p.distance(0, 0);
	}
	
	public static void normalize(Point2D p)
	{
		double m = mag(p);
		if (m > 1)
			scale(p, 1/m);
	}
	
	public static Point2D normal(Point2D p)
	{
		Point2D n = new Point2D.Double(p.getX(), p.getY());
		normalize(n);
		return n;
	}
	
	public static double dot(Point2D p1, Point2D p2)
	{
		return p1.getX()*p2.getX() + p1.getY()*p2.getY();
	}
	
	public static void rotate(Point2D p, double theta)
	{
		double cosTheta = Math.cos(theta);
		double sinTheta = Math.sin(theta);
		double nx = p.getX()*cosTheta + p.getY()*sinTheta;
		double ny = -p.getX()*sinTheta + p.getY()*cosTheta;
		p.setLocation(nx, ny);
	}

	public static void rotate(Point2D p, int theta)
	{
		rotate(p, theta*DEG_TO_RAD);
	}
	
	public static Point2D rotation(Point2D p, double theta)
	{
		Point2D p2 = new Point2D.Double(p.getX(), p.getY());
		rotate(p2, theta);
		return p2;
	}
	
	public static void rotate(Point2D p, Point2D around, double theta)
	{
		decr(p, around);
		rotate(p, theta);
		incr(p, around);
	}
	
	public static Point2D rotation(Point2D p, Point2D around, double theta)
	{
		Point2D p2 = new Point2D.Double(p.getX(), p.getY());
		rotate(p2, around, theta);
		return p2;
	}

	public static String toString(Point2D p)
	{
		return "("+FormatUtils.formatDouble(p.getX(), 1)+","+FormatUtils.formatDouble(p.getY(), 1)+")";
	}

}
