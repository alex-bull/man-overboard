package seng302.Controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import seng302.Model.Regatta;
import seng302.Model.RegattaFactory;

import java.util.InputMismatchException;
import java.util.Scanner;

public class App extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }



    public static void main( String[] args )
    {
        launch(args);


        Scanner scanner = new Scanner(System.in);
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
        
        Regatta regatta = new RegattaFactory().createRegatta(numberOfBoats, raceDuration);
        regatta.begin();
    }
}
