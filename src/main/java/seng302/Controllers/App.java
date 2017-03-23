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
        primaryStage.setScene(new Scene(root, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight()));
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());

        Race r = generateRace(primaryScreenBounds.getWidth() * 0.70, height);
        mainController.setRace(r,4000,2000);
        primaryStage.show();

    }


    public static void main( String[] args )
    {
        launch(args);

    }

    /**
     * Creates a Race object
     * @param screenX then width of the window screen
     * @param screenY the height of the window screen
     * @return Race a race created by the race factory
     */
    public Race generateRace(Double screenX, Double screenY){
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
        return new RaceFactory().createRace(6,5, raceCourse);

    }
}
