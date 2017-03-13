package seng302;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by khe60 on 14/03/17.
 */
public class XMLParserTest {
    @Test
    public void parseCourse() throws Exception {
    File inputFile=new File("src/main/resources/test_course.xml");
    XMLParser parser=new XMLParser(inputFile);
    ArrayList<CoursePoint> points=new ArrayList<>();
    points.add(new Mark("Startline 1", new Point(32.296577,-64.854304)));
        points.add(new Mark("Startline 2", new Point(32.293771,-64.855242),true));
assertEquals(points, parser.parseCourse());
    }

}