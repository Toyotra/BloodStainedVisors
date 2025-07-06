/*
PauseScreen.java
Jad Menkara
used to display and run the pause screen menu


 */


import java.awt.*;

//non static class for the pause screen
public class PauseScreen {

    private int screenX, screenY;
    private boolean currentState;
    Font font;
    public PauseScreen(int screenX, int screenY, Font font) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.font = font;
        currentState=false;



    }

    //runs the screen

    public boolean run(Graphics2D g2d, boolean escapeTap, boolean buttonTap ){
        if(escapeTap || buttonTap){
            switchState(); //this swithed the screen on if either escape or the pause button is clicked
        }

        if(currentState){ //if it is on the pause screen will be displayed

            g2d.setColor(new Color(0,0,0,150));
            g2d.fillRect(0,0,screenX,screenY);




            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth("Paused");

            g2d.setColor(Color.BLACK);
            g2d.drawString("Paused", screenX/2-textWidth/2+10, 400+10);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Paused", screenX/2-textWidth/2, 400);




            return false;
        }

        return true;
    }

    public void switchState(){ //switches state from off to on or on to off
        if(currentState){ currentState=false; return;}
        currentState=true;
    }
}
