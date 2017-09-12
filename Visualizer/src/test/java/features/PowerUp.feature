Feature: Boat uses a power up
  Scenario: Boat picks up a power up
    Given Boat does not have a power up
    When The boat picks up the power up
    Then The boat has a power up they can use

  Scenario: Boat uses a power up
    Given Boat has already picked up a power up
    When The boat uses the power up
    Then The boat boost is activated
    And The boat has no power ups
