package utilities;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ikj11 on 25/05/17.
 */
public class RaceCalculatorTest {

    private RaceCalculator raceCalculator = new RaceCalculator();

    @Test
    public void testCalculateStartSymbol(){
        String testNegative = raceCalculator.calculateStartSymbol(200, 250, 10.0, 30);
        Assert.assertEquals("-", testNegative);
        String testPositive = raceCalculator.calculateStartSymbol(200, 250, 10.0, 5);
        Assert.assertEquals("+", testPositive);
        String testOnTime = raceCalculator.calculateStartSymbol(200, 250, 10.0, 20);
        Assert.assertEquals("", testOnTime);
        String testWithinBound = raceCalculator.calculateStartSymbol(200, 250, 10.0, 18);
        Assert.assertEquals("", testWithinBound);
    }

    @Test
    public void testCalculateBoatDirection() {
        double theta = raceCalculator.calcBoatDirection(10, 10, 20, 20);
        Assert.assertEquals(135.0, theta, 0.1);
    }

    @Test
    public void testisWestOfWind() {
        boolean isNotWestOfWind = raceCalculator.isWestOfWind(50, 100, 20);
        Assert.assertEquals(false, isNotWestOfWind);
        boolean isWestOfWind = raceCalculator.isWestOfWind(130, 100, 20);
        Assert.assertEquals(true, isWestOfWind);


    }


}
