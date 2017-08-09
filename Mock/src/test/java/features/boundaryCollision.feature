Feature:  The boat collides with the course boundary
  Scenario: The player is playing the game, and the player crashes into the course boundary
    Given boat's current position is (x1, y1)
    And boundary position is (x2, y2)
    When the boat collides with the course boundary
    Then boat gets knocked back


