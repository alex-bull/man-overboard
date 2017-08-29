Feature: An arrow on screen shows the player how to round the next mark

  Scenario: The player is sailing at any point in the race
    Given: I am playing the game
    When: I have not finished the race
    Then: An arrow on the course shows how to round the next mark


  Scenario: Pre-start
    Given: I am playing the game
    When: I my next gate is the start gate
    Then: There is an arrow showing to go through the start gate

  Scenario: The player rounds a mark
    Given: I am playing the game
    When: I round a mark
    Then: The arrow moves to show how to round the next mark

  Scenario: The player passes the finish
    Given: I am playing the game
    When: I cross the finish
    Then: The arrow on screen disappears
