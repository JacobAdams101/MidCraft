
package engine;

import engine.game.Game;

/**
 *
 * @author jacob
 */
public class Engine
{
    /**
     * 
     */
    public static final float ONEBILLIONTH = 0.000000001f;
    /**
     * 
     */
    public static final String GAMETITLE = "MEH CRAFT";
    
    /**
     * 
     */
    public static final long MAXFPS = 60;
    /**
     * 
     */
    public static final long TIME = 1000000000l/MAXFPS;
    
    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException
    {
        Game game = new Game();
        game.start();
    }
}
