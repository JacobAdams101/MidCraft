
package engine.environment;

import java.util.Random;

/**
 *
 * @author jacob
 */
public class WorldGenerator
{
    /**
     * 
     */
    final double SEED_DOUBLE;
    
    final long SEED_LONG;
    /**
     * 
     */
    private final long DEFAULT_SIZE;
    
    private final int SCALE;
    /**
     * 
     */
    private int[] p;
    /**
     * 
     */
    private int[] permutation;
    
    private float[][][]rand;

    /**
     * 
     * @param seed
     */
    public WorldGenerator(long seed)
    {
        
        this.SEED_LONG = seed;
        
        this.SEED_DOUBLE = Double.longBitsToDouble(SEED_LONG);
        
        this.DEFAULT_SIZE = 256;
        
        this.SCALE = 40;

        // Initialize the permutation array.
        this.p = new int[512];
        this.permutation = new int[] { 151, 160, 137, 91, 90, 15, 131, 13, 201,
                        95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99,
                        37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26,
                        197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88,
                        237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74,
                        165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111,
                        229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40,
                        244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76,
                        132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159,
                        86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250,
                        124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207,
                        206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170,
                        213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155,
                        167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113,
                        224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242,
                        193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235,
                        249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184,
                        84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236,
                        205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66,
                        215, 61, 156, 180 };

        // Populate it
        for (int i = 0; i < 256; i++)
        {
                p[256 + i] = p[i] = permutation[i];
        }
        
        rand = new float [64][64][64];
        
        Random generator = new Random(this.SEED_LONG);
        
        
        
        for (int ix = 0; ix < rand.length; ix++)
        {
            for (int iy = 0; iy < rand[0].length; iy++)
            {
                for (int iz = 0; iz < rand[0][0].length; iz++)
                {
                    rand[ix][iy][iz] = generator.nextFloat();
                }
            }
        }
    }
    
    /**
     * 
     */
    private final static float FIVE_THOUSANTHS = 5f/1000f;
    /**
     * 
     * @param x
     * @param z
     * @return 
     */
    private boolean isTree(int x, int z)
    {
        return rand[realMod(x, rand.length)][0][realMod(z, rand[0].length)] < FIVE_THOUSANTHS;
    }
    
    private int treeHeight(int x, int z)
    {
        return (int)(rand[realMod(x, rand.length)][0][realMod(z, rand[0].length)]*2)+4;
    }
    /**
     * 
     * @param x
     * @param z
     * @return 
     */
    private boolean isLeaf(int x, int y, int z, float density)
    {
        return rand[realMod(x, rand.length)][realMod(y, rand[0].length)][realMod(z, rand[0][0].length)] < density;
    }
    /**
     * 
     * @param x
     * @param mod
     * @return 
     */
    private int realMod(int x, int mod)
    {
        return ((x % mod) + mod) % mod;
    }
    
    /**
     * 
     * @param X_ORIGIN
     * @param Y_ORIGIN
     * @param Z_ORIGIN
     * @param WIDTH
     * @param HEIGHT
     * @param DEPTH
     * @return 
     */
    public byte[][][] getData(final int X_ORIGIN, final int Y_ORIGIN, final int Z_ORIGIN, final int WIDTH, final int HEIGHT, final int DEPTH)
    {
        final byte[][][] RESULT = new byte[WIDTH][HEIGHT][DEPTH];
        
        for (int ix = 0; ix < WIDTH; ix++)
        {
            for (int iy = 0; iy < HEIGHT; iy++)
            {
                for (int iz = 0; iz < DEPTH; iz++)
                {
                    int worldX = ix+X_ORIGIN;
                    int worldY = iy+Y_ORIGIN;
                    int worldZ = iz+Z_ORIGIN;
                    
                    final double CAVE_DENSITY_AT_SURFACE = 0.9;
                    final double MAX_DENSITY = 0.4;
                    final double ROLL_OFF_RATE = 0.1;
                    
                    final double CAVE_DENSITY = Math.max(CAVE_DENSITY_AT_SURFACE/(1d+Math.pow(Math.abs(worldY), ROLL_OFF_RATE)), MAX_DENSITY);
                    
                    
                    int heightNoise = (int)noise(worldX, worldZ);
                    double caveNoise = noise(worldX, worldY, worldZ);
                    
                    int biome = getBiome(worldX, worldZ);
                    
                    boolean generatedTree = false;
                    
                    
                    if (biome == 1 && isTree(worldX, worldZ) && heightNoise > worldY && heightNoise - treeHeight(worldX, worldZ) < worldY)
                    {
                        RESULT[ix][iy][iz] = 4;
                        generatedTree = true;
                    }
                    for (int jx = -2; jx <= 2; jx++)
                    {
                        for (int jz = -2; jz <= 2; jz++)
                        {
                            if (isTree(worldX+jx, worldZ+jz) && getBiome(worldX+jx, worldZ+jz) == 1)
                            {
                                int currentHeightNoise = (int)noise(worldX+jx, worldZ+jz);
                                int treeHeight = treeHeight(worldX+jx, worldZ+jz);
                                
                                float density = 0;
                                boolean generateLeaf = false;
                                
                                if (currentHeightNoise + 1 - treeHeight == worldY || currentHeightNoise - treeHeight - 3 == worldY)
                                {
                                    density = 0.4f;
                                    generateLeaf = true;
                                }
                                else if (currentHeightNoise + 1 - treeHeight > worldY && currentHeightNoise - treeHeight - 3 < worldY)
                                {
                                    density = 0.8f;
                                    generateLeaf = true;
                                }
                                if (generateLeaf)
                                {
                                    if (isLeaf(worldX, worldY, worldZ, density))
                                    {
                                        RESULT[ix][iy][iz] = 5;
                                        generatedTree = true;
                                    }
                                }
                            }
                        }
                    }
                    
                    
                    if (generatedTree == false)
                    {
                        if (caveNoise > CAVE_DENSITY)
                        {
                            //System.out.println("Cave");
                            //Cave
                            RESULT[ix][iy][iz] = 0;
                        }
                        else if (heightNoise > worldY)
                        {
                            //Air
                            RESULT[ix][iy][iz] = 0;
                        }
                        else if (heightNoise == worldY)
                        {
                            
                            if (biome == 1)
                            {
                                //Grass
                                RESULT[ix][iy][iz] = 2;
                            }
                            else
                            {
                                //Sand
                                RESULT[ix][iy][iz] = 7;
                            }
                        }
                        else if (heightNoise > worldY - 3)
                        {
                            if (biome == 1)
                            {
                                //Dirt
                                RESULT[ix][iy][iz] = 6;
                            }
                            else
                            {
                                //Sand
                                RESULT[ix][iy][iz] = 7;
                            }
                        }
                        else if (heightNoise < worldY)
                        {
                            //Stone
                            RESULT[ix][iy][iz] = 1;
                        }

                    }
                    
                }
            }
        }
        
        return RESULT;
    }
    /**
     * 
     * @param x
     * @param y
     * @return 
     */
    public int getBiome(double x, double y)
    {
        double noise = noise(x, y, x+y);
        if (noise > 0)
        {
            return 1;
        }
        else
        {
            return 2;
        }
    }
    
    
    /**
     * 
     * @param x
     * @param y
     * @param z
     * @return 
     */
    public double noise(double x, double y, double z)
    {
        double value = 0.0;
        double size = DEFAULT_SIZE;
        double initialSize = size;

        while (size >= 1)
        {
                value += smoothNoise((x / size), (y / size), (z / size)) * size;
                size /= 2.0;
        }

        return value / initialSize;
    }
    /**
     * 
     * @param x
     * @param y
     * @return 
     */
    public double noise(double x, double y)
    {
        double value = 0.0;
        double size = DEFAULT_SIZE;
        double initialSize = size;

        while (size >= 1)
        {
                value += smoothNoise((x / size), (y / size), (0f / size)) * size;
                size /= 2.0;
        }

        return (SCALE*value) / initialSize;
    }

    /**
     * 
     * @param x
     * @param y
     * @param z
     * @return 
     */
    public double smoothNoise(double x, double y, double z)
    {
        // Offset each coordinate by the seed value
        x += this.SEED_DOUBLE+0.5;
        y += this.SEED_DOUBLE+0.5;
        x += this.SEED_DOUBLE+0.5;

        int X = (int) Math.floor(x) & 255; // FIND UNIT CUBE THAT
        int Y = (int) Math.floor(y) & 255; // CONTAINS POINT.
        int Z = (int) Math.floor(z) & 255;

        x -= Math.floor(x); // FIND RELATIVE X,Y,Z
        y -= Math.floor(y); // OF POINT IN CUBE.
        z -= Math.floor(z);

        double u = fade(x); // COMPUTE FADE CURVES
        double v = fade(y); // FOR EACH OF X,Y,Z.
        double w = fade(z);

        int A = p[X] + Y;
        int AA = p[A] + Z;
        int AB = p[A + 1] + Z; // HASH COORDINATES OF
        int B = p[X + 1] + Y;
        int BA = p[B] + Z;
        int BB = p[B + 1] + Z; // THE 8 CUBE CORNERS,

        return lerp(w, lerp(v, lerp(u, grad(p[AA], 		x, 		y, 		z		), 	// AND ADD
                                                                        grad(p[BA],		x - 1, 	y, 		z		)), // BLENDED
                                                        lerp(u, grad(p[AB], 	x, 		y - 1, 	z		), 	// RESULTS
                                                                        grad(p[BB], 	x - 1, 	y - 1, 	z		))),// FROM 8
                                        lerp(v, lerp(u, grad(p[AA + 1], x, 		y, 		z - 1	), 	// CORNERS
                                                                        grad(p[BA + 1], x - 1, 	y, 		z - 1	)), // OF CUBE
                                                        lerp(u, grad(p[AB + 1], x, 		y - 1,	z - 1	),
                                                                        grad(p[BB + 1], x - 1, 	y - 1, 	z - 1	))));
    }
    /**
     * 
     * @param t
     * @return 
     */
    private double fade(double t)
    {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }
    /**
     * 
     * @param t
     * @param a
     * @param b
     * @return 
     */
    private double lerp(double t, double a, double b)
    {
        return a + t * (b - a);
    }
    /**
     * 
     * @param hash
     * @param x
     * @param y
     * @param z
     * @return 
     */
    private double grad(int hash, double x, double y, double z)
    {
        int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
        double u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
        v = h < 4 ? y : h == 12 || h == 14 ? x : z;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}
