package utilities;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.io.IOException;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Map;

import static utilities.Utility.fileToString;

/**
 * Created by khe60 on 19/07/17.
 * class to load polar table and do some interpolations
 */
public class PolarTable {

    private PolynomialSplineFunction speedFunc;
    private PolynomialSplineFunction twaFunc;


    /**
     * constructor to load the table given path to the file
     *
     * @param filepath the path to the polar table
     * @param speed double the speed
     */
    public PolarTable(String filepath, double speed) throws IOException {
        this.buildSpeedFunc(filepath, speed);
        this.buildTWAFunc(filepath);
    }


    /**
     * Put file data into interpolator for speed
     * @param filePath String, the polar file
     * @param speed double the windSpeed
     * @throws IOException
     */
    private void buildSpeedFunc(String filePath, double speed) throws IOException {

        Map<Double, ArrayContainer> polar = new HashMap<>();
        String content = fileToString(filePath);
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
        speedFunc = splineInterpolator.interpolate(data.getX(), data.getY());
    }


    /**
     * Put file data into interpolator for twa
     * @param filePath String, the location of the polar file
     * @throws IOException
     */
    private void buildTWAFunc(String filePath) throws IOException {
        String content = fileToString(filePath);
        String rows[] = content.split("\n");
        double[] windVals = new double[rows.length];
        double[] twaVals = new double [rows.length];

        for (int i = 1; i < rows.length; i++) {
            String[] values = rows[i].split("\\s+");

            double windSpeed = Double.parseDouble(values[0]);
            windVals[i] = windSpeed;
            double twa = Double.parseDouble(values[3]);
            twaVals[i] = twa;
        }

        SplineInterpolator splineInterpolator = new SplineInterpolator();
        twaFunc = splineInterpolator.interpolate(windVals, twaVals);

    }


    /**
     * Calculates the twa value that corresponds to the lowest boat speed above 0
     * @param windSpeed The current wind speed
     * @return double, the twa
     */
    public double getMinimalTwa(double windSpeed) {

        return twaFunc.value(windSpeed);
    }


    /**
     * get the speed of the boat given twa
     *
     * @param twa the true wind angle
     * @return the speed of the boat under such condition
     */
    public double getSpeed(double twa) {
        //currently uses the neareast neighbour approach to find the speed
        return speedFunc.value(twa);
    }
}
