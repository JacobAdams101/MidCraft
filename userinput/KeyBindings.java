
package engine.userinput;

import java.awt.event.KeyEvent;

/**
 *
 * @author jacob
 */
public class KeyBindings
{
    /**
     * 
     */
    public static class InputBind
    {
        /**
         * 
         */
        public int inputBindedTo;
        /**
         * 
         */
        public int alternativeBind;
        /**
         * 
         * @param key1
         * @param key2 
         */
        public InputBind(int key1, int key2)
        {
            inputBindedTo = key1;
            alternativeBind = key2;
        }
        /**
         * 
         * @param keyEvent
         * @return 
         */
        public boolean isActive(int keyEvent)
        {
            return keyEvent == inputBindedTo || keyEvent == alternativeBind;
        }

    }
    /**
     * 
     */
    public static InputBind bindedKey [];
    /**
     * 
     */
    public static final String SAVENAME = "Settings/Keybindings.txt";


    /**
     * 
     */
    static
    {
        bindedKey = new InputBind [14];
        resetKeyBinds();
    }


    /**
     * 
     */
    public static void resetKeyBinds()
    {
        bindedKey [0] = new InputBind(KeyEvent.VK_W, KeyEvent.VK_W);
        bindedKey [1] = new InputBind(KeyEvent.VK_S, KeyEvent.VK_S);
        bindedKey [2] = new InputBind(KeyEvent.VK_A, KeyEvent.VK_A);
        bindedKey [3] = new InputBind(KeyEvent.VK_D, KeyEvent.VK_D);
        bindedKey [4] = new InputBind(KeyEvent.VK_SPACE, KeyEvent.VK_SPACE);
        bindedKey [5] = new InputBind(KeyEvent.VK_SHIFT, KeyEvent.VK_SHIFT);
        bindedKey [6] = new InputBind(KeyEvent.VK_LEFT, KeyEvent.VK_Q);
        bindedKey [7] = new InputBind(KeyEvent.VK_RIGHT, KeyEvent.VK_E);
        bindedKey [8] = new InputBind(KeyEvent.VK_UP, KeyEvent.VK_UP);
        bindedKey [9] = new InputBind(KeyEvent.VK_DOWN, KeyEvent.VK_DOWN);
        bindedKey [10] = new InputBind(KeyEvent.VK_ESCAPE, KeyEvent.VK_ESCAPE);
        bindedKey [11] = new InputBind(KeyEvent.VK_CONTROL, KeyEvent.VK_CONTROL);
        bindedKey [12] = new InputBind(KeyEvent.VK_TAB, KeyEvent.VK_TAB);
        bindedKey [13] = new InputBind(KeyEvent.VK_ENTER, KeyEvent.VK_ENTER);
    }
    /**
     * 
     * @param key
     * @return 
     */
    public static KeyInput.InputKey getInputFromKeyEvent(int key)
    {
        int i; //Declare int 'i' for looping

        for (i = 0; i < bindedKey.length; i++)
        {
            if (bindedKey [i].isActive(key))
            {
                return KeyInput.InputKey.values() [i];
            }
        }
        return null;
    }

}
