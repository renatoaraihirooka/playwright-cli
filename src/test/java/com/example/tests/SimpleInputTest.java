package com.example.tests;

import com.microsoft.playwright.Locator;

import br.com.bradesco.PlaywrightTestBase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SimpleInputTest extends PlaywrightTestBase {
    private static final String URL = "https://www.qa-practice.com/elements/input/simple";

    @Test
    @DisplayName("Deve enviar texto valido e exibir o resultado")
    void shouldSubmitValidText() {
        Locator input = openPage();

        fillWithEvidence("01-fill-valid-text", input, "Teste_123-OK");
        pressEnterWithEvidence("02-submit-valid-text", input);

        assertTextWithEvidence(
                "03-validate-submitted-text",
                page.locator("p").filter(new Locator.FilterOptions().setHasText("Your input was:")),
                "Your input was:"
        );
        assertTextWithEvidence(
            "04-validate-submitted-value",
            page.locator("p").nth(1),
            "Teste_123-OK"
        );
    }

    @Test
    @DisplayName("Deve rejeitar texto com menos de dois caracteres")
    void shouldRejectTextShorterThanTwoCharacters() {
        Locator input = openPage();

        fillWithEvidence("01-fill-one-character", input, "a");
        pressEnterWithEvidence("02-submit-short-text", input);

        assertTextWithEvidence(
                "03-validate-short-text-message",
                page.locator("strong"),
                "Please enter 2 or more characters"
        );
    }

    @Test
    @DisplayName("Deve rejeitar caracteres fora do formato permitido")
    void shouldRejectInvalidCharacters() {
        Locator input = openPage();

        fillWithEvidence("01-fill-invalid-characters", input, "abc@123");
        pressEnterWithEvidence("02-submit-invalid-characters", input);

        assertTextWithEvidence(
                "03-validate-invalid-characters-message",
                page.locator("strong"),
                "Enter a valid string consisting of letters, numbers, underscores or hyphens."
        );
    }

    @Test
    @DisplayName("Deve rejeitar texto com mais de 25 caracteres")
    void shouldRejectTextLongerThanTwentyFiveCharacters() {
        Locator input = openPage();

        fillWithEvidence("01-fill-long-text", input, "12345678901234567890123456");
        pressEnterWithEvidence("02-submit-long-text", input);

        assertTextWithEvidence(
                "03-validate-long-text-message",
                page.locator("strong"),
                "Please enter no more than 25 characters"
        );
    }

    @Test
    @DisplayName("Deve exigir o preenchimento do campo")
    void shouldRequireText() {
        Locator input = openPage();

        pressEnterWithEvidence("01-submit-empty-text", input);
        assertInvalidWithEvidence("02-validate-required-field", input);
    }

    private Locator openPage() {
        navigateWithEvidence(URL);
        return page.getByRole(com.microsoft.playwright.options.AriaRole.TEXTBOX,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("Text string*"));
    }
}