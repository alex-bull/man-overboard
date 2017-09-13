package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mgo65 on 11/09/17.
 */
public class ControlsController implements Initializable{

    @FXML private ImageView imageView;
    @FXML private GridPane imageGrid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageView.setPreserveRatio(false);
        imageView.fitWidthProperty().bind(imageGrid.widthProperty());
        imageView.fitHeightProperty().bind(imageGrid.heightProperty());
    }


    @FXML
    public void exit() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("start.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert root != null;
        App.getScene().setRoot(root);
    }


}
