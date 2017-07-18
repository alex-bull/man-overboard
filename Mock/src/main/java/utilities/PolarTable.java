package utilities;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static utilities.Utility.fileToString;

/**
 * Created by khe60 on 19/07/17.
 * class to load polar table and do some interpolations
 */
public class PolarTable {

    private Map<Double,Map<Double,Double>> polar;

    /**
     * constructor to load the table given path to the file
     * @param filepath the path to the polar table
     */
    public PolarTable(String filepath) throws IOException {
        polar=new HashMap<>();

        String content=fileToString(filepath);
        String[] rows=content.split("\n");
        //ignore first row
        for(int i=1;i<rows.length;i++){
            String[] values=rows[i].split("\\s+");
            double windSpeed=Double.parseDouble(values[0]);
            Map<Double,Double> twaBspMap=new HashMap<>();

            for(int j=1;j<values.length;j+=2){
                twaBspMap.put(Double.parseDouble(values[j]),Double.parseDouble(values[j+1]));
            }
        polar.put(windSpeed,twaBspMap);
        }
    }

    public Map<Double, Map<Double, Double>> getPolar() {
        return polar;
    }

    /**
     * get the speed of the boat given wind speed and twa
     * @param speed the wind speed
     * @param twa the true wind angle
     * @return the speed of the boat under such condition
     */
    public double getSpeed(double speed, double twa){

        //currently uses the neareast neighbour approach to find the speed

    }
}
