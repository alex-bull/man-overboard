package steps;

import Elements.HealthBar;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import models.Boat;
import models.Competitor;

import static org.junit.Assert.assertEquals;

/**
 * Created by psu43 on 10/08/17.
 * Testing Health Bar
 */
public class HealthBarSteps {
    private Competitor boat=new Boat();
    private HealthBar healthBar = new HealthBar();

    @Given("^the max health is (\\d+)$")
    public void the_max_health_is(int maxHealth) throws Throwable {
        boat.setMaxHealth(maxHealth);
    }

    @Given("^the current health is (\\d+)$")
    public void the_current_health_is(int currentHealth) throws Throwable {
        boat.setHealthLevel(currentHealth);
    }

    @When("^the player is in a collision of damage (\\d+)$")
    public void the_player_is_in_a_collision_of_damage(int damage) throws Throwable {
        boat.updateHealth(-damage);
    }



    @Then("^the boat's health should be (\\d+)$")
    public void the_boat_s_health_should_be(int resultHealth) throws Throwable {
        assertEquals(resultHealth, boat.getHealthLevel(),0.01);
    }

    @Then("^the colour of the health bar should be GREEN$")
    public void the_colour_of_the_health_bar_should_be_GREEN() throws Throwable {
        Color result = healthBar.calculateHealthColour(boat.getHealthLevel(), boat.getMaxHealth());
        assertEquals(Color.GREEN, result);
    }
    @Then("^the colour of the health bar should be GREENYELLOW$")
    public void the_colour_of_the_health_bar_should_be_GREENYELLOW() throws Throwable {
        Color result = healthBar.calculateHealthColour(boat.getHealthLevel(), boat.getMaxHealth());
        assertEquals(Color.GREENYELLOW, result);
    }

    @Then("^the colour of the health bar should be YELLOW$")
    public void the_colour_of_the_health_bar_should_be_YELLOW() throws Throwable {
        Color result = healthBar.calculateHealthColour(boat.getHealthLevel(), boat.getMaxHealth());
        assertEquals(Color.YELLOW, result);
    }

    @Then("^the colour of the health bar should be ORANGE$")
    public void the_colour_of_the_health_bar_should_be_ORANGE() throws Throwable {
        Color result = healthBar.calculateHealthColour(boat.getHealthLevel(), boat.getMaxHealth());
        assertEquals(Color.ORANGE, result);
    }

    @Then("^the colour of the health bar should be RED$")
    public void the_colour_of_the_health_bar_should_be_RED() throws Throwable {
        Color result = healthBar.calculateHealthColour(boat.getHealthLevel(), boat.getMaxHealth());
        assertEquals(Color.RED, result);
    }
}
