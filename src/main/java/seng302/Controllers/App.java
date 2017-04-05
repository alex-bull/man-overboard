package seng302.Controllers;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Screen;
import javafx.stage.Stage;
import seng302.Factories.CourseFactory;
import seng302.Factories.RaceFactory;
import seng302.Model.*;

import java.util.InputMismatchException;
import java.util.Scanner;

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

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
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
