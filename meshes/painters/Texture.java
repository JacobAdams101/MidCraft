
package engine.meshes.painters;


import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;


/**
 *
 * @author jacob
 */
public class Texture implements Painter
{
    /**
     * 
     */
    private final BufferedImage TEXTURE;
    
    private final BufferedImage ORIGINAL_TEXTURE;
    /**
     * 
     */
    private final int[] TEXDATA;
    /**
     * 
     */
    private final int PIXWIDTH, PIXHEIGHT;
    
    public final static int SCALE = 8;
    
    public final static int SIZE = 2;
    
    public final static float SCALE_RECIPRICOL = 1f/SCALE;
    /**
     * 
     */
    private final boolean HASLIGHTING;
    /**
     * 
     * @param texture
     * @param hasLighting 
     */
    public Texture(BufferedImage texture, boolean hasLighting)
    {
        TEXTURE = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(texture.getWidth()*SCALE, texture.getHeight()*SCALE);
        Graphics g = TEXTURE.createGraphics();
        for (int ix = 0; ix < SCALE/SIZE; ix++)
        {
            for (int iy = 0; iy < SCALE/SIZE; iy++)
            {
                g.drawImage(texture, texture.getWidth()*ix*SIZE, texture.getHeight()*iy*SIZE, texture.getWidth()*SIZE, texture.getHeight()*SIZE, null);
            }
        }
        ORIGINAL_TEXTURE = texture;
        
        TEXDATA = ((DataBufferInt)TEXTURE.getRaster().getDataBuffer()).getData();
        PIXWIDTH = TEXTURE.getWidth();
        PIXHEIGHT = TEXTURE.getHeight();
        
        HASLIGHTING = hasLighting;
    }
    
    /**
     * 
     * @param x
     * @param y
     * @param w
     * @return 
     */
    @Override
    public final int getRGB(double x, double y, double w)
    {
        final double TEXTURE_INNACURACY_COMPENSATION = 0;
        double px = (PIXWIDTH*x/(w)), py = (PIXHEIGHT*y/(w));
        /*
        if (px >= 0 && px < PIXWIDTH && py >= 0 && py < PIXHEIGHT) return TEXDATA[((int)px) + ((int)py)*PIXWIDTH];
        else return 0x00FF00FF;
        */
        
        int pxint = (int)px;
        int pyint = (int)py;
        
        if (pxint < 0)
        {
            pxint = 0;
        }
        if (pxint >= PIXWIDTH)
        {
            pxint = PIXWIDTH-1;
        }
        if (pyint < 0)
        {
            pyint = 0;
        }
        if (pyint >= PIXHEIGHT)
        {
            pyint = PIXHEIGHT-1;
        }
        return TEXDATA[pxint + pyint*PIXWIDTH];
    }
    /**
     * 
     * @return 
     */
    @Override
    public boolean hasLighting()
    {
        return HASLIGHTING;
    }
    /**
     * 
     * @return 
     */
    @Override
    public BufferedImage getImage()
    {
        return ORIGINAL_TEXTURE;
    }
}
