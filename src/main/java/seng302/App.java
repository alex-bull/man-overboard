package seng302;

import java.util.InputMismatchException;
import java.util.Scanner;

public class App
{
    public static void main( String[] args )
    {
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
