
package engine.meshes.structures;

import engine.vectors.Vec2D;
import engine.vectors.Vec3D;

/**
 *
 * @author jacob
 */
public class Triangle
{
    /**
     * 
     */
    public final int[] POINTS;
    /**
     * 
     */
    public final Vec2D[] TEXPOINTS;
    /**
     * 
     */
    public int TEXTUREID;
    /**
     * 
     */
    public final Vec3D NORM;
    /**
     * 
     */
    public float redB;
    /**
     * 
     */
    public float greenB;
    /**
     * 
     */
    public float blueB;
    /**
     * 
     */
    public final boolean OVER_RIDE_BACKFACE_CULLING;
    /**
     * 
     * @param points
     * @param texPoints
     * @param textureID
     * @param norm 
     * @param overrideBackFaceCulling 
     */
    public Triangle(int[] points, Vec2D[] texPoints, int textureID, Vec3D norm, boolean overrideBackFaceCulling)
    {
        POINTS = points;
        TEXPOINTS = texPoints;
        TEXTUREID = textureID;
        NORM = norm;

        redB = 0;
        greenB = 0;
        blueB = 0;
        
        this.OVER_RIDE_BACKFACE_CULLING = overrideBackFaceCulling;
    }
    /**
     * 
     */
    public void resetLighting()
    {
        redB = 0;
        greenB = 0;
        blueB = 0;
    }
    /**
     * 
     * @return 
     */
    public Triangle deepCopy()
    {
        return deepCopy(0);
    }
    /**
     * 
     * @param pointOrigin
     * @return 
     */
    public Triangle deepCopy(int pointOrigin)
    {
        return new Triangle(new int[]{POINTS[0]+pointOrigin, POINTS[1]+pointOrigin, POINTS[2]+pointOrigin}, TEXPOINTS, TEXTUREID, NORM.deepCopy(), this.OVER_RIDE_BACKFACE_CULLING);
    }

}
