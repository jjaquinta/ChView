package jo.util.geomhpl;

import jo.util.geom2d.Point2D;
import jo.util.geom2d.Poly2D;

public class HPImageLogic
{
    private static final Poly2D[] IMAGE_BOUNDS = {
        new Poly2D( new Point2D(0, 0), new Point2D(.25, 0), new Point2D(.25, 1.0/3.0), new Point2D(0, 1.0/3.0) ),
        new Poly2D( new Point2D(.25, 0), new Point2D(.5, 0), new Point2D(.5, 1.0/3.0), new Point2D(.25, 1.0/3.0) ),
        new Poly2D( new Point2D(.5, 0), new Point2D(.75, 0), new Point2D(.75, 1.0/3.0), new Point2D(.5, 1.0/3.0) ),
        new Poly2D( new Point2D(.75, 0), new Point2D(1, 0), new Point2D(1, 1.0/3.0), new Point2D(.75, 1.0/3.0) ),
        new Poly2D( new Point2D(0, 1.0/3.0), new Point2D(.25, 1.0/3.0), new Point2D(.25, 2.0/3.0), new Point2D(0, 2.0/3.0) ),
        new Poly2D( new Point2D(.25, 1.0/3.0), new Point2D(.5, 1.0/3.0), new Point2D(.5, 2.0/3.0), new Point2D(.25, 2.0/3.0) ),
        new Poly2D( new Point2D(.5, 1.0/3.0), new Point2D(.75, 1.0/3.0), new Point2D(.75, 2.0/3.0), new Point2D(.5, 2.0/3.0) ),
        new Poly2D( new Point2D(.75, 1.0/3.0), new Point2D(1, 1.0/3.0), new Point2D(1, 2.0/3.0), new Point2D(.75, 2.0/3.0) ),
        new Poly2D( new Point2D(0, 2.0/3.0), new Point2D(.25, 2.0/3.0), new Point2D(.25, 1), new Point2D(0, 1) ),
        new Poly2D( new Point2D(.25, 2.0/3.0), new Point2D(.5, 2.0/3.0), new Point2D(.5, 1), new Point2D(.25, 1) ),
        new Poly2D( new Point2D(.5, 2.0/3.0), new Point2D(.75, 2.0/3.0), new Point2D(.75, 1), new Point2D(.5, 1) ),
        new Poly2D( new Point2D(.75, 2.0/3.0), new Point2D(1, 2.0/3.0), new Point2D(1, 1), new Point2D(.75, 2) ),
    };
    
    public static Poly2D getImageFaceBounds(long coord)
    {
        return IMAGE_BOUNDS[HPLongLogic.getFace(coord)];
    }
    
    public static Poly2D getImageBounds(long coord)
    {
        Poly2D b = getImageFaceBounds(coord);
        int res = HPLongLogic.getResolution(coord);
        for (int i = 0; i < res; i++)
        {
            int idx = HPLongLogic.getIndex(coord, i);
            b = HPLongLogic.subdivide(b, idx);
        }
        return b;
    }
    
    public static long toHPL(Point2D xy, int res)
    {
        long coord = 0;
        while (xy.x < 0)
            xy.x++;
        while (xy.x > 1)
            xy.x -= 1;
        while (xy.y < 0)
            xy.y++;
        while (xy.y > 1)
            xy.y -= 1;
        LonLat ll = new LonLat(xy);
        LonLatLogic.normalize(ll);
        HPLongLogic.setResolution(coord, res);
        int face = -1;
        for (int f = 0; f < 12; f++)
            if (IMAGE_BOUNDS[f].contains(xy))
            {
                face = f;
                break;
            }
        if (face == -1)
            throw new IllegalArgumentException("Unsupported image location: "+xy);
        HPLongLogic.setFace(coord, face);
        Poly2D b = IMAGE_BOUNDS[face];
        Poly2D[] subs = HPLongLogic.subdivide(b);
        for (int r = 0; r < res; r++)
        {
            int subsquare = -1;
            for (int ss = 0; ss < 4; ss++)
            {
                Poly2D b1 = subs[ss];
                if (b1.contains(ll))
                {
                    b = b1;
                    subsquare = ss;
                    //DebugUtils.trace("Found "+ll.getLongitude()+","+ll.getLatitude()+" in subdivision "+ss+" ("+b1+")");
                    break;
                }
                //else
                //    DebugUtils.trace("Did not find "+ll.getLongitude()+","+ll.getLatitude()+" in subdivision "+ss+" ("+b1+")");
            }
            if (subsquare == -1)
                throw new IllegalArgumentException("Cannot find "+ll.getLongitude()+","+ll.getLatitude()+" in subdivision "+r+" ("+b+") of face "+face);
            HPLongLogic.setIndex(coord, r, subsquare);
        }
        return coord;
    }

}
