
package engine.meshes;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author jacob
 */
public class RescourceLoader
{
    /**
     * 
     */
    public RescourceLoader()
    {
        
    }
    /**
     * 
     * @param name
     * @return 
     * @throws java.io.IOException 
     */
    public BufferedImage loadImage(String name) throws IOException
    {
        return ImageIO.read(this.getClass().getResource(name));
    }
}
