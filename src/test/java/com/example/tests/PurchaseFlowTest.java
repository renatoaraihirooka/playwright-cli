package com.example.tests;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class PurchaseFlowTest extends PlaywrightTestBase {
    private static final String APP_URL = "https://codemify-demo-app.vercel.app/demo-app";

    @Test
    void standardUserCanPurchaseAProduct() {
        page.navigate(APP_URL);

        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).fill("standard_user");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill("my_secret_code");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();

        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Products"))).isVisible();

        Locator product = page.locator("li").filter(new Locator.FilterOptions().setHasText("Codemify Backpack"));
        product.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Add to Cart")).click();
        assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cart 1"))).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cart 1")).click();

        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Your Shopping Cart"))).isVisible();
        assertThat(page.getByText("$29.99", new Page.GetByTextOptions().setExact(true))).isVisible();
        page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Proceed to Checkout")).click();

        fill("First Name *", "Ada");
        fill("Last Name *", "Lovelace");
        fill("Email *", "ada@example.com");
        fill("Address *", "1 Analytical Engine Way");
        fill("City *", "London");
        fill("State *", "LDN");
        fill("Zip Code *", "12345");
        fill("Card Number *", "4242424242424242");
        fill("Expiry Date *", "12/30");
        fill("CVV *", "123");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Complete Order")).click();

        assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cart"))).isVisible();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Products"))).isVisible();
    }

    private void fill(String name, String value) {
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(name)).fill(value);
    }
}