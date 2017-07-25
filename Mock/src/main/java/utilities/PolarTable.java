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
    private PolynomialSplineFunction upTwaFunc;
    private PolynomialSplineFunction downTwaFunc;


    /**
     * constructor to load the table given path to the file
     *
     * @param filepath the path to the polar table
     * @param speed double the speed
     */
    public PolarTable(String filepath, double speed) throws IOException {
        this.buildSpeedFunc(filepath, speed);
        this.buildTWAFuncs(filepath);
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
     * Put file data into interpolators for up and down twas
     * @param filePath String, the location of the polar file
     * @throws IOException
     */
    private void buildTWAFuncs(String filePath) throws IOException {
        String content = fileToString(filePath);
        String rows[] = content.split("\n");
        double[] windVals = new double[rows.length];
        double[] upTwaVals = new double[rows.length];
        double[] downTwaVals = new double[rows.length];

        for (int i = 1; i < rows.length; i++) {
            String[] values = rows[i].split("\\s+");

            windVals[i] = Double.parseDouble(values[0]);
            upTwaVals[i] = Double.parseDouble(values[3]);
            downTwaVals[i] = Double.parseDouble(values[13]);
        }

        SplineInterpolator splineInterpolator = new SplineInterpolator();
        upTwaFunc = splineInterpolator.interpolate(windVals, upTwaVals);
        downTwaFunc = splineInterpolator.interpolate(windVals, downTwaVals);

    }


    /**
     * Calculates the twa value that corresponds to the lowest boat speed above 0
     * @param windSpeed The current wind speed
     * @return double, the twa
     */
    public double getMinimalTwa(double windSpeed, boolean upwind) {
        if (upwind) {
            return upTwaFunc.value(windSpeed);
        }
        return downTwaFunc.value(windSpeed);
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
