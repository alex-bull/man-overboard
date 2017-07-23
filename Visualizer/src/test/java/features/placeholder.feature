Feature: Game X Controller

  Scenario: Heading of the boat is changed (upwind)
    Given the boat I am controlling's heading is 0 degrees
    When I push the upwind key once
    Then the boats heading should be around 3 degrees

  Scenario: Heading of the boat is changed (downwind)
    Given the boat I am controlling's heading is 3 degrees
    When I push the downwind key once
    Then the boats heading should be around 0 degrees

  Scenario: Sail visually goes in and out (out)
    Given the sail is in
    When I push the sail in/out key once
    Then the boats sail should be out

  Scenario: Sail visually goes in and out (in)
    Given the sail is out
    When I push the sail in/out key once
    Then the boats sail should be in

  Scenario: Adjust boats heading to best TWA depending on upwind or downwind (upwind)
    Given the boat is not at the best TWA for travelling upwind
    When I push VMG key once
    Then the boats heading should change to the best TWA for travelling upwind

  Scenario: Adjust boats heading to best TWA depending on upwind or downwind (downwind)
    Given the boat is not at the best TWA for travelling upwind
    When I push VMG key once
    Then the boats heading should change to the best TWA for travelling upwind

  Scenario: A player cannot spoof my controls
    Given I am a client connected to the race game
    And I am not touching my controls
    When another player attempts to control my boat from their client
    Then my boat remains unchanged in state

