package Animations;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.InputStream;

/**
 * Created by mattgoodson on 30/08/17.
 */
public class SoundPlayer {


    public void playSound(String soundFile) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(soundFile);
            AudioStream audioStream = new AudioStream(inputStream);
            AudioPlayer.player.start(audioStream);

        } catch (Exception e) {
            e.printStackTrace();
            // a special way i'm handling logging in this application
            System.out.println("Could not find sound tile");
        }
    }

}
