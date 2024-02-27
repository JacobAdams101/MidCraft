
package engine.environment;

import engine.vectors.Vec3D;

/**
 *
 * @author jacob
 */
public class Camera
{
    /**
     * 
     */
    public Vec3D pos;
    /**
     * 
     */    
    public float yRot;
    /**
     * 
     */
    public float xRot;
    /**
     * 
     */
    public float cosy;
    /**
     * 
     */
    public float siny;
    /**
     * 
     */
    public float cosx;
    /**
     * 
     */
    public float sinx;
    /**
     * 
     * @param p 
     */
    public Camera(Vec3D p)
    {
        this.pos = p;
        this.yRot = 0;
        this.xRot = 0;
        CALC();
    }
    /**
     * 
     * @param x
     * @param y
     * @param z 
     */
    public Camera(float x, float y, float z)
    {
        this(new Vec3D(x, y, z));
    }
    /**
     * 
     * @param x
     * @param y
     * @param z 
     */
    public void setPosition(float x, float y, float z)
    {
        this.pos.X = x;
        this.pos.Y = y;
        this.pos.Z = z;
    }

    
    /**
     * 
     */
    public final void CALC()
    {
        cosy = (float)Math.cos(yRot);
        siny = (float)Math.sin(yRot);

        cosx = (float)Math.cos(xRot);
        sinx = (float)Math.sin(xRot);
    }
    /**
     * 
     * @param p 
     */
    public void snapToPlayer(Player p)
    {
        this.pos.X = p.pos.X;
        this.pos.Y = p.pos.Y;
        this.pos.Z = p.pos.Z;
        this.yRot = p.yRot;
        this.xRot = p.xRot;
        
        this.CALC();
    }
}
