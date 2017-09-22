package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import utilities.Interpreter;
import utilities.Sounds;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mattgoodson on 4/09/17.
 * Controller for the start view
 */
public class StartController implements Initializable {

    @FXML
    private Button joinButton;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ImageView backgroundImage;


    void begin() {

        Sounds.player.loopMP3WithFadeIn("sounds/bensound-theduel.mp3", 5);
    }


    @FXML
    public void initialize(URL u, ResourceBundle r) {
        backgroundImage.setPreserveRatio(false);
        backgroundImage.fitHeightProperty().bind(anchorPane.heightProperty());
        backgroundImage.fitWidthProperty().bind(anchorPane.widthProperty());

    }


    @FXML
    public void joinGame() {
        Sounds.player.fadeOut("sounds/bensound-theduel.mp3", 3);
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("lobby.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        assert root != null;
        LobbyController lobbyController = loader.getController();
        Interpreter interpreter = new Interpreter();
        interpreter.setPrimaryStage(App.getPrimaryStage());
        lobbyController.setDataSource(interpreter);
        lobbyController.begin();
        App.getScene().setRoot(root);

    }


    @FXML
    public void settings() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("settings.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert root != null;
        SettingsController settingsController = loader.getController();
        settingsController.refresh();
        App.getScene().setRoot(root);
    }


    @FXML
    public void controls() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("controls.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert root != null;
        App.getScene().setRoot(root);
    }


    @FXML
    public void quit() {
        System.exit(0);
    }


}
