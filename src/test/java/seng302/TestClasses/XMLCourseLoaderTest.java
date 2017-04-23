package seng302.TestClasses;

import org.junit.Assert;
import org.junit.Test;
import seng302.Model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by khe60 on 14/03/17.
 */
public class XMLCourseLoaderTest {
    @Test
    public void parseCourseTest() throws Exception {
        File inputFile = new File("src/main/resources/test_course.xml");
        XMLCourseLoader parser = new XMLCourseLoader(inputFile);

        CourseFeature point1 = new Gate("Startline",
                new MutablePoint(32.296577,-64.854304),
                new MutablePoint(32.293771,-64.855242),
                new MutablePoint(559.985, 51.2),
                new MutablePoint(559.985, 51.2),
                true, true, 0);

        CourseFeature point2 = new Mark("Marker", new MutablePoint(1176.0, 301.01), new MutablePoint(32.293039,-64.843983), 1);


        List<CourseFeature> features = parser.parseCourse(1680,1024);
        CourseFeature pointa = features.get(0);
        CourseFeature pointb = features.get(1);

        Assert.assertEquals(point1.getGPSCentre().getXValue(), pointa.getGPSCentre().getXValue(), 0.0001);
        Assert.assertEquals(point1.getGPSCentre().getYValue(), pointa.getGPSCentre().getYValue(), 0.0001);
        Assert.assertEquals(point2.getGPSCentre().getXValue(), pointb.getGPSCentre().getXValue(), 0.0001);
        Assert.assertEquals(point2.getGPSCentre().getYValue(), pointb.getGPSCentre().getYValue(), 0.0001);


        Assert.assertEquals(point1.getPixelLocations().get(0).getXValue(), pointa.getPixelLocations().get(0).getXValue(), 0.01);
        Assert.assertEquals(point1.getPixelLocations().get(0).getYValue(), pointa.getPixelLocations().get(0).getYValue(), 0.01);
        Assert.assertEquals(point1.getPixelLocations().get(1).getXValue(), pointa.getPixelLocations().get(0).getXValue(), 0.01);
        Assert.assertEquals(point1.getPixelLocations().get(1).getYValue(), pointa.getPixelLocations().get(0).getYValue(), 0.01);

        Assert.assertEquals(point2.getPixelLocations().get(0).getXValue(), pointb.getPixelLocations().get(0).getXValue(), 0.01);
        Assert.assertEquals(point2.getPixelLocations().get(0).getYValue(), pointb.getPixelLocations().get(0).getYValue(), 0.01);


    }




}