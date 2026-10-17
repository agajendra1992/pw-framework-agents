@ui
Feature: Login
  Users should be able to access the inventory or receive a useful login error.

  Scenario: Standard user logs in successfully
    When I log in as the standard user
    Then the inventory page is displayed

  Scenario: Locked user is rejected
    Given I open the login page
    When I log in as the locked user
    Then the login error contains "locked out"

  Scenario: Incorrect password is rejected
    Given I open the login page
    When I log in with an incorrect password
    Then the login error contains "do not match"