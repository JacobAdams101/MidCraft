
package engine.environment.data;

/**
 *
 * @author jacob
 */
public class RescourceManager
{
    private final BlockData[] BLOCK_DATA;
    
    public RescourceManager()
    {
        BLOCK_DATA = new BlockData[]{
            new BlockData("Air", new int[]{-1, -1, -1, -1, -1, -1}, false, false, false, 0f, 0f, 0f),
            new BlockData("Stone", new int[]{0, 0, 0, 0, 0, 0}, true, true, false, 0f, 0f, 0f),
            new BlockData("Grass", new int[]{7, 7, 1, 5, 7, 7}, true, true, false, 0f, 0f, 0f),
            new BlockData("Planks", new int[]{2, 2, 2, 2, 2, 2}, true, true, false, 0f, 0f, 0f),
            new BlockData("Log", new int[]{3, 3, 3, 3, 3, 3}, true, true, false, 0f, 0f, 0f),
            new BlockData("Leaves", new int[]{4, 4, 4, 4, 4, 4}, true, true, false, 0f, 0f, 0f),
            new BlockData("Dirt", new int[]{5, 5, 5, 5, 5, 5}, true, true, false, 0f, 0f, 0f),
            new BlockData("Sand", new int[]{6, 6, 6, 6, 6, 6}, true, true, true, 0f, 0f, 0f),
            new BlockData("Fire", new int[]{8, 8, 8, 8, 8, 8}, true, true, false, 1f, 1f, 1f),
        };
    }
    /**
     * 
     * @param ID
     * @return 
     */
    public BlockData getBlockData(byte ID)
    {
        return BLOCK_DATA[ID];
    }

}
