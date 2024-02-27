
package engine.meshes.structures;

/**
 *
 * @author jacob
 */
public class Particle
{
    /**
     * 
     */
    public final int POINT;
    /**
     * 
     */
    public final double RADIUS;
    /**
     * 
     */
    public final int TEXTUREID;
    /**
     * 
     */
    public double redB;
    /**
     * 
     */
    public double greenB;
    /**
     * 
     */
    public double blueB;
    /**
     * 
     * @param point
     * @param radius
     * @param textureID
     */
    public Particle(int point, int radius, int textureID)
    {
        POINT = point;
        RADIUS = radius;
        TEXTUREID = textureID;

        redB = 0;
        greenB = 0;
        blueB = 0;
    }
}
