package utilities;

import controllers.RaceViewController;
import org.junit.Before;
import org.junit.Test;
import models.MutablePoint;

import static java.lang.Math.*;
import static java.lang.Math.sqrt;
import static org.junit.Assert.assertTrue;

/**
 * Created by msl47 on 17/07/17.
 */
public class DistanceCalculationTest {

    private RaceCalculator raceCalculator;
    private MutablePoint startPoint;
    private MutablePoint endPoint;

    @Before
    public void setUp() {
        raceCalculator = new RaceCalculator();
        startPoint = new MutablePoint (10.0, 10.0);
        endPoint = new MutablePoint (20.0, 20.0);
    }

    @Test
    public void calcDistBetweenGPSPoints() {
        long earthRadius = 6371000;
        double lat1 = startPoint.getXValue();
        double long1 = startPoint.getYValue();
        double lat2 = endPoint.getXValue();
        double long2 = endPoint.getYValue();

        double phiStart = Math.toRadians(lat2);
        double phiBoat = Math.toRadians(lat1);

        double deltaPhi = Math.toRadians(lat1 - lat2);
        double deltaLambda = Math.toRadians(long1 - long2);

        double a = sin(deltaPhi / 2) * sin(deltaPhi / 2) + cos(phiStart) * cos(phiBoat) * sin(deltaLambda / 2) * sin(deltaLambda / 2);
        double c = 2 * atan2(sqrt(a), sqrt(1 - a));
        assertTrue(earthRadius * c == raceCalculator.calcDistBetweenGPSPoints(10.0, 10.0,20.0,20.0));

    }


}
