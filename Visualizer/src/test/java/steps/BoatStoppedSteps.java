package steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Boat;
import models.Competitor;
import org.junit.Assert;

/**
 * Created by jar156 on 24/07/17.
 */
public class BoatStoppedSteps {

    Competitor boat = new Boat();


    @Given("^Boat has its sails out$")
    public void boatHasItsSailsOut() throws Throwable {
        Assert.assertTrue(boat.getSailValue() > 0);
    }

    @Given("^Boat is moving$")
    public void boatIsMoving() throws Throwable {
        double velocity = 1.0;
        boat.setVelocity(velocity);
        Assert.assertTrue(boat.getVelocity() > 0);
    }

    @When("^The sail slider is set to (\\d+)$")
    public void theSailSliderIsSetTo(int arg1) throws Throwable {

        for (int i = (int) boat.getSailValue(); i != 0; i--) {
            boat.sailsIn();
        }
        Assert.assertTrue(boat.getSailValue() == arg1);
    }


    @Then("^Boat stops moving$")
    public void boatStopsMoving() throws Throwable {
        Assert.assertTrue(boat.getVelocity() * boat.getSailValue() == 0);
    }

}
