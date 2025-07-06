/*
CharacterSetting.java
Jad Menkara
has the code to create the settings and metadata for each individual character
 */

import javax.imageio.ImageIO;

import java.awt.*;
import java.util.*;
import java.io.File;
import java.awt.image.BufferedImage;

//non-static CharacterSetting class used to define settings for a particular character including stats, sprites, etc
public class CharacterSetting {
    /*
    {
      "punch_damage": 8,
      "kick_damage": 5,
      "special_damage": 9,
      "punch_time": 40,
      "kick_time": 30,
      "special_time": 35,
      "health": 110,
      "speed": 0.9,
      "path": "edward"
    },
     */

    //defines various character stats
    private int punchDamage;
    private int punchTime;
    private int kickDamage;
    private int kickTime;
    private int specialDamage;
    private int specialTime;

    private int maxHealth;

    private double speed;
    BufferedImage icon;


    private static final int height = 400;


    private String name;

    ArrayList<BufferedImage> runSprites = new ArrayList<BufferedImage>();
    ArrayList<BufferedImage> punchSprites = new ArrayList<BufferedImage>();
    ArrayList<BufferedImage> punch2Sprites = new ArrayList<BufferedImage>();
    ArrayList<BufferedImage> specialSprites = new ArrayList<BufferedImage>();
    ArrayList<BufferedImage> idleSprites = new ArrayList<BufferedImage>();


    private int punchCooldown;
    private int punch2Cooldown;
    private int specialCooldown;

    private int runTime;
    private int idleTime;

    //constructor
    //the whole point of this class is to get the values from it, so this is the only method that is not just a get method
    public CharacterSetting(int punch, int punch2, int special, int punchTime, int punch2Time, int specialTime, int health, double speed, String path, String name, int punchCooldown, int punch2Cooldown, int specialCooldown, int runTime, int idleTime) {
        this.punchDamage = punch;
        this.punchTime = punchTime;
        this.kickDamage = punch2;
        this.kickTime = punch2Time;
        this.specialDamage = special;
        this.specialTime = specialTime;
        this.speed = speed*2.2;
        this.maxHealth=health;
        //System.out.println(path);
        try { //gets the icon as a buffered image
            icon = ImageIO.read(new File(String.format("assets/playerIcons/%s.png", path)));
        }
        catch (Exception e){e.printStackTrace();}
        this.name = name;
        this.runTime = runTime;
        this.idleTime = idleTime;


        //used for idle
        int n=0;
        while(true) { //gets all the sprites and scalaes them by creating an image buffer, drawing to a G2D, and then disposing the g2d with
                      // the image in the buffer sitll intact, they are scaled such that each sprites is noramlzied to the same height
            File file = new File(String.format("assets/animationSprites/%s/idle/idle%d.png", path, n));
            if(!file.exists()) break;
            n++;
            try {
                BufferedImage newImage = ImageIO.read(file);

                int newImageHeight = newImage.getHeight(null);
                int newImageWidth = newImage.getWidth(null);
                BufferedImage imageBuffer = new BufferedImage(newImageWidth * height / newImageHeight, height, BufferedImage.TYPE_INT_ARGB);

                Graphics2D newGraphics = imageBuffer.createGraphics();
                newGraphics.drawImage(newImage, 0, 0, newImageWidth * height / newImageHeight, height, null);
                newGraphics.dispose();

                idleSprites.add(imageBuffer);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }




        //used for kick
        n=0;
        while(true){
            //System.out.println(String.format("assets/animationSprites/%s/punch2%d.png",path,n));
            File file = new File(String.format("assets/animationSprites/%s/punch2/punch2%d.png",path,n));  n++;
            if(!file.exists()) break;
            try {
                BufferedImage newImage = ImageIO.read(file);

                int newImageHeight = newImage.getHeight(null);
                int newImageWidth = newImage.getWidth(null);
                BufferedImage imageBuffer = new BufferedImage(newImageWidth * height / newImageHeight, height, BufferedImage.TYPE_INT_ARGB);

                Graphics2D newGraphics = imageBuffer.createGraphics();
                newGraphics.drawImage(newImage, 0, 0, newImageWidth * height / newImageHeight, height, null);
                newGraphics.dispose();

                if (file.exists()) punch2Sprites.add(imageBuffer);
                else {
                    break;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        //used for punch
        n=0;
        while(true){
            File file = new File(String.format("assets/animationSprites/%s/punch/punch%d.png",path,n));  n++;
            if(!file.exists()) break;
            try {
                BufferedImage newImage = ImageIO.read(file);

                int newImageHeight = newImage.getHeight(null);
                int newImageWidth = newImage.getWidth(null);
                BufferedImage imageBuffer = new BufferedImage(newImageWidth * height / newImageHeight, height, BufferedImage.TYPE_INT_ARGB);

                Graphics2D newGraphics = imageBuffer.createGraphics();
                newGraphics.drawImage(newImage, 0, 0, newImageWidth * height / newImageHeight, height, null);
                newGraphics.dispose();

                if (file.exists()) punchSprites.add(imageBuffer);
                else {
                    break;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }


        //used for run
        n=0;
        while(true){
            File file = new File(String.format("assets/animationSprites/%s/run/run%d.png",path,n));  n++;
            if(!file.exists()) break;
            try {
                BufferedImage newImage = ImageIO.read(file);

                int newImageHeight = newImage.getHeight(null);
                int newImageWidth = newImage.getWidth(null);
                BufferedImage imageBuffer = new BufferedImage(newImageWidth*height/newImageHeight,height,BufferedImage.TYPE_INT_ARGB);

                Graphics2D newGraphics = imageBuffer.createGraphics();
                newGraphics.drawImage(newImage, 0, 0, newImageWidth*height/newImageHeight,height,null);
                newGraphics.dispose();

                if(file.exists()) runSprites.add(imageBuffer);
                else{break;}
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        //used for special
        n=0;
            while(true){
            File file = new File(String.format("assets/animationSprites/%s/special/special%d.png",path,n));  n++;
            if(!file.exists()) break;
            try {
                BufferedImage newImage = ImageIO.read(file);

                int newImageHeight = newImage.getHeight(null);
                int newImageWidth = newImage.getWidth(null);
                BufferedImage imageBuffer = new BufferedImage(newImageWidth * height / newImageHeight, height, BufferedImage.TYPE_INT_ARGB);

                Graphics2D newGraphics = imageBuffer.createGraphics();
                newGraphics.drawImage(newImage, 0, 0, newImageWidth * height / newImageHeight, height, null);
                newGraphics.dispose();

                if (file.exists()) specialSprites.add(imageBuffer);
                else {
                    break;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }


        this.punchCooldown = punchCooldown;
        this.punch2Cooldown = punch2Cooldown;
        this.specialCooldown = specialCooldown;
    }



    //GETTER METHODS
    public int getSpecialDamage() {
        return specialDamage;
    }

    public int getSpecialTime() {
        return specialTime;
    }

    public int getKickTime() {
        return kickTime;
    }

    public int getPunchDamage() {
        return punchDamage;
    }

    public int getPunchTime() {
        return punchTime;
    }

    public int getKickDamage() {
        return kickDamage;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public double getSpeed() {
        return speed;
    }

    public ArrayList<BufferedImage> getRunSprites() {
        return runSprites;
    }

    public BufferedImage getIcon() {
        return icon;
    }

    public ArrayList<BufferedImage> getPunch2Sprites() {
        return punch2Sprites;
    }

    public ArrayList<BufferedImage> getPunchSprites() {
        return punchSprites;
    }

    public ArrayList<BufferedImage> getSpecialSprites() {
        return specialSprites;
    }

    public ArrayList<BufferedImage> getIdleSprites() {
        return idleSprites;
    }

    public String getName() {
        return name;
    }

    public int getSpecialCooldown() {
        return specialCooldown;
    }

    public int getPunch2Cooldown() {
        return punch2Cooldown;
    }

    public int getPunchCooldown() {
        return punchCooldown;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public int getRunTime() {
        return runTime;
    }
}
