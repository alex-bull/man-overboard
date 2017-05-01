package seng302.Factories;

import org.jdom2.JDOMException;
import seng302.Model.*;

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
    public Course createCourse(Double screenX, Double screenY, String courseFile){
        // load XML file that contains course points
        File inputFile = new File(courseFile);
        XMLTestCourseLoader parser = new XMLTestCourseLoader(inputFile);

        // create a raceCourse with course features
        List<CourseFeature> points = null;
        List<MutablePoint> boundary = null;
        try {
            points = parser.parseCourse(screenX, screenY);
            boundary = parser.parseCourseBoundary(screenX, screenY);
        } catch (JDOMException e) {
            System.out.println("Invalid format for course XML file.");
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Failed to load file, please make sure path is correct.");
            e.printStackTrace();
            System.exit(1);
        }
        return new RaceCourse(points, boundary, parser.getWindDirection(),false);
    }

//    public static void main(String[] args){
//        CourseFactory cf=new CourseFactory();
//        cf.createCourse(1000.0,1000.0,"src/main/resources/mockXML/new_format_course.xml");
//    }
}
