package steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Boat;
import models.Competitor;
import org.junit.Assert;

public class HealthUpSteps {
    Competitor boat = new Boat();

    @Given("^Boat does not have a potion$")
    public void boat_does_not_have_a_potion() throws Throwable {
        Assert.assertTrue(!boat.hasPotion());
    }

    @When("^The boat picks up the potion$")
    public void the_boat_picks_up_the_potion() throws Throwable {
        boat.enablePotion();
    }

    @Then("^The boat has a potion they can use$")
    public void the_boat_has_a_potion_they_can_use() throws Throwable {
        Assert.assertTrue(boat.hasPotion());
    }

    @Given("^Boat has already picked up a potion with a current health of (\\d+) and max health of (\\d+)$")
    public void boat_has_already_picked_up_a_potion_with_a_current_health_of_and_max_health_of(int currentHealth, int maxHealth) throws Throwable {
        boat.enablePotion();
        boat.setMaxHealth(maxHealth);
        boat.setHealthLevel(currentHealth);
        Assert.assertTrue(boat.hasPotion());

    }

    @When("^The boat uses the potion$")
    public void the_boat_uses_the_potion() throws Throwable {
        boat.usePotion();
        boat.updateHealth((int) boat.getMaxHealth()/2);
    }

    @Then("^The boat health is increased by (\\d+) and the boat has no more potion$")
    public void the_boat_health_is_increased_by_and_the_boat_has_no_more_potion(int result) throws Throwable {
        Assert.assertEquals(boat.getHealthLevel(), result, 0.01);
        Assert.assertTrue(!boat.hasPotion());
    }


}
