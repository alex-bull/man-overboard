package controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;


public class App extends Application {
    private static Stage pStage;
    private static Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return pStage;
    }

    public static Scene getScene() {
        return scene;
    }

    /**
     * Starts the app
     *
     * @param primaryStage Stage the primary stage
     * @throws IOException when the app cannot load
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("start.fxml"));
        Parent root = loader.load();
        Scene rootScene = new Scene(root);
        primaryStage.setScene(rootScene);
        primaryStage.setTitle("Man Overboard");
        primaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
        primaryStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());


        scene = rootScene;
        pStage = primaryStage;

        StartController startController = loader.getController();

        String imagePath = "file: resources/logo.png";
        primaryStage.getIcons().add(new Image(imagePath));
        startController.begin();
        // for mac
//        java.awt.Image image = Toolkit.getDefaultToolkit().getImage(imagePath);
//        Application.getApplication().setDockIconImage(image);

        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);

        //set on close requests
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });

        primaryStage.show();
    }


}
