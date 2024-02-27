

package engine.graphics;

import engine.environment.World;
import engine.meshes.Mesh;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author jacob
 */
public class Window extends JPanel
{
    /**
     * 
     */
    public JFrame window;
    /**
     * 
     */
    private World worldToDraw;
    /**
     * 
     */
    private final String TITLE;
    /**
     * 
     */

    private boolean done = false;
    /**
     * 
     */
    final int CROSSHAIRLENGTH = 15;
    /**
     * 
     */
    final int CROSSHAIRTHICKNESS = 2;
    /**
     * 
     */
    final BufferedImage IMAGEBUFFER;
    //final VolatileImage IMAGEBUFFER;
    /**
     * 
     */
    public final MeshRenderer MESHRENDERER;
    
    /**
     * 
     * @param fps 
     */
    public final void dispFps(float fps)
    {
        window.setTitle(TITLE + " | fps: " + fps);
    }

    /**
     * 
     * @param title
     * @param width
     * @param height 
     */
    public Window(String title, int width, int height)
    {

        window = new JFrame(); //Set the windows name
        TITLE = title;
        dispFps(0);
        window.add(this); //Add myself to the window as I am a Jframe so what this code here does is it adds the canvas for painting so to speak which allows you to call the paint() method
        
        this.setIgnoreRepaint(true);
        window.setIgnoreRepaint(true);
        
        window.setSize(width, height); //Set window size

        window.setVisible(true); //Set the window to be visible
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Stop the program when the window is closed
        window.setResizable(true); //Set my window to be resizable

        final int SCALEDOWN = 2;
        

        float fov = 100f;
        int frameheight = this.getHeight()/SCALEDOWN;
        int framewidth = this.getWidth()/SCALEDOWN;
        float aspectRatio = ((float)frameheight) / ((float)framewidth);
        float tanReciprocal = 1f / (float)Math.tan((fov*Math.PI)/360f);

        int halfWidthInt = framewidth/2;
        int halfHeightInt = frameheight/2;

        this.setCursor
        (
            this.getToolkit().createCustomCursor
            (
               new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB ),
               new Point(),
               null 
            ) 
        );
        IMAGEBUFFER = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(framewidth, frameheight);
        
        //IMAGEBUFFER = this.createVolatileImage(framewidth, frameheight);
        
        int[] frame = ((DataBufferInt)IMAGEBUFFER.getRaster().getDataBuffer()).getData();
        float[] depth = new float [frame.length];

        
        MESHRENDERER = new MeshRenderer(IMAGEBUFFER.getWidth(), IMAGEBUFFER.getHeight(), aspectRatio, tanReciprocal, halfHeightInt, halfWidthInt, frame, depth);
        
    }
    
    public static Color SKY = new Color(100, 255, 255);
    
    /**
     * 
     * @param graphics 
     */
    @Override
    public synchronized void paint(Graphics graphics)
    {
        int i;
        
        if (worldToDraw != null)
        {

            MESHRENDERER.clearScreen(SKY);
            
            MeshCamPlacer renderedMesh = new MeshCamPlacer(worldToDraw.getMesh(), worldToDraw.cam);
            
            MESHRENDERER.drawTris(renderedMesh.getPlacedMesh(), 0, renderedMesh.getTrisUsed());
            
            graphics.drawImage(IMAGEBUFFER, 0, 0, getWidth(), getHeight(), null);
            
            byte selectedBlock = (byte)(worldToDraw.player.mySelction.getSelectedBlockID());
            
            byte nextBlock = (byte)(worldToDraw.player.mySelction.nextBlock());
            byte prevBlock = (byte)(worldToDraw.player.mySelction.prevBlock());
            
            final int HUD_SIZE = 64;
            
            final int MID_SCALE_UP = 16;
            
            final int AD_SPACE = 16;
            
            final int HUD_HEIGHT = 64;
            
            graphics.drawImage(Mesh.TEXTURES[worldToDraw.RESCOURCE_MANAGER.getBlockData(selectedBlock).getTexture(0)].getImage(), (getWidth()-HUD_SIZE-MID_SCALE_UP)/2, getHeight() - HUD_SIZE - HUD_HEIGHT, HUD_SIZE + MID_SCALE_UP, HUD_SIZE + MID_SCALE_UP, null);
            
            graphics.drawImage(Mesh.TEXTURES[worldToDraw.RESCOURCE_MANAGER.getBlockData(nextBlock).getTexture(0)].getImage(), ((getWidth()-HUD_SIZE)/2) + HUD_SIZE + AD_SPACE, getHeight() - HUD_SIZE - HUD_HEIGHT, HUD_SIZE, HUD_SIZE, null);
            graphics.drawImage(Mesh.TEXTURES[worldToDraw.RESCOURCE_MANAGER.getBlockData(prevBlock).getTexture(0)].getImage(), ((getWidth()-HUD_SIZE)/2) - HUD_SIZE - AD_SPACE, getHeight() - HUD_SIZE - HUD_HEIGHT, HUD_SIZE, HUD_SIZE, null);
            
            
            
        }
        
        
        
        
        graphics.setXORMode(Color.WHITE);
        graphics.setColor(Color.BLACK);
        graphics.fillRect((this.getWidth()/2)-CROSSHAIRLENGTH, (this.getHeight()/2)-CROSSHAIRTHICKNESS, CROSSHAIRLENGTH*2, CROSSHAIRTHICKNESS*2);
        graphics.fillRect((this.getWidth()/2)-CROSSHAIRTHICKNESS, (this.getHeight()/2)-CROSSHAIRLENGTH, CROSSHAIRTHICKNESS*2, CROSSHAIRLENGTH*2);
        graphics.setPaintMode();

        
        
        graphics.setColor(Color.WHITE);
        graphics.drawString("POSITION: " + worldToDraw.cam.pos, 20, 20);
        
        graphics.dispose();
        done = true;
    }
    /**
     * 
     * @param world 
     */
    public synchronized void render(World world)
    {
        done = false;
        worldToDraw = world;
        window.repaint(0);
       
    }
    
    /**
     * 
     * @return 
     */
    public synchronized boolean done()
    {
        return done;
    }
    
}
    
