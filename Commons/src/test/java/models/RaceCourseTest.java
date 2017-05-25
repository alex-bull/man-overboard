package models;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Tests for the RaceCourse Model
 */
public class RaceCourseTest {
    @Test
    public void distanceBetweenGPSPoints() throws Exception {
        Course raceCourse = new RaceCourse(new ArrayList<>(), false);

        MutablePoint point1 = new MutablePoint(0.0, 0.0);
        MutablePoint point2 = new MutablePoint(1.0, 2.0);
        MutablePoint point3 = new MutablePoint(34.45, 43.9473);
        MutablePoint point4 = new MutablePoint(-99.999, -0.2460);
        MutablePoint point5 = new MutablePoint(-87.0, 34.455);

        Assert.assertEquals(248600, raceCourse.distanceBetweenGPSPoints(point1, point2), 100);
        Assert.assertEquals(5721700, raceCourse.distanceBetweenGPSPoints(point2, point3), 100);
        Assert.assertEquals(14597700, raceCourse.distanceBetweenGPSPoints(point3, point4), 100);
        Assert.assertEquals(1398900, raceCourse.distanceBetweenGPSPoints(point4, point5), 100);
    }

}