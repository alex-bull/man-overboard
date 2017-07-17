package utility;

import static java.lang.Math.*;
import static java.lang.Math.sqrt;

/**
 * Created by psu43 on 17/07/17.
 * Calculating functions
 */
public class Calculator {

    /**
     * Calculates the distance in metres between a pair of coordinates.
     * @param latitude1 first point's latitude
     * @param longitude1 first point's longitude
     * @param latitude2 second point's latitude
     * @param longitude2 second point's longitude
     * @return double distance (m)
     */
    public double calcDistBetweenGPSPoints(double latitude1, double longitude1, double latitude2, double longitude2) {
        long earthRadius = 6371000;
        double phiStart = Math.toRadians(latitude2);
        double phiBoat = Math.toRadians(latitude1);

        double deltaPhi = Math.toRadians(latitude1 - latitude2);
        double deltaLambda = Math.toRadians(longitude1 - longitude2);

        double a = sin(deltaPhi / 2) * sin(deltaPhi / 2) + cos(phiStart) * cos(phiBoat) * sin(deltaLambda / 2) * sin(deltaLambda / 2);
        double c = 2 * atan2(sqrt(a), sqrt(1 - a));
        return earthRadius * c;
    }
}
