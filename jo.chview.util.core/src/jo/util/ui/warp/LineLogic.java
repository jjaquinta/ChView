package jo.util.ui.warp;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class LineLogic
{
	public static Point2D getPointOnLineClosestToPoint(Line2D line, Point2D p)
	{
		Point2D origin = line.getP1();
		Point2D direction = PointLogic.sub(line.getP2(), line.getP1());
		Point2D w = PointLogic.sub(p, origin);
		double vsq = PointLogic.dot(direction, direction);
		double proj = PointLogic.dot(w, direction);
		return PointLogic.add(origin, PointLogic.mult(direction, proj/vsq));
	}
	
	public static double length(Line2D line)
	{
		return line.getP1().distance(line.getP2());
	}
}
