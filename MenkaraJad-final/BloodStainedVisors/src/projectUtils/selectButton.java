/*
selectButton.java
Jad Menkara
has the code to create sselectbutton objects which are used to select characters, very similr to the button class but with an image instead



 */



package projectUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

//the class for creating selectbutton objects
public class selectButton {


    private int xVal, yVal, width, height;


    //usecd for the hovering to non hovering transition and making the ui cleaner and more responseive
    float currentColorA;
    float currentColorB;
    float currentColorC;


    float nonHoverA;
    float nonHoverB;
    float nonHoverC;


    float HoverA;
    float HoverB;
    float HoverC;

    String text;

    SoundEffect clickSound = new SoundEffect("assets/sound_effects/button.wav");
    BufferedImage img;

    //constructor for the class, initializes variables and gets the image
    public selectButton(int xVal, int yVal, int width, int height, float[] color, float[] color2, BufferedImage img){
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

        this.img = img;
    }

    public boolean run(Graphics2D g2d, int mx, int my,boolean clicking){


        //when you hover, the color changes according to the manually made animation
        //this is done by increaseing or lowering the currentColor values by regulat internvals when you start or stop hoverin on the image
        boolean clicked = false;
        if (isHovering(mx,my)){
            currentColorA+=(HoverA-nonHoverA)/10;
            currentColorB+=(HoverB-nonHoverB)/10;
            currentColorC+=(HoverC-nonHoverC)/10;

            if (clicking){
                clicked = true; //changes the reutrn vallue whne you clikc the screen
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

        //clips the colors so they dont go off infinitely
        currentColorA = Clip.clipVal(HoverA, nonHoverA, currentColorA);
        currentColorB = Clip.clipVal(HoverB, nonHoverB, currentColorB);
        currentColorC = Clip.clipVal(HoverC, nonHoverC, currentColorC);


        //draws the border with the color on top of the image like a frame
        g2d.setStroke(new BasicStroke(10));
        g2d.setColor(new Color((int)currentColorA, (int)currentColorB, (int)currentColorC));
        g2d.drawRect(xVal, yVal, width, height);
        g2d.drawImage(img, xVal, yVal, width, height, null);


        if(clicked){
            currentColorA=nonHoverA;
            currentColorB=nonHoverB;
            currentColorC=nonHoverC;
        }
        return clicked;
    }

    private boolean isHovering(int mx, int my){
        if(mx>=xVal && my>=yVal){
            return (mx<=xVal+width && my<=yVal+height);
        }
        return false;
    }
}