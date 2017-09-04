package Animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static javafx.util.Duration.ZERO;

/**
 * Created by mattgoodson on 30/08/17.
 * A class for playing sounds
 */
public class SoundPlayer {

    private Map<String, MediaPlayer> players = new HashMap<>();


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


    /**
     * Loop a sound file
     * @param track String, the sound file to play
     */
    public void loopMP3(String track) {

        Media sound = new Media(getClass().getClassLoader().getResource(track).toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        players.put(track, mediaPlayer);
        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(ZERO));
        mediaPlayer.play();
    }


    /**
     * Loop a sound file with a fade in
     * @param track String, the sound file to play
     * @param time int, the fade time
     */
    public void loopMP3WithFade(String track, int time) {

        Media sound = new Media(getClass().getClassLoader().getResource(track).toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        players.put(track, mediaPlayer);
        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(ZERO));
        mediaPlayer.setVolume(0);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(time),
                        new KeyValue(mediaPlayer.volumeProperty(), 1.0)));
        mediaPlayer.play();
        timeline.play();

    }


    /**
     * Fade out a playing track
     * @param track String, the name of the track to fade
     * @param time int, the fade time
     */
    public void fade(String track, int time) {
        MediaPlayer player = players.get(track);
        if (player == null) return;
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(time),
                        new KeyValue(player.volumeProperty(), 0)));
        timeline.setOnFinished(event -> player.stop());
        timeline.play();
    }


    /**
     * Stop a track playing
     * @param track String the track to stop
     */
    public void stop(String track) {
        MediaPlayer player = players.get(track);
        if (player == null) return;
        player.stop();
    }

}
