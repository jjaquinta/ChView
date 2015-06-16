package jo.util.ui.warp;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import jo.util.utils.DebugUtils;
import jo.util.utils.FormatUtils;

public class WarpLogic
{
	public static final double P = 0.0; // useful range 0 - 1
	public static final double A = .01;
	public static final double B = 2.0; // useful range 0.5 - 2.0
	
	public static void warp(BufferedImage iSource, List<Line2D> segsSource, BufferedImage iTarget, List<Line2D> segsTarget)
	{
	    DebugUtils.trace("[");
		int pc = 0;
		for (int x = 0; x < iTarget.getWidth(); x++)
		{
			for (int y = 0; y < iTarget.getHeight(); y++)
			{
				//mDebug = (y == 228) && (x >= 322) && (x <= 436);
				Point2D s = warpPoint(segsSource, segsTarget, x, y);
				int xx = (int)s.getX();
				int yy = (int)s.getY();
				DebugUtils.trace("=>"+PointLogic.toString(s));
				if ((xx < 0) || (yy < 0) || (xx >= iSource.getWidth()) || (yy >= iSource.getHeight()))
					continue;
				int rgb = iSource.getRGB(xx, yy);
				iTarget.setRGB(x, y, rgb);
			}
			int newpc = x/iTarget.getWidth();
			if ((pc/10) != (newpc/10))
			    DebugUtils.trace(newpc+"%");
			pc = newpc;
		}
		DebugUtils.trace("]");
	}

	public static Point2D warpPoint(List<Line2D> segsSource, List<Line2D> segsTarget, int x, int y)
	{
		Point2D t = new Point2D.Double(x, y);
		Point2D s = new Point2D.Double();
		double totalWeight = 0;
		for (int i = 0; i < segsSource.size(); i++)
		{
			Line2D segSource = segsSource.get(i);
			Line2D segTarget = segsTarget.get(i);
			double length = LineLogic.length(segSource);
			double dist = segTarget.ptSegDist(t);
			double weight = Math.pow(Math.pow(length, P)/(A + dist), B);
			DebugUtils.debug("L"+(i+1)+": ");
			Point2D p = warp(segSource, segTarget, t);
			PointLogic.scale(p, weight);
			DebugUtils.debug("\twieght="+weight+", dist="+dist);
			totalWeight += weight;
			PointLogic.incr(s, p);
		}
		DebugUtils.debug("\t=>"+PointLogic.toString(s));
		PointLogic.scale(s, 1/totalWeight);
		return s;
	}
	
	public static void warp(BufferedImage iSource, Line2D segSource, BufferedImage iTarget, Line2D segTarget)
	{
		for (int y = 0; y < iTarget.getHeight(); y++)
		{
			for (int x = 0; x < iTarget.getWidth(); x++)
			{
				Point2D t = new Point2D.Double(x, y);
				Point2D s = warp(segSource, segTarget, t);
				int xx = (int)s.getX();
				int yy = (int)s.getY();
				if ((xx < 0) || (yy < 0) || (xx >= iSource.getWidth()) || (yy >= iSource.getHeight()))
					continue;
				int rgb = iSource.getRGB(xx, yy);
				iTarget.setRGB(x, y, rgb);
			}
		}
	}
	
	private static Point2D warp(Line2D segSource, Line2D segTarget, Point2D p)
	{
		double[] uv = calcUV(segTarget, p);
		Point2D p2 = uncalcUV(segSource, uv);
		DebugUtils.debug(PointLogic.toString(p)
					+"->"+FormatUtils.formatDouble(uv[0], 2)+","+FormatUtils.formatDouble(uv[1], 2)
					+"->"+PointLogic.toString(p2));
		return p2;
	}
	
	private static double[] calcUV(Line2D line, Point2D point)
	{
		double l = LineLogic.length(line);
		Point2D closest = LineLogic.getPointOnLineClosestToPoint(line, point);
		double u = closest.distance(line.getP1());
		double u2 = closest.distance(line.getP2());
		if ((u2 > u) && (u + u2 > l))
			u = -u;
		double v = point.distance(closest)*line.relativeCCW(point);
		u /= l;
		v /= l;
		double[] ret = new double[2];
		ret[0] = u;
		ret[1] = v;
		return ret;
	}
	
	private static Point2D uncalcUV(Line2D line, double[] uv)
	{
		double l = LineLogic.length(line);
		Point2D lineNorm = PointLogic.normal(PointLogic.sub(line.getP2(), line.getP1()));
		Point2D linePerp = PointLogic.rotation(lineNorm, 90*PointLogic.DEG_TO_RAD);
		Point2D closest = PointLogic.add(line.getP1(), PointLogic.mult(lineNorm, uv[0]*l));
		return PointLogic.add(closest, PointLogic.mult(linePerp, uv[1]*l));
	}
	
	public static void main(String[] argv)
	{
		// unit test
		try
		{
			BufferedImage src = ImageIO.read(new File("c:\\temp\\jo_grant.jpg"));
			BufferedImage trg = ImageIO.read(new File("c:\\temp\\head.jpg"));
			ArrayList<Line2D> segsSource = new ArrayList<Line2D>();
			segsSource.add(new Line2D.Double(1, 9, 18, 11));
			segsSource.add(new Line2D.Double(41, 10, 56, 8));
			segsSource.add(new Line2D.Double(18, 41, 39, 41));
			ArrayList<Line2D> segsTarget = new ArrayList<Line2D>();
			segsTarget.add(new Line2D.Double(322, 230, 436, 226));
			segsTarget.add(new Line2D.Double(592, 226, 704, 230));
			segsTarget.add(new Line2D.Double(438, 330, 586, 330));
			//warp(src, segsSource, trg, segsTarget);
			warp(src, segsSource, trg, segsTarget);
			ImageIO.write(trg, "JPG", new File("c:\\temp\\warped.jpg"));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
