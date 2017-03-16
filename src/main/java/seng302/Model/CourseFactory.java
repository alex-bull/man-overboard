package seng302.Model;

import org.jdom2.JDOMException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by psu43 on 15/03/17.
 * A factory for the course points
 */
public class CourseFactory {

    public Course createCourse(Double screenX, Double screenY){
        //create the marks
        File inputFile=new File("src/main/resources/course.xml");
        XMLCourseLoader parser=new XMLCourseLoader(inputFile, screenX, screenY);
        List<CourseFeature> points = null;
        try {
            points = parser.parseCourse(screenX, screenY);
        } catch (JDOMException e) {
            System.out.println("XML file format error");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("file not found or something");
            e.printStackTrace();
        }
        return new RaceCourse(points);

    }
}
