/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.meshes.painters;

import java.awt.image.BufferedImage;

/**
 *
 * @author jacob
 */
public interface Painter
{
    /**
     * 
     * @param x
     * @param y
     * @param w
     * @return 
     */
    public int getRGB(double x, double y, double w);
    /**
     * 
     * @return 
     */
    public boolean hasLighting();
    
    public BufferedImage getImage();
}
