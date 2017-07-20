Feature: Powered up
  Scenario: Boat is powered up when the player sails in at an angle to the apparent wind
    Given Boat is sailing in
    When Boat is at a slight angle to the wind
    Then Boat is powered up


Feature: Powered up
  Scenario: Boat is powered up when the player sails out at an angle to the apparent wind
    Given Boat is sailing out
    When Boat is at a slight angle to the wind
    Then Boat is powered up

