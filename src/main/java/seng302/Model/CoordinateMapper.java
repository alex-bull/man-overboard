package seng302.Model;

import java.util.ArrayList;

/**
 * Created by psu43 on 23/03/17.
 * Calculation utilities for mapping coordinates
 */
public class CoordinateMapper {

    /**
     * Function to map latitude and longitude to screen coordinates
     * @param lat latitude
     * @param lon longitude
     * @param width width of the screen
     * @param height height of the screen
     * @return ArrayList the coordinates in metres
     */
    public ArrayList<Double> mercatorProjection(double lat, double lon, double width, double height){
        ArrayList<Double> ret=new ArrayList<>();
        double x = (lon+180)*(width/360);
        double latRad = lat*Math.PI/180;
        double merc = Math.log(Math.tan(Math.PI/4)+(latRad/2));
        double y = (height/2)-(width*merc/(2*Math.PI));
        ret.add(x);
        ret.add(y);
        return ret;

    }




//    /**
//     * Calculates pixels to metres given two course points coordinates
//     * @param xPixelCoords ArrayList
//     * @param yPixelCoords ArrayList
//     * @param xGPSCoords ArrayList
//     * @param yGPSCoords ArrayList
//     * @return double
//     */
//    public double calculatePixelsToMetres(ArrayList<Double> xPixelCoords, ArrayList<Double> yPixelCoords,
//                                          ArrayList<Double> xGPSCoords, ArrayList<Double> yGPSCoords) {
//        // get the first two points
//        double x1 = xGPSCoords.get(0);
//        double x2 = xGPSCoords.get(1);
//        double y1 = yGPSCoords.get(0);
//        double y2 = yGPSCoords.get(1);
//
//        // calc dist between the first two points
//        double distanceMetres = distanceBetweenGPSPoints(x1, x2, y1, y2);
//
//        // get the first two mercator coords
//        double xm1 = xPixelCoords.get(0);
//        double xm2 = xPixelCoords.get(1);
//        double ym1 = yPixelCoords.get(0);
//        double ym2 = yPixelCoords.get(1);
//
//        // calc dist between first two mercator points
//        double distancePixels = distanceBetweenGPSPoints(xm1, xm2, ym1, ym2);
//
//        System.out.println("dist metres " + Double.toString(distanceMetres));
//        System.out.println("dist pixels " + Double.toString(distancePixels));
//
//        // find factor pixels to metres
//        double pixelsToMetres = distanceMetres/distancePixels;
//        return pixelsToMetres;
//    }

}
