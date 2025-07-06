/*
Character.java
Jad Menkara
public non-static class for the currently running charcters
contains methods to change the properties and states of the player based on various factors and inputs


 */

import java.awt.*;
import java.util.*;
import java.awt.image.BufferedImage;
import projectUtils.SoundEffect;


public class Character {
    private int currentVelocity;

    private double maxVelocity;
    private double currentHealth;
    private double maxHealth;

    private int punchDamage;
    private int kickDamage;
    private int specialDamage;
    private int specialTime;
    private int kickTime;
    private int punchTime;

    private boolean isPunching = false;
    private boolean isKicking = false;
    private boolean isSpecial = false;

    private Image icon;

    public static final int RIGHT = 1;
    public static final int LEFT = 0;

    private int direction = RIGHT;

    private String name;

    //constants for the current animation that is playing
    public static final int PUNCHANIM = 0, PUNCH2ANIM = 1, RUNANIM = 2, SPECIALANIM = 3, IDLEANIM = 4;
    private int currAnimation = IDLEANIM;


    private double timeFromStartAnim = 0;
    private double stopTime;

    private String characterName;

    //used for the sprites for the animations

    private ArrayList<BufferedImage> idleImages;
    private ArrayList<BufferedImage> punchImages;
    private ArrayList<BufferedImage> punch2Images;
    private ArrayList<BufferedImage> specialImages;
    private ArrayList<BufferedImage> runImages;

    private Rectangle defenseBox = new Rectangle();
    private Rectangle hitBox = new Rectangle();
    CharacterSetting characterSettings;


    private int punchCooldown;
    private int punch2Cooldown;
    private int specialCooldown;

    private int runTime;
    private int idleTime;

    private int currentCooldown;
    double xPos;

    private int currentDamage;


    int currentMaxCooldown;

    SoundEffect punchSound;
    SoundEffect kickSound;
    SoundEffect specialSound;


    ScreenShake screenShaker; //used to shake the screen with the custom ScreenShake object
    public Character(CharacterSetting character, int right) {
        /*
            private int punchDamage;
        private int punchTime;
        private int kickDamage;
        private int kickTime;
        private int specialDamage;
        private int specialTime;

         */

        //we take character settings as a parameter to take the values from said character settings for your character

        currentCooldown = 0;
        this.characterName = character.getName();
        this.maxVelocity = character.getSpeed();
        this.currentHealth = character.getMaxHealth();
        this.maxHealth = character.getMaxHealth();
        this.punchDamage = character.getPunchDamage();
        this.kickDamage = character.getKickDamage();
        this.specialDamage = character.getSpecialDamage();
        this.specialTime = character.getSpecialTime();
        this.kickTime = character.getKickTime();
        this.punchTime = character.getPunchTime();
        this.runTime = character.getRunTime();
        this.idleTime=character.getIdleTime();
        this.name = character.getName();


        idleImages = character.getIdleSprites();
        punchImages = character.getPunchSprites();
        specialImages = character.getSpecialSprites();
        runImages = character.getRunSprites();
        punch2Images = character.getPunch2Sprites();

        punchSound = new SoundEffect("assets/sound_effects/punch.wav");
        kickSound = new SoundEffect("assets/sound_effects/kick.wav");
        specialSound = new SoundEffect("assets/sound_effects/break.wav");

        this.icon = character.getIcon();

        direction = right;

        characterSettings = character;

        //calculates your positions and your defensebox dependinv on your directioin
        if (right == RIGHT) {
            xPos =1870-350-idleImages.getFirst().getWidth(null);
            defenseBox = new Rectangle((int)xPos, 560, idleImages.getFirst().getWidth(null), 400);
        }
        if (right == LEFT) {
            xPos=350;
            defenseBox = new Rectangle((int)xPos, 560, idleImages.getFirst().getWidth(null), 400);
        }

        punchCooldown = characterSettings.getPunchCooldown();
        punch2Cooldown = characterSettings.getPunch2Cooldown();
        specialCooldown = characterSettings.getSpecialCooldown();


        stopTime = idleTime;


        screenShaker = new ScreenShake(10);
    }


    //getter and setter functions

    public int getCurrentVelocity() {
        return currentVelocity;
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public double getCurrentHealth() {
        return currentHealth;
    }

    public int getPunchDamage() {
        return punchDamage;
    }

    public int getKickDamage() {
        return kickDamage;
    }

    public int getSpecialDamage() {
        return specialDamage;
    }

    public int getSpecialTime() {
        return specialTime;
    }

    public int getKickTime() {
        return kickTime;
    }

    public int getPunchTime() {
        return punchTime;
    }

    public boolean isPunching() {
        return isPunching;
    }

    public boolean isKicking() {
        return isKicking;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public Image getIcon() {
        return icon;
    }


    //runs the character, takes in enemy character to be able to remove their health when you hit them
    public void run(Character enemyCharacter, int[] currentControls, Graphics2D g2d, boolean playing) {
        drawCharacter(g2d); //draws your character
        if(!playing) return;


        //checks if you are punching, creates a hitbox, plays soundeffect
        if (currentControls[PUNCHANIM]==1  && currentCooldown==0) {
            currAnimation = PUNCHANIM;
            timeFromStartAnim = 0;
            stopTime = punchTime;

            currentDamage = characterSettings.getPunchDamage();

            //hitbox is different depending on if you are left or right
            if (direction == RIGHT) {
                hitBox = new Rectangle((int) defenseBox.getX() - 200, (int) defenseBox.getY(), idleImages.getFirst().getWidth(null), 400);
            }
            if (direction == LEFT) {
                hitBox = new Rectangle((int) defenseBox.getX() + 200, (int) defenseBox.getY(), idleImages.getFirst().getWidth(null), 400);
            }
            punchSound.play();
            currentCooldown = punchCooldown;
            currentMaxCooldown = punchCooldown;
        }
        //checks if you are kicking, creates a hitbox, plays soundeffect
        else if (currentControls[PUNCH2ANIM]==1 && currentCooldown==0) {
            currAnimation = PUNCH2ANIM;
            timeFromStartAnim = 0;
            stopTime = kickTime;

            currentDamage = characterSettings.getKickDamage();
            if (direction == RIGHT) {
                hitBox = new Rectangle((int) defenseBox.getX() - 240, (int) defenseBox.getY(), idleImages.getFirst().getWidth(null), 400);
            }
            if (direction == LEFT) {
                hitBox = new Rectangle((int) defenseBox.getX() + 240, (int) defenseBox.getY(), idleImages.getFirst().getWidth(null), 400);
            }

            kickSound.play();
            currentCooldown=punch2Cooldown;
            currentMaxCooldown=punch2Cooldown;
        }
        //checks if you are doing a special attack, creates a hitbox, plays soundeffect
        else if (currentControls[SPECIALANIM]==1  && currentCooldown==0) {
            currAnimation = SPECIALANIM;
            timeFromStartAnim = 0;
            stopTime = specialTime;


            currentDamage = characterSettings.getSpecialDamage();
            if (direction == RIGHT) {
                hitBox = new Rectangle((int) defenseBox.getX() - 210, (int) defenseBox.getY(), idleImages.getFirst().getWidth(null), 400);
            }
            if (direction == LEFT) {
                hitBox = new Rectangle((int) defenseBox.getX()+ 210, (int) defenseBox.getY(), idleImages.getFirst().getWidth(null), 400);
            }

            specialSound.play();
            currentCooldown = specialCooldown;
            currentMaxCooldown = specialCooldown;
        }


        if (direction == RIGHT) {
            if (currentControls[RUNANIM]!=0 &&(currAnimation==IDLEANIM ||currAnimation==RUNANIM)) { //starts running and changes animation
                currAnimation = RUNANIM;
                stopTime = runTime;
                xPos-= characterSettings.getSpeed()*currentControls[RUNANIM];
                defenseBox = new Rectangle((int)xPos, (int)defenseBox.getY(), idleImages.getFirst().getWidth(null), 400);
                //moves defensebox and your xposition
            }
            else if (currentControls[RUNANIM]==0 && currAnimation==RUNANIM) { //stops you from running
                currAnimation = IDLEANIM;
                stopTime = idleTime;
            }
        }
        if (direction == LEFT) {
            if (currentControls[RUNANIM]!=0 && (currAnimation==IDLEANIM ||currAnimation==RUNANIM) ) {
                currAnimation = RUNANIM;
                stopTime = runTime;
                xPos+= characterSettings.getSpeed()*currentControls[RUNANIM];
                defenseBox = new Rectangle((int)xPos, (int)defenseBox.getY(), idleImages.getFirst().getWidth(null), 400);

            }
            else if (currentControls[RUNANIM]==0 && currAnimation==RUNANIM) {
                currAnimation = IDLEANIM;
                stopTime = idleTime;
            }
        }

        /*g2d.setColor(Color.GREEN);
        g2d.drawRect((int)defenseBox.getX(), (int)defenseBox.getY(), (int)defenseBox.getWidth(), (int)defenseBox.getHeight());
        g2d.setColor(Color.RED);*/

        //only attack the enemey if you currnelty have a hitbox
        if(hitBox!=null) {
           /* g2d.setStroke(new BasicStroke(5));
            g2d.drawRect((int) hitBox.getX(), (int) hitBox.getY(), (int) hitBox.getWidth(), (int) hitBox.getHeight());*/
            if (hitBox.intersects(enemyCharacter.getDefenseBox()) && timeFromStartAnim/stopTime>0.4) {
                enemyCharacter.removeHealth(currentDamage);
                hitBox=null;
            }
        }





        timeFromStartAnim += 1; //variable is used to caluclate the current frame in your animation
        if(currentCooldown!=0){
            currentCooldown--;
            screenShaker.run(g2d, true); //shakes the screen when you are hitting the enemy
        }
        if (timeFromStartAnim >=  stopTime && currAnimation!=RUNANIM) { //resets the current animation
            currAnimation = IDLEANIM;
            stopTime = idleTime;
            timeFromStartAnim=0;
        }


    }

    public void drawCharacter(Graphics2D g2d) { //draws the character on the screen
        int widthMultiply = 1;
        if(direction==RIGHT) widthMultiply=-1;
        int attackDisplacement = 0;
        //if(stopTime==0) stopTime=1;


        //this calculates whether or not to mvoe forward or back when you are hitting the enemy
        if(currAnimation!=IDLEANIM && currAnimation!=RUNANIM && timeFromStartAnim>0 && currentCooldown>0) attackDisplacement = 150*widthMultiply;

        //calculate sthe current index of the sprites based on the time you starterd, stop time, and # of sprites in the folder
        //does this for each animatio
        if (currAnimation == IDLEANIM) {
            Image currentImage = idleImages.get(((int)(timeFromStartAnim/stopTime* idleImages.size()))% idleImages.size());
            g2d.drawImage(currentImage, (int) defenseBox.getX()+direction*currentImage.getWidth(null)+attackDisplacement, (int) defenseBox.getY(), widthMultiply*currentImage.getWidth(null), currentImage.getHeight(null), null);
            if(timeFromStartAnim>stopTime) {currAnimation=IDLEANIM; timeFromStartAnim=0;}
        }
        if (currAnimation == RUNANIM) {
            Image currentImage = runImages.get(((int)(timeFromStartAnim/stopTime*runImages.size()))% runImages.size());
            g2d.drawImage(currentImage, (int) defenseBox.getX()+direction*currentImage.getWidth(null)+attackDisplacement, (int) defenseBox.getY(), widthMultiply*currentImage.getWidth(null), currentImage.getHeight(null), null);
            if(timeFromStartAnim>stopTime) {currAnimation=IDLEANIM; timeFromStartAnim=0;}
        }
        if (currAnimation == PUNCHANIM) {
            Image currentImage = punchImages.get(((int)(timeFromStartAnim/stopTime*punchImages.size())) % punchImages.size());
            g2d.drawImage(currentImage, (int) defenseBox.getX()+direction*currentImage.getWidth(null)+attackDisplacement, (int) defenseBox.getY(), widthMultiply*currentImage.getWidth(null), currentImage.getHeight(null), null);
            if(timeFromStartAnim>stopTime) {currAnimation=IDLEANIM; timeFromStartAnim=0;}
        }
        if (currAnimation == PUNCH2ANIM) {
            Image currentImage = punch2Images.get(((int)(timeFromStartAnim/stopTime*punch2Images.size())) % punch2Images.size());
            g2d.drawImage(currentImage, (int) defenseBox.getX()+direction*currentImage.getWidth(null)+attackDisplacement, (int) defenseBox.getY(), widthMultiply*currentImage.getWidth(null), currentImage.getHeight(null), null);
            if(timeFromStartAnim>stopTime) {currAnimation=IDLEANIM; timeFromStartAnim=0;}
        }
        if (currAnimation == SPECIALANIM) {
            Image currentImage = specialImages.get(((int)(timeFromStartAnim/stopTime*specialImages.size()))% specialImages.size());
            g2d.drawImage(currentImage, (int) defenseBox.getX()+direction*currentImage.getWidth(null)+attackDisplacement, (int) defenseBox.getY(), widthMultiply*currentImage.getWidth(null), currentImage.getHeight(null), null);
            if(timeFromStartAnim>stopTime) {currAnimation=IDLEANIM; timeFromStartAnim=0;}
        }


    }

    public void removeHealth(int healthInput){ //removes health from the player
        currentHealth-=healthInput;
        currentHealth = Math.max(0,currentHealth);
    }


    public double[] getCooldownBar(){ //usd for the game gui
        if(currentCooldown==0){
            return new double[]{50,50};
        }
        return new double[]{currentCooldown, currentMaxCooldown};
    }

    // getter methods
    public Rectangle getHitBox() {
        return hitBox;
    }

    public Rectangle getDefenseBox() {
        return defenseBox;
    }

    public String getCharacterName() {
        return characterName;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public String getName() {
        return name;
    }
}
