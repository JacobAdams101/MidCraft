/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.game;

import static engine.Engine.ONEBILLIONTH;
import static engine.Engine.TIME;
import engine.environment.World;
import engine.environment.data.RescourceManager;
import engine.graphics.Window;

/**
 *
 * @author jacob
 */
public class GameRenderer extends Thread
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
    private final RescourceManager RESCOURCE_MANAGER;
    /**
     * 
     * @param window
     * @param world 
     * @param rescourceManager 
     */
    public GameRenderer(Window window, World world, RescourceManager rescourceManager)
    {
        this.WINDOW = window;
        this.WORLD = world;
        this.RESCOURCE_MANAGER = rescourceManager;
    }
    /**
     * 
     */
    @Override
    public synchronized void run()
    {
        int currentFrame = 0;

        long start, end;

        float fps;

        long fpsStart = 0, fpsEnd;

        final int FPSSAMPLESIZE = 60;

        while (true)
        {
            start = System.nanoTime();
            WINDOW.render(WORLD);
            do
            {
                end = System.nanoTime();
            }
            while(end < start+TIME || !WINDOW.done());
            currentFrame++;
            
            if (currentFrame >= FPSSAMPLESIZE)
            {
                currentFrame = 0;
                fpsEnd = end;

                fps = FPSSAMPLESIZE / ((fpsEnd-fpsStart)*ONEBILLIONTH);
                fpsStart = end;
                WINDOW.dispFps(fps);
            }
        }
    }
}
