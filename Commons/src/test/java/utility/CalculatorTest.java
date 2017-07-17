package utility;

import org.junit.Test;

import static java.lang.Math.atan;
import static org.junit.Assert.assertEquals;
import static utility.Calculator.calcAngleBetweenPoints;
import static utility.Calculator.convertRadiansToShort;

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


}
