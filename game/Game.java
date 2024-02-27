/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.game;

import engine.Engine;
import static engine.Engine.GAMETITLE;
import engine.environment.World;
import engine.environment.data.RescourceManager;
import engine.graphics.Window;
import engine.userinput.KeyInput;
import engine.userinput.MouseInput;
import java.awt.AWTException;
import java.awt.Robot;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jacob
 */
public class Game
{ 
    private Window window;

    private World world;

    private MouseInput mouse;

    private KeyInput keyInput; //Used to get the keyboard input of the user

    private Robot ROBOT;
    
    private final RescourceManager RESCOURCE_MANAGER;

    public Game()
    {
        
        RESCOURCE_MANAGER = new RescourceManager();
        
        window = new Window(GAMETITLE, 1920, 1080);

        world = new World(RESCOURCE_MANAGER);

        mouse = new MouseInput(window.window);

        keyInput = new KeyInput(); //Setup the key listener

        ROBOT = null;
        try
        {
            ROBOT = new Robot();
        }
        catch (AWTException ex)
        {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

    public void start() throws InterruptedException
    {
        GameRunner gr = new GameRunner(window, world, mouse, keyInput, ROBOT, RESCOURCE_MANAGER);
        GameRenderer r = new GameRenderer(window, world, RESCOURCE_MANAGER);

        
        
        gr.start();
        r.start();

        //gr.join();
        //r.join();
    }


}
