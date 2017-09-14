package utilities;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

import static javafx.util.Duration.ZERO;

/**
 * Created by mattgoodson on 30/08/17.
 * A singleton class for playing sounds throughout the app
 */
public class Sounds {

    public static Sounds player = new Sounds();
    private Map<String, MediaPlayer> music = new HashMap<>();
    private Map<String, MediaPlayer> effects = new HashMap<>();


    private Sounds() {
        //make it singleton
    }


    /**
     * Play a sound effect
     * This should be used instead of play mp3 for any sound effects
     * @param effect String, the file of the effect
     */
    public void playSoundEffect(String effect) {
        Media sound = new Media(Sounds.class.getClassLoader().getResource(effect).toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        effects.put(effect, mediaPlayer);
        mediaPlayer.play();
        mediaPlayer.setVolume(EnvironmentConfig.maxFXVolume);
    }

    /**
     * Set the volume value of all effects
     */
    public void setAllEffectVolume() {
        for (MediaPlayer player: effects.values()) {
            player.setVolume(EnvironmentConfig.maxFXVolume);
        }
    }


    /**
     * Play a sound file
     * @param track String the file to play
     */
    public void playMP3(String track) {

        Media sound = new Media(Sounds.class.getClassLoader().getResource(track).toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        music.put(track, mediaPlayer);
        mediaPlayer.play();
        mediaPlayer.setVolume(EnvironmentConfig.maxMusicVolume);
    }


    /**
     * Loop a sound file continuously
     * @param track String, the sound file to play
     */
    public void loopMP3(String track) {

        Media sound = new Media(Sounds.class.getClassLoader().getResource(track).toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        music.put(track, mediaPlayer);
        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(ZERO));
        mediaPlayer.play();
        mediaPlayer.setVolume(EnvironmentConfig.maxMusicVolume);
    }


    /**
     * Loop a sound file with a fadeOut in
     * @param track String, the sound file to play
     * @param time int, the fadeOut time
     */
    public void loopMP3WithFadeIn(String track, int time) {

        Media sound = new Media(Sounds.class.getClassLoader().getResource(track).toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        music.put(track, mediaPlayer);
        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(ZERO));
        mediaPlayer.setVolume(0);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(time),
                        new KeyValue(mediaPlayer.volumeProperty(), EnvironmentConfig.maxMusicVolume)));
        mediaPlayer.play();
        timeline.play();
    }


    /**
     * Set the volume value of all tracks
     */
    public void setAllMusicVolume() {
        for (MediaPlayer player: music.values()) {
            player.setVolume(EnvironmentConfig.maxMusicVolume);
        }
    }



    /**
     * Set the volume of a track
     * @param track String, the track to set
     * @param volume Double, the volume (0 - 1)
     */
    public void setVolume(String track, Double volume) {
        MediaPlayer player = music.get(track);
        if (player == null) return;
        if (volume > EnvironmentConfig.maxMusicVolume) player.setVolume(EnvironmentConfig.maxMusicVolume);
        else player.setVolume(volume);
    }



    /**
     * Fade out a playing track
     * @param track String, the name of the track to fadeOut
     * @param time int, the fadeOut time
     */
    public void fadeOut(String track, int time) {
        MediaPlayer player = music.get(track);
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
        MediaPlayer player = music.get(track);
        if (player == null) return;
        player.stop();
    }

}
