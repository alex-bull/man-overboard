package seng302.Controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import seng302.Factories.CourseFactory;
import seng302.Factories.RaceFactory;
import seng302.Model.*;
import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;

public class App extends Application
{

    private static String courseFile;
    private DataReceiver dataReceiver;
    private static int choice;
    private Rectangle2D primaryScreenBounds;
    private Race r;

    @Override
    public void start(Stage primaryStage) throws Exception{
//        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("starters.fxml"));
//        Parent root = loader.load();
//        StarterController starterController = loader.getController();
//        starterController.setCourseFile(courseFile);
//        starterController.setStage(primaryStage);
//        primaryStage.setMinWidth(530);
//        primaryStage.setMinWidth(548);
//        primaryStage.setScene(new Scene(root));
//        primaryStage.show();

        primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        if (choice == 1) {
            try {
                dataReceiver = new DataReceiver("localhost", 4941);
                dataReceiver.setCanvasDimensions(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (choice == 2) {

            try {
                dataReceiver = new DataReceiver("csse-s302staff.canterbury.ac.nz", 4941);
                dataReceiver.setCanvasDimensions(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (choice == 3) {
            try {
                dataReceiver = new DataReceiver("livedata.americascup.com", 4941);
                dataReceiver.setCanvasDimensions(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Timer receiverTimer = new Timer();
        receiverTimer.schedule(dataReceiver,0,1);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO: Number of the boats and race duration are hard-coded and get rid of courseFile.
        double height = primaryScreenBounds.getHeight() * 0.8;
        Course raceCourse = new CourseFactory().createCourse(primaryScreenBounds.getWidth() * 0.70, height, courseFile);
        r = new RaceFactory().createRace(6, 1, raceCourse);

        MainController mainController = loader.getController();
        mainController.setRace(r, dataReceiver, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight(), 6);
        primaryStage.setTitle("RaceVision");
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
        primaryStage.setMinHeight(primaryScreenBounds.getHeight());
        primaryStage.setMinWidth(primaryScreenBounds.getWidth());
        primaryStage.setScene(new Scene(root, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight()));
        primaryStage.show();
    }


    public static void main( String[] args )
    {
        System.out.println();
        System.out.println("Choose your source of race data");
        System.out.println("1. RaceVision Mock Feed.");
        System.out.println("2. Test AC35 feed from CSSE Stream ");
        System.out.println("3. Test AC35 feed at livedata.americascup.com port 4941");
        Scanner scan = new Scanner(System.in);
        while (!scan.hasNextInt()) {
            System.out.println("Please enter value between 1 to 3!");
            scan.nextLine();
        }
        choice = scan.nextInt();

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
