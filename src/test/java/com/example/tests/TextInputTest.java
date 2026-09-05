package com.example.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class TextInputTest extends PlaywrightTestBase {
    private static final String INPUT_URL = "https://www.qa-practice.com/elements/input/simple";

    @Test
    void acceptsAValidStringAndDisplaysTheSubmittedValue() {
        navigateAndSubmit("Hello_123-Test");

        assertThat(page.getByText("Your input was:")).isVisible();
        assertThat(page.getByText("Hello_123-Test", new Page.GetByTextOptions().setExact(true))).isVisible();
    }

    @Test
    void rejectsAnEmptyValue() {
        page.navigate(INPUT_URL);
        textInput().press("Enter");

        assertThat(page.getByText("Your input was:")).not().isVisible();
    }

    @Test
    void rejectsValuesShorterThanTwoCharacters() {
        navigateAndSubmit("a");

        assertThat(page.getByText("Please enter 2 or more characters")).isVisible();
    }

    @Test
    void rejectsValuesLongerThan25Characters() {
        navigateAndSubmit("12345678901234567890123456");

        assertThat(page.getByText("Please enter no more than 25 characters")).isVisible();
    }

    @Test
    void rejectsCharactersOutsideTheAllowedSet() {
        navigateAndSubmit("abc!");

        assertThat(page.getByText(
                "Enter a valid string consisting of letters, numbers, underscores or hyphens.")).isVisible();
    }

    private Page.GetByRoleOptions textInputOptions() {
        return new Page.GetByRoleOptions().setName("Text string*");
    }

    private com.microsoft.playwright.Locator textInput() {
        return page.getByRole(AriaRole.TEXTBOX, textInputOptions());
    }

    private void navigateAndSubmit(String value) {
        page.navigate(INPUT_URL);
        textInput().fill(value);
        textInput().press("Enter");
    }
}