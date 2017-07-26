Feature:  The boat's heading is adjustable by the player
    Scenario Outline: The player is playing the game, and he/she wishes to adjust the heading of his/her boat
    Given the current heading is <CurHeading> degrees
      And the wind direction is <WindDir> degrees
    When the player presses the <Key> key
    Then the boat's heading should be <ChangedHeading>
      Examples:
        | CurHeading | WindDir | Key |ChangedHeading |
        | 200        |300      |up   |203            |
        | 200        |300      |down |197            |
        | 200        |100      |up   |197            |
        | 200        |100      |down |203            |
        | 200        |20       |down |203            |
        | 0          |31       |down |357            |
        |358         |31       |down |355            |
        |1           |31       |down |358            |
        |270         |31       |down |267            |



