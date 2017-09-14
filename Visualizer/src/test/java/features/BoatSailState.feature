Feature: Boat Sails Out
  Scenario Outline: Boat changes speed depending on the state of the sail slider
    Given the boat has their sails <Out>
    And the boat's velocity is <Velocity>
    When the boat's sail slider is set at <SailSlider>
    Then the boat's speed multiplier should be <BoatMultiplier>
    And the boat's total speed should be <BoatSpeed>


  Examples:
    | Out  | Velocity | SailSlider | BoatMultiplier  | BoatSpeed |
    | true    | 5        | 10         | 10              | 50        |
    | true    | 5        | 5          | 5               | 25        |
    | false    | 5        | 0          | 0               |  0        |
