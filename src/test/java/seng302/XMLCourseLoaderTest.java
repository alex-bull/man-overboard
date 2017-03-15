package seng302;

import org.junit.Test;
import seng302.Model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by khe60 on 14/03/17.
 */
public class XMLParserTest {
    @Test
    public void parseCourseTest() throws Exception {

    File inputFile=new File("src/main/resources/test_course.xml");
    XMLParser parser=new XMLParser(inputFile);
    ArrayList<CourseFeature> points=new ArrayList<>();
    points.add(new Gate("Startline", new MutablePoint(32.296577,-64.854304), new MutablePoint(32.293771,-64.855242), true));
    points.add(new Mark("Marker", new MutablePoint(32.293771,-64.855242),false));
    assertEquals(points, parser.parseCourse());
    }

}