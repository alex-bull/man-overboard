package seng302.Factories;

import org.jdom2.JDOMException;
import seng302.Model.Course;
import seng302.Model.CourseFeature;
import seng302.Model.RaceCourse;
import seng302.Model.XMLCourseLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by psu43 on 15/03/17.
 * A factory for the course points
 */
public class CourseFactory {


    /**
     * Creates an instance of RaceCourse and injects all dependencies
     * @param screenX The width of the screen
     * @param screenY The height of the screen
     * @return Course an implementation of Course loaded from an XML file
     */
    public Course createCourse(Double screenX, Double screenY){
        // load XML file that contains course points
        File inputFile = new File("src/main/resources/course.xml");
        XMLCourseLoader parser = new XMLCourseLoader(inputFile);

        // create a raceCourse with course features
        List<CourseFeature> points = null;
        try {
            points = parser.parseCourse(screenX, screenY);
        } catch (JDOMException e) {
            System.out.println("XML file format error.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Failed to load file.");
            e.printStackTrace();
        }
        return new RaceCourse(points, parser.getWindDirection());
    }
}
