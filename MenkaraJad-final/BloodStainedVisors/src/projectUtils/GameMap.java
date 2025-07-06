/*
GameMap.java
Jad Menkara
used to create a game map object, containing the information for one map



 */



package projectUtils;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.awt.image.BufferedImage;


//non-static GameMap object, used to create a "map" for the game
public class GameMap {

    private Image background;
    private BufferedImage floor;

    //constructor, reads the files for the floor and background for the map
    public GameMap(String name, int width, int height){
        try{
            floor = ImageIO.read(new File(String.format("assets/floor/%s_floor.png", name)));
        }
        catch (Exception e){e.printStackTrace();}
         background = new ImageIcon(String.format("assets/%s.gif", name)).getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
    }

    //getter methods

    public Image getBackground() {
        return background;
    }

    public BufferedImage getFloor() {
        return floor;
    }
}
