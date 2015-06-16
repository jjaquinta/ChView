package jo.util.geom3d;


public class Point3D
{
	public double	x;
	public double	y;
	public double	z;
	
	public Point3D()
	{
		x = 0;
		y = 0;
		z = 0;
	}
	
	public Point3D(double _x, double _y, double _z)
	{
		x = _x;
		y = _y;
		z = _z;
	}
	
	public Point3D(Point3D p)
	{
		x = p.x;
		y = p.y;
		z = p.z;
	}

    /**
     * Returns a hash code value based on the data values in this
     * object.  Two different Tuple3d objects with identical data values
     * (i.e., Tuple3d.equals returns true) will return the same hash
     * code value.  Two objects with different data members may return the
     * same hash value, although this is not likely.
     * @return the integer hash code value
     */  
     public int hashCode() {
      long bits = 1L;
      bits = 31L * bits + doubleToLongBits(x);
      bits = 31L * bits + doubleToLongBits(y);
      bits = 31L * bits + doubleToLongBits(z);
      return (int) (bits ^ (bits >> 32));
    }

     /**
      * Returns the representation of the specified floating-point
      * value according to the IEEE 754 floating-point "double format"
      * bit layout, after first mapping -0.0 to 0.0. This method is
      * identical to Double.doubleToLongBits(double) except that an
      * integer value of 0L is returned for a floating-point value of
      * -0.0. This is done for the purpose of computing a hash code
      * that satisfies the contract of hashCode() and equals(). The
      * equals() method in each vecmath class does a pair-wise "=="
      * test on each floating-point field in the class (e.g., x, y, and
      * z for a Tuple3d). Since 0.0&nbsp;==&nbsp;-0.0 returns true, we
      * must also return the same hash code for two objects, one of
      * which has a field with a value of -0.0 and the other of which
      * has a cooresponding field with a value of 0.0.
      *
      * @param d an input double precision floating-point number
      * @return the integer bits representing that floating-point
      * number, after first mapping -0.0f to 0.0f
      */
      private long doubleToLongBits(double d) {
       // Check for +0 or -0
       if (d == 0.0) {
           return 0L;
       }
       else {
           return Double.doubleToLongBits(d);
       }
     }

	public String toString()
	{
	    return "("+x+","+y+","+z+")";
	}
    
    public String toIntString()
    {
        return "("+(int)x+","+(int)y+","+(int)z+")";
    }
    
    public float[] toFloatArray()
    {
        return new float[] { (float)x, (float)y, (float)z };
    }
    
    public double[] toDoubleArray()
    {
        return new double[] { (double)x, (double)y, (double)z };
    }
	
	public Point3D add(Point3D p)
	{
		return new Point3D(x + p.x, y + p.y, z + p.z);
	}
	
	public Point3D sub(Point3D p)
	{
		return new Point3D(x - p.x, y - p.y, z - p.z);
	}
	
	public void incr(Point3D p)
	{
		x += p.x;
		y += p.y;
		z += p.z;
	}
	
	public void decr(Point3D p)
	{
		x -= p.x;
		y -= p.y;
		z -= p.z;
	}
	
	public Point3D mult(double scale)
	{
		return new Point3D(x*scale, y*scale, z*scale);
	}
	
	public void scale(double scale)
	{
		x *= scale;
		y *= scale;
		z *= scale;
	}
	
	public double mag()
	{
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	public void setMag(double mag)
	{
		normalize();
		scale(mag);
	}
	
	public void normalize()
	{
		double m = mag();
		if (m > 0)
			scale(1/m);
	}
	
	public Point3D normal()
	{
		Point3D n = new Point3D(this);
		n.normalize();
		return n;
	}
	
	public double dot(Point3D p)
	{
		return x*p.x + y*p.y + z*p.z;
	}
	
	public Point3D cross(Point3D v2)
	{
    	Point3D v3 = new Point3D();
    	v3.x = y*v2.z - z*v2.y;
    	v3.y = z*v2.x - x*v2.z;
    	v3.z = x*v2.y - y*v2.x;
    	return v3;
	}
	
	public double dist(Point3D p)
	{
		return sub(p).mag();
	}

    public double dist(double x, double y, double z)
    {
        return dist(new Point3D(x, y, z));
    }

	public void set(Point3D p)
	{
		x = p.x;
		y = p.y;
		z = p.z;
	}

	public void set(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getZ()
    {
        return z;
    }

    public void setZ(double z)
    {
        this.z = z;
    }

	/*
	public void rotate(double theta)
	{
		double cosTheta = Math.cos(theta);
		double sinTheta = Math.sin(theta);
		double nx = x*cosTheta + y*sinTheta;
		double ny = -x*sinTheta + y*cosTheta;
		x = nx;
		y = ny;
	}
	
	public Point3D rotation(double theta)
	{
		Point3D p = new Point3D(this);
		p.rotate(theta);
		return p;
	}
	
	public void rotate(Point3D around, double theta)
	{
		decr(around);
		rotate(theta);
		incr(around);
	}
	
	public Point3D rotation(Point3D around, double theta)
	{
		Point3D p = new Point3D(this);
		p.decr(around);
		p.rotate(theta);
		p.incr(around);
		return p;
	}
	*/
}
