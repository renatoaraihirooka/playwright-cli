package com.example.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GoogleSearchTest extends PlaywrightTestBase {
    @Test
    void userCanSearchForMichaelJacksonOnGoogle() {
        page.navigate("https://www.google.com");

        Page.GetByRoleOptions searchOptions = new Page.GetByRoleOptions().setName("Pesquisar");
        page.getByRole(AriaRole.COMBOBOX, searchOptions).fill("michael jackson");
        page.getByRole(AriaRole.COMBOBOX, searchOptions).press("Enter");

        page.waitForURL(Pattern.compile(".*google\\.com/(?:search|sorry/index).*"));
        String searchUrl = URLDecoder.decode(page.url(), StandardCharsets.UTF_8);
        assertTrue(searchUrl.matches(".*[?&]q=michael(?:\\+|%20|\\s)jackson.*"),
            "Search URL did not contain the expected query: " + searchUrl);
    }
}