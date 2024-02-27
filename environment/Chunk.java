
package engine.environment;

import engine.Engine;
import engine.environment.data.RescourceManager;
import engine.meshes.Mesh;
import engine.vectors.Vec3D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jacob
 */
public class Chunk
{
    private boolean needUnload;
    
    private boolean hasGeneratedMesh;
    
    private boolean isLazy;
    
    /**
     * 
     */
    private Mesh MESH;
    /**
     * 
     */
    private final byte[][][] DATA;
    
    /**
     * 
     */
    public final int X_ORIGIN, Y_ORIGIN, Z_ORIGIN; 
    /**
     * 
     */
    public final static int CHUNK_WIDTH = 8;
    /**
     * 
     */
    public final static int CHUNK_DEPTH = 8;
    /**
     * 
     */
    public final static int CHUNK_HEIGHT = 8;
    
    private final static byte[][][]EMPTY_CHUNK;
    private final static byte[][][]FULL_CHUNK;
    
    /*
    private final static byte[][][]LEFT;
    private final static byte[][][]RIGHT;
    
    private final static byte[][][]BOTTOM;
    private final static byte[][][]TOP;
    
    private final static byte[][][]BACK;
    private final static byte[][][]FRONT;
    */
    
    static
    {
        EMPTY_CHUNK = new byte[Chunk.CHUNK_WIDTH] [Chunk.CHUNK_HEIGHT] [Chunk.CHUNK_DEPTH];
        FULL_CHUNK = new byte[Chunk.CHUNK_WIDTH] [Chunk.CHUNK_HEIGHT] [Chunk.CHUNK_DEPTH];
        /*
        LEFT = new byte[Chunk.CHUNK_WIDTH] [Chunk.CHUNK_HEIGHT] [Chunk.CHUNK_DEPTH];
        RIGHT = new byte[Chunk.CHUNK_WIDTH] [Chunk.CHUNK_HEIGHT] [Chunk.CHUNK_DEPTH];
        
        BOTTOM = new byte[Chunk.CHUNK_WIDTH] [Chunk.CHUNK_HEIGHT] [Chunk.CHUNK_DEPTH];
        TOP = new byte[Chunk.CHUNK_WIDTH] [Chunk.CHUNK_HEIGHT] [Chunk.CHUNK_DEPTH];
        
        BACK = new byte[Chunk.CHUNK_WIDTH] [Chunk.CHUNK_HEIGHT] [Chunk.CHUNK_DEPTH];
        FRONT = new byte[Chunk.CHUNK_WIDTH] [Chunk.CHUNK_HEIGHT] [Chunk.CHUNK_DEPTH];
        
        for (int ix = 0; ix < Chunk.CHUNK_WIDTH; ix++)
        {
            for (int iy = 0; iy < Chunk.CHUNK_HEIGHT; iy++)
            {
                for (int iz = 0; iz < Chunk.CHUNK_DEPTH; iz++)
                {
                    LEFT[ix][iy][iz] = ix == Chunk.CHUNK_WIDTH-1 ? (byte)0 : (byte)1;
                    RIGHT[ix][iy][iz] = ix == 0 ? (byte)0 : (byte)1;
                    
                    BOTTOM[ix][iy][iz] = iy == Chunk.CHUNK_HEIGHT-1 ? (byte)0 : (byte)1;
                    TOP[ix][iy][iz] = iy == 0 ? (byte)0 : (byte)1;
                    
                    BACK[ix][iy][iz] = iz == Chunk.CHUNK_DEPTH-1 ? (byte)0 : (byte)1;
                    FRONT[ix][iy][iz] = iz == 0? (byte)0 : (byte)1;
                }
            }
        }
        */
        
        for (int ix = 0; ix < Chunk.CHUNK_WIDTH; ix++)
        {
            for (int iy = 0; iy < Chunk.CHUNK_HEIGHT; iy++)
            {
                for (int iz = 0; iz < Chunk.CHUNK_DEPTH; iz++)
                {
                    FULL_CHUNK[ix][iy][iz] = 1;
                }
            }
        }
               
    }
    
    
    /**
     * 
     * @param x
     * @param y
     * @param z 
     * @param data 
     */
    public Chunk(int x, int y, int z, byte[][][] data)
    {
        this.X_ORIGIN = x;
        this.Y_ORIGIN = y;
        this.Z_ORIGIN = z;
        
        
        DATA = data;
        
        MESH = new Mesh();
        
        needUnload = false;
        
        hasGeneratedMesh = false;
        
        isLazy = false;
    }
    
    /**
     * 
     * @param surroundingChunks 
     * @param WORLD 
     */
    public void updateChunk(Chunk[] surroundingChunks, final World WORLD)
    {
        for (int ix = 0; ix < Chunk.CHUNK_WIDTH; ix++)
        {
            for (int iy = 0; iy < Chunk.CHUNK_HEIGHT; iy++)
            {
                for (int iz = 0; iz < Chunk.CHUNK_DEPTH; iz++)
                {
                    if (iy == Chunk.CHUNK_HEIGHT - 1)
                    {
                        if (DATA[ix][iy][iz] == 7)
                        {
                            if (surroundingChunks[3] != null && surroundingChunks[3].DATA[ix][0][iz] == 0)
                            {
                                DATA[ix][iy][iz] = 0;
                                surroundingChunks[3].DATA[ix][0][iz] = 7;
                                this.needsMeshUpdate();
                                surroundingChunks[3].needsMeshUpdate();
                                WORLD.MESH_UPDATER.markUpdate();
                            }
                        }
                    }
                    else
                    {
                        if (DATA[ix][iy][iz] == 7 && DATA[ix][iy+1][iz] == 0)
                        {
                            DATA[ix][iy][iz] = 0;
                            DATA[ix][iy+1][iz] = 7;
                            this.needsMeshUpdate();
                            WORLD.MESH_UPDATER.markUpdate();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 
     * @return 
     */
    public Vec3D getChunkCenter()
    {
        return new Vec3D(this.X_ORIGIN + (Chunk.CHUNK_WIDTH/2), this.Y_ORIGIN + (Chunk.CHUNK_HEIGHT/2), this.Z_ORIGIN + (Chunk.CHUNK_DEPTH/2));
    }
    
    /**
     * 
     */
    public void markUnload()
    {
        needUnload = true;
    }
    /**
     * 
     * @return 
     */
    public boolean needUnload()
    {
        return needUnload;
    }
    /**
     * 
     * @param set 
     */
    public void setLazy(boolean set)
    {
        isLazy = set;
    }
    /**
     * 
     * @return 
     */
    public boolean isLazy()
    {
        return isLazy;
    }
    
    /**
     * 
     * @param x
     * @param y
     * @param z 
     */
    public Chunk(int x, int y, int z)
    {
        
        this(x, y, z, new byte[CHUNK_WIDTH][CHUNK_DEPTH][CHUNK_HEIGHT]);
        this.LOAD_CHUNK();
    }
    
    /**
     * 
     * @param worldX
     * @return 
     */
    private int worldToChunkX(int worldX)
    {
        return worldX - X_ORIGIN;
    }
    /**
     * 
     * @param worldX
     * @return 
     */
    private int worldToChunkY(int worldY)
    {
        return worldY - Y_ORIGIN;
    }
    /**
     * 
     * @param worldX
     * @return 
     */
    private int worldToChunkZ(int worldZ)
    {
        return worldZ - Z_ORIGIN;
    }
    /**
     * 
     * @param chunkX
     * @param chunkY
     * @param chunkZ
     * @return 
     */
    private byte getBlockFromChunkCord(int chunkX, int chunkY, int chunkZ)
    {
        return DATA[chunkX][chunkY][chunkZ];
    }
    /**
     * 
     * @param worldX
     * @param worldY
     * @param worldZ
     * @return 
     */
    public byte getBlockFromWorldCord(int worldX, int worldY, int worldZ)
    {
        return getBlockFromChunkCord(worldToChunkX(worldX), worldToChunkY(worldY), worldToChunkZ(worldZ));
    }
    
    /**
     * 
     * @param chunkX
     * @param chunkY
     * @param chunkZ
     * @param data 
     */
    public void setBlockFromChunkCord(int chunkX, int chunkY, int chunkZ, byte data)
    {
        DATA[chunkX][chunkY][chunkZ] = data;
    }
    /**
     * 
     * @param worldX
     * @param worldY
     * @param worldZ
     * @param data 
     */
    public void setBlockFromWorldCord(int worldX, int worldY, int worldZ, byte data)
    {
        setBlockFromChunkCord(worldToChunkX(worldX), worldToChunkY(worldY), worldToChunkZ(worldZ), data);
    }
    
    /**
     * 
     * @param worldX
     * @param worldY
     * @param worldZ
     * @return 
     */
    public boolean isWorldCordInChunk(int worldX, int worldY, int worldZ)
    {
        int chunkX = worldToChunkX(worldX);
        int chunkY = worldToChunkY(worldY);
        int chunkZ = worldToChunkZ(worldZ);
        
        return 0 <= chunkX && chunkX < CHUNK_WIDTH && 0 <= chunkY && chunkY < CHUNK_HEIGHT && 0 <= chunkZ && chunkZ < CHUNK_DEPTH;
    }
    
    
    
    /**
     * 
     * @param surroundingChunks
     * @param RESCOURCE_MANAGER
     */
    public final void REGENERATE_CHUNK_MESH(Chunk[] surroundingChunks, RescourceManager RESCOURCE_MANAGER)
    {
        byte[][][][] externalData = new byte[6][][][];
        
        for (int i = 0; i < 6; i++)
        {
            if (surroundingChunks[i] != null)
            {
                externalData[i] = surroundingChunks[i].DATA;
            }
            else
            {
                externalData[i] = FULL_CHUNK;
            }
        }
        /*
        externalData[0] = LEFT;
        externalData[1] = RIGHT;
        
        externalData[2] = BOTTOM;
        externalData[3] = TOP;
        
        externalData[4] = BACK;
        externalData[5] = FRONT;
*/
        
        MESH = Mesh.generateChunkMesh(DATA, externalData, X_ORIGIN, Y_ORIGIN, Z_ORIGIN, RESCOURCE_MANAGER);
        
        hasGeneratedMesh = true;
    }
    /**
     * 
     * @return 
     */
    public boolean hasGeneratedMesh()
    {
        return hasGeneratedMesh;
    }
    /**
     * 
     */
    public void needsMeshUpdate()
    {
        hasGeneratedMesh = false;
    }
    
    /**
     * 
     * @return Returns filename for saved chunk
     */
    private String getFileName()
    {
        return "World\\chnk&"+X_ORIGIN+"&"+Y_ORIGIN+"&"+Z_ORIGIN;
    }
    /**
     * 
     */
    public final void SAVE_CHUNK()
    {

        if (1==1) return; //Disable saving

        System.out.println("Saving Chunk");
        //long start = System.nanoTime();
        //System.out.println("YAY");
        
        FileOutputStream outputStream = null;
        try
        {
            File outputFile = new File(getFileName());
            
            outputStream = new FileOutputStream(outputFile);
            for (int ix = 0; ix < DATA.length; ix++)
            {
                for (int iy = 0; iy < DATA[ix].length; iy++)
                {
                    outputStream.write(DATA[ix][iy]);
                }
            }
            
            outputStream.close();
            
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(Chunk.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Chunk.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*
        long end = System.nanoTime();
        
        long time = end - start;
        
        final double TIME_SECONDS= time * Engine.ONEBILLIONTH;
        
        System.out.println("TIME: " + TIME_SECONDS);
        */
    }
    /**
     * 
     */
    public final void LOAD_CHUNK()
    {

        System.out.println("Loading Chunk");
        
        try
        {
            File inputFile = new File(getFileName());

            FileInputStream inputStream = new FileInputStream(inputFile);
            
            for (byte[][] DATA1 : DATA) {
                for (byte[] item : DATA1) {
                    inputStream.read(item);
                    /*
                    for (int iz = 0; iz < DATA[ix][iy].length; iz++)
                    {
                    DATA[ix][iy][iz] = (byte)inputStream.read();
                    }
                    */
                }
            }
            
        }
        catch (IOException ex)
        {
            Logger.getLogger(Chunk.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    /**
     * 
     * @param x
     * @param y
     * @param z
     * @return 
     */
    public static final boolean CHUNK_EXISTS(int x, int y, int z)
    {
        File outputFile = new File("World\\chnk&"+x+"&"+y+"&"+z);
        return outputFile.isFile();
    }
    /**
     * 
     * @param m 
     */
    public void addMeshLighting(Mesh m)
    {
        for (int ix = 0; ix < Chunk.CHUNK_WIDTH; ix++)
        {
            for (int iy = 0; iy < Chunk.CHUNK_HEIGHT; iy++)
            {
                for (int iz = 0; iz < Chunk.CHUNK_DEPTH; iz++)
                {
                    /*
                    Used for creating blocks with light
                    if (DATA[ix][iy][iz] == 3)
                    {
                        m.addLightSource(new Vec3D(ix+X_ORIGIN+0.5f, iy+Y_ORIGIN+0.5f, iz+Z_ORIGIN+0.5f), 1f, 0.6f, 0.5f);
                    }
                    */
                }
            }
        }
    }

    /**
     * 
     * @return 
     */
    public Mesh getMesh()
    {
        return MESH;
    }
}
