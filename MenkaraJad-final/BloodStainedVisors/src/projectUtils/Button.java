/*
Button.java
Jad Menkara
non static class for creating button objects, used for the user interface and ux of the game
 */

//class importing, also makes it so this is part of a package i created
package projectUtils;

import java.awt.*;

//Button class, it makes buttons
public class Button {


    private int xVal, yVal, width, height;


    //uses 3 floats for current, hover, and non hover, so whne you hover it changes and clips at the max and min min based on the hover and non hover colors
    float currentColorA;
    float currentColorB;
    float currentColorC;


    float nonHoverA;
    float nonHoverB;
    float nonHoverC;


    float HoverA;
    float HoverB;
    float HoverC;

    SoundEffect clickSound = new SoundEffect("assets/sound_effects/button.wav"); //sound whne you click

    String text;

    //contrusctor, initializes the posotiion  and proprteis + the colors of the button, alos text
    public Button(int xVal, int yVal, int width, int height, float[] color, float[] color2, String text){
        this.xVal = xVal;
        this.yVal = yVal;
        this.width = width;
        this.height = height;


        nonHoverA = color[0];
        nonHoverB = color[1];
        nonHoverC = color[2];

        HoverA=color2[0];
        HoverB=color2[1];
        HoverC=color2[2];

        currentColorA = nonHoverA;
        currentColorB = nonHoverB;
        currentColorC = nonHoverC;
        this.text = text;
    }

    public boolean run(Graphics2D g2d, int mx, int my,boolean clicking, Font textFont){ //runs the button

        boolean clicked = false;
        if (isHovering(mx,my)){
            currentColorA+=(HoverA-nonHoverA)/10;
            currentColorB+=(HoverB-nonHoverB)/10;
            currentColorC+=(HoverC-nonHoverC)/10;

            if (clicking){ //if hovering and clicking, plays the sound effect, changes clicked value so yo return true
                clicked = true;
                currentColorA=255;
                currentColorB=255;
                currentColorC=255;
                clickSound.play();
            }
        }
        else{
            currentColorA-=(HoverA-nonHoverA)/10;
            currentColorB-=(HoverB-nonHoverB)/10;
            currentColorC-=(HoverC-nonHoverC)/10;
        }

        //clips the 3 values so they never cross the boundary of non hover and hover colors
        currentColorA = Clip.clipVal(HoverA, nonHoverA, currentColorA);
        currentColorB = Clip.clipVal(HoverB, nonHoverB, currentColorB);
        currentColorC = Clip.clipVal(HoverC, nonHoverC, currentColorC);

        //drawss the button
        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(new Color((int)currentColorA, (int)currentColorB, (int)currentColorC));
        g2d.fillRect(xVal, yVal, width, height);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(xVal, yVal, width, height);

        g2d.setFont(textFont);

        //calculates where to put the string so it's in the middle
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        //draws the string to the screen
        g2d.drawString(text, xVal+(width-textWidth)/2, yVal+(height-textHeight)/2+20);

        if(clicked){
            currentColorA=nonHoverA;
            currentColorB=nonHoverB;
            currentColorC=nonHoverC;
        }
        return clicked;
    }

    //checks if you are hovering
    private boolean isHovering(int mx, int my){
        if(mx>=xVal && my>=yVal){
            return (mx<=xVal+width && my<=yVal+height);
        }
        return false;
    }
}