package com.framework.e2e;

import com.framework.api.ApiClient;
import com.framework.api.services.PostsService;
import com.framework.base.BaseUiTest;
import com.framework.pages.CartPage;
import com.framework.pages.InventoryPage;
import com.framework.pages.LoginPage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class CheckoutE2ETest extends BaseUiTest {

    @Test(groups = "smoke", description = "User buys a product end to end")
    public void userCompletesPurchase() {
        InventoryPage inventory = new LoginPage(page).loginAsStandardUser();
        inventory.addToCart("Sauce Labs Backpack");
        inventory.assertCartCount(1);

        CartPage cart = inventory.openCart();
        cart.assertItemCount(1);

        cart.checkout()
            .fillShippingInfo("Jane", "Doe", "560001")
            .finish()
            .assertOrderConfirmed();
    }

    /**
     * Hybrid pattern: API for fast setup/verification, UI for real behaviour.
     * Against your own app this becomes:
     *   Order order = ordersService.createPending(user);    // API: arrange
     *   ...drive the payment screens in the UI...           // UI: act
     *   assertEquals(ordersService.get(order.id).status, "PAID");  // API: verify
     * Here the "backend" is a different demo service, so we only show the call shape.
     */
    @Test(description = "Hybrid: API precondition check followed by UI flow")
    public void hybridApiThenUi() {
        try (ApiClient api = new ApiClient()) {
            assertEquals(new PostsService(api).get(1).status(), 200, "Backend precondition failed");
        }
        new LoginPage(page).loginAsStandardUser().assertLoaded();
    }
}
