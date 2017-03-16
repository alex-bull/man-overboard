package seng302.Controllers;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import seng302.Model.*;

public class App extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        Parent root = loader.load();

        RaceViewController raceViewController = loader.getController();

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        //Set window to full screen
        double height=primaryScreenBounds.getHeight()-30;
        primaryStage.setTitle("RaceVision");
        primaryStage.setScene(new Scene(root, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight()));
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());

        Race r = generateRace(raceViewController, primaryScreenBounds.getWidth(), height);

        raceViewController.setRace(r);
        raceViewController.begin(primaryScreenBounds.getWidth(),height);

        primaryStage.show();
//        System.out.println(primaryStage.getWidth());
//        System.out.println(primaryStage.getHeight());

    }


    public static void main( String[] args )
    {
        launch(args);

    }

    public Race generateRace(RaceDelegate delegate, Double screenX, Double screenY){
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Enter number of boats in Regatta: ");
//        int numberOfBoats;
//        while (true) {
//            try {
//                numberOfBoats = scanner.nextInt();
//                if (numberOfBoats >= 2 && numberOfBoats <= 6) {
//                    break;
//                }
//                throw new InputMismatchException("Not in valid range");
//            } catch (Exception e) {
//                System.out.println("Invalid input. Please enter an integer between 2 and 6: ");
//                scanner.nextLine();
//            }
//        }
//
//        System.out.println("Would you like the race to be completed in approximately 1 or 5 minutes? ");
//        int raceDuration;
//        while (true) {
//            try {
//                raceDuration = scanner.nextInt();
//                if (raceDuration == 1 || raceDuration == 5) {
//                    break;
//                }
//                throw new InputMismatchException("Not in valid range");
//            } catch (Exception e) {
//                System.out.println("Invalid input. Please enter 1 or 5: ");
//                scanner.nextLine();
//            }
//        }

        //create the match races, only one is used for now
        Course raceCourse = new CourseFactory().createCourse(screenX, screenY);
        return new RaceFactory().createRace(6,1, delegate, raceCourse);

    }
}
