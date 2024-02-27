
package engine.environment;


import engine.Engine;
import engine.environment.data.RescourceManager;
import engine.game.GameRunner;
import engine.graphics.Window;
import engine.meshes.Mesh;
import engine.userinput.KeyInput;
import engine.userinput.MouseInput;
import engine.vectors.Vec3D;
import java.util.ArrayList;

/**
 *
 * @author jacob
 */
public class World
{
    /**
     * 
     */
    public Camera cam;
    /**
     * 
     */
    public Player player;
    /**
     * 
     */
    public Mesh mesh;
    /**
     * 
     */
    public int camFocus = 0;
    
    /**
     * 
     */
    public ArrayList<Chunk>LOADED_CHUNKS;
    /**
     * 
     */
    public final WorldGenerator WORLD_GEN;
    /**
     * 
     */
    public final MeshUpdater MESH_UPDATER;

    
    /**
     * 
     */
    public final int RENDER_DISTANCE_IN_CHUNKS = 4;
    /**
     * 
     * @return 
     */
    public int getBlockRenderDistance()
    {
        return RENDER_DISTANCE_IN_CHUNKS*Chunk.CHUNK_WIDTH;
    }
    
    
    public final RescourceManager RESCOURCE_MANAGER;
    
    /**
     * 
     * @return 
     */
    public Mesh getMesh()
    {
        return mesh;
    }
    /**
     * 
     * @param rescourceManager
     */
    public World(RescourceManager rescourceManager)
    {
        WORLD_GEN = new WorldGenerator(1);
        
        player = new Player(0, -40, 0);
        cam = new Camera(0, -10, 0);
        mesh = new Mesh();
        
        this.RESCOURCE_MANAGER = rescourceManager;
        
        LOADED_CHUNKS = new ArrayList<>();
        
        MESH_UPDATER = new MeshUpdater();
        MESH_UPDATER.setPriority(Thread.MIN_PRIORITY);
        MESH_UPDATER.start();

        callLoadUnloadChunks(player.pos);
        
        SaveOnExit exitThread = new SaveOnExit();
        
        Runtime.getRuntime().addShutdownHook(exitThread);
        
        
        
        //System.out.println("MEsh Size: " + mesh.TRIANGLES.length);
        
        
    }
    
    /**
     * 
     */
    public class SaveOnExit extends Thread
    {
        /**
        * 
        */
        @Override
        public void run()
        {
            System.out.println("=========== EXIT TIME ===========");
            LOADED_CHUNKS.forEach((c) -> {
                c.SAVE_CHUNK();
            });
        }
    }
    
    /**
     * 
     * @param list
     * @param compare
     * @return 
     */
    public int contains(ArrayList<int[]> list, int[]compare)
    {
        for (int index = 0; index < list.size(); index++)
        {
            int[] test = list.get(index);
            boolean equal = true;
            for(int i = 0; i < test.length; i++)
            {
                if (test[i] != compare[i])
                {
                    equal = false;
                }
            }
            if (equal)
            {
                return index;
            }
        }
        return -1;
    }
    /**
     * 
     * @param centerPos 
     */
    public void callLoadUnloadChunks(Vec3D centerPos)
    {
        
        //long start = System.nanoTime();
        
        int roundX;
        int roundY;
        int roundZ;
        
        if (centerPos.X > 0)
        {
            roundX = (int)(centerPos.X/Chunk.CHUNK_WIDTH);
        }
        else
        {
            roundX = (int)(centerPos.X/Chunk.CHUNK_WIDTH)-1;
        }
        if (centerPos.Y > 0)
        {
            roundY = (int)(centerPos.Y/Chunk.CHUNK_HEIGHT);
        }
        else
        {
            roundY = (int)(centerPos.Y/Chunk.CHUNK_HEIGHT)-1;
        }
        if (centerPos.Z > 0)
        {
            roundZ = (int)(centerPos.Z/Chunk.CHUNK_DEPTH);
        }
        else
        {
            roundZ = (int)(centerPos.Z/Chunk.CHUNK_DEPTH)-1;
        }
        
        
        int playerChunkX = roundX*Chunk.CHUNK_WIDTH;
        int playerChunkY = roundY*Chunk.CHUNK_HEIGHT;
        int playerChunkZ = roundZ*Chunk.CHUNK_DEPTH;
        

        ArrayList<int[]>potentialLoads = new ArrayList<>();
        ArrayList<int[]>lazyLoads = new ArrayList<>();
        
        final int ADJACENT_CHUNK_THRESH = RENDER_DISTANCE_IN_CHUNKS+1;
        
        for (int ix = -ADJACENT_CHUNK_THRESH*Chunk.CHUNK_WIDTH; ix <= ADJACENT_CHUNK_THRESH*Chunk.CHUNK_WIDTH; ix += Chunk.CHUNK_WIDTH)
        {
            for (int iy = -ADJACENT_CHUNK_THRESH*Chunk.CHUNK_HEIGHT; iy <= ADJACENT_CHUNK_THRESH*Chunk.CHUNK_HEIGHT; iy += Chunk.CHUNK_HEIGHT)
            {
                for (int iz = -ADJACENT_CHUNK_THRESH*Chunk.CHUNK_DEPTH; iz <= ADJACENT_CHUNK_THRESH*Chunk.CHUNK_DEPTH; iz += Chunk.CHUNK_DEPTH)
                {
                    if (
                            ix >= -RENDER_DISTANCE_IN_CHUNKS*Chunk.CHUNK_WIDTH && ix <= RENDER_DISTANCE_IN_CHUNKS*Chunk.CHUNK_WIDTH && 
                            iy >= -RENDER_DISTANCE_IN_CHUNKS*Chunk.CHUNK_HEIGHT && iy <= RENDER_DISTANCE_IN_CHUNKS*Chunk.CHUNK_HEIGHT && 
                            iz >= -RENDER_DISTANCE_IN_CHUNKS*Chunk.CHUNK_DEPTH && iz <= RENDER_DISTANCE_IN_CHUNKS*Chunk.CHUNK_DEPTH
                            )
                    {
                        potentialLoads.add(new int[]{ix+playerChunkX, iy+playerChunkY, iz+playerChunkZ});
                    }
                    else
                    {
                        lazyLoads.add(new int[]{ix+playerChunkX, iy+playerChunkY, iz+playerChunkZ});
                    }
                    
                    //System.out.println("x: " + ix + " y:" + iy + " z: " + iz);
                }
            }
        }
        
        
        for (int i = 0; i < LOADED_CHUNKS.size(); i++)
        {
            Chunk c = LOADED_CHUNKS.get(i);
            int result = contains(potentialLoads, new int[]{c.X_ORIGIN, c.Y_ORIGIN, c.Z_ORIGIN});
            if (result == -1)
            {
                result = contains(lazyLoads, new int[]{c.X_ORIGIN, c.Y_ORIGIN, c.Z_ORIGIN});
                if (result == -1)
                { 
                    c.markUnload();
                }
                else
                {
                    lazyLoads.remove(result);
                    c.setLazy(true);
                }
                
            }
            else
            {
                potentialLoads.remove(result);
                c.setLazy(false);
            }
        }
        
        final int MAX_CHUNKS_UNLOAD_FRAME = 1;
        
        int chunksAlreadyUnloaded = 0;
        

        boolean removedChunk = false;
        boolean loadedNewChunk = false;

        for (int i = 0; i < LOADED_CHUNKS.size(); i++)
        {
            Chunk c = LOADED_CHUNKS.get(i);
            if (c.needUnload())
            {
                c.SAVE_CHUNK(); //Save chunk so data is remembered
                LOADED_CHUNKS.remove(c);
                removedChunk = true;
                i--;
                chunksAlreadyUnloaded++;
                if (chunksAlreadyUnloaded >= MAX_CHUNKS_UNLOAD_FRAME)
                {
                    break;
                }
            }
        }

        for (int[] load : potentialLoads)
        {
            loadedNewChunk = true;
            Chunk chunk;
            if (Chunk.CHUNK_EXISTS(load[0], load[1], load[2]))
            {
                chunk = new Chunk(load[0], load[1], load[2]);
                chunk.setLazy(false);
            }
            else
            {
                chunk = new Chunk(load[0], load[1], load[2], WORLD_GEN.getData(load[0], load[1], load[2], Chunk.CHUNK_WIDTH, Chunk.CHUNK_HEIGHT, Chunk.CHUNK_DEPTH));
                chunk.setLazy(false);
            }
            LOADED_CHUNKS.add(chunk);
        }

        for (int[] load : potentialLoads)
        {
            Chunk chunk;
            if (Chunk.CHUNK_EXISTS(load[0], load[1], load[2]))
            {
                chunk = new Chunk(load[0], load[1], load[2]);
                chunk.setLazy(true);
            }
            else
            {
                chunk = new Chunk(load[0], load[1], load[2], WORLD_GEN.getData(load[0], load[1], load[2], Chunk.CHUNK_WIDTH, Chunk.CHUNK_HEIGHT, Chunk.CHUNK_DEPTH));
                chunk.setLazy(true);
            }
            LOADED_CHUNKS.add(chunk);
        }

        if (removedChunk || loadedNewChunk)
        {
            MESH_UPDATER.markUpdate();
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
     * @param TIME
     * @param KEY
     * @param WINDOW
     * @param MOUSE
     * @param GR
     */
    public void update(final float TIME, final KeyInput KEY, final MouseInput MOUSE, final Window WINDOW, final GameRunner GR)
    {
        
        player.updatePlayer(KEY, MOUSE, WINDOW, this, TIME, GR, RESCOURCE_MANAGER);
        cam.snapToPlayer(player);
        
        callLoadUnloadChunks(player.pos);
        
        for (Chunk c : LOADED_CHUNKS)
        {
            if (c.isLazy() == false)
            {
                c.updateChunk(getSurroundingChunks(c.X_ORIGIN, c.Y_ORIGIN, c.Z_ORIGIN), this);
            }
        }
    }

    /**
     * 
     * @param worldX
     * @param worldY
     * @param worldZ
     * @return 
     */
    public byte getBlock(int worldX, int worldY, int worldZ)
    {
        //Loop through all chunks
        for (Chunk c : LOADED_CHUNKS)
        {
            if (c.isWorldCordInChunk(worldX, worldY, worldZ))
            {
                return c.getBlockFromWorldCord(worldX, worldY, worldZ);
            }
        }
        return 0;
    }
    /**
     * 
     * @param worldX
     * @param worldY
     * @param worldZ
     * @param set 
     */
    public void setBlock(int worldX, int worldY, int worldZ, byte set)
    {
        //Loop through all chunks
        for (Chunk c : LOADED_CHUNKS)
        {
            if (c.isWorldCordInChunk(worldX, worldY, worldZ))
            {
                c.setBlockFromWorldCord(worldX, worldY, worldZ, set);
                
                Chunk[] chunks = getSurroundingChunks(c.X_ORIGIN, c.Y_ORIGIN, c.Z_ORIGIN);
                
                c.needsMeshUpdate();
                
                //Test if modification at chunk border
                int chunkX = worldX - c.X_ORIGIN;
                int chunkY = worldY - c.Y_ORIGIN;
                int chunkZ = worldZ - c.Z_ORIGIN;
                boolean borderModified = false;
                
                if (chunkX == 0)
                {
                    borderModified = true;
                }
                if (chunkX == Chunk.CHUNK_WIDTH-1)
                {
                    borderModified = true;
                }
                if (chunkY == 0)
                {
                    borderModified = true;
                }
                if (chunkY == Chunk.CHUNK_HEIGHT-1)
                {
                    borderModified = true;
                }
                if (chunkZ == 0)
                {
                    borderModified = true;
                }
                if (chunkZ == Chunk.CHUNK_DEPTH-1)
                {
                    borderModified = true;
                }
                if (borderModified)
                {
                    for (Chunk d : chunks)
                    {
                        if (d != null)
                        {
                            d.needsMeshUpdate();
                        }
                    }
                }
                MESH_UPDATER.markUpdate();
                return;
            }
        }
    }
    
    

    
    /**
     * 
     */
    public class MeshUpdater extends Thread
    {
        
        private boolean needUpdate;
        /**
         * 
         */
        public MeshUpdater()
        {
            needUpdate = false;
        }
        /**
         * 
         */
        public void markUpdate()
        {
            needUpdate = true;
        }
        
        /**
        * 
        */
        @Override
        public void run()
        {
            while (true)
            {
                while (needUpdate == false)
                {

                }
                needUpdate = false;
                
                //long start = System.nanoTime();

                ArrayList<Mesh>meshes = new ArrayList<>();

                for (int i = 0; i < LOADED_CHUNKS.size(); i++)
                {
                    Chunk c = LOADED_CHUNKS.get(i);
                    if (c.hasGeneratedMesh() == false && c.isLazy() == false)
                    {
                        Chunk[] surroundingChunks = getSurroundingChunks(c.X_ORIGIN, c.Y_ORIGIN, c.Z_ORIGIN);
                        if (canGenerateFromSurroundings(surroundingChunks))
                        {
                            c.REGENERATE_CHUNK_MESH(surroundingChunks, RESCOURCE_MANAGER);
                        }
                        else
                        {
                        }
                    }
                    if (c.isLazy() == false)
                    {
                        meshes.add(c.getMesh());
                    }
                    
                }
                meshes.add(player.mySelction.getMesh());

                Mesh tempmesh = new Mesh();
                tempmesh = tempmesh.addMeshes(meshes);
                tempmesh.resetLighting();
                for (int i = 0; i < LOADED_CHUNKS.size(); i++)
                {
                    Chunk c = LOADED_CHUNKS.get(i);
                    c.addMeshLighting(tempmesh);
                }
                tempmesh.addParralellLightSource(new Vec3D(1f, 5f, 1f), 1f, 1f, 1f);
                
                mesh = tempmesh;
                //mesh.addLightSource(new Vec3D(0, 0, 0) , 1, 1, 1);
                
                /*
                long end = System.nanoTime();
        
                long time = end - start;

                final double TIME_SECONDS= time * Engine.ONEBILLIONTH;

                System.out.println("TIME: " + TIME_SECONDS);
                */
            }
        }
    }
    /**
     * 
     * @param chunks
     * @return 
     */
    public boolean canGenerateFromSurroundings(Chunk[] chunks)
    {
        for (Chunk c : chunks)
        {
            if (c == null) return false;
        }
        return true;
    }
    
    /**
     * 
     * @param x
     * @param y
     * @param z
     * @return 
     */
    public Chunk[] getSurroundingChunks(int x, int y, int z)
    {
        int left = x-Chunk.CHUNK_WIDTH;
        int right = x+Chunk.CHUNK_WIDTH;
        
        int bottom = y-Chunk.CHUNK_HEIGHT;
        int top = y+Chunk.CHUNK_HEIGHT;
        
        int back = z-Chunk.CHUNK_DEPTH;
        int front = z+Chunk.CHUNK_DEPTH;
        
        Chunk[] result = new Chunk[6];
        
        result[0] = getChunk(left, y, z);
        result[1] = getChunk(right, y, z);
        
        result[2] = getChunk(x, bottom, z);
        result[3] = getChunk(x, top, z);
        
        result[4] = getChunk(x, y, back);
        result[5] = getChunk(x, y, front);
        
        return result;
    }
    /**
     * 
     * @param x
     * @param y
     * @param z
     * @return 
     */
    public Chunk getChunk(int x, int y, int z)
    {
        for (int i = 0; i < LOADED_CHUNKS.size(); i++)
        {
            Chunk c = LOADED_CHUNKS.get(i);
            if (c != null && c.X_ORIGIN == x && c.Y_ORIGIN == y && c.Z_ORIGIN == z)
            {
                return c;
            }
        }
        return null;
    }
    
    /*
    Unused Code
    public void addLightSource(Vec3D pos, float r, float g, float b)
    {
        float theta;
        float iota;
        
        final float STEP_SIZE = ((float)Math.PI*2)/8f;
        final float MAX = (float)Math.PI*2;
        
        for (theta = 0; theta < MAX; theta += STEP_SIZE)
        {
            for (iota = 0; iota < MAX; iota += STEP_SIZE)
            {
                Vec3D angle = Vec3D.contructAngleVector((float)Math.sin(theta), (float)Math.sin(iota), (float)Math.cos(theta), (float)Math.cos(iota), 0.05f);
            }
        }
    }
    
    public void castLight(Vec3D pos, Vec3D dir)
    {
        
    }
*/
}
