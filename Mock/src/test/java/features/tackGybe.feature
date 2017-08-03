Feature:  The boats tack and gybe
  Scenario Outline: The player is playing the game, and he/she presses the tack key
    Given the current heading is <CurHeading> degrees
    And the wind direction is <WindDir> degrees
    When the player presses the <Key> key
    Then the boat's heading should be <ChangedHeading>
    Examples:
      | CurHeading | WindDir | Key    |ChangedHeading |
      | 0          |45       |enter   |90             |
      | 90         |45       |enter   |0              |
      | 180        |45       |enter   |270            |
      | 270        |45       |enter   |180            |