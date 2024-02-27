
package engine.vectors;

/**
 *
 * @author jacob
 */
public class Vec3D
{
    /**
     * 
     */
    public float X, Y, Z;
    /**
     * 
     */
    public Vec3D()
    {
        this(0,0,0);
    }
    /**
     * 
     * @param x
     * @param y
     * @param z 
     */
    public Vec3D(float x, float y, float z)
    {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }
    /**
     * 
     * @param t 
     */
    public void translate(Vec3D t)
    {
        this.X += t.X;
        this.Y += t.Y;
        this.Z += t.Z;
    }
    /**
     * 
     * @param t
     * @return 
     */
    public Vec3D addHere(Vec3D t)
    {
        this.X += t.X;
        this.Y += t.Y;
        this.Z += t.Z;
        return this;
    }
    /**
     * 
     * @param t
     * @return 
     */
    public Vec3D subHere(Vec3D t)
    {
        this.X -= t.X;
        this.Y -= t.Y;
        this.Z -= t.Z;
        return this;
    }
    /**
     * 
     * @param s
     * @return 
     */
    public Vec3D scaleHere(float s)
    {
        this.X *= s;
        this.Y *= s;
        this.Z *= s;
        return this;
    }
    /**
     * 
     * @param x
     * @param y
     * @param z
     * @return 
     */
    public Vec3D setHere(float x, float y, float z)
    {
        this.X = x;
        this.Y = y;
        this.Z = z;
        return this;
    }
    
    /**
     * 
     * @param t
     * @return 
     */
    public Vec3D add(Vec3D t)
    {
        return new Vec3D(this.X+t.X, this.Y+t.Y, this.Z+t.Z);
    }
    /**
     * 
     * @param t
     * @return 
     */
    public Vec3D sub(Vec3D t)
    {
        return new Vec3D(this.X-t.X, this.Y-t.Y, this.Z-t.Z);
    }
    /**
     * 
     * @param t
     * @return 
     */
    public Vec3D diff(Vec3D t)
    {
        return new Vec3D(this.X-t.X, this.Y-t.Y, this.Z-t.Z);
    }
    /**
     * 
     * @return 
     */
    public Vec3D norm()
    {
        return this.scale(1f/mag());
    }
    /**
     * 
     * @return 
     */
    public float mag()
    {
        return (float)Math.sqrt((X*X)+(Y*Y)+(Z*Z));
    }
    /**
     * 
     * @return 
     */
    public float magSquared()
    {
        return (X*X)+(Y*Y)+(Z*Z);
    }
    /**
     * 
     * @param s
     * @return 
     */
    public Vec3D scale(float s)
    {
        return new Vec3D(this.X*s, this.Y*s, this.Z*s);
    }
    /**
     * 
     * @return 
     */
    public Vec3D deepCopy()
    {
        return new Vec3D(this.X, this.Y, this.Z);
    }
    /**
     * 
     * @param a
     * @return 
     */
    public float dot(Vec3D a)
    {
        return (this.X*a.X)+(this.Y*a.Y)+(this.Z*a.Z);
    }
    /**
     * 
     * @return 
     */
    @Override
    public String toString()
    {
        StringBuilder ret = new StringBuilder();
        
        ret.append("X: ");
        ret.append(this.X);
        ret.append(" Y: ");
        ret.append(this.Y);
        ret.append(" Z: ");
        ret.append(this.Z);
        
        return ret.toString();
    }
    
    /**
     * 
     * @param siny
     * @param sinx
     * @param cosy
     * @param cosx
     * @param mag
     * @return 
     */
    public static Vec3D contructAngleVector(float siny, float sinx, float cosy, float cosx, float mag)
    {
        return new Vec3D(-(cosx*siny*mag), (sinx*mag), cosy*cosx*mag);
    }
}
