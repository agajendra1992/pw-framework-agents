package com.framework.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CartPage extends BasePage {
    private final Locator cartItems;
    private final Locator checkoutButton;

    public CartPage(Page page) {
        super(page);
        cartItems = page.getByTestId("inventory-item");
        checkoutButton = page.getByTestId("checkout");
    }

    public void assertItemCount(int count) { assertThat(cartItems).hasCount(count); }

    public CheckoutPage checkout() {
        checkoutButton.click();
        return new CheckoutPage(page);
    }
}
