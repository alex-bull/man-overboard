Feature: Boat Stopped
  Scenario: Boat stops moving when its sails are drawn in
    Given Boat has its sails out
    And Boat is moving
    When The sails in/out key is pressed
    Then The sails are in
    And Boat stops moving