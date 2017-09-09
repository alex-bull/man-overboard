package steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Boat;
import models.Competitor;
import org.junit.Assert;
import org.junit.Before;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by jar156 on 24/07/17.
 */
public class BoatStoppedSteps {

    Competitor boat = new Boat();

    @Given("^Boat is moving$")
    public void boat_is_moving() throws Throwable {
        double velocity = 5.0;
        boat.setVelocity(velocity);
        Assert.assertTrue(boat.getVelocity() > 0);
    }

    @Given("^Boat has its sails out$")
    public void boat_has_its_sails_out() throws Throwable {
        Assert.assertFalse(boat.hasSailsOut());
    }

    @When("^The sails in/out key is pressed$")
    public void the_sails_in_out_key_is_pressed() throws Throwable {
        boat.switchSails();
        boat.setVelocity(0);

    }

    @Then("^Boat stops moving$")
    public void boat_stops_moving() throws Throwable {
        Assert.assertTrue(boat.getVelocity() == 0);
    }

    @Then("^The sails are in$")
    public void the_sails_are_in() throws Throwable {
        Assert.assertTrue(boat.hasSailsOut());
    }

}
