
package engine.environment.data;

/**
 *
 * @author jacob
 */
public class BlockData
{
    /**
     * 
     */
    private final int[] FACE_TEXTURE_ID;
    /**
     * 
     */
    private final String NAME;
    /**
     * 
     */
    private final boolean IS_COLLISION_SOLID;
    /**
     * 
     */
    private final boolean IS_RENDER_SOLID;
    /**
     * 
     */
    private final boolean HAS_GRAVITY;
    /**
     * 
     */
    private final float R_LIGHT;
    /**
     * 
     */
    private final float G_LIGHT;
    /**
     * 
     */
    private final float B_LIGHT;
    
    private final boolean EMITS_LIGHT;
    /**
     * 
     * @param name
     * @param faceTextureId 
     * @param isCollisionSolid 
     * @param isRenderSolid 
     * @param hasGravity 
     * @param r 
     * @param g 
     * @param b 
     */
    public BlockData(String name, int[] faceTextureId, boolean isCollisionSolid, boolean isRenderSolid, boolean hasGravity, float r, float g, float b)
    {
        this.NAME = name;
        this.FACE_TEXTURE_ID = faceTextureId;
        
        this.IS_COLLISION_SOLID = isCollisionSolid;
        this.IS_RENDER_SOLID = isRenderSolid;
        
        this.HAS_GRAVITY = hasGravity;
        
        this.R_LIGHT = r;
        this.G_LIGHT = g;
        this.B_LIGHT = b;
        
        this.EMITS_LIGHT = this.R_LIGHT != 0 || this.G_LIGHT != 0 || this.B_LIGHT != 0;
    }
    /**
     * 
     * @return 
     */
    public String getName()
    {
        return NAME;
    }
    /**
     * 
     * @param face
     * @return 
     */
    public int getTexture(int face)
    {
        return FACE_TEXTURE_ID[face];
    }
    /**
     * 
     * @return 
     */
    public boolean isCollisionSolid()
    {
        return IS_COLLISION_SOLID;
    }
    /**
     * 
     * @return 
     */
    public boolean isRenderSolid()
    {
        return IS_RENDER_SOLID;
    }
    /**
     * 
     * @return 
     */
    public boolean hasGravity()
    {
        return HAS_GRAVITY;
    }
    /**
     * 
     * @return 
     */
    public boolean emitsLight()
    {
        return EMITS_LIGHT;
    }
    /**
     * 
     * @return 
     */
    public float getRed()
    {
        return R_LIGHT;
    }
    /**
     * 
     * @return 
     */
    public float getGreen()
    {
        return G_LIGHT;
    }
    /**
     * 
     * @return 
     */
    public float getBlue()
    {
        return B_LIGHT;
    }
}
