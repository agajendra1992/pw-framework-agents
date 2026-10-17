package com.framework.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class InventoryPage extends BasePage {
    private final Locator items;
    private final Locator cartLink;
    private final Locator cartBadge;
    private final Locator menuButton;
    private final Locator logoutLink;

    public InventoryPage(Page page) {
        super(page);
        items = page.getByTestId("inventory-item");
        cartLink = page.getByTestId("shopping-cart-link");
        cartBadge = page.getByTestId("shopping-cart-badge");
        menuButton = page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Open Menu"));
        logoutLink = page.getByTestId("logout-sidebar-link");
    }

    public InventoryPage addToCart(String productName) {
        items.filter(new Locator.FilterOptions().setHasText(productName))
             .getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                     new Locator.GetByRoleOptions().setName(Pattern.compile("add to cart", Pattern.CASE_INSENSITIVE)))
             .click();
        return this;
    }

    public CartPage openCart() {
        cartLink.click();
        return new CartPage(page);
    }

    public LoginPage logout() {
        menuButton.click();
        logoutLink.click();
        return new LoginPage(page);
    }

    public void assertLoaded() { assertThat(page).hasURL(Pattern.compile(".*inventory\\.html")); }
    public void assertProductCount(int count) { assertThat(items).hasCount(count); }
    public void assertCartCount(int count) { assertThat(cartBadge).hasText(String.valueOf(count)); }
}
