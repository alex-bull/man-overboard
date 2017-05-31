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


}
