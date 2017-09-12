Feature: Boat uses a health up potion
  Scenario: Boat picks up a potion
    Given Boat does not have a potion
    When The boat picks up the potion
    Then The boat has a potion they can use


  Scenario Outline: Boat uses a potion
    Given Boat has already picked up a potion with a current health of <CurrentHealth> and max health of <Max>
    When The boat uses the potion
    Then The boat health is increased by <Result> and the boat has no more potion
      Examples:
      | CurrentHealth | Max | Result |
      | 0             |100        | 50 |
      | 10            |100        | 60 |
      | 100           |100        | 100 |
      | 50            |100        | 100 |
      | 60            |100        | 100 |