package com.framework.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CheckoutPage extends BasePage {
    private final Locator firstName;
    private final Locator lastName;
    private final Locator postalCode;
    private final Locator continueButton;
    private final Locator finishButton;
    private final Locator confirmation;

    public CheckoutPage(Page page) {
        super(page);
        firstName = page.getByTestId("firstName");
        lastName = page.getByTestId("lastName");
        postalCode = page.getByTestId("postalCode");
        continueButton = page.getByTestId("continue");
        finishButton = page.getByTestId("finish");
        confirmation = page.getByTestId("complete-header");
    }

    public CheckoutPage fillShippingInfo(String first, String last, String zip) {
        firstName.fill(first);
        lastName.fill(last);
        postalCode.fill(zip);
        continueButton.click();
        return this;
    }

    public CheckoutPage finish() {
        finishButton.click();
        return this;
    }

    public void assertOrderConfirmed() {
        assertThat(confirmation).hasText("Thank you for your order!");
    }
}
