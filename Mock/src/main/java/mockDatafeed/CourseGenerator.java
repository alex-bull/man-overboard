package mockDatafeed;

import models.MutablePoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static parsers.xml.race.ThemeEnum.AMAZON;
import static parsers.xml.race.ThemeEnum.ANTARCTICA;
import static parsers.xml.race.ThemeEnum.BERMUDA;

/**
 * Created by psu43 on 20/09/17.
 * Course Generator
 */
public class CourseGenerator {
    private Random randomGen = new Random();
    private HashMap<String, MutablePoint> coursePaths = new HashMap<>();
    private String chosenCourse;

    /**
     * Constructs a course generate with preset paths to course templates
     */
    CourseGenerator() {
        coursePaths.put("/bermuda.xml", new MutablePoint(32.35763 , -64.81332));
        coursePaths.put("/antarctica.xml", new MutablePoint(-64.68325, -63.09448));
        coursePaths.put("/amazon.xml", new MutablePoint(0.49918, -50.52389));
    }

    /**
     * Generates a random course template
     * @return String the path to the template
     */
    public String generateCourse() {
        List<String> keys = new ArrayList<>(coursePaths.keySet());
        this.chosenCourse = keys.get(randomGen.nextInt(keys.size()));
        return this.chosenCourse;
    }

    /**
     * Gets the prestart coordinates that matches with the chosen course
     * @return MutablePoint the prestart location
     */
    public MutablePoint getPrestart() {
        return coursePaths.get(this.chosenCourse);
    }

    /**
     * Returns the value of the course theme
     * @return Integer course theme id
     */
    public Integer getThemeId() {
        if (chosenCourse.contains("antarctica")) {
            return ANTARCTICA.getValue();
        }
        else if (chosenCourse.contains("bermuda")) {
            return BERMUDA.getValue();
        }
        else if (chosenCourse.contains("amazon")) {
            return AMAZON.getValue();
        }
        return BERMUDA.getValue();
    }
}
