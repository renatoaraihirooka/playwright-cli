package com.example.tests;

import org.junit.jupiter.api.Test;

import br.com.bradesco.PlaywrightTestBase;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class SmokeTest extends PlaywrightTestBase {
    @Test
    void shouldOpenExamplePage() {
        page.navigate("https://example.com");

        assertThat(page).hasTitle("Example Domain");
        assertThat(page.locator("h1")).hasText("Example Domain");
    }
}