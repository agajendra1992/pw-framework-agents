@api
Feature: Posts API
  Scenario: Retrieve a post by ID
    Given the posts API is available
    When I request post 1
    Then the response status is 200
    And the response contains post 1 with a title

  Scenario: Unknown post returns not found
    Given the posts API is available
    When I request post 999999
    Then the response status is 404