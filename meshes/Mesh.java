
package engine.meshes;

import engine.environment.Chunk;
import static engine.environment.Chunk.CHUNK_DEPTH;
import static engine.environment.Chunk.CHUNK_HEIGHT;
import static engine.environment.Chunk.CHUNK_WIDTH;
import engine.environment.data.RescourceManager;
import engine.meshes.painters.FloodFill;
import engine.meshes.painters.Painter;
import engine.meshes.painters.Texture;
import engine.meshes.structures.Triangle;
import engine.vectors.Vec3D;
import engine.vectors.Vec2D;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jacob
 */
public class Mesh
{

    /**
     * 
     */
    public final Triangle[] TRIANGLES;
    /**
     * 
     */
    public final Vec3D[] POINTS;
    /**
     * 
     */
    public static final Painter[] TEXTURES;
    /**
     * 
     */
    static
    {
        TEXTURES = new Painter[20];
        
        final RescourceLoader LOADER = new RescourceLoader();
        
        try
        {
            
            TEXTURES[0] = new Texture(LOADER.loadImage("Stone.png"), true);
            TEXTURES[1] = new Texture(LOADER.loadImage("Grass.png"), true);
            TEXTURES[2] = new Texture(LOADER.loadImage("Wood.png"), true);
            TEXTURES[3] = new Texture(LOADER.loadImage("Log.png"), true);
            TEXTURES[4] = new Texture(LOADER.loadImage("Leaves.png"), true);
            TEXTURES[5] = new Texture(LOADER.loadImage("Dirt.png"), true);
            TEXTURES[6] = new Texture(LOADER.loadImage("Sand.png"), true);
            TEXTURES[7] = new Texture(LOADER.loadImage("GrassSide.png"), true);
            TEXTURES[8] = new Texture(LOADER.loadImage("Fire.png"), true);

            TEXTURES[17] = new FloodFill(Color.RED, false);
            TEXTURES[18] = new FloodFill(new Color(255, 0, 0, 200), false);
            TEXTURES[19] = new FloodFill(new Color(255, 255, 255, 100), false);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Mesh.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
     */
    public Mesh()
    {
        this(0, 0);
    }
    /**
     * 
     * @param pointCount
     * @param triCount 
     */
    public Mesh(int pointCount, int triCount)
    {
        //Points
        POINTS = new Vec3D[pointCount];
        //Triangles
        TRIANGLES = new Triangle[triCount];
        
        
    }
    /**
     * 
     */
    final private float LUM_CONST = 5;
    
    /**
     * 
     * @param t
     * @param dir
     * @param lightR
     * @param lightG
     * @param lightB 
     */
    public void parralellLight(Triangle t, Vec3D dir, float lightR, float lightG, float lightB)
    {

        t.redB += (0.5d + -0.5d*dir.dot(t.NORM))*lightR;
        t.greenB += (0.5d + -0.5d*dir.dot(t.NORM))*lightG;
        t.blueB += (0.5d + -0.5d*dir.dot(t.NORM))*lightB;


        if (t.redB < 0)
        {
            t.redB = 0;
        }
        else if (t.redB > 1)
        {
            t.redB = 1;
        }
        if (t.greenB < 0)
        {
            t.greenB = 0;
        }
        else if (t.greenB > 1)
        {
            t.greenB = 1;
        }
        if (t.blueB < 0)
        {
            t.blueB = 0;
        }
        else if (t.blueB > 1)
        {
            t.blueB = 1;
        }
    }
    
    /**
     * 
     * @param t
     * @param pos
     * @param lightR
     * @param lightG
     * @param lightB 
     */
    public void light(Triangle t, Vec3D pos, float lightR, float lightG, float lightB)
    {
        Vec3D mid = getTriMidPoint(t);
        
        Vec3D dist = mid.sub(pos);

        float luminosity =  LUM_CONST / (LUM_CONST+dist.magSquared());
        
        Vec3D dir = mid.diff(pos).norm();

        t.redB += (0.5d + -0.5d*dir.dot(t.NORM))*lightR*luminosity;
        t.greenB += (0.5d + -0.5d*dir.dot(t.NORM))*lightG*luminosity;
        t.blueB += (0.5d + -0.5d*dir.dot(t.NORM))*lightB*luminosity;


        if (t.redB < 0)
        {
            t.redB = 0;
        }
        else if (t.redB > 1)
        {
            t.redB = 1;
        }
        if (t.greenB < 0)
        {
            t.greenB = 0;
        }
        else if (t.greenB > 1)
        {
            t.greenB = 1;
        }
        if (t.blueB < 0)
        {
            t.blueB = 0;
        }
        else if (t.blueB > 1)
        {
            t.blueB = 1;
        }
    }
    /**
     * 
     * @param t
     * @return 
     */
    public Vec3D getTriMidPoint(Triangle t)
    {
        return new Vec3D
        (
            (POINTS[t.POINTS[0]].X+POINTS[t.POINTS[1]].X+POINTS[t.POINTS[2]].X)*THIRD,
            (POINTS[t.POINTS[0]].Y+POINTS[t.POINTS[1]].Y+POINTS[t.POINTS[2]].Y)*THIRD,
            (POINTS[t.POINTS[0]].Z+POINTS[t.POINTS[1]].Z+POINTS[t.POINTS[2]].Z)*THIRD
        );
    }
    /**
     * 
     */
    public void resetLighting()
    {
        for (Triangle t : TRIANGLES)
        {
            if (t != null)
            {
                t.resetLighting();
            }
        }
    }
    /**
     * 
     * @param dir
     * @param lightR
     * @param lightG
     * @param lightB 
     */
    public void addParralellLightSource(Vec3D dir, float lightR, float lightG, float lightB)
    {
        dir = dir.norm(); //Normalise direction
        for (Triangle t : TRIANGLES)
        {
            if (t != null)
            {
                parralellLight(t, dir, lightR, lightG, lightB);
            }
        }
    }
    /**
     * 
     * @param pos
     * @param lightR
     * @param lightG
     * @param lightB 
     */
    public void addLightSource(Vec3D pos, float lightR, float lightG, float lightB)
    {
        for (Triangle t : TRIANGLES)
        {
            if (t != null)
            {
                light(t, pos, lightR, lightG, lightB);
            }
        }
    }
    /**
     * 
     * @return 
     */
    public Mesh createDisplayMesh()
    {
        Mesh ret = new Mesh(this.getPointCount(), this.getTriCount());
        
        for (int i = 0; i < this.getTriCount(); i++)
        {
            if (this.TRIANGLES[i] != null)
            {
                ret.TRIANGLES[i] = this.TRIANGLES[i].deepCopy();
            }
        }
        for (int i = 0; i < this.POINTS.length; i++)
        {
            ret.POINTS[i] = this.POINTS[i].deepCopy();
        }
        
        return ret;
    }
    /**
     * 
     * @param pos
     * @param original 
     */
    public void updateDisplayMesh(Vec3D pos, Mesh original)
    {
        for (int i = 0; i < this.POINTS.length; i++)
        {
            this.POINTS[i].X = original.POINTS[i].X + pos.X;
            this.POINTS[i].Y = original.POINTS[i].Y + pos.Y;
            this.POINTS[i].Z = original.POINTS[i].Z + pos.Z;
        }
    }
    /**
     * 
     * @return 
     */
    public int getTriCount()
    {
        return TRIANGLES.length;
    }
    /**
     * 
     * @return 
     */
    public int getPointCount()
    {
        return POINTS.length;
    }
    /**
     * 
     * @param startIndex
     * @param POINTS
     * @param TEXTUREPOINTS
     * @param textureID
     * @param overrideBackFace
     */
    public void setFace(int startIndex, int[] POINTS, Vec2D[] TEXTUREPOINTS, int textureID, boolean overrideBackFace)
    {
        //Make triangles
        for (int i = 0; i < POINTS.length - 2; i++)
        {
            this.setTri(i+startIndex, new int[]{POINTS[0], POINTS[i+1], POINTS[i+2]}, new Vec2D[]{TEXTUREPOINTS[0], TEXTUREPOINTS[i+1],TEXTUREPOINTS[i+2]}, textureID, overrideBackFace);
        }
    }
    /**
     * 
     */
    public final static float THIRD = 1f/3f;
    /**
     * 
     * @param index
     * @return 
     */
    public float getZMidPoint(int index)
    {
        return (POINTS[TRIANGLES[index].POINTS[0]].Z+POINTS[TRIANGLES[index].POINTS[1]].Z+POINTS[TRIANGLES[index].POINTS[2]].Z)*THIRD;
    }
    /**
     * 
     * @param index
     * @param p
     * @param texPoints
     * @param textureID
     * @param overrideBackFace
     */
    public void setTri(int index, int[]p, Vec2D[] texPoints, int textureID, boolean overrideBackFace)
    {
        Vec3D line1 = new Vec3D(POINTS[p[0]].X - POINTS[p[1]].X, POINTS[p[0]].Y - POINTS[p[1]].Y, POINTS[p[0]].Z - POINTS[p[1]].Z);
        Vec3D line2 = new Vec3D(POINTS[p[1]].X - POINTS[p[2]].X, POINTS[p[1]].Y - POINTS[p[2]].Y, POINTS[p[1]].Z - POINTS[p[2]].Z);
        setTri(index, p, texPoints, textureID, getNormalVector(line1, line2), overrideBackFace);
    }
    /**
     * 
     * @param index
     * @param norm 
     */
    public void updateTriNorm(int index, Vec3D norm)
    {
        Vec3D currentNorm = TRIANGLES[index].NORM;
        
        currentNorm.X = norm.X;
        currentNorm.Y = norm.Y;
        currentNorm.Z = norm.Z;
    }
    /**
     * 
     * @param index
     * @param p
     * @param texPoints
     * @param textureID
     * @param norm
     * @param overrideBackFace
     */
    public void setTri(int index, int[]p, Vec2D[] texPoints, int textureID, Vec3D norm, boolean overrideBackFace)
    {
        TRIANGLES[index] = new Triangle(p, texPoints, textureID, norm, overrideBackFace);
    }

    /**
     * 
     * @param line1
     * @param line2
     * @return 
     */
    public static Vec3D getNormalVector(Vec3D line1, Vec3D line2)
    {
        float x = (line1.Y*line2.Z)-(line1.Z*line2.Y);
        float y = (line1.Z*line2.X)-(line1.X*line2.Z);
        float z = (line1.X*line2.Y)-(line1.Y*line2.X);
        float magnitude = (float)Math.sqrt((x*x)+(y*y)+(z*z));
        return new Vec3D(x/magnitude, y/magnitude, z/magnitude);
    }
    /**
     * 
     * @param pos 
     */
    public void translateMesh(Vec3D pos)
    {
        for (Vec3D p : POINTS) p.translate(pos);
    }
    /**
     * 
     * @param m
     * @return 
     */
    public Mesh addMesh(Mesh m)
    {

        Mesh result = new Mesh(this.getPointCount()+m.getPointCount(), this.getTriCount()+m.getTriCount());
        
        System.arraycopy(this.TRIANGLES, 0, result.TRIANGLES, 0, this.getTriCount());
        for (int i = 0; i < m.TRIANGLES.length; i++) //Used instead of System.arraycopy(m.TRIS, 0, result.TRIS, this.TRIS.length, m.TRIS.length);
        {
            if (m.TRIANGLES[i] != null)
            {
                result.TRIANGLES[this.getTriCount()+i] = m.TRIANGLES[i].deepCopy(this.getPointCount()); //Offset traingle point refrences as second mesh pioints don't start from 0
            }
        }

        //Shallow copy of points
        System.arraycopy(this.POINTS, 0, result.POINTS, 0, this.POINTS.length);
        System.arraycopy(m.POINTS, 0, result.POINTS, this.POINTS.length, m.POINTS.length);


        return result;
    }
    /**
     * 
     * @param meshes
     * @return 
     */
    public Mesh addMeshes(ArrayList<Mesh>meshes)
    {
        meshes.add(0, this);
        
        int pointCount = 0;
        int triCount = 0;
        
        for (Mesh m : meshes)
        {
            pointCount += m.getPointCount();
            triCount += m.getTriCount();
        }
        
        Mesh result = new Mesh(pointCount, triCount);
        
        int currentPointIndex = 0;
        int currentTriIndex = 0;
        for (Mesh m : meshes)
        {
            for (Triangle TRIANGLES1 : m.TRIANGLES) //Used instead of System.arraycopy(m.TRIS, 0, result.TRIS, this.TRIS.length, m.TRIS.length);
            {
                if (TRIANGLES1 != null) {
                    result.TRIANGLES[currentTriIndex] = TRIANGLES1.deepCopy(currentPointIndex); //Offset traingle point refrences as second mesh pioints don't start from 0
                }
                currentTriIndex++;
            }
            
            System.arraycopy(m.POINTS, 0, result.POINTS, currentPointIndex, m.POINTS.length);
            currentPointIndex += m.getPointCount();
        }
        
        
        return result;
    }
    /**
     * 
     * @param x
     * @param y
     * @param z
     * @param data
     * @return 
     */
    private static boolean[] getIncludedFaces(int x, int y, int z, byte[][][] data, byte[][][][] externalData, RescourceManager RESCOURCE_MANAGER)
    {
        int left = x-1;
        int right = x+1;

        int bottom = y-1;
        int top = y+1;

        int back = z-1;
        int front = z+1;

        boolean[]includedFaces = {true, true, true, true, true, true};

        if (left >= 0)
        {
            includedFaces[0] = RESCOURCE_MANAGER.getBlockData(data[left][y][z]).isRenderSolid() == false;
        }
        else
        {
            includedFaces[0] = RESCOURCE_MANAGER.getBlockData(externalData[0][Chunk.CHUNK_WIDTH-1][y][z]).isRenderSolid() == false;

        }
        if (right < CHUNK_WIDTH)
        {
            includedFaces[1] = RESCOURCE_MANAGER.getBlockData(data[right][y][z]).isRenderSolid() == false;
        }
        else
        {
            includedFaces[1] = RESCOURCE_MANAGER.getBlockData(externalData[1][0][y][z]).isRenderSolid() == false;
        }

        if (bottom >= 0)
        {
            includedFaces[2] = RESCOURCE_MANAGER.getBlockData(data[x][bottom][z]).isRenderSolid() == false;
        }
        else
        {
            includedFaces[2] = RESCOURCE_MANAGER.getBlockData(externalData[2][x][Chunk.CHUNK_HEIGHT-1][z]).isRenderSolid() == false;
        }
        if (top < CHUNK_HEIGHT)
        {
            includedFaces[3] = RESCOURCE_MANAGER.getBlockData(data[x][top][z]).isRenderSolid() == false;
        }
        else
        {
            includedFaces[3] = RESCOURCE_MANAGER.getBlockData(externalData[3][x][0][z]).isRenderSolid() == false;
        }

        if (back >= 0)
        {
            includedFaces[4] = RESCOURCE_MANAGER.getBlockData(data[x][y][back]).isRenderSolid() == false;
        }
        else
        {
            includedFaces[4] = RESCOURCE_MANAGER.getBlockData(externalData[4][x][y][Chunk.CHUNK_DEPTH-1]).isRenderSolid() == false;
        }
        if (front < CHUNK_DEPTH)
        {
            includedFaces[5] = RESCOURCE_MANAGER.getBlockData(data[x][y][front]).isRenderSolid() == false;
        }
        else
        {
            includedFaces[5] = RESCOURCE_MANAGER.getBlockData(externalData[5][x][y][0]).isRenderSolid() == false;
        }
        
        return includedFaces;
    }
    /**
     * 
     * @param includedFaces
     * @return 
     */
    private static boolean[][][] getIncludedPoints(int face)
    {
        
        int ix, iy, iz;
        
        boolean[][][] includedPoints = new boolean[2][2][2];
        //Left, Right, Bottom, Top, Back, Front
        if (face == 0)
        {
            ix = 0;
            for (iy = 0; iy < includedPoints[0].length; iy++)
            {
                for (iz = 0; iz < includedPoints[0][0].length; iz++)
                {
                    includedPoints[ix][iy][iz] = true;
                }
            }
            
        }
        if (face == 1)
        {
            ix = 1;
            for (iy = 0; iy < includedPoints[0].length; iy++)
            {
                for (iz = 0; iz < includedPoints[0][0].length; iz++)
                {
                    includedPoints[ix][iy][iz] = true;
                }
            }
        }
        if (face == 2)
        {
            iy = 0;
            for (ix = 0; ix < includedPoints.length; ix++)
            {
                for (iz = 0; iz < includedPoints[0][0].length; iz++)
                {
                    includedPoints[ix][iy][iz] = true;
                }
            }
        }
        if (face == 3)
        {
            iy = 1;
            for (ix = 0; ix < includedPoints.length; ix++)
            {
                for (iz = 0; iz < includedPoints[0][0].length; iz++)
                {
                    includedPoints[ix][iy][iz] = true;
                }
            }
        }
        if (face == 4)
        {
            iz = 0;
            for (ix = 0; ix < includedPoints.length; ix++)
            {
                for (iy = 0; iy < includedPoints[0].length; iy++)
                {
                    includedPoints[ix][iy][iz] = true;
                }
            }
        }
        if (face == 5)
        {
            iz = 1;
            for (ix = 0; ix < includedPoints.length; ix++)
            {
                for (iy = 0; iy < includedPoints[0].length; iy++)
                {
                    includedPoints[ix][iy][iz] = true;
                }
            }
        }
        return includedPoints;
    }
    /**
     * 
     * @param data
     * @param externalData
     * @param X_ORIGIN
     * @param Y_ORIGIN
     * @param Z_ORIGIN
     * @param RESCOURCE_MANAGER
     * @return 
     */
    public static Mesh generateChunkMesh(byte[][][] data, byte[][][][] externalData, final int X_ORIGIN, final int Y_ORIGIN, final int Z_ORIGIN, final RescourceManager RESCOURCE_MANAGER)
    {
        
        int faceCount = 0;
        int pointCount = 0;
        
        int index = 0;
        
        int currentFaceCount = 0;
        
        boolean[][][] INCLUDED_POINTS = new boolean[CHUNK_WIDTH+1][CHUNK_HEIGHT+1][CHUNK_DEPTH+1];
        int[][][] POINT_MAPPING_INDEX = new int[CHUNK_WIDTH+1][CHUNK_HEIGHT+1][CHUNK_DEPTH+1];
        
        boolean[][][][] INCLUDED_FACE = new boolean[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_DEPTH][6];
        
        boolean[][][][] WRITTEN_FACE = new boolean[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_DEPTH][6];
        
        for (int ix = 0; ix < CHUNK_WIDTH; ix++)
        {
            for (int iy = 0; iy < CHUNK_HEIGHT; iy++)
            {
                for (int iz = 0; iz < CHUNK_DEPTH; iz++)
                {
                    if (RESCOURCE_MANAGER.getBlockData(data[ix][iy][iz]).isRenderSolid())
                    {
                        INCLUDED_FACE[ix][iy][iz] = getIncludedFaces(ix, iy, iz, data, externalData, RESCOURCE_MANAGER);
                    }
                }
            }
        }
        
        for (int face = 0; face < 6; face++)
        {
            for (int ix = 0; ix < CHUNK_WIDTH; ix++)
            {
                for (int iy = 0; iy < CHUNK_HEIGHT; iy++)
                {
                    for (int iz = 0; iz < CHUNK_DEPTH; iz++)
                    {
                        if (INCLUDED_FACE[ix][iy][iz][face] == true && WRITTEN_FACE[ix][iy][iz][face] == false)
                        {
                            byte currentTile = data[ix][iy][iz];
                            
                            int sizea = 0;
                            int sizeb = 0;
                            
                            boolean canInflate = true;
                            
                            //Try inflate face size
                            do
                            {
                                sizea++;
                                for (int ib = 0; ib <= sizeb; ib++)
                                {
                                    int tempx = ix;
                                    int tempy = iy;
                                    int tempz = iz;
                                    
                                    if (face == 0 || face == 1)
                                    {
                                        tempy += sizea;
                                        tempz += ib;
                                    }
                                    if (face == 2 || face == 3)
                                    {
                                        tempx += sizea;
                                        tempz += ib;
                                        
                                    }
                                    if (face == 4 || face == 5)
                                    {
                                        tempx += sizea;
                                        tempy += ib;
                                    }
                                    if (tempx >= Chunk.CHUNK_WIDTH || tempy >= Chunk.CHUNK_HEIGHT || tempz >= Chunk.CHUNK_DEPTH)
                                    {
                                        canInflate = false;
                                    }
                                    else if (INCLUDED_FACE[tempx][tempy][tempz][face] == false || WRITTEN_FACE[tempx][tempy][tempz][face] == true || currentTile != data[tempx][tempy][tempz])
                                    {
                                        canInflate = false;
                                    }
                                }
                            }
                            while (canInflate);
                            sizea--;
                            
                            canInflate = true;
                            do
                            {
                                sizeb++;
                                for (int ia = 0; ia <= sizea; ia++)
                                {
                                    int tempx = ix;
                                    int tempy = iy;
                                    int tempz = iz;
                                    
                                    if (face == 0 || face == 1)
                                    {
                                        tempy += ia;
                                        tempz += sizeb;
                                    }
                                    if (face == 2 || face == 3)
                                    {
                                        tempx += ia;
                                        tempz += sizeb;
                                        
                                    }
                                    if (face == 4 || face == 5)
                                    {
                                        tempx += ia;
                                        tempy += sizeb;
                                    }
                                    if (tempx >= Chunk.CHUNK_WIDTH || tempy >= Chunk.CHUNK_HEIGHT || tempz >= Chunk.CHUNK_DEPTH)
                                    {
                                        canInflate = false;
                                    }
                                    else if (INCLUDED_FACE[tempx][tempy][tempz][face] == false || WRITTEN_FACE[tempx][tempy][tempz][face] == true || currentTile != data[tempx][tempy][tempz])
                                    {
                                        canInflate = false;
                                    }
                                }
                            }
                            while (canInflate);
                            sizea++;
                            
                            
                            //Write included faces
                            for (int ia = 0; ia < sizea; ia++)
                            {
                                for (int ib = 0; ib < sizeb; ib++)
                                {
                                    int tempx = ix;
                                    int tempy = iy;
                                    int tempz = iz;
                                    if (face == 0 || face == 1)
                                    {
                                        tempy += ia;
                                        tempz += ib;
                                    }
                                    if (face == 2 || face == 3)
                                    {
                                        tempx += ia;
                                        tempz += ib;
                                        
                                    }
                                    if (face == 4 || face == 5)
                                    {
                                        tempx += ia;
                                        tempy += ib;
                                    }
                                    
                                    WRITTEN_FACE[tempx][tempy][tempz][face] = true;
                                }
                            }
                            
                            faceCount += 2;
                            
                            int nextx = 1;
                            int nexty = 1;
                            int nextz = 1;
                            if (face == 0 || face == 1)
                            {
                                nexty = sizea;
                                nextz = sizeb;
                            }
                            if (face == 2 || face == 3)
                            {
                                nextx = sizea;
                                nextz = sizeb;

                            }
                            if (face == 4 || face == 5)
                            {
                                nextx = sizea;
                                nexty = sizeb;
                            }
                            
                            boolean[][][] includedPoints = getIncludedPoints(face);

                            for (int jx = 0; jx < includedPoints.length; jx++)
                            {
                                for (int jy = 0; jy < includedPoints[0].length; jy++)
                                {
                                    for (int jz = 0; jz < includedPoints[0][0].length; jz++)
                                    {
                                        int indexX = ix + (jx*nextx);
                                        int indexY = iy + (jy*nexty);
                                        int indexZ = iz + (jz*nextz);

                                        INCLUDED_POINTS[indexX][indexY][indexZ] = INCLUDED_POINTS[indexX][indexY][indexZ] || includedPoints[jx][jy][jz]; 
                                    }
                                }
                            }
                            
                            
                        }
                    }
                }
            }
        }
        //Find points
        for (int ix = 0; ix <= CHUNK_WIDTH; ix++)
        {
            for (int iy = 0; iy <= CHUNK_HEIGHT; iy++)
            {
                for (int iz = 0; iz <= CHUNK_DEPTH; iz++)
                {
                    pointCount += INCLUDED_POINTS[ix][iy][iz] ? 1 : 0;
                }
            }
        }
        
        

        
        //Create Mesh
        //Mesh result = new Mesh((CHUNK_WIDTH+1)*(CHUNK_HEIGHT+1)*(CHUNK_DEPTH+1), faceCount);
        Mesh result = new Mesh(pointCount, faceCount);
        
        //Fill points
        
        
        for (int ix = 0; ix <= CHUNK_WIDTH; ix++)
        {
            for (int iy = 0; iy <= CHUNK_HEIGHT; iy++)
            {
                for (int iz = 0; iz <= CHUNK_DEPTH; iz++)
                {
                    if (INCLUDED_POINTS[ix][iy][iz])
                    { //If this point is included
                        POINT_MAPPING_INDEX[ix][iy][iz] = index; //Add to lookup table for easy access later
                        result.POINTS[index] = new Vec3D(ix + X_ORIGIN -0.5f, iy + Y_ORIGIN -0.5f, iz + Z_ORIGIN -0.5f); //Add point
                        index++; //Next point index
                    }
                }
            }
        }

        
        
        //Reset face
        for (int face = 0; face < 6; face++)
        {
            for (int ix = 0; ix < CHUNK_WIDTH; ix++)
            {
                for (int iy = 0; iy < CHUNK_HEIGHT; iy++)
                {
                    for (int iz = 0; iz < CHUNK_DEPTH; iz++)
                    { //For all voxels
                        WRITTEN_FACE[ix][iy][iz][face] = false; //Set false
                    }
                }
            }
        }
        
        //Add faces
        
        for (int face = 0; face < 6; face++)
        {
            //Use an included face array for easy call of cube function
            //Include one face
            boolean[] includedFaces = new boolean[6];
            
            includedFaces[face] = true;
            //Generates {false, .... true... , false, false} with true at index face
            
            for (int ix = 0; ix < CHUNK_WIDTH; ix++)
            {
                for (int iy = 0; iy < CHUNK_HEIGHT; iy++)
                {
                    for (int iz = 0; iz < CHUNK_DEPTH; iz++)
                    {
                        if (INCLUDED_FACE[ix][iy][iz][face] == true && WRITTEN_FACE[ix][iy][iz][face] == false)
                        {
                            byte currentTile = data[ix][iy][iz];
                            
                            int sizea = 0;
                            int sizeb = 0;
                            
                            boolean canInflate = true;
                            
                            //Try inflate face size
                            do
                            {
                                sizea++;
                                for (int ib = 0; ib <= sizeb; ib++)
                                {
                                    int tempx = ix;
                                    int tempy = iy;
                                    int tempz = iz;
                                    
                                    if (face == 0 || face == 1)
                                    {
                                        tempy += sizea;
                                        tempz += ib;
                                    }
                                    if (face == 2 || face == 3)
                                    {
                                        tempx += sizea;
                                        tempz += ib;
                                        
                                    }
                                    if (face == 4 || face == 5)
                                    {
                                        tempx += sizea;
                                        tempy += ib;
                                    }
                                    if (tempx >= Chunk.CHUNK_WIDTH || tempy >= Chunk.CHUNK_HEIGHT || tempz >= Chunk.CHUNK_DEPTH)
                                    {
                                        canInflate = false;
                                    }
                                    else if (INCLUDED_FACE[tempx][tempy][tempz][face] == false || WRITTEN_FACE[tempx][tempy][tempz][face] == true || currentTile != data[tempx][tempy][tempz])
                                    {
                                        canInflate = false;
                                    }
                                }
                            }
                            while (canInflate);
                            sizea--;
                            
                            canInflate = true;
                            do
                            {
                                sizeb++;
                                for (int ia = 0; ia <= sizea; ia++)
                                {
                                    int tempx = ix;
                                    int tempy = iy;
                                    int tempz = iz;
                                    
                                    if (face == 0 || face == 1)
                                    {
                                        tempy += ia;
                                        tempz += sizeb;
                                    }
                                    if (face == 2 || face == 3)
                                    {
                                        tempx += ia;
                                        tempz += sizeb;
                                        
                                    }
                                    if (face == 4 || face == 5)
                                    {
                                        tempx += ia;
                                        tempy += sizeb;
                                    }
                                    if (tempx >= Chunk.CHUNK_WIDTH || tempy >= Chunk.CHUNK_HEIGHT || tempz >= Chunk.CHUNK_DEPTH)
                                    {
                                        canInflate = false;
                                    }
                                    else if (INCLUDED_FACE[tempx][tempy][tempz][face] == false || WRITTEN_FACE[tempx][tempy][tempz][face] == true || currentTile != data[tempx][tempy][tempz])
                                    {
                                        canInflate = false;
                                    }
                                }
                            }
                            while (canInflate);
                            sizea++;
                            
                            //Write included faces
                            for (int ia = 0; ia < sizea; ia++)
                            {
                                for (int ib = 0; ib < sizeb; ib++)
                                {
                                    int tempx = ix;
                                    int tempy = iy;
                                    int tempz = iz;
                                    if (face == 0 || face == 1)
                                    {
                                        tempy += ia;
                                        tempz += ib;
                                    }
                                    if (face == 2 || face == 3)
                                    {
                                        tempx += ia;
                                        tempz += ib;
                                        
                                    }
                                    if (face == 4 || face == 5)
                                    {
                                        tempx += ia;
                                        tempy += ib;
                                    }
                                    WRITTEN_FACE[tempx][tempy][tempz][face] = true;
                                }
                            }
                            
                            int nextx = 1;
                            int nexty = 1;
                            int nextz = 1;
                            
                            int textScalea = 1;
                            int textScaleb = 1;
                            
                            if (face == 0 || face == 1)
                            {
                                nexty = sizea;
                                nextz = sizeb;
                                
                                textScalea = sizeb;
                                textScaleb = sizea;
                            }
                            if (face == 2 || face == 3)
                            {
                                nextx = sizea;
                                nextz = sizeb;
                                
                                textScalea = sizea;
                                textScaleb = sizeb;

                            }
                            if (face == 4 || face == 5)
                            {
                                nextx = sizea;
                                nexty = sizeb;
                                
                                textScalea = sizea;
                                textScaleb = sizeb;
                            }

                            int[] pointIndicies = new int[8];

                            pointIndicies[0] = POINT_MAPPING_INDEX[ix][iy][iz]; //000   
                            pointIndicies[1] = POINT_MAPPING_INDEX[ix+nextx][iy][iz]; //001
                            pointIndicies[2] = POINT_MAPPING_INDEX[ix][iy+nexty][iz]; //010
                            pointIndicies[3] = POINT_MAPPING_INDEX[ix+nextx][iy+nexty][iz]; //011

                            pointIndicies[4] = POINT_MAPPING_INDEX[ix][iy][iz+nextz]; //100
                            pointIndicies[5] = POINT_MAPPING_INDEX[ix+nextx][iy][iz+nextz];
                            pointIndicies[6] = POINT_MAPPING_INDEX[ix][iy+nexty][iz+nextz];
                            pointIndicies[7] = POINT_MAPPING_INDEX[ix+nextx][iy+nexty][iz+nextz];
                            
                            
                            
                            Vec2D[] textureCoordinates;
                            /*
                            if (face == 1 || face == 4)
                            {
                            textureCoordinates = new Vec2D[]{new Vec2D(0, textScaleb*Texture.SCALE_RECIPRICOL),new Vec2D(textScalea*Texture.SCALE_RECIPRICOL, textScaleb*Texture.SCALE_RECIPRICOL),new Vec2D(textScalea*Texture.SCALE_RECIPRICOL, 0),new Vec2D(0, 0)};
                            }
                            else
                            {
                            textureCoordinates = new Vec2D[]{new Vec2D(0, 0),new Vec2D(textScalea*Texture.SCALE_RECIPRICOL, 0),new Vec2D(textScalea*Texture.SCALE_RECIPRICOL, textScaleb*Texture.SCALE_RECIPRICOL),new Vec2D(0, textScaleb*Texture.SCALE_RECIPRICOL)};
                            }
                             */
                            
                            
                            float originX, originY;
                            switch (face)
                            {
                                case 0:
                                    originX = iz*Texture.SCALE_RECIPRICOL;
                                    originY = iy*Texture.SCALE_RECIPRICOL;
                                    textureCoordinates = new Vec2D[]{new Vec2D(originX, originY),new Vec2D(originX+textScalea*Texture.SCALE_RECIPRICOL, originY),new Vec2D(originX+textScalea*Texture.SCALE_RECIPRICOL, originY+textScaleb*Texture.SCALE_RECIPRICOL),new Vec2D(originX, originY+textScaleb*Texture.SCALE_RECIPRICOL)};
                                    break;
                                case 1:
                                    originX = iz*Texture.SCALE_RECIPRICOL;
                                    originY = iy*Texture.SCALE_RECIPRICOL;
                                    textureCoordinates = new Vec2D[]{new Vec2D(originX, originY+textScaleb*Texture.SCALE_RECIPRICOL),new Vec2D(originX+textScalea*Texture.SCALE_RECIPRICOL, originY+textScaleb*Texture.SCALE_RECIPRICOL),new Vec2D(originX+textScalea*Texture.SCALE_RECIPRICOL, originY),new Vec2D(originX, originY)};
                                    break;
                                case 2:
                                    originX = ix*Texture.SCALE_RECIPRICOL;
                                    originY = iz*Texture.SCALE_RECIPRICOL;
                                    textureCoordinates = new Vec2D[]{new Vec2D(originX, originY),new Vec2D(originX+textScalea*Texture.SCALE_RECIPRICOL, originY),new Vec2D(originX+textScalea*Texture.SCALE_RECIPRICOL, originY+textScaleb*Texture.SCALE_RECIPRICOL),new Vec2D(originX, originY+textScaleb*Texture.SCALE_RECIPRICOL)};
                                    break;
                                case 3:
                                    originX = ix*Texture.SCALE_RECIPRICOL;
                                    originY = iz*Texture.SCALE_RECIPRICOL;
                                    textureCoordinates = new Vec2D[]{new Vec2D(originX, originY),new Vec2D(originX+textScalea*Texture.SCALE_RECIPRICOL, originY),new Vec2D(originX+textScalea*Texture.SCALE_RECIPRICOL, originY+textScaleb*Texture.SCALE_RECIPRICOL),new Vec2D(originX, originY+textScaleb*Texture.SCALE_RECIPRICOL)};
                                    break;
                                case 4:
                                    originX = ix*Texture.SCALE_RECIPRICOL;
                                    originY = iy*Texture.SCALE_RECIPRICOL;
                                    textureCoordinates = new Vec2D[]{new Vec2D(originX, originY+textScaleb*Texture.SCALE_RECIPRICOL),new Vec2D(originX+textScalea*Texture.SCALE_RECIPRICOL, originY+textScaleb*Texture.SCALE_RECIPRICOL),new Vec2D(originX+textScalea*Texture.SCALE_RECIPRICOL, originY),new Vec2D(originX, originY)};
                                    break;
                                default:
                                    originX = ix*Texture.SCALE_RECIPRICOL;
                                    originY = iy*Texture.SCALE_RECIPRICOL;
                                    textureCoordinates = new Vec2D[]{new Vec2D(originX, originY),new Vec2D(originX+textScalea*Texture.SCALE_RECIPRICOL, originY),new Vec2D(originX+textScalea*Texture.SCALE_RECIPRICOL, originY+textScaleb*Texture.SCALE_RECIPRICOL),new Vec2D(originX, originY+textScaleb*Texture.SCALE_RECIPRICOL)};
                                    break;
                            }
                            currentFaceCount = Mesh.addCube(
                                    result, 
                                    currentFaceCount, 
                                    pointIndicies, 
                                    RESCOURCE_MANAGER.getBlockData(data[ix][iy][iz]).getTexture(face), 
                                    includedFaces, 
                                    textureCoordinates
                            );
                            
                        }
                    }
                }
            }
        }
        
        return result;
    }
    /**
     * 
     * @param POINTS
     * @param TEXTUREPOINTS
     * @param textureID
     * @return 
     */
    public static Mesh generateFace(Vec3D[] POINTS, Vec2D[] TEXTUREPOINTS, int textureID) //Redo this code
    {
        Mesh mesh = new Mesh(POINTS.length, POINTS.length - 2);
        //Copy Point Array
        System.arraycopy(POINTS, 0, mesh.POINTS, 0, POINTS.length);
        //Make triangles
        for (int i = 0; i < mesh.getTriCount(); i++)
        {
            mesh.setTri(i, new int[]{0, i+1, i+2}, new Vec2D[]{TEXTUREPOINTS[0], TEXTUREPOINTS[i+1],TEXTUREPOINTS[i+2]}, textureID, false);
        }
        return mesh;
    }
    /**
     * 
     * @param x
     * @param y
     * @param z
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param textureID
     * @return 
     */
    public static Mesh generateCube(float x, float y, float z, float sizeX, float sizeY, float sizeZ, int textureID)
    {
        return generateCube(x, y, z, sizeX, sizeY, sizeZ, textureID, new boolean[]{true, true, true, true, true, true});
    }
    /**
     * 
     * @param mesh
     * @param currentFaceCount
     * @param pointIndicies
     * @param textureID
     * @param includeFace
     * @param texturePoints
     * @return 
     */
    public static int addCube(Mesh mesh, int currentFaceCount, int[] pointIndicies, int textureID, boolean[] includeFace, Vec2D[]texturePoints)
    {
        //Left, Right, Bottom, Top, Back, Front
        //Face Left
        if (includeFace[0])
        {
            mesh.setFace
            (
                    currentFaceCount, 
                    new int[]
                    {
                        pointIndicies[0],//new Vec3D(MINX, MINY, MINZ),
                        pointIndicies[4],//new Vec3D(MINX, MINY, MAXZ), 
                        pointIndicies[6],//new Vec3D(MINX, MAXY, MAXZ),
                        pointIndicies[2],//new Vec3D(MINX, MAXY, MINZ),
                    }, 
                    texturePoints, //new Vec2D[]{new Vec2D(0, 0),new Vec2D(1, 0),new Vec2D(1, 1),new Vec2D(0, 1)},
                    textureID, false
            );
            currentFaceCount += 2;
        }
        //Face Right
        if (includeFace[1])
        {
            mesh.setFace
            (
                    currentFaceCount, 
                    new int[]
                    {
                        pointIndicies[3],//new Vec3D(MAXX, MAXY, MINZ),
                        pointIndicies[7],//new Vec3D(MAXX, MAXY, MAXZ), 
                        pointIndicies[5],//new Vec3D(MAXX, MINY, MAXZ), 
                        pointIndicies[1],//new Vec3D(MAXX, MINY, MINZ),
                    }, 
                    texturePoints,
                    textureID, false
            );
            currentFaceCount += 2;
        }
        //Face Bottom
        if (includeFace[2])
        {
            mesh.setFace
            (
                    currentFaceCount, 
                    new int[]
                    {
                        pointIndicies[0],//new Vec3D(MINX, MINY, MINZ), 
                        pointIndicies[1],//new Vec3D(MAXX, MINY, MINZ),
                        pointIndicies[5],//new Vec3D(MAXX, MINY, MAXZ),
                        pointIndicies[4],//new Vec3D(MINX, MINY, MAXZ),
                    }, 
                    texturePoints,
                    textureID, false
            );
            currentFaceCount += 2;
        }
        //Face Top
        if (includeFace[3])
        {
            mesh.setFace
            (
                    currentFaceCount, 
                    new int[]
                    {
                        pointIndicies[6],//new Vec3D(MINX, MAXY, MAXZ),
                        pointIndicies[7],//new Vec3D(MAXX, MAXY, MAXZ), 
                        pointIndicies[3],//new Vec3D(MAXX, MAXY, MINZ), 
                        pointIndicies[2],//new Vec3D(MINX, MAXY, MINZ),
                    }, 
                    texturePoints,
                    textureID, false
            );
            currentFaceCount += 2;
        }
        //Face Back
        if (includeFace[4])
        {
            mesh.setFace
            (
                    currentFaceCount, 
                    new int[]
                    {
                        pointIndicies[2],//new Vec3D(MINX, MAXY, MINZ),
                        pointIndicies[3], //new Vec3D(MAXX, MAXY, MINZ),
                        pointIndicies[1], //new Vec3D(MAXX, MINY, MINZ), 
                        pointIndicies[0], //new Vec3D(MINX, MINY, MINZ), 
                    }, 
                    texturePoints,
                    textureID, false
            );
            currentFaceCount += 2;
        }
        //Face Front
        if (includeFace[5])
        {
            mesh.setFace
            (
                    currentFaceCount, 
                    new int[]
                    {
                        pointIndicies[4],//new Vec3D(MINX, MINY, MAXZ), 
                        pointIndicies[5],//new Vec3D(MAXX, MINY, MAXZ), 
                        pointIndicies[7],//new Vec3D(MAXX, MAXY, MAXZ),
                        pointIndicies[6],//new Vec3D(MINX, MAXY, MAXZ),
                    }, 
                    texturePoints,
                    textureID, false
            );
            currentFaceCount += 2;
        }

        return currentFaceCount;
    }
    
    /**
     * 
     * @param x
     * @param y
     * @param z
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param textureID
     * @param includeFace
     * @return 
     */    
    public static Mesh generateCube(float x, float y, float z, float sizeX, float sizeY, float sizeZ, int textureID, boolean[] includeFace)
    {
        
        int totalFaceCount = 0;
        int currentFaceCount = 0;
        //Left, Right, Bottom, Top, Back, Front
        for (boolean b : includeFace)
        {
            if (b)
            {
                totalFaceCount += 2;
            }
        }
        
        
        Mesh mesh = new Mesh(8, totalFaceCount);
        final float RADIUSX = sizeX/2;
        final float RADIUSY = sizeY/2;
        final float RADIUSZ = sizeZ/2;

        final float MINX = x - RADIUSX;
        final float MAXX = x + RADIUSX;
        final float MINY = y - RADIUSY;
        final float MAXY = y + RADIUSY;
        final float MINZ = z - RADIUSZ;
        final float MAXZ = z + RADIUSZ;
        
        mesh.POINTS[0] = new Vec3D(MINX, MINY, MINZ);
        mesh.POINTS[1] = new Vec3D(MAXX, MINY, MINZ);
        mesh.POINTS[2] = new Vec3D(MINX, MAXY, MINZ);
        mesh.POINTS[3] = new Vec3D(MAXX, MAXY, MINZ);
        mesh.POINTS[4] = new Vec3D(MINX, MINY, MAXZ);
        mesh.POINTS[5] = new Vec3D(MAXX, MINY, MAXZ);
        mesh.POINTS[6] = new Vec3D(MINX, MAXY, MAXZ);
        mesh.POINTS[7] = new Vec3D(MAXX, MAXY, MAXZ);
        
        addCube(mesh, currentFaceCount, new int[]{0,1,2,3,4,5,6,7}, textureID, includeFace, new Vec2D[]{new Vec2D(0, 0),new Vec2D(1, 0),new Vec2D(1, 1),new Vec2D(0, 1)});
        
        return mesh;
    }
    
    /**
     * 
     * @param x
     * @param y
     * @param z
     * @param radius
     * @param textureID
     * @param hasLighting
     * @return 
     */
    public static Mesh generateTetrahedron(float x, float y, float z, float radius, int textureID, boolean hasLighting)
    {
        Mesh mesh = new Mesh(4, 4);
       
        Vec3D center = new Vec3D(x,y,z);
        
        final float HALFROOTTHREE = 0.5f*(float)Math.sqrt(3);
        final float QUATERROOTTHREE = 0.5f*HALFROOTTHREE;
        final float THREEQUATERS = 0.75f;

        
        mesh.POINTS[0] = new Vec3D(0, 1, 0).scale(radius).add(center);
        mesh.POINTS[1] = new Vec3D(HALFROOTTHREE, -0.5f, 0).scale(radius).add(center);
        mesh.POINTS[2] = new Vec3D(-QUATERROOTTHREE, -0.5f, THREEQUATERS).scale(radius).add(center);
        mesh.POINTS[3] = new Vec3D(-QUATERROOTTHREE, -0.5f, -THREEQUATERS).scale(radius).add(center);
        
        mesh.setFace
        (
                0, 
                new int[]
                {
                    2,//new Vec3D(MINX, MAXY, MINZ),
                    1, //new Vec3D(MAXX, MAXY, MINZ),
                    0, //new Vec3D(MAXX, MINY, MINZ), 
                }, 
                new Vec2D[]{new Vec2D(0, 0),new Vec2D(1, 0),new Vec2D(1, 1)},
                textureID, false
        );
        mesh.setFace
        (
                1, 
                new int[]
                {
                    0,//new Vec3D(MINX, MINY, MINZ), 
                    1,//new Vec3D(MAXX, MINY, MINZ),
                    3,//new Vec3D(MAXX, MINY, MAXZ),
                }, 
                new Vec2D[]{new Vec2D(0, 0),new Vec2D(1, 0),new Vec2D(1, 1)},
                textureID, false
        );
        mesh.setFace
        (
                2, 
                new int[]
                {
                    3,//new Vec3D(MINX, MINY, MINZ),
                    2,//new Vec3D(MINX, MINY, MAXZ), 
                    0,//new Vec3D(MINX, MAXY, MAXZ),
                }, 
                new Vec2D[]{new Vec2D(0, 0),new Vec2D(1, 0),new Vec2D(1, 1)},
                textureID, false
        );
        mesh.setFace
        (
                3, 
                new int[]
                {
                    1,//new Vec3D(MINX, MINY, MAXZ), 
                    2,//new Vec3D(MAXX, MINY, MAXZ), 
                    3,//new Vec3D(MAXX, MAXY, MAXZ),
                }, 
                new Vec2D[]{new Vec2D(0, 0),new Vec2D(1, 0),new Vec2D(1, 1)},
                textureID, false
        );
        return mesh;
    }
    
    /**
     * 
     * @param n
     * @param x
     * @param y
     * @param z
     * @param RADIUS
     * @param variation
     * @param textureID
     * @param hasLighting
     * @return 
     */
    public static Mesh generateCubicSphere(int n, float x, float y, float z, float RADIUS, float variation, int[] textureID, boolean hasLighting)
    { 
        Mesh mesh = new Mesh(6*(n+1)*(n+1), 6*2*n*n); //Normally faces would be 3x bigger (replace 2 with 6)
        
        //n = 1 means a cube
        final float[] X = new float[n+1];
        final float[] Y = new float[n+1];
        final float[] Z = new float[n+1];
        
        final float MINX = x - RADIUS;
        final float MINY = y - RADIUS;
        final float MINZ = z - RADIUS;
        
        final float STEP = (2*RADIUS)/n;
        
        for (int i = 0; i < X.length; i++)
        {
            X[i] = MINX + STEP*i;
        }
        for (int i = 0; i < X.length; i++)
        {
            Y[i] = MINY + STEP*i;
        }
        for (int i = 0; i < X.length; i++)
        {
            Z[i] = MINZ + STEP*i;
        }
        int index = 0;
        final int WHOLEFACE = X.length*Y.length;
        for (int ix = 0; ix < X.length; ix++)
        {
            for (int iy = 0; iy < Y.length; iy++)
            {
                mesh.POINTS[index] = new Vec3D(X[ix], Y[iy], Z[0]);
                index++;
            }
        }
        for (int ix = 0; ix < X.length; ix++)
        {
            for (int iy = 0; iy < Y.length; iy++)
            {
                mesh.POINTS[index] = new Vec3D(X[ix], Y[iy], Z[Z.length-1]);
                index++;
            }
        }
        for (int ix = 0; ix < X.length; ix++)
        {
            for (int iz = 0; iz < Z.length; iz++)
            {
                mesh.POINTS[index] = new Vec3D(X[ix], Y[Y.length-1], Z[iz]);
                index++;
            }
        }
        for (int ix = 0; ix < X.length; ix++)
        {
            for (int iz = 0; iz < Z.length; iz++)
            {
                mesh.POINTS[index] = new Vec3D(X[ix], Y[0], Z[iz]);
                index++;
            }
        }
        for (int iy = 0; iy < Y.length; iy++)
        {
            for (int iz = 0; iz < Z.length; iz++)
            {
                mesh.POINTS[index] = new Vec3D(X[0], Y[iy], Z[iz]);
                index++;
            }
        }
        for (int iy = 0; iy < Y.length; iy++)
        {
            for (int iz = 0; iz < Z.length; iz++)
            {
                mesh.POINTS[index] = new Vec3D(X[X.length-1], Y[iy], Z[iz]);
                index++;
            }
        }
        for (Vec3D POINTS1 : mesh.POINTS)
        {
            ArrayList<Vec3D> samePoints = new ArrayList();
            for (Vec3D POINTS2 : mesh.POINTS)
            {
                if (Math.abs(POINTS2.X - POINTS1.X) < 0.0001 && Math.abs(POINTS2.Y - POINTS1.Y) < 0.0001 && Math.abs(POINTS2.Z - POINTS1.Z) < 0.0001)
                {
                    samePoints.add(POINTS2);
                }
            }
            float px = POINTS1.X - x;
            float py = POINTS1.Y - y;
            float pz = POINTS1.Z - z;
            float mag = (float)Math.sqrt((px*px)+(py*py)+(pz*pz));
            float recip = RADIUS/(mag+(variation*((0.5f*(float)Math.random())-0.5f)));
            px *= recip;
            py *= recip;
            pz *= recip;
            //POINTS1.X = px+x;
            //POINTS1.Y = py+y;
            //POINTS1.Z = pz+z;
            
            for (Vec3D POINTS2 : samePoints)
            {
                POINTS2.X = px+x;
                POINTS2.Y = py+y;
                POINTS2.Z = pz+z;
            }
        }
        int triCount = 0;
        
        final float XSTEP = 1f/X.length;
        final float YSTEP = 1f/Y.length;
        
        for (int i = 0; i < 3; i++)
        {
            for (int ix = 0; ix < X.length-1; ix++)
            {
                for (int iy = 0; iy < Y.length-1; iy++)
                {
                    int minPoint1 = (WHOLEFACE*i*2)+(ix*Y.length)+iy;
                    
                    
                    float xStart1 = (ix)*XSTEP;
                    float yStart1 = iy*YSTEP;
                    float xEnd1 = (ix+1)*XSTEP;
                    float yEnd1 = (iy+1)*YSTEP;
                    
                    
                    float xStart2 = 1-xStart1;
                    float yStart2 = 1-yStart1;
                    float xEnd2 = 1-xEnd1;
                    float yEnd2 = 1-yEnd1;

                    mesh.setFace
                    (
                            triCount, 
                            new int[]
                            {
                                minPoint1+1,
                                minPoint1+Y.length+1,
                                minPoint1+Y.length,
                                minPoint1,
                            }, 
                            new Vec2D[]{new Vec2D(xStart2, yEnd2),new Vec2D(xEnd2, yEnd2),new Vec2D(xEnd2, yStart2),new Vec2D(xStart2, yStart2)},
                            textureID[i*2], false
                    );
                    triCount += 2; //2 triangle for square face
                    int minPoint2 = (WHOLEFACE*((i*2)+1))+(ix*Y.length)+iy;

                    mesh.setFace
                    (
                            triCount, 
                            new int[]
                            {
                                minPoint2,
                                minPoint2+Y.length,
                                minPoint2+Y.length+1, 
                                minPoint2+1,
                            }, 
                            new Vec2D[]{new Vec2D(xStart2, yStart2),new Vec2D(xEnd2, yStart2),new Vec2D(xEnd2, yEnd2),new Vec2D(xStart2, yEnd2)},
                            textureID[1+(i*2)], false
                    );
                    triCount += 2; //2 triangle for square face
                }
            }
        }
        return mesh;
    }

}
