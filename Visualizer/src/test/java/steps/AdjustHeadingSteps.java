package steps;

import controllers.App;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mockDatafeed.BoatMocker;
import models.Boat;
import models.Competitor;

import static org.junit.Assert.assertEquals;

/**
 * Created by khe60 on 21/07/17.
 */
public class AdjustHeadingSteps {
    Competitor boat=new Boat();
    double windAngle;

    @Given("^the current heading is (\\d+) degrees$")
    public void theCurrentHeadingIsDegrees(int heading) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        boat.setCurrentHeading(heading);
    }

    @Given("^the wind direction is (\\d+) degrees$")
    public void theWindDirectionIsDegrees(int windAngle) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        this.windAngle=windAngle;
    }

    @When("^the player presses the up key$")
    public void thePlayerPressesTheUpKey() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        boat.changeHeading(true,windAngle);
    }

    @Then("^the boat's heading should be (\\d+)$")
    public void theBoatSHeadingShouldBe(int heading) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        assertEquals(heading, boat.getCurrentHeading(),0.01);
    }

    @When("^the player presses the down key$")
    public void thePlayerPressesTheDownKey() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        boat.changeHeading(false,windAngle);
    }
}
