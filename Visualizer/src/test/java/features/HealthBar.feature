Feature:  The boat's health bar decreases when the boat collides
  Scenario Outline: The player is playing the game, and he/she crashes into an object
    Given the max health is <MaxHealth>
    And the current health is <CurrHealth>
    When the player is in a collision of damage <Threshold>
    Then the boat's health should be <ResultHealth>
    And the colour of the health bar should be <Colour>
    Examples:
      | MaxHealth | CurrHealth | Threshold |ResultHealth | Colour       |
      | 30        |30          |5          |25           | GREEN        |
      | 30        |22          |0          |22           | GREEN        |
      | 100       |80          |9          |71           | GREEN        |
      | 100       |80          |10         |70           | GREENYELLOW  |
      | 100       |80          |11         |69           | GREENYELLOW  |
      | 20        |15          |1          |14           | GREENYELLOW  |
      | 100       |60          |5          |55           | YELLOW       |
      | 100       |80          |21         |59           | YELLOW       |
      | 100       |60          |9          |51           | YELLOW       |
      | 100       |80          |31         |49           | ORANGE       |
      | 100       |60          |19         |41           | ORANGE       |
      | 100       |90          |51         |39           | RED          |
      | 30        |30          |30         |0            | RED          |