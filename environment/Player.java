
package engine.environment;

import engine.environment.data.RescourceManager;
import engine.game.GameRunner;
import engine.graphics.Window;
import engine.userinput.KeyInput;
import engine.userinput.MouseInput;
import engine.vectors.Vec3D;

/**
 *
 * @author jacob
 */
public class Player
{
    /**
     * 
     */
    public Vec3D pos;
    /**
     * 
     */    
    public float yRot;
    /**
     * 
     */
    public float xRot;
    /**
     * 
     */
    public Vec3D vel;
    /**
     * 
     */
    public float cosy;
    /**
     * 
     */
    public float siny;
    /**
     * 
     */
    public float cosx;
    /**
     * 
     */
    public float sinx;
    
    public final static float BLOCK_UPDATE_COOLDOWN = 0.3f;
    
    public SelectionObject mySelction;
    /**
     * 
     * @param p 
     */
    public Player(Vec3D p)
    {
        this.pos = p;
        this.vel = new Vec3D(0, 0, 0);
        this.yRot = 0;
        this.xRot = 0;
        
        mySelction = new SelectionObject();
        
        CALC();
    }
    /**
     * 
     * @param x
     * @param y
     * @param z 
     */
    public Player(float x, float y, float z)
    {
        this(new Vec3D(x, y, z));
    }
    /**
     * 
     * @param x
     * @param y
     * @param z 
     */
    public void setPosition(float x, float y, float z)
    {
        this.pos.X = x;
        this.pos.Y = y;
        this.pos.Z = z;
    }

    
    /**
     * 
     */
    public final void CALC()
    {
        cosy = (float)Math.cos(yRot);
        siny = (float)Math.sin(yRot);

        cosx = (float)Math.cos(xRot);
        sinx = (float)Math.sin(xRot);
    }
    
    private boolean canJump = false;
    
    /**
     * 
     * @param KEY
     * @param MOUSE
     * @param WINDOW
     * @param WORLD
     * @param time
     * @param GR
     * @param RESCOURCE_MANAGER
     */
    public void updatePlayer(final KeyInput KEY, final MouseInput MOUSE, final Window WINDOW, final World WORLD, float time, final GameRunner GR, final RescourceManager RESCOURCE_MANAGER)
    {

        float shiftX, shiftY;
        
        final float SPEED = 2.5f;
        
        final float GRAVITATIONAL_CONSTANT = 20f;
        
        final float JUMP_VELOCITY = 10f;

        float shiftSpeed;
        /*
        SPRINT CODE
        */
        if (KEY.isPressed(KeyInput.InputKey.SHIFT))
        {
            shiftSpeed = 1.8f;
        }
        else
        {
            shiftSpeed = 1;
        }
        
        /*
        UPDATE TRANSLATIONAL POSITION
        */
        Vec3D delta = new Vec3D(0, 0, 0);

        if (KEY.isPressed(KeyInput.InputKey.UP))
        {
            delta = delta.add(new Vec3D(-(this.siny * SPEED * shiftSpeed), 0, this.cosy * SPEED * shiftSpeed));
        }
        if (KEY.isPressed(KeyInput.InputKey.DOWN))
        {
            delta = delta.add(new Vec3D(this.siny * SPEED * shiftSpeed, 0, -(this.cosy * SPEED * shiftSpeed)));
        }
        if (KEY.isPressed(KeyInput.InputKey.LEFT))
        {
            delta = delta.add(new Vec3D(-(this.cosy * SPEED * shiftSpeed), 0, -(this.siny * SPEED * shiftSpeed)));
        }
        if (KEY.isPressed(KeyInput.InputKey.RIGHT))
        {
            delta = delta.add(new Vec3D(this.cosy * SPEED * shiftSpeed, 0, this.siny * SPEED * shiftSpeed));
        }
        vel.X = delta.X;
        vel.Z = delta.Z;

        /*
        JUMP CODE
        */
        if (KEY.isPressed(KeyInput.InputKey.SPACE))
        {
            if (canJump)
            {
                vel.Y = -JUMP_VELOCITY;
            }
        }
        if (KEY.isPressed(KeyInput.InputKey.CONTROL))
        {
            //Unused
        }
        /*
        APPLY GRAVITY
        */
        vel.Y += (GRAVITATIONAL_CONSTANT*time);
        
        
        
        /*
        TEST FOR COLLISIONS
        */
        boolean[] movedInAxis = tryMove(vel.scale(time), WORLD, RESCOURCE_MANAGER);
        if (movedInAxis[0] == false)
        {
            vel.X = 0; //Reset velocity
        }
        if (movedInAxis[1] == false)
        {
            if (vel.Y > 0)
            {
                canJump = true; //Mark whether on ground
            }
            vel.Y = 0; //Reset velocity
        }
        else
        {
            canJump = false; //Mark whether on ground
        }
        if (movedInAxis[2] == false)
        {
            vel.Z = 0; //Reset velocity
        }
        
        /*
        UPDATE MOUSE/HEAD LOCKING POSITION
        */
        //Key rotation
        if (KEY.isPressed(KeyInput.InputKey.ROTLEFT))
        {
            this.yRot += 0.02;
        }
        if (KEY.isPressed(KeyInput.InputKey.ROTRIGHT))
        {
            this.yRot -= 0.02;
        }
        if (KEY.isPressed(KeyInput.InputKey.ROTUP))
        {
            this.xRot -= 0.02;
        }
        if (KEY.isPressed(KeyInput.InputKey.ROTDOWN))
        {
            this.xRot += 0.02;
        }
        
        //Mouse rotation
        shiftX = ((float)MOUSE.getMouseX() - WINDOW.MESHRENDERER.halfWidthInt) / 1000f;
        shiftY = ((float)MOUSE.getMouseY() - WINDOW.MESHRENDERER.halfHeightInt) / 1000f;
        this.yRot -= shiftX;
        this.xRot += shiftY;
        GR.centerMouse();

        this.CALC(); //Recompute sin and cosine for fast calculations
        
        
        
        /*
        SELECTED BLOCK
        */
        this.mySelction.changeSelection(MOUSE.mouseWheelMoved);
        MOUSE.mouseWheelMoved = 0;
        
        /*
        RAY CASTING
        */
        final Vec3D HEAD_POS = this.pos.add(new Vec3D(0.5f, 0.4f, 0.5f));
        
        final float RAYCAST_STEP = 0.01f;
        
        Vec3D raycastResult = raycast(HEAD_POS, Vec3D.contructAngleVector(this.siny, this.sinx, this.cosy, this.cosx, RAYCAST_STEP), WORLD, RESCOURCE_MANAGER);
        if (raycastResult != null)
        {
            Vec3D lookingAtBlock = snapToBlock(raycastResult);
            
            Vec3D localBlock = raycastResult.sub(lookingAtBlock);

            int face = 0;
            
            if (localBlock.X < RAYCAST_STEP)
            {
                face = 0;
            }
            if (localBlock.X > 1f - RAYCAST_STEP)
            {
                face = 1;
            }
            
            if (localBlock.Y < RAYCAST_STEP)
            {
                face = 2;
            }
            if (localBlock.Y > 1f - RAYCAST_STEP)
            {
                face = 3;
            }
            
            if (localBlock.Z < RAYCAST_STEP)
            {
                face = 4;
            }
            if (localBlock.Z > 1f - RAYCAST_STEP)
            {
                face = 5;
            }
            
            mySelction.UPDATE_SELECTION((int)lookingAtBlock.X, (int)lookingAtBlock.Y, (int)lookingAtBlock.Z, face);
        }
        if (mineCooldown > 0)
        {
            mineCooldown -= time;
        }
        else
        {
            if (MOUSE.getMouse1Pressed())
            {
                WORLD.setBlock((int)mySelction.pos.X, (int)mySelction.pos.Y, (int)mySelction.pos.Z, (byte)mySelction.getMineBlockID());
                mineCooldown = BLOCK_UPDATE_COOLDOWN;
            }
            if (MOUSE.getMouse2Pressed())
            {
                
                int deltaX = 0;
                int deltaY = 0;
                int deltaZ = 0;
                
                switch (mySelction.face)
                {
                    case 0:
                        deltaX = -1;
                        break;
                    case 1:
                        deltaX = 1;
                        break;
                    case 2:
                        deltaY = -1;
                        break;
                    case 3:
                        deltaY = 1;
                        break;
                    case 4:
                        deltaZ = -1;
                        break;
                    case 5:
                        deltaZ = 1;
                        break;
                    default:
                        break;
                }
                WORLD.setBlock((int)mySelction.pos.X+deltaX, (int)mySelction.pos.Y+deltaY, (int)mySelction.pos.Z+deltaZ, (byte)mySelction.getSelectedBlockID());
                mineCooldown = BLOCK_UPDATE_COOLDOWN;
            }
        }

        

        //WORLD.this.change(MOUSE.mouseWheelMoved);
        //MOUSE.mouseWheelMoved = 0;
    }
    
    public float mineCooldown = -1;

    /**
     * 
     * @param pos
     * @param step
     * @param WORLD
     * @param RESCOURCE_MANAGER
     * @return 
     */
    public Vec3D raycast(Vec3D pos, Vec3D step, final World WORLD, final RescourceManager RESCOURCE_MANAGER)
    {
        
        pos = pos.deepCopy();
        
        int count = 500;
        while (RESCOURCE_MANAGER.getBlockData(getBlock(pos, WORLD)).isCollisionSolid() == false && count > 0)
        {
            pos.addHere(step);
            count--;
        }
        if (count == 0)
        {
            return null;
        }
        else
        {
            return pos;
        }
    }
    /**
     * 
     * @param pos
     * @return 
     */
    private Vec3D snapToBlock(Vec3D pos)
    {
        int x;
        int y;
        int z;
        if (pos.X < 0)
        {
            x = (int)(pos.X-1d);
        }
        else
        {
            x = (int)pos.X;
        }
        if (pos.Y < 0)
        {
            y = (int)(pos.Y-1d);
        }
        else
        {
            y = (int)pos.Y;
        }
        if (pos.Z < 0)
        {
            z = (int)(pos.Z-1d);
        }
        else
        {
            z = (int)pos.Z;
        }
        return new Vec3D(x, y, z);
    }
    /**
     * 
     * @param xp
     * @param yp
     * @param zp
     * @param WORLD
     * @return 
     */
    private byte getBlock(Vec3D pos, final World WORLD)
    {
        Vec3D snap = snapToBlock(pos);
        return WORLD.getBlock((int)snap.X, (int)snap.Y, (int)snap.Z);
    }
    
    
    /**
     * 
     * @param delta
     * @param WORLD
     * @param RESCOURCE_MANAGER
     * @return 
     */
    public boolean[] tryMove(Vec3D delta, final World WORLD, final RescourceManager RESCOURCE_MANAGER)
    {
        if (this.canMove(delta, WORLD, RESCOURCE_MANAGER))
        {
            this.move(delta);
            return new boolean[]{true, true, true};
        }
        
        Vec3D deltaTemp;
        
        deltaTemp = new Vec3D(0, delta.Y, delta.Z);
        if (this.canMove(deltaTemp, WORLD, RESCOURCE_MANAGER))
        {
            this.move(deltaTemp);
            return new boolean[]{false, true, true};
        }
        deltaTemp = new Vec3D(delta.X, 0, delta.Z);
        if (this.canMove(deltaTemp, WORLD, RESCOURCE_MANAGER))
        {
            this.move(deltaTemp);
            return new boolean[]{true, false, true};
        }
        deltaTemp = new Vec3D(delta.X, delta.Y, 0);
        if (this.canMove(deltaTemp, WORLD, RESCOURCE_MANAGER))
        {
            this.move(deltaTemp);
            return new boolean[]{true, true, false};
        }
        deltaTemp = new Vec3D(0, 0, delta.Z);
        if (this.canMove(deltaTemp, WORLD, RESCOURCE_MANAGER))
        {
            this.move(deltaTemp);
            return new boolean[]{false, false, true};
        }
        deltaTemp = new Vec3D(delta.X, 0, 0);
        if (this.canMove(deltaTemp, WORLD, RESCOURCE_MANAGER))
        {
            this.move(deltaTemp);
            return new boolean[]{true, false, false};
        }
        deltaTemp = new Vec3D(0, delta.Y, 0);
        if (this.canMove(deltaTemp, WORLD, RESCOURCE_MANAGER))
        {
            this.move(deltaTemp);
            return new boolean[]{false, true, false};
        }
        return new boolean[]{false, false, false};
    }
    /**
     * 
     * @param delta
     * @param WORLD
     * @param RESCOURCE_MANAGER
     * @return 
     */
    public boolean canMove(Vec3D delta, final World WORLD, final RescourceManager RESCOURCE_MANAGER)
    {
        
        float[] xPoints = {0.2f, 0.8f};
        float[] yPoints = {0.2f, 1f, 2f};
        float[] zPoints = {0.2f, 0.8f};
        
        for (float xPoint : xPoints)
        {
            for (float yPoint : yPoints)
            {
                for (float zPoint : zPoints)
                {
                    Vec3D updatedHitTesPoint = this.pos.add(delta).add(new Vec3D(xPoint, yPoint, zPoint));
                    
                    byte result = getBlock(updatedHitTesPoint, WORLD);
                    
                    if (RESCOURCE_MANAGER.getBlockData(result).isCollisionSolid())
                    {
                        return false;
                    }
                }
            }
        }
        
        return true;
        
        
    }

    /**
     * 
     * @param delta 
     */
    public void move(Vec3D delta)
    {
        this.pos = this.pos.add(delta);
    }
}
