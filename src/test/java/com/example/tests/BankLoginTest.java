package com.example.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class BankLoginTest extends PlaywrightTestBase {
    @Test
    void standardUserCanLogInToSecureBank() {
        page.navigate("https://qaplayground.com/bank/login");

        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).fill("standard_user");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill("bank_sauce");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign in to SecureBank")).click();

        assertThat(page).hasURL("https://qaplayground.com/bank/dashboard");
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Welcome back, Alex"))).isVisible();
    }
}