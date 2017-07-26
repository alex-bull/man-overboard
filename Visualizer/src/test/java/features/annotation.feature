Feature: Annotation
  Scenario: Clear Annotations
    Given Some annotations selected
    When I choose to click no annotations button
    Then All individual annotations should be cleared
