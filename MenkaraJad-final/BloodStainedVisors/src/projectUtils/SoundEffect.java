package projectUtils;

/* SoundEffect.java
 * Mr. Mckenzie
 * code for using sound effects in java swing
 * 
 * 
 */


//class importing
import java.io.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;


//non-static SoundEffect class used to add sound effects to game with object
public class SoundEffect{
    private Clip c;
    public SoundEffect(String filename){
        setClip(filename);
    }
    public void setClip(String filename){
        try{
            File f = new File(filename);
            c = AudioSystem.getClip();
            c.open(AudioSystem.getAudioInputStream(f));
            //System.out.println(filename);
        } catch(Exception e){ System.out.println("error"); }
    }
    public void play(){
     //c.loop(Clip.LOOP_CONTINUOUSLY);
     if(!c.isRunning()){    
         c.setFramePosition(0);
         c.start();
     }
    }
    public void stop(){
        c.stop();
    }
}