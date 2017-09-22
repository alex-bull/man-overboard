Feature: Boat Stopped

  Scenario: Boat stops moving when its sails are drawn in
    Given Boat has its sails out
    And Boat is moving
    When The sail slider is set to 0
    Then Boat stops moving