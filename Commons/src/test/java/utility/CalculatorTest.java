package utility;

import models.MutablePoint;
import org.junit.Test;

import static java.lang.Math.atan;
import static java.lang.Math.toDegrees;
import static org.junit.Assert.assertEquals;
import static utility.Calculator.calcAngleBetweenPoints;
import static utility.Calculator.convertRadiansToShort;
import static utility.Calculator.shortToDegrees;

/**
 * Created by psu43 on 17/07/17.
 * Testing for Calculator
 */
public class CalculatorTest {


    @Test
    public void calculateAngleBetweenTwoPoints() {
        double calculatedRadians = calcAngleBetweenPoints(50, 0 , 50, 0);
        assertEquals(calculatedRadians,Math.PI,0.01);

        calculatedRadians = calcAngleBetweenPoints(0, 0 , 1, 1);
        assertEquals(calculatedRadians,Math.PI/4,0.01);

        calculatedRadians = calcAngleBetweenPoints(0, 1 , 0, 1);
        assertEquals(calculatedRadians,Math.PI,0.01);

        calculatedRadians = calcAngleBetweenPoints(0, 1 , 2, 2);
        assertEquals(calculatedRadians,atan(0.5),0.01);

    }
    @Test
    public void convertRadiansToShortRange() {
        short calculatedShort = convertRadiansToShort(0.0);
        assertEquals(calculatedShort, -32768);

        calculatedShort = convertRadiansToShort(0.5);
        assertEquals(calculatedShort, -27552);

        calculatedShort = convertRadiansToShort(3.12);
        assertEquals(calculatedShort, -225);

        calculatedShort = convertRadiansToShort(-1);
        assertEquals(calculatedShort, 22338);
    }

    @Test
    public void shortToDegreesTest() {
        assertEquals(shortToDegrees((short) 0 ), 180, 0.01);
        assertEquals(shortToDegrees((short) -32768 ), 0, 0.01);
        assertEquals(shortToDegrees((short)-27552), toDegrees(0.5), 0.01);

    }



}
