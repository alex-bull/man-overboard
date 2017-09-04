package controllers;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utilities.Interpreter;

import java.io.IOException;


public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }


    private static Stage pStage;

    public static Stage getPrimaryStage() {
        return pStage;
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



        StartController startController = loader.getController();
        startController.setStage(primaryStage);

        String imagePath = "file: resources/logo.png";
        primaryStage.getIcons().add(new Image(imagePath));
        startController.begin();
        // for mac
//        java.awt.Image image = Toolkit.getDefaultToolkit().getImage(imagePath);
//        Application.getApplication().setDockIconImage(image);

//        primaryStage.setMaximized(true);
//        primaryStage.setFullScreen(true);

        //set on close requests
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
//            Platform.runLater(() -> {
//                try {
//                    primaryStage.close();
//
//                    new App().start(new Stage());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });

        });

        primaryStage.show();
//        lobbyController.autoStart();

    }


}
