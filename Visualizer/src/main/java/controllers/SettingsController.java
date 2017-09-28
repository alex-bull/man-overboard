package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import utilities.EnvironmentConfig;
import utilities.Sounds;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mattgoodson on 10/09/17.
 */
public class SettingsController implements Initializable {


    @FXML
    private TextField hostField;
    @FXML
    private TextField portField;
    @FXML
    private Slider musicSlider;
    @FXML
    private Slider soundFXSlider;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        musicSlider.valueProperty().addListener((ov, old_val, new_val) -> {
            EnvironmentConfig.maxMusicVolume = new_val.doubleValue();
            Sounds.player.setAllMusicVolume();
        });
    }


    public void saveAndExit() {

        try {
            EnvironmentConfig.port = Integer.parseInt(portField.getText());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Port must be an integer", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        EnvironmentConfig.host = hostField.getText();
        EnvironmentConfig.maxMusicVolume = musicSlider.getValue();
        EnvironmentConfig.maxFXVolume = soundFXSlider.getValue();
        Sounds.player.setAllMusicVolume();
        Sounds.player.setAllEffectVolume();

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("start.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
           // e.printStackTrace();
        }

        assert root != null;
        App.getScene().setRoot(root);

    }


    void refresh() {
        hostField.setText(EnvironmentConfig.host);
        portField.setText(EnvironmentConfig.port.toString());
        musicSlider.setValue(EnvironmentConfig.maxMusicVolume);
        soundFXSlider.setValue(EnvironmentConfig.maxFXVolume);
    }


}
