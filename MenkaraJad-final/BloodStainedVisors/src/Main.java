/* Main.java
 * Jad Menkara
 * Code to run the computer vision game "BloodStained Visors," a fighting game controlled with your real life motions
 */


//class importing
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import javax.sound.midi.*;
import projectUtils.Button;
import projectUtils.GameMap;
import projectUtils.selectButton;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
//import  java.net.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import projectUtils.SoundEffect;
//import projectUtils.MusicSound;





//extends JFrame, used for Java Swing api
class   Main extends JFrame{
    GamePanel game;


    private static Sequencer midiPlayer;

    public static void startMidi(String midFilename) {
        try {
            File midiFile = new File(midFilename);
            Sequence song = MidiSystem.getSequence(midiFile);
            midiPlayer = MidiSystem.getSequencer();
            midiPlayer.open();
            midiPlayer.setSequence(song);
            midiPlayer.setLoopCount(-1); // repeat 0 times (play once)
            midiPlayer.start();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopMidi() {
        midiPlayer.stop();
    }

    public static void main(String[] args){
        Main game = new Main();
        startMidi("assets/background.mid");

    }

    public Main(){
        super("Bl00dSt@iπεd Visors");
        game = new GamePanel();
        add(game);
        //startMidi("assets/Avgvsts_Tunes_MIDI-Endgame.mid");
        pack();   // Set size of JFame == size of stuff on it.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }


}

//contains code for the actual panel on the windows screen
class GamePanel extends JPanel implements KeyListener, ActionListener, MouseListener {

  //key press on/off
    boolean []keys;


    public static final int SCREENX =1870, SCREENY = 1150;
    public static final int MENU=0, SELECT=1, OPTIONS=2,INSTRUCTIONS=3, GAME1=4;
    //current scene
    private int screen=MENU;

    javax.swing.Timer timer;
    javax.swing.Timer requestTimer;

    //mouse values
    private Point mouse;
    private Point offset;
    private int mx;
    private int my;



    private boolean mouseLeftClick = false;
    private boolean leftMouseTap = false;

    //buttoon declaration / initialization
    Button optionsButton = new Button(590+135,250, 420, 80, new float[]{10,10,80}, new float[]{50,50,80}, "Options");
    Button introButton = new Button(590+135,350, 420, 80, new float[]{10,10,80}, new float[]{50,50,80}, "How to Play");
    Button exitButton = new Button(590+135,450, 420, 80, new float[]{10,10,80}, new float[]{50,50,80}, "Exit");
    Button startButton = new Button(590+135,150, 420, 80, new float[]{10,10,80}, new float[]{50,50,80}, "Start Game");

    Button backButton = new Button(1500,50, 320, 80, new float[]{50,50,50}, new float[]{140,140,80}, "Back");
    Button restartBackButton = new Button(1500,50, 320, 80, new float[]{50,50,50}, new float[]{140,140,80}, "Restart");
    Button pauseButton = new Button(1650,20, 200, 80, new float[]{50,50,50}, new float[]{140,140,80}, "Pause");

    Button switchCharacterButton = new Button(SCREENX/2-140,600, 280, 80, new float[]{10,80,80}, new float[]{40,120,120}, "Switch Character");

    Button startGameButton = new Button(SCREENX/2-210,850, 420, 80, new float[]{10,60,70}, new float[]{40,200,70}, "Start Game");


    Button restartGameButton = new Button(SCREENX/2-210,650, 420, 80, new float[]{50,50,50}, new float[]{140,140,80}, "Restart");
    Button pauseExitButton = new Button(SCREENX/2-210,850, 420, 80, new float[]{50,50,50}, new float[]{140,140,80}, "Exit Game");

    //declaring various fonts
    Font fontLarge = loadFont("assets/supercharge-font/Supercharge-JRgPo.otf", 45);
    Font fontML = loadFont("assets/supercharge-font/Supercharge-JRgPo.otf", 30);
    Font fontMedium = loadFont("assets/supercharge-font/Supercharge-JRgPo.otf", 20);
    Font fontSmall = loadFont("assets/supercharge-font/Supercharge-JRgPo.otf", 15);
    Font startTimerFont = loadFont("assets/supercharge-font/SuperchargeHalftone-6Y0Vq.otf", 70);

    //background images
    Image menuBackground;
    Image menuBackground2;


    ArrayList<GameMap> maps = new ArrayList<GameMap>(); //maps

    ArrayList<CharacterSetting> characterList = characterLoad(); //character loading

    ArrayList<selectButton> characterSettingButtons;

    /*private MusicSound currentMusic;

    private MusicSound[] gameSongs;
    private MusicSound menuSong;*/


    GameStartTimer startTimer;

    int currentCharacterSelect = 0;

    Image instructionsImage = new ImageIcon("assets/Instructions.png").getImage();


    Character[] inGameCharacters;

    JSONObject retrievedJson;

    //public static final int PUNCHANIM = 0, PUNCH2ANIM = 1, RUNANIM = 2, IDLEANIM = 3, SPECIALANIM = 4;

    private int[] currentInputRight = new int[]{0,0,0,0};
    private int[] currentInputLeft = new int[]{0,0,0,0};


    private GameMap currentMap;

    BottomUI bottomUI;

    boolean playing= true;

    boolean escapeTap = false;

    PauseScreen pauseScreen;

    boolean paused = true;

    SoundEffect gameOverSound;

    private boolean isPlaying = false;


    //constructor, initializes various fields and sets up the panel
    public GamePanel(){

        setPreferredSize(new Dimension(SCREENX, SCREENY));
        keys = new boolean[2000];



        timer = new javax.swing.Timer(20, this);
        timer.start();

        requestTimer = new javax.swing.Timer(90, this);
        requestTimer.start();

        //menu background loading
        menuBackground = new ImageIcon("assets/menu.gif").getImage().getScaledInstance(SCREENX, SCREENY, Image.SCALE_DEFAULT);
        menuBackground2= new ImageIcon("assets/menu2.gif").getImage().getScaledInstance(SCREENX, SCREENY, Image.SCALE_DEFAULT);


        setFocusable(true);
        requestFocus();
        addKeyListener(this);
        addMouseListener(this);

        gameOverSound =  new SoundEffect("assets/sound_effects/game.wav");

        bottomUI = new BottomUI(SCREENX,SCREENY, fontML);
        startTimer = new GameStartTimer(SCREENX, SCREENY);
        pauseScreen= new PauseScreen(SCREENX, SCREENY, startTimerFont);

        //loads all the buttons for the character select menu
        characterSettingButtons = new ArrayList<>();
        for(int i = 0; i<10; i++){
            characterSettingButtons.add(new selectButton(i*133+270, 100+100, 120, 120,  new float[]{10,10,14}, new float[]{240,240,240}, characterList.get(i).getIcon()));
        }
        for(int i = 0; i<8; i++){
            characterSettingButtons.add(new selectButton(i*133+270+135, 230+100, 120, 120,  new float[]{10,10,14}, new float[]{240,240,240}, characterList.get(i+10).getIcon()));
        }
        for(int i = 0; i<4; i++){
            characterSettingButtons.add(new selectButton(i*133+270+135+266, 360+100, 120, 120,  new float[]{10,10,14}, new float[]{240,240,240}, characterList.get(i+18).getIcon()));
        }
        inGameCharacters = new Character[2];
        inGameCharacters[0] = new Character(characterList.getFirst(), Character.LEFT);
        inGameCharacters[1] = new Character(characterList.getFirst(), Character.RIGHT);



        //adds maps to the map list
        for(int i=0;i<4;i++){
            maps.add(new GameMap(String.format("map%d", i), SCREENX, SCREENY));
        }

        /*gameSongs = new MusicSound[]{
                new MusicSound("assets/music/[SPOTDOWNLOADER.COM] Bat Country.mp3"),
                new MusicSound("assets/music/[SPOTDOWNLOADER.COM] Gungrave.mp3"),
                new MusicSound("assets/music/[SPOTDOWNLOADER.COM] Her Voice Resides.mp3"),
                new MusicSound("assets/music/[SPOTDOWNLOADER.COM] Holy Roller.mp3"),
                new MusicSound("assets/music/[SPOTDOWNLOADER.COM] Impermanence.mp3"),
                new MusicSound("assets/music/[SPOTDOWNLOADER.COM] Monsters.mp3"),
                new MusicSound("assets/music/[SPOTDOWNLOADER.COM] MUKANJYO (3).mp3"),
                new MusicSound("assets/music/[SPOTDOWNLOADER.COM] Natural Born Killer.mp3"),
                new MusicSound("assets/music/[SPOTDOWNLOADER.COM] on the verge.mp3"),
                new MusicSound("assets/music/[SPOTDOWNLOADER.COM] Reincarnate_ Reincarnated.mp3"),
                new MusicSound("assets/music/[SPOTDOWNLOADER.COM] Roots Below.mp3"),
                new MusicSound("assets/music/[SPOTDOWNLOADER.COM] Slaughterhouse 2 (feat. Chris Motionless).mp3 "),
                new MusicSound("assets/music/[SPOTDOWNLOADER.COM] Top 10 staTues tHat CriEd bloOd.mp3"),
                new MusicSound("assets/music/[SPOTDOWNLOADER.COM] Waking the Demon.mp3"),
                new MusicSound("assets/music/[SPOTDOWNLOADER.COM] Welcome to the Family.mp3"),

        };*/

        /*menuSong = new MusicSound("assets/music/[SPOTDOWNLOADER.COM] FLY ME TO THE MOON - Instrumental Version.mp3");

        currentMusic = menuSong;
        currentMusic.play();*/
    }

    //boiler plate method for loading fonts
    public Font loadFont(String name, int size){
        Font font=null;
        try{
            File fntFile = new File(name);
            font = Font.createFont(Font.TRUETYPE_FONT, fntFile).deriveFont((float)size);
        }
        catch(IOException ex){
            System.out.println(ex);
        }
        catch(FontFormatException ex){
            System.out.println(ex);
        }
        return font;
    }


    //boiler plate code for mouse and keyboard events
    @Override
    public void mousePressed(MouseEvent e){
        if(SwingUtilities.isLeftMouseButton(e)){
            mouseLeftClick=true;
            leftMouseTap = true;

        }
    }
    @Override
    public void mouseReleased(MouseEvent e){

        if(SwingUtilities.isLeftMouseButton(e)){
            mouseLeftClick=false;
            leftMouseTap = false;

        }
    }
    @Override
    public void mouseClicked(MouseEvent e){
        if(SwingUtilities.isLeftMouseButton(e)){

        }
    }
    @Override
    public void mouseEntered(MouseEvent e){
    }
    @Override
    public void mouseExited(MouseEvent e){
    }


    @Override
    public void keyReleased(KeyEvent e){
        keys[e.getKeyCode()] = false;
        if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
            escapeTap = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e){

    }

    @Override
    public void keyPressed(KeyEvent e){
        keys[e.getKeyCode()] = true;
        if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
            escapeTap = true;
        }
    }




    //this runs every time timer is fired, used for to actually run the code needed for the game
    @Override
    public void actionPerformed(ActionEvent e){
        Object source = e.getSource();
        //mouseLeftClick=false;
        if(source == timer){
            try{
                mouse = MouseInfo.getPointerInfo().getLocation();
                offset = getLocationOnScreen();
                mx = mouse.x-offset.x;
                my = mouse.y-offset.y;
            }
            catch(IllegalComponentStateException ex ){
                mx = 0;
                my = 0;
            }
            repaint(); //paints screen
            //leftMouseTap = false;
        }
        if(source == requestTimer){
            //https://youtu.be/WS_H44tvZMI?si=sYenHKtiqsUI0Nj0 //got help to create this with this youtube video

            // creates an api connection and does a get request to get the current information from the server

            retrievedJson = getFlaskApi();

            //edits the left and right character input values depending on the json object
            try{
                JSONObject left = (JSONObject)retrievedJson.get("left");

                currentInputRight[Character.PUNCHANIM] = (int)(long)left.get("punch");
                currentInputRight[Character.PUNCH2ANIM] = (int)(long)left.get("kick");
                currentInputRight[Character.SPECIALANIM] = (int)(long)left.get("special");
                currentInputRight[Character.RUNANIM] = (int)(long)left.get("moving");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try{
                JSONObject right = (JSONObject)retrievedJson.get("right");

                currentInputLeft[Character.PUNCHANIM] = (int)(long)right.get("punch");
                currentInputLeft[Character.PUNCH2ANIM] = (int)(long)right.get("kick");
                currentInputLeft[Character.SPECIALANIM] = (int)(long)right.get("special");
                currentInputLeft[Character.RUNANIM] = (int)(long)right.get("moving");

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //private int[] currentInputRight = new int[]{0,0,0,0,0};


        }





    }
    @Override //draws the game to the screen
    public void paint(Graphics g){
        Random rand = new Random();

        //instead of writing to g2d, we can write to a buffered image and then draw the buffered image to the screen to make it crisper
        BufferedImage imageBuffer = new BufferedImage(SCREENX,SCREENY,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = imageBuffer.createGraphics();
        //g2d.setFont(fontLocal);


        if(screen==MENU) { //menu screen
            //System.out.println(mx);
            //g2d.fillRect(0,0,SCREENX,SCREENY);
            g2d.drawImage(menuBackground, 0, 0, null);
            g2d.setColor(new Color(0,0,0,100));
            g2d.fillRect(0,0, SCREENX,SCREENY);


            g2d.setFont(fontLarge);
            g2d.setColor(Color.BLACK);
            g2d.drawString("BloodSta1n3d V1s0rs", 536+100,88);
            g2d.setColor(Color.WHITE);
            g2d.drawString("BloodSta1n3d V1s0rs", 528+100,80);

            //System.out.println(leftMouseTap);
            //public static final int MENU=0, SELECT=1, OPTIONS=2,INSTRUCTIONS=3, GAME1=4, GAME2=5, GAME3=6;
            changeScene(startButton.run(g2d, mx, my, leftMouseTap, fontMedium),SELECT);
            changeScene(optionsButton.run(g2d, mx, my, leftMouseTap, fontMedium), OPTIONS);
            changeScene(introButton.run(g2d, mx, my, leftMouseTap, fontMedium), INSTRUCTIONS);
            changeScene(exitButton.run(g2d, mx, my, leftMouseTap, fontMedium), true);
        }

        if(screen==OPTIONS){
            g2d.drawImage(menuBackground, 0, 0, null);
            g2d.setColor(new Color(0,0,0,100));
            g2d.fillRect(0,0, SCREENX,SCREENY);
            
            changeScene(backButton.run(g2d, mx, my, leftMouseTap, fontMedium),MENU);
            g2d.setFont(fontLarge);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Coming Soon!", 536+200,308);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Coming Soon!", 528+200,300);
        }

        if(screen==INSTRUCTIONS) { //instructions screen
            //System.out.println(mx);
            //g2d.fillRect(0,0,SCREENX,SCREENY);
            g2d.drawImage(menuBackground, 0, 0, null);

            g2d.setColor(new Color(0,0,0,100));
            g2d.fillRect(0,0, SCREENX,SCREENY);

            g2d.setFont(fontLarge);
            g2d.setColor(Color.BLACK);
            g2d.drawString("In5trucT1ons", 50,74);
            g2d.setColor(Color.WHITE);
            g2d.drawString("In5trucT1ons", 56,80);

            g2d.drawImage(instructionsImage, (SCREENX-1200)/2, (SCREENY-600)/2, null);

            changeScene(backButton.run(g2d, mx, my, leftMouseTap, fontMedium),MENU);

        }

        if(screen==SELECT) { //character select

            g2d.drawImage(menuBackground2, 0, 0, null);
            g2d.setColor(new Color(0,0,0,100));
            g2d.fillRect(0,0, SCREENX,SCREENY);

            //this changes the current character when you click a charcter select button
            for(int i =0; i<characterSettingButtons.size(); i++){
                setCharacter(characterSettingButtons.get(i).run(g2d, mx, my, leftMouseTap),   characterList.get(i),     currentCharacterSelect);
            }
            switchCharacter(switchCharacterButton.run(g2d, mx, my, leftMouseTap, fontMedium));





            g2d.setFont(fontSmall);


            //draws the bottom ui
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(5));

            g2d.fillRect(40, 900, 600, 170);
            g2d.fillRect(1390+270-430, 900, 600, 170);
            g2d.setColor(Color.WHITE);

            g2d.drawRect(1390+270-430, 900, 600, 170);
            g2d.drawRect(40, 900, 600, 170);

            //draws characters at the bottom
            g2d.drawImage(inGameCharacters[0].getIcon(), 40, 900, 170,170,null);

            g2d.drawImage(inGameCharacters[1].getIcon(), 1390+270, 900, 170,170,null);



            g2d.drawString(inGameCharacters[0].getCharacterName(), 220, 925);
            g2d.drawString(inGameCharacters[1].getCharacterName(), 1390+270-330+10-100, 925);

            //shows character stats

            g2d.drawString(String.format("Health: %.2f", inGameCharacters[0].getCurrentHealth()), 220, 955);
            g2d.drawString(String.format("Health: %.2f", inGameCharacters[1].getCurrentHealth()), 1390+270-430+10, 955);

            g2d.drawString(String.format("Firepower: %d", inGameCharacters[0].getSpecialDamage()), 220, 980);
            g2d.drawString(String.format("Firepower: %d", inGameCharacters[1].getSpecialDamage()), 1390+270-430+10, 980);

            g2d.drawString(String.format("Cooldown: %d", inGameCharacters[0].getSpecialTime()), 220, 1005);
            g2d.drawString(String.format("Cooldown: %d", inGameCharacters[1].getSpecialTime()), 1390+270-430+10, 1005);

            //g2d.setColor(Color.BLACK);
            g2d.drawRect(1390+270, 900, 170, 170);
            g2d.drawRect(40, 900, 170,170);







            changeScene(backButton.run(g2d, mx, my, leftMouseTap, fontMedium),MENU);
            startGame(startGameButton.run(g2d, mx,my, leftMouseTap, fontMedium));



        }

        if(screen==GAME1) { //game rendering
            g2d.drawImage(currentMap.getBackground(), 0, 0, null);
            g2d.setColor(new Color(0,0,0,180));
            g2d.fillRect(0,0, SCREENX,SCREENY);

            if(isPlaying){
                //actually runs the characters
                inGameCharacters[1].run(inGameCharacters[0], currentInputLeft, g2d, playing);
                inGameCharacters[0].run(inGameCharacters[1], currentInputRight, g2d, playing);


                bottomUI.run(g2d, inGameCharacters[0], inGameCharacters[1], currentMap);

                paused = pauseScreen.run(g2d, escapeTap, pauseButton.run(g2d, mx,my,leftMouseTap, fontMedium));
                boolean startingTimer = false;


                //so either when you click the pause button, or you click the esc button, the game pauses and you are in the pause screen
                if(!paused) { //paused is true when it is not paused
                    if(changeScene(restartGameButton.run(g2d, mx,my,leftMouseTap,fontMedium), MENU)) pauseScreen.switchState();
                    changeScene(pauseExitButton.run(g2d, mx,my,leftMouseTap,fontMedium), true);
                }
                else{
                    startingTimer = startTimer.run(g2d, startTimerFont);
                }

                playing = setPlaying(startingTimer, paused);
                isPlaying = checkIfDead();
            }
            else{ //this is for the death screen
                g2d.setFont(startTimerFont);
                if(inGameCharacters[0].getCurrentHealth()<0){
                    g2d.drawImage(inGameCharacters[1].getIcon(), 100, 300, SCREENY-600,SCREENY-600,null);
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(5));
                    g2d.drawRect( 100, 300, SCREENY-600,SCREENY-600);


                    g2d.setColor(Color.BLACK);
                    g2d.drawString(String.format("Winner: %s", inGameCharacters[1].getName()), SCREENY-600+100+30+10, 560+10);
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(String.format("Winner: %s", inGameCharacters[1].getName()), SCREENY-600+100+30, 560);
                }
                else {
                    g2d.drawImage(inGameCharacters[0].getIcon(), 100, 300, SCREENY-600,SCREENY-600,null);
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(5));
                    g2d.drawRect( 100, 300, SCREENY-600,SCREENY-600);


                    g2d.setColor(Color.BLACK);
                    g2d.drawString(String.format("Winner: %s", inGameCharacters[0].getName()), SCREENY-600+100+30+10, 560+10);
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(String.format("Winner: %s", inGameCharacters[0].getName()), SCREENY-600+100+30, 560);
                }
                if(restartBackButton.run(g2d, mx,my,leftMouseTap,fontMedium)){
                    Main.stopMidi();
                    Main.startMidi(String.format("assets/fight_music/%d.mid", rand.nextInt(6)+1));
                    changeScene(true,MENU);
                }

            }



        }
        g2d.dispose(); //removes the old g2d and draws the new buffered image to the screen
        g.drawImage(imageBuffer,0,0,null);
        leftMouseTap = false;
        escapeTap = false;
    }


    private boolean changeScene(boolean activate, int sceneChange){ //switches scene based on activate

        if(activate) {screen = sceneChange;
            if(screen==GAME1){
                /*currentMusic.stop();

                currentMusic = menuSong;
                currentMusic.play();*/
            }
        }
        return activate;
    }

    private boolean setPlaying(boolean a, boolean b){ //helper function
        if(a&&b)return true;
        return false;
    }





    private boolean checkIfDead(){
        if(inGameCharacters[0].getCurrentHealth()<=0 || inGameCharacters[1].getCurrentHealth()<=0 ){
            gameOverSound.play();
            Main.stopMidi();

            Random rand = new Random();
            Main.startMidi("assets/background.mid");
            return false;
        }

        return true;
    }




    private void startGame(boolean activate){ //starts the game and makes sure vaariabls are set
        if(activate) {

            screen = GAME1;

            paused =true;
            Random rand = new Random();
            startTimer = new GameStartTimer(SCREENX, SCREENY);
            currentMap = maps.get(rand.nextInt(4));
            isPlaying=true;
            Main.stopMidi();
            Main.startMidi(String.format("assets/fight_music/%d.mid", rand.nextInt(6)+1));
        }


    }

    private void setCharacter(boolean activate, CharacterSetting character, int playerNum){ //changes character
        if(activate)inGameCharacters[playerNum] = new Character(character, playerNum);
    }
    private void switchCharacter(boolean activate){ //switches the current character you are selecting
        if(activate) {
            if(currentCharacterSelect==0) {currentCharacterSelect=1;System.out.println("reet");return;}
            if(currentCharacterSelect==1) currentCharacterSelect=0;}
    }


    private void changeScene(boolean activate, boolean delete){ //overloading so you can use change scene to turn the game off
        if(activate) System.exit(0);
    }






    private ArrayList<CharacterSetting> characterLoad(){ //loads all the characters using the josn somple package

        //https://stackoverflow.com/questions/10926353/how-to-read-json-file-into-java-with-simple-json-library


        ArrayList<CharacterSetting> characterSettings = new ArrayList<>();
        try {

            //creates a parser and parses the characters.json file
            JSONParser parser = new JSONParser();
            JSONObject root = (JSONObject) parser.parse(new FileReader("assets/characters.json"));
            JSONObject characters = (JSONObject) root.get("characters"); //creates a new json object with all the characters


            for (Object key : characters.keySet()) { // we can iterate through each object in the jsonobject with JSONObject.keyset
                JSONObject character = (JSONObject) characters.get((String)key);

                /*
                 "punch_cooldown": 40,
      "punch2_cooldown": 50,
      "special_cooldown": 60
                 */
                //gets the values for each variable needed from each individual json object
                int punchDamage = (int)(long)character.get("punch_damage");
                int kickDamage = (int)(long)character.get("kick_damage");
                int specialDamage = (int)(long)character.get("special_damage");

                int punchTime = (int)(long)character.get("punch_time");
                int kickTime = (int)(long)character.get("kick_time");
                int specialTime = (int)(long) character.get("special_time");

                int health = (int)(long)character.get("health");
                double speed = ((Number) character.get("speed")).doubleValue();

                int punch_cooldown = (int)(long)character.get("punch_cooldown");
                int punch2_cooldown = (int)(long)character.get("punch2_cooldown");
                int special_cooldown = (int)(long)character.get("special_cooldown");

                int runTime = (int)(long)character.get("run_time");
                int idleTime = (int)(long)character.get("idle_time");

                String path = (String) character.get("path");
                String name = (String) character.get("name");

                //creates a new characterSetting object with the values taken from the json, this keeps everything organized and makes sure
                //we don't need to update the codebase to add new characters
                characterSettings.add(new CharacterSetting(punchDamage, kickDamage, specialDamage, punchTime, kickTime,
                        specialTime, health, speed, path, name,
                        punch_cooldown, punch2_cooldown, special_cooldown, runTime, idleTime));
            }
        }
         catch (Exception e) {
            e.printStackTrace();

        }



        return characterSettings;

    }

    private static JSONObject getFlaskApi(){ //this uses the java.net.*; package to take get requests to our flask server
        String url = "http://127.0.0.1:5000/api";
        try {
            HttpURLConnection apiConnection = fetchApiResponse(url); //we establish a connection

            if(apiConnection.getResponseCode() !=200){ //connection failed
                System.out.println("Error, could not connect to server");
                return null;
            }

            String jsonResponse = readApiResponse(apiConnection);

            JSONParser parser = new JSONParser();
            JSONObject output = (JSONObject) parser.parse(jsonResponse);

            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){ //fetches the api response (wow)
        try{
            URL url = new URL(urlString); //creates a url

            HttpURLConnection apiConnection = (HttpURLConnection) url.openConnection(); //creates a connecting

            apiConnection.setRequestMethod("GET"); //get requests because we are taking data
            return apiConnection;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null; //this happens when connection fails



    }

    private static String readApiResponse(HttpURLConnection apiConnection){ //reads the api response from an HTTPUrlConnectino
        try{

            StringBuilder resultsJson = new StringBuilder(); //we used string builder and scanner to take each line from the json and append it on

            Scanner scanner = new Scanner(apiConnection.getInputStream());

            while(scanner.hasNext()){
                resultsJson.append(scanner.nextLine());
            }
            scanner.close();

            return resultsJson.toString(); //converts it to an acutal string


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}