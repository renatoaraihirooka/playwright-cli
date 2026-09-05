package com.example.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class PlaywrightTestBase {
    protected Playwright playwright;
    protected Browser browser;
    protected Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        if (page != null) {
            try {
                Path evidenceDirectory = Paths.get("target", "test-evidence");
                Files.createDirectories(evidenceDirectory);
                String className = testInfo.getTestClass()
                    .map(Class::getSimpleName)
                    .orElse("UnknownTest");
                String testName = (className + "_" + testInfo.getDisplayName())
                    .replaceAll("[^a-zA-Z0-9._-]", "_");
                page.screenshot(new Page.ScreenshotOptions()
                        .setPath(evidenceDirectory.resolve(testName + ".png"))
                        .setFullPage(true));
            } catch (IOException | RuntimeException ignored) {
            }
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}