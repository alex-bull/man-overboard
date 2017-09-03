package Animations;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static javafx.util.Duration.ZERO;

/**
 * Created by mattgoodson on 30/08/17.
 */
public class SoundPlayer {
    private List<MediaPlayer> sounds=new ArrayList<>();

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
    public void playMP3(String soundFile){
        Media sound = new Media(getClass().getClassLoader().getResource(soundFile).toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        sounds.add(mediaPlayer);
        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(ZERO));
        mediaPlayer.play();
    }

}
