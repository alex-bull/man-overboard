package mockDatafeed;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by psu43 on 20/09/17.
 * Course Generator
 */
public class CourseGenerator {
    private Random randomGen = new Random();
    private ArrayList<String> coursePaths = new ArrayList<>();

    CourseGenerator() {
//        coursePaths.add("/raceTemplate.xml");
        coursePaths.add("/antarctica.xml");

    }

    public String generateCourse() {
        int index = randomGen.nextInt(coursePaths.size());
        return coursePaths.get(index);
    }
}
