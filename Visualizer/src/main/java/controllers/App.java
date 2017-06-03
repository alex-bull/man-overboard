package controllers;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utilities.Interpreter;

import java.io.IOException;


public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the app
     * @param primaryStage Stage the primary stage
     * @throws IOException when the app cannot load
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("startView.fxml"));
        Parent root = loader.load();

        Scene rootScene = new Scene(root);
        primaryStage.setScene(rootScene);

        StarterController starterController = loader.getController();
        starterController.setStage(primaryStage);
        starterController.setDataSource(new Interpreter());

        String imagePath = "file: resources/logo.png";
        primaryStage.getIcons().add(new Image(imagePath));

        // for mac
//        java.awt.Image image = Toolkit.getDefaultToolkit().getImage(imagePath);
//        Application.getApplication().setDockIconImage(image);

        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);

        //set on close requests
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();

    }


}
