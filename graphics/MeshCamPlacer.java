
package engine.graphics;

import engine.environment.Camera;
import engine.meshes.Mesh;
import engine.vectors.Vec2D;
import engine.vectors.Vec3D;

/**
 *
 * @author jacob
 */
public class MeshCamPlacer
{
    /**
     * 
     */
    private final Vec3D ZPLANEPOINT = new Vec3D(0, 0, 0.1f);
    /**
     * 
     */
    private final Vec3D ZPLANENORMAL = new Vec3D(0, 0, 1f);
    /**
     * 
     */
    private final Mesh CULLEDTRIS;
    /**
     * 
     */
    private final int TRISUSED;
    /**
     * 
     * @param m
     * @param cam 
     */
    public MeshCamPlacer(Mesh m, Camera cam)
    {
        CULLEDTRIS = new Mesh((int)Math.ceil(m.getPointCount()*1.4d), m.getTriCount()*2);
        int i;
        int nextPoint = 0;
        int nextTri = 0;
        
        float x, y, z;
        
        //Pre compute matrix (not using a class to increase performance)
        float matxx = cam.cosy;
        float matxz = cam.siny;
        
        float matyx = cam.sinx*cam.siny;
        float matyy = cam.cosx;
        float matyz = -cam.sinx*cam.cosy;
        
        float matzx = cam.cosx*cam.cosy;
        float matzy = cam.sinx;
        float matzz = -cam.cosx*cam.siny;
        
        
        Vec3D insidePoints[] = new Vec3D[3];
        int insideRefrences[] = new int[3];
        Vec3D outsidePoints[] = new Vec3D[3];
        Vec2D insideTex[] = new Vec2D[3];
        Vec2D outsideTex[] = new Vec2D[3];
        
        Vec3D norm = new Vec3D();
        
        float camX = cam.pos.X;
        float camY = cam.pos.Y;
        float camZ = cam.pos.Z;

        for (i = 0; i < CULLEDTRIS.POINTS.length; i++)
        {
            CULLEDTRIS.POINTS[i] = new Vec3D(0, 0, 0);
        }
        for (i = 0; i < m.POINTS.length; i++)
        {
            x = m.POINTS[i].X - camX;
            y = m.POINTS[i].Y - camY;
            z = m.POINTS[i].Z - camZ;

            CULLEDTRIS.POINTS[nextPoint].X = (matxx*x) + (matxz*z);
            CULLEDTRIS.POINTS[nextPoint].Y = (matyx*x) + (matyy*y) + (matyz*z);
            CULLEDTRIS.POINTS[nextPoint].Z = (matzx*z) + (matzy*y) + (matzz*x);
            nextPoint++;
        }
        /*

        */
        for (i = 0; i < m.getTriCount(); i++)
        {
            
            norm.setHere(
                    (matxx*m.TRIANGLES[i].NORM.X) + (matxz*m.TRIANGLES[i].NORM.Z), 
                    (matyx*m.TRIANGLES[i].NORM.X) + (matyy*m.TRIANGLES[i].NORM.Y) + (matyz*m.TRIANGLES[i].NORM.Z), 
                    (matzx*m.TRIANGLES[i].NORM.Z) + (matzy*m.TRIANGLES[i].NORM.Y) + (matzz*m.TRIANGLES[i].NORM.X)
            );
            /*

            */
            int[] currentTriPointRefrences = 
            {
                m.TRIANGLES[i].POINTS[0],
                m.TRIANGLES[i].POINTS[1],
                m.TRIANGLES[i].POINTS[2]
            };
            /*

            */
            Vec3D[] currentTriPoints = 
            {
                CULLEDTRIS.POINTS[currentTriPointRefrences[0]],
                CULLEDTRIS.POINTS[currentTriPointRefrences[1]],
                CULLEDTRIS.POINTS[currentTriPointRefrences[2]]
            };

            Vec2D[] currentTriTexture =
            {
                m.TRIANGLES[i].TEXPOINTS[0],
                m.TRIANGLES[i].TEXPOINTS[1],
                m.TRIANGLES[i].TEXPOINTS[2]
            };


            if (currentTriPoints[0].dot(norm) < 0 || m.TRIANGLES[i].OVER_RIDE_BACKFACE_CULLING) //Is the triangle facing the right way (OR DOUBLESIDED = true)
            { //If the triangle WASN'T backface culled

                //CLIP AGAINST CAMERA PLANE TIME
                Vec3D planeN = ZPLANENORMAL.norm();
                
                int insidePointCount = 0;
                int insideTexCount = 0;
                
                int outsidePointCount = 0;
                int outsideTexCount = 0;

                float d0 = dist(currentTriPoints[0], ZPLANENORMAL, ZPLANEPOINT);
                float d1 = dist(currentTriPoints[1], ZPLANENORMAL, ZPLANEPOINT);
                float d2 = dist(currentTriPoints[2], ZPLANENORMAL, ZPLANEPOINT);
                
                float t;

                if (d0 >= 0)
                {
                    insidePoints[insidePointCount] = currentTriPoints[0];
                    insideRefrences[insidePointCount] = currentTriPointRefrences[0];
                    insidePointCount++;
                    
                    insideTex[insideTexCount] = currentTriTexture[0];
                    insideTexCount++;
                }
                else
                {
                    outsidePoints[outsidePointCount] = currentTriPoints[0];
                    outsidePointCount++;
                    
                    outsideTex[outsideTexCount] = currentTriTexture[0];
                    outsideTexCount++;
                }
                if (d1 >= 0)
                {
                    insidePoints[insidePointCount] = currentTriPoints[1];
                    insideRefrences[insidePointCount] = currentTriPointRefrences[1];
                    insidePointCount++;
                    
                    insideTex[insideTexCount] = currentTriTexture[1];
                    insideTexCount++;
                }
                else
                {
                    outsidePoints[outsidePointCount] = currentTriPoints[1];
                    outsidePointCount++;
                    
                    outsideTex[outsideTexCount] = currentTriTexture[1];
                    outsideTexCount++;
                }
                if (d2 >= 0)
                {
                    insidePoints[insidePointCount] = currentTriPoints[2];
                    insideRefrences[insidePointCount] = currentTriPointRefrences[2];
                    insidePointCount++;
                    
                    insideTex[insideTexCount] = currentTriTexture[2];
                    insideTexCount++;
                }
                else
                {
                    outsidePoints[outsidePointCount] = currentTriPoints[2];
                    outsidePointCount++;
                    
                    outsideTex[outsideTexCount] = currentTriTexture[2];
                    outsideTexCount++;
                }
                //Make correctly clipped triangles
                if (insidePointCount == 0)
                {
                }
                else if (insidePointCount == 3)
                {
                    CULLEDTRIS.setTri(nextTri, currentTriPointRefrences, m.TRIANGLES[i].TEXPOINTS, m.TRIANGLES[i].TEXTUREID, m.TRIANGLES[i].NORM, m.TRIANGLES[i].OVER_RIDE_BACKFACE_CULLING);
                    CULLEDTRIS.TRIANGLES[nextTri].redB = m.TRIANGLES[i].redB;
                    CULLEDTRIS.TRIANGLES[nextTri].greenB = m.TRIANGLES[i].greenB;
                    CULLEDTRIS.TRIANGLES[nextTri].blueB = m.TRIANGLES[i].blueB;
                    nextTri++;
                }
                else if (insidePointCount == 1 && outsidePointCount == 2)
                {
                    Vec2D[] outTexPoints = new Vec2D[3];
                    
                    outTexPoints[0] = insideTex[0].deepCopy();

                    CULLEDTRIS.POINTS[nextPoint] = intersectPlane(ZPLANEPOINT, planeN, insidePoints[0], outsidePoints[0]);
                    t = getTIntersectPlane(ZPLANEPOINT, planeN, insidePoints[0], outsidePoints[0]);
                    
                    outTexPoints[1] = (outsideTex[0].sub(insideTex[0])).scale(t).add(insideTex[0]);
                    
                    int p1 = nextPoint;
                    nextPoint++;
                    
                    CULLEDTRIS.POINTS[nextPoint] = intersectPlane(ZPLANEPOINT, planeN, insidePoints[0], outsidePoints[1]);
                    t = getTIntersectPlane(ZPLANEPOINT, planeN, insidePoints[0], outsidePoints[1]);
                    
                    outTexPoints[2] = (outsideTex[1].sub(insideTex[0])).scale(t).add(insideTex[0]);
                    
                    int p2 = nextPoint;
                    nextPoint++;
                    //Use outTexPoints
                    //Or m.TRIANGLES[i].TEXPOINTS
                    CULLEDTRIS.setTri(nextTri, new int[]{insideRefrences[0], p1, p2}, outTexPoints, m.TRIANGLES[i].TEXTUREID, m.TRIANGLES[i].NORM, m.TRIANGLES[i].OVER_RIDE_BACKFACE_CULLING);
                    CULLEDTRIS.TRIANGLES[nextTri].redB = m.TRIANGLES[i].redB;
                    CULLEDTRIS.TRIANGLES[nextTri].greenB = m.TRIANGLES[i].greenB;
                    CULLEDTRIS.TRIANGLES[nextTri].blueB = m.TRIANGLES[i].blueB;
                    nextTri++;
                }
                else if (insidePointCount == 2 && outsidePointCount == 1)
                {
                    Vec2D[] out1TexPoints = new Vec2D[3];
                    Vec2D[] out2TexPoints = new Vec2D[3];
                    
                    out1TexPoints[0] = insideTex[0].deepCopy();
                    out1TexPoints[1] = insideTex[1].deepCopy();
                    
                    CULLEDTRIS.POINTS[nextPoint] = intersectPlane(ZPLANEPOINT, planeN, insidePoints[0], outsidePoints[0]);
                    t = getTIntersectPlane(ZPLANEPOINT, planeN, insidePoints[0], outsidePoints[0]);
                    
                    out1TexPoints[2] = (outsideTex[0].sub(insideTex[0])).scale(t).add(insideTex[0]);
                    
                    int p1 = nextPoint;
                    nextPoint++;

                    out2TexPoints[0] = insideTex[1].deepCopy();
                    out2TexPoints[1] = out1TexPoints[2].deepCopy();
                    
                    CULLEDTRIS.POINTS[nextPoint] = intersectPlane(ZPLANEPOINT, planeN, insidePoints[1], outsidePoints[0]);
                    t = getTIntersectPlane(ZPLANEPOINT, planeN, insidePoints[1], outsidePoints[0]);
                    
                    out2TexPoints[2] = (outsideTex[0].sub(insideTex[1])).scale(t).add(insideTex[1]);

                    int p2 = nextPoint;
                    nextPoint++;
                    //Use  m.TRIANGLES[i].TEXPOINTS
                    //Or out1TexPoints
                    CULLEDTRIS.setTri(nextTri, new int[]{insideRefrences[0], insideRefrences[1], p1}, out1TexPoints, m.TRIANGLES[i].TEXTUREID, m.TRIANGLES[i].NORM, m.TRIANGLES[i].OVER_RIDE_BACKFACE_CULLING);
                    CULLEDTRIS.TRIANGLES[nextTri].redB = m.TRIANGLES[i].redB;
                    CULLEDTRIS.TRIANGLES[nextTri].greenB = m.TRIANGLES[i].greenB;
                    CULLEDTRIS.TRIANGLES[nextTri].blueB = m.TRIANGLES[i].blueB;
                    nextTri++;
                    //Use  m.TRIANGLES[i].TEXPOINTS
                    //Or out2TexPoints
                    CULLEDTRIS.setTri(nextTri, new int[]{insideRefrences[1], p1, p2}, out2TexPoints, m.TRIANGLES[i].TEXTUREID, m.TRIANGLES[i].NORM, m.TRIANGLES[i].OVER_RIDE_BACKFACE_CULLING);
                    CULLEDTRIS.TRIANGLES[nextTri].redB = m.TRIANGLES[i].redB;
                    CULLEDTRIS.TRIANGLES[nextTri].greenB = m.TRIANGLES[i].greenB;
                    CULLEDTRIS.TRIANGLES[nextTri].blueB = m.TRIANGLES[i].blueB;
                    nextTri++;
                }
            }
        }
        this.TRISUSED = nextTri;
    }
    
    
    
    /**
     * 
     * @param planeP
     * @param planeN
     * @param lineStart
     * @param lineEnd
     * @return 
     */
    private static Vec3D intersectPlane(Vec3D planeP, Vec3D planeN, Vec3D lineStart, Vec3D lineEnd)
    {
        //planeN = planeN.norm();
        float dot = planeN.dot(planeP);
        float ad = planeN.dot(lineStart);
        float bd = planeN.dot(lineEnd);
        float t = (dot-ad)/(bd-ad);
        Vec3D lineStartToEnd = new Vec3D(lineEnd.X-lineStart.X, lineEnd.Y-lineStart.Y, lineEnd.Z-lineStart.Z);
        Vec3D lineToIntersect = new Vec3D(lineStartToEnd.X*t, lineStartToEnd.Y*t, lineStartToEnd.Z*t);
        return new Vec3D(lineStart.X+lineToIntersect.X, lineStart.Y+lineToIntersect.Y, lineStart.Z+lineToIntersect.Z);
    }
    /**
     * 
     * @param planeP
     * @param planeN
     * @param lineStart
     * @param lineEnd
     * @return 
     */
    private static float getTIntersectPlane(Vec3D planeP, Vec3D planeN, Vec3D lineStart, Vec3D lineEnd)
    {
        //planeN = planeN.norm();
        float dot = planeN.dot(planeP);
        float ad = planeN.dot(lineStart);
        float bd = planeN.dot(lineEnd);
        float t = (dot-ad)/(bd-ad);
        return t;
    }
    /**
     * 
     * @param p
     * @param planeN
     * @param planeP
     * @return 
     */
    private static float dist(Vec3D p, Vec3D planeN, Vec3D planeP)
    {
        //float mag = (float)Math.sqrt((planeN.X*planeN.X)+(planeN.Y*planeN.Y)+(planeN.Z*planeN.Z));
        //planeN = new Vec3D(planeN.X/mag, planeN.Y/mag, planeN.Z/mag);
        
        return planeN.dot(p)-planeN.dot(planeP);
    }
    /**
     * 
     * @return 
     */
    public Mesh getPlacedMesh()
    {
        return CULLEDTRIS;
    }
    /**
     * 
     * @return 
     */
    public int getTrisUsed()
    {
        return TRISUSED;
    }
    
    
    /* NOT USED ANYMORE... MIGHT NEED TO USE FOR TRANSPARENCEY
    public static void quickSortMesh(Mesh m, int begin, int end)
    {
        if (begin < end)
        {
            int partitionIndex = partition(m, begin, end);

            quickSortMesh(m, begin, partitionIndex-1);
            quickSortMesh(m, partitionIndex+1, end);
        }
    }
    private static int partition(Mesh m, int begin, int end)
    {
        float pivot = m.getZMidPoint(end);
        int i = (begin-1);

        for (int j = begin; j < end; j++) {
            if (m.getZMidPoint(j) >= pivot)
            {
                i++;
                Triangle swapTri = m.TRIANGLES[i];
                m.TRIANGLES[i] = m.TRIANGLES[j];
                m.TRIANGLES[j] = swapTri;
            }
        }
        Triangle swapTri = m.TRIANGLES[i+1];
        m.TRIANGLES[i+1] = m.TRIANGLES[end];
        m.TRIANGLES[end] = swapTri;
        return i+1;
    }
    */
}
