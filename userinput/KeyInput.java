
package engine.userinput;


import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

/*
Author(s): Jacob Adams
*/
public class KeyInput
{
    /**
     * 
     */
    private KeyboardFocusManager keyManager;
    /**
     * 
     */
    public int lastKeyCode;
    
    /**
     * 
     */
    private boolean[] isPressed = new boolean [InputKey.values().length];
    
    /**
     * 
     */
    public KeyInput()
    {

        keyManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        keyManager.addKeyEventDispatcher((KeyEvent e) -> {
            if(e.getID()==KeyEvent.KEY_PRESSED)
            {
                
                lastKeyCode = e.getKeyCode();
                onKey(e.getKeyCode());
                return true;
            }
            if(e.getID()==KeyEvent.KEY_RELEASED)
            {
                lastKeyCode = e.getKeyCode();
                offKey(e.getKeyCode());
                return true;
            }
            return false;
        });   
    }
    /**
     * 
     * @param key 
     */
    private void onKey(int key)
    {
        InputKey currentlyPressed; //Used to temporarily store the value of any key inputs
        currentlyPressed = KeyBindings.getInputFromKeyEvent(key); //Load the requested input from the key bindings stored
        
        if (currentlyPressed != null)
        { //If the inputed key has a use and is not null
            press(currentlyPressed); //Update the user input
        }

    }
    /**
     * 
     * @param key 
     */
    private void offKey(int key)
    {
        InputKey currentlyReleased; //Used to temporarily store the value of any key inputs
        currentlyReleased = KeyBindings.getInputFromKeyEvent(key); //Load the requested input from the key bindings stored
        
        if (currentlyReleased != null) { //If the inputed key has a use and is not null
            release(currentlyReleased); //Update the user input
        }

    }
    
    /**
     * 
     * @param k 
     */
    public void press(InputKey k)
    {
        isPressed[k.ordinal()] = true;
    }
    /**
     * 
     * @param k 
     */
    public void release(InputKey k)
    {
        isPressed[k.ordinal()] = false;
    }
    /**
     * 
     * @param k
     * @return 
     */
    public boolean isPressed(InputKey k)
    {
        return isPressed[k.ordinal()];
    }
    /**
     * 
     */
    static public enum InputKey
    {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        SPACE,
        SHIFT,
        ROTLEFT,
        ROTRIGHT,
        ROTUP,
        ROTDOWN,
        ESCAPE,
        CONTROL,
        TIMEWARP,
        JUMP,
    }
    
}
