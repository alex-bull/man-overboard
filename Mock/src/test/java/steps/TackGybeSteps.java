package steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Boat;
import models.Competitor;

import static org.junit.Assert.assertEquals;

/**
 * Created by ikj11 on 2/08/17.
 */
public class TackGybeSteps {
    Competitor boat = new Boat();
    double windAngle;
    boolean upWind;
    int quadrant;
    boolean clockwise;


    @Given("^the current heading is (\\d+) degrees$")
    public void theCurrentHeadingIsDegrees(int heading) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        boat.setCurrentHeading(heading);
    }

    @Given("^the wind direction is (\\d+) degrees$")
    public void theWindDirectionIsDegrees(int windAngle) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        this.windAngle = windAngle;
    }

    @When("^the player presses the enter key$")
    public void thePlayerPressesTheEnterKey() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        boat.setCurrentHeading(windAngle - (boat.getCurrentHeading() - windAngle));
    }

    @Then("^the boat's heading should be (\\d+)$")
    public void theBoatSHeadingShouldBe(int heading) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        assertEquals(heading, boat.getCurrentHeading(), 0.01);
    }

    @Given("^the wind direction is (\\d+)$")
    public void theWindDirectionIs(int windAngle) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        this.windAngle = windAngle;
    }

    @Given("^the boat is travelling upwind$")
    public void theBoatIsTravellingUpwind() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        this.upWind = !(boat.getCurrentHeading() > windAngle + 90 && boat.getCurrentHeading() < windAngle + 270);
    }

    @Given("^the (\\d+) is between (\\d+)$")
    public void theIsBetween(int heading, int windAngle) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        if (this.upWind && heading > windAngle) {
            this.quadrant = 1;
        } else if (this.upWind && (heading < windAngle || heading > windAngle + 270)) {
            this.quadrant = 0;
        } else if (!this.upWind && heading < windAngle + 180) {
            this.quadrant = 4;
        } else {
            this.quadrant = 3;
        }
    }


    @Then("^the boat rotation should be anticlockwise$")
    public void theBoatRotationShouldBeAnticlockwise() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        this.clockwise = !(this.quadrant == 1 || this.quadrant == 3);
    }

    @Then("^the boat rotation should be clockwise$")
    public void theBoatRotationShouldBeClockwise() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        this.clockwise = (this.quadrant == 0 || this.quadrant == 4);
    }

    @Given("^the boat is travelling downwind$")
    public void theBoatIsTravellingDownwind() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        this.upWind = ((boat.getCurrentHeading() > windAngle + 90 && boat.getCurrentHeading() > 0) ||
                boat.getCurrentHeading() < windAngle + 270 && boat.getCurrentHeading() < 360);
    }

}
