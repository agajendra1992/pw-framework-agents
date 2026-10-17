package com.framework.pages;

import com.framework.config.Config;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginPage extends BasePage {
    private final Locator username;
    private final Locator password;
    private final Locator loginButton;
    private final Locator error;

    public LoginPage(Page page) {
        super(page);
        username = page.getByTestId("username");
        password = page.getByTestId("password");
        loginButton = page.getByTestId("login-button");
        error = page.getByTestId("error");
    }

    public LoginPage open() {
        navigate("/");
        return this;
    }

    public InventoryPage loginAs(String user, String pass) {
        username.fill(user);
        password.fill(pass);
        loginButton.click();
        return new InventoryPage(page);
    }

    public InventoryPage loginAsStandardUser() {
        return open().loginAs(Config.get("std.user"), Config.get("std.password"));
    }

    public void assertErrorContains(String text) {
        assertThat(error).containsText(text);
    }

    public void assertLoaded() {
        assertThat(username).isVisible();
        assertThat(password).isVisible();
        assertThat(loginButton).isVisible();
    }
}
