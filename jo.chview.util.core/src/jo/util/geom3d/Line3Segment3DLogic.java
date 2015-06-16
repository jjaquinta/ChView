package jo.util.geom3d;

public class Line3Segment3DLogic {
	// make a line from two points
	public static LineSegment3D fromPoints(Point3D p1, Point3D p2)
	{
		return new LineSegment3D(p1, p2);
	}
	
	public static boolean intersect(LineSegment3D l, Point3D p)
	{
	    return Point3DLogic.equals(l.getP1().dist(p) + l.getP2().dist(p), l.length());
	}

	public static Point3D intersect(Line3D line, LineSegment3D segment)
	{
	    Line3D line2 = Line3DLogic.fromSegment(segment);
		Point3D[] closest = Line3DLogic.closestPoints(line, line2);
		if (closest == null)
			return null;
		if (!Point3DLogic.equals(closest[0].dist(closest[1]), 0))
			return null;
		if (!intersect(segment, closest[0]))
		    return null;
		return closest[0];
	}
}
