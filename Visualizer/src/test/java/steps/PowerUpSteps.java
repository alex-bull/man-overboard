package steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Boat;
import models.Competitor;
import org.junit.Assert;

/**
 * Created by psu43 on 8/09/17.
 * Power Up steps
 */
public class PowerUpSteps {
    private Competitor boat = new Boat();

    @Given("^Boat does not have a power up$")
    public void boat_does_not_have_a_power_up() throws Throwable {    // Write code here that turns the phrase above into concrete actions
        boat.disableBoost();
        Assert.assertTrue(!boat.hasSpeedBoost());
    }

    @When("^The boat picks up the power up$")
    public void the_boat_picks_up_the_power_up() throws Throwable {
        boat.enableBoost();
    }

    @Then("^The boat has a power up they can use$")
    public void the_boat_has_a_power_up_they_can_use() throws Throwable {
        Assert.assertTrue(boat.hasSpeedBoost());
    }

    @Given("^Boat has already picked up a power up$")
    public void boat_has_already_picked_up_a_power_up() throws Throwable {
        boat.enableBoost();
        Assert.assertTrue(boat.hasSpeedBoost());
    }

    @When("^The boat uses the power up$")
    public void the_boat_uses_the_power_up() throws Throwable {
        boat.activateBoost();
    }

    @Then("^The boat boost is activated$")
    public void the_boat_boost_is_activated() throws Throwable {
        Assert.assertTrue(boat.boostActivated());
    }

    @Then("^The boat has no power ups$")
    public void the_boat_has_no_power_ups() throws Throwable {
        Assert.assertTrue(!boat.hasSpeedBoost());
    }


}
