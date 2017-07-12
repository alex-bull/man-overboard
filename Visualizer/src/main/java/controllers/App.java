package controllers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utilities.Interpreter;

import java.io.IOException;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the app
     *
     * @param primaryStage Stage the primary stage
     * @throws IOException when the app cannot load
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("startView.fxml"));
        Parent root = loader.load();
        StarterController starterController = loader.getController();
        starterController.setStage(primaryStage);
        Interpreter interpreter = new Interpreter();
        interpreter.setPrimaryStage(primaryStage);
        starterController.setDataSource(interpreter);

        primaryStage.setMinWidth(530);
        primaryStage.setMinWidth(548);
        primaryStage.setScene(new Scene(root));
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
        starterController.autoStart();

    }


}
