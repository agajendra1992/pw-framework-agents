package com.framework.ui;

import com.framework.base.BaseUiTest;
import com.framework.config.Config;
import com.framework.pages.InventoryPage;
import com.framework.pages.LoginPage;
import org.testng.annotations.Test;

public class LoginUiTest extends BaseUiTest {

    @Test(groups = "smoke", description = "Valid user lands on inventory")
    public void validLogin() {
        InventoryPage inventory = new LoginPage(page).loginAsStandardUser();
        inventory.assertLoaded();
    }

    @Test(description = "Valid user can log out and return to the login page")
    public void validLoginThenLogout() {
        InventoryPage inventory = new LoginPage(page).loginAsStandardUser();
        inventory.assertLoaded();

        LoginPage login = inventory.logout();
        login.assertLoaded();
    }

    @Test(description = "Locked-out user sees an error")
    public void lockedUser() {
        LoginPage login = new LoginPage(page).open();
        login.loginAs(Config.get("locked.user"), Config.get("std.password"));
        login.assertErrorContains("locked out");
    }

    @Test(description = "Wrong password sees an error")
    public void wrongPassword() {
        LoginPage login = new LoginPage(page).open();
        login.loginAs(Config.get("std.user"), "wrong");
        login.assertErrorContains("do not match");
    }
}
