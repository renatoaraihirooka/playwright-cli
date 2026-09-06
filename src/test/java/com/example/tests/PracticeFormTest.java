package com.example.tests;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PracticeFormTest extends PlaywrightTestBase {
    private static final String FORM_URL = "https://www.qa-practice.com/forms/practice-form";

    @Test
    void rejectsAnEmptySubmission() {
        openForm();
        submitButton().click();

        String validationMessage = (String) firstName().evaluate("el => el.validationMessage");
        assertFalse(validationMessage.isBlank());
    }

    @Test
    void rejectsMobileNumbersWithFewerThanTenDigits() {
        openForm();
        fillRequiredFields();
        mobile().fill("123");
        submitButton().click();

        assertThat(page.getByText("Mobile number must be exactly 10 digits", new Page.GetByTextOptions()
                .setExact(true))).isVisible();
    }

    @Test
    void rejectsAnInvalidEmail() {
        openForm();
        fillRequiredFields();
        email().fill("email-invalido");
        mobile().fill("1198765432");
        submitButton().click();

        String validationMessage = (String) email().evaluate("el => el.validationMessage");
        assertFalse(validationMessage.isBlank());
    }

    @Test
    void submitsValidStudentData() {
        openForm();
        fillRequiredFields();
        email().fill("ana.silva@example.com");
        mobile().fill("1198765432");
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Sports")).check();
        submitButton().click();

        assertThat(page.getByRole(AriaRole.DIALOG)).isVisible();
        assertThat(page.getByText("Thanks for submitting the form", new Page.GetByTextOptions()
                .setExact(true))).isVisible();
        assertThat(page.getByText("Ana Silva", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.getByText("ana.silva@example.com", new Page.GetByTextOptions()
                .setExact(true))).isVisible();
    }

    @Test
    void includesSelectedCityInTheSubmissionSummary() {
        openForm();
        fillRequiredFields();
        email().fill("ana.silva@example.com");
        mobile().fill("1198765432");
        selectStateAndCity();
        submitButton().click();

        assertThat(page.getByText("NCR Delhi", new Page.GetByTextOptions().setExact(true))).isVisible();
    }

    private void openForm() {
        page.navigate(FORM_URL);
    }

    private void fillRequiredFields() {
        firstName().fill("Ana");
        lastName().fill("Silva");
        page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("Female")).check();
    }

    private void selectStateAndCity() {
        page.locator(".custom-dropdown-control").first().click();
        page.locator(".custom-dropdown-option[data-value='NCR']").click();
        page.locator(".custom-dropdown-control").nth(1).click();
        page.getByText("Delhi", new Page.GetByTextOptions().setExact(true)).click();
    }

    private Locator firstName() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First Name*"));
    }

    private Locator lastName() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last Name*"));
    }

    private Locator email() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email"));
    }

    private Locator mobile() {
        return page.getByRole(AriaRole.TEXTBOX,
                new Page.GetByRoleOptions().setName("Mobile (10 Digits)*"));
    }

    private Locator submitButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
    }
}