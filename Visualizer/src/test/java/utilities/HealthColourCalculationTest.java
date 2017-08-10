package utilities;

import javafx.scene.paint.Color;
import models.Boat;
import models.Competitor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static utilities.RaceCalculator.calculateHealthColour;

/**
 * Created by psu43 on 10/08/17.
 * Test health colour calculations
 */
public class HealthColourCalculationTest {

    private Competitor boat;


    @Before
    public void setUp() {
        boat = new Boat();
        boat.setMaxHealth(100);
        boat.setHealthLevel(100);
    }

    @Test
    public void calculateHealthColourGreenTest() {
        boat.decreaseHealth(1);
        Color resultColour = calculateHealthColour(boat);
        assertEquals(Color.GREEN, resultColour);
    }

    @Test
    public void calculateHealthColourGreenTest2() {
        boat.decreaseHealth(29);
        Color resultColour = calculateHealthColour(boat);
        assertEquals(Color.GREEN, resultColour);
    }


    @Test
    public void calculateHealthColourGreenYellowTest() {
        boat.decreaseHealth(30);
        Color resultColour = calculateHealthColour(boat);
        assertEquals(Color.GREENYELLOW, resultColour);
    }

    @Test
    public void calculateHealthColourGreenYellowTest2() {
        boat.decreaseHealth(39);
        Color resultColour = calculateHealthColour(boat);
        assertEquals(Color.GREENYELLOW, resultColour);
    }


    @Test
    public void calculateHealthColourYellowTest() {
        boat.decreaseHealth(40);
        Color resultColour = calculateHealthColour(boat);
        assertEquals(Color.YELLOW, resultColour);
    }

    @Test
    public void calculateHealthColourYellowTest2() {
        boat.decreaseHealth(49);
        Color resultColour = calculateHealthColour(boat);
        assertEquals(Color.YELLOW, resultColour);
    }



    @Test
    public void calculateHealthColourOrangeTest() {
        boat.decreaseHealth(50);
        Color resultColour = calculateHealthColour(boat);
        assertEquals(Color.ORANGE, resultColour);
    }

    @Test
    public void calculateHealthColourOrangeTest2() {
        boat.decreaseHealth(59);
        Color resultColour = calculateHealthColour(boat);
        assertEquals(Color.ORANGE, resultColour);
    }



    @Test
    public void calculateHealthColourRedTest() {
        boat.decreaseHealth(60);
        Color resultColour = calculateHealthColour(boat);
        assertEquals(Color.RED, resultColour);
    }

    @Test
    public void calculateHealthColourRedTest2() {
        boat.decreaseHealth(100);
        Color resultColour = calculateHealthColour(boat);
        assertEquals(Color.RED, resultColour);
    }
}
