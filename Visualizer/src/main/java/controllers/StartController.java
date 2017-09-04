package controllers;

import Animations.SoundPlayer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import utilities.Interpreter;

import java.io.IOException;

/**
 * Created by mattgoodson on 4/09/17.
 * Controller for the start view
 */
public class StartController {

    @FXML private Button joinButton;
    private SoundPlayer soundPlayer;


    void begin() {

        soundPlayer = new SoundPlayer();
        soundPlayer.loopMP3WithFade("sounds/bensound-theduel.mp3", 6);
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
        Stage stage = (Stage) joinButton.getScene().getWindow();

        assert root != null;
        Scene scene = new Scene(root);
        LobbyController lobbyController = loader.getController();
        Interpreter interpreter = new Interpreter();
        interpreter.setPrimaryStage(stage);
        lobbyController.setDataSource(interpreter);
        lobbyController.begin();
        stage.setScene(scene);
    }


}
