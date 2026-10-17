package com.framework.bdd;

import com.framework.api.ApiClient;
import com.framework.api.models.Post;
import com.framework.api.services.PostsService;
import com.framework.config.Config;
import com.framework.core.PlaywrightFactory;
import com.framework.pages.CartPage;
import com.framework.pages.CheckoutPage;
import com.framework.pages.InventoryPage;
import com.framework.pages.LoginPage;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Page;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class FrameworkSteps {
    private Page page;
    private ApiClient apiClient;
    private PostsService posts;
    private APIResponse response;
    private Post post;
    private InventoryPage inventory;
    private CartPage cart;
    private CheckoutPage checkout;

    @Before
    public void setUp(Scenario scenario) {
        if (scenario.getSourceTagNames().contains("@ui")) {
            page = PlaywrightFactory.startBrowserSession();
        }
    }

    @After
    public void tearDown(Scenario scenario) throws Exception {
        if (page != null) {
            Path trace = null;
            try {
                if (scenario.isFailed()) {
                    Path directory = Paths.get("artifacts");
                    Files.createDirectories(directory);
                    String name = scenario.getName().replaceAll("[^A-Za-z0-9._-]", "_");
                    page.screenshot(new Page.ScreenshotOptions()
                            .setPath(directory.resolve(name + ".png"))
                            .setFullPage(true));
                    trace = directory.resolve(name + "-trace.zip");
                }
            } finally {
                // Always release the browser, even if the failure screenshot itself threw.
                PlaywrightFactory.stopBrowserSession(trace);
            }
        }
        if (apiClient != null) {
            apiClient.close();
        }
    }

    @Given("I open the login page")
    public void openLoginPage() {
        new LoginPage(page).open();
    }

    @When("I log in as the standard user")
    public void loginAsStandardUser() {
        inventory = new LoginPage(page).loginAsStandardUser();
    }

    @When("I log in as the locked user")
    public void loginAsLockedUser() {
        new LoginPage(page).open().loginAs(Config.get("locked.user"), Config.get("std.password"));
    }

    @When("I log in with an incorrect password")
    public void loginWithIncorrectPassword() {
        new LoginPage(page).open().loginAs(Config.get("std.user"), "wrong");
    }

    @Then("the inventory page is displayed")
    public void inventoryPageIsDisplayed() {
        inventory.assertLoaded();
    }

    @Then("the login error contains {string}")
    public void loginErrorContains(String message) {
        new LoginPage(page).assertErrorContains(message);
    }

    @Given("I am logged in as the standard user")
    public void loggedInAsStandardUser() {
        inventory = new LoginPage(page).loginAsStandardUser();
    }

    @Then("the inventory contains {int} products")
    public void inventoryContainsProducts(int count) {
        inventory.assertProductCount(count);
    }

    @When("I add {string} to the cart")
    public void addProductToCart(String productName) {
        inventory.addToCart(productName);
    }

    @Then("the cart badge shows {int} item(s)")
    public void cartBadgeShowsItems(int count) {
        inventory.assertCartCount(count);
    }

    @When("I add {string} to the cart and start checkout")
    public void addProductAndStartCheckout(String productName) {
        cart = inventory.addToCart(productName).openCart();
        cart.assertItemCount(1);
        checkout = cart.checkout();
    }

    @When("I submit shipping details for {string} {string} with postal code {string}")
    public void submitShippingDetails(String firstName, String lastName, String postalCode) {
        checkout.fillShippingInfo(firstName, lastName, postalCode).finish();
    }

    @Then("the order is confirmed")
    public void orderIsConfirmed() {
        checkout.assertOrderConfirmed();
    }

    @Given("the posts API is available")
    public void postsApiIsAvailable() {
        apiClient = new ApiClient();
        posts = new PostsService(apiClient);
    }

    @When("I request post {int}")
    public void requestPost(int id) {
        response = posts.get(id);
    }

    @Then("the response status is {int}")
    public void responseStatusIs(int status) {
        assertEquals(response.status(), status);
    }

    @Then("the response contains post {int} with a title")
    public void responseContainsPostWithTitle(int id) {
        post = ApiClient.parse(response, Post.class);
        assertEquals(post.id, Integer.valueOf(id));
        assertNotNull(post.title);
    }
}