package utilities;

import models.MutablePoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by msl47 on 7/08/17.
 */
public class CollisionUtility {

    private List<List<MutablePoint>> courseBoundaryPairs = new ArrayList<>();
    private List<MutablePoint> courseLineEquations = new ArrayList<>();
    private List<MutablePoint> courseBoundary;


    public void setCourseBoundary(List<MutablePoint> list) {
        this.courseBoundary = list;
    }

    public List<MutablePoint> getCourseLineEquations() {
        return this.courseLineEquations;
    }

    /**
     * Checks whether the boat is between the two boundary edges
     * @param boatPosition MutablePoint boat's position
     * @param index int iterator of the list
     * @return Boolean result
     */
    public boolean isWithinBoundaryLines(MutablePoint boatPosition, int index) {
        double x = boatPosition.getXValue();
        double y = boatPosition.getYValue();
        MutablePoint pair1 = courseBoundaryPairs.get(index).get(0);
        MutablePoint pair2 = courseBoundaryPairs.get(index).get(1);
        return (y < max(pair1.getYValue(), pair2.getYValue()) &&
                y > min(pair1.getYValue(), pair2.getYValue()) &&
                x < max(pair1.getXValue(), pair2.getXValue()) &&
                x > min(pair1.getXValue(), pair2.getXValue()));
    }

    /**
     * Gets a line equation of each boundary edges and store them in courseLineEquations
     * and also stores the pair of the edges into courseBoundaryPairs.
     */
    public void setCourseInformation() {
        MutablePoint pt1;
        MutablePoint pt2;
        for (int i=0; i <courseBoundary.size(); i++) {
            if (i == courseBoundary.size()-1) {
                pt1 = courseBoundary.get(i);
                pt2 = courseBoundary.get(0);
            }
            else {
                pt1 = courseBoundary.get(i);
                pt2 = courseBoundary.get(i + 1);
            }
            double x1 = pt1.getXValue();
            double y1 = pt1.getYValue();
            double x2 = pt2.getXValue();
            double y2 = pt2.getYValue();
            double slope = (y2 - y1)/(x2 - x1);
            double c = y1 - slope * x1;
            MutablePoint lineEquation = new MutablePoint(slope, c);
            List<MutablePoint> pairs = Arrays.asList(pt1, pt2);
            courseBoundaryPairs.add(pairs);
            courseLineEquations.add(lineEquation);
        }
    }

}
