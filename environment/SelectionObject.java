/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.environment;

import engine.meshes.Mesh;
import engine.vectors.Vec2D;
import engine.vectors.Vec3D;

/**
 *
 * @author jacob
 */
public class SelectionObject
{
    /**
     * 
     */
    private final Mesh PRESERVEDMESH;
    /**
     * 
     */
    private final Mesh DISPLAYMESH;
    /**
     * 
     */
    public Vec3D pos;
    /**
     * 
     */
    public int face;
    
    
    private byte selectedBlock = (byte)1;
    
    
    final int MIN_ID = 1;
    final int MAX_ID = 7;
    /**
     * 
     * @param m
     * @param face 
     */
    private void updatePoints(Mesh m, int face)
    {
        final float RADIUSX = 1.0625f/2f;
        final float RADIUSY = 1.0625f/2f;
        final float RADIUSZ = 1.0625f/2f;

        final float MINX = -RADIUSX;
        final float MAXX = RADIUSX;
        final float MINY = -RADIUSY;
        final float MAXY = RADIUSY;
        final float MINZ = -RADIUSZ;
        final float MAXZ = RADIUSZ;
        
        Vec3D[] points = new Vec3D[8];
        
        points[0] = new Vec3D(MINX, MINY, MINZ);
        points[1] = new Vec3D(MAXX, MINY, MINZ);
        points[2] = new Vec3D(MINX, MAXY, MINZ);
        points[3] = new Vec3D(MAXX, MAXY, MINZ);
        points[4] = new Vec3D(MINX, MINY, MAXZ);
        points[5] = new Vec3D(MAXX, MINY, MAXZ);
        points[6] = new Vec3D(MINX, MAXY, MAXZ);
        points[7] = new Vec3D(MAXX, MAXY, MAXZ);
        
        int[]pointMapping = new int[4];
        
        //System.out.println("Face: " + face);
        
        switch (face)
        {
            case 0:
                pointMapping[0] = 0;
                pointMapping[1] = 4;
                pointMapping[2] = 6;
                pointMapping[3] = 2;
                break;
            case 1:
                pointMapping[0] = 3;
                pointMapping[1] = 7;
                pointMapping[2] = 5;
                pointMapping[3] = 1;
                break;
            case 2:
                pointMapping[0] = 0;
                pointMapping[1] = 1;
                pointMapping[2] = 5;
                pointMapping[3] = 4;
                break;
            case 3:
                pointMapping[0] = 6;
                pointMapping[1] = 7;
                pointMapping[2] = 3;
                pointMapping[3] = 2;
                break;
            case 4:
                pointMapping[0] = 2;
                pointMapping[1] = 3;
                pointMapping[2] = 1;
                pointMapping[3] = 0;
                break;
            case 5:
                pointMapping[0] = 4;
                pointMapping[1] = 5;
                pointMapping[2] = 7;
                pointMapping[3] = 6;
                break;
            default:
                break;
        }
        
        for (int i = 0; i < 4; i++)
        {
            m.POINTS[i].X = points[pointMapping[i]].X;
            m.POINTS[i].Y = points[pointMapping[i]].Y;
            m.POINTS[i].Z = points[pointMapping[i]].Z;
        }
        /*
        for (int i = 0; i < 2; i++)
        {
            Vec3D line1 = new Vec3D(m.POINTS[m.TRIANGLES[i].POINTS[0]].X - m.POINTS[m.TRIANGLES[i].POINTS[1]].X, m.POINTS[m.TRIANGLES[i].POINTS[0]].Y - m.POINTS[m.TRIANGLES[i].POINTS[1]].Y, m.POINTS[m.TRIANGLES[i].POINTS[0]].Z - m.POINTS[m.TRIANGLES[i].POINTS[1]].Z);
            Vec3D line2 = new Vec3D(m.POINTS[m.TRIANGLES[i].POINTS[1]].X - m.POINTS[m.TRIANGLES[i].POINTS[2]].X, m.POINTS[m.TRIANGLES[i].POINTS[1]].Y - m.POINTS[m.TRIANGLES[i].POINTS[2]].Y, m.POINTS[m.TRIANGLES[i].POINTS[1]].Z - m.POINTS[m.TRIANGLES[i].POINTS[2]].Z);
            m.updateTriNorm(i, Mesh.getNormalVector(line1, line2));
        }
        */
        m.setFace(0, new int[]{0,1,2,3}, new Vec2D[]{new Vec2D(0, 0),new Vec2D(1, 0),new Vec2D(1, 1),new Vec2D(0, 1)},19, true);
    }
        
    
    /**
     * 
     */
    public SelectionObject()
    {
        pos = new Vec3D(0, 0, 0);
        
        PRESERVEDMESH = new Mesh(4, 2);
        
        for (int i = 0; i < 4; i++)
        {
            PRESERVEDMESH.POINTS[i] = new Vec3D();
        }
        
        updatePoints(PRESERVEDMESH, 0);
        
        
        //PRESERVEDMESH.setFace(0, new int[]{0,1,2,3}, new Vec2D[]{new Vec2D(0, 0),new Vec2D(1, 0),new Vec2D(1, 1),new Vec2D(0, 1)},19);
                //Mesh.generateCube(0, 0, 0, 1.0625f, 1.0625f, 1.0625f, 19, new boolean[]{true, false, false, false, false, false});
        
        //PRESERVEDMESH = Mesh.generateCube(0, 0, 0, 1.0625f, 1.0625f, 1.0625f, 19);
        DISPLAYMESH = PRESERVEDMESH.createDisplayMesh();
        
        
        
        UPDATE_SELECTION(0, 0, 0, 0);
    }
    
    /**
     * 
     * @return 
     */
    public byte getSelectedBlockID()
    {
        return selectedBlock;
    }
    /**
     * 
     * @return 
     */
    public byte nextBlock()
    {
        return change((byte)1);
    }
    /**
     * 
     * @return 
     */
    public byte prevBlock()
    {
        return change((byte)-1);
    }
    /**
     * 
     * @param change
     * @return 
     */
    private byte change(byte change)
    {
        final int CHANGE = MAX_ID + 1 - MIN_ID;
        
        byte temp = selectedBlock;
        
        temp += change;
        
        while (temp > MAX_ID)
        {
            temp -= CHANGE;
        }
        while (temp < MIN_ID)
        {
            temp = CHANGE;
        }
        
        return temp;
    }
    /**
     * 
     * @return 
     */
    public byte getMineBlockID()
    {
        return 0;
    }
    /**
     * 
     * @param change 
     */
    public void changeSelection(int change)
    {
        selectedBlock = change((byte)change);
    }
    
    /**
     * 
     * @param x
     * @param y
     * @param z 
     * @param face 
     */
    public final void UPDATE_SELECTION(int x, int y, int z, int face)
    {
        pos.X = x;
        pos.Y = y;
        pos.Z = z;
        
        this.face = face;
        /*
        boolean[]includedFaces = new boolean[6];
        
        for (int i = 0; i < 6; i++)
        {
            includedFaces[i] = i == face;
            if (i == face)
            {
                System.out.println("Face: " + i);
            }
        }
        */
        /*
        Mesh selectFace = Mesh.generateCube(0, 0, 0, 1.0625f, 1.0625f, 1.0625f, 19, includedFaces);
        
        for (int i = 0; i < selectFace.TRIANGLES.length; i++)
        {
            PRESERVEDMESH.TRIANGLES[i].POINTS[0] = selectFace.TRIANGLES[i].POINTS[0];
            PRESERVEDMESH.TRIANGLES[i].POINTS[1] = selectFace.TRIANGLES[i].POINTS[1];
            PRESERVEDMESH.TRIANGLES[i].POINTS[2] = selectFace.TRIANGLES[i].POINTS[2];
        }
        */
        
        updatePoints(PRESERVEDMESH, this.face);
        
        
        DISPLAYMESH.updateDisplayMesh(pos, PRESERVEDMESH);

        
        //Mesh.addCube(DISPLAYMESH, 0, new int[]{0,1,2,3,4,5,6,7}, 19, includedFaces);
        /*
        Mesh selectFace = Mesh.generateCube(0, 0, 0, 1.0625f, 1.0625f, 1.0625f, 19, includedFaces);
        
        for (int i = 0; i < selectFace.TRIANGLES.length; i++)
        {
            DISPLAYMESH.TRIANGLES[i].POINTS[0] = selectFace.TRIANGLES[i].POINTS[0];
            DISPLAYMESH.TRIANGLES[i].POINTS[1] = selectFace.TRIANGLES[i].POINTS[1];
            DISPLAYMESH.TRIANGLES[i].POINTS[2] = selectFace.TRIANGLES[i].POINTS[2];
        }
        */
    }
    /**
     * 
     * @return 
     */
    public Mesh getMesh()
    {
        return DISPLAYMESH;
    }
}
