package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utilities.EnvironmentConfig;

import java.io.IOException;

/**
 * Created by mattgoodson on 10/09/17.
 */
public class SettingsController {


    @FXML private TextField hostField;
    @FXML private TextField portField;
    @FXML private Slider musicSlider;
    @FXML private Slider soundFXSlider;


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


        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("start.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) hostField.getScene().getWindow();

        assert root != null;
        Scene scene = new Scene(root);
        stage.setScene(scene);

    }


    public void refresh() {
        hostField.setText(EnvironmentConfig.host);
        portField.setText(EnvironmentConfig.port.toString());
        musicSlider.setValue(EnvironmentConfig.maxMusicVolume);
        soundFXSlider.setValue(EnvironmentConfig.maxFXVolume);
    }


}
