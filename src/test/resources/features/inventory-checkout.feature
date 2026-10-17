@ui
Feature: Inventory and checkout
  Scenario: Cart badge reflects added products
    Given I am logged in as the standard user
    Then the inventory contains 6 products
    When I add "Sauce Labs Backpack" to the cart
    Then the cart badge shows 1 item

  Scenario: User completes a purchase
    Given I am logged in as the standard user
    When I add "Sauce Labs Backpack" to the cart and start checkout
    And I submit shipping details for "Jane" "Doe" with postal code "560001"
    Then the order is confirmed