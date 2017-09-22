Feature:  The boats tack and gybe

  Scenario Outline: The player is playing the game, and he/she presses the tack key
    Given the current heading is <CurHeading> degrees
    And the wind direction is <WindDir> degrees
    When the player presses the <Key> key
    Then the boat's heading should be <ChangedHeading>
    Examples:
      | CurHeading | WindDir | Key   | ChangedHeading |
      | 0          | 45      | enter | 90             |
      | 90         | 45      | enter | 0              |
      | 180        | 45      | enter | 270            |
      | 270        | 45      | enter | 180            |

  Scenario Outline: the player's boat rotates the correct direction when tacking/gybing
    Given the current heading is <CurHeading> degrees
    And the wind direction is <WindDir>
    And the boat is travelling <Upwind/Downwind>
    And the <CurHeading> is between <WindDir>
    When the player presses the <Key> key
    Then the boat rotation should be <Anti/Clockwise>
    Examples:
      | Upwind/Downwind | CurHeading | WindDir | Key   | Anti/Clockwise |
      | upwind          | 80         | 45      | enter | anticlockwise  |
      | upwind          | 10         | 45      | enter | clockwise      |
      | downwind        | 200        | 225     | enter | clockwise      |
      | downwind        | 240        | 225     | enter | anticlockwise  |

