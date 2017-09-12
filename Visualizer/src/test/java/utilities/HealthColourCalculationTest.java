package utilities;

import Elements.HealthBar;
import javafx.scene.paint.Color;
import models.Boat;
import models.Competitor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by psu43 on 10/08/17.
 * Test health colour calculations
 */
public class HealthColourCalculationTest {

    private Competitor boat;
    private HealthBar healthBar;


    @Before
    public void setUp() {
        boat = new Boat();
        boat.setMaxHealth(100);
        boat.setHealthLevel(100);
        healthBar = new HealthBar();
    }

    @Test
    public void calculateHealthColourGreenTest() {
        boat.updateHealth(-1);
        Color resultColour = healthBar.calculateHealthColour(boat.getHealthLevel(), boat.getMaxHealth());
        assertEquals(Color.GREEN, resultColour);
    }

    @Test
    public void calculateHealthColourGreenTest2() {
        boat.updateHealth(-29);
        Color resultColour = healthBar.calculateHealthColour(boat.getHealthLevel(), boat.getMaxHealth());
        assertEquals(Color.GREEN, resultColour);
    }


    @Test
    public void calculateHealthColourGreenYellowTest() {
        boat.updateHealth(-30);
        Color resultColour = healthBar.calculateHealthColour(boat.getHealthLevel(), boat.getMaxHealth());
        assertEquals(Color.GREENYELLOW, resultColour);
    }

    @Test
    public void calculateHealthColourGreenYellowTest2() {
        boat.updateHealth(-39);
        Color resultColour = healthBar.calculateHealthColour(boat.getHealthLevel(), boat.getMaxHealth());
        assertEquals(Color.GREENYELLOW, resultColour);
    }


    @Test
    public void calculateHealthColourYellowTest() {
        boat.updateHealth(-40);
        Color resultColour = healthBar.calculateHealthColour(boat.getHealthLevel(), boat.getMaxHealth());
        assertEquals(Color.YELLOW, resultColour);
    }

    @Test
    public void calculateHealthColourYellowTest2() {
        boat.updateHealth(-49);
        Color resultColour = healthBar.calculateHealthColour(boat.getHealthLevel(), boat.getMaxHealth());
        assertEquals(Color.YELLOW, resultColour);
    }



    @Test
    public void calculateHealthColourOrangeTest() {
        boat.updateHealth(-50);
        Color resultColour = healthBar.calculateHealthColour(boat.getHealthLevel(), boat.getMaxHealth());
        assertEquals(Color.ORANGE, resultColour);
    }

    @Test
    public void calculateHealthColourOrangeTest2() {
        boat.updateHealth(-59);
        Color resultColour = healthBar.calculateHealthColour(boat.getHealthLevel(), boat.getMaxHealth());
        assertEquals(Color.ORANGE, resultColour);
    }



    @Test
    public void calculateHealthColourRedTest() {
        boat.updateHealth(-60);
        Color resultColour = healthBar.calculateHealthColour(boat.getHealthLevel(), boat.getMaxHealth());
        assertEquals(Color.RED, resultColour);
    }

    @Test
    public void calculateHealthColourRedTest2() {
        boat.updateHealth(-100);
        Color resultColour = healthBar.calculateHealthColour(boat.getHealthLevel(), boat.getMaxHealth());
        assertEquals(Color.RED, resultColour);
    }
}
