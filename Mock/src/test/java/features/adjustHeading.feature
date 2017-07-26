Feature:  The boats collide realistically with each other
    Scenario Outline: The player is playing the game, and he/she crashes into another boat
    Given boat1's heading is <heading1>
      And boat1's velocity is <v1>
      And boat1 is at <x1>,<y1>
      And boat2's heading is <heading2>
      And boat2's velocity is <v2>
      And boat2 is at <x2>,<y2>
      When the two boats collide
    Then boat1's velocity is <v1x>,<v1y>, boat2's velocity is <v2x>,<v2y>
      Examples:
        | heading1 | v1 | x1 | y1 | heading2 | v2 | x2 | y2 | v1x | v1y | v2x | v2y |


