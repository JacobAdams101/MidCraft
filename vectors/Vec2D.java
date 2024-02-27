
package engine.vectors;

/**
 *
 * @author jacob
 */
public class Vec2D
{
    /**
     * 
     */
    public float X, Y;
    /**
     * 
     */
    public Vec2D()
    {
        this(0,0);
    }
    /**
     * 
     * @param x
     * @param y
     */
    public Vec2D(float x, float y)
    {
        this.X = x;
        this.Y = y;
    }
    /**
     * 
     * @param t 
     */
    public void translate(Vec2D t)
    {
        this.X += t.X;
        this.Y += t.Y;
    }
    /**
     * 
     * @param t
     * @return 
     */
    public Vec2D add(Vec2D t)
    {
        return new Vec2D(this.X+t.X, this.Y+t.Y);
    }
    /**
     * 
     * @param t
     * @return 
     */
    public Vec2D sub(Vec2D t)
    {
        return new Vec2D(this.X-t.X, this.Y-t.Y);
    }
    /**
     * 
     * @param t
     * @return 
     */
    public Vec2D diff(Vec2D t)
    {
        return new Vec2D(this.X-t.X, this.Y-t.Y);
    }
    /**
     * 
     * @return 
     */
    public Vec2D norm()
    {
        return this.scale(1f/mag());
    }
    /**
     * 
     * @return 
     */
    public float mag()
    {
        return (float)Math.sqrt((X*X)+(Y*Y));
    }
    /**
     * 
     * @param s
     * @return 
     */
    public Vec2D scale(float s)
    {
        return new Vec2D(this.X*s, this.Y*s);
    }
    /**
     * 
     * @return 
     */
    public Vec2D deepCopy()
    {
        return new Vec2D(this.X, this.Y);
    }
    /**
     * 
     * @param a
     * @return 
     */
    public float dot(Vec2D a)
    {
        return (this.X*a.X)+(this.Y*a.Y);
    }
}
