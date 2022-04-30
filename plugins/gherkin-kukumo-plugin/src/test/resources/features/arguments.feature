Feature: Test - Scenario with arguments

Scenario: Test Scenario with document
  Given a number with value 8.02 and another number with value 9
  When both numbers are multiplied
  Then the result is equals to:
  ```json
  {
    "result": 72.18
  }
  ```

Scenario: Test Scenario with data table
    Given a number with value 8.02 and another number with value 9
    When both numbers are multiplied
    Then the result is equals to:
    | name   | value |
    | result | 72.18 |
