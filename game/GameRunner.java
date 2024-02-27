
package engine.game;

import static engine.Engine.ONEBILLIONTH;
import static engine.Engine.TIME;
import engine.environment.World;
import engine.environment.data.RescourceManager;
import engine.graphics.Window;
import engine.userinput.KeyInput;
import engine.userinput.MouseInput;
import java.awt.Insets;
import java.awt.Robot;

/**
 *
 * @author jacob
 */
public class GameRunner extends Thread
{
    /**
     * 
     */
    private final Window WINDOW;
    /**
     * 
     */
    private final World WORLD;
    /**
     * 
     */
    private final MouseInput MOUSE;
    /**
     * 
     */
    private final KeyInput KEY;
    /**
     * 
     */
    private final Robot ROBOT;
    /**
     * 
     */
    private final RescourceManager RESCOURCE_MANAGER;
    /**
     * 
     * @param window
     * @param world
     * @param mouse
     * @param keyInput
     * @param robot 
     * @param rescourceManager 
     */
    public GameRunner(Window window, World world, MouseInput mouse, KeyInput keyInput, Robot robot, RescourceManager rescourceManager)
    {
        this.WINDOW = window;
        this.WORLD = world;
        this.MOUSE = mouse;
        this.KEY = keyInput;
        this.ROBOT = robot;
        this.RESCOURCE_MANAGER = rescourceManager;
    }
    /**
     * 
     */
    @Override
    public void run()
    {
        long start, end, period = 0;
        

        while (true)
        {
            start = System.nanoTime();

            WORLD.update(period*ONEBILLIONTH, KEY, MOUSE, WINDOW, this);

            
            if (KEY.isPressed(KeyInput.InputKey.ESCAPE))
            {
                System.exit(0);
            }
            
            
            

            do
            {
                end = System.nanoTime();
            }
            while(end < start+TIME);
            period = end-start;
        }
    }
    /**
     * 
     */
    public void centerMouse()
    {
        if (ROBOT != null)
        {
            Insets inset = WINDOW.window.getInsets();
            int windowMidX = WINDOW.MESHRENDERER.halfWidthInt + WINDOW.getX() + inset.left;
            int windowMidY = WINDOW.MESHRENDERER.halfHeightInt + WINDOW.getY() + inset.top;
            ROBOT.mouseMove(windowMidX, windowMidY);
        }
    }
}
