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
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        Parent root = loader.load();
        MainController mainController = loader.getController();

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        //Set window to full screen
        double height=primaryScreenBounds.getHeight()-30;
        primaryStage.setTitle("RaceVision");
        primaryStage.setMinHeight(1000);
        primaryStage.setMinWidth(1500);
        primaryStage.setScene(new Scene(root, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight()));
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());

        Scanner scanner = new Scanner(System.in);
        int numBoats = getNumBoats(scanner);
        int duration = getDuration(scanner);
        Course raceCourse = new CourseFactory().createCourse(primaryScreenBounds.getWidth() * 0.70, height);

        Race r = new RaceFactory().createRace(numBoats, duration, raceCourse);
        mainController.setRace(r,4000,4000, numBoats);
        primaryStage.show();

    }


    public static void main( String[] args )
    {
        launch(args);

    }

    /**
     * Gets number of boats from user input
     * @param scanner Scanner
     * @return int the number of competitors
     */
    private int getNumBoats(Scanner scanner) {
        System.out.println("Enter number of boats in Regatta: ");
        int numberOfBoats;
        while (true) {
            try {
                numberOfBoats = scanner.nextInt();
                if (numberOfBoats >= 2 && numberOfBoats <= 6) {
                    break;
                }
                throw new InputMismatchException("Not in valid range");
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter an integer between 2 and 6: ");
                scanner.nextLine();
            }
        }
        return numberOfBoats;
    }

    /**
     * Gets the duration from user input
     * @param scanner Scanner
     * @return int the approximate duration in minutes
     */
    private int getDuration(Scanner scanner) {
        System.out.println("Would you like the race to be completed in approximately 1 or 5 minutes? ");
        int raceDuration;
        while (true) {
            try {
                raceDuration = scanner.nextInt();
                if (raceDuration == 1 || raceDuration == 5) {
                    break;
                }
                throw new InputMismatchException("Not in valid range");
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter 1 or 5: ");
                scanner.nextLine();
            }
        }
        return raceDuration;
    }

}
