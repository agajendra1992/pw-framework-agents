package com.framework.ui;

import com.framework.base.BaseUiTest;
import com.framework.pages.InventoryPage;
import com.framework.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class InventoryUiTest extends BaseUiTest {
    private InventoryPage inventory;

    // Runs after BaseUiTest.setUpBrowser (superclass @BeforeMethod runs first)
    @BeforeMethod(dependsOnMethods = "setUpBrowser")
    public void login() {
        inventory = new LoginPage(page).loginAsStandardUser();
    }

    @Test(description = "Inventory shows 6 products")
    public void showsProducts() {
        inventory.assertProductCount(6);
    }

    @Test(description = "Adding items updates cart badge")
    public void addToCartUpdatesBadge() {
        inventory.addToCart("Sauce Labs Backpack").addToCart("Sauce Labs Bike Light");
        inventory.assertCartCount(2);
    }
}
