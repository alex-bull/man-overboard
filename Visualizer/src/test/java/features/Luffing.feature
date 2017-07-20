Feature: Luffing
  Scenario: Boat is luffing when the player sails in and it is in line with the apparent wind
    Given Boat is sailing in
    When Boat is in line with the wind
    Then Boat is luffing


Feature: Luffing
  Scenario: Boat is luffing when the player sails out and it is in line with the apparent wind
    Given Boat is sailing out
    When Boat is in line with the wind
    Then Boat is luffing