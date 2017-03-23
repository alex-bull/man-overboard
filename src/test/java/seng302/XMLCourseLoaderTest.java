package seng302;

import org.junit.Test;
import seng302.Model.*;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by khe60 on 14/03/17.
 */
public class XMLCourseLoaderTest {
    @Test
    public void parseCourseTest() throws Exception {

    File inputFile=new File("src/main/resources/test_course.xml");
    XMLCourseLoader parser=new XMLCourseLoader(inputFile, 1.0, 1.0);
    ArrayList<CourseFeature> points=new ArrayList<>();
//    points.add(new Gate("Startline", new MutablePoint(32.296577,-64.854304), new MutablePoint(32.293771,-64.855242), true, true));
//    points.add(new Mark("Marker", new MutablePoint(32.293771,-64.855242),false));
//    assertEquals(points, parser.parseCourse(1680,1024));
    }

}