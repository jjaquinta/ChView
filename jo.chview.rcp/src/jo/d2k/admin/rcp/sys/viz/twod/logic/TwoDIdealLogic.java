package jo.d2k.admin.rcp.sys.viz.twod.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDConnectionBean;
import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDDisplay;
import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDObject;
import jo.d2k.admin.rcp.sys.viz.twod.data.TwoDStarBean;
import jo.d2k.data.logic.StarExtraLogic;
import jo.util.geom2d.Line2D;
import jo.util.geom2d.Line2DLogic;
import jo.util.geom2d.Point2D;
import jo.util.utils.ArrayUtils;
import jo.util.utils.DebugUtils;
import jo.util.utils.MathUtils;

public class TwoDIdealLogic
{
    public static void findIdeal(TwoDDisplay disp, int iterations)
    {
        Random rnd = new Random();
        ScoredArrangement base = new ScoredArrangement();
        for (TwoDStarBean star : disp.getStars())
            base.locations.put(star, star.getLocation());        
        score(disp, base);     
        while (iterations-- > 0)
        {
            DebugUtils.trace("Score: "+base.score);
            ScoredArrangement alt = new ScoredArrangement();
            for (TwoDStarBean s : base.locations.keySet())
                alt.locations.put(s, base.locations.get(s));
            TwoDStarBean star;
            if (base.culprits.size() == 0)
                star = disp.getStars().get(rnd.nextInt(disp.getStars().size()));
            else
                star = (TwoDStarBean)ArrayUtils.get(base.culprits, rnd.nextInt(base.culprits.size()));
            Point2D loc = new Point2D(base.locations.get(star));
            loc.x += (rnd.nextDouble()-.5)*6;
            loc.y += (rnd.nextDouble()-.5)*6;
            alt.locations.put(star, loc);
            score(disp, alt);
            if (alt.score > base.score)
                base = alt;
        }
        TwoDDataLogic.move(disp, base.locations);
    }
    
    public static void score(TwoDDisplay disp, ScoredArrangement set)
    {
        adjustForCrossingLines(disp, set);
        adjustForSeparation(disp, set);
    }

    private static void adjustForSeparation(TwoDDisplay disp, ScoredArrangement set)
    {
        Map<TwoDStarBean, Point2D> locations = set.locations;
        double min = 1;
        double max = 1;
        double total = 0;
        List<Double> proportions = new ArrayList<>();
        for (TwoDObject c : disp.getObjects())
            if (c instanceof TwoDConnectionBean)
            {
                TwoDConnectionBean conn = (TwoDConnectionBean)c;
                double realDistance = StarExtraLogic.distance(conn.getStar1().getStar(), conn.getStar2().getStar());
                double scaleDistance = locations.get(conn.getStar1()).dist(locations.get(conn.getStar2()));
                double prop = scaleDistance/realDistance;
                total += prop;
                proportions.add(prop);
                if (proportions.size() == 1)
                {
                    min = prop;
                    max = prop;
                }
                else
                {
                    min = Math.min(min, prop);
                    max = Math.max(max, prop);
                }
            }
        double average = total/proportions.size();
        for (Double prop : proportions)
        {
            double norm = prop/average;
            if (norm > 1)
            {
                if (norm > 10)
                    norm = 10;
                set.score -= MathUtils.interpolate(norm, 1, 10, 0, 10);
            }
            else
                set.score -= MathUtils.interpolate(norm, 1, 0, 0, 10);
        }
    }

    private static void adjustForCrossingLines(TwoDDisplay disp, ScoredArrangement set)
    {
        Map<TwoDStarBean, Point2D> locations = set.locations;
        List<Line2D> segments = new ArrayList<Line2D>();
        Map<Line2D,TwoDConnectionBean> register = new HashMap<>();
        for (TwoDObject o : disp.getObjects())
            if (o instanceof TwoDConnectionBean)
            {
                TwoDConnectionBean c = (TwoDConnectionBean)o;
                Line2D line = new Line2D(locations.get(c.getStar1()), locations.get(c.getStar2()));
                segments.add(line);
                register.put(line, c);
            }
        // links
        for (int i = 0; i < segments.size() - 1; i++)
        {
            Line2D line1 = segments.get(i);
            for (int j = i + 1; j < segments.size(); j++)
            {
                Line2D line2 = segments.get(j);                
                Point2D cross = Line2DLogic.intersectSegment(line1, line2);
                if (cross == null)
                    continue;
                double interp1 = line1.p1.dist(cross)/line1.p1.dist(line1.p2);
                if (interp1 > .5)
                    interp1 = 1.0 - interp1;
                if (interp1 < 0.01)
                    continue;
                double interp2 = line2.p1.dist(cross)/line2.p1.dist(line2.p2);
                if (interp2 > .5)
                    interp2 = 1.0 - interp2;
                if (interp2 < 0.01)
                    continue;
                set.score -= 50 + MathUtils.interpolate(interp1, 0, .5, 0, 25) + MathUtils.interpolate(interp2, 0, .5, 0, 25);
                set.culprits.add(register.get(line1).getStar1());
                set.culprits.add(register.get(line1).getStar2());
                set.culprits.add(register.get(line2).getStar1());
                set.culprits.add(register.get(line2).getStar2());
            }
        }
    }
}

class ScoredArrangement
{
    Map<TwoDStarBean, Point2D> locations = new HashMap<>();
    Set<TwoDStarBean> culprits = new HashSet<>();
    double score;
}