/*
GameStartTimer.java
Jad Menkara
Used to display the starting timer for the game


 */


import java.awt.*;
import projectUtils.SoundEffect;

//non static class to create the timer and display the timer on the screen
public class GameStartTimer {
    private double currentTimer;

    private String currentString;
    private int screenX;
    private int screenY;


    private String[] startWords = {
            "GAME IN 3",
            "GAME IN 2",
            "GAME IN 1",
            "START!"
    };


    //constructor
    public GameStartTimer(int screenX, int screenY) {
        currentTimer = 0;
        currentString = "GAME IN 3";
        this.screenX = screenX;
        this.screenY =screenY;
    }

    //runs the starting timer on the screen
    public boolean run(Graphics2D g2d, Font font) {
        /*FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(char2.getName());
        g2d.setColor(Color.BLACK);
        g2d.drawString(char2.getName(), screenX-20-textWidth+10,950+10);
        g2d.setColor(Color.WHITE);
        g2d.drawString(char2.getName(), screenX-20-textWidth,950);*/


        //if it is less than the max time, it will continue showing
        if (currentTimer < 215) {
            currentString = startWords[(int)((currentTimer/220.0*4) % 4)];
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(currentString);

            g2d.setColor(Color.BLACK);
            g2d.drawString(currentString, screenX/2-textWidth/2+10, 400+10);
            g2d.setColor(Color.WHITE);
            g2d.drawString(currentString, screenX/2-textWidth/2, 400);
            currentTimer += 1;

            return (((int)((currentTimer/220.0*4) % 4)==3)); //if you coutnted down three this value will chang and the game will start
        }

        return true;
    }



}

