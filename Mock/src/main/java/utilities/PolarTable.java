package utilities;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static utilities.Utility.fileToString;

/**
 * Created by khe60 on 19/07/17.
 * class to load polar table and do some interpolations
 */
public class PolarTable {

    private PolynomialSplineFunction func;

    /**
     * constructor to load the table given path to the file
     *
     * @param filepath the path to the polar table
     * @param speed    double the speed
     */
    public PolarTable(String filepath, double speed) throws IOException {
        Map<Double, ArrayContainer> polar = new HashMap<>();
        String content = fileToString(filepath);
        String[] rows = content.split("\n");
        //ignore first row
        for (int i = 1; i < rows.length; i++) {
            String[] values = rows[i].split("\\s+");

            double windSpeed = Double.parseDouble(values[0]);
            double[] x = new double[(values.length - 1) / 2];
            double[] y = new double[(values.length - 1) / 2];

            for (int j = 0; j < x.length; j++) {
                x[j] = Double.parseDouble(values[j * 2 + 1]);
                y[j] = Double.parseDouble(values[j * 2 + 2]);
            }
            polar.put(windSpeed, new ArrayContainer(x, y));

        }

        ArrayContainer data = polar.get(speed);
        SplineInterpolator splineInterpolator = new SplineInterpolator();
        func = splineInterpolator.interpolate(data.getX(), data.getY());
    }


    /**
     * get the speed of the boat given twa
     *
     * @param twa the true wind angle
     * @return the speed of the boat under such condition
     */
    public double getSpeed(double twa) {
        //currently uses the neareast neighbour approach to find the speed
        return func.value(twa);
    }
}
