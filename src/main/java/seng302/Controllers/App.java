package seng302.Controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import seng302.TestMockDatafeed.BinaryPackager;

public class App extends Application
{

    private static String courseFile;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("starters.fxml"));
        Parent root = loader.load();
        StarterController starterController = loader.getController();
        starterController.setCourseFile(courseFile);
        starterController.setStage(primaryStage);
        primaryStage.setMinWidth(530);
        primaryStage.setMinWidth(548);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }


    public static void main( String[] args )
    {
        try {
            courseFile = args[0];
            launch(args);

        }
        catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("No course XML file was provided.");
            System.exit(1);
        }

    }



}
