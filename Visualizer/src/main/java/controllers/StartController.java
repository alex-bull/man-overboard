package controllers;

import Animations.SoundPlayer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utilities.Interpreter;

import java.io.IOException;

/**
 * Created by mattgoodson on 4/09/17.
 */
public class StartController {

    private Stage primaryStage;
    private SoundPlayer soundPlayer;

    /**
     * Sets the stage
     * @param primaryStage Stage the stage for this window
     */
    void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    void begin() {

        soundPlayer = new SoundPlayer();
        soundPlayer.loopMP3("sounds/bensound-theduel.mp3");
    }


    @FXML
    public void joinGame() {
        soundPlayer.fade("sounds/bensound-theduel.mp3", 3);
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("lobby.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert root != null;
        Scene scene = new Scene(root);
        LobbyController lobbyController = loader.getController();
        lobbyController.setStage(primaryStage);
        Interpreter interpreter = new Interpreter();
        interpreter.setPrimaryStage(primaryStage);
        lobbyController.setDataSource(interpreter);
        lobbyController.begin();
        primaryStage.setScene(scene);
    }


}
