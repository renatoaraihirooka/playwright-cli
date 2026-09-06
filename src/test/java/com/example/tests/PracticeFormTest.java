package com.example.tests;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class PracticeFormTest extends PlaywrightTestBase {
    private static final String FORM_URL = "https://www.qa-practice.com/forms/practice-form";

    @Test
    void shouldSubmitPracticeFormWithValidData() throws Exception {
        page.navigate(FORM_URL);

        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First Name*")).fill("Ana");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last Name*")).fill("Silva");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).fill("ana.silva@example.com");
        page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("Female")).check();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Mobile (10 Digits)*")).fill("1198765432");

        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Type to search subjects...")).fill("Math");
        page.locator("#subjectsSuggestions").click();
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Sports")).check();
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Reading")).check();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Current Address"))
                .fill("Rua das Flores, 123 - Sao Paulo, SP");

        selectCustomOption("state", "NCR");
        selectCustomOption("city", "Noida");

        Path picture = Files.createTempFile("practice-form-picture-", ".png");
        page.screenshot(new Page.ScreenshotOptions().setPath(picture));
        page.locator("input[type='file']").setInputFiles(picture);

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit")).click();

        assertThat(page.getByRole(AriaRole.DIALOG)).containsText("Thanks for submitting the form");
        assertThat(page.getByRole(AriaRole.CELL, new Page.GetByRoleOptions().setName("Ana Silva"))).isVisible();
        assertThat(page.getByRole(AriaRole.CELL, new Page.GetByRoleOptions().setName("ana.silva@example.com"))).isVisible();
        assertThat(page.getByRole(AriaRole.CELL, new Page.GetByRoleOptions().setName("Female"))).isVisible();
        assertThat(page.getByRole(AriaRole.CELL, new Page.GetByRoleOptions().setName("1198765432"))).isVisible();
        assertThat(page.getByRole(AriaRole.CELL, new Page.GetByRoleOptions().setName("Maths"))).isVisible();
        assertThat(page.getByRole(AriaRole.CELL, new Page.GetByRoleOptions().setName("Sports, Reading"))).isVisible();
        assertThat(page.getByRole(AriaRole.CELL, new Page.GetByRoleOptions().setName("Rua das Flores, 123 - Sao Paulo, SP"))).isVisible();
        assertThat(page.getByRole(AriaRole.CELL, new Page.GetByRoleOptions().setName("NCR Noida"))).isVisible();
    }

    private void selectCustomOption(String fieldId, String value) {
        Locator field = page.locator("#div_id_" + fieldId);
        field.locator(".custom-dropdown-control").click();
        field.locator(".custom-dropdown-option[data-value='" + value + "']").click();
    }
}