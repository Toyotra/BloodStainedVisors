/*
ScreenShake.java
Jad Menkara
used to shake the screen


 */

import java.awt.*;
import java.util.*;


//non static screen shake class, shakes screen when you run it and activate is true
public class ScreenShake {
    private int intensity;
    Random rand;
    public ScreenShake(int intensity) {
        this.intensity = intensity;
        rand=new Random();
    }

    public void run(Graphics2D g2d, boolean activate){ //very simple, just translates the screen based on the intensity
        if(activate){
            g2d.translate(rand.nextInt(intensity),rand.nextInt(intensity));
        }
    }
}
