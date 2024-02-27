
package engine.graphics;

import engine.meshes.Mesh;
import engine.meshes.painters.Painter;
import engine.vectors.Vec3D;
import java.awt.Color;

/**
 *
 * @author jacob
 */
public class MeshRenderer
{
    /**
     * Aspect ratio of the screen
     */
    float aspectRatio;
    /**
     * 1/tan(fov)
     */
    float tanReciprocal;
    /**
     * 
     */
    public int halfHeightInt;
    /**
     * 
     */
    public int halfWidthInt;
    /**
     * 
     */
    int[] FRAME;
    /**
     * 
     */
    float[] DEPTH;
    /**
     * 
     */
    final int FRAMEWIDTH;
    /**
     * 
     */
    final int FRAMEHEIGHT;
    /**
     * Stores a fraction for fast computation
     */
    private final static float FRACTION = 1f/255f;
    /**
     * 
     * @param frameWidth
     * @param frameHeight
     * @param aspectRatio
     * @param tanReciprocal
     * @param halfHeightInt
     * @param halfWidthInt
     * @param FRAME
     * @param DEPTH 
     */
    public MeshRenderer(int frameWidth, int frameHeight, float aspectRatio, float tanReciprocal, int halfHeightInt, int halfWidthInt, int[]FRAME, float[]DEPTH)
    {
        this.FRAMEWIDTH = frameWidth;
        this.FRAMEHEIGHT = frameHeight;
        
        this.aspectRatio = aspectRatio;
        this.tanReciprocal = tanReciprocal;
        
        this.halfHeightInt = halfHeightInt;
        this.halfWidthInt = halfWidthInt;
    
        this.FRAME = FRAME;
        this.DEPTH = DEPTH;
    }
    
    
    /**
     * 
     * @param M
     * @param START
     * @param END 
     */
    public void drawTris(final Mesh M, final int START, final int END)
    {
        int i, i2;
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];

        float red, green, blue;
        for (i = START; i < END; i++)
        {
            Vec3D[] tPoints = 
            {
                M.POINTS[M.TRIANGLES[i].POINTS[0]],
                M.POINTS[M.TRIANGLES[i].POINTS[1]],
                M.POINTS[M.TRIANGLES[i].POINTS[2]]
            };
            float[] uP = 
            {
                M.TRIANGLES[i].TEXPOINTS[0].X,
                M.TRIANGLES[i].TEXPOINTS[1].X,
                M.TRIANGLES[i].TEXPOINTS[2].X
            };
            float[] vP = 
            {
                M.TRIANGLES[i].TEXPOINTS[0].Y,
                M.TRIANGLES[i].TEXPOINTS[1].Y,
                M.TRIANGLES[i].TEXPOINTS[2].Y
            };
            float[] wP = new float[3];
            for (i2 = 0; i2 < 3; i2++)
            {
                xPoints[i2] = (int) ((((aspectRatio*tanReciprocal*tPoints[i2].X) / tPoints[i2].Z) * halfWidthInt));
                yPoints[i2] = (int) ((((tanReciprocal*tPoints[i2].Y) / tPoints[i2].Z) * halfHeightInt));
                uP[i2] /= tPoints[i2].Z;
                vP[i2] /= tPoints[i2].Z;
                wP[i2] = 1f / tPoints[i2].Z;
            }    
            if (M.TEXTURES[M.TRIANGLES[i].TEXTUREID].hasLighting())
            {
                red = M.TRIANGLES[i].redB;
                green = M.TRIANGLES[i].greenB;
                blue = M.TRIANGLES[i].blueB;
            }
            else
            {
                red = 1;
                green = 1;
                blue = 1;
            }
            drawTexturedTri(xPoints, yPoints, uP, vP, wP, M.TEXTURES[M.TRIANGLES[i].TEXTUREID], red, green, blue);
        }
    }

    
    /**
     * 
     * @param x
     * @param y
     * @param rgb 
     */
    public void setRGB(int x, int y, int rgb)
    { 
        final int MASK = 0b11111111; //USed for bitwise operations
        
        int colA = (rgb >> 24) & MASK; //Get alpha channel
        
        int index = x + (FRAMEWIDTH*y); //Find frame position
        
        //Test for transparency
        if (colA == 0)
        {
            FRAME[index] = rgb;
        }
        else
        {
            //Extract rgb channels
            int colB = rgb & MASK; //Blue
            rgb = rgb >> 8;
            int colG = rgb & MASK; //Green
            rgb = rgb >> 8;
            int colR = rgb & MASK; //Red
            
            //Get scale factors for linear combination
            float u = colA*FRACTION;
            float v = 1-u;
            
            //Get current frame rgb
            int frameRGB = FRAME[index];
            
            int frameB = frameRGB & MASK;
            frameRGB = frameRGB >> 8;
            int frameG = frameRGB & MASK;
            frameRGB = frameRGB >> 8;
            int frameR = frameRGB & MASK;
            
            //Find blended rgb
            int r = (int)(frameR*v+colR*u);
            int g = (int)(frameG*v+colG*u);
            int b = (int)(frameB*v+colB*u);
            
            
            //Rebuild rgb
            int set = 0;
            set = set << 8;
            set |= r;
            set = set << 8;
            set |= g;
            set = set << 8;
            set |= b;
            
            FRAME[index] = set;
        }
        
    }
    
    /**
     * 
     * @param xP
     * @param yP
     * @param uP
     * @param g
     * @param vP
     * @param wP
     * @param TEX
     * @param b
     * @param r
     */
    public void drawTexturedTri(int[]xP, int[]yP, float uP[], float vP[], float wP[],  final Painter TEX, float r, float g, float b)
    {
        
        
        if (yP[1] < yP[0])
        {
            SWAP(xP, 1, 0);
            SWAP(yP, 1, 0);
            SWAP(uP, 1, 0);
            SWAP(vP, 1, 0);
            SWAP(wP, 1, 0);
        }
        if (yP[2] < yP[0])
        {
            SWAP(xP, 2, 0);
            SWAP(yP, 2, 0);
            SWAP(uP, 2, 0);
            SWAP(vP, 2, 0);
            SWAP(wP, 2, 0);
        }
        if (yP[2] < yP[1])
        {
            SWAP(xP, 2, 1);
            SWAP(yP, 2, 1);
            SWAP(uP, 2, 1);
            SWAP(vP, 2, 1);
            SWAP(wP, 2, 1);
        }
        
        //Pixel co-ordinates for slope
        float dy1 = yP[1]-yP[0];
        float dx1 = xP[1]-xP[0]; 
        //Texture co-ordinated for slope
        float dv1 = vP[1]-vP[0]; 
        float du1 = uP[1]-uP[0];
        float dw1 = wP[1]-wP[0];

        //Pixel co-ordinates for slope
        float dy2 = yP[2]-yP[0];
        float dx2 = xP[2]-xP[0]; 
        //Texture co-ordinated for slope
        float dv2 = vP[2]-vP[0]; 
        float du2 = uP[2]-uP[0];
        float dw2 = wP[2]-wP[0];
        
        //Initialse x steps for drawing triangle
        float daxStep = 0;
        float dbxStep = 0;
        
        //Initialse texture co-ordinates
        float du1Step = 0;
        float dv1Step = 0;
        float dw1Step = 0;
        float du2Step = 0;
        float dv2Step = 0;
        float dw2Step = 0;
        
        float startU, startV, startW, endU, endV, endW;
        int ax, bx;
        float tStep, USTEP, VSTEP, WSTEP;
        
        //Pixel step calculations
        if (dy1 != 0) daxStep = dx1 / Math.abs(dy1);
        if (dy2 != 0) dbxStep = dx2 / Math.abs(dy2);
        
        //Texture step calculations
        if (dy1 != 0) du1Step = du1 / Math.abs(dy1);
        if (dy1 != 0) dv1Step = dv1 / Math.abs(dy1);
        if (dy1 != 0) dw1Step = dw1 / Math.abs(dy1);
        
        if (dy2 != 0) du2Step = du2 / Math.abs(dy2);
        if (dy2 != 0) dv2Step = dv2 / Math.abs(dy2);
        if (dy2 != 0) dw2Step = dw2 / Math.abs(dy2);
        
        if (dy1 != 0)
        {
            //Check not to draw out of range
            int loopYStart = yP[0];
            int loopYEnd = yP[1];
            if (loopYStart < -halfHeightInt)
            {
                loopYStart = -halfHeightInt;
            } 
            if (loopYEnd >= halfHeightInt)
            {
                loopYEnd = halfHeightInt-1;
            } 
            
            for (int i = loopYStart; i < loopYEnd; i++)
            {
                ax = (int)(xP[0] + ((float)(i-yP[0])*daxStep));
                bx = (int)(xP[0] + ((float)(i-yP[0])*dbxStep));
                
                startU = (uP[0] + ((float)(i-yP[0])*du1Step));
                startV = (vP[0] + ((float)(i-yP[0])*dv1Step));
                startW = (wP[0] + ((float)(i-yP[0])*dw1Step));
                
                endU = (uP[0] + ((float)(i-yP[0])*du2Step));
                endV = (vP[0] + ((float)(i-yP[0])*dv2Step));
                endW = (wP[0] + ((float)(i-yP[0])*dw2Step));
                if (ax > bx)
                { //If in wrong order
                    //Swap
                    int tempX = ax;
                    ax = bx;
                    bx = tempX;
                    //Swap
                    float tempU = startU;
                    startU = endU;
                    endU = tempU;
                    //Swap
                    float tempV = startV;
                    startV = endV;
                    endV = tempV;
                    //Swap
                    float tempW = startW;
                    startW = endW;
                    endW = tempW;
                }
                tStep = 1f / ((float)(bx-ax));
                //float t = 0;
                float u=startU,v=startV,w=startW;
                USTEP = tStep*(endU-startU);
                VSTEP = tStep*(endV-startV);
                WSTEP = tStep*(endW-startW);
                //Check not drawing to offscreen
                if (ax < -halfWidthInt)
                {
                    u += USTEP*((-halfWidthInt)-ax);
                    v += VSTEP*((-halfWidthInt)-ax);
                    w += WSTEP*((-halfWidthInt)-ax);
                    ax = -halfWidthInt;
                } 
                if (bx >= halfWidthInt)
                {
                    bx = halfWidthInt-1;
                }
                for (int j = ax; j < bx; j++)
                {
                    int x = j + halfWidthInt;
                    int y = i + halfHeightInt;
                    if (w > DEPTH[x+(y*FRAMEWIDTH)])
                    { //Depth buffer
                        setRGB(x, y, APPLYDISTFOG(APPLYLIGHTING(TEX.getRGB(u, v, w),r,g,b), w));
                        DEPTH[x+(y*FRAMEWIDTH)] = w;
                    }
                    u += USTEP;
                    v += VSTEP;
                    w += WSTEP;
                }
            }
        }
        
        //Update old values for the second half of the triangle
        //Pixel co-ordinates for slope
        dy1 = yP[2]-yP[1];
        dx1 = xP[2]-xP[1]; 
        //Texture co-ordinated for slope
        dv1 = vP[2]-vP[1]; 
        du1 = uP[2]-uP[1];
        dw1 = wP[2]-wP[1];

        
        //Pixel step calculations
        if (dy1 != 0) daxStep = dx1 / Math.abs(dy1);
        if (dy2 != 0) dbxStep = dx2 / Math.abs(dy2);
        
        du1Step = 0;
        dv1Step = 0;
        //Texture step calculations
        if (dy1 != 0) du1Step = du1 / Math.abs(dy1);
        if (dy1 != 0) dv1Step = dv1 / Math.abs(dy1);
        if (dy1 != 0) dw1Step = dw1 / Math.abs(dy1);
        
        if (dy1 != 0)
        {
            
            int loopYStart = yP[1];
            int loopYEnd = yP[2];
            if (loopYStart < -halfHeightInt)
            {
                loopYStart = -halfHeightInt;
            } 
            if (loopYEnd >= halfHeightInt)
            {
                loopYEnd = halfHeightInt-1;
            }
            
            for (int i = loopYStart; i < loopYEnd; i++)
            {
                ax = (int)(xP[1] + ((float)(i-yP[1])*daxStep));
                bx = (int)(xP[0] + ((float)(i-yP[0])*dbxStep));
                
                startU = (uP[1] + ((float)(i-yP[1])*du1Step));
                startV = (vP[1] + ((float)(i-yP[1])*dv1Step));
                startW = (wP[1] + ((float)(i-yP[1])*dw1Step));
                
                endU = (uP[0] + ((float)(i-yP[0])*du2Step));
                endV = (vP[0] + ((float)(i-yP[0])*dv2Step));
                endW = (wP[0] + (((float)i-yP[0])*dw2Step));
                
                if (ax > bx)
                { //If in wrong order
                    //Swap
                    int tempX = ax;
                    ax = bx;
                    bx = tempX;
                    //Swap
                    float tempU = startU;
                    startU = endU;
                    endU = tempU;
                    //Swap
                    float tempV = startV;
                    startV = endV;
                    endV = tempV;
                    //Swap
                    float tempW = startW;
                    startW = endW;
                    endW = tempW;
                }

                
                tStep = 1f / ((float)(bx-ax));
                //float t = 0;
                float u=startU,v=startV,w=startW;
                USTEP = tStep*(endU-startU);
                VSTEP = tStep*(endV-startV);
                WSTEP = tStep*(endW-startW);
                
                
                if (ax < -halfWidthInt)
                {
                    u += USTEP*((-halfWidthInt)-ax);
                    v += VSTEP*((-halfWidthInt)-ax);
                    w += WSTEP*((-halfWidthInt)-ax);
                    //t += tStep*((-halfWidthInt)-ax);
                    ax = -halfWidthInt;
                } 
                if (bx >= halfWidthInt)
                {
                    bx = halfWidthInt-1;
                } 
                
                for (int j = ax; j < bx; j++)
                {
                    //u = (1d-t) * startU + t * endU;
                    //v = (1d-t) * startV + t * endV;
                    //w = (1d-t) * startW + t * endW;
                    //System.out.println("U: " + u + " V: " + v);
                    int x = j + halfWidthInt;
                    int y = i + halfHeightInt;
                    if (w > DEPTH[x+(y*FRAMEWIDTH)])
                    {
                        setRGB(x, y, APPLYDISTFOG(APPLYLIGHTING(TEX.getRGB(u, v, w),r,g,b), w));
                        DEPTH[x+(y*FRAMEWIDTH)] = w;
                    }
                    u += USTEP;
                    v += VSTEP;
                    w += WSTEP;
                    //t += tStep;
                }
            }
        }
    }
    private final int MASK = 0b11111111;
    private final int FADECOLOUR = Window.SKY.getRGB();
    private final int FADEBLUE = FADECOLOUR & MASK;
    private final int FADEGREEN = FADECOLOUR >> 8 & MASK;
    private final int FADERED = FADECOLOUR >> 16 & MASK;
    private final int FADEALPHA = FADECOLOUR >> 24 & MASK;
    
    private int APPLYDISTFOG(int rgb, float dist) {
        int ret;
        
        int blue = rgb & MASK;
        rgb = rgb >> 8;
        int green = rgb & MASK;
        rgb = rgb >> 8;
        int red = rgb & MASK;
        rgb = rgb >> 8;
        int alpha = rgb & MASK;
        
        float a = dist*8;
        if (a > 1f) {
            a = 1f;
        }
        float aPrime = 1f - a;
        
        
        red = (int)(red*a+FADERED*aPrime);
        green = (int)(green*a+FADEGREEN*aPrime);
        blue = (int)(blue*a+FADEBLUE*aPrime);
        
        ret = alpha;
        ret = ret << 8;
        ret += red;
        ret = ret << 8;
        ret += green;
        ret = ret << 8;
        ret += blue;
        return ret;
    }
    
    /**
     * 
     * @param rgb
     * @param r
     * @param g
     * @param b
     * @return 
     */
    private int APPLYLIGHTING(int rgb, float r, float g, float b)
    {
        int ret;
        
        final int MASK = 0b11111111;

        int blue = rgb & MASK;
        rgb = rgb >> 8;
        int green = rgb & MASK;
        rgb = rgb >> 8;
        int red = rgb & MASK;
        rgb = rgb >> 8;
        int alpha = rgb & MASK;

        red = (int)(red*r);
        green = (int)(green*g);
        blue = (int)(blue*b);
        
        ret = alpha;
        ret = ret << 8;
        ret += red;
        ret = ret << 8;
        ret += green;
        ret = ret << 8;
        ret += blue;
        return ret;
    }
    
    /**
     * 
     * @param arr
     * @param a
     * @param b 
     */
    private void SWAP(int[] arr, int a, int b)
    {
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }
    /**
     * 
     * @param arr
     * @param a
     * @param b 
     */
    private void SWAP(float[] arr, int a, int b)
    {
        float temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }
    /**
     * 
     * @param clear
     */
    public void clearScreen(Color clear)
    {
        int fill = clear.getRGB();
        for (int i = 0; i < DEPTH.length; i++)
        {
            DEPTH[i] = 0;
            FRAME[i] = fill;
        }
    }
}
