/*
BottomUI.java
Jad Menkara
Contains class to run the bottom ui for the running game section of the game


 */

//clas importing
import java.awt.*;
import java.awt.image.BufferedImage;
import projectUtils.GameMap;


//class containing all the info to display and update the BEAUTIFUl BottomUI
public class BottomUI {

    private int screenX;
    private int screenY;
    private Font font;

    private BufferedImage bottomImage; //the image for the floor
    BottomUI(int screenX, int screenY, Font font){ //contructor, intializes variables
        this.screenX=screenX;
        this.screenY=screenY;
        this.font=font;



    }

    void run(Graphics2D g2d, Character char1, Character char2, GameMap currentMap){ //runs the bottom ui screen


        g2d.drawImage(currentMap.getFloor(),0,900,null);

        //g2d.fillRect(0, 900, screenX, screenY-900);
        g2d.setFont(font);

        g2d.setColor(new Color(0,0,0,100));
        g2d.fillRect(0,930+15,screenX,300);

        //shows the characters' names
        g2d.setColor(Color.BLACK);
        g2d.drawString(char1.getName(), 20+70+10,950+10+50);
        g2d.setColor(Color.WHITE);
        g2d.drawString(char1.getName(), 20+70+10,950+50);



        //displays icons
        g2d.setStroke(new BasicStroke(4));
        g2d.drawImage(char1.getIcon(), 20,930+30,60,60, null);
        g2d.drawRect(20,930+30,60,60);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(char2.getName());
        g2d.setColor(Color.BLACK);
        g2d.drawString(char2.getName(), screenX-20-60-textWidth-70+10,950+10+50);
        g2d.setColor(Color.WHITE);
        g2d.drawString(char2.getName(), screenX-20-60-textWidth-70,950+50);



        g2d.setStroke(new BasicStroke(4));
        g2d.drawImage(char2.getIcon(), screenX-20-60,930+30,60,60, null);
        g2d.drawRect(screenX-20-60,930+30,60,60);





        //shows the healthbar
        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(20, 980+50, 700, 30, 22,22);
        g2d.fillRoundRect(screenX-20-700, 980+50, 700, 30, 22,22);

        g2d.setColor(Color.RED);
        g2d.fillRoundRect(20, 980+50, (int)(700*char1.getCurrentHealth()/char1.getMaxHealth()), 30, 22,22);
        g2d.fillRoundRect(screenX-20-(int)(700*char2.getCurrentHealth()/char2.getMaxHealth()), 980+50, (int)(700*char2.getCurrentHealth()/char2.getMaxHealth()), 30, 22,22);



        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(5));
        g2d.drawRoundRect(20, 980+50, 700, 30, 22,22);
        g2d.drawRoundRect(screenX-20-700, 980+50, 700, 30, 22,22);




        //shows the cooldown bar
        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(20, 980+50+45, 700, 30, 22,22);
        g2d.fillRoundRect(screenX-20-700, 980+50+45, 700, 30, 22,22);

        g2d.setColor(Color.BLUE);
        g2d.fillRoundRect(20, 980+50+45, (int)(700*(char1.getCooldownBar()[0]/char1.getCooldownBar()[1])), 30, 22,22);
        g2d.fillRoundRect(screenX-20-(int)(700*(char2.getCooldownBar()[0]/char2.getCooldownBar()[1])), 980+50+45, (int)(700*(char2.getCooldownBar()[0]/char2.getCooldownBar()[1])), 30, 22,22);



        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(5));
        g2d.drawRoundRect(20, 980+50+45, 700, 30, 22,22);
        g2d.drawRoundRect(screenX-20-700, 980+50+45, 700, 30, 22,22);


    }
}
