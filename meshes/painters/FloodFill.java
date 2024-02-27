/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.meshes.painters;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

/**
 *
 * @author jacob
 */
public class FloodFill implements Painter
{
    /**
     * 
     */
    private final int BLOCKRGB;
    /**
     * 
     */
    private final boolean HASLIGHTING;
    /**
     * 
     * @param color
     * @param hasLighting 
     */
    public FloodFill(Color color, boolean hasLighting)
    {
        BLOCKRGB = color.getRGB();
        
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
    public int getRGB(double x, double y, double w)
    {
        return BLOCKRGB;
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
        final BufferedImage TEXTURE = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(1, 1);
        Graphics g = TEXTURE.createGraphics();
        g.setColor(new Color(BLOCKRGB));
        g.fillRect(0, 0, 1, 1);
        return TEXTURE;
    }
}
