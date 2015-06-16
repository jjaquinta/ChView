package jo.util.geomhpl;

import java.util.Random;

import jo.util.geom2d.Point2D;
import jo.util.geom2d.Point2DLogic;
import jo.util.geom2d.Poly2D;

/*
 * Spherical representation based on HEALPix.
 * Coordinates are longs.
 * bits 00 to 04 are the resolution
 * bits 05 to 07 are the face
 * bits 08 to 63 are the subsquare indexes, for as many positions as the resolution
 *  Faces:
     /\  /\  /\  /\
    /0 \/1 \/2 \/3 \
    \  /\  /\  /\  /\
     \/4 \/5 \/6 \/7 \
      \  /\  /\  /\  /\
       \/8 \/9 \/10\/12\
        \  /\  /\  /\  /
         \/  \/  \/  \/     
 *  Subsquares
         /\
        /0 \
       /\  /\
      /3 \/1 \
      \  /\  /
       \/2 \/
        \  /
         \/     
 */

public class HPLongLogic
{
    public static int getResolution(long coord)
    {
        int res = (int)(coord&0x1fL);
        if (res > 28)
            throw new IllegalStateException("Ill formatted hlp: "+Long.toHexString(coord)+", res="+res);
        return res;
    }
    public static int getFace(long coord)
    {
        int face = (int)((coord>>5)&0xfL);
        if (face >= 12)
            throw new IllegalStateException("Ill formatted hlp: "+Long.toHexString(coord)+", res="+face);
        return face;
    }
    public static long getIndicies(long coord)
    {
        long indicies = (long)(coord>>8);
        return indicies;
    }
    public static int getIndex(long coord, int index)
    {
        int idx = (int)((coord>>(8+2*index))&0x3L);
        return idx;
    }
    public static long setResolution(long coord, int res)
    {
        if ((res < 0) || (res > 28))
            throw new IllegalStateException("Unsupported resolution="+res);
        coord &= ~0x1fL;
        coord |= (res<<0);
        return coord;
    }
    public static long setFace(long coord, int face)
    {
        if ((face < 0) || (face > 28))
            throw new IllegalStateException("Unsupported face="+face);
        coord &= ~0xe0L;
        coord |= (face<<5);
        return coord;
    }
    public static long setIndicies(long coord, long indicies)
    {
        coord &= ~0xffL;
        coord |= (indicies<<8);
        return coord;
    }
    public static long setIndex(long coord, int index, int val)
    {
        coord &= ~(0x3L<<(8+2*index));
        coord |= (val<<(8+2*index));
        return coord;
    }
    
    private static final Poly2D[] FACE_BOUNDS = {
        new Poly2D( LonLat.NORTH_POLE, new LonLat(Math.PI/4, Math.PI/4), new LonLat(0, 0), new LonLat(-Math.PI/4, Math.PI/4) ),
        new Poly2D( LonLat.NORTH_POLE, new LonLat(3*Math.PI/4, Math.PI/4), new LonLat(Math.PI/2, 0), new LonLat(Math.PI/4, Math.PI/4) ),
        new Poly2D( LonLat.NORTH_POLE, new LonLat(5*Math.PI/4, Math.PI/4), new LonLat(Math.PI, 0), new LonLat(3*Math.PI/4, Math.PI/4) ),
        new Poly2D( LonLat.NORTH_POLE, new LonLat(7*Math.PI/4, Math.PI/4), new LonLat(3*Math.PI/2, 0), new LonLat(5*Math.PI/4, Math.PI/4) ),
        new Poly2D( new LonLat(Math.PI/4, Math.PI/4), new LonLat(Math.PI/2, 0), new LonLat(Math.PI/4, -Math.PI/4), new LonLat(0, 0) ),
        new Poly2D( new LonLat(3*Math.PI/4, Math.PI/4), new LonLat(Math.PI, 0), new LonLat(3*Math.PI/4, -Math.PI/4), new LonLat(Math.PI/2, 0) ),
        new Poly2D( new LonLat(5*Math.PI/4, Math.PI/4), new LonLat(3*Math.PI/2, 0), new LonLat(5*Math.PI/4, -Math.PI/4), new LonLat(Math.PI, 0) ),
        new Poly2D( new LonLat(7*Math.PI/4, Math.PI/4), new LonLat(2*Math.PI, 0), new LonLat(7*Math.PI/4, -Math.PI/4), new LonLat(3*Math.PI/2, 0) ),
        new Poly2D( new LonLat(Math.PI/2, 0), new LonLat(3*Math.PI/4, -Math.PI/4), LonLat.SOUTH_POLE, new LonLat(Math.PI/4, -Math.PI/2) ),
        new Poly2D( new LonLat(Math.PI, 0), new LonLat(5*Math.PI/4, -Math.PI/4), LonLat.SOUTH_POLE, new LonLat(3*Math.PI/4, -Math.PI/2) ),
        new Poly2D( new LonLat(3*Math.PI/2, 0), new LonLat(7*Math.PI/4, -Math.PI/4), LonLat.SOUTH_POLE, new LonLat(5*Math.PI/4, -Math.PI/2) ),
        new Poly2D( new LonLat(2*Math.PI, 0), new LonLat(9*Math.PI/4, -Math.PI/4), LonLat.SOUTH_POLE, new LonLat(7*Math.PI/4, -Math.PI/2) ),
    };
    
    static Poly2D subdivide(Poly2D bounds, int subSquare)
    {
        switch (subSquare)
        {
            case 0:
            {
                Point2D p01 = Point2DLogic.average(bounds.points[0], bounds.points[1]);
                Point2D p30 = Point2DLogic.average(bounds.points[3], bounds.points[0]);
                Point2D p0123 = Point2DLogic.average(bounds.points[0], bounds.points[1], bounds.points[2], bounds.points[3]);
                return new Poly2D(bounds.points[0], p01, p0123, p30);
            }
            case 1:
            {
                Point2D p01 = Point2DLogic.average(bounds.points[0], bounds.points[1]);
                Point2D p12 = Point2DLogic.average(bounds.points[1], bounds.points[2]);
                Point2D p0123 = Point2DLogic.average(bounds.points[0], bounds.points[1], bounds.points[2], bounds.points[3]);
                return new Poly2D(p01, bounds.points[1], p12, p0123);
            }
            case 2:
            {
                Point2D p12 = Point2DLogic.average(bounds.points[1], bounds.points[2]);
                Point2D p23 = Point2DLogic.average(bounds.points[2], bounds.points[3]);
                Point2D p0123 = Point2DLogic.average(bounds.points[0], bounds.points[1], bounds.points[2], bounds.points[3]);
                return new Poly2D(p0123, p12, bounds.points[2], p23);
            }
            case 3:
            {
                Point2D p23 = Point2DLogic.average(bounds.points[2], bounds.points[3]);
                Point2D p30 = Point2DLogic.average(bounds.points[3], bounds.points[0]);
                Point2D p0123 = Point2DLogic.average(bounds.points[0], bounds.points[1], bounds.points[2], bounds.points[3]);
                return new Poly2D(p30, p0123, p23, bounds.points[3]);
            }
        }
        throw new IllegalArgumentException("Invalid subsquare: "+subSquare);
    }
    
    static Poly2D[] subdivide(Poly2D bounds)
    {
        Point2D p01 = Point2DLogic.average(bounds.points[0], bounds.points[1]);
        Point2D p12 = Point2DLogic.average(bounds.points[1], bounds.points[2]);
        Point2D p23 = Point2DLogic.average(bounds.points[2], bounds.points[3]);
        Point2D p30 = Point2DLogic.average(bounds.points[3], bounds.points[0]);
        Point2D p0123 = Point2DLogic.average(bounds.points[0], bounds.points[1], bounds.points[2], bounds.points[3]);
        Poly2D[] subs = new Poly2D[] { 
                new Poly2D(bounds.points[0], p01, p0123, p30),
                new Poly2D(p01, bounds.points[1], p12, p0123),
                new Poly2D(p0123, p12, bounds.points[2], p23),
                new Poly2D(p30, p0123, p23, bounds.points[3]),
        };
        return subs;
    }
    
    public static Poly2D getFaceBounds(long coord)
    {
        return FACE_BOUNDS[getFace(coord)];
    }
    
    public static Poly2D getBounds(long coord)
    {
        Poly2D b = getFaceBounds(coord);
        int res = getResolution(coord);
        for (int i = 0; i < res; i++)
        {
            int idx = getIndex(coord, i);
            b = subdivide(b, idx);
        }
        return b;
    }
    
    public static LonLat toLonLat(long coord)
    {
        Poly2D b = getBounds(coord);
        Point2D middle = Point2DLogic.average(b.points);
        return new LonLat(middle);
    }
    
    public static long toHPL(Point2D lonlat, int res)
    {
        long coord = 0;
        LonLat ll = new LonLat(lonlat);
        LonLatLogic.normalize(ll);
        setResolution(coord, res);
        int face = -1;
        for (int f = 0; f < 12; f++)
            if (FACE_BOUNDS[f].contains(lonlat))
            {
                face = f;
                break;
            }
        if (face == -1)
        {
            ll.setLongitude(ll.getLongitude() + Math.PI*2);
            for (int f = 0; f < 12; f++)
                if (FACE_BOUNDS[f].contains(lonlat))
                {
                    face = f;
                    break;
                }
            if (face == -1)
                throw new IllegalArgumentException("Unsupported lon/lat: "+lonlat);
        }
        setFace(coord, face);
        Poly2D b = FACE_BOUNDS[face];
        for (int r = 0; r < res; r++)
        {
            int subsquare = -1;
            Poly2D[] subSquares = subdivide(b);
            for (int ss = 0; ss < 4; ss++)
            {
                Poly2D b1 = subSquares[ss];
                if (b1.contains(ll))
                {
                    b = b1;
                    subsquare = ss;
                    break;
                }
            }
            if (subsquare == -1)
                throw new IllegalArgumentException("Cannot find "+ll+" in subdivision "+r+" of face "+face);
            setIndex(coord, r, subsquare);
        }
        return coord;
    }
    
    public static long nextHPLong(Random rnd, int res)
    {
        long coord = 0;
        setResolution(coord, res);
        setFace(coord, rnd.nextInt(12));
        setIndicies(coord, rnd.nextLong());
        return coord;
    }
}
