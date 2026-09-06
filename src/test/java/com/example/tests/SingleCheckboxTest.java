package com.example.tests;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class SingleCheckboxTest extends PlaywrightTestBase {
    private static final String CHECKBOX_URL =
            "https://www.qa-practice.com/elements/checkbox/single_checkbox";

    @Test
    void doesNotDisplayAResultWhenCheckboxIsNotSelected() {
        openCheckboxPage();

        assertThat(checkbox()).hasCount(1);
        assertThat(checkbox()).not().isChecked();
        assertThat(submitButton()).isEnabled();

        submitButton().click();

        assertThat(page.getByText("Selected checkboxes:")).not().isVisible();
    }

    @Test
    void displaysTheSelectedCheckboxNameAfterSubmission() {
        openCheckboxPage();
        checkbox().check();
        submitButton().click();

        assertThat(page.getByText("Selected checkboxes:", new Page.GetByTextOptions()
                .setExact(true))).isVisible();
        assertThat(page.getByText("select me or not", new Page.GetByTextOptions()
                .setExact(true))).isVisible();
    }

    private void openCheckboxPage() {
        page.navigate(CHECKBOX_URL);
    }

    private Locator checkbox() {
        return page.getByRole(AriaRole.CHECKBOX,
                new Page.GetByRoleOptions().setName("Select me or not"));
    }

    private Locator submitButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
    }
}