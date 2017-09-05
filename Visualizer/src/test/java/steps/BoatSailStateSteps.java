package steps;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Boat;
import models.Competitor;
import org.junit.Assert;

/**
 * Created by Izzy on 5/09/17.
 */
public class BoatSailStateSteps {

    Competitor boat = new Boat();

    @Given("^the boat has their sails (\\d+)$")
    public void theBoatHasTheirSails(boolean out) throws Throwable {
        if(out){ Assert.assertTrue(boat.getSailValue() > 0); }
        else { Assert.assertTrue(boat.getSailValue() == 0); }
    }

    @Given("^the boat's velocity is (\\d+)$")
    public void theBoatSVelocityIs(int velocity) throws Throwable {
        boat.setVelocity(velocity);
    }

    @When("^the boat's sail slider is set at (\\d+)$")
    public void theBoatSSailSliderIsSetAt(int sailSlider) throws Throwable {
        boat.setSailValue(sailSlider);
    }

    @Then("^the boat's speed multiplier should be (\\d+)$")
    public void theBoatSSpeedMultiplierShouldBe(int sailValue) throws Throwable {
        boat.getSailValue();
        Assert.assertTrue(sailValue == boat.getSailValue());

    }

    @Then("^the boat's total speed should be (\\d+)$")
    public void theBoatSTotalSpeedShouldBe(int speed) throws Throwable {
        Assert.assertTrue(boat.getVelocity() * boat.getSailValue() == speed);
    }

}
